package com.sideproject.parking_java.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sideproject.parking_java.dao.IdDao;
import com.sideproject.parking_java.dao.MemberDao;
import com.sideproject.parking_java.dao.TransactionDao;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.exception.InternalServerError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.model.Transaction;
import com.sideproject.parking_java.redis.RedisService;
import com.sideproject.parking_java.utility.MemberIdUtil;
import com.sideproject.parking_java.utility.TimeUtils;

@Service
public class TransactionService {
    
    @Autowired 
    private MemberDao memberDao;
    @Autowired
    private IdDao idDao;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private RedisService redisService;

    @Transactional
    public void postParkingLotUsageService(Transaction transaction) {
        boolean isRedisConnected = redisService.isRedisConnected();
        if (transaction.getCarId() == null || transaction.getCarId() == 0 ||
            transaction.getParkingLotId() == null || transaction.getParkingLotId() == 0 ||
            transaction.getParkingLotSquareId() == null || transaction.getParkingLotSquareId() == 0 ||
            transaction.getStartTime() == null || transaction.getStartTime().equals("")
            ) {
            throw new InvalidParameterError("parameter is null or empty");
        }

        int memberId = MemberIdUtil.getMemberIdUtil();
        int depositAccountId = memberDao.getDepositAccountIdDao(memberId);
        String orderNumber = TimeUtils.timeFormat(new Date()) + Integer.toString(memberId);
        Member member = new Member();
        member.setStatus("停車中");
        transaction.setTransactionType("CONSUMPTION");
        transaction.setStatus("未付款");

        transactionDao.postInsertTransactionDao(memberId, depositAccountId, orderNumber, transaction);
        // 更改車位狀態
        transactionDao.putUpdateParkingLotSquareStatusDao(transaction);
        // 更改車位狀態 Redis
        if (isRedisConnected) {
            redisService.updateParkingLotSquareStatusInRedis(transaction);
        }
        // 更改會員狀態
        memberDao.putUpdateMemberStatusDao(memberId, member);
        // income method
        postParkingLotIncomeService(orderNumber, transaction);
    }
    // income method
    public void postParkingLotIncomeService(String orderNumber, Transaction transaction) {
        int parkingLotOwnerId = idDao.getMemberIdByParkingLotDataId(transaction.getParkingLotId());
        int parkingLotOwnerDepositAccountId = memberDao.getDepositAccountIdDao(parkingLotOwnerId);
        transaction.setTransactionType("INCOME");
        transaction.setStatus("未收款");
        transactionDao.postInsertTransactionDao(parkingLotOwnerId, parkingLotOwnerDepositAccountId, orderNumber, transaction);
    }

    public Transaction getUnpaidParkingLotUsageService() {
        int memberId = MemberIdUtil.getMemberIdUtil();
        Transaction unpaidParkingLotUasgeData = transactionDao.getUnpaidTransactionDao(memberId);

        return unpaidParkingLotUasgeData;
    }
    
    @Transactional
    public void putParkingLotUsageService(String orderNumber, Transaction transaction) throws DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        boolean isRedisConnected = redisService.isRedisConnected();
        Member member = new Member();
        Date currentTime = new Date();
        String currentTimeToString = TimeUtils.timeFormat(currentTime);
        String startTime = transaction.getStartTime();
        int usingTime = TimeUtils.timeCalculate(startTime, currentTimeToString);
        int cost = 0;
        int income = 0;

        int min = usingTime/60;
        int hour = usingTime/(60*60);
        int halfhour = (usingTime+1799)/1800;
        if (min < 5) {
            cost = 0;
            income = 0;
        } else if (hour < 1) {
            cost -= transaction.getPrice();
            income += (int) Math.round(transaction.getPrice() * 0.9);
        } else {
            cost -= (int) Math.round(transaction.getPrice()*0.5*halfhour);
            income += (int) Math.round(transaction.getPrice() * 0.5 * halfhour * 0.9);
        }

        if (transaction.getBalance() - cost < 0) {
            throw new InternalServerError("餘額不足!");
        }

        transaction.setStopTime(currentTimeToString);
        transaction.setTransactionType("CONSUMPTION");
        transaction.setAmount(cost);
        transaction.setStatus("已付款");
        // 餘額更新
        transactionDao.putUpdateBalanceDao(memberId, cost);
        int insertNumber = transactionDao.putUpdateParkingLotUsageDao(memberId, orderNumber, transaction);
        if (insertNumber== 0) {
            throw new DatabaseError("transactions failed");
        }
        //回復狀態
        member.setStatus(null);
        memberDao.putUpdateMemberStatusDao(memberId, member);
        //update income
        putParkingLotIncomeService(orderNumber, transaction, income);
        // 回復車位狀態
        transactionDao.putUpdateParkingLotSquareStatusDao(transaction);
        // 回復車位狀態 Redis
        if (isRedisConnected) {
            redisService.updateParkingLotSquareStatusInRedis(transaction);
        }
    }
    // income method
    public void putParkingLotIncomeService(String orderNumber, Transaction transaction, int income) {
        int parkingLotOwnerId = idDao.getMemberIdByParkingLotDataId(transaction.getParkingLotId());
        transaction.setTransactionType("INCOME");
        transaction.setAmount(income);
        transaction.setStatus("已收款");
        transactionDao.putUpdateParkingLotUsageDao(parkingLotOwnerId, orderNumber, transaction);
        transactionDao.putUpdateBalanceDao(parkingLotOwnerId, income);
    }

    public List<Transaction> getTransactionRecordsService() {
        int memberId = MemberIdUtil.getMemberIdUtil();
        List<Transaction> transactionRecords = transactionDao.getTransactionRecordsDao(memberId);

        if (transactionRecords.isEmpty()) {
            return null;
        } else {
            return transactionRecords;
        }
    }
}

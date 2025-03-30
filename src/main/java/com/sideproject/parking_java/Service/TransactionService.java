package com.sideproject.parking_java.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sideproject.parking_java.dao.IdDao;
import com.sideproject.parking_java.dao.MemberDao;
import com.sideproject.parking_java.dao.TransactionDao;
import com.sideproject.parking_java.model.Transaction;
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

    @Transactional
    public void postParkingLotUsageService(Transaction transaction) {
        int memberId = MemberIdUtil.getMemberIdUtil();
        int depositAccountId = memberDao.getDepositAccountIdDao(memberId);
        String orderNumber = TimeUtils.timeFormat(new Date()) + Integer.toString(memberId);

        transaction.setTransactionType("CONSUMPTION");
        transaction.setStatus("未付款");

        transactionDao.postInsertTransactionDao(memberId, depositAccountId, orderNumber, transaction);
        transactionDao.putUpdateParkingLotSquareStatusDao(transaction);
        transactionDao.putUpdateMemberStatusDao(memberId);
        
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
    public void putParkingLotUsageService(String orderNumber, Transaction transaction) {
        int memberId = MemberIdUtil.getMemberIdUtil();
        Date currentTime = new Date();
        String currentTimeToString = TimeUtils.timeFormat(currentTime);
        String startTime = transaction.getStartTime();
        System.out.println("startTime: "+ startTime);

        long usingTime = TimeUtils.timeCalculate(startTime, currentTimeToString);
        int cost = 0;
        int income = 0;

        if (usingTime/60 < 5) {
            cost = 0;
            income = 0;
        }

        if (usingTime/(60*2)<1) {
            cost -= transaction.getPrice();
            income += (int) Math.round(transaction.getPrice() * 0.9);
        }

        if ((usingTime/(60*2))-(usingTime/=(60*2)) > 0.5) {
            cost -= (int) Math.round(transaction.getPrice() * (usingTime /= (60*2) + 1));
            income += (int) Math.round(transaction.getPrice() * (usingTime /= (60*2) + 1));
        }

        if ((usingTime/(60*2))-(usingTime/=(60*2)) < 0.5) {
            cost -= (int) Math.round(transaction.getPrice() * (usingTime /= (60*2)));
            income += (int) Math.round(transaction.getPrice() * (usingTime /= (60*2)));
        }
        System.out.println(cost);
        transaction.setStopTime(currentTimeToString);
        transaction.setAmount(cost);
        transaction.setStatus("已付款");
        transactionDao.putUpdateParkingLotUsageDao(memberId, orderNumber, transaction);
        //update income
        putParkingLotIncomeService(orderNumber, transaction, income);
    }
    // income method
    public void putParkingLotIncomeService(String orderNumber, Transaction transaction, int income) {
        int parkingLotOwnerId = idDao.getMemberIdByParkingLotDataId(transaction.getParkingLotId());
        transaction.setAmount(income);
        transaction.setStatus("已收款");
        transactionDao.putUpdateParkingLotUsageDao(parkingLotOwnerId, orderNumber, transaction);
    }
}

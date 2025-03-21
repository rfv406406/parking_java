package com.sideproject.parking_java.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.MemberDao;
import com.sideproject.parking_java.dao.ParkingLotUsingDao;
import com.sideproject.parking_java.model.Transaction;
import com.sideproject.parking_java.utility.MemberIdUtil;
import com.sideproject.parking_java.utility.TimeFormat;

@Service
public class ParkingLotUsingService {
    
    @Autowired MemberDao memberDao;
    @Autowired
    private ParkingLotUsingDao parkingLotUsingDao;

    public void postParkingLotUsingService(Transaction transaction) {
        int memberId = MemberIdUtil.getMemberIdUtil();
        int depositAccountId = memberDao.getDepositAccountIdDao(memberId);
        String orderNumber = TimeFormat.timeFormat(new Date()) + Integer.toString(memberId);

        parkingLotUsingDao.postUnpaidParkingLotUsageDao(memberId, depositAccountId, orderNumber, transaction);
        parkingLotUsingDao.postModifiedParkingLotSquareStatusDao(transaction);
        parkingLotUsingDao.postModifiedMemberStatusDao(memberId);
    }
}

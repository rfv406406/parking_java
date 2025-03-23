package com.sideproject.parking_java.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.ParkingLotDataIdByMemberIdDao;
import com.sideproject.parking_java.dao.ParkingLotImagesDao;
import com.sideproject.parking_java.dao.ParkingLotRegisterDao;
import com.sideproject.parking_java.dao.ParkingLotSquareDao;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.utility.MemberIdUtil;

@Service
public class ParkingLotRegisterService{
    @Autowired
    private ParkingLotDataIdByMemberIdDao parkingLotDataIdByMemberIdDao;
    @Autowired
    private ParkingLotRegisterDao parkingLotRegisterDao;
    @Autowired
    private ParkingLotImagesDao parkingLotImagesDao;
    @Autowired
    private ParkingLotSquareDao parkingLotSquareDao;

    public List<ParkingLot> getParkingLotRegister() {
        return parkingLotRegisterDao.getParingLotRegisterDao();
    }

    public void postParkingLotRegister(ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {

        int memberId = MemberIdUtil.getMemberIdUtil();

        if (parkingLot.getName() == null || parkingLot.getName().equals("") ||
            parkingLot.getAddress() == null || parkingLot.getAddress().equals("") ||
            parkingLot.getNearLandmark() == null || parkingLot.getNearLandmark().equals("") ||
            parkingLot.getOpeningTimeAm() == null || parkingLot.getOpeningTimeAm().equals("") ||
            parkingLot.getOpeningTimePm() == null || parkingLot.getOpeningTimePm().equals("") ||
            parkingLot.getSpaceInOut() == null || parkingLot.getSpaceInOut().equals("") ||
            parkingLot.getCarWidth() == null || parkingLot.getCarWidth().equals("") ||
            parkingLot.getCarHeight() == null || parkingLot.getCarHeight().equals("") ||
            parkingLot.getLatitude() == null || parkingLot.getLatitude().equals("") ||
            parkingLot.getLongitude() == null || parkingLot.getLongitude().equals("") ||
            parkingLot.getCarSpaceNumber() == null || parkingLot.getCarSpaceNumber().isEmpty() 
            ) {
            throw new InvalidParameterError("parameter is null or empty");
        }

        int parkingLotRegisterDaoInsertId = parkingLotRegisterDao.postParkingLotRegisterDao(parkingLot, memberId);
        if (parkingLotRegisterDaoInsertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }

        int parkingLotDataId = parkingLotDataIdByMemberIdDao.getParkingLotDataIdByMemberId(memberId);

        parkingLotImagesDao.postParkinglotimagesDao(parkingLot, parkingLotDataId);

        int parkingLotDataIdByMemberIdDaoInsertId = parkingLotSquareDao.postParkingLotSquareDao(parkingLot, parkingLotDataId);
        if (parkingLotDataIdByMemberIdDaoInsertId == 0) {
            throw new DatabaseError("ParkingLotSquareDao inserted failed");
        }
    }

    public void deleteParkingLotRegister(ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        
        int insertId = parkingLotRegisterDao.deleteParkingLotRegisterDao(parkingLot, memberId);
        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }
    }

    public void putParkingLotRegister(ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();

        if (parkingLot.getName() == null || parkingLot.getName().equals("") ||
            parkingLot.getAddress() == null || parkingLot.getAddress().equals("") ||
            parkingLot.getNearLandmark() == null || parkingLot.getNearLandmark().equals("") ||
            parkingLot.getOpeningTimeAm() == null || parkingLot.getOpeningTimeAm().equals("") ||
            parkingLot.getOpeningTimePm() == null || parkingLot.getOpeningTimePm().equals("") ||
            parkingLot.getSpaceInOut() == null || parkingLot.getSpaceInOut().equals("") ||
            parkingLot.getCarWidth() == null || parkingLot.getCarWidth().equals("") ||
            parkingLot.getCarHeight() == null || parkingLot.getCarHeight().equals("") ||
            parkingLot.getLatitude() == null || parkingLot.getLatitude().equals("") ||
            parkingLot.getLongitude() == null || parkingLot.getLongitude().equals("") ||
            parkingLot.getCarSpaceNumber() == null || parkingLot.getCarSpaceNumber().isEmpty() 
            ) {
            throw new InvalidParameterError("parameter is null or empty");
        }

        int insertId = parkingLotRegisterDao.putParkingLotRegisterDao(parkingLot, memberId);

        int parkingLotDataId = parkingLotDataIdByMemberIdDao.getParkingLotDataIdByMemberId(memberId);

        parkingLotImagesDao.putParkinglotimagesDao(parkingLot, parkingLotDataId);

        parkingLotSquareDao.putParkinglotsquareDao(parkingLot, parkingLotDataId);

        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }
    }
}

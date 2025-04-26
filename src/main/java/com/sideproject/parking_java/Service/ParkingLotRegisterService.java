package com.sideproject.parking_java.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sideproject.parking_java.dao.IdDao;
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
    private IdDao parkingLotDataIdByMemberIdDao;
    @Autowired
    private ParkingLotRegisterDao parkingLotRegisterDao;
    @Autowired
    private ParkingLotImagesDao parkingLotImagesDao;
    @Autowired
    private ParkingLotSquareDao parkingLotSquareDao;
    @Autowired
    private GpsService gpsService;

    public List<ParkingLot> getParkingLotRegister() {
        Integer memberId = MemberIdUtil.getMemberIdUtil();   
        List<ParkingLot> parkingLot = parkingLotRegisterDao.getParingLotRegisterDao(memberId);
        return parkingLot;
    }

    @Transactional
    public void postParkingLotRegister(ParkingLot parkingLot) throws InvalidParameterError, DatabaseError, RuntimeException, JsonProcessingException{

        int memberId = MemberIdUtil.getMemberIdUtil();

        if (parkingLot.getName() == null || parkingLot.getName().equals("") ||
            parkingLot.getAddress() == null || parkingLot.getAddress().equals("") ||
            parkingLot.getOpeningTime() == null || parkingLot.getOpeningTime().equals("") ||
            parkingLot.getClosingTime() == null || parkingLot.getClosingTime().equals("") ||
            parkingLot.getCarWidth() == null || parkingLot.getCarWidth().equals("") ||
            parkingLot.getCarHeight() == null || parkingLot.getCarHeight().equals("") ||
            parkingLot.getImg().length == 0 ||
            parkingLot.getCarSpaceNumber() == null || parkingLot.getCarSpaceNumber().isEmpty() 
            ) {
            throw new InvalidParameterError("parameter is null or empty");
        }

        String address = parkingLot.getAddress();
        System.out.println("address: "+ address);
        HashMap<String, Object> gps = gpsService.getLatAndLngService(address);
        double lat = (double)gps.get("lat");
        double lng = (double)gps.get("lng");
        parkingLot.setLatitude(lat);
        parkingLot.setLongitude(lng);

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
    @Transactional
    public void deleteParkingLotRegister(Integer parkingLotId) throws InvalidParameterError, DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        
        int insertId = parkingLotRegisterDao.deleteParkingLotRegisterDao(parkingLotId, memberId);
        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }
    }

    @Transactional
    public void putParkingLotRegister(Integer parkingLotId, ParkingLot parkingLot) throws InvalidParameterError, DatabaseError, JsonProcessingException {
        int memberId = MemberIdUtil.getMemberIdUtil();
        parkingLot.setParkingLotId(parkingLotId);

        if (parkingLot.getName() == null || parkingLot.getName().equals("") ||
            parkingLot.getAddress() == null || parkingLot.getAddress().equals("") ||
            parkingLot.getNearLandmark() == null || parkingLot.getNearLandmark().equals("") ||
            parkingLot.getOpeningTime() == null || parkingLot.getOpeningTime().equals("") ||
            parkingLot.getClosingTime() == null || parkingLot.getClosingTime().equals("") ||
            parkingLot.getSpaceInOut() == null || parkingLot.getSpaceInOut().equals("") ||
            parkingLot.getCarWidth() == null || parkingLot.getCarWidth().equals("") ||
            parkingLot.getCarHeight() == null || parkingLot.getCarHeight().equals("") ||
            parkingLot.getCarSpaceNumber() == null || parkingLot.getCarSpaceNumber().isEmpty() 
            ) {
            throw new InvalidParameterError("parameter is null or empty");
        }

        String address = parkingLot.getAddress();
        HashMap<String, Object> gps = gpsService.getLatAndLngService(address);
        double lat = (double)gps.get("lat");
        double lng = (double)gps.get("lng");
        parkingLot.setLatitude(lat);
        parkingLot.setLongitude(lng);

        int insertId = parkingLotRegisterDao.putParkingLotRegisterDao(parkingLot, memberId);

        // int parkingLotDataId = parkingLotDataIdByMemberIdDao.getParkingLotDataIdByMemberId(memberId);

        // parkingLotImagesDao.putParkinglotimagesDao(parkingLot, parkingLotDataId);

        // parkingLotSquareDao.putParkinglotsquareDao(parkingLot, parkingLotDataId);
        parkingLotImagesDao.putParkinglotimagesDao(parkingLot);

        parkingLotSquareDao.putParkinglotsquareDao(parkingLot);

        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }
    }
}

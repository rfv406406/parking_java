package com.sideproject.parking_java.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sideproject.parking_java.dao.ParkingLotImagesDao;
import com.sideproject.parking_java.dao.ParkingLotRegisterDao;
import com.sideproject.parking_java.dao.ParkingLotSquareDao;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.redis.RedisService;
import com.sideproject.parking_java.utility.MemberIdUtil;

@Service
public class ParkingLotRegisterService{
    @Autowired
    private ParkingLotRegisterDao parkingLotRegisterDao;
    @Autowired
    private ParkingLotImagesDao parkingLotImagesDao;
    @Autowired
    private ParkingLotSquareDao parkingLotSquareDao;
    @Autowired
    private GpsService gpsService;
    @Autowired
    private RedisOperations<String, Object> operations;
    @Autowired
    private RedisService redisService;

    public Map<Object, Object> getParkingLotRegister() {
        Integer memberId = MemberIdUtil.getMemberIdUtil();
        Boolean isRedisConnected = redisService.isRedisConnected();
        Boolean hasParkingLotMap = operations.hasKey("parkingLotMap");
        Map<Object, Object> parkingLotMap = new HashMap<>();

        if (isRedisConnected && hasParkingLotMap != null && hasParkingLotMap) {
            parkingLotMap = redisService.getParkingLotMapFromRedis(parkingLotMap);
        } else {
            List<ParkingLot> parkingLotList = parkingLotRegisterDao.getParingLotRegisterDao(memberId, null);
            parkingLotMap = redisService.putAllParkingLotMapInRedis(parkingLotMap, parkingLotList);
        }
        return parkingLotMap.isEmpty() ? null : parkingLotMap;
    }

    @Transactional
    public void postParkingLotRegister(ParkingLot parkingLot) throws InvalidParameterError, DatabaseError, Exception, JsonProcessingException{

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
        HashMap<String, Object> gps = gpsService.getLatAndLngService(address);
        double lat = (double)gps.get("lat");
        double lng = (double)gps.get("lng");
        parkingLot.setLatitude(lat);
        parkingLot.setLongitude(lng);

        int parkingLotId = parkingLotRegisterDao.postParkingLotRegisterDao(parkingLot, memberId);

        parkingLotImagesDao.postParkinglotimagesDao(parkingLot, parkingLotId);

        parkingLotSquareDao.postParkingLotSquareDao(parkingLot, parkingLotId);
 
        //Redis
        redisService.putParkingLotMapInRedis(memberId, parkingLotId);
    }

    @Transactional
    public void deleteParkingLotRegister(Integer parkingLotId) throws InvalidParameterError, DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        
        int insertId = parkingLotRegisterDao.deleteParkingLotRegisterDao(parkingLotId, memberId);
        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }
        //Redis
        redisService.deleteParkinglotMapFromRedis(parkingLotId);
    }

    @Transactional
    public void putParkingLotRegister(Integer parkingLotId, ParkingLot parkingLot) throws InvalidParameterError, DatabaseError, JsonProcessingException, Exception {
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

        parkingLotImagesDao.putParkinglotimagesDao(parkingLot);
        parkingLotSquareDao.putParkinglotsquareDao(parkingLot);

        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }

        //Redis
        redisService.putParkingLotMapInRedis(memberId, parkingLotId);
    }
}

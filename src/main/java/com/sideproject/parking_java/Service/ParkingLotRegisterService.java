package com.sideproject.parking_java.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisService redisService;
    @Autowired
    private AwsS3Service awsS3Service;

    public Map<String, ParkingLot> getParkingLotRegister(Integer memberId, Double lng, Double lat,  Integer distance, Integer price, Integer carWidth, Integer carHeight) {
        boolean isRedisConnected = redisService.isRedisConnected();
        boolean hasParkingLotMap = redisTemplate.hasKey("parkingLotMap");
        boolean hasGeoParkingLotMap = redisTemplate.hasKey("geoParkingLotMap");
        Map<String, ParkingLot> parkingLotMap = new HashMap<>();

        if (isRedisConnected && memberId == null) {
            if (hasParkingLotMap && hasGeoParkingLotMap) {
                List<String> parkingLotIdList = redisService.getGeoParkingLotMapFromRedis(lng, lat, distance);
                Map<String, ParkingLot> parkingLotMapFromRedis = redisService.getParkingLotMapFromRedis(parkingLotIdList);
                parkingLotMap = parkingLotFiltByParknigLotParas(price, carWidth, carHeight, parkingLotMapFromRedis);
            } else {
                List<ParkingLot> parkingLotList = parkingLotRegisterDao.getParingLotRegisterDao(null, null);
                if (parkingLotList.isEmpty()) {
                    return null;
                }
                redisService.putGeoParkingLotMapInRedis(parkingLotList);
                redisService.putAllParkingLotMapInRedis(parkingLotList);
                List<String> parkingLotIdList = redisService.getGeoParkingLotMapFromRedis(lng, lat, distance);
                Map<String, ParkingLot> parkingLotMapFromRedis = redisService.getParkingLotMapFromRedis(parkingLotIdList);
                parkingLotMap = parkingLotFiltByParknigLotParas(price, carWidth, carHeight, parkingLotMapFromRedis);
            }
        } else {
            if (memberId == null) {
                Map<String, ParkingLot> parkingLotMapFromDB = new HashMap<>();
                List<ParkingLot> parkingLotList = parkingLotRegisterDao.getParingLotRegisterDao(null, null);
                if (parkingLotList.isEmpty()) {
                    return null;
                }
                for (ParkingLot e : parkingLotList) {
                    parkingLotMapFromDB.put(e.getParkingLotId().toString(), e);
                }
                parkingLotMapFromDB = parkingLotFiltByDistance(lat, lng, distance, parkingLotMapFromDB);
                parkingLotMap = parkingLotFiltByParknigLotParas(price, carWidth, carHeight, parkingLotMapFromDB);
            } else {
                List<ParkingLot> parkingLotList = parkingLotRegisterDao.getParingLotRegisterDao(memberId, null);
                if (parkingLotList.isEmpty()) {
                    return null;
                }
                for (ParkingLot e : parkingLotList) {
                    parkingLotMap.put(e.getParkingLotId().toString(), e);
                }
            }
        }
        
        return parkingLotMap;
    }

    public Map<String, ParkingLot> parkingLotFiltByDistance(Double lng, Double lat, Integer distance, Map<String, ParkingLot> parkingLotMap) {
        Double latPlus = lat + (distance/1000) * 0.00904;
        Double lngPlus = lng + (distance/1000) * (0.00904 / Math.cos(lat));
        Double latMinus = lat - (distance/1000) * 0.00904;
        Double lngMinus = lng - (distance/1000) * (0.00904 / Math.cos(lat));
        Map<String, ParkingLot> filterMap = new HashMap<>();

        for (String key : parkingLotMap.keySet()) {
            ParkingLot parkingLot = parkingLotMap.get(key);
            if ((parkingLot.getLatitude() < latPlus && parkingLot.getLatitude() > latMinus) &&
                (parkingLot.getLongitude() < lngPlus && parkingLot.getLongitude() > lngMinus)) {
                filterMap.put(key, parkingLot);
            }
        }

        return filterMap;
    }

    public Map<String, ParkingLot> parkingLotFiltByParknigLotParas(Integer price, Integer carWidth, Integer carHeight, Map<String, ParkingLot> parkingLotMap) {
        Map<String, ParkingLot> filterMap = new HashMap<>();
        for (String key : parkingLotMap.keySet()) {
            ParkingLot parkingLot = parkingLotMap.get(key);
            if (parkingLot.getPrice() <= price && 
                Integer.valueOf(parkingLot.getCarWidth()) <= carWidth && 
                Integer.valueOf(parkingLot.getCarHeight()) <= carHeight) {
                filterMap.put(key, parkingLot);
                System.out.println("parkingLot: "+parkingLot);
            }
        }

        return filterMap;
    }

    @Transactional
    public void postParkingLotRegister(ParkingLot parkingLot) throws InvalidParameterError, DatabaseError, Exception, JsonProcessingException{
        System.out.println(parkingLot.getCarWidth());
        System.out.println(parkingLot.getCarHeight());
        System.out.println(parkingLot.getCarSpaceNumber());
        
        if (parkingLot.getName() == null || parkingLot.getName().isEmpty() ||
            parkingLot.getAddress() == null || parkingLot.getAddress().isEmpty() ||
            parkingLot.getOpeningTime() == null || parkingLot.getOpeningTime().isEmpty() ||
            parkingLot.getClosingTime() == null || parkingLot.getClosingTime().isEmpty() ||
            parkingLot.getCarWidth() == null || parkingLot.getCarWidth().isEmpty() ||
            parkingLot.getCarHeight() == null || parkingLot.getCarHeight().isEmpty() ||
            parkingLot.getImg().length == 0 ||
            parkingLot.getCarSpaceNumber() == null || parkingLot.getCarSpaceNumber().isEmpty() 
            ) {
            throw new InvalidParameterError("parameter is null or empty");
        }

        int memberId = MemberIdUtil.getMemberIdUtil();
        boolean isRedisConnected = redisService.isRedisConnected();
        String address = parkingLot.getAddress();
        HashMap<String, Object> gps = gpsService.getLatAndLngService(address);
        double lat = (double)gps.get("lat");
        double lng = (double)gps.get("lng");
        parkingLot.setLatitude(lat);
        parkingLot.setLongitude(lng);

        int parkingLotId = parkingLotRegisterDao.postParkingLotRegisterDao(parkingLot, memberId);
        parkingLot.setParkingLotId(parkingLotId);

        List<String> imgUrlList = new ArrayList<>();
        for (MultipartFile img : parkingLot.getImg()) {
            String fileName = awsS3Service.uploadFile(img);
            String imgUrl = awsS3Service.returnUrl(fileName);
            imgUrlList.add(imgUrl);
        }
        
        parkingLotImagesDao.postParkinglotimagesDao(imgUrlList, parkingLotId);

        parkingLotSquareDao.postParkingLotSquareDao(parkingLot, parkingLotId);
        
        if (isRedisConnected) {
            redisService.putParkingLotMapInRedis(memberId, parkingLotId);
            redisService.updateGeoParkingLotMapInRedis(parkingLot);
        }
    }

    @Transactional
    public void deleteParkingLotRegister(Integer parkingLotId) throws InvalidParameterError, DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        boolean isRedisConnected = redisService.isRedisConnected();
        
        int insertId = parkingLotRegisterDao.deleteParkingLotRegisterDao(parkingLotId, memberId);
        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }
        if (isRedisConnected) {
            redisService.deleteGeoParkinglotMapFromRedis(parkingLotId);
            redisService.deleteParkinglotMapFromRedis(parkingLotId);
        }
    }

    @Transactional
    public void putParkingLotRegister(Integer parkingLotId, ParkingLot parkingLot) throws InvalidParameterError, DatabaseError, JsonProcessingException, Exception {
        int memberId = MemberIdUtil.getMemberIdUtil();
        boolean isRedisConnected = redisService.isRedisConnected();
        parkingLot.setParkingLotId(parkingLotId);

        if (parkingLot.getName() == null || parkingLot.getName().isEmpty() ||
            parkingLot.getAddress() == null || parkingLot.getAddress().isEmpty() ||
            parkingLot.getOpeningTime() == null || parkingLot.getOpeningTime().isEmpty() ||
            parkingLot.getClosingTime() == null || parkingLot.getClosingTime().isEmpty() ||
            parkingLot.getSpaceInOut() == null || parkingLot.getSpaceInOut().isEmpty() ||
            parkingLot.getCarWidth() == null || parkingLot.getCarWidth().isEmpty() ||
            parkingLot.getCarHeight() == null || parkingLot.getCarHeight().isEmpty() ||
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

        List<String> imgUrlList = new ArrayList<>();
        for (MultipartFile img : parkingLot.getImg()) {
            String fileName = awsS3Service.uploadFile(img);
            String imgUrl = awsS3Service.returnUrl(fileName);
            imgUrlList.add(imgUrl);
        }

        parkingLotImagesDao.putParkinglotimagesDao(parkingLot, imgUrlList);
        parkingLotSquareDao.putParkinglotsquareDao(parkingLot);

        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }
        if (isRedisConnected) {
            redisService.putParkingLotMapInRedis(memberId, parkingLotId);
        }
    }
}

package com.sideproject.parking_java.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.ParkingLotRegisterDao;
import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.model.Transaction;

@Service
public class RedisService {

    @Autowired
    private LettuceConnectionFactory connectionFactory;
    
    @Autowired
    private RedisOperations<String, Object> operations;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private ChannelTopic channelTopic;

    @Autowired
    private ParkingLotRegisterDao parkingLotRegisterDao;

    public boolean isRedisConnected() {
        try {
            RedisConnection conn = connectionFactory.getConnection();
            return "PONG".equals(conn.ping());
        } catch (Exception e) {
            System.out.println("isRedisConnected: " + e.getMessage());
            return false;
        } 
    }

    public Map<Object, Object> getParkingLotMapFromRedis(Map<Object, Object> parkingLotMap) {
        parkingLotMap = operations.opsForHash().entries("parkingLotMap");
        return parkingLotMap;
    }

    public Map<Object, Object> putAllParkingLotMapInRedis(Map<Object, Object> parkingLotMap, List<ParkingLot> parkingLotList) {
        for (int i=0; i<parkingLotList.size(); i++) {
            parkingLotMap.put(Integer.toString(parkingLotList.get(i).getParkingLotId()), parkingLotList.get(i));
        }
        operations.opsForHash().putAll("parkingLotMap", parkingLotMap);
        // operations.expire("parkingLotMap", Duration.ofMillis(1000000));
        return parkingLotMap;
    }

    public void putParkingLotMapInRedis(int member, int parkingLotId) {
        boolean isRedisConnected = isRedisConnected();
        Boolean hasParkingLotMap = operations.hasKey("parkingLotMap");
        boolean isKeyExisted;
        Map<String, Object> parkingLotMap = new HashMap<>();
        if (isRedisConnected) {
            List<ParkingLot> parkingLotList = parkingLotRegisterDao.getParingLotRegisterDao(member, parkingLotId);
            if (hasParkingLotMap != null && hasParkingLotMap) {
                isKeyExisted = operations.opsForHash().putIfAbsent("parkingLotMap", Integer.toString(parkingLotId), parkingLotList.get(0));
                parkingLotMap.put(Integer.toString(parkingLotId), parkingLotList.get(0));
                if (isKeyExisted == false) {
                    operations.opsForHash().put("parkingLotMap", Integer.toString(parkingLotId), parkingLotList.get(0));
                }
            } else {
                parkingLotMap.put(Integer.toString(parkingLotId), parkingLotList.get(0));
                operations.opsForHash().putAll("parkingLotMap", parkingLotMap);
            }
            redisPublisher.publish(channelTopic, parkingLotMap);
        }
    }

    public void deleteParkinglotMapFromRedis(int parkingLotId) {
        boolean isRedisConnected = isRedisConnected();
        if (isRedisConnected) {
            operations.opsForHash().delete("parkingLotMap", Integer.toString(parkingLotId));
            redisPublisher.publish(channelTopic, parkingLotId);
        }

    }

    @SuppressWarnings("null")
    public void updateParkingLotSquareStatusInRedis(Transaction transaction) {
        boolean isRedisConnected = isRedisConnected();
        String parkingLotIdtoString = Integer.toString(transaction.getParkingLotId());
        Map<String, Object> parkingLotMap = new HashMap<>();
        if (isRedisConnected) {
            ParkingLot parkingLot = (ParkingLot) operations.opsForHash().get("parkingLotMap", parkingLotIdtoString);
            if (parkingLot != null && parkingLot.getCarSpaceNumber() != null) {
                List<CarSpaceNumber> carSpaceNumber = parkingLot.getCarSpaceNumber();
                for (int i=0; i<carSpaceNumber.size(); i++) {
                    if (Objects.equals(carSpaceNumber.get(i).getId(), transaction.getParkingLotSquareId())) {
                        carSpaceNumber.get(i).setStatus(transaction.getParkingLot().getCarSpaceNumber().get(0).getStatus());
                    }
                }
            }
            operations.opsForHash().put("parkingLotMap", parkingLotIdtoString, parkingLot);
            parkingLotMap.put(parkingLotIdtoString, parkingLot);
            redisPublisher.publish(channelTopic, parkingLotMap);
        }
    }
}

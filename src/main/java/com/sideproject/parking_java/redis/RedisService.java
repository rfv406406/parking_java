package com.sideproject.parking_java.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.ParkingLotRegisterDao;
import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.model.Transaction;

@Service
public class RedisService {

    @Autowired
    private LettuceConnectionFactory connectionFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisOperations<String, Object> operations;

    @Autowired
    private RedisPublisher<Map<String, Object>> redisPublisher;

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
        Map<String, Object> parkingLotMap = new HashMap<>();
        if (isRedisConnected) {
            Boolean hasParkingLotMap = redisTemplate.hasKey("parkingLotMap");
            List<ParkingLot> parkingLotList = parkingLotRegisterDao.getParingLotRegisterDao(member, parkingLotId);
            if (hasParkingLotMap != null && hasParkingLotMap) {
                operations.opsForHash().put("parkingLotMap", Integer.toString(parkingLotId), parkingLotList.get(0));
                parkingLotMap.put(Integer.toString(parkingLotId), parkingLotList.get(0));
            } else {
                parkingLotMap.put(Integer.toString(parkingLotId), parkingLotList.get(0));
                operations.opsForHash().putAll("parkingLotMap", parkingLotMap);
            }
            redisPublisher.publishParkingLot(parkingLotMap);
        }
    }

    public void deleteParkinglotMapFromRedis(int parkingLotId) {
        boolean isRedisConnected = isRedisConnected();
        if (isRedisConnected) {
            operations.opsForHash().delete("parkingLotMap", Integer.toString(parkingLotId));
            redisPublisher.publishParkingLot(parkingLotId);
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
            redisPublisher.publishParkingLot(parkingLotMap);
        }
    }

    public void putChatMessageMapInRedis(Integer cahtroomId, List<ChatMessage> chatMessage) {
        Boolean hasChatroomMap = operations.hasKey("chatRoomMap");
        Map<String, ChatMessage> chatMessageMap = new LinkedHashMap<>();

        for (int i=0; i<chatMessage.size(); i++) {
            chatMessageMap.put(Integer.toString(chatMessage.get(i).getId()), chatMessage.get(i));
        }

        if (hasChatroomMap != null && hasChatroomMap) {
            operations.opsForHash().put("chatRoomMap", Integer.toString(cahtroomId), chatMessageMap);
            operations.expire(Integer.toString(cahtroomId), Duration.ofMillis(1000000));
        } else {
            Map<String, Object> chatRoom = new HashMap<>();
            chatRoom.put(Integer.toString(cahtroomId), chatMessageMap);
            operations.opsForHash().putAll("chatRoomMap", chatRoom);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<Object, Object> getChatMessageMapFromRedis(Integer cahtroomId) {
        Object chatroomMapObject = operations.opsForHash().get("chatRoomMap", Integer.toString(cahtroomId));
        Map<Object, Object> chatroomMap = (Map<Object, Object>) chatroomMapObject;
        return chatroomMap;
    }

    @SuppressWarnings({ "null", "unchecked" })
    public void updateChatMessageInRedis(ChatMessage chatMessage) {
        Map<String, ChatMessage> chatroomMap;
        String chatroomId = Integer.toString(chatMessage.getChatroomId());
        Object chatroomMapObject = operations.opsForHash().get("chatRoomMap", chatroomId);

        if (chatMessage.getId() == null) {
            Long tempIdInRedis = operations.opsForValue().increment("tempId" + chatroomId, 1L);
            Integer tempId = -tempIdInRedis.intValue();
            chatMessage.setId(tempId);
        }

        if (chatroomMapObject != null) {
            chatroomMap = (Map<String, ChatMessage>) chatroomMapObject;
            chatroomMap.put(Integer.toString(chatMessage.getId()), chatMessage);
            operations.opsForHash().put("chatRoomMap", Integer.toString(chatMessage.getChatroomId()), chatroomMap);
        } 

        redisPublisher.publishChatMessage(chatroomId, chatMessage);
    }
}

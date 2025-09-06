package com.sideproject.parking_java.redis;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.ChatDao;
import com.sideproject.parking_java.dao.ParkingLotRegisterDao;
import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.model.Transaction;

@Service
public class RedisService {

    @Autowired
    private LettuceConnectionFactory connectionFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisPublisher<Map<String, Object>> redisPublisher;

    @Autowired
    private ParkingLotRegisterDao parkingLotRegisterDao;

    @Autowired
    private ChatDao chatDao;

    public boolean isRedisConnected() {
        try {
            RedisConnection conn = connectionFactory.getConnection();
            return "PONG".equals(conn.ping());
        } catch (Exception e) {
            return false;
        } 
    }

    public Map<String, ParkingLot> getParkingLotMapFromRedis(List<String> parkingLotIdList) {
        String key = "parkingLotMap";
        Map<String, ParkingLot> parkingLotMap = new HashMap<>();
        for (int i=0; i<parkingLotIdList.size(); i++) {
            Object parkingLotObject = redisTemplate.opsForHash().get(key, parkingLotIdList.get(i));
            ParkingLot parkingLot = (ParkingLot) parkingLotObject;
            parkingLotMap.put(parkingLotIdList.get(i), parkingLot);
        }
        
        return parkingLotMap;
    }

    @SuppressWarnings("null")
    public List<String> getGeoParkingLotMapFromRedis(Double lng, Double lat, Integer distance) {
        List<String> parkingLotIdList = new ArrayList<>();
        String key = "geoParkingLotMap";
        Point currentPosition = new Point(lng, lat);
        Circle circle = new Circle(currentPosition, distance);
        GeoResults<GeoLocation<Object>> geoResults = redisTemplate.opsForGeo().radius(key, circle);
        List<GeoResult<GeoLocation<Object>>> geoResultsList = geoResults.getContent();
        for (GeoResult<GeoLocation<Object>> e : geoResultsList) {
            String parkingLotId = e.getContent().getName().toString();
            parkingLotIdList.add(parkingLotId);
        }

        return parkingLotIdList;
    }

    public void putGeoParkingLotMapInRedis(List<ParkingLot> parkingLotList) {
        String key = "geoParkingLotMap";

        for (int i=0; i<parkingLotList.size(); i++) {
            String parkingLotId = parkingLotList.get(i).getParkingLotId().toString();
            Double lng = parkingLotList.get(i).getLongitude();
            Double lat = parkingLotList.get(i).getLatitude();
            Point point = new Point(lng, lat);
            redisTemplate.opsForGeo().add(key, point, parkingLotId);
        }
    }

    public void updateGeoParkingLotMapInRedis(ParkingLot parkingLot) {
        String key = "geoParkingLotMap";
        String parkingLotId = parkingLot.getParkingLotId().toString();
        Double lng = parkingLot.getLongitude();
        Double lat = parkingLot.getLatitude();
        Point point = new Point(lng, lat);
        redisTemplate.opsForGeo().add(key, point, parkingLotId);
    
    }

    public void putAllParkingLotMapInRedis(List<ParkingLot> parkingLotList) {
        Map<Object, Object> parkingLotMap = new HashMap<>();
        for (int i=0; i<parkingLotList.size(); i++) {
            parkingLotMap.put(Integer.toString(parkingLotList.get(i).getParkingLotId()), parkingLotList.get(i));
        }
        redisTemplate.opsForHash().putAll("parkingLotMap", parkingLotMap);
    }

    public void putParkingLotMapInRedis(int member, int parkingLotId) {
        boolean isRedisConnected = isRedisConnected();
        Map<String, Object> parkingLotMap = new HashMap<>();
        if (isRedisConnected) {
            Boolean hasParkingLotMap = redisTemplate.hasKey("parkingLotMap");
            List<ParkingLot> parkingLotList = parkingLotRegisterDao.getParingLotRegisterDao(member, parkingLotId);
            if (hasParkingLotMap != null && hasParkingLotMap) {
                redisTemplate.opsForHash().put("parkingLotMap", Integer.toString(parkingLotId), parkingLotList.get(0));
                parkingLotMap.put(Integer.toString(parkingLotId), parkingLotList.get(0));
            } else {
                parkingLotMap.put(Integer.toString(parkingLotId), parkingLotList.get(0));
                redisTemplate.opsForHash().putAll("parkingLotMap", parkingLotMap);
            }
            redisPublisher.publishParkingLot(parkingLotMap);
        }
    }

    public void deleteParkinglotMapFromRedis(int parkingLotId) {
        redisTemplate.opsForHash().delete("parkingLotMap", Integer.toString(parkingLotId));
        redisPublisher.publishParkingLot(parkingLotId);
    }

    public void deleteGeoParkinglotMapFromRedis(int parkingLotId) {
        redisTemplate.opsForGeo().remove("geoParkingLotMap", Integer.toString(parkingLotId));
    }

    @SuppressWarnings("null")
    public void updateParkingLotSquareStatusInRedis(Transaction transaction) {
        boolean isRedisConnected = isRedisConnected();
        String parkingLotIdtoString = Integer.toString(transaction.getParkingLotId());
        Map<String, Object> parkingLotMap = new HashMap<>();
        if (isRedisConnected) {
            ParkingLot parkingLot = (ParkingLot) redisTemplate.opsForHash().get("parkingLotMap", parkingLotIdtoString);
            if (parkingLot != null && parkingLot.getCarSpaceNumber() != null) {
                List<CarSpaceNumber> carSpaceNumber = parkingLot.getCarSpaceNumber();
                for (int i=0; i<carSpaceNumber.size(); i++) {
                    if (Objects.equals(carSpaceNumber.get(i).getId(), transaction.getParkingLotSquareId())) {
                        carSpaceNumber.get(i).setStatus(transaction.getParkingLot().getCarSpaceNumber().get(0).getStatus());
                    }
                }
            } else {
                return;
            }
            redisTemplate.opsForHash().put("parkingLotMap", parkingLotIdtoString, parkingLot);
            parkingLotMap.put(parkingLotIdtoString, parkingLot);
            redisPublisher.publishParkingLot(parkingLotMap);
        }
    }

    public List<Object> getChatRoomMapFromRedis(String hashKey) {
        boolean hasChatRoomMap = redisTemplate.hasKey(hashKey);

        if (hasChatRoomMap) {
            List<Object> chatRoomListObject = redisTemplate.opsForHash().values(hashKey);
            return chatRoomListObject;
        }

        return null;
    } 

    public void putChatRoomMapListInRedis(String hashKey, List<ChatRoom> chatRoom) {
        boolean hasChatRoomMap = redisTemplate.hasKey(hashKey);
        Map<String, ChatRoom> chatRoomMap = new LinkedHashMap<>();

        if (hasChatRoomMap) {
            for (int i=0; i<chatRoom.size(); i++) {
                redisTemplate.opsForHash().put(hashKey, Integer.toString(chatRoom.get(i).getId()), chatRoom.get(i));
            }
        } else {
            for (int i=0; i<chatRoom.size(); i++) {
                chatRoomMap.put(Integer.toString(chatRoom.get(i).getId()), chatRoom.get(i));
            }
            redisTemplate.opsForHash().putAll(hashKey, chatRoomMap);
        }

        long expireAt = Instant.now().plusSeconds(3600).getEpochSecond();
        redisTemplate.opsForZSet().add("chatRoomMapExpiry", hashKey, expireAt);
    }

    public void putChatRoomMapInRedis(String hashKey, ChatRoom chatRoom, Integer receiver) {
        redisTemplate.opsForHash().put(hashKey, Integer.toString(chatRoom.getId()), chatRoom);
        long expireAt = Instant.now().plusSeconds(3600).getEpochSecond();
        redisTemplate.opsForZSet().add("chatRoomMapExpiry", hashKey, expireAt);

        redisPublisher.publishChatroom(Integer.toString(receiver), chatRoom);
    }

    public void updateChatRoomMapInRedis(String hashKey, Integer chatroomId, Integer receiver, String activityTime, Integer parkingLotId, String parkingLotName, String lastReadTime, String lastReceivedMessageTime) {
        
        ChatRoom chatroom = (ChatRoom) redisTemplate.opsForHash().get(hashKey, Integer.toString(chatroomId));

        if (chatroom != null) {

            if (activityTime != null) {
                chatroom.setLastActivity(activityTime);
            }
            if (parkingLotId != null) {
                chatroom.setParkingLotId(parkingLotId);
            }
            if (parkingLotName != null) {
                chatroom.setParkingLotName(parkingLotName);
            }
            if (lastReadTime != null) {
                chatroom.setLastRead(lastReadTime);
            }
            if (lastReceivedMessageTime != null) {
                chatroom.setLastReceivedMessage(lastReceivedMessageTime);
            }

            redisTemplate.opsForHash().put(hashKey, Integer.toString(chatroomId), chatroom);
            long expireAt = Instant.now().plusSeconds(3600).getEpochSecond();
            redisTemplate.opsForZSet().add("chatRoomMapExpiry", hashKey, expireAt);

            redisPublisher.publishChatroom(Integer.toString(receiver), chatroom);
        }
    }

    public void putChatMessageListInRedis(String hashKey, List<ChatMessage> chatMessage) {
        for (ChatMessage message : chatMessage) {
            redisTemplate.opsForList().rightPush(hashKey, message);
        }
        long expireAt = Instant.now().plusSeconds(3600).getEpochSecond();
        redisTemplate.opsForZSet().add("chatMessageListExpiry", hashKey, expireAt);
    }

    public List<Object> getChatMessageListFromRedis(String hashKey) {
        boolean hasChatMessageList = redisTemplate.hasKey(hashKey);
        if (hasChatMessageList) {
            List<Object> chatMessageList = redisTemplate.opsForList().range(hashKey, 0, -1);

            return chatMessageList;
        }
        return null;
    }

    public void updateChatMessageListInRedis(String hashKey, String chatroomId, List<ChatMessage> chatMessageList) {
        for (int i=0; i<chatMessageList.size(); i++) {
            redisTemplate.opsForList().rightPush(hashKey, chatMessageList.get(i));
        }
        
        long expireAt = Instant.now().plusSeconds(3600).getEpochSecond();
        redisTemplate.opsForZSet().add("chatMessageListExpiry", hashKey, expireAt);

        redisPublisher.publishChatMessage(chatroomId, chatMessageList);
    }

    @Scheduled(fixedRate = 10000)
    public void scanChatRoomExpiredKeyInRedis() {
        String pattern = "chatRoomMapExpiry";
        long now = Instant.now().getEpochSecond();

        Set<Object> expiredChatroom = redisTemplate.opsForZSet().rangeByScore(pattern, 0, now);

        if (expiredChatroom == null || expiredChatroom.isEmpty()) {
            return;
        }

        for (Object chatRoomMapKey : expiredChatroom) {
            String keys = (String) chatRoomMapKey;
            String[] parts = keys.split("-");
            Integer memberId = Integer.valueOf(parts[1]);
            List<Object> chatRoomList = redisTemplate.opsForHash().values(keys);

            if (chatRoomList == null || chatRoomList.isEmpty()) {
                redisTemplate.opsForZSet().remove(pattern, keys);
                continue;
            }

            for (int i=0; i<chatRoomList.size();i++) {
                ChatRoom chatroom = (ChatRoom) chatRoomList.get(i);
                chatDao.putChatRoomParkingLotIdDao(chatroom.getId(), chatroom);
                chatDao.putChatRoomActivityTimeDao(chatroom.getId(), chatroom.getLastActivity());
                chatDao.putChatRoomLastReadDao(memberId, chatroom.getId(), chatroom.getLastRead());
            }
            redisTemplate.delete(keys);
            redisTemplate.opsForZSet().remove(pattern, keys);
        }        
    }

    @Scheduled(fixedRate = 10000)
    public void scanChatMessageExpiredKeyInRedis() {
        String pattern = "chatMessageListExpiry";
        long now = Instant.now().getEpochSecond();

        Set<Object> expiredChatMessageList = redisTemplate.opsForZSet().rangeByScore(pattern, 0, now);

        if (expiredChatMessageList == null || expiredChatMessageList.isEmpty()) {
            return;
        }

        for (Object expiredChatMessageKey : expiredChatMessageList) {
            String key = (String) expiredChatMessageKey;
            List<Object> chatMessageList = redisTemplate.opsForList().range(key, 0, -1);

            if (chatMessageList == null || chatMessageList.isEmpty()) {
                redisTemplate.opsForZSet().remove(pattern, key);
                continue;
            }

            List<ChatMessage> cl = new ArrayList<>();
            for (int i=0; i<chatMessageList.size();i++) {
                ChatMessage chatMessage = (ChatMessage) chatMessageList.get(i);
                cl.add(chatMessage);
            }

            chatDao.postChatMessageDao(cl);
            chatDao.putChatRoomActivityTimeDao(cl.get(cl.size() - 1).getChatroomId(), cl.get(cl.size() - 1).getTimestamp());
            redisTemplate.delete(key);
            redisTemplate.opsForZSet().remove(pattern, key);
        }        
    }
}

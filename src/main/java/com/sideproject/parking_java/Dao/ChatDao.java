package com.sideproject.parking_java.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;
import com.sideproject.parking_java.utility.ChatMessageRowMapper;
import com.sideproject.parking_java.utility.ChatRoomRowMapper;

@Component
public class ChatDao {
    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Integer postChatRoomDao(ChatRoom chatRoom) {
        String aqlS = "SELECT * FROM chatroom WHERE (sender_id = :sender_id AND recipient_id = :recipient_id) OR (sender_id = :recipient_id AND recipient_id = :sender_id)";
        String sqlI = "INSERT INTO chatroom(sender_id, recipient_id, parkingLot_id) VALUES (:sender_id, :recipient_id, :parkingLot_id)";
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender_id", chatRoom.getSenderId());
        map.put("recipient_id", chatRoom.getRecipientId());
        map.put("parkingLot_id", chatRoom.getParkingLotId());

        List<ChatRoom> chatroom = namedParameterJdbcTemplate.query(aqlS, map, new ChatRoomRowMapper());

        if (chatroom.isEmpty()) {
            int insertNumber = namedParameterJdbcTemplate.update(sqlI, new MapSqlParameterSource(map));

            if (insertNumber == 0) {
                throw new DatabaseError("No key returned");
            }

            return null;
        } else {
            Integer chatroomId = chatroom.get(0).getId();
            return chatroomId;
        }
    }

    public int putChatRoomDao(Integer chatroomId, ChatRoom chatRoom) {
        String sql = "UPDATE chatroom SET parkingLot_id = :parkingLot_id WHERE id = :id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", chatroomId);
        map.put("parkingLot_id", chatRoom.getParkingLotId());

        int updateId = namedParameterJdbcTemplate.update(sql, map);
    
        return updateId;
    }

    public int postChatMessageDao(ChatMessage chatMessage) {
        String sql = "INSERT INTO chatmessage(chatroom_id, sender_id, recipient_id, message, timestamp) "
        + "VALUES (:chatroom_id, :sender_id, :recipient_id, :message, :timestamp)";
        HashMap<String, Object> map = new HashMap<>();
        map.put("chatroom_id", chatMessage.getChatroomId());
        map.put("sender_id", chatMessage.getSenderId());
        map.put("recipient_id", chatMessage.getRecipientId());
        map.put("message", chatMessage.getMessage());
        map.put("timestamp", chatMessage.getTimestamp());

        int insertNumber = namedParameterJdbcTemplate.update(sql, map);

        if (insertNumber == 0) {
            throw new DatabaseError("Message inserts failure");
        }

        return insertNumber;
    }

    // public void postChatFileDao(ChatMessage chatMessage) {
    //     String sql = "INSERT INTO chatmessage(chatroom_id, sender_id, recipient_id, message, timestamp) "
    //     + "VALUES (?, ?, ?, ?, ?)";
    //     List<Object[]> batch = new ArrayList<>();
    //     for (String imgUrl : chatMessage.getImgUrl()) {
    //         Object[] value = new Object[5];
    //         value[0] = chatMessage.getChatroomId();
    //         value[1] = chatMessage.getSenderId();
    //         value[2] = chatMessage.getRecipientId();
    //         value[3] = imgUrl;
    //         value[4] = chatMessage.getTimestamp();
    //         batch.add(value);  
    //     }

    //     int[] insertNumber = jdbcTemplate.batchUpdate(sql, batch);

    //     for (int number : insertNumber) {
    //         if (number == 0) {
    //             throw new DatabaseError("Message inserts failure");
    //         }
    //     }
    // }

    public List<ChatRoom> getChatroomDao(Integer memberId) {
        String sql = "SELECT chatroom.*, parkinglotdata.name FROM chatroom "
                + "LEFT JOIN parkinglotdata ON chatroom.parkingLot_id = parkinglotdata.id " 
                + "WHERE chatroom.sender_id = :member_id OR chatroom.recipient_id = :member_id";        
        
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        List<ChatRoom> chatroom = namedParameterJdbcTemplate.query(sql, map, new ChatRoomRowMapper());

        return chatroom;
    }

    public List<ChatMessage> getChatMessage(Integer chatroomId) {
        String sql = "SELECT * FROM chatmessage WHERE chatroom_id = :chatroom_id";
        
        HashMap<String, Object> map = new HashMap<>();
        map.put("chatroom_id", chatroomId);
        List<ChatMessage> chatmessage = namedParameterJdbcTemplate.query(sql, map, new ChatMessageRowMapper());

        return chatmessage;
    }
}

package com.sideproject.parking_java.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer getChatroomDao(ChatRoom chatRoom) {
        String sql = "SELECT * FROM chatroom WHERE (sender_id = :sender_id AND recipient_id = :recipient_id) OR (sender_id = :recipient_id AND recipient_id = :sender_id)";
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender_id", chatRoom.getSenderId());
        map.put("recipient_id", chatRoom.getRecipientId());

        List<ChatRoom> chatroomList = namedParameterJdbcTemplate.query(sql, map, new ChatRoomRowMapper());
        
        if (!chatroomList.isEmpty()) {
            Integer chatroomId = chatroomList.get(0).getId();
            return chatroomId;
        } 

        return null;
    }

    @SuppressWarnings("null")
    public Integer postChatRoomDao(ChatRoom chatRoom) throws DatabaseError {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO chatroom(sender_id, recipient_id, parkingLot_id, last_activity_time) VALUES (:sender_id, :recipient_id, :parkingLot_id, :last_activity_time)";
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender_id", chatRoom.getSenderId());
        map.put("recipient_id", chatRoom.getRecipientId());
        map.put("parkingLot_id", chatRoom.getParkingLotId());
        map.put("last_activity_time", chatRoom.getLastActivity());

        int insertNumber = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        if (insertNumber == 0) {
            throw new DatabaseError("No key returned");
        }

        return keyHolder.getKey().intValue();
    }

    public int postChatRoomLastReadDao(Integer memberId, Integer key, ChatRoom chatRoom) {
        String sql = "INSERT INTO chatroom_read_status(chatroom_id, member_id, last_read_time) VALUES (:chatroom_id, :member_id, :last_read_time)";
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("chatroom_id", key); 
        map.put("last_read_time", chatRoom.getLastRead());

        int updateId = namedParameterJdbcTemplate.update(sql, map);

        return updateId;
    }

    public int putChatRoomParkingLotIdDao(Integer chatroomId, ChatRoom chatRoom) {
        String sql = "UPDATE chatroom SET parkingLot_id = :parkingLot_id WHERE id = :id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", chatroomId);
        map.put("parkingLot_id", chatRoom.getParkingLotId());

        int updateId = namedParameterJdbcTemplate.update(sql, map);
    
        return updateId;
    }

    public int putChatRoomActivityTimeDao(Integer chatroomId, String currentTime) {
        String sql = "UPDATE chatroom SET last_activity_time = :last_activity_time WHERE id = :id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", chatroomId);
        map.put("last_activity_time", currentTime);

        int updateId = namedParameterJdbcTemplate.update(sql, map);
    
        return updateId;
    }

    public int putChatRoomLastReadDao(Integer memberId, Integer chatroomId, String lastRead) {
        String sql = "UPDATE chatroom_read_status SET last_read_time = :last_read_time WHERE chatroom_id = :chatroom_id AND member_id = :member_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("chatroom_id", chatroomId);
        map.put("last_read_time", lastRead);

        int updateId = namedParameterJdbcTemplate.update(sql, map);
    
        return updateId;
    }

    public void postChatMessageDao(List<ChatMessage> chatMessage) {
        String sql = "INSERT INTO chatmessage(chatroom_id, sender_id, recipient_id, message, timestamp) "
        + "VALUES (?, ?, ?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (int i=0; i<chatMessage.size(); i++) {
            if (chatMessage.get(i).getId() != null) {
                continue;
            }
            Object[] value = new Object[5];
            value[0] = chatMessage.get(i).getChatroomId();
            value[1] = chatMessage.get(i).getSenderId();
            value[2] = chatMessage.get(i).getRecipientId();
            value[3] = chatMessage.get(i).getMessage();
            value[4] = chatMessage.get(i).getTimestamp();
            batch.add(value);  
        }

        if (!batch.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batch);
        }
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

    public List<ChatRoom> getChatroomListDao(Integer memberId) {
        String sql = "SELECT chatroom.*, parkinglotdata.name, chatroom_read_status.last_read_time, "
                + "(SELECT chatmessage.timestamp FROM chatmessage "
                + "WHERE chatmessage.chatroom_id = chatroom.id AND chatmessage.recipient_id = :member_id "
                + "ORDER BY chatmessage.timestamp DESC LIMIT 1) AS last_received_message_time "
                + "FROM chatroom "
                + "LEFT JOIN parkinglotdata ON chatroom.parkingLot_id = parkinglotdata.id " 
                + "LEFT JOIN chatroom_read_status ON chatroom.id = chatroom_read_status.chatroom_id AND chatroom_read_status.member_id = :member_id "
                + "WHERE chatroom.sender_id = :member_id OR chatroom.recipient_id = :member_id "
                + "ORDER BY chatroom.last_activity_time DESC";
        
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        List<ChatRoom> chatroom = namedParameterJdbcTemplate.query(sql, map, new ChatRoomRowMapper());

        return chatroom;
    }

    public List<ChatMessage> getChatMessageList (Integer chatroomId) {
        String sql = "SELECT * FROM chatmessage WHERE chatroom_id = :chatroom_id";
        
        HashMap<String, Object> map = new HashMap<>();
        map.put("chatroom_id", chatroomId);
        List<ChatMessage> chatmessage = namedParameterJdbcTemplate.query(sql, map, new ChatMessageRowMapper());

        return chatmessage;
    }
}

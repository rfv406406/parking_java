package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.sideproject.parking_java.model.ChatRoom;

public class ChatRoomRowMapper implements RowMapper<ChatRoom> {
    @Override
    public ChatRoom mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ChatRoom chatRoom = new ChatRoom();

        ResultSetMetaData metaData = rs.getMetaData();
        int length = metaData.getColumnCount();

        for (int i=1; i<=length; i++) {
            String columnName = metaData.getColumnName(i);
            if (columnName.equals("name")) {
                chatRoom.setParkingLotName(rs.getString("name"));
            }
        }
        
        chatRoom.setId(rs.getInt("id"));
        chatRoom.setSenderId(rs.getInt("sender_id"));
        chatRoom.setRecipientId(rs.getInt("recipient_id"));
        chatRoom.setParkingLotId(rs.getInt("parkingLot_id"));

        return chatRoom;
    }
}

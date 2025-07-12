package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.sideproject.parking_java.model.ChatMessage;

public class ChatMessageRowMapper implements RowMapper<ChatMessage> {
    @Override
    public ChatMessage mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ChatMessage chatMessage = new ChatMessage();

        ResultSetMetaData metaData = rs.getMetaData();
        int length = metaData.getColumnCount();

        for (int i=1; i<=length; i++) {
            String columnName = metaData.getColumnName(i);
            
            if (columnName.equals("message")) {
                chatMessage.setMessage(rs.getString("message"));
            }
        }

        chatMessage.setId(rs.getInt("id"));
        chatMessage.setChatroomId(rs.getInt("chatroom_id"));
        chatMessage.setSenderId(rs.getInt("sender_id"));
        chatMessage.setRecipientId(rs.getInt("recipient_id"));
        chatMessage.setTimestamp(rs.getString("timestamp"));

        return chatMessage;
    }
    
}

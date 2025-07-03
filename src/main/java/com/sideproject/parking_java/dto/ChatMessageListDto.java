package com.sideproject.parking_java.dto;

import java.util.List;

import com.sideproject.parking_java.model.ChatMessage;

public class ChatMessageListDto {
    private List<ChatMessage> chatMessage;
    
    public List<ChatMessage> getChatMessage() { 
        return chatMessage; 
    }
    public void setChatMessage(List<ChatMessage> chatMessage) { 
        this.chatMessage = chatMessage; 
    }
}

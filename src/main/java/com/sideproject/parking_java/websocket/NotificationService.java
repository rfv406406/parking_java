package com.sideproject.parking_java.websocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.model.ChatMessage;

@Service
public class NotificationService {
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendNotification(Map<String, Object> parkingLotMap) {
        simpMessagingTemplate.convertAndSend("/topic/parkingLot", parkingLotMap);
    }

    public void sendNotification(Integer parkingLotId) {
        simpMessagingTemplate.convertAndSend("/topic/parkingLot", parkingLotId);
    }

    public void sendNotification(ChatMessage chatMessage) {
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getChatroomId().toString(), "/queue/messages", chatMessage);
    }
}

package com.sideproject.parking_java.websocket;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;

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

    public void sendNotification(List<ChatMessage> chatMessageList) {
        for (int i=0; i<chatMessageList.size(); i++) {
            System.out.println("chatMessageList.get(i).getChatroomId().toString(): "+chatMessageList.get(i).getChatroomId().toString());
            simpMessagingTemplate.convertAndSendToUser(chatMessageList.get(i).getChatroomId().toString(), "/queue/messages", chatMessageList.get(i));
        }
    }

    public void sendNotification(String channel, ChatRoom chatRoom) {
        System.out.println("chatRoom: "+chatRoom);
        System.out.println("channel: "+channel);
        String channelId = channel.split("receiver:")[1];
        simpMessagingTemplate.convertAndSendToUser(channelId, "/queue/chatroom", chatRoom);
    }
}

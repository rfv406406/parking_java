package com.sideproject.parking_java.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;
import com.sideproject.parking_java.redis.RedisService;
import com.sideproject.parking_java.service.ChatService;



@RestController
public class ChatController {

    @Autowired 
    private ChatService chatService;

    @Autowired 
    private RedisService redisService;

    @PostMapping("/api/chatroom")
    public ResponseEntity<Integer> postChatRoom(@RequestBody ChatRoom chatRoom) {
        Integer chatRoomId = chatService.postChatRoomService(chatRoom);
        ResponseEntity<Integer> response = ResponseEntity.status(HttpStatus.OK).body(chatRoomId);

        return response;
    }

    @PostMapping("/api/chatmessage")
    public ResponseEntity<String> postChatMessage(@ModelAttribute ChatMessage chatMessage) {
        chatService.postChatMessageService(chatMessage);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");

        return response;
    }

    @MessageMapping("/chatmessage")
    public void sendMessage(ChatMessage chatMessage) {
        System.out.println("chatMessage: "+chatMessage);
        redisService.updateChatMessageInRedis(chatMessage);
    }

    @GetMapping("/api/chatmessage/{chatroomId}")
    public ResponseEntity<List<ChatMessage>> getChatmessage(@PathVariable Integer chatroomId) {
        List<ChatMessage> chatMessage = chatService.getChatMessageService(chatroomId);
        ResponseEntity<List<ChatMessage>> response = ResponseEntity.status(HttpStatus.OK).body(chatMessage);

        return response;
    }

    @GetMapping("/api/chatroom")
    public ResponseEntity<List<ChatRoom>> getChatmessage() {
        List<ChatRoom> chatroom = chatService.getChatRoomService();
        ResponseEntity<List<ChatRoom>> response = ResponseEntity.status(HttpStatus.OK).body(chatroom);

        return response;
    }
    
}

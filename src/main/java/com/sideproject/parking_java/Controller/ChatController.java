package com.sideproject.parking_java.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.dao.ChatDao;
import com.sideproject.parking_java.dto.ChatMessageListDto;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;
import com.sideproject.parking_java.model.MemberDetails;
import com.sideproject.parking_java.redis.RedisService;
import com.sideproject.parking_java.service.ChatService;




@RestController
public class ChatController {

    @Autowired 
    private ChatService chatService;

    @Autowired 
    private RedisService redisService;

    @Autowired 
    private ChatDao chatDao;
 
    @PostMapping("/api/chatroom")
    public ResponseEntity<Integer> postChatRoom(@RequestBody ChatRoom chatRoom) {
        Integer chatRoomId = chatService.postChatRoomService(chatRoom);
        ResponseEntity<Integer> response = ResponseEntity.status(HttpStatus.OK).body(chatRoomId);

        return response;
    }

    @PostMapping("/api/chatmessage")
    public ResponseEntity<String> postChatMessage(@ModelAttribute("chatMessage") ChatMessageListDto chatMessageList) throws DatabaseError, IOException {
        chatService.postChatMessageService(chatMessageList.getChatMessage());
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");

        return response;
    }

    @SuppressWarnings("null")
    @MessageMapping("/chatmessage")
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, List<ChatMessage> chatMessage) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        Object principal = auth.getPrincipal();
        MemberDetails memberDetails = (MemberDetails)principal;
        int memberId = memberDetails.getId();
        Integer recipientId = chatMessage.get(chatMessage.size() - 1).getRecipientId();
        boolean isRedisConnected = redisService.isRedisConnected();
        String chatroomMapKey = "memberId-" + Integer.toString(memberId) + "-chatRoomMap";
        String recipientChatroomMapKey = "memberId-" + recipientId + "-chatRoomMap";
        Integer chatroomId = chatMessage.get(chatMessage.size() - 1).getChatroomId();
        String chatMessageListKey = "chatMessageList-" + chatroomId;
        
        if (isRedisConnected) {
            redisService.updateChatMessageListInRedis(chatMessageListKey, Integer.toString(chatroomId), chatMessage);
            redisService.updateChatRoomMapInRedis(chatroomMapKey, chatroomId, memberId, chatMessage.get(chatMessage.size() - 1).getTimestamp(), null, null, null, null);
            redisService.updateChatRoomMapInRedis(recipientChatroomMapKey, chatroomId, recipientId, null, null, null, null, chatMessage.get(chatMessage.size() - 1).getTimestamp());            
        } else {
            chatDao.postChatMessageDao(chatMessage);
            chatDao.putChatRoomActivityTimeDao(chatroomId, chatMessage.get(chatMessage.size() - 1).getTimestamp());
        }
    }

    @GetMapping("/api/chatmessage/{chatroomId}")
    public ResponseEntity<List<ChatMessage>> getChatmessage(@PathVariable Integer chatroomId) {
        List<ChatMessage> chatMessage = chatService.getChatMessageService(chatroomId);
        ResponseEntity<List<ChatMessage>> response = ResponseEntity.status(HttpStatus.OK).body(chatMessage);

        return response;
    }

    @GetMapping("/api/chatroom")
    public ResponseEntity<List<ChatRoom>> getChatroom() {
        List<ChatRoom> chatroom = chatService.getChatRoomService();
        ResponseEntity<List<ChatRoom>> response = ResponseEntity.status(HttpStatus.OK).body(chatroom);

        return response;
    }

    @PutMapping("/api/chatroom/{chatroomId}/{lastRead}")
    public ResponseEntity<String> putMethodName(@PathVariable Integer chatroomId, @PathVariable String lastRead) {
        chatService.putChatroomService(chatroomId, lastRead);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");

        return response;
    }
    
}

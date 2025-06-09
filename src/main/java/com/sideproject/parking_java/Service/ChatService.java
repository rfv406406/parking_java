package com.sideproject.parking_java.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.ChatDao;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;
import com.sideproject.parking_java.redis.RedisService;
import com.sideproject.parking_java.utility.MemberIdUtil;
import com.sideproject.parking_java.utility.S3Util;

@Service
public class ChatService {

    @Autowired 
    private ChatDao chatDao;

    @Autowired 
    private RedisOperations<String, Object> operations;

    @Autowired 
    private RedisService redisService;

    @Autowired
    private S3Util s3Util;


    public Integer postChatRoomService(ChatRoom chatRoom) {
        Integer chatroomId = chatDao.postChatRoomDao(chatRoom);

        if (chatroomId != null) {
            chatDao.putChatRoomDao(chatroomId, chatRoom);
        }
        return chatroomId;
    }

    public void postChatMessageService(ChatMessage chatMessage) throws DatabaseError {
        if (chatMessage.getMessage() != null) {
            chatDao.postChatMessageDao(chatMessage);
        }
        if (chatMessage.getImg() != null) {
            String imgUrl = s3Util.uploadToS3(chatMessage.getImg()[0]);
            chatMessage.setMessage(imgUrl);
            chatMessage.setImg(null);
            chatDao.postChatMessageDao(chatMessage);
            // chatDao.postChatFileDao(chatMessage);
        }
        redisService.updateChatMessageInRedis(chatMessage);
    }

    public List<ChatMessage> getChatMessageService(Integer chatroomId) {
        boolean isRedisConnected = redisService.isRedisConnected();
        Boolean hasChatroomMap = operations.hasKey("chatRoomMap");
        List<ChatMessage> chatMessage = new ArrayList<>();
        Map<Object, Object> chatroomMap = redisService.getChatMessageMapFromRedis(chatroomId);
        if (isRedisConnected && hasChatroomMap != null && hasChatroomMap && chatroomMap != null) {
            for (Object chatmessage : chatroomMap.values()) {
                ChatMessage message = (ChatMessage) chatmessage;
                chatMessage.add(message);
            }
        } else {
            chatMessage = chatDao.getChatMessage(chatroomId);
            redisService.putChatMessageMapInRedis(chatroomId, chatMessage);
        }

        return chatMessage;
    }

    public List<ChatRoom> getChatRoomService() {
        int memberId = MemberIdUtil.getMemberIdUtil();
        List<ChatRoom> chatroom = chatDao.getChatroomDao(memberId);

        return chatroom;
    }
}

package com.sideproject.parking_java.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sideproject.parking_java.dao.ChatDao;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;
import com.sideproject.parking_java.redis.RedisService;
import com.sideproject.parking_java.utility.MemberIdUtil;
import com.sideproject.parking_java.utility.S3Util;

@Service
@Transactional
public class ChatService {

    @Autowired 
    private ChatDao chatDao;

    @Autowired 
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired 
    private RedisService redisService;

    @Autowired
    private S3Util s3Util;

    public Integer postChatRoomService(ChatRoom chatRoom) throws DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        Integer returnedChatroomId = chatDao.getChatroomDao(chatRoom);
        Integer recipientId = chatRoom.getRecipientId();
        String chatroomMapKey = "memberId-" + Integer.toString(memberId) + "-chatRoomMap";
        String recipientChatroomMapKey = "memberId-" + Integer.toString(chatRoom.getRecipientId()) + "-chatRoomMap";
        boolean isRedisConnected = redisService.isRedisConnected();

        if (returnedChatroomId == null) {
            returnedChatroomId = chatDao.postChatRoomDao(chatRoom);
            if (memberId == recipientId) {
                chatDao.postChatRoomLastReadDao(memberId, returnedChatroomId, chatRoom);
            } else {
                chatDao.postChatRoomLastReadDao(memberId, returnedChatroomId, chatRoom);
                chatDao.postChatRoomLastReadDao(recipientId, returnedChatroomId, chatRoom);
            }
        } else {
            chatDao.putChatRoomParkingLotIdDao(returnedChatroomId, chatRoom);
            chatDao.putChatRoomActivityTimeDao(returnedChatroomId, chatRoom.getLastActivity());
        }

        if (isRedisConnected) {
            boolean hasChatRoomMap = redisTemplate.hasKey(chatroomMapKey);
            ChatRoom chatroom = (ChatRoom) redisTemplate.opsForHash().get(chatroomMapKey, Integer.toString(returnedChatroomId));
            boolean hasRecipientChatRoomMap = redisTemplate.hasKey(recipientChatroomMapKey);
            ChatRoom recipientChatroom = (ChatRoom) redisTemplate.opsForHash().get(recipientChatroomMapKey, Integer.toString(returnedChatroomId));
            if (hasChatRoomMap && chatroom != null) {
                redisService.updateChatRoomMapInRedis(chatroomMapKey, returnedChatroomId, memberId, chatRoom.getLastActivity(), chatRoom.getParkingLotId(), chatRoom.getParkingLotName(), chatRoom.getLastRead(), null);
            } else if (hasChatRoomMap) {
                chatRoom.setId(returnedChatroomId);
                redisService.putChatRoomMapInRedis(chatroomMapKey, chatRoom, memberId);
            }
            if (hasRecipientChatRoomMap && recipientChatroom != null) {
                redisService.updateChatRoomMapInRedis(recipientChatroomMapKey, returnedChatroomId, recipientId, chatRoom.getLastActivity(), chatRoom.getParkingLotId(), chatRoom.getParkingLotName(), chatRoom.getLastRead(), null);
            } else if (hasRecipientChatRoomMap) {
                chatRoom.setId(returnedChatroomId);
                redisService.putChatRoomMapInRedis(recipientChatroomMapKey, chatRoom, recipientId);
            }
        }

        return returnedChatroomId;
    }

    public void postChatMessageService(List<ChatMessage> chatMessage) throws DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        Integer chatroomId = chatMessage.get(chatMessage.size() - 1).getChatroomId();
        String chatroomMapKey = "memberId-" + Integer.toString(memberId) + "-chatRoomMap";
        Integer recipientId = chatMessage.get(chatMessage.size() - 1).getRecipientId();
        String recipientChatroomMapKey = "memberId-" + recipientId + "-chatRoomMap";
        String chatMessageListKey = "chatMessageList-" + chatroomId;
        boolean isRedisConnected = redisService.isRedisConnected();

        for (int i=0; i<chatMessage.size(); i++) {
            if (chatMessage.get(i).getImg() != null) {
                String imgUrl = s3Util.uploadToS3(chatMessage.get(i).getImg()[0]);
                chatMessage.get(i).setMessage(imgUrl);
                chatMessage.get(i).setImg(null);
            }
        }

        chatDao.postChatMessageDao(chatMessage);
        chatDao.putChatRoomActivityTimeDao(chatroomId, chatMessage.get(chatMessage.size() - 1).getTimestamp());
        // chatDao.postChatFileDao(chatMessage);
        if (isRedisConnected) {
            boolean hasChatRoomMap = redisTemplate.hasKey(chatroomMapKey);
            boolean hasChatMessageList = redisTemplate.hasKey(chatMessageListKey);
            boolean hasRecipientChatroomMapKey = redisTemplate.hasKey(recipientChatroomMapKey);
            ChatRoom chatroom = (ChatRoom) redisTemplate.opsForHash().get(chatroomMapKey, chatroomId.toString());
            if (hasChatMessageList) {
                redisService.updateChatMessageListInRedis(chatMessageListKey, Integer.toOctalString(chatroomId), chatMessage);
            }
            if (hasChatRoomMap && chatroom != null) {
                redisService.updateChatRoomMapInRedis(chatroomMapKey, chatroomId, memberId, chatMessage.get(chatMessage.size() - 1).getTimestamp(), null, null, null, null);
            }
            if (hasRecipientChatroomMapKey) {
                redisService.updateChatRoomMapInRedis(recipientChatroomMapKey, chatroomId, recipientId, null, null, null, null, chatMessage.get(chatMessage.size() - 1).getTimestamp());
            }
        }
    }

    public List<ChatMessage> getChatMessageService(Integer chatroomId) {
        boolean isRedisConnected = redisService.isRedisConnected();
        String chatMessageListKey = "chatMessageList-" + chatroomId.toString();
        boolean hasChatMessageList = redisTemplate.hasKey(chatMessageListKey);
        List<ChatMessage> chatMessage = new ArrayList<>();
        if (isRedisConnected && hasChatMessageList) {
            List<Object> chatMessageList = redisService.getChatMessageListFromRedis(chatMessageListKey);
            if (chatMessageList == null) {
                chatMessage = chatDao.getChatMessageList(chatroomId);
                return chatMessage;
            }
            for (Object chatmessage : chatMessageList) {
                ChatMessage message = (ChatMessage) chatmessage;
                chatMessage.add(message);
            }
        } else if (isRedisConnected) {
            chatMessage = chatDao.getChatMessageList(chatroomId);
            redisService.putChatMessageListInRedis(chatMessageListKey, chatMessage);
        } else {
            chatMessage = chatDao.getChatMessageList(chatroomId);
        }

        return chatMessage;
    }

    public List<ChatRoom> getChatRoomService() {
        int memberId = MemberIdUtil.getMemberIdUtil();
        boolean isRedisConnected = redisService.isRedisConnected();
        String chatroomMapKey = "memberId-" + Integer.toString(memberId) + "-chatRoomMap";
        boolean hasChatRoomMap = redisTemplate.hasKey(chatroomMapKey);
        List<ChatRoom> chatroomList = new ArrayList<>();

        if (isRedisConnected && hasChatRoomMap) {
            List<Object> chatroomObject = redisService.getChatRoomMapFromRedis(chatroomMapKey);
            if (chatroomObject == null) {
                chatroomList = chatDao.getChatroomListDao(memberId);
                return chatroomList;
            }
            for (Object item : chatroomObject) {
                ChatRoom chatroom = (ChatRoom) item;
                chatroomList.add(chatroom);
            }
            chatroomList.sort(Comparator.comparing(ChatRoom::getLastActivity).reversed());
        } else if (isRedisConnected) {
            chatroomList = chatDao.getChatroomListDao(memberId);
            if (chatroomList.isEmpty()) {
                return null;
            }
            redisService.putChatRoomMapListInRedis(chatroomMapKey, chatroomList);
        } else {
            chatroomList = chatDao.getChatroomListDao(memberId);
        }

        return chatroomList;
    }

    public void putChatroomService(Integer chatroomId, String lastRead) {
        int memberId = MemberIdUtil.getMemberIdUtil();
        String chatroomMapKey = "memberId-" + Integer.toString(memberId) + "-chatRoomMap";
        boolean isRedisConnected = redisService.isRedisConnected();
        boolean hasChatRoomMap = redisTemplate.hasKey(chatroomMapKey);

        if (isRedisConnected) {
            ChatRoom chatroom = (ChatRoom) redisTemplate.opsForHash().get(chatroomMapKey, Integer.toString(chatroomId));
            if (hasChatRoomMap && chatroom != null) {
                redisService.updateChatRoomMapInRedis(chatroomMapKey, chatroomId, memberId, null, null , null, lastRead, null);
            }
        } else {
            chatDao.putChatRoomLastReadDao(memberId, chatroomId, lastRead);
        }
    }
}

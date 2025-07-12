package com.sideproject.parking_java.redis;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;
import com.sideproject.parking_java.websocket.NotificationService;

@Component
public class RedisSubscriber implements MessageListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    @Override
    @SuppressWarnings({ "CallToPrintStackTrace", "unchecked" })
    public void onMessage(@SuppressWarnings("null") Message message, @SuppressWarnings("null") byte[] pattern) {

        Object payload = redisTemplate.getValueSerializer().deserialize(message.getBody());
        String channel = new String(message.getChannel());

        if (payload instanceof Map<?,?> map) {
            try {
                handleMap((Map<String, Object>) map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (payload instanceof List<?> tmp) {
            List<ChatMessage> chatList = (List<ChatMessage>) tmp;
            handleChatMessageList(chatList);
        } else if (payload instanceof Integer integer) {
            handleInt(integer);
        } else if (payload instanceof ChatRoom chatRoom) {
            handleChatroom(channel, chatRoom);
        }
    }

    public void handleMap(Map<String, Object> parkingLotMap) throws Exception {
        System.out.println("parkingLotMap: " + parkingLotMap);
        logger.info("parkingLotMap", parkingLotMap);
        notificationService.sendNotification(parkingLotMap);
    }

    public void handleInt(Integer parkingLotId) {
        System.out.println("int: " + parkingLotId);
        logger.info("int", parkingLotId);
        notificationService.sendNotification(parkingLotId);
    }

    public void handleChatMessageList(List<ChatMessage> chatMessageList) {
        for (int i=0; i<chatMessageList.size(); i++) {
            System.out.println("chatMessage: " + chatMessageList.get(i));
            logger.info("chatMessage", chatMessageList.get(i));
        }
        
        notificationService.sendNotification(chatMessageList);
    }

    public void handleChatroom(String channel, ChatRoom chatRoom) {
        System.out.println("chatRoom: " + chatRoom);
        logger.info("chatRoom", chatRoom);
        notificationService.sendNotification(channel, chatRoom);
    }
}

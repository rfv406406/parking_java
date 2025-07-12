package com.sideproject.parking_java.redis;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.ChatMessage;
import com.sideproject.parking_java.model.ChatRoom;

@Component
public class RedisPublisher<fianl> {

    @Autowired
    private ChannelTopic topicParking;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void publishParkingLot(Map<String, Object> parkingLotMap) {
        redisTemplate.convertAndSend(topicParking.getTopic(), parkingLotMap);
    }

    public void publishParkingLot(Integer parkingLotId) {
        redisTemplate.convertAndSend(topicParking.getTopic(), parkingLotId);
    }

    public void publishChatMessage(String chatroomId, List<ChatMessage> chatMessageList) {
        String topicChat = "chat:room:" + chatroomId;
        redisTemplate.convertAndSend(topicChat, chatMessageList);
    }

    public void publishChatroom(String receiver, ChatRoom chatroom) {
        String topicReceiver = "receiver:" + receiver;
        redisTemplate.convertAndSend(topicReceiver, chatroom);
    }
}

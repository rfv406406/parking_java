package com.sideproject.parking_java.redis;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.ChatMessage;

@Component
public class RedisPublisher<fianl> {

    @Autowired
    private ChannelTopic topicParking;

    @Autowired
    private RedisOperations<String, Object> operations;
    
    public void publishParkingLot(Map<String, Object> parkingLotMap) {
        operations.convertAndSend(topicParking.getTopic(), parkingLotMap);
    }

    public void publishParkingLot(Integer parkingLotId) {
        operations.convertAndSend(topicParking.getTopic(), parkingLotId);
    }

    public void publishChatMessage(String chatroomId, ChatMessage chatMessage) {
        String topicChat =  "chat:room:" + chatroomId;
        operations.convertAndSend(topicChat, chatMessage);
    }
}

package com.sideproject.parking_java.redis;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisPublisher {

    @Autowired
    private RedisOperations<String, Object> operations;
    
    public void publish(ChannelTopic channelTopic, Map<String, Object> parkingLotMap) {
        operations.convertAndSend(channelTopic.getTopic(), parkingLotMap);
        System.out.println("parkingLotMap: "+ parkingLotMap);
    }

    public void publish(ChannelTopic channelTopic, Integer parkingLotId) {
        operations.convertAndSend(channelTopic.getTopic(), parkingLotId);
        System.out.println("parkingLotId: "+ parkingLotId);
    }
}

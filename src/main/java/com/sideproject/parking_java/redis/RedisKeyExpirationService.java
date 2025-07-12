package com.sideproject.parking_java.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyExpirationService extends KeyExpirationEventMessageListener {

    final static Logger logger = LoggerFactory.getLogger(RedisKeyExpirationService.class);

    public RedisKeyExpirationService(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void doHandleMessage(@SuppressWarnings("null") Message message) {
        
        byte[] body = message.getBody();
        
        byte[] channel = message.getChannel();
        
        logger.info("message = {}, channel = {}", new String(body), new String(channel));
        System.out.println("body: " + new String(body));
        System.out.println("channel: " + new String(channel));
    }
}

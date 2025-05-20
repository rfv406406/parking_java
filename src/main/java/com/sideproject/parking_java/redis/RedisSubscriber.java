package com.sideproject.parking_java.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.websocket.NotificationService;

@Component
public class RedisSubscriber {

    @Autowired
    private NotificationService notificationService;

    Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    @SuppressWarnings("unchecked")
    public void handleMessage(Object payload) throws Exception {
        if (payload instanceof Map) {
            handleMap((Map<String, Object>) payload);
        } else if (payload instanceof String string) {
            handleString(string);
        } else if (payload instanceof Integer integer) {
            handleInt(integer);
        }
    }

    public void handleMap(Map<String, Object> parkingLotMap) throws Exception {
        System.out.println("parkingLotMap: " + parkingLotMap);
        logger.info("parkingLotMap", parkingLotMap);
        notificationService.sendNotification(parkingLotMap);
    }

    public void handleString(String string) {
        System.out.println("string: " + string);
        logger.info("string", string);
    }

    public void handleInt(Integer parkingLotId) {
        System.out.println("int: " + parkingLotId);
        logger.info("int", parkingLotId);
        notificationService.sendNotification(parkingLotId);
    }

    // @MessageMapping("/parkingLot")
    // @SendTo("/topic/parkingLot")
    // public Map<String, Object> returnUpdateData(Map<String, Object> parkingLotMap) throws Exception {
    //     Thread.sleep(1000); // simulated delay
    //     return parkingLotMap;
    // }
}

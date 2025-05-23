package com.sideproject.parking_java.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue")
    .setHeartbeatValue(new long[] {10000, 10000})
    .setTaskScheduler(new DefaultManagedTaskScheduler());
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
    registry
    .addEndpoint("/parkingLot-websocket", "/chat")
    .setAllowedOriginPatterns("*")  //all pass
    .withSockJS();       
  }

  @Override
	public void configureWebSocketTransport(@NonNull WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(4 * 8192);
		registry.setTimeToFirstMessage(30000);
	}
}

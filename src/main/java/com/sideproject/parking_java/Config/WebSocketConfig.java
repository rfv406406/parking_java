package com.sideproject.parking_java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.sideproject.parking_java.model.MemberDetails;
import com.sideproject.parking_java.service.UserDetailsServiceImpl;
import com.sideproject.parking_java.utility.JwtUtil;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Autowired
  private UserDetailsServiceImpl userDetailsServiceImpl;

  @Override
  public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/user")
    .setHeartbeatValue(new long[] {10000, 10000})
    .setTaskScheduler(webSocketMessageBrokerTaskScheduler());
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

  @Bean
  public ThreadPoolTaskScheduler webSocketMessageBrokerTaskScheduler() {
      ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
      taskScheduler.setPoolSize(1);
      taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
      taskScheduler.initialize();
      return taskScheduler;
  }

  @Override
  public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
    registry
    .addEndpoint("/parkingLot-websocket", "/chat-websocket")
    .setAllowedOriginPatterns("*")  //all pass
    .withSockJS();       
  }

  @Override
	public void configureWebSocketTransport(@NonNull WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(4 * 8192);
		registry.setTimeToFirstMessage(30000);
	}

  @Override
  public void configureClientInboundChannel(@SuppressWarnings("null") ChannelRegistration registration) throws IllegalArgumentException {
    registration.interceptors(new ChannelInterceptor() {
      @SuppressWarnings("null")
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearer = accessor.getFirstNativeHeader("Authorization");
            if (bearer != null && bearer.startsWith("Bearer ")) {
                String token = bearer.substring(7);
                String account = JwtUtil.parseToken(token);
                MemberDetails memberDetails = userDetailsServiceImpl.loadUserByUsername(account);
                memberDetails.eraseCredentials();
                Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
                accessor.setUser(authentication);
            } else {
              throw new IllegalArgumentException("Invalid JWT token");
            }
        }
        return message;
      }
    });
  }
}

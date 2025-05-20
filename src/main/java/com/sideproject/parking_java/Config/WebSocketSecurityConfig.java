// package com.sideproject.parking_java.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.Message;
// import org.springframework.security.authorization.AuthorizationManager;
// import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
// import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

// @Configuration
// @EnableWebSocketSecurity
// public class WebSocketSecurityConfig {
//     @Bean
//     public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//         messages
//                 .nullDestMatcher().permitAll() 
//                 .simpSubscribeDestMatchers("/topic/**").permitAll() 
//                 .simpDestMatchers("/topic/**").permitAll()
//                 .simpDestMatchers("/app/**").permitAll()
//                 // .simpSubscribeDestMatchers("/user/**", "/topic/parkingLot/**").hasRole("USER"); 
//                 // .simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll() 
//                 .anyMessage().permitAll(); 

//         return messages.build();
//     }
// }

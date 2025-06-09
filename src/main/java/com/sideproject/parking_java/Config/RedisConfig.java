package com.sideproject.parking_java.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sideproject.parking_java.redis.RedisSubscriber;

@Configuration
@EnableTransactionManagement
public class RedisConfig {
    @Bean
    LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setEnableTransactionSupport(true); 
        template.setConnectionFactory(connectionFactory);
        setSerializer(template);
        return template;
    }

    @Bean
    PlatformTransactionManager transactionManager(DataSource ds) throws SQLException {
        return new DataSourceTransactionManager(ds);   
    }

    public void setSerializer(RedisTemplate<String, Object> redisTemplate) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

    @Bean
    ChannelTopic topicParking() {
        return new ChannelTopic("parkingLotMap");
    }

    @Bean
    MessageListenerAdapter messageListenerAdapter(RedisSubscriber redisSubscriber, RedisTemplate<String, Object> redisTemplate) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(redisSubscriber, "handleMessage");
        adapter.setSerializer(redisTemplate.getValueSerializer());
        return adapter;
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, topicParking());
        container.addMessageListener(messageListenerAdapter, new PatternTopic("chat:room:*"));

        return container;
    }
}

package com.sideproject.parking_java.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sideproject.parking_java.redis.RedisSubscriber;

@Configuration
@EnableTransactionManagement
@EnableScheduling
@EnableRedisRepositories(
    enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.OFF,
    keyspaceNotificationsConfigParameter = ""
)
public class RedisConfig {

    // @Value("${redisPassword}")
    // private String redisPassword;

    // @Bean
    // LettuceConnectionFactory connectionFactory() {
    //     // RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration()
    //     //     .clusterNode("parkingjava-3f4b3j.serverless.apse2.cache.amazonaws.com", 6379);
            
    //     // RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    //     // config.setHostName("master.parkingjava.3f4b3j.apse2.cache.amazonaws.com");
    //     // config.setPort(6379); 
    //     // // config.setPassword(redisPassword); 
    //     // config.setDatabase(0);

    //     // GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
    //     // poolConfig.setMaxIdle(30); 
    //     // poolConfig.setMinIdle(0); 
    //     // poolConfig.setMaxTotal(300); 

    //     // LettucePoolingClientConfiguration poolingClientConfig = 
    //     //     LettucePoolingClientConfiguration.builder()
    //     //     .commandTimeout(Duration.ofMillis(3000))
    //     //     .poolConfig(poolConfig)
    //     //     .useSsl() // 啟用 TLS
    //     //     .disablePeerVerification() // 禁用對等驗證
    //     //     .build();
        
    //     LettuceConnectionFactory factory = new LettuceConnectionFactory();
    //     factory.setValidateConnection(false);

    //     return factory;
    // }

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
        MessageListenerAdapter adapter = new MessageListenerAdapter(redisSubscriber, "onMessage");
        adapter.setSerializer(redisTemplate.getValueSerializer());
        return adapter;
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, topicParking());
        container.addMessageListener(messageListenerAdapter, new PatternTopic("chat:room:*"));
        container.addMessageListener(messageListenerAdapter, new PatternTopic("receiver:*"));

        return container;
    }
}

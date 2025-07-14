package com.sideproject.parking_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
// 開啟 Redis Repositories 支援 keyspace 事件，但不要去修改伺服器設定
@EnableRedisRepositories(
  enableKeyspaceEvents = EnableKeyspaceEvents.ON_STARTUP,
  keyspaceNotificationsConfigParameter = ""
)
public class ParkingJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingJavaApplication.class, args);
	}

}

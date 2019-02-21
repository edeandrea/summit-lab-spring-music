package io.k8s.samples.music.config.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@Profile("kubernetes & redis")
public class RedisK8sConfig {
	@Bean
	public RedisConnectionFactory redisConnection() {
		// @TODO Define RedisConnectionFactory
		return null;
	}
}

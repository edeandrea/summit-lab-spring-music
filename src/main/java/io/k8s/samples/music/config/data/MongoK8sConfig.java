package io.k8s.samples.music.config.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;

@Configuration
@Profile("kubernetes & mongodb")
public class MongoK8sConfig {
	@Bean
	public MongoDbFactory mongoDbFactory() {
		// @TODO Define MongoDbFactory
		return null;
	}
}

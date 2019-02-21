package io.k8s.samples.music.config.data;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("kubernetes & (mysql | postgres | oracle | sqlserver)")
public class RelationalK8sDataSourceConfig {
	@Bean
	public DataSource dataSource() {
		// @TODO Define DataSource bean
		return null;
	}
}

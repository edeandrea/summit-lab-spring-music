package com.redhat.springmusic.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.jdbc.datasource.JdbcTelemetry;

@Configuration
public class OtelDataSourceConfig {
	@Bean
	public DataSource dataSource(DataSourceProperties dataSourceProperties, OpenTelemetry openTelemetry) {
		var datasSource = DataSourceBuilder.create()
			.driverClassName(dataSourceProperties.determineDriverClassName())
			.url(dataSourceProperties.determineUrl())
			.username(dataSourceProperties.getUsername())
			.password(dataSourceProperties.getPassword())
			.build();

		return JdbcTelemetry.create(openTelemetry).wrap(datasSource);
	}
}

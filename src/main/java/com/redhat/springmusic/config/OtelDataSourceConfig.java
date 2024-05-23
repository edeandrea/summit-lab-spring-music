package com.redhat.springmusic.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.jdbc.datasource.JdbcTelemetry;

/**
 * This class is needed to allow tracing down to the JDBC level.
 * Taken from https://opentelemetry.io/docs/languages/java/automatic/spring-boot/#jdbc-instrumentation
 */
@Configuration
public class OtelDataSourceConfig {
	@Bean
	public DataSource dataSource(DataSourceProperties dataSourceProperties, OpenTelemetry openTelemetry) {
		var dataSource = DataSourceBuilder.create()
			.driverClassName(dataSourceProperties.determineDriverClassName())
			.url(dataSourceProperties.determineUrl())
			.username(dataSourceProperties.getUsername())
			.password(dataSourceProperties.getPassword())
			.build();

		return JdbcTelemetry.create(openTelemetry).wrap(dataSource);
	}
}

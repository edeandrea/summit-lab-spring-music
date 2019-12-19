package com.redhat.summit2019.springmusic.config;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {
	private final Optional<BuildProperties> buildProperties;

	public OpenAPIConfig(Optional<BuildProperties> buildProperties) {
		this.buildProperties = buildProperties;
	}

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.info(getInfo());
	}

	private Info getInfo() {
		return new Info()
			.title("Red Hat Summit 2019 - Spring Music")
			.version(this.buildProperties.map(BuildProperties::getVersion).orElse("1.0"))
			.description("Red Hat Summit 2019 sample application - Spring Music")
			.contact(
				new Contact()
					.name("Eric Deandrea")
					.email("edeandrea@redhat.com")
					.url("https://github.com/edeandrea/summit-lab-spring-music")
			);
	}
}

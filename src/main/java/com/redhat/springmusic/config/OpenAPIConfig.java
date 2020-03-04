package com.redhat.springmusic.config;

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
		OpenAPI openAPI = new OpenAPI()
			.info(getInfo());

		// Adds a 500 response message to all endpoints in the API
		// This is instead of us having to go and add an @ApiResponse annotation on every action method in every controller
//		Arrays.stream(RequestMethod.values())
//			.forEach(requestMethod ->
//				docket.globalResponseMessage(
//					requestMethod,
//					Arrays.asList(
//						new ResponseMessageBuilder()
//							.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
//							.message("Something bad happened!")
//							.build()
//					)
//				)
//			);

		return openAPI;
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

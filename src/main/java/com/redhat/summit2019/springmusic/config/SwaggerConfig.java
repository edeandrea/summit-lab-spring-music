package com.redhat.summit2019.springmusic.config;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.redhat.summit2019.springmusic.Application;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	private final Optional<BuildProperties> buildProperties;

	public SwaggerConfig(Optional<BuildProperties> buildProperties) {
		this.buildProperties = buildProperties;
	}

	@Bean
	public Docket apiDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
			.useDefaultResponseMessages(false)
			.apiInfo(apiInfo())
			.select()
				.apis(RequestHandlerSelectors.basePackage(Application.class.getPackage().getName()))
				.paths(PathSelectors.any())
			.build();

		// Adds a 500 response message to all endpoints in the API
		// This is instead of us having to go and add an @ApiResponse annotation on every action method in every controller
		Arrays.stream(RequestMethod.values())
			.forEach(requestMethod ->
				docket.globalResponseMessage(
					requestMethod,
					Arrays.asList(
						new ResponseMessageBuilder()
							.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
							.message("Something bad happened!")
							.build()
					)
				)
			);

		return docket;
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Red Hat Summit 2019 - Spring Music")
			.description("Red Hat Summit 2019 sample application - Spring Music")
			.version(this.buildProperties.map(BuildProperties::getVersion).orElse("1.0"))
			.contact(new Contact("Eric Deandrea", "https://github.com/edeandrea/summit-lab-spring-music", "edeandrea@redhat.com"))
			.build();
	}
}

package com.redhat.springmusic;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;

import com.redhat.springmusic.Application.ApplicationRuntimeHints;
import com.redhat.springmusic.domain.RandomIdGenerator;

@SpringBootApplication
@ImportRuntimeHints(ApplicationRuntimeHints.class)
@RegisterReflectionForBinding(RandomIdGenerator.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {
		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			hints.resources().registerResource(new ClassPathResource("albums.json"));
		}
	}
}

package com.redhat.springmusic;

import org.springframework.boot.SpringApplication;

public class TestApplication {
	public static void main(String[] args) {
		SpringApplication
			.from(Application::main)
			.run(args);
	}
}

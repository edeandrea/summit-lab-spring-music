package com.redhat.summit2019.springmusic;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.redhat.summit2019.springmusic.repositories.AlbumRepositoryPopulator;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class)
			.listeners(new AlbumRepositoryPopulator())
			.application()
			.run(args);
	}
}

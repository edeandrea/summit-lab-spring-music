package com.redhat.summit2019.springmusic.repositories;

import java.util.Collection;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2ResourceReader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.summit2019.springmusic.domain.jpa.Album;
import com.redhat.summit2019.springmusic.service.AlbumService;

@Component
public class AlbumRepositoryPopulator implements ApplicationListener<ApplicationReadyEvent> {
	private final AlbumService albumService;
	private final Jackson2ResourceReader resourceReader;
	private final Resource sourceData;

	public AlbumRepositoryPopulator(AlbumService albumService) {
		this.albumService = albumService;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.resourceReader = new Jackson2ResourceReader(mapper);
		this.sourceData = new ClassPathResource("albums.json");
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (this.albumService.isEmpty()) {
			populate();
		}
	}

	private void populate() {
		Object entity = getEntityFromResource(this.sourceData);

		if (entity instanceof Collection) {
			((Collection<?>) entity).stream()
				.filter(Album.class::isInstance)
				.map(Album.class::cast)
				.forEach(this.albumService::createAlbum);
		}
		else if (entity instanceof Album) {
			this.albumService.createAlbum((Album) entity);
		}
	}

	private Object getEntityFromResource(Resource resource) {
		try {
			return this.resourceReader.readFrom(resource, getClass().getClassLoader());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

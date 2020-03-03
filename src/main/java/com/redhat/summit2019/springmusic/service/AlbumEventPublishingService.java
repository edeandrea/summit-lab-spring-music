package com.redhat.summit2019.springmusic.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.redhat.summit2019.springmusic.domain.AlbumEventType;
import com.redhat.summit2019.springmusic.domain.jpa.Album;
import com.redhat.summit2019.springmusic.domain.jpa.AlbumEvent;
import com.redhat.summit2019.springmusic.repositories.jpa.AlbumRepository;

@Service
public class AlbumEventPublishingService implements AlbumService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumEventPublishingService.class);
	private final AlbumRepository albumRepository;
	private final ApplicationEventPublisher eventPublisher;

	public AlbumEventPublishingService(AlbumRepository albumRepository, ApplicationEventPublisher eventPublisher) {
		this.albumRepository = albumRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public Iterable<Album> getAllAlbums() {
		LOGGER.info("Getting all albums");
		return this.albumRepository.findAll();
	}

	@Override
	@Transactional
	public Album createAlbum(Album album) {
		LOGGER.info("Creating album {}", album);

		Album newAlbum = this.albumRepository.save(album);
		this.eventPublisher.publishEvent(
			AlbumEvent.album(newAlbum)
				.eventType(AlbumEventType.ALBUM_CREATED)
				.build()
		);

		return newAlbum;
	}

	@Override
	@Transactional
	public void updateAlbum(Album album) {
		this.albumRepository.findById(album.getId())
			.map(this.albumRepository::detach)
			.ifPresent(existingAlbum -> {
				LOGGER.info("Updating previous album {} to {}", existingAlbum, album);

				this.eventPublisher.publishEvent(
					AlbumEvent.album(this.albumRepository.save(album))
						.eventType(AlbumEventType.ALBUM_UPDATED)
						.build()
				);
			});
	}

	@Override
	public Optional<Album> getAlbum(String albumId) {
		LOGGER.info("Getting album {}", albumId);
		return this.albumRepository.findById(albumId);
	}

	@Override
	@Transactional
	public void deleteAlbum(String albumId) {
		if (this.albumRepository.existsById(albumId)) {
			LOGGER.info("Deleting album {}", albumId);
			this.albumRepository.deleteById(albumId);
			this.eventPublisher.publishEvent(
				AlbumEvent.builder()
					.eventType(AlbumEventType.ALBUM_DELETED)
					.albumId(albumId)
					.build()
			);
		}
	}

	@Override
	public boolean isEmpty() {
		return this.albumRepository.count() == 0;
	}
}

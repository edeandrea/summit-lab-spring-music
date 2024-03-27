package com.redhat.springmusic.service;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.redhat.springmusic.domain.event.AlbumCreatedEvent;
import com.redhat.springmusic.domain.event.AlbumDeletedEvent;
import com.redhat.springmusic.domain.event.AlbumUpdatedEvent;
import com.redhat.springmusic.domain.jpa.Album;
import com.redhat.springmusic.repositories.jpa.AlbumRepository;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;

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
	@NewSpan(name = "AlbumService.getAllAlbums")
	public Iterable<Album> getAllAlbums() {
		LOGGER.info("Getting all albums");
		return this.albumRepository.findAll();
	}

	@Override
	@Transactional
	@NewSpan(name = "AlbumService.createAlbum")
	public Album createAlbum(@SpanTag(key = "arg.album") Album album) {
		LOGGER.info("Creating album {}", album);

		Album newAlbum = this.albumRepository.save(album);
		this.eventPublisher.publishEvent(new AlbumCreatedEvent(newAlbum));

		return newAlbum;
	}

	@Override
	@Transactional
	@NewSpan(name = "AlbumService.updateAlbum")
	public void updateAlbum(@SpanTag(key = "arg.album") Album album) {
		this.albumRepository.findById(album.getId())
			.map(this.albumRepository::detach)
			.ifPresent(existingAlbum -> {
				LOGGER.info("Updating previous album {} to {}", existingAlbum, album);
				this.eventPublisher.publishEvent(new AlbumUpdatedEvent(existingAlbum, this.albumRepository.save(album)));
			});
	}

	@Override
	@NewSpan(name = "AlbumService.getAlbum")
	public Optional<Album> getAlbum(@SpanTag(key = "arg.albumId") String albumId) {
		LOGGER.info("Getting album {}", albumId);
		return this.albumRepository.findById(albumId);
	}

	@Override
	@Transactional
	@NewSpan(name = "AlbumService.deleteAlbum")
	public void deleteAlbum(@SpanTag(key = "arg.albumId") String albumId) {
		this.albumRepository.findById(albumId)
			.map(this.albumRepository::detach)
			.ifPresent(existingAlbum -> {
				LOGGER.info("Deleting album {}", existingAlbum.getId());
				this.albumRepository.delete(existingAlbum);
				this.eventPublisher.publishEvent(new AlbumDeletedEvent(existingAlbum));
			});
	}

	@Override
	public boolean isEmpty() {
		return this.albumRepository.count() == 0;
	}
}

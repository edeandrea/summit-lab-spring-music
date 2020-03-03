package com.redhat.summit2019.springmusic.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.redhat.summit2019.springmusic.domain.jpa.AlbumEvent;
import com.redhat.summit2019.springmusic.repositories.jpa.AlbumEventRepository;

@Service
public class DefaultAlbumEventService implements AlbumEventService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAlbumEventService.class);
	private final AlbumEventRepository albumEventRepository;

	public DefaultAlbumEventService(AlbumEventRepository albumEventRepository) {
		this.albumEventRepository = albumEventRepository;
	}

	@Override
	public Iterable<AlbumEvent> getAllEventsOrderedByTimestampDescending() {
		LOGGER.info("Getting all album events ordered by timestamp descending");
		return this.albumEventRepository.findAll(Sort.by("timestamp").descending());
	}

	@Override
	public AlbumEvent persistEvent(AlbumEvent event) {
		return this.albumEventRepository.save(event);
	}

	@Override
	public Optional<AlbumEvent> getById(long eventId) {
		return this.albumEventRepository.findById(eventId);
	}

	@Override
	public Iterable<AlbumEvent> getAllEventsForAlbumIdOrderedByTimestampDescending(String albumId) {
		return this.albumEventRepository.findAllByAlbumIdOrderByTimestampDesc(albumId);
	}
}

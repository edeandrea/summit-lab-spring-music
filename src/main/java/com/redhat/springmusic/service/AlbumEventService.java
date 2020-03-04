package com.redhat.springmusic.service;

import java.util.Optional;

import com.redhat.springmusic.domain.jpa.AlbumEvent;

public interface AlbumEventService {
	Iterable<AlbumEvent> getAllEventsOrderedByTimestampDescending();
	AlbumEvent persistEvent(AlbumEvent event);
	Optional<AlbumEvent> getById(long eventId);
	Iterable<AlbumEvent> getAllEventsForAlbumIdOrderedByTimestampDescending(String albumId);
}

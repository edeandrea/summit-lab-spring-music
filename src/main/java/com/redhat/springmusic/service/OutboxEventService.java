package com.redhat.springmusic.service;

import java.util.Optional;

import com.redhat.springmusic.domain.event.AlbumEvent;
import com.redhat.springmusic.domain.jpa.OutboxEvent;

public interface OutboxEventService {
	Iterable<OutboxEvent> getAllEventsOrderedByTimestampDescending();
	OutboxEvent persistEvent(AlbumEvent event);
	Optional<OutboxEvent> getById(long eventId);
	Iterable<OutboxEvent> getAllEventsForAlbumIdOrderedByTimestampDescending(String albumId);
}

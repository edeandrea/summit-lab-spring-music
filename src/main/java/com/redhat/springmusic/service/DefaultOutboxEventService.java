package com.redhat.springmusic.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.springmusic.domain.event.AlbumEvent;
import com.redhat.springmusic.domain.jpa.OutboxEvent;
import com.redhat.springmusic.repositories.jpa.OutboxEventRepository;

@Service
public class DefaultOutboxEventService implements OutboxEventService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOutboxEventService.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private final OutboxEventRepository outboxEventRepository;

	public DefaultOutboxEventService(OutboxEventRepository outboxEventRepository) {
		this.outboxEventRepository = outboxEventRepository;
	}

	@Override
	public Iterable<OutboxEvent> getAllEventsOrderedByTimestampDescending() {
		LOGGER.info("Getting all album events ordered by timestamp descending");
		return this.outboxEventRepository.findAll(Sort.by("eventTimestamp").descending());
	}

	@Override
	public Optional<OutboxEvent> getById(long eventId) {
		return this.outboxEventRepository.findById(eventId);
	}

	@Override
	public Iterable<OutboxEvent> getAllEventsForAlbumIdOrderedByTimestampDescending(String albumId) {
		return this.outboxEventRepository.findAllByAggregateIdOrderByEventTimestampDesc(albumId);
	}

	@Override
	public void deleteAllEvents() {
		this.outboxEventRepository.deleteAll();
	}

	@Override
	public OutboxEvent persistEvent(AlbumEvent event) {
		return this.outboxEventRepository.save(
			OutboxEvent.builder()
				.aggregateType("Album")
				.aggregateId(event.getAlbumId())
				.eventType(event.getEventType())
				.payload(OBJECT_MAPPER.convertValue(event, JsonNode.class))
				.build()
		);
	}
}

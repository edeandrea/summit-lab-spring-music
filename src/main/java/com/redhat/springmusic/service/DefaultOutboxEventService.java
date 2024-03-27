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
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;

@Service
public class DefaultOutboxEventService implements OutboxEventService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOutboxEventService.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private final OutboxEventRepository outboxEventRepository;

	public DefaultOutboxEventService(OutboxEventRepository outboxEventRepository) {
		this.outboxEventRepository = outboxEventRepository;
	}

	@Override
	@NewSpan(name = "OutboxEventService.getAllEventsOrderedByTimestampDescending")
	public Iterable<OutboxEvent> getAllEventsOrderedByTimestampDescending() {
		LOGGER.info("Getting all album events ordered by timestamp descending");
		return this.outboxEventRepository.findAll(Sort.by("eventTimestamp").descending());
	}

	@Override
	@NewSpan(name = "OutboxEventService.getById")
	public Optional<OutboxEvent> getById(@SpanTag(key = "arg.eventId") long eventId) {
		return this.outboxEventRepository.findById(eventId);
	}

	@Override
	@NewSpan(name = "OutboxEventService.getAllEventsForAlbumIdOrderedByTimestampDescending")
	public Iterable<OutboxEvent> getAllEventsForAlbumIdOrderedByTimestampDescending(@SpanTag(key = "arg.albumId") String albumId) {
		return this.outboxEventRepository.findAllByAggregateIdOrderByEventTimestampDesc(albumId);
	}

	@Override
	@NewSpan(name = "OutboxEventService.deleteAllEvents")
	public void deleteAllEvents() {
		this.outboxEventRepository.deleteAll();
	}

	@Override
	@NewSpan(name = "OutboxEventService.persistEvent")
	public OutboxEvent persistEvent(@SpanTag(key = "arg.event") AlbumEvent event) {
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

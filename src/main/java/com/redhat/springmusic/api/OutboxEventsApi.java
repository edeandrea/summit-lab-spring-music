package com.redhat.springmusic.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redhat.springmusic.domain.jpa.OutboxEvent;
import com.redhat.springmusic.service.OutboxEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/outbox")
@Tag(name = "Outbox Events", description = "The outbox events API")
public class OutboxEventsApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventsApi.class);
	private final OutboxEventService outboxEventService;

	public OutboxEventsApi(OutboxEventService outboxEventService) {
		this.outboxEventService = outboxEventService;
	}

	@Operation(summary = "Get all outbox events", tags = { "Outbox events" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@ApiResponse(responseCode = "500", description = "Something bad happened")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<OutboxEvent> getAllEvents() {
		LOGGER.info("Getting all album events");
		return this.outboxEventService.getAllEventsOrderedByTimestampDescending();
	}

	@Operation(summary = "Get an event by it's id", tags = { "Outbox event" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@ApiResponse(responseCode = "404", description = "No event for given id found")
	@ApiResponse(responseCode = "500", description = "Something bad happened")
	@GetMapping(path = "/event/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OutboxEvent> getEventById(@Parameter(description = "The event id", required = true) @PathVariable long eventId) {
		return ResponseEntity.of(this.outboxEventService.getById(eventId));
	}

	@Operation(summary = "Get all events for an id, ordered by timestamp, descending", tags = { "Outbox events" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@ApiResponse(responseCode = "500", description = "Something bad happened")
	@GetMapping(path = "/album/{albumId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<OutboxEvent> getEventsForAlbumId(@Parameter(description = "The album id", required = true) @PathVariable String albumId) {
		return this.outboxEventService.getAllEventsForAlbumIdOrderedByTimestampDescending(albumId);
	}
}

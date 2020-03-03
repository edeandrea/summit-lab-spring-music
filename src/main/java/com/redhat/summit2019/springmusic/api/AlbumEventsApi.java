package com.redhat.summit2019.springmusic.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redhat.summit2019.springmusic.domain.jpa.AlbumEvent;
import com.redhat.summit2019.springmusic.service.AlbumEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/album_events")
@Tag(name = "Album Events", description = "The album events API")
public class AlbumEventsApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumEventsApi.class);
	private final AlbumEventService albumEventService;

	public AlbumEventsApi(AlbumEventService albumEventService) {
		this.albumEventService = albumEventService;
	}

	@Operation(summary = "Get all album events", tags = { "Album events" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@ApiResponse(responseCode = "500", description = "Something bad happened")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<AlbumEvent> getAllEvents() {
		LOGGER.info("Getting all album events");
		return this.albumEventService.getAllEventsOrderedByTimestampDescending();
	}

	@Operation(summary = "Get an event by it's id", tags = { "Album event" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@ApiResponse(responseCode = "404", description = "No event for given id found")
	@ApiResponse(responseCode = "500", description = "Something bad happened")
	@GetMapping(path = "/event/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AlbumEvent> getEventById(@Parameter(description = "The event id", required = true) @PathVariable long eventId) {
		return ResponseEntity.of(this.albumEventService.getById(eventId));
	}

	@Operation(summary = "Get all events for an album, ordered by timestamp, descending", tags = { "Album events" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@ApiResponse(responseCode = "500", description = "Something bad happened")
	@GetMapping(path = "/album/{albumId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<AlbumEvent> getEventsForAlbumId(@Parameter(description = "The album id", required = true) @PathVariable String albumId) {
		return this.albumEventService.getAllEventsForAlbumIdOrderedByTimestampDescending(albumId);
	}
}

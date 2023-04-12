package com.redhat.springmusic.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.springmusic.domain.AlbumEventType;
import com.redhat.springmusic.domain.jpa.Album;
import com.redhat.springmusic.domain.jpa.OutboxEvent;
import com.redhat.springmusic.service.OutboxEventService;

@WebMvcTest(value = OutboxEventsApi.class, properties = { "spring.cloud.kubernetes.config.enabled=false" })
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class OutboxEventsApiTests {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final Album ALBUM = Album.builder()
			.artist("Eric Deandrea")
			.genre("Rock")
			.id("34291eeb-7191-4e12-b60a-5e2a514d993b")
			.releaseYear("2020")
			.title("Eric Rocks")
			.trackCount(10)
			.build();

	private static final OutboxEvent ALBUM_CREATED_EVENT = OutboxEvent.builder()
		.eventId(1L)
		.eventTimestamp(Instant.now())
		.eventType(AlbumEventType.ALBUM_CREATED)
		.aggregateType("Album")
		.aggregateId(ALBUM.getId())
		.payload(OBJECT_MAPPER.convertValue(ALBUM, JsonNode.class))
		.build();

	@MockBean
	private OutboxEventService outboxEventService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getAllEventsWorks() throws Exception {
		given(this.outboxEventService.getAllEventsOrderedByTimestampDescending())
			.willReturn(Collections.singletonList(ALBUM_CREATED_EVENT));

		this.mockMvc.perform(get("/outbox").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(handler().handlerType(OutboxEventsApi.class))
			.andExpect(handler().method(ReflectionUtils.findMethod(OutboxEventsApi.class, "getAllEvents")))
			.andExpect(jsonPath("$").exists())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0]").exists())
			.andExpect(jsonPath("$[0].aggregateId").value(ALBUM_CREATED_EVENT.getAggregateId()))
			.andExpect(jsonPath("$[0].eventId").value(ALBUM_CREATED_EVENT.getEventId()))
			.andExpect(jsonPath("$[0].eventType").value(ALBUM_CREATED_EVENT.getEventType().name()))
			.andExpect(jsonPath("$[0].aggregateType").value(ALBUM_CREATED_EVENT.getAggregateType()))
			.andExpect(jsonPath("$[0].payload").exists())
			.andExpect(jsonPath("$[0].payload.id").value(ALBUM.getId()))
			.andExpect(jsonPath("$[0].payload.title").value(ALBUM.getTitle()))
			.andExpect(jsonPath("$[0].payload.artist").value(ALBUM.getArtist()))
			.andExpect(jsonPath("$[0].payload.releaseYear").value(ALBUM.getReleaseYear()))
			.andExpect(jsonPath("$[0].payload.genre").value(ALBUM.getGenre()))
			.andExpect(jsonPath("$[0].payload.trackCount").value(ALBUM.getTrackCount()));
	}
}

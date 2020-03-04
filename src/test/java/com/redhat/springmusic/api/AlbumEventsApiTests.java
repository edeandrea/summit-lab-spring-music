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

import com.redhat.springmusic.domain.AlbumEventType;
import com.redhat.springmusic.domain.jpa.Album;
import com.redhat.springmusic.domain.jpa.AlbumEvent;
import com.redhat.springmusic.service.AlbumEventService;

@WebMvcTest(AlbumEventsApi.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class AlbumEventsApiTests {
	private static final Album ALBUM = Album.builder()
			.artist("Eric Deandrea")
			.genre("Rock")
			.id("34291eeb-7191-4e12-b60a-5e2a514d993b")
			.releaseYear("2020")
			.title("Eric Rocks")
			.trackCount(10)
			.build();

	private static final AlbumEvent ALBUM_EVENT = AlbumEvent.album(ALBUM)
		.eventId(1L)
		.timestamp(Instant.now())
		.eventType(AlbumEventType.ALBUM_CREATED)
		.build();

	@MockBean
	private AlbumEventService albumEventService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getAllEventsWorks() throws Exception {
		given(this.albumEventService.getAllEventsOrderedByTimestampDescending())
			.willReturn(Collections.singletonList(ALBUM_EVENT));

		this.mockMvc.perform(get("/album_events").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(handler().handlerType(AlbumEventsApi.class))
			.andExpect(handler().method(ReflectionUtils.findMethod(AlbumEventsApi.class, "getAllEvents")))
			.andExpect(jsonPath("$").exists())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0]").exists())
			.andExpect(jsonPath("$[0].albumId").value(ALBUM_EVENT.getAlbumId()))
			.andExpect(jsonPath("$[0].eventId").value(ALBUM_EVENT.getEventId()))
			.andExpect(jsonPath("$[0].eventType").value(ALBUM_EVENT.getEventType().name()))
			.andExpect(jsonPath("$[0].title").value(ALBUM.getTitle()))
			.andExpect(jsonPath("$[0].artist").value(ALBUM.getArtist()))
			.andExpect(jsonPath("$[0].releaseYear").value(ALBUM.getReleaseYear()))
			.andExpect(jsonPath("$[0].genre").value(ALBUM.getGenre()))
			.andExpect(jsonPath("$[0].trackCount").value(ALBUM.getTrackCount()));
	}
}

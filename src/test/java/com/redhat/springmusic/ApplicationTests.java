package com.redhat.springmusic;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Year;
import java.util.List;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.redhat.springmusic.domain.AlbumEventType;
import com.redhat.springmusic.domain.jpa.Album;
import com.redhat.springmusic.domain.jpa.OutboxEvent;

@SpringBootTest(
	webEnvironment = WebEnvironment.RANDOM_PORT,
	properties = { "spring.cloud.kubernetes.config.enabled=false" }
)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationTests {
	private static final int NUM_ALBUMS = 29;
	private static final int DEFAULT_ORDER = 0;
	private static final Album ALBUM = Album.builder()
																			.artist("Jim Smith")
																			.genre("Rock")
																			.releaseYear(String.valueOf(Year.now().getValue()))
																			.title("Spring Music")
																			.trackCount(10)
																			.build();

	@Autowired
	private WebTestClient webTestClient;

	@ParameterizedTest
	@ValueSource(strings = { "/", "/index.html" })
	@Order(DEFAULT_ORDER)
	public void appLoads(String uri) {
		this.webTestClient.get()
			.uri(uri)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML);
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void getAllAlbums() {
		assertNumAlbums(NUM_ALBUMS);
	}

	@Test
	@Order(DEFAULT_ORDER)
	public void noOutboxEvents() {
		this.webTestClient.get()
			.uri("/outbox")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(OutboxEvent.class)
			.hasSize(NUM_ALBUMS)
			.consumeWith(events ->
				assertThat(events.getResponseBody())
					.isNotNull()
					.allMatch(event -> event.getEventType() == AlbumEventType.ALBUM_CREATED)
			);

		this.webTestClient.delete()
			.uri("/outbox")
			.exchange()
			.expectStatus().isNoContent();

		this.webTestClient.get()
			.uri("/outbox")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(OutboxEvent.class)
			.hasSize(0);
	}

	@Test
	@Order(DEFAULT_ORDER + 1)
	public void addAlbum() {
		var addedAlbum = this.webTestClient.post()
			.uri("/albums")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(ALBUM)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
			.expectBody(Album.class)
			.returnResult().getResponseBody();

		assertThat(addedAlbum)
			.isNotNull()
			.usingRecursiveComparison(
				RecursiveComparisonConfiguration.builder()
					.withIgnoredFields("id", "albumId")
					.build()
			)
			.isEqualTo(ALBUM);

		ALBUM.setId(addedAlbum.getId());
		ALBUM.setAlbumId(addedAlbum.getAlbumId());

		assertNumAlbums(NUM_ALBUMS + 1);

		checkAlbumById(addedAlbum);

		verifyOutboxEvents(ALBUM.getId(), 1);
	}

	@Test
	@Order(DEFAULT_ORDER + 2)
	public void updateAlbum() {
		var albumToUpdate = ALBUM.toBuilder()
			.genre("Rock and Roll")
			.build();

		this.webTestClient.put()
			.uri("/albums/{id}", albumToUpdate.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(albumToUpdate)
			.exchange()
			.expectStatus().isNoContent();

		checkAlbumById(albumToUpdate);

		ALBUM.setGenre(albumToUpdate.getGenre());

		verifyOutboxEvents(albumToUpdate.getId(), 2);
	}

	@Test
	@Order(DEFAULT_ORDER + 3)
	public void getAlbumById() {
		checkAlbumById(ALBUM);
	}

	@Test
	@Order(DEFAULT_ORDER + 4)
	public void deleteAlbum() {
		this.webTestClient.delete()
			.uri("/albums/{id}", ALBUM.getId())
			.exchange()
			.expectStatus().isNoContent();

		assertNumAlbums(NUM_ALBUMS);

		this.webTestClient.get()
			.uri("/albums/{id}", ALBUM.getId())
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody().isEmpty();

		verifyOutboxEvents(ALBUM.getId(), 3);
	}

	private void verifyOutboxEvents(String albumId, int expectedNumEvents) {
		var outboxEvents = getOutboxEvents(albumId);

		assertThat(outboxEvents)
			.isNotNull()
			.hasSize(expectedNumEvents);

		// Remember, events are ordered by timestamp descending
		// So the last element in the list is the oldest
		var createdEvent = outboxEvents.get(outboxEvents.size() - 1);
		assertThat(createdEvent)
			.isNotNull()
			.extracting(
				OutboxEvent::getAggregateType,
				OutboxEvent::getAggregateId,
				OutboxEvent::getEventType
			)
			.containsExactly(
				"Album",
				albumId,
				AlbumEventType.ALBUM_CREATED
			);

		assertThat(getEvent(createdEvent.getEventId()))
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(createdEvent);

		if (expectedNumEvents >= 2) {
			var updatedEvent = outboxEvents.get(outboxEvents.size() - 2);
			assertThat(updatedEvent)
				.isNotNull()
				.extracting(
					OutboxEvent::getAggregateType,
					OutboxEvent::getAggregateId,
					OutboxEvent::getEventType
				)
				.containsExactly(
					"Album",
					albumId,
					AlbumEventType.ALBUM_UPDATED
				);

			assertThat(getEvent(updatedEvent.getEventId()))
				.isNotNull()
				.usingRecursiveComparison()
				.isEqualTo(updatedEvent);
		}

		if (expectedNumEvents == 3) {
			var deletedEvent = outboxEvents.get(outboxEvents.size() - 3);
			assertThat(deletedEvent)
				.isNotNull()
				.extracting(
					OutboxEvent::getAggregateType,
					OutboxEvent::getAggregateId,
					OutboxEvent::getEventType
				)
				.containsExactly(
					"Album",
					albumId,
					AlbumEventType.ALBUM_DELETED
				);

			assertThat(getEvent(deletedEvent.getEventId()))
				.isNotNull()
				.usingRecursiveComparison()
				.isEqualTo(deletedEvent);
		}

		assertThat(outboxEvents)
			.allSatisfy(event ->
				assertThat(event)
					.isNotNull()
					.extracting(
						OutboxEvent::getEventId,
						OutboxEvent::getEventTimestamp,
						OutboxEvent::getPayload
					)
					.doesNotContainNull()
			);
	}

	private OutboxEvent getEvent(long eventId) {
		return this.webTestClient.get()
			.uri("/outbox/event/{eventId}", eventId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
			.expectBody(OutboxEvent.class)
			.returnResult().getResponseBody();
	}

	private List<OutboxEvent> getOutboxEvents(String albumId) {
		return this.webTestClient.get()
			.uri("/outbox/album/{albumId}", albumId)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
			.expectBodyList(OutboxEvent.class)
			.returnResult().getResponseBody();
	}

	private void checkAlbumById(Album expectedAlbum) {
		assertThat(getAlbumById(ALBUM.getId()))
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(expectedAlbum);
	}

	private Album getAlbumById(String id) {
		return this.webTestClient.get()
			.uri("/albums/{id}", id)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
			.expectBody(Album.class)
			.returnResult().getResponseBody();
	}

	private void assertNumAlbums(int expectedSize) {
		this.webTestClient.get()
			.uri("/albums")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Album.class)
			.hasSize(expectedSize);
	}
}

package com.redhat.springmusic.domain.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.springmusic.domain.AlbumEventType;
import com.redhat.springmusic.domain.event.AlbumCreatedEvent;
import com.redhat.springmusic.domain.event.AlbumEvent;
import com.redhat.springmusic.listener.AlbumEventListener;
import com.redhat.springmusic.repositories.jpa.OutboxEventRepository;
import com.redhat.springmusic.service.AlbumService;

@DataJpaTest
//@SpringBootTest
class OutboxEventTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventTests.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
	private OutboxEventRepository outboxEventRepository;

	@Autowired
	private AlbumService albumService;

	@Autowired
	private JdbcOperations jdbcOperations;

	@Test
	public void eventSavesCorrectly() {
		Album album = Album.builder()
			.artist("Eric Deandrea")
			.genre("Rock")
			.releaseYear("2020")
			.title("Eric Rocks")
			.trackCount(10)
			.build();

		Album createdAlbum = this.albumService.createAlbum(album);
		assertThat(createdAlbum)
			.isNotNull();

		Map<String, Object> events = this.jdbcOperations.queryForMap("SELECT * FROM outbox_events");
		LOGGER.info("Events = {}", events);
		assertThat(events)
			.isNotEmpty()
			.containsKey("PAYLOAD")
			.containsEntry("EVENT_ID", 1L)
			.containsEntry("AGGREGATE_ID", createdAlbum.getId())
			.containsEntry("AGGREGATE_TYPE", "Album")
			.containsEntry("EVENT_TYPE", AlbumEventType.ALBUM_CREATED.name());

		Optional<OutboxEvent> outboxEvent = this.outboxEventRepository.findById(1L);
		LOGGER.info("Got OutboxEvent = {}", outboxEvent);

		assertThat(outboxEvent)
			.isPresent()
			.get()
			.extracting(
				OutboxEvent::getAggregateId,
				OutboxEvent::getAggregateType,
				OutboxEvent::getEventId,
				OutboxEvent::getEventType
			)
			.containsExactly(
				createdAlbum.getId(),
				"Album",
				1L,
				AlbumEventType.ALBUM_CREATED
			);

		AlbumEvent albumEvent = OBJECT_MAPPER.convertValue(outboxEvent.get().getPayload(), AlbumEvent.class);
		assertThat(albumEvent)
			.isNotNull()
			.isExactlyInstanceOf(AlbumCreatedEvent.class);

		assertThat((AlbumCreatedEvent) albumEvent)
			.extracting(
				AlbumCreatedEvent::getAlbumId,
				AlbumCreatedEvent::getEventType
			)
			.containsExactly(
				createdAlbum.getId(),
				AlbumEventType.ALBUM_CREATED
			);

		assertThat(((AlbumCreatedEvent) albumEvent).getAlbum())
			.isEqualToComparingFieldByField(createdAlbum);
	}

	@TestConfiguration
	@EnableJpaAuditing
	@ComponentScan(basePackageClasses = { AlbumEventListener.class, AlbumService.class })
	static class AlbumEventTestsConfig {
		@Bean
		public JdbcOperations jdbcOperations(DataSource dataSource) {
			return new JdbcTemplate(dataSource);
		}
	}
}

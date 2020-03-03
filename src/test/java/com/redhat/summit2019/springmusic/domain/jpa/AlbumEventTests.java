package com.redhat.summit2019.springmusic.domain.jpa;

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

import com.redhat.summit2019.springmusic.domain.AlbumEventType;
import com.redhat.summit2019.springmusic.listener.AlbumEventListener;
import com.redhat.summit2019.springmusic.repositories.jpa.AlbumEventRepository;
import com.redhat.summit2019.springmusic.service.AlbumService;

@DataJpaTest
//@SpringBootTest
class AlbumEventTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumEventTests.class);

	@Autowired
	private AlbumEventRepository albumEventRepository;

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

		Map<String, Object> events = this.jdbcOperations.queryForMap("SELECT * FROM album_events");
		LOGGER.info("Events = {}", events);
		assertThat(events)
			.isNotEmpty()
			.containsEntry("EVENT_ID", 1L)
			.containsEntry("ALBUM_EVENT_TYPE", AlbumEventType.ALBUM_CREATED.name())
			.containsEntry("ALBUM_ID", createdAlbum.getId())
			.containsEntry("ARTIST", album.getArtist())
			.containsEntry("GENRE", album.getGenre())
			.containsEntry("RELEASE_YEAR", album.getReleaseYear())
			.containsEntry("TITLE", album.getTitle())
			.containsEntry("TRACK_COUNT", album.getTrackCount());

		Optional<AlbumEvent> albumCreatedEvent = this.albumEventRepository.findById(1L);

		assertThat(albumCreatedEvent)
			.isPresent();

		LOGGER.info("Got event = {}", albumCreatedEvent);
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

package com.redhat.summit2019.springmusic.domain.jpa;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.redhat.summit2019.springmusic.domain.AlbumEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "album_events")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class AlbumEvent extends AlbumBase {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "The album event id")
	private Long eventId;

	@Schema(description = "The album id")
	private String albumId;

	@CreatedDate
	@Schema(description = "The event timestamp")
	private Instant timestamp;

	@Schema(description = "The album event type")
	@Enumerated(EnumType.STRING)
	private AlbumEventType eventType;

	public static AlbumEventBuilder<?, ?> album(Album album) {
		return AlbumEvent.builder()
			.albumId(album.getId())
			.artist(album.getArtist())
			.genre(album.getGenre())
			.releaseYear(album.getReleaseYear())
			.title(album.getTitle())
			.trackCount(album.getTrackCount());
	}
}

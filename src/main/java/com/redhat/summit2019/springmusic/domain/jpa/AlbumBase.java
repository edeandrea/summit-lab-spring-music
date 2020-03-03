package com.redhat.summit2019.springmusic.domain.jpa;

import javax.persistence.MappedSuperclass;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@ToString
@SuperBuilder(toBuilder = true)
@MappedSuperclass
public abstract class AlbumBase {
	@Schema(description = "The album title")
	private String title;

	@Schema(description = "The album artist")
	private String artist;

	@Schema(description = "The album release year")
	private String releaseYear;

	@Schema(description = "The album genre")
	private String genre;

	@Schema(description = "The number of tracks on the album")
	private int trackCount;

	@Schema(description = "The album id")
	private String albumId;
}

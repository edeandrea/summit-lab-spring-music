package com.redhat.springmusic.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.redhat.springmusic.domain.AlbumEventType;
import com.redhat.springmusic.domain.jpa.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@JsonTypeName("ALBUM_CREATED")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumCreatedEvent implements AlbumEvent {
	@NonNull
	private Album album;

	@Override
	public String getAlbumId() {
		return this.album.getId();
	}

	@Override
	public AlbumEventType getEventType() {
		return AlbumEventType.ALBUM_CREATED;
	}
}

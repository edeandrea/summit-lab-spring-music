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
@JsonTypeName("ALBUM_DELETED")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumDeletedEvent implements AlbumEvent {
	@NonNull
	private Album deletedAlbum;

	@Override
	public String getAlbumId() {
		return this.deletedAlbum.getId();
	}

	@Override
	public AlbumEventType getEventType() {
		return AlbumEventType.ALBUM_DELETED;
	}
}

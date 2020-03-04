package com.redhat.springmusic.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.redhat.springmusic.domain.AlbumEventType;

@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "eventType")
@JsonSubTypes({
	@Type(AlbumCreatedEvent.class),
	@Type(AlbumDeletedEvent.class),
	@Type(AlbumUpdatedEvent.class)
})
public interface AlbumEvent {
	@JsonIgnore
	String getAlbumId();
	AlbumEventType getEventType();
}

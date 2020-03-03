package com.redhat.summit2019.springmusic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.redhat.summit2019.springmusic.domain.jpa.AlbumEvent;
import com.redhat.summit2019.springmusic.service.AlbumEventService;

@Component
public class AlbumEventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumEventListener.class);
	private final AlbumEventService albumEventService;

	protected AlbumEventListener(AlbumEventService albumEventService) {
		this.albumEventService = albumEventService;
	}

	@EventListener
	public void handleAlbumEvent(AlbumEvent albumEvent) {
		Assert.notNull(albumEvent, "albumEvent can not be null");
		LOGGER.info("Handling AlbumEvent {}", albumEvent);

		this.albumEventService.persistEvent(albumEvent);
	}
}

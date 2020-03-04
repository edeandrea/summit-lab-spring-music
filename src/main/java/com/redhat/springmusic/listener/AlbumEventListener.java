package com.redhat.springmusic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.redhat.springmusic.domain.event.AlbumEvent;
import com.redhat.springmusic.service.OutboxEventService;

@Component
public class AlbumEventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumEventListener.class);
	private final OutboxEventService outboxEventService;

	protected AlbumEventListener(OutboxEventService outboxEventService) {
		this.outboxEventService = outboxEventService;
	}

	@EventListener
	public void handleAlbumEvent(AlbumEvent albumEvent) {
		Assert.notNull(albumEvent, "albumEvent can not be null");
		LOGGER.info("Handling AlbumEvent {}", albumEvent);

		this.outboxEventService.persistEvent(albumEvent);
	}
}

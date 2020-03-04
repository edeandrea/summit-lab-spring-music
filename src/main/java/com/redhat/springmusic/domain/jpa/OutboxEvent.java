package com.redhat.springmusic.domain.jpa;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Convert;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.springmusic.domain.AlbumEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OutboxEvent {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "The album event id")
	private Long eventId;

	@Schema(description = "The type of aggregate root to which a given event is related")
	private String aggregateType;

	@Schema(description = "The id of the aggregate root that is affected by the event")
	private String aggregateId;

	@Schema(description = "The type of event that occurred")
	@Enumerated(EnumType.STRING)
	private AlbumEventType eventType;

	@Schema(description = "The timestamp the event occurred")
	@CreatedDate
	private Instant eventTimestamp;

	@Schema(description = "The payload with the actual event contents")
	@Column(columnDefinition = "json")
	@Convert(converter = JsonNodeConverter.class)
	private JsonNode payload;
}

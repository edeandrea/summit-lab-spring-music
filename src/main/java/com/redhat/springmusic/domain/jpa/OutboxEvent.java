package com.redhat.springmusic.domain.jpa;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
	@Column(nullable = false)
	private String aggregateType;

	@Schema(description = "The id of the aggregate root that is affected by the event")
	@Column(nullable = false)
	private String aggregateId;

	@Schema(description = "The type of event that occurred")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AlbumEventType eventType;

	@Schema(description = "The timestamp the event occurred")
	@CreatedDate
	@Column(nullable = false)
	private Instant eventTimestamp;

	@Schema(description = "The payload with the actual event contents")
	@Column(columnDefinition = "json")
	@Convert(converter = JsonNodeConverter.class)
	private JsonNode payload;
}

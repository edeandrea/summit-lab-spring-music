package com.redhat.springmusic.repositories.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redhat.springmusic.domain.jpa.OutboxEvent;

@Repository
@Profile("!mongodb & !redis")
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long>, EntityOperationsRepository<OutboxEvent> {
	Iterable<OutboxEvent> findAllByAggregateIdOrderByEventTimestampDesc(String aggregateId);
}

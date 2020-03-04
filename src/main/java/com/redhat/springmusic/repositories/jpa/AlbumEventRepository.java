package com.redhat.springmusic.repositories.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redhat.springmusic.domain.jpa.AlbumEvent;

@Repository
@Profile("!mongodb & !redis")
public interface AlbumEventRepository extends JpaRepository<AlbumEvent, Long>, EntityOperationsRepository<AlbumEvent> {
	Iterable<AlbumEvent> findAllByAlbumIdOrderByTimestampDesc(String albumId);
}

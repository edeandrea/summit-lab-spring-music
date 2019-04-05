package com.redhat.summit2019.springmusic.repositories.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redhat.summit2019.springmusic.domain.Album;

@Repository
@Profile("!mongodb & !redis")
public interface JpaAlbumRepository extends JpaRepository<Album, String> {
}

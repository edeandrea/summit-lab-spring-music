package com.redhat.springmusic.repositories.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redhat.springmusic.domain.jpa.Album;

@Repository
@Profile("!mongodb & !redis")
public interface AlbumRepository extends JpaRepository<Album, String>, EntityOperationsRepository<Album> {

}

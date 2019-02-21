package io.k8s.samples.music.repositories.jpa;

import io.k8s.samples.music.domain.Album;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!mongodb & !redis")
public interface JpaAlbumRepository extends JpaRepository<Album, String> {
}

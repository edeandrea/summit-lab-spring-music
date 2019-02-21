package io.k8s.samples.music.repositories.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.k8s.samples.music.domain.Album;

@Repository
@Profile("mongodb")
public interface MongoAlbumRepository extends MongoRepository<Album, String> {
}

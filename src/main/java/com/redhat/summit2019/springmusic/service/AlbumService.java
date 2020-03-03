package com.redhat.summit2019.springmusic.service;

import java.util.Optional;

import com.redhat.summit2019.springmusic.domain.jpa.Album;

public interface AlbumService {
	Iterable<Album> getAllAlbums();
	Album createAlbum(Album album);
	void updateAlbum(Album album);
	Optional<Album> getAlbum(String albumId);
	void deleteAlbum(String albumId);
	boolean isEmpty();
}

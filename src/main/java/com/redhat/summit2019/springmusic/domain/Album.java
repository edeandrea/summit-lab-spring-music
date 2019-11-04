package com.redhat.summit2019.springmusic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
public class Album {
    @Id
    @Column(length = 40)
    @GeneratedValue(generator = "randomId")
    @GenericGenerator(name = "randomId", strategy = "com.redhat.summit2019.springmusic.domain.RandomIdGenerator")
    @Schema(description = "The album id", hidden = true)
    private String id;

    @Schema(description = "The album title")
    private String title;

    @Schema(description = "The album artist")
    private String artist;

    @Schema(description = "The album release year")
    private String releaseYear;

    @Schema(description = "The album genre")
    private String genre;

    @Schema(description = "The number of tracks on the album")
    private int trackCount;

    @Schema(description = "The album id")
    private String albumId;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleaseYear() {
        return this.releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getTrackCount() {
        return this.trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public String getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}

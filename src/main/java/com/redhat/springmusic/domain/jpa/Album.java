package com.redhat.springmusic.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.redhat.springmusic.domain.RandomIdGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder(toBuilder = true)
public class Album {
    @Id
    @EqualsAndHashCode.Include
    @Column(length = 40)
    @GeneratedValue(generator = "randomId")
		@GenericGenerator(name = "randomId", type = RandomIdGenerator.class)
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
}

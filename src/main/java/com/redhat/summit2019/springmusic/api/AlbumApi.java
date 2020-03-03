package com.redhat.summit2019.springmusic.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.redhat.summit2019.springmusic.domain.jpa.Album;
import com.redhat.summit2019.springmusic.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/albums")
@Tag(name = "Albums", description = "The albums API")
public class AlbumApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumApi.class);
	private final AlbumService albumService;

	public AlbumApi(AlbumService albumService) {
		this.albumService = albumService;
	}

	@Operation(summary = "Get all albums", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<Album> albums() {
		LOGGER.info("Getting all albums");
		return this.albumService.getAllAlbums();
	}

	@Operation(summary = "Adds an album", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Album add(@Parameter(description = "The album to add", required = true) @RequestBody @Valid Album album) {
		LOGGER.info("Adding album {}", album.getId());
		return this.albumService.createAlbum(album);
	}

	@Operation(summary = "Updates an album", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Album update(@Parameter(description = "The album to update", required = true) @RequestBody @Valid Album album) {
		LOGGER.info("Updating album {}", album.getId());
		this.albumService.updateAlbum(album);

		return album;
	}

	@Operation(summary = "Get an album", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Album getById(@Parameter(description = "The album id", required = true) @PathVariable String id) {
		LOGGER.info("Getting album {}", id);
		return this.albumService.getAlbum(id).orElse(null);
	}

	@Operation(summary = "Delete an album", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@Parameter(description = "The album id", required = true) @PathVariable String id) {
		LOGGER.info("Deleting album {}", id);
		this.albumService.deleteAlbum(id);
	}
}

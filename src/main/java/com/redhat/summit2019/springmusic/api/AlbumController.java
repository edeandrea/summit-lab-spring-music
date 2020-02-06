package com.redhat.summit2019.springmusic.api;

import javax.validation.Valid;

import com.redhat.summit2019.springmusic.domain.Album;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/albums")
@Tag(name = "Albums", description = "The albums API")
public class AlbumController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumController.class);
	private final CrudRepository<Album, String> repository;

	public AlbumController(CrudRepository<Album, String> repository) {
		this.repository = repository;
	}

	@Operation(summary = "Get all albums", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<Album> albums() {
		LOGGER.info("Getting all albums");
        return this.repository.findAll();
        
	}

	@Operation(summary = "Adds an album", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Album add(@Parameter(description = "The album to add") @RequestBody @Valid Album album) {
		LOGGER.info("Adding album {}", album.getId());
		return this.repository.save(album);
	}

	@Operation(summary = "Updates an album", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Album update(@Parameter(description = "The album to update", required = true) @RequestBody @Valid Album album) {
		LOGGER.info("Updating album {}", album.getId());
		return this.repository.save(album);
	}

	@Operation(summary = "Get an album", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Album getById(@Parameter(description = "The album id") @PathVariable String id) {
		LOGGER.info("Getting album {}", id);
		return this.repository.findById(id).orElse(null);
	}

	@Operation(summary = "Delete an album", tags = { "Albums" })
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Success!"),
		@ApiResponse(responseCode = "500", description = "Something bad happened")
	})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@Parameter(description = "The album id") @PathVariable String id) {
		LOGGER.info("Deleting album {}", id);
		if (this.repository.existsById(id)) {
			this.repository.deleteById(id);
		}
	}
}

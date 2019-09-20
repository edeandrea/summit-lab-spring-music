package com.redhat.summit2019.springmusic.api;

import javax.validation.Valid;

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

import com.redhat.summit2019.springmusic.domain.Album;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/albums")
@Api("Albums")
public class AlbumController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumController.class);
	private final CrudRepository<Album, String> repository;

	public AlbumController(CrudRepository<Album, String> repository) {
		this.repository = repository;
	}

	@ApiOperation(value = "Get all albums", notes = "Get all albums", nickname = "get-all-albums")
	@ApiResponses({
									@ApiResponse(code = 200, message = "Success!")
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<Album> albums() {
		LOGGER.info("Getting all albums");
		return this.repository.findAll();
	}

	@ApiOperation(value = "Adds an album", notes = "Adds an album", nickname = "add-album")
	@ApiResponses({
									@ApiResponse(code = 200, message = "Success!")
	})
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Album add(@ApiParam(value = "The album to add", required = true) @RequestBody @Valid Album album) {
		LOGGER.info("Adding album {}", album.getId());
		return this.repository.save(album);
	}

	@ApiOperation(value = "Updates an album", notes = "Updates an album", nickname = "update-album")
	@ApiResponses({
									@ApiResponse(code = 200, message = "Success!")
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Album update(@ApiParam(value = "The album to update", required = true) @RequestBody @Valid Album album) {
		LOGGER.info("Updating album {}", album.getId());
		return this.repository.save(album);
	}

	@ApiOperation(value = "Get an album", notes = "Get an album", nickname = "get-album")
	@ApiResponses({
									@ApiResponse(code = 200, message = "Success!")
	})
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Album getById(@ApiParam(value = "The album id", required = true) @PathVariable String id) {
		LOGGER.info("Getting album {}", id);
		return this.repository.findById(id).orElse(null);
	}

	@ApiOperation(value = "Delete an album", notes = "Delete an album", nickname = "delete-album")
	@ApiResponses({
									@ApiResponse(code = 204, message = "Success!")
	})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable String id) {
		LOGGER.info("Deleting album {}", id);
		this.repository.deleteById(id);
	}
}

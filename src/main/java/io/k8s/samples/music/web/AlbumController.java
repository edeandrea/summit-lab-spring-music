package io.k8s.samples.music.web;

import javax.validation.Valid;

import io.k8s.samples.music.domain.Album;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/albums")
public class AlbumController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumController.class);
	private final CrudRepository<Album, String> repository;

	public AlbumController(CrudRepository<Album, String> repository) {
		this.repository = repository;
	}

	@GetMapping
	public Iterable<Album> albums() {
		return this.repository.findAll();
	}

	@PutMapping
	public Album add(@RequestBody @Valid Album album) {
		LOGGER.info("Adding album {}", album.getId());
		return this.repository.save(album);
	}

	@PostMapping
	public Album update(@RequestBody @Valid Album album) {
		LOGGER.info("Updating album {}", album.getId());
		return this.repository.save(album);
	}

	@GetMapping("/{id}")
	public Album getById(@PathVariable String id) {
		LOGGER.info("Getting album {}", id);
		return this.repository.findById(id).orElse(null);
	}

	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable String id) {
		LOGGER.info("Deleting album {}", id);
		this.repository.deleteById(id);
	}
}

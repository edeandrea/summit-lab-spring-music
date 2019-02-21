package io.k8s.samples.music.repositories.redis;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;

import io.k8s.samples.music.domain.Album;
import io.k8s.samples.music.domain.RandomIdGenerator;

public class RedisAlbumRepository implements CrudRepository<Album, String> {
	public static final String ALBUMS_KEY = "albums";

	private final HashOperations<String, String, Album> hashOps;
	private final RandomIdGenerator randomIdGenerator = new RandomIdGenerator();

	public RedisAlbumRepository(RedisTemplate<String, Album> redisTemplate) {
		this.hashOps = redisTemplate.opsForHash();
	}

	@Override
	public <S extends Album> S save(S album) {
		if (album.getId() == null) {
			album.setId(this.randomIdGenerator.generateId());
		}

		this.hashOps.put(ALBUMS_KEY, album.getId(), album);

		return album;
	}

	@Override
	public <S extends Album> Iterable<S> saveAll(Iterable<S> albums) {
		return Optional.ofNullable(albums)
			.map(a -> StreamSupport.stream(a.spliterator(), false))
			.orElseGet(Stream::empty)
			.map(this::save)
			.collect(Collectors.toList());
	}

	@Override
	public Optional<Album> findById(String id) {
		return Optional.ofNullable(this.hashOps.get(ALBUMS_KEY, id));
	}

	@Override
	public boolean existsById(String id) {
		return this.hashOps.hasKey(ALBUMS_KEY, id);
	}

	@Override
	public Iterable<Album> findAll() {
		return this.hashOps.values(ALBUMS_KEY);
	}

	@Override
	public Iterable<Album> findAllById(Iterable<String> ids) {
		return this.hashOps.multiGet(ALBUMS_KEY, convertIterableToCollection(ids));
	}

	@Override
	public long count() {
		return this.hashOps.keys(ALBUMS_KEY).size();
	}

	@Override
	public void deleteById(String id) {
		this.hashOps.delete(ALBUMS_KEY, id);
	}

	@Override
	public void delete(Album album) {
		this.hashOps.delete(ALBUMS_KEY, album.getId());
	}

	@Override
	public void deleteAll(Iterable<? extends Album> albums) {
		Optional.ofNullable(albums)
			.ifPresent(a -> a.forEach(this::delete));
	}

	@Override
	public void deleteAll() {
		this.hashOps.keys(ALBUMS_KEY)
			.forEach(this::deleteById);
	}

	private <T> Collection<T> convertIterableToCollection(Iterable<T> iterable) {
		return Optional.ofNullable(iterable)
			.map(i -> StreamSupport.stream(i.spliterator(), false))
			.orElseGet(Stream::empty)
			.collect(Collectors.toList());
	}
}

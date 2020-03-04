package com.redhat.springmusic.repositories.jpa;

public interface EntityOperationsRepository<T> {
	T detach(T entity);
}

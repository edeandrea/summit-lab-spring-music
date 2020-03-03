package com.redhat.summit2019.springmusic.repositories.jpa;

public interface EntityOperationsRepository<T> {
	T detach(T entity);
}

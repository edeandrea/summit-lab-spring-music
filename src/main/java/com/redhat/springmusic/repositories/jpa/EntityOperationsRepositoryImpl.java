package com.redhat.springmusic.repositories.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class EntityOperationsRepositoryImpl<T> implements EntityOperationsRepository<T> {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public T detach(T entity) {
		this.entityManager.detach(entity);
		return entity;
	}
}

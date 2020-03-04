package com.redhat.springmusic.repositories.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityOperationsRepositoryImpl<T> implements EntityOperationsRepository<T> {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public T detach(T entity) {
		this.entityManager.detach(entity);
		return entity;
	}
}

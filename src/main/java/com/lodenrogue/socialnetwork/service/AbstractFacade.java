package com.lodenrogue.socialnetwork.service;

import java.util.List;
import java.util.Map;

import com.lodenrogue.socialnetwork.persistance.EntityManager;

public abstract class AbstractFacade<T> {
	private EntityManager<T> entityManager;

	public AbstractFacade(Class<T> entityClass) {
		entityManager = new EntityManager<T>(entityClass);
	}

	public List<T> findAll() {
		return entityManager.getAll();
	}

	public T find(long id) {
		return entityManager.get(id);
	}

	public T create(T t) {
		return entityManager.create(t);
	}

	public void delete(long id) {
		entityManager.delete(id);
	}

	protected T findUnique(String query, Map<String, Object> parameters) {
		return entityManager.getUnique(query, parameters);
	}

	protected List<T> findAllFromQuery(String query, Map<String, Object> parameters) {
		return entityManager.findAllFromQuery(query, parameters);
	}

}

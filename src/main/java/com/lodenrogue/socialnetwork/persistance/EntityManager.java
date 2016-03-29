package com.lodenrogue.socialnetwork.persistance;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class EntityManager<T> {
	private Class<T> entityClass;
	private SessionFactory factory;

	public EntityManager(Class<T> entityClass) {
		factory = Database.getInstance().getSessionFactory();
		this.entityClass = entityClass;
	}

	public T create(T t) {
		Session session = factory.openSession();
		session.beginTransaction();
		session.persist(t);
		session.getTransaction().commit();
		session.close();
		return t;
	}

	public List<T> getAll() {
		Session session = factory.openSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<T> objectList = session.createCriteria(entityClass).list();
		session.getTransaction().commit();
		session.close();
		return objectList;
	}

	public T get(long id) {
		Session session = factory.openSession();
		session.beginTransaction();
		T t = (T) session.get(entityClass, id);
		session.getTransaction().commit();
		session.close();
		return t;
	}

	public void delete(long id) {
		Session session = factory.openSession();
		session.beginTransaction();
		T t = session.get(entityClass, id);
		if (t != null) {
			session.delete(t);
		}
		session.getTransaction().commit();
		session.close();
	}

	public T getUnique(String query, Map<String, Object> parameters) {
		Session session = factory.openSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		T object = (T) buildQuery(session, query, parameters).uniqueResult();
		session.getTransaction().commit();
		session.close();
		return object;
	}

	public List<T> findAllFromQuery(String query, Map<String, Object> parameters) {
		Session session = factory.openSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<T> list = buildQuery(session, query, parameters).list();
		session.getTransaction().commit();
		session.close();
		return list;
	}

	public Session openSession() {
		return factory.openSession();
	}

	private Query buildQuery(Session session, String query, Map<String, Object> parameters) {
		Query targetQuery = session.createQuery(query);
		for (String key : parameters.keySet()) {
			targetQuery.setParameter(key, parameters.get(key));
		}
		return targetQuery;
	}

}

package com.lodenrogue.socialnetwork.persistance;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Database {
	private Configuration cfg;
	private SessionFactory factory;
	private static Database instance;

	public static Database getInstance() {
		if (instance == null || instance.getSessionFactory() == null || instance.getSessionFactory().isClosed()) {
			instance = new Database();
		}
		return instance;
	}

	private Database() {
		cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");
		connect();
	}

	private void connect() {
		if (factory == null || factory.isClosed()) {
			factory = cfg.buildSessionFactory();
		}
	}

	public SessionFactory getSessionFactory() {
		return factory;
	}

	public void disconnect() {
		if (factory != null && !factory.isClosed()) {
			factory.close();
		}
	}

}

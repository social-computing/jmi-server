package com.socialcomputing.wps.server.persistence.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.socialcomputing.utils.database.DatabaseHelper;
import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;


public class DictionaryManagerImpl implements DictionaryManager {
	
	@Override
	public Collection<Dictionary> findAll() {
		Collection<Dictionary> results = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			results = session.createQuery("from DictionaryImpl").setCacheable(true).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
		return results;
	}
	
	@Override
	public Dictionary findByName(String name) {
		Dictionary result = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			result = (Dictionary) session.get(DictionaryImpl.class, name);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
		
		return result;
	}
	
	@Override
	public Dictionary create(String name, String definition) {
		Dictionary result = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			result = new DictionaryImpl(name, definition, "");
			session.save( result);
			String coefTable = WPSDictionary.getCoefficientTableName(name);
			String queueTable = WPSDictionary.getCoefficientQueuingTableName(name);
			session.createSQLQuery("create table " +  coefTable + " (id1 varchar(255) not null, id2 varchar(255) not null, ponderation float)").executeUpdate();
			session.createSQLQuery("create index id1 on " + coefTable + " (id1 , ponderation)").executeUpdate();
			session.createSQLQuery("create index id2 on " + coefTable + " (id2 , ponderation)").executeUpdate();
			switch (DatabaseHelper.GetDbType(session.connection())) {
				case DatabaseHelper.DB_MYSQL:
					session.createSQLQuery("create table " + queueTable + " (id varchar(255) not null, date timestamp)").executeUpdate();
					break;
				case DatabaseHelper.DB_SQLSERVER:
					session.createSQLQuery("create table " + queueTable + " (id varchar(255) not null, date DATETIME DEFAULT (getdate()))");
					break;
				case DatabaseHelper.DB_HSQL:
					session.createSQLQuery("create table " + queueTable + " (id varchar(255) not null, date timestamp)");
			}
			session.createSQLQuery("create index id on " + queueTable + " (id)").executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
		return result;
	}
	
	@Override
	public void update( Dictionary dictionary) {
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.update( dictionary);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
	}
	
	@Override
	public void remove(String name) {
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			Dictionary d = (Dictionary) session.get(DictionaryImpl.class, name);
			session.delete(d);
			session.createSQLQuery("drop table " + WPSDictionary.getCoefficientTableName(name)).executeUpdate();
			session.createSQLQuery("drop table " + WPSDictionary.getCoefficientQueuingTableName(name)).executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
	}
	
}

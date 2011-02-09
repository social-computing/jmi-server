package com.socialcomputing.wps.server.persistence.hibernate;

import java.util.Collection;

import org.apache.commons.collections.functors.SwitchTransformer;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.persistence.Swatch;
import com.socialcomputing.wps.server.persistence.SwatchManager;


public class SwatchManagerImpl implements SwatchManager {
	
	@Override
	public Collection<Swatch> findAll() {
		Collection<Swatch> results = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			results = session.createQuery("from SwatchImpl").setCacheable(true).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
		
		return results;
	}
	
	@Override
	public Swatch findByName(String name) {
		Swatch result = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			result = (Swatch) session.get(SwatchImpl.class, name);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
		return result;
	}
	
	@Override
	public Swatch create(String name, String definition) {
		Swatch result = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			result = new SwatchImpl( name, definition);
			session.save( result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
		return result;
	}
	
	@Override
	public void update( Swatch swatch) {
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.update( swatch);
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
			Swatch s = (Swatch) session.get(Swatch.class, name);
			session.delete(s);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			// HibernateUtil.closeSession();
		}
	}
}

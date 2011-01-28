package com.socialcomputing.wps.server.swatchs.loader;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.socialcomputing.utils.database.HibernateUtil;


public class SwatchManager {
	
	public Collection<Swatch> findAll() throws RemoteException {
		List<Swatch> results = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			results = session.createQuery("from Swatch").list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			session.close();
		}
		
		return results;
	}
	
	public Swatch findByName(String name) throws RemoteException {
		Swatch result = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			result = (Swatch) session.createQuery("from Swatch as s where s.name = ?").setString(0, name).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			session.close();
		}
		return result;
	}
	
	public Swatch create(String name) throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Swatch s = new Swatch();
			s.setName(name);
			session.save(s);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			session.close();
		}
		return new Swatch(name, null);
	}
	
	public void update(SwatchLoader sl) throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Swatch s = (Swatch) session.get(Swatch.class, sl.getName());
			s.setSwatch(sl.getSwatchDefinition());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			session.close();
		}
	}
	
	public void delete(String name) {
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Swatch s = (Swatch) session.get(Swatch.class, name);
			session.delete(s);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			session.close();
		}
	}
}

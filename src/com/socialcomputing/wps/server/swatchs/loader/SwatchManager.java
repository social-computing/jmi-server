package com.socialcomputing.wps.server.swatchs.loader;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.socialcomputing.utils.HibernateUtil;


public class SwatchManager {
	
	public Collection findAll() throws RemoteException {
		ArrayList<Swatch> csl = new ArrayList<Swatch>();
		Swatch sl = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Iterator<Swatch> results = session.createQuery("from Swatch").iterate();
			while (results.hasNext()) {
				Swatch s = (Swatch) results.next();
				sl = new Swatch(s.getName(), s.getSwatchDefinition());
				csl.add(sl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			session.close();
		}
		
		return csl;
	}
	
	public Swatch findByName(String name) throws RemoteException {
		Swatch sl = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Swatch result = (Swatch) session.createQuery("from Swatch as s where s.name = ?").setString(0, name).uniqueResult();
			if (result != null) {
				sl = new Swatch(result.getName(), result.getSwatchDefinition());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.commit();
			session.close();
		}
		
		return sl;
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

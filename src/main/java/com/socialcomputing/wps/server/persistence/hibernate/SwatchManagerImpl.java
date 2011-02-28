package com.socialcomputing.wps.server.persistence.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;
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
            results = session.createQuery("from SwatchImpl").list();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            tx.commit();
            // HibernateUtil.closeSession();
        }

        return results;
    }

    @Override
    public Swatch findByName(String name, String dictionaryName) {
        Swatch result = null;
        SwatchPk swatchPk = new SwatchPk(name, dictionaryName);
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            result = (Swatch) session.get(SwatchImpl.class, swatchPk);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            tx.commit();
            // HibernateUtil.closeSession();
        }
        return result;
    }

    @Override
    public Swatch create(String name, String definition, String dictionaryName) {
        Swatch result = null;
        SwatchPk swatchPk = new SwatchPk(name, dictionaryName);
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            result = new SwatchImpl(swatchPk, definition);
            session.save(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            tx.commit();
            // HibernateUtil.closeSession();
        }
        return result;
    }

    @Override
    public void update(Swatch swatch) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            session.update(swatch);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            tx.commit();
            // HibernateUtil.closeSession();
        }
    }

    @Override
    public void remove(String name, String dicoName) {
        SwatchPk swatchPk = new SwatchPk(name, dicoName);
        Session session = null;
        Transaction tx = null;
        DictionaryManager dManager = new DictionaryManagerImpl();
        Dictionary d = dManager.findByName(dicoName);
        
        try {
            session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            Swatch s = (Swatch) session.get(SwatchImpl.class, swatchPk);
            d.getSwatchs().remove(s);
            session.delete(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            tx.commit();
            // HibernateUtil.closeSession();
        }
    }
}

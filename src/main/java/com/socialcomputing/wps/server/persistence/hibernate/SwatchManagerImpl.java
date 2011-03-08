package com.socialcomputing.wps.server.persistence.hibernate;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;
import com.socialcomputing.wps.server.persistence.Swatch;
import com.socialcomputing.wps.server.persistence.SwatchManager;

public class SwatchManagerImpl implements SwatchManager {

    private static final Logger LOG = LoggerFactory.getLogger(SwatchManagerImpl.class);
                                                              
    @Override
    public Collection<Swatch> findAll() {
        Collection<Swatch> results = null;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            results = session.createQuery("from SwatchImpl").list();
            tx.commit();
        }
        catch (HibernateException e) {
            // If a transaction was opened before the error occured
            if ( tx != null )
                tx.rollback();
            LOG.error(e.getMessage(), e);
        }
        // Do not close session here yet 
        // closed in jsp files
        //        finally {
        //            HibernateUtil.closeSession();
        //        }

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
        catch (HibernateException e) {
            // If a transaction was opened before the error occured
            if ( tx != null )
                tx.rollback();
            LOG.error(e.getMessage(), e);
        }
        // Do not close session here yet 
        // closed in jsp files
        //        finally {
        //            HibernateUtil.closeSession();
        //        }
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
            tx.commit();
        }
        catch (HibernateException e) {
            // If a transaction was opened before the error occured
            if ( tx != null )
                tx.rollback();
            LOG.error(e.getMessage(), e);
        }
        // Do not close session here yet 
        // closed in jsp files
        //        finally {
        //            HibernateUtil.closeSession();
        //        }
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
            tx.commit();
        }
        catch (HibernateException e) {
            // If a transaction was opened before the error occured
            if ( tx != null )
                tx.rollback();
            LOG.error(e.getMessage(), e);
        }
        // Do not close session here yet 
        // closed in jsp files
        //        finally {
        //            HibernateUtil.closeSession();
        //        }
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
            tx.commit();
        }
        catch (HibernateException e) {
            // If a transaction was opened before the error occured
            if ( tx != null )
                tx.rollback();
            LOG.error(e.getMessage(), e);
        }
        // Do not close session here yet 
        // closed in jsp files
        //        finally {
        //            HibernateUtil.closeSession();
        //        }
    }
}

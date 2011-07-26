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
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            results = session.createQuery("from SwatchImpl").list();
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        }
        return results;
    }

    @Override
    public Swatch findByName(String name, String dictionaryName) {
        Swatch result = null;
        SwatchPk swatchPk = new SwatchPk(name, dictionaryName);
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            result = (Swatch) session.get(SwatchImpl.class, swatchPk);
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    
    @Override
    public Swatch create(String name, String definition, String dictionaryName) {
        Swatch result = null;
        SwatchPk swatchPk = new SwatchPk(name, dictionaryName);
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            result = new SwatchImpl(swatchPk, definition);
            session.save(result);
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void update(Swatch swatch) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.update(swatch);
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    
    @Override
    public void remove(String name, String dicoName) {
        SwatchPk swatchPk = new SwatchPk(name, dicoName);
        DictionaryManager dManager = new DictionaryManagerImpl();
        Dictionary d = dManager.findByName(dicoName);
        
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Swatch s = (Swatch) session.get(SwatchImpl.class, swatchPk);
            d.getSwatchs().remove(s);
            session.delete(s);
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}

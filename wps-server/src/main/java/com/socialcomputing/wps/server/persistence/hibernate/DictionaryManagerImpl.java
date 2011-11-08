package com.socialcomputing.wps.server.persistence.hibernate;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;

public class DictionaryManagerImpl implements DictionaryManager {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryManagerImpl.class);
    
    @Override
    public Collection<Dictionary> findAll() {
        Collection<Dictionary> results = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            results = session.createQuery("from DictionaryImpl as dico order by upper(dico.name)").list();
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        }
        return results;
    }

    @Override
    public Dictionary findByName(String name) {
        Dictionary result = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            result = (Dictionary) session.get(DictionaryImpl.class, name);
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Dictionary create(String name, String definition) {
        Dictionary result = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();

            result = new DictionaryImpl(name, definition, "");
            session.save(result);
/*            String coefTable = WPSDictionary.getCoefficientTableName(name);
            String queueTable = WPSDictionary.getCoefficientQueuingTableName(name);
            session.createSQLQuery("create table "
                                           + coefTable
                                           + " (id1 varchar(255) not null, id2 varchar(255) not null, ponderation float)")
                    .executeUpdate();
            session.createSQLQuery("create index id1 on " + coefTable + " (id1 , ponderation)").executeUpdate();
            session.createSQLQuery("create index id2 on " + coefTable + " (id2 , ponderation)").executeUpdate();
            switch (DatabaseHelper.GetDbType(session.connection())) {
                case DatabaseHelper.DB_MYSQL:
                    session.createSQLQuery("create table " + queueTable + " (id varchar(255) not null, date timestamp)")
                            .executeUpdate();
                    break;
                case DatabaseHelper.DB_SQLSERVER:
                    session.createSQLQuery("create table " + queueTable
                            + " (id varchar(255) not null, date DATETIME DEFAULT (getdate()))").executeUpdate();
                    break;
                case DatabaseHelper.DB_HSQL:
                    session.createSQLQuery("create table " + queueTable + " (id varchar(255) not null, date timestamp)").executeUpdate();
            }
            session.createSQLQuery("create index id on " + queueTable + " (id)").executeUpdate();*/
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    
    @Override
    public void update(Dictionary dictionary) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.update( dictionary);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void remove(String name) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            DictionaryImpl d =  ( DictionaryImpl)session.get(DictionaryImpl.class, name);
            session.delete(d);
//            session.createSQLQuery("drop table " + WPSDictionary.getCoefficientTableName(name)).executeUpdate();
//            session.createSQLQuery("drop table " + WPSDictionary.getCoefficientQueuingTableName(name)).executeUpdate();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}

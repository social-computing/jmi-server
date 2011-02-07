package com.socialcomputing.wps.server.affinityengine.scheduler;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Session;

import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.affinityengine.AffinityProcess;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;

public class BeanAffinityScheduler implements AffinityScheduler {

//	private SessionContext m_SessionContext = null;
	private DataSource m_DataSource = null; // Do not use

	public void deleteNow( String plan, String entity) throws RemoteException
	{
		try
		{
			Connection connection = this.getConnection();
			PreparedStatement stmtDeleteCoef = connection.prepareStatement( "delete from " + WPSDictionary.getCoefficientTableName( plan) + " where (id1 = ?) || (id2 = ?)");
			stmtDeleteCoef.setString( 1, entity);
			stmtDeleteCoef.setString( 2, entity);
			stmtDeleteCoef.executeUpdate();
			stmtDeleteCoef.close();
			connection.close();
		}
		catch(Exception e)
		{
			throw new RemoteException("Could not delete : " + e.getMessage());
		}
	}

	public void requestUpdateAll( String plan) throws RemoteException
	{
		try
		{
			Connection connection = this.getConnection();
			Statement stat = connection.createStatement();
			stat.executeUpdate( "delete from " + WPSDictionary.getCoefficientQueuingTableName( plan));
			stat.executeUpdate( "insert into " + WPSDictionary.getCoefficientQueuingTableName( plan) + " (id) VALUES ('')");
			stat.close();
			connection.close();
		}
		catch(Exception e)
		{
			throw new RemoteException("Could not update : " + e.getMessage());
		}
	}
	public void requestUpdate( String plan, String entity) throws RemoteException
	{
		WPSDictionary  dico = null;
		Connection connection = null;
		try
		{
			connection = this.getConnection();

			Statement stat = connection.createStatement();
			stat.executeUpdate( "insert into " + WPSDictionary.getCoefficientQueuingTableName( plan) + " (id) VALUES ('" + entity + "')");
			stat.close();
		}
		catch(Exception e)
		{
			throw new RemoteException("Could not update : " + e.getMessage());
		}
		finally
		{
			try {
				if( dico != null)
					dico.closeConnections();
				if( connection!= null)
					connection.close();
			} catch( Exception e) {}
		}
	}

	public void updateAllNow( String plan) throws RemoteException
	{
		WPSDictionary  dico = null;
		Connection connection = null;
		try
		{
			DictionaryManagerImpl manager =  new DictionaryManagerImpl();
			Dictionary dictionaryLoader = manager.findByName(plan);
			
			connection = this.getConnection();
			dico = 	dictionaryLoader.getDictionary();
			dico.openConnections( null);
			AffinityProcess process = new AffinityProcess( dico, connection);
			//Collection col = process.compute();
			process.compute();
		}
		catch(Exception e)
		{
			throw new RemoteException("Could not update : " + e.getMessage());
		}
		finally
		{
			try {
				if( dico != null)
					dico.closeConnections();
				if( connection!= null)
					connection.close();
			} catch( Exception e) {}
		}
	}

	public void updateNow( String plan, HashSet entities) throws RemoteException
	{
		WPSDictionary dico = null;
		Connection connection = null;
		try
		{
			DictionaryManagerImpl manager =  new DictionaryManagerImpl();
			Dictionary dictionaryLoader = manager.findByName(plan);
			
			connection = this.getConnection();
			dico = 	dictionaryLoader.getDictionary();
			dico.openConnections(  null);
			
			AffinityProcess process = new AffinityProcess( dico, entities, connection);
			//Collection col = process.compute();
			process.compute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RemoteException("Could not update : " + e.getMessage());
		}
		finally
		{
			try {
				if( dico != null)
					dico.closeConnections();
				if( connection!= null)
					connection.close();
			} catch( Exception e) {}
		}
	}


	private Connection getConnection() throws SQLException, RemoteException
	{
		/*if( m_DataSource == null)
		{
			try {
				Context context = new InitialContext();
				m_DataSource = (DataSource) context.lookup("java:comp/env/jdbc/WPSPooledDS");
			}
			catch (NamingException e)
			{
				throw new RemoteException("Could not obtain DataSource: " + e.getMessage());
			}
		}
		return m_DataSource.getConnection();*/
		Session session = HibernateUtil.currentSession();
		
		return session.connection();
	}
}

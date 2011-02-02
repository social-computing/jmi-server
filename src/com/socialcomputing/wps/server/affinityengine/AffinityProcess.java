package com.socialcomputing.wps.server.affinityengine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.socialcomputing.wps.server.plandictionary.FilteringProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierRuleConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;

/**
  * describes object which computes affinity coef in background
  *
  * **********************************************************
  * Java Class Name : AffinityProcess
  * ---------------------------------------------------------
  * Filetype: (SOURCE)
  * Filepath: C:\Dvpt\src\AffinityProcess.java
  *
  * ********************************************************** */

public class AffinityProcess
{
	private WPSDictionary m_Dictionary = null;
	private Collection m_EntitiesToUpdate = null;
	//private boolean m_IsAffinityInit = true;
	private Connection m_DbConnection = null;

	public  AffinityProcess( WPSDictionary dictionary, Connection connection )
	{
		m_Dictionary = dictionary;
		m_DbConnection = connection;
	}
	public AffinityProcess( WPSDictionary dictionary, Collection entitiesToUpdate, Connection connection )
	{
		this(dictionary, connection);
		m_EntitiesToUpdate = entitiesToUpdate;
	}

	/** *  delete all affinity coef in Db */
	private  void deleteAll(  )
	{
		try {
			Statement st  = m_DbConnection.createStatement();
			st.executeUpdate( "delete from " + WPSDictionary.getCoefficientTableName( m_Dictionary.m_Name));
		}
		catch( SQLException e) {}
	}

	/**
	* Compute All the affinity coef for all the entities (Rule by Rul, i.e. SegmentBySegment)
	*
	*/
	public Collection compute() throws WPSConnectorException
	{
		Collection col = null;
		iClassifierConnector classifier = m_Dictionary.getFilteringClassifier();

		if( m_EntitiesToUpdate == null)
		{
			deleteAll();
			Iterator ruleIt = classifier.getRules().iterator();
			while ( ruleIt.hasNext())
			{
				if ( col == null)
					col = compute( (iClassifierRuleConnector)ruleIt.next(), null);
				else
					col.addAll(  compute( (iClassifierRuleConnector)ruleIt.next(), null));
			}
		}
		else
		{	// if faut segmenter les entites pour ne les traiter que dans leur groupe
			Hashtable repartition = new Hashtable();
			Iterator it = m_EntitiesToUpdate.iterator();
			while( it.hasNext())
			{
				String id = (String )it.next();
				String classification = classifier.getClassification( id);
				ArrayList lst = ( ArrayList)repartition.get( classification);
				if( lst == null)
				{
					lst = new ArrayList();
					repartition.put( classification, lst);
				}
				lst.add( id);
			}
			// Calcul des groupes
			Enumeration keys = repartition.keys();
			while( keys.hasMoreElements())
			{
				String k = ( String) keys.nextElement();
				if ( col == null)
					col = compute( classifier.getRule( k), ( List)repartition.get( k));
				else
					col.addAll( compute( classifier.getRule( k), ( List)repartition.get( k)));
			}
		}
		return col;
	}

	private Collection compute( iClassifierRuleConnector ruleConnector, List update) throws WPSConnectorException
	{
		if( ruleConnector == null) return new ArrayList(); // id(s) has been destroyed or data error

		FilteringProfile    filterProf = m_Dictionary.getFilteringProfile( ruleConnector.getName());
		iIdEnumerator	 	entityEnum = ruleConnector.iterator();

		AffinitySegment seg = null;
		if ( update == null)
		   seg = new AffinitySegment( m_DbConnection, m_Dictionary, filterProf, entityEnum);
		else
		   seg = new AffinitySegment( m_DbConnection, m_Dictionary, filterProf, entityEnum, update);

		return seg.compute();
	}

	// Test
	public static void main(String [] args)
	{
		WPSDictionary dico = WPSDictionary.CreateTestInstance( "BooSol");

		try {
			dico.openConnections( null);

			Connection connection = null;
			Class.forName( "org.gjt.mm.mysql.Driver");
			Class.forName( "com.microsoft.jdbc.sqlserver.SQLServerDriver");
			connection = DriverManager.getConnection( "jdbc:mysql://io:3306/WPS?user=boosolreader&password=boosolreader");
			//connection = DriverManager.getConnection( "jdbc:microsoft:sqlserver://SATURNE:1433;DatabaseName=WPS;user=sa;password=youarehere");

			//st.executeUpdate( "delete from " + WPSDictionary.getCoefficientTableName( dico.m_Name));

			HashSet set = new HashSet();

			connection.setCatalog( "BOOSOL");
			Statement st  = connection.createStatement();
			ResultSet rs = st.executeQuery( "select id from users");
			while( rs.next())
			{
			   String id = rs.getString( 1);
			   set.add( id);
			}
			rs.close();

			/*ResultSet rs = st.executeQuery( "select id from " + WPSDictionary.getCoefficientQueuingTableName( "BooSol"));
			while( rs.next())
			{
				String id = rs.getString( 1);
				set.add( id);
			}
			rs.close();*/

			/*set.add( "7");
			set.add( "41");
			set.add( "2");
			set.add( "27");*/

			st.close();
			System.out.println("N:"+set.size());

			long t1= System.currentTimeMillis();
			connection.setCatalog( "WPS");
			AffinityProcess proc = new AffinityProcess( dico, set, connection);
			proc.compute();
			long t2= System.currentTimeMillis();
			System.out.println("Time:"+(t2-t1));
			dico.closeConnections();
		}
		catch( Exception e) {
			e.printStackTrace();
		}
	}
}


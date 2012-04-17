package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierRuleConnector;

public class JDBCEntityClassifier implements iClassifierConnector, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7962750791804278557L;
	public String m_Name = null;
	public String m_Description = null;

	public JDBCQuery m_GetClassifierQuery = null;

	public JDBCConnectionProfile m_ConnectionProfile = null;
	public boolean m_UseEntityConnection = true;

	private transient Connection m_Connection = null;

	// JDBCRuleConnector
	private Hashtable<String, JDBCRuleConnector> m_Rules = null;

	static JDBCEntityClassifier readObject( org.jdom.Element element)
	{
		JDBCEntityClassifier cl = new JDBCEntityClassifier( element.getAttributeValue( "name"));
		cl.m_ConnectionProfile = JDBCConnectionProfile.readObject( element.getChild( "JDBC-connection"));
		cl.m_UseEntityConnection = (cl.m_ConnectionProfile == null) ? true : false;

		if( cl.m_Name == null)
		{   // Classifier par défaut : tous les éléments : Une seule regle avec une requete pour tout sélectionner
			cl.m_Name =  WPSDictionary.DEFAULT_NAME;
			cl.m_Description = "Default classifier";

			JDBCRuleConnector rule = JDBCRuleConnector.readObject( element);
			rule.m_Name =  WPSDictionary.DEFAULT_NAME;
			rule.m_Description = "Default classifier";
			cl.m_Rules.put( rule.m_Name, rule);
		}
		else
		{
			cl.m_GetClassifierQuery = JDBCQuery.readObject( element);
			{   // Rules
				List lst = element.getChildren( "JDBC-classifier-rule");
				int size = lst.size();
				for( int i = 0; i < size; ++i)
				{
					JDBCRuleConnector rule = JDBCRuleConnector.readObject(  ( org.jdom.Element) lst.get( i));
					cl.m_Rules.put( rule.m_Name, rule);
				}
			}
		}
		return cl;
	}

	public JDBCEntityClassifier( String name)
	{
		m_Name = name;
		m_Rules = new Hashtable<String, JDBCRuleConnector>();
	}

	public void openConnections(   Hashtable<String, Object> wpsparams, Connection connection) throws JMIException
	{
		if( m_UseEntityConnection)
			m_Connection = connection;
		else
		{
			m_Connection = m_ConnectionProfile.getConnection();
		}
		for( JDBCRuleConnector j : m_Rules.values())
		{
			j.openConnections( wpsparams, m_Connection);
		}
		if( m_GetClassifierQuery != null)
			m_GetClassifierQuery.open( wpsparams, m_Connection);
	}

	public void closeConnections() throws JMIException
	{
		try {
			if( m_GetClassifierQuery != null)
				m_GetClassifierQuery.close();
			m_GetClassifierQuery = null;
			for( JDBCRuleConnector j : m_Rules.values())
			{
				j.closeConnections();
			}
			if( !m_UseEntityConnection)
				m_Connection.close();
		}
		catch( SQLException e)
		{
			throw new JMIException(JMIException.ORIGIN.CONNECTOR, "JDBC connector can't close JDBCEntityClassifier connection", e);
		}
	}

	@Override
	public  String getName()
	{
		return m_Name;
	}
	@Override
	public  String getDescription()
	{
		return m_Description;
	}
	@Override
	public Collection<iClassifierRuleConnector> getRules() 
	{
		return new ArrayList<iClassifierRuleConnector>( m_Rules.values());
	}

	@Override
	public iClassifierRuleConnector getRule( String id)
	{
		return ( iClassifierRuleConnector)m_Rules.get( id);
	}

	@Override
	public String getClassification( String id) throws JMIException
	{
		if( id == null)
			throw new JMIException(JMIException.ORIGIN.CONNECTOR, "JDBCEntityClassifier failed to set getClassification, id is null");

		// Default
		if( m_Name.equalsIgnoreCase(  WPSDictionary.DEFAULT_NAME))
			return m_Name;

		ResultSet rs = null;
		try {
			m_GetClassifierQuery.setCurEntity( id);
			rs = m_GetClassifierQuery.executeQuery();
			if( rs.next())
				return rs.getString( 1);
		}
		catch( SQLException e) { }
		finally {
			try {
				if( rs != null) rs.close();
			}
			catch( SQLException e) { }
		}
		return  WPSDictionary.DEFAULT_NAME;
	}
}

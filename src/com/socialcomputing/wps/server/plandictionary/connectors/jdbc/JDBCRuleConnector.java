package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierRuleConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;

public class JDBCRuleConnector implements iClassifierRuleConnector, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4878838437855810367L;
	private transient Connection m_Connection = null;
	public String m_Name = null;
	public String m_Description = null;

	public String m_ClassifierQuery = null;

	static JDBCRuleConnector readObject( org.jdom.Element element)
	{
		JDBCRuleConnector rule = new JDBCRuleConnector( element.getAttributeValue( "name"));
		rule.m_Description = element.getChildText( "comment");
		rule.m_ClassifierQuery = element.getChildText( "JDBC-query");
		return rule;
	}

	public JDBCRuleConnector( String name)
	{
		m_Name = name;
	}

	public void openConnections(  Hashtable wpsparams, Connection connection)
	{
		m_Connection = connection;
	}

	public void closeConnections()
	{
		m_Connection = null;
	}

	// iClassifierRuleConnector interface
	public  String getName()
	{
		return m_Name;
	}

	public  String getDescription()
	{
		return m_Description;
	}

	public iIdEnumerator getEnumerator() throws WPSConnectorException
	{
		try {
			Statement st = m_Connection.createStatement();
			return new JDBCIdEnumerator( st.executeQuery( m_ClassifierQuery));
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBCRuleConnector failed to set enumerator", e);
		}
	}

}

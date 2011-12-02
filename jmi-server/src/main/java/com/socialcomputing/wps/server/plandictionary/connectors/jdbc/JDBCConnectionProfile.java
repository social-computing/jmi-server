package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;

/**
 * Title:        WPS Connectors
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class JDBCConnectionProfile implements java.io.Serializable
{
	static final long serialVersionUID=3872029169563913212L;

	public String m_Driver = null;
	public String m_URL = null;
	public String m_Catalog = null;
	public String m_User = null, m_Password = null;

	public String m_JNDIDataSource = null;
	private transient DataSource m_DataSource = null;

	static JDBCConnectionProfile readObject( org.jdom.Element element)
	{
		JDBCConnectionProfile connector = null;
		if( element != null)
		{
			org.jdom.Element node = element.getChild( "JNDI-data-source");
			if( node != null)
				connector = new JDBCConnectionProfile( node.getChildText( "url"));
			else
			{
				node = element.getChild( "JDBC-driver");
				if( node != null)
				{
					connector = new JDBCConnectionProfile( node.getChildText( "class"), node.getChildText( "url"));
					connector.m_User = node.getAttributeValue( "user");
					connector.m_Password = node.getAttributeValue( "password");
				}
			}
			connector.m_Catalog = element.getAttributeValue( "catalog");
		}
		return connector;
	}

	public JDBCConnectionProfile( String JNDIDataSource)
	{
		m_JNDIDataSource = JNDIDataSource;
	}
	public JDBCConnectionProfile( String driver, String URL)
	{
		m_Driver = driver;
		m_URL = URL;
	}

	public Connection getConnection() throws WPSConnectorException
	{
		Connection connection = null;
		try {
			if( m_JNDIDataSource != null)
			{
				if( m_DataSource == null)
				{
					Context context =  new InitialContext();
					m_DataSource = (DataSource) context.lookup( m_JNDIDataSource);
				}
				connection = (m_DataSource != null) ? m_DataSource.getConnection() : null;
			}
			else if( (m_Driver != null) && (m_URL != null))
			{
				Class.forName( m_Driver);
				if( m_User != null && m_Password != null)
					connection = DriverManager.getConnection( m_URL, m_User, m_Password);
				else
					connection = DriverManager.getConnection( m_URL);
			}

			if( connection != null && m_Catalog != null)
			{
				connection.setCatalog( m_Catalog);
			}
		}
		catch( Exception e)
		{
			throw new WPSConnectorException( "JDBC connector can not open connection: '" + (m_JNDIDataSource == null ? m_URL : m_JNDIDataSource) + "'", e);
		}
		return connection;
	}
}
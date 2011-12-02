package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public abstract class JDBCQuery implements Serializable
{
	protected String m_Query = null;
	protected transient PreparedStatement m_QueryPS = null;

	protected transient Connection m_Connection = null;
	private transient Hashtable<String, Object> m_WPSParams = null;

	static JDBCQuery readObject( org.jdom.Element element)
	{
		JDBCQuery query = null;
		if( element.getChild( "JDBC-query") != null)
			query = new JDBCQuery2( element.getChildText( "JDBC-query"));
		else
			query = new JDBCQuery2( element.getChildText( "JDBC-query2"));
		return query;
	}

	public JDBCQuery( String query)
	{
		m_Query = query;
	}

	public void open( Hashtable<String, Object> wpsparams, Connection connection)
	{
		m_Connection = connection;
		m_WPSParams = wpsparams;
	}

	public Object getWPSProperty( String id)
	{
		Object o = m_WPSParams.get( id);
		if( o == null && id.startsWith( "$"))
			return m_WPSParams.get( id.substring( 1));
		return o;
	}

	public abstract void setCurEntity( String id) throws SQLException, WPSConnectorException;

	public abstract void setCurAttribute( String id) throws SQLException, WPSConnectorException;

	public abstract void setCurSubAttribute( String id) throws SQLException, WPSConnectorException;

	public abstract ResultSet executeQuery() throws SQLException, WPSConnectorException;

	public abstract void reset();
	
	public void close() throws WPSConnectorException
	{
		try {
			if( m_QueryPS != null)
				m_QueryPS.close();
			m_QueryPS = null;
			m_Connection = null;
			m_WPSParams = null;
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBC query can't close query", e);
		}
	}
}
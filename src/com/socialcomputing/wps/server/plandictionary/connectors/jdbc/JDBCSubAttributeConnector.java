package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.SubAttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;

public class JDBCSubAttributeConnector implements iSubAttributeConnector, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2566398189498805318L;
	public String m_Name = null;
	public String m_Description = null;

	public JDBCConnectionProfile m_ConnectionProfile = null;
	public boolean m_UseEntityConnection = true;
	private transient Connection m_Connection = null;

	/**
	* The SQL query retrieving sub attributes for an entity attribute (multiple rows).
	*
	* Used for implementing iEnumerator<SubAttributeEnumeratorItem>.
	*
	* Sample:
	* select PRODUCT_ID, PRODUCT_NAME from PRODUCTS, SELLS group by PRODUCT_ID where user = ?;
	*
	* ? will be substitued at run-time by the entity id.
	*             */
	public JDBCQuery m_SubAttributeQuery = null;

	// Propri�t�s
	public JDBCProperties m_Properties = null;

	static JDBCSubAttributeConnector readObject( org.jdom.Element element)
	{
		JDBCSubAttributeConnector subatt = null;
		if( element != null)
		{
			subatt = new JDBCSubAttributeConnector( element.getAttributeValue( "name"));
			subatt.m_SubAttributeQuery = JDBCQuery.readObject( element);
			subatt.m_Description = element.getChildText( "comment");
			// Connection
			subatt.m_ConnectionProfile = JDBCConnectionProfile.readObject( element.getChild( "JDBC-connection"));
			subatt.m_UseEntityConnection = (subatt.m_ConnectionProfile == null) ? true : false;
			// Properties
			subatt.m_Properties = JDBCProperties.readObject( JDBCProperties.SUBATTRIBUTE_PROPS, element);
		}
		return subatt;
	}

	public JDBCSubAttributeConnector( String name)
	{
		m_Name = name;
	}

	public void openConnections( Hashtable<String, Object> wpsparams, Connection connection) throws WPSConnectorException
	{
		if( m_UseEntityConnection)
			m_Connection = connection;
		else
		{
			m_Connection = m_ConnectionProfile.getConnection();
		}
		m_Properties.openConnections( wpsparams, m_Connection);
		m_SubAttributeQuery.open( wpsparams, m_Connection);
	}

	public void closeConnections() throws WPSConnectorException
	{
		try {
			m_SubAttributeQuery.close();
			m_SubAttributeQuery = null;
			m_Properties.closeConnections();
			if( !m_UseEntityConnection)
				m_Connection.close();
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBC connector can't close JDBCSubAttributeConnector connection", e);
		}
	}

	// iSubAttributeConnector interface
	public  String getName(  )
	{
		return m_Name;
	}

	public  String getDescription(  )
	{
		return m_Description;
	}

	public iEnumerator<SubAttributeEnumeratorItem> getEnumerator( String entity, String attribute)  throws WPSConnectorException
	{
		if( entity == null)
			throw new WPSConnectorException( "JDBCSubAttributeConnector failed to set getEnumerator, entity is null");
		if( attribute == null)
			throw new WPSConnectorException( "JDBCSubAttributeConnector failed to set getEnumerator, attribute is null");
		try {
			m_SubAttributeQuery.setCurEntity( entity);
			m_SubAttributeQuery.setCurAttribute( attribute);
			return new JDBCSubAttributeEnumerator( m_SubAttributeQuery.executeQuery());
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBCSubAttributeConnector failed to set enumerator", e);
		}
	}

	public Hashtable<String, Object> getProperties( String subAttributeId, String attributeId, String entityId ) throws WPSConnectorException
	{
		Hashtable<String, Object> table = new Hashtable<String, Object>();
		if( m_Properties != null)
			m_Properties.getProperties( table, subAttributeId, true, attributeId, entityId);
		return table;
	}
}

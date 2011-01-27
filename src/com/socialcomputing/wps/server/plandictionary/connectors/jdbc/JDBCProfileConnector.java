package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAttributeEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeConnector;

public class JDBCProfileConnector implements iProfileConnector, Serializable
{
	static final long serialVersionUID = -7700416109363057722L;

	public String m_Name = null;
	public String m_Description = null;

	public JDBCConnectionProfile m_ConnectionProfile = null;
	public boolean m_UseEntityConnection = true;

	private transient Connection m_Connection = null;

	/**
	* The SQL query retrieving attributes for an entity (multiple rows).
	*
	* Used for implementing iAttributeEnumerator.
	*
	* Sample:
	* select PRODUCT_ID from PRODUCTS, SELLS group by PRODUCT_ID where user = ?;
	*
	* ? will be substitued at run-time by the entity id.  */
	public JDBCQuery m_ProfileQuery = null, m_ExclusionQuery = null;

	public JDBCSubAttributeConnector m_SubAttributes = null;

	// JDBCSelectionConnector TreeMap
	public Hashtable  m_Selections = null;

	// Propriétés
	public JDBCProperties m_AnalysisProperties = null;
	public JDBCProperties m_Properties = null;

	static JDBCProfileConnector readObject( org.jdom.Element element)
	{
		JDBCProfileConnector profile = new JDBCProfileConnector( element.getAttributeValue( "name"));
		profile.m_ProfileQuery = JDBCQuery.readObject( element);
		profile.m_Description = element.getChildText( "comment");
		// Connection
		profile.m_ConnectionProfile = JDBCConnectionProfile.readObject( element.getChild( "JDBC-connection"));
		profile.m_UseEntityConnection = (profile.m_ConnectionProfile == null) ? true : false;
		// Sub Attributes
		profile.m_SubAttributes = JDBCSubAttributeConnector.readObject( element.getChild( "JDBC-subattributes"));
		// Exclusions
		org.jdom.Element exclusion = element.getChild( "JDBC-exclusion");
		if( exclusion != null)
			profile.m_ExclusionQuery = JDBCQuery.readObject( exclusion);
		// Analysis Properties
		profile.m_AnalysisProperties = JDBCProperties.readObject( JDBCProperties.ATTRIBUTE_PROPS, element.getChild( "JDBC-analysis-properties"));
		// Properties
		profile.m_Properties = JDBCProperties.readObject( JDBCProperties.ATTRIBUTE_PROPS, element);

		{   // Sélections
			List lst = element.getChildren( "JDBC-selection");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				JDBCSelectionConnector sel = JDBCSelectionConnector.readObject(  JDBCSelectionConnector.ATTRIBUTE_SELECTION, ( org.jdom.Element) lst.get( i));
				profile.m_Selections.put( sel.m_Name, sel);
			}
		}

		return profile;
	}

	public JDBCProfileConnector( String name)
	{
		m_Name = name;
		m_Selections = new Hashtable();
	}

	public void openConnections( Hashtable wpsparams, Connection connection) throws WPSConnectorException
	{
		if( m_UseEntityConnection)
			m_Connection = connection;
		else
			m_Connection = m_ConnectionProfile.getConnection();

		Iterator it = m_Selections.values().iterator();
		while( it.hasNext())
		{
			JDBCSelectionConnector j = ( JDBCSelectionConnector) it.next();
			j.openConnections( wpsparams, m_Connection);
		}
		if( m_SubAttributes != null)
			m_SubAttributes.openConnections( wpsparams, m_Connection);

		m_Properties.openConnections( wpsparams, m_Connection);
		m_AnalysisProperties.openConnections( wpsparams, m_Connection);

		m_ProfileQuery.open( wpsparams, m_Connection);
		if( m_ExclusionQuery != null)
			m_ExclusionQuery.open( wpsparams, m_Connection);
	}

	public void closeConnections() throws WPSConnectorException
	{
		try {
			m_ProfileQuery.close();
			if( m_ExclusionQuery != null)
				m_ExclusionQuery.close();
			if( !m_UseEntityConnection)
				m_Connection.close();

			Iterator it = m_Selections.values().iterator();
			while( it.hasNext())
			{
				JDBCSelectionConnector j = ( JDBCSelectionConnector) it.next();
				j.closeConnections();
			}

			m_Properties.closeConnections();
			m_AnalysisProperties.closeConnections();
			if( m_SubAttributes != null)
				m_SubAttributes.closeConnections();
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBC connector can't close JDBCProfileConnector connection", e);
		}
	}

	// iProfileConnector interface
	public  String getName()
	{
		return m_Name;
	}

	public  String getDescription()
	{
		return m_Description;
	}

	public iAttributeEnumerator getEnumerator( String entityId ) throws WPSConnectorException
	{
		if( entityId == null)
			throw new WPSConnectorException( "JDBCProfileConnector failed to set getEnumerator, entityId is null");
		try {
			m_ProfileQuery.setCurEntity( entityId);
			return new JDBCAttributeEnumerator( m_ProfileQuery.executeQuery());
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBCProfileConnector failed to set enumerator", e);
		}
	}

	public Hashtable getAnalysisProperties( String attributeId, String entityId) throws WPSConnectorException
	{
		Hashtable table = new Hashtable();
		m_AnalysisProperties.getProperties( table, attributeId, false, entityId, null);
		return table;
	}

	public iIdEnumerator getExclusionEnumerator( String entityId) throws WPSConnectorException
	{
		if( m_ExclusionQuery != null)
		{
			try {
				if( entityId != null)
					m_ExclusionQuery.setCurEntity( entityId);
				return new JDBCIdEnumerator( m_ExclusionQuery.executeQuery());
			}
			catch( SQLException e)
			{
				throw new WPSConnectorException( "JDBCProfileConnector failed to set exclusion enumerator", e);
			}
		}
		return new JDBCIdEnumerator();
	}

	public Hashtable getProperties( String attributeId, boolean bInBase, String entityId) throws WPSConnectorException
	{
		Hashtable table = new Hashtable();
		m_Properties.getProperties( table, attributeId, bInBase, entityId, null);
		return table;
	}

	public iSubAttributeConnector getSubAttribute()
	{
		return m_SubAttributes;
	}

	public Collection getSelections()
	{
		return m_Selections.values();
	}

	public iSelectionConnector getSelection( String selectionId)
	{
		return ( JDBCSelectionConnector) m_Selections.get( selectionId);
	}
}

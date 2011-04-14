package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;

/**
 * Title:        WPS Connectors
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class JDBCSelectionConnector implements iSelectionConnector, java.io.Serializable
{
	static final long serialVersionUID = 6931896435066884702L;

	public static final int USE_NO_ID = 0;
	public static final int USE_CURRENTID = 1;
	public static final int USE_REFID = 2;
	public static final int USE_CURRENTID_REFID = 3;

	public static final int ENTITY_SELECTION = 0;
	public static final int ATTRIBUTE_SELECTION = 1;

	public String m_Name = null;
	public String m_Description = null;

	public int m_Type = ENTITY_SELECTION;

	public JDBCConnectionProfile m_ConnectionProfile = null;
	public boolean m_UseEntityConnection = true;

	private transient Connection m_Connection = null;

	public JDBCQuery m_SelectionQuery = null;
	public int m_Flag = USE_NO_ID;
	public int m_AttributeRestriction = WPSDictionary.APPLY_TO_ALL;

	private transient HashSet<String> m_Result = null; // optimisation pour USE_NO_ID et USE_REFID

	static JDBCSelectionConnector readObject( int type, org.jdom.Element element)
	{
		JDBCSelectionConnector sel = new JDBCSelectionConnector( element.getAttributeValue( "name"));
		sel.m_SelectionQuery = JDBCQuery.readObject( element);
		sel.m_Description = element.getChildText( "comment");
		sel.m_Type = type;
		String p = element.getAttributeValue( "use");
		if( p.equalsIgnoreCase( "curid"))
			sel.m_Flag = USE_CURRENTID;
		else if( p.equalsIgnoreCase( "refid"))
			sel.m_Flag = USE_REFID;
		else if( p.equalsIgnoreCase( "currefid"))
			sel.m_Flag = USE_CURRENTID_REFID;
		else
			sel.m_Flag = USE_NO_ID;
		p = element.getAttributeValue( "apply");
		if( p.equalsIgnoreCase( "base"))
			sel.m_AttributeRestriction = WPSDictionary.APPLY_TO_BASE;
		else if( p.equalsIgnoreCase( "notbase"))
			sel.m_AttributeRestriction = WPSDictionary.APPLY_TO_NOT_BASE;
		else
			sel.m_AttributeRestriction = WPSDictionary.APPLY_TO_ALL;

		// Connection
		sel.m_ConnectionProfile = JDBCConnectionProfile.readObject( element.getChild( "JDBC-connection"));
		sel.m_UseEntityConnection = (sel.m_ConnectionProfile == null) ? true : false;
		return sel;
	}

	public JDBCSelectionConnector( String name)
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
		m_SelectionQuery.open( wpsparams, m_Connection);
	}

	public void closeConnections() throws WPSConnectorException
	{
		try {
			m_SelectionQuery.close();
			m_SelectionQuery = null;
			if( !m_UseEntityConnection)
				m_Connection.close();
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBC connector can't close JDBCSelectionConnector connection", e);
		}
	}

	@Override
	public String getName()
	{
		return m_Name;
	}

	@Override
	public  String getDescription()
	{
		return m_Description;
	}

	@Override
	public boolean isRuleVerified(String id, boolean bInBase, String refEntityId) throws WPSConnectorException
	{
		boolean ret  = false;
		if ((bInBase && (m_AttributeRestriction == WPSDictionary.APPLY_TO_NOT_BASE))
		  || (!bInBase && (m_AttributeRestriction == WPSDictionary.APPLY_TO_BASE)))
		  return false;

		try {
			if( m_Type == ENTITY_SELECTION)
				m_SelectionQuery.setCurEntity( id);
			else //if( m_Type == ATTRIBUTE_SELECTION)
				m_SelectionQuery.setCurAttribute( id);
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBCSelectionConnector failed to verify rule", e);
		}

		switch( m_Flag)
		{
			case USE_NO_ID:
			case USE_REFID:
				// Memorisation des resultats
				if( m_Result == null)
				{
					try {
						ResultSet rs = m_SelectionQuery.executeQuery();
						m_Result = new HashSet<String>();
						while( rs.next())
							m_Result.add( rs.getString( 1));
						rs.close();
					}
					catch( SQLException e)
					{
						throw new WPSConnectorException( "JDBCSelectionConnector failed to verify rule", e);
					}
				}
				else
					m_SelectionQuery.reset();
				ret = m_Result.contains( id);
				break;
			case USE_CURRENTID:
			case USE_CURRENTID_REFID:
					// Requete a chaque demande
					try {
						ResultSet rs = m_SelectionQuery.executeQuery();
						if( rs.next())
							ret = true;
						rs.close();
					} catch( SQLException e)
					{
						throw new WPSConnectorException( "JDBCSelectionConnector failed to verify rule", e);
					}
				break;
		}
		return ret;
	}
}
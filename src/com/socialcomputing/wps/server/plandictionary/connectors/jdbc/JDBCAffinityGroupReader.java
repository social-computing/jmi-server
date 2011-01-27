package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.utils.StringAndFloat;

/**
 * Title:        WPS Connectors
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class JDBCAffinityGroupReader implements iAffinityGroupReader, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8069490644747383487L;
	public String m_Name = null;
	public String m_Description = null;

	public JDBCConnectionProfile m_ConnectionProfile = null;
	public boolean m_UseEntityConnection = true;

	private transient Connection m_Connection = null;

	private JDBCQuery m_ReaderQuery = null;

	static JDBCAffinityGroupReader readObject( org.jdom.Element element)
	{
		JDBCAffinityGroupReader grp = new JDBCAffinityGroupReader( element.getAttributeValue( "name"));
		grp.m_ReaderQuery = JDBCQuery.readObject( element);
		grp.m_Description = element.getChildText( "comment");
		// Connection
		grp.m_ConnectionProfile = JDBCConnectionProfile.readObject( element.getChild( "JDBC-connection"));
		grp.m_UseEntityConnection = (grp.m_ConnectionProfile == null) ? true : false;
		return grp;
	}

	public JDBCAffinityGroupReader( String name)
	{
		m_Name = name;
	}

	public void openConnections(  Hashtable wpsparams, Connection connection) throws WPSConnectorException
	{
		if( m_UseEntityConnection)
			m_Connection = connection;
		else
			m_Connection = m_ConnectionProfile.getConnection();
		m_ReaderQuery.open( wpsparams, m_Connection);
	}

	public void closeConnections() throws WPSConnectorException
	{
		try {
			m_ReaderQuery.close();
			if( !m_UseEntityConnection)
				m_Connection.close();
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBC connector can't close JDBCAffinityGroupReader connection", e);
		}
	}

	public StringAndFloat[] retrieveAffinityGroup( String id, int affinityThreshold, int max) throws WPSConnectorException
	{
		boolean normalizeIt=false;
		float pond;
		float maxPond=Float.MIN_VALUE;

		ArrayList<StringAndFloat> eList = new ArrayList<StringAndFloat>();
		try {
			if (id != null)
			   m_ReaderQuery.setCurAttribute( id);
			ResultSet rs = m_ReaderQuery.executeQuery();
			for( int i = 0; (i < max) && (rs.next()); ++i)
			{
				pond = rs.getFloat( 2);
				eList.add( new StringAndFloat( rs.getString( 1), pond));

				if ((!normalizeIt) && (pond>1.0))
					normalizeIt = true;
				if ((normalizeIt) && (pond>maxPond) )
					maxPond=pond;
			}
			rs.close();
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBCAffinityGroupReader can not read affinity group", e);
		}

		if (normalizeIt)
		{
			StringAndFloat obj;
			float threshold=(float)affinityThreshold/(float)100;

			int size = eList.size();
			for( int i = 0; i < size; )
			{
				obj= (StringAndFloat) eList.get( i);
				obj.m_value=(float)((maxPond-obj.m_value)/maxPond);
				if (obj.m_value>threshold)
				{
				   eList.remove( i);
				   --size;
				}
				else
					++i;
			}
		}
		else
		{
			StringAndFloat obj;
			float threshold=(float)affinityThreshold/(float)100;

			int size = eList.size();
			for( int i = 0; i < size; )
			{
				obj= (StringAndFloat) eList.get( i);
				if (obj.m_value>threshold)
				{
				   eList.remove( i);
				   --size;
				}
				else
					++i;
			}
		}
		return ( StringAndFloat[]) eList.toArray( new StringAndFloat[0]);
	}
}
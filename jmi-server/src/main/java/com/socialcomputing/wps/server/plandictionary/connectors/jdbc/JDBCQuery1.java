package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

@Deprecated
public class JDBCQuery1 extends JDBCQuery
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -909314903407368310L;
	private int cpt = 1;

	public JDBCQuery1( String query)
	{
		super( query);
	}

	@Override
	public void setCurEntity( String id) throws SQLException, JMIException
	{
		check();
		m_QueryPS.setString( cpt++, id);
	}

	@Override
	public void setCurAttribute( String id) throws SQLException, JMIException
	{
		check();
		m_QueryPS.setString( cpt++, id);
		//m_QueryPS.setInt(cpt++, new Integer(id).intValue() );
	}

	@Override
	public void setCurSubAttribute( String id) throws SQLException, JMIException
	{
		check();
		//System.out.println("setCurSubAttribute : "+id+"\n");
		m_QueryPS.setString( cpt++, id);
	}

	@Override
	public ResultSet executeQuery() throws SQLException, JMIException
	{
		//System.out.println(m_Query);
		check();
		reset();
		return m_QueryPS.executeQuery();
	}

	@Override
	public void reset()
	{
		cpt = 1;
	}

	// Remplacement parametres globaux uniquement ex : {s$PROP} et creation du PreparedStatement
	private void check() throws SQLException, JMIException
	{
		if( m_QueryPS != null) return;

		int start = 0;
		int pos = m_Query.indexOf( "{s$", start);
		if( pos >= 0)
		{
			int end = m_Query.indexOf( "}", pos);
			StringBuffer nquery = new StringBuffer();
			while( pos >= 0 && end >= 0)
			{
				String propName = m_Query.substring( pos+2, end);
				String propValue = (String) getWPSProperty( propName);
				if( propValue == null)
					throw new JMIException(JMIException.ORIGIN.CONNECTOR, "JDBCQuery1 : global property '" + propName + "'is not set for query: " + m_Query);
				nquery.append( m_Query.substring( start, pos));
				nquery.append( propValue);
				start = end+1;
				pos = m_Query.indexOf( "{s$", start);
				end = m_Query.indexOf( "}", pos);
			}
			nquery.append( m_Query.substring( start));
			m_Query = nquery.toString();
		}
		m_QueryPS = m_Connection.prepareStatement( m_Query);
	}
}
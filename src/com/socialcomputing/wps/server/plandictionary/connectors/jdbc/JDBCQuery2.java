package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class JDBCQuery2 extends JDBCQuery
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3133883409410549876L;
	//private int cpt = 1;
	private String curEntity = null, curAttribute = null, curSubAttribute = null;
	private ArrayList listParameters = new ArrayList();

	private class QueryParameter
	{
		public static final int STRING = 0x0000;
		public static final int INT = 0x0001;
		public static final int FLOAT = 0x0002;

		public static final int VALUE = 0x0000;
		public static final int CUR_ENTITY = 0x0100;
		public static final int CUR_ATTRIBUTE = 0x0200;
		public static final int CUR_SUB_ATTRIBUTE = 0x0400;

		public QueryParameter( int type)
		{
			this.type = type;
		}

		public QueryParameter( int type, String value)
		{
			this.type = type;
			this.stringValue = value;
		}

		public String stringValue = null;
		public int type = STRING;
	}

	public JDBCQuery2( String query)
	{
		super( query);
	}

	public void setCurEntity( String id)
	{
		curEntity = id;
	}

	public void setCurAttribute( String id)
	{
		curAttribute = id;
	}

	public void setCurSubAttribute( String id)
	{
		curSubAttribute = id;
	}

	public ResultSet executeQuery() throws SQLException, WPSConnectorException
	{
		prepareQuery();
		fillParameters();
		reset();
		return m_QueryPS.executeQuery();
	}

	public void reset()
	{
		curEntity = null;
		curAttribute = null;
		curSubAttribute = null;
	}

	// Remplacement parametres globaux ex : {s$PROP} et création du PreparedStatement
	private void prepareQuery() throws SQLException
	{
		if( m_QueryPS != null) return;

		StringBuffer nquery = new StringBuffer();

		boolean beg = true;
		StringTokenizer st = new StringTokenizer( m_Query, "{}");
		for( int i = 0; st.hasMoreTokens(); ++i, beg = !beg)
		{
			String s = st.nextToken();
			if( beg)
				nquery.append( s);
			else
			{
				int type = QueryParameter.STRING;
				switch( s.charAt( 0))
				{
					case 's':
						type = QueryParameter.STRING;
						break;
					case 'i':
						type = QueryParameter.INT;
						break;
					case 'f':
						type = QueryParameter.FLOAT;
						break;
					default:
						throw new SQLException( "JDBCQuery2: unknown tag type " + s.charAt( 0) + "of " + s + " in " +  m_Query);
				}
				s = s.substring( 1);

				if( s.equalsIgnoreCase( "curEntity"))
					listParameters.add( new QueryParameter( type | QueryParameter.CUR_ENTITY));
				else if( s.equalsIgnoreCase( "curAttribute"))
					listParameters.add( new QueryParameter( type | QueryParameter.CUR_ATTRIBUTE));
				else if( s.equalsIgnoreCase( "curSubAttribute"))
					listParameters.add( new QueryParameter( type | QueryParameter.CUR_ENTITY));
				else if( s.equalsIgnoreCase( "$attributeId"))
				{
					String propValue = (String) getWPSProperty( "~$connectorAttributeId");
					if( propValue == null)
						throw new SQLException( "JDBCQuery2: global property '~$connectorAttributeId' is not set for query: " + m_Query);
					listParameters.add( new QueryParameter( type | QueryParameter.VALUE, propValue));
				}
				else if( s.startsWith( "$"))
				{
					String propValue = (String) getWPSProperty( s);
					if( propValue == null)
						throw new SQLException( "JDBCQuery2: global property '" + s + "'is not set for query: " + m_Query);
					listParameters.add( new QueryParameter( type | QueryParameter.VALUE, propValue));
				}
				else
					throw new SQLException( "JDBCQuery2: unknown tag " + s + " in " +  m_Query);
				nquery.append( '?');
			}
			//System.out.println( nquery.toString());
		}
		m_QueryPS = m_Connection.prepareStatement( nquery.toString());
	}
	private void fillParameters() throws SQLException, WPSConnectorException
	{
		for( int i = 1; i <= listParameters.size(); ++i)
		{
			 QueryParameter qp = ( QueryParameter) listParameters.get( i-1);
			 switch( qp.type & 0xFF00)
			 {
				 case QueryParameter.CUR_ENTITY:
					 if( curEntity == null)
						 throw new WPSConnectorException( "JDBCQuery2 curEntity is not set for query: " + m_Query);
					 fillParameter( i, qp.type, curEntity);
					 break;
				 case QueryParameter.CUR_ATTRIBUTE:
					 if( curAttribute == null)
						 throw new WPSConnectorException( "JDBCQuery2 curAttribute is not set for query: " + m_Query);
					 fillParameter( i, qp.type, curAttribute);
					 break;
				 case QueryParameter.CUR_SUB_ATTRIBUTE:
					 if( curSubAttribute == null)
						 throw new WPSConnectorException( "JDBCQuery2 curSubAttribute is not set for query: " + m_Query);
					fillParameter( i, qp.type, curSubAttribute);
					 break;
				 case QueryParameter.VALUE:
					 fillParameter( i, qp.type, qp.stringValue);
					 break;
			 }
		}
	}
	private void fillParameter( int index, int type, String stringValue) throws SQLException, WPSConnectorException
	{
		if((type & 0x00FF) == QueryParameter.STRING)
		{
			m_QueryPS.setString( index, stringValue);
		}
		else if((type & 0x00FF) == QueryParameter.INT)
		{
			m_QueryPS.setInt( index, Integer.parseInt( stringValue));
		}
		else //if((type & 0x00FF) == QueryParameter.FLOAT)
		{
			m_QueryPS.setFloat( index, Float.parseFloat( stringValue));
		}
	}

	public static void main(String [] args)
	{
		try {
			JDBCQuery2 q = new JDBCQuery2( "select * from and {scurEntity} or {scurAttribute} {s$TOTO} ");
			q.executeQuery();
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
	}
}

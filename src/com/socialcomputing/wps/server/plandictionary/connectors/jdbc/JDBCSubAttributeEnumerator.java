package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.socialcomputing.wps.server.plandictionary.connectors.SubAttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeEnumerator;

public class JDBCSubAttributeEnumerator implements iSubAttributeEnumerator
{
	private ResultSet m_ResultSet = null;
	private boolean m_needNext = true;

	public JDBCSubAttributeEnumerator( ResultSet rs )
	{
		m_ResultSet = rs;
	}

	public boolean hasNext(  )
	{
		try {
			if( m_needNext)
			{
				m_needNext = false;
				if( !m_ResultSet.next())
				{
					m_ResultSet.close();
					return false;
				}
			}
		}
		catch( SQLException e)
		{
			return false;
		}
		return true;
	}

	public  void next( SubAttributeEnumeratorItem item) throws WPSConnectorException
	{
		try {
			if( m_needNext)
				if( !m_ResultSet.next())
					return;

			item.m_Id = m_ResultSet.getString( 1);
			item.m_Ponderation = m_ResultSet.getFloat( 2);

			m_needNext = true;
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBCSubAttributeEnumerator failed to read next item", e);
		}
	}

}

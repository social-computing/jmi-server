package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.socialcomputing.wps.server.plandictionary.connectors.IdEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;

public class JDBCIdEnumerator implements iIdEnumerator
{
	private ResultSet m_ResultSet = null;
	private boolean m_needNext = true;

	public JDBCIdEnumerator()
	{   // Empty Enumerator
	}

	public JDBCIdEnumerator( ResultSet rs )
	{
		m_ResultSet = rs;
	}

	public void next( IdEnumeratorItem item) throws WPSConnectorException
	{
		if( m_ResultSet == null) return;
		try {
			if( m_needNext)
				if( !m_ResultSet.next())
					return;

			item.m_Id = m_ResultSet.getString( 1);

			m_needNext = true;
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBCIdEnumerator failed to read next item", e);
		}
	}

	public boolean hasNext()
	{
		if( m_ResultSet == null) return false;
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

}

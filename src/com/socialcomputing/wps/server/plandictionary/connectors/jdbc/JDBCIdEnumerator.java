package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

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

	@Override
	public iIdEnumerator iterator() {
		return this;
	}
	
	@Override
	public String next()
	{
		if( m_ResultSet == null) return null;
		try {
			if( m_needNext)
				if( !m_ResultSet.next())
					return null;

			m_needNext = true;
			return m_ResultSet.getString( 1);
		}
		catch( SQLException e)
		{
			e.printStackTrace();
			//throw new WPSConnectorException( "JDBCIdEnumerator failed to read next item", e);
		}
		return null;
	}

	@Override
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

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}

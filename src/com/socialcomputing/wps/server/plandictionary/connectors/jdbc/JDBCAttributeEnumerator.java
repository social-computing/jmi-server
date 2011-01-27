package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAttributeEnumerator;

public class JDBCAttributeEnumerator implements iAttributeEnumerator
{
	private ResultSet m_ResultSet = null;
	private boolean m_needNext = true;

	public JDBCAttributeEnumerator( ResultSet rs )
	{
		m_ResultSet = rs;
	}

	public void next( AttributeEnumeratorItem item) throws WPSConnectorException
	{
		try {
			if( m_needNext)
				if( !m_ResultSet.next())
					return;

			item.m_Id = m_ResultSet.getString( 1);
			if( item.m_Id == null)
			{
				System.out.println( "WARNING: JDBCAttributeEnumerator next, id is null");
				item.m_Id = "";
			}
			item.m_Ponderation = m_ResultSet.getFloat( 2);

			m_needNext = true;
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBCAttributeEnumerator failed to read next item", e);
		}
	}

	public boolean hasNext()
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
}

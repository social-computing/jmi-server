package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

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

	@Override
	public Iterator<SubAttributeEnumeratorItem> iterator() {
		return this;
	}

	@Override
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

	@Override
	public SubAttributeEnumeratorItem next() 
	{
		SubAttributeEnumeratorItem item = null;
		try {
			if( m_needNext)
				if( !m_ResultSet.next())
					return item;

			item = new SubAttributeEnumeratorItem( m_ResultSet.getString( 1), m_ResultSet.getFloat( 2));
			m_needNext = true;
		}
		catch( SQLException e)
		{
			e.printStackTrace();
			//throw new WPSConnectorException( "JDBCSubAttributeEnumerator failed to read next item", e);
		}
		return item;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}

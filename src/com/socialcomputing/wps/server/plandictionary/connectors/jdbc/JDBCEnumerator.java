package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;

public abstract class JDBCEnumerator<E> implements iEnumerator<E> {
	protected ResultSet m_ResultSet = null;
	protected boolean m_needNext = true;

	public JDBCEnumerator(ResultSet rs) {
		m_ResultSet = rs;
	}

	@Override
	public Iterator<E> iterator() {
		return this;
	}

	public abstract E getItem() throws SQLException;
	
	@Override
	public E next() 
	{
		try {
			if( m_needNext)
				if( !m_ResultSet.next())
					return null;

			E item = getItem();
			m_needNext = true;
			return item;
		}
		catch( SQLException e)
		{
			e.printStackTrace();
			//throw new WPSConnectorException( "JDBCEnumerator failed to read next item", e);
		}
		return null;
	}

	@Override
	public boolean hasNext() {
		if( m_ResultSet == null)
			return false;
		try {
			if (m_needNext) {
				m_needNext = false;
				if (!m_ResultSet.next()) {
					m_ResultSet.close();
					return false;
				}
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	@Override
	public void remove() {
	}

}

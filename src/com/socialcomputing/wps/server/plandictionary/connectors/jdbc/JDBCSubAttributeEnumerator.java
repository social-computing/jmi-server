package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.socialcomputing.wps.server.plandictionary.connectors.SubAttributeEnumeratorItem;

public class JDBCSubAttributeEnumerator extends JDBCEnumerator<SubAttributeEnumeratorItem>
{
	public JDBCSubAttributeEnumerator( ResultSet rs )
	{
		super( rs);
	}

	@Override
	public SubAttributeEnumeratorItem getItem() throws SQLException {
		return new SubAttributeEnumeratorItem( m_ResultSet.getString( 1), m_ResultSet.getFloat( 2));
	}
			
}

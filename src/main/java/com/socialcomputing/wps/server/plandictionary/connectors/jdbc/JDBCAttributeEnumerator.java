package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;

public class JDBCAttributeEnumerator extends JDBCEnumerator<AttributeEnumeratorItem>
{
	public JDBCAttributeEnumerator( ResultSet rs)
	{
		super( rs);
	}

	@Override
	public AttributeEnumeratorItem getItem() throws SQLException {
		AttributeEnumeratorItem item = new AttributeEnumeratorItem( m_ResultSet.getString( 1), m_ResultSet.getFloat( 2));
		if( item.m_Id == null)
		{
			System.out.println( "WARNING: JDBCAttributeEnumerator next, id is null");
			item.m_Id = "";
		}
		return item;
	}

}

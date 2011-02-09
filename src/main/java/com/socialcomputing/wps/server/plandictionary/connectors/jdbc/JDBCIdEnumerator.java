package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCIdEnumerator extends JDBCEnumerator<String>
{
	public JDBCIdEnumerator( ResultSet rs )
	{
		super( rs);
	}

	@Override
	public String getItem() throws SQLException {
		return m_ResultSet.getString( 1);
	}

			
}

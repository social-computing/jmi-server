package com.socialcomputing.utils.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Title:        Database Tool
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class DatabaseHelper
{
	public static final int DB_UNDETERMINED = -1;
	public static final int DB_MYSQL = 0;
	public static final int DB_SQLSERVER = 1;
	public static final int DB_HSQL = 2;

	public static int GetDbType( Connection connection) throws SQLException
	{
		if( connection != null)
		{
			String name = connection.getMetaData().getDatabaseProductName();
			if( name.equalsIgnoreCase( "MySQL"))
				return DB_MYSQL;
			else if ( name.startsWith( "HSQL"))
			   return DB_HSQL;
			return DB_SQLSERVER; //DB_UNDETERMINED;
		}
		return DB_UNDETERMINED;
	}
	public static boolean DatabaseExist( Connection connection, String database) throws SQLException
	{
		DatabaseMetaData meta = connection.getMetaData();
		ResultSet rs = meta.getCatalogs();
		try {
			while( rs.next())
			{
				if( rs.getString( 1).equalsIgnoreCase( database))
					return true;
			}
		}
		finally	{
			rs.close();
		}
		return false;
	}

	public static boolean TableExist( Connection connection, String table) throws SQLException
	{
		DatabaseMetaData meta = connection.getMetaData();
		String[] names = {"TABLE"};
		ResultSet rs = meta.getTables( connection.getCatalog(), "%", "%", names);
		try {
			while( rs.next())
			{
				if( rs.getString( "TABLE_NAME").equalsIgnoreCase( table))
					return true;
			}
		}
		finally {
			rs.close();
		}
		return false;
	}

	public static boolean ColumnInTableExist( Connection connection, String table, String column) throws SQLException
	{
		DatabaseMetaData meta = connection.getMetaData();
		ResultSet rs = meta.getColumns( connection.getCatalog(), "%", table, "%");
		try {
			while( rs.next())
			{
				if( rs.getString( "COLUMN_NAME").equalsIgnoreCase( column))
					return true;
			}
		}
		finally {
			rs.close();
		}
		return false;
	}

	private int m_DbType = DB_UNDETERMINED;
	public Connection connection = null;
	private Statement st = null;
	private boolean m_bCloseConnection = false;

	public DatabaseHelper( Connection c, boolean closeAfterUse)
	{
		connection = c;
		try {
			if( connection != null)
			{
				m_DbType = DatabaseHelper.GetDbType( connection);
				st = connection.createStatement();
			}
		} catch( Exception e)
		{	e.printStackTrace();	}
		m_bCloseConnection = closeAfterUse;
	}

	public void close()
	{
		try {
			if( st != null)
				st.close();
			if( m_bCloseConnection && connection != null)
				connection.close();
		} catch( Exception e)
		{	e.printStackTrace();	}
		connection = null;
	}

	public int GetDbType()
	{
		return m_DbType;
	}

	public int GetLastInsertId() throws SQLException
	{
		Statement st = connection.createStatement();
		ResultSet rs = null;
		try {
			switch( m_DbType)
			{
				case DB_MYSQL:
					rs = st.executeQuery( "select LAST_INSERT_ID()");
					break;
				case DB_SQLSERVER:
					rs = st.executeQuery( "SELECT @@identity as ID");
					break;
			}
			if( rs != null && rs.next())
				return rs.getInt( 1);
		}
		finally
		{
			if( rs != null)
				rs.close();
			st.close();
		}
		return -1;
	}

	public void Lock( String table) throws SQLException
	{
		try {
			switch( m_DbType)
			{
				case DB_MYSQL:
					st.execute( "Lock tables " + table + " write");
					break;
				case DB_SQLSERVER:
					st.execute( "BEGIN TRAN " + table);
					break;
			}
		}
		finally
		{		}
	}

	public void Lock( String []tables) throws SQLException
	{
		try {
			switch( m_DbType)
			{
				case DB_MYSQL:
					 String sql = "Lock tables ";
					 for( int i = 0; i < tables.length; ++i)
						  sql += tables[ i] + " write" + ( i<tables.length-1 ? "," : "");
					 st.execute( sql);
					 break;
				case DB_SQLSERVER:
					 st.execute( "BEGIN TRAN " + tables[0]);
					 break;
			}
		}
		finally
		{		}
	}

	public String AddExtraTableLock() throws SQLException
	{
		try {
			switch( m_DbType)
			{
				case DB_SQLSERVER:
					 return " WITH (TABLOCK,HOLDLOCK) ";
			}
		}
		finally	{ 	}
		return " ";
	}

	public void Unlock( String table)
	{
		try {
			try {
				switch( m_DbType)
				{
					case DB_MYSQL:
						st.execute( "Unlock tables");
						break;
					case DB_SQLSERVER:
						st.execute( "COMMIT TRAN " + table);
						break;
				}
			}
			finally
			{			}
		}
		catch( SQLException e)
		{
			e.printStackTrace();
		}
	}


}
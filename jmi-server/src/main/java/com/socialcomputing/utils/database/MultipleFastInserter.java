package com.socialcomputing.utils.database;

import java.sql.SQLException;
import java.sql.Statement;
/**
 * Title:        Database Tool
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VOYEZ VOUS
 * @author
 * @version 1.0
 */

public class MultipleFastInserter implements iFastInsert
{
	private DatabaseHelper databaseHelper = null;
	private String insertCommand = null;
	private boolean bFirstLine = true, bFirstArg = true;
	private StringBuffer insertBuffer = null;

	public MultipleFastInserter( DatabaseHelper c, String insert) throws SQLException
	{
		databaseHelper = c;
		insertCommand = insert;
		insertBuffer = new StringBuffer( 30*1024);
		insertBuffer.append( insertCommand);
	}

	public void startLine()
	{
		if( !bFirstLine)
			insertBuffer.append( ',');
		else
			bFirstLine = false;
		insertBuffer.append( '(');
	}

	private void addParam()
	{
		if( !bFirstArg)
			insertBuffer.append( ',');
		else
			bFirstArg = false;
	}

	public void addParam( String val)
	{
		addParam();
		insertBuffer.append( '\'');
		insertBuffer.append( val);
		insertBuffer.append( '\'');
	}
	public void addParam( int val)
	{
		addParam();
		insertBuffer.append( String.valueOf( val));
	}
	public void addParam( float val)
	{
		addParam();
		insertBuffer.append( String.valueOf( val));
	}

	public void endLine() throws java.sql.SQLException
	{
		insertBuffer.append( ')');
		bFirstArg = true;

		// SQL Server ne supporte pas les insertions multiples
		if( insertBuffer.length() > 29*1024 || databaseHelper.GetDbType() == DatabaseHelper.DB_SQLSERVER)
			doInsert();
	}

	public void insertAll() throws java.sql.SQLException
	{
		doInsert();
	}

	private void doInsert() throws java.sql.SQLException
	{
		if( !bFirstLine)
		{   // Send update now (la taille de la requete est limitée)
			Statement st = databaseHelper.connection.createStatement();
			st.executeUpdate( insertBuffer.toString());
			st.close();
			insertBuffer.delete( 0, insertBuffer.length());
			bFirstLine = true;
			insertBuffer.append( insertCommand);
		}
	}
}
package com.socialcomputing.utils.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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

public class FileFastInserter implements iFastInsert
{
	private DatabaseHelper databaseHelper = null;
	private boolean bFirstArg = true;
	private String  table = null;
	private File              insertFile = null;
	private BufferedWriter    insertWriter = null;

	private FileFastInserter( DatabaseHelper c, String t, File f) throws SQLException, Exception
	{
		databaseHelper = c;
		table = t;
		insertFile = f;
		insertWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( insertFile)), 60*1024);
	}
	public FileFastInserter( DatabaseHelper c, String t, String extension) throws SQLException, Exception
	{
		this( c, t, File.createTempFile( "MapStan", extension));
	}
	public FileFastInserter( DatabaseHelper c, String t, String extension, String tmp) throws SQLException, Exception
	{
		this( c, t, File.createTempFile( "MapStan", extension, new File( tmp)));
	}

	public void startLine() throws java.lang.Exception
	{
	}

	private void addParam() throws java.lang.Exception
	{
		if( !bFirstArg)
			insertWriter.write( '\t');
		else
			bFirstArg = false;
	}

	public void addParam( String val) throws java.lang.Exception
	{
		addParam();
		//if( databaseHelper.GetDbType() == DatabaseHelper.DB_MYSQL)
		//	insertWriter.write( '\'');
		insertWriter.write( val);
		//if( databaseHelper.GetDbType() == DatabaseHelper.DB_MYSQL)
		//	insertWriter.write( '\'');
	}
	public void addParam( int val) throws java.lang.Exception
	{
		addParam();
		insertWriter.write( String.valueOf( val));
	}
	public void addParam( float val) throws java.lang.Exception
	{
		addParam();
		insertWriter.write( String.valueOf( val));
	}

	public void endLine() throws java.lang.Exception
	{
		insertWriter.write( "\r\n");
		bFirstArg = true;
	}

	public void insertAll() throws java.lang.Exception
	{
		insertWriter.close();
		Statement st = databaseHelper.connection.createStatement();
		switch( databaseHelper.GetDbType())
		{
			case DatabaseHelper.DB_MYSQL:
				 st.execute( "LOAD DATA LOCAL INFILE '"+ insertFile.getPath().replace( '\\', '/') + "' INTO TABLE " + table);
				 break;
			case DatabaseHelper.DB_SQLSERVER:
				 st.execute( "BULK INSERT " + table + " from '"+ insertFile.getPath().replace( '\\', '/') + "' WITH (TABLOCK, FIELDTERMINATOR = '\t', ROWTERMINATOR = '\r\n')");
				 break;
		}
		st.close();
		insertFile.delete();
	}
}
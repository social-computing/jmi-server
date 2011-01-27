package com.socialcomputing.utils.database;

/**
 * Title:        Database Tool
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VOYEZ VOUS
 * @author
 * @version 1.0
 */

public interface iFastInsert
{
	public void startLine() throws java.lang.Exception;
	public void addParam( String val) throws java.lang.Exception;
	public void addParam( int val) throws java.lang.Exception;
	public void addParam( float val) throws java.lang.Exception;
	public void endLine() throws java.lang.Exception;

	public void insertAll() throws java.lang.Exception;
}
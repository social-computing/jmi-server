package com.socialcomputing.wps.server.generator;

/**
 * <p>Title: ProtoObject</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class ProtoObject
{
	/**
	 * Identifier of the Object in the Dictionary.
	 */
	public String       m_strId     = null;

	/**
	 * Handles type identifier or flags.
	 */
	public int          m_type      = 0;

	/***
	 * Number which identify Attribute in an array
	 */
	public int          m_num       = -1;

	public ProtoObject( String strId, int type )
	{
		m_strId = strId;
		m_type  = type;
	}

	public ProtoObject( String strId, int type, int num )
	{
		this( strId, type );
		m_num   = num;
	}

	public String getStrId()
	{
		return m_strId;
	}
}
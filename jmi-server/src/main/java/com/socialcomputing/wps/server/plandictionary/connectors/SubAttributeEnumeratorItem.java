package com.socialcomputing.wps.server.plandictionary.connectors;

/**
 * Title:        WPS Connectors
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class SubAttributeEnumeratorItem implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8085872941465851083L;
	public String  m_Id = null;
	public float   m_Ponderation = 0;

	public SubAttributeEnumeratorItem()
	{
	}

	public SubAttributeEnumeratorItem( String id, float pond)
	{
		m_Id = id;
		m_Ponderation = pond;
	}
}
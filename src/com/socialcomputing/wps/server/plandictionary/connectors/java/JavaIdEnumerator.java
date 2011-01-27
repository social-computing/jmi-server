package com.socialcomputing.wps.server.plandictionary.connectors.java;

import java.util.Enumeration;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.IdEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class JavaIdEnumerator implements iIdEnumerator
{
	private Enumeration m_enum = null;

	public JavaIdEnumerator( Hashtable table)
	{
		m_enum = table.keys();
	}

	public void reset()
	{
	}

	public void next( IdEnumeratorItem item)
	{
		item.m_Id = (String) m_enum.nextElement();
	}

	public boolean hasNext()
	{
		return m_enum.hasMoreElements();
	}
}

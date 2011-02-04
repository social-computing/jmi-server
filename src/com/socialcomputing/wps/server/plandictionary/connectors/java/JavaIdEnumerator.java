package com.socialcomputing.wps.server.plandictionary.connectors.java;

import java.util.Enumeration;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class JavaIdEnumerator implements iEnumerator<String>
{
	private Enumeration<String> m_enum = null;

	public JavaIdEnumerator( Hashtable table)
	{
		m_enum = table.keys();
	}

	public void reset()
	{
	}

	@Override
	public iEnumerator<String> iterator() {
		return this;
	}
	
	@Override
	public String next()
	{
		return m_enum.nextElement();
	}

	@Override
	public boolean hasNext()
	{
		return m_enum.hasMoreElements();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
}

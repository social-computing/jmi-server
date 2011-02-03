package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import com.socialcomputing.wps.server.plandictionary.connectors.*;
//import java.io.*;
import java.util.*;
import org.jdom.*;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class XmlIdEnumerator implements iIdEnumerator
{
	private String m_IdAttribute = null;
	private List m_lst = null;
	private int m_size = 0;
	private int m_index = 0;

	public XmlIdEnumerator( List lst, String idAttribute)
	{
		m_lst = lst;
		m_size = lst.size();
		m_IdAttribute = idAttribute;
	}

	public void reset()
	{
		m_index = 0;
	}

	@Override
	public iIdEnumerator iterator() {
		return this;
	}
	
	@Override
	public String next()
	{
		Element e = ( Element) m_lst.get( m_index++);
		return e.getAttributeValue( m_IdAttribute);
	}

	@Override
	public boolean hasNext()
	{
		return m_index < m_size;
	}

	@Override
	public void remove() {
	}
}

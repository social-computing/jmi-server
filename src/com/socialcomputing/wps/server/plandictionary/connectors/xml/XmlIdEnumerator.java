package com.socialcomputing.wps.server.plandictionary.connectors.xml;

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

	public void next( IdEnumeratorItem item)
	{
		Element e = ( Element) m_lst.get( m_index++);
		item.m_Id = e.getAttributeValue( m_IdAttribute);
	}

	public boolean hasNext()
	{
		return m_index < m_size;
	}
}

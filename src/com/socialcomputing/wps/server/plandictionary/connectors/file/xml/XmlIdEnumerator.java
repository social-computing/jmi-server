package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import java.util.List;

import org.jdom.Element;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class XmlIdEnumerator extends XmlEnumerator<String>
{
	private String m_IdAttribute = null;

	public XmlIdEnumerator( List<Element> lst, String idAttribute)
	{
		super( lst);
		m_IdAttribute = idAttribute;
	}

	@Override
	public String getItem() {
		Element e = m_lst.get( m_index++);
		return e.getAttributeValue( m_IdAttribute);
	}
}

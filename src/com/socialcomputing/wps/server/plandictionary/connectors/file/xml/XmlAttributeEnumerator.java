package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import java.util.List;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;

public class XmlAttributeEnumerator extends XmlEnumerator<AttributeEnumeratorItem> {

	private String m_IdAttribute = null;

	public XmlAttributeEnumerator( List<Element> lst, String idAttribute) {
		super( lst);
		m_IdAttribute = idAttribute;
	}
	
	@Override
	public AttributeEnumeratorItem getItem() {
		Element e = m_lst.get( m_index++);
		return new AttributeEnumeratorItem( e.getAttributeValue( m_IdAttribute), 1);
	}

}

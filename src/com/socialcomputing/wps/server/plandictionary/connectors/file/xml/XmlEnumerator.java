package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import java.util.List;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;

public abstract class XmlEnumerator<E> implements iEnumerator<E> {
	protected List<Element> m_lst = null;
	protected int m_size = 0;
	protected int m_index = 0;

	public XmlEnumerator( List<Element> lst)
	{
		m_lst = lst;
		m_size = lst.size();
	}

	public void reset()
	{
		m_index = 0;
	}

	@Override
	public iEnumerator<E> iterator() {
		return this;
	}

	public abstract E getItem();
	
	@Override
	public E next()
	{
		return getItem();
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

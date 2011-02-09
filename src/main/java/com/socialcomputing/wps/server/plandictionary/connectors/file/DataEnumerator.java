package com.socialcomputing.wps.server.plandictionary.connectors.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;

public class DataEnumerator<E> implements iEnumerator<E> {

	protected Collection<E> 	m_Collection;
	protected Iterator<E>		m_Iterator;
	
	public DataEnumerator(Collection<E> m_Collection) {
		super();
		this.m_Collection = m_Collection;
		this.m_Iterator =  m_Collection.iterator();
	}

	public DataEnumerator() {
		super();
		this.m_Collection = new ArrayList<E>(0);
		this.m_Iterator =  m_Collection.iterator();
	}
	
	@Override
	public Iterator<E> iterator() {
		m_Iterator = m_Collection.iterator();
		return this;
	}

	@Override
	public boolean hasNext() {
		return m_Iterator.hasNext();
	}
	
	@Override
	public E next() {
		return m_Iterator.next();
	}

	@Override
	public void remove() {
	}

}

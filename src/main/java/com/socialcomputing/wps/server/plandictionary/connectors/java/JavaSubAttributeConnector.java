package com.socialcomputing.wps.server.plandictionary.connectors.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import com.socialcomputing.wps.server.plandictionary.connectors.SubAttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;

public class JavaSubAttributeConnector implements iSubAttributeConnector, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5485343662719037175L;
	public static final String s_Name = "JavaProfileConnector";
	public static final String s_Description = "";

	private transient PlanData m_planData = null;

	public JavaSubAttributeConnector()
	{
	}

	public boolean openConnections( PlanData planData)
	{
		m_planData = planData;
		return true;
	}

	// iSubAttributeConnector interface
	public  String getName()
	{
		return s_Name;
	}

	public  String getDescription()
	{
		return s_Description;
	}

	public iEnumerator<SubAttributeEnumeratorItem> getEnumerator( String entity, String attribute)
	{
		return new JavaSubAttributeEnumerator( m_planData.getAttribute( attribute).getSubAttributes());
	}

	public Hashtable getProperties( String subAttributeId, String attributeId, String entityId )
	{
		return m_planData.getSubAttribute( subAttributeId).getProperties();
	}

	// INNER CLASS	JavaSubAttributeEnumerator
	public class JavaSubAttributeEnumerator implements iEnumerator<SubAttributeEnumeratorItem>
	{
		private ArrayList<SubAttributeEnumeratorItem> m_links = null;
		private int i = 0, max = 0;
		public JavaSubAttributeEnumerator( ArrayList<SubAttributeEnumeratorItem> links)
		{
			m_links = links;
			max = m_links.size();
		}

		@Override
		public Iterator<SubAttributeEnumeratorItem> iterator() {
			return this;
		}

		public void reset()
		{
		}

		@Override
		public SubAttributeEnumeratorItem next() {
			return m_links.get( i++);
		}

		@Override
		public boolean hasNext()
		{
			return i < max;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
	}
}

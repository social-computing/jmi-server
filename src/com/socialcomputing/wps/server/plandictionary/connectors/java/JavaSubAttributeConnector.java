package com.socialcomputing.wps.server.plandictionary.connectors.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.SubAttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeEnumerator;

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

	public iSubAttributeEnumerator getEnumerator( String entity, String attribute)
	{
		return new JavaSubAttributeEnumerator( m_planData.getAttribute( attribute).getSubAttributes());
	}

	public Hashtable getProperties( String subAttributeId, String attributeId, String entityId )
	{
		return m_planData.getSubAttribute( subAttributeId).getProperties();
	}

	// INNER CLASS	JavaSubAttributeEnumerator
	public class JavaSubAttributeEnumerator implements iSubAttributeEnumerator
	{
		private ArrayList m_links = null;
		private int i = 0, max = 0;
		public JavaSubAttributeEnumerator( ArrayList links)
		{
			m_links = links;
			max = m_links.size();
		}

		public void reset()
		{
		}

		public void next( SubAttributeEnumeratorItem item)
		{
			SubAttributeEnumeratorItem sitem = ( SubAttributeEnumeratorItem)m_links.get( i++);
			item.m_Id = sitem.m_Id;
			item.m_Ponderation = sitem.m_Ponderation;
		}

		public boolean hasNext()
		{
			return i < max;
		}
	}
}

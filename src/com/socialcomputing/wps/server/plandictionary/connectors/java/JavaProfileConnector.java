package com.socialcomputing.wps.server.plandictionary.connectors.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.SubAttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAttributeEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeConnector;

public class JavaProfileConnector implements iProfileConnector, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7253322586487844920L;
	public static final String s_Name = "JavaProfileConnector";
	public static final String s_Description = "";

	private transient PlanData m_planData = null;

	private Hashtable  m_Selections = null;
	private JavaSubAttributeConnector m_SubAttributeConnector = null;

	public JavaProfileConnector()
	{
		m_SubAttributeConnector = new JavaSubAttributeConnector();
	}

	// iProfileConnector interface
	public  String getName()
	{
		return s_Name;
	}

	public  String getDescription()
	{
		return s_Description;
	}

	public boolean openConnections( PlanData planData)
	{
		m_planData = planData;
		return m_SubAttributeConnector.openConnections( planData);
	}

	public iAttributeEnumerator getEnumerator( String entityId ) throws WPSConnectorException
	{
		return new JavaAttributeEnumerator( m_planData.getEntityLinks( entityId));
	}

	public Hashtable getAnalysisProperties( String attributeId, String entityId)
	{
		return m_planData.getAttribute( attributeId).getAnalysisProperties();
	}

	public iIdEnumerator getExclusionEnumerator( String entityId)
	{
		return new JavaIdEnumerator( new Hashtable()); // Pas d'exclusions
	}

	public Hashtable getProperties( String attributeId, boolean bInBase, String entityId) throws WPSConnectorException
	{
		PlanData.Attribute attribute = m_planData.getAttribute( attributeId);
		Hashtable properties = attribute.getProperties();
		// Ajout des propri�t�s des sous attributs sous forme de tableaux
		iSubAttributeConnector subConnector = this.getSubAttribute();
		int subAttCount = attribute.getSubAttributes().size();
		int max = attribute.getSubAttributes().size();
		for( int i = 0; i < max; ++i)
		{
			String sid = ((SubAttributeEnumeratorItem)(attribute.getSubAttributes().get(i))).m_Id;
			Hashtable subProperties = subConnector.getProperties( sid, attributeId, entityId);
			Set s = subProperties.entrySet();
			for( Iterator it = s.iterator(); it.hasNext(); )
			{
				Map.Entry entry = ( Map.Entry) it.next();
				String name = ( String)entry.getKey();
				String value = ( String)entry.getValue();
				String []data = ( String [])properties.get( name);
				if( data == null)
				{
					data = new String[ subAttCount];
					properties.put( name, data);
				}
				data[i] = value;
			}
		}
		return properties;
	}

	public iSubAttributeConnector getSubAttribute()
	{
		return m_SubAttributeConnector;
	}

	public Collection getSelections()
	{
		return m_Selections.values();
	}

	public iSelectionConnector getSelection( String selectionId)
	{
		return ( iSelectionConnector) m_Selections.get( selectionId);
	}

	// INNER CLASS	JavaAttributeEnumerator
	public class JavaAttributeEnumerator implements iAttributeEnumerator
	{
		private ArrayList<AttributeEnumeratorItem> m_links = null;
		private int i = 0, max = 0;
		public JavaAttributeEnumerator( ArrayList<AttributeEnumeratorItem> links)
		{
			m_links = links;
			max = (m_links == null ? 0 : m_links.size());
		}

		@Override
		public Iterator<AttributeEnumeratorItem> iterator() {
			return this;
		}

		public void reset()
		{
		}

		@Override
		public AttributeEnumeratorItem next() 
		{
			return m_links.get( i++);
		}

		public boolean hasNext()
		{
			return i < max;
		}

		@Override
		public void remove() {
		}
	}
}

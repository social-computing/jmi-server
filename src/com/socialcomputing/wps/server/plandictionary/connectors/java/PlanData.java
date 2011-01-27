package com.socialcomputing.wps.server.plandictionary.connectors.java;

import java.io.*;
import java.util.*;
import org.jdom.*;

import com.socialcomputing.wps.server.plandictionary.connectors.*;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class PlanData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -965798939084363473L;
	public static final String ENTITY = "entity";
	public static final String ATTRIBUTE = "attribute";
	public static final String ANALYSIS_PROPERTY = "analysis";
	public static final String LINK_SUBATT = "link";
	public static final String LINK_SUBATT_ATTRIBUTE_PROPERTY = "sub-a";
	public static final String LINK_SUBATT_PONDERATION_PROPERTY = "p";
	public static final String SUBATTRIBUTE = "sub-attribute";
	public static final String LINK = "link";
	public static final String LINK_ENTITY_PROPERTY = "e-id";
	public static final String LINK_ATTRIBUTE_PROPERTY = "a-id";
	public static final String LINK_PONDERATION_PROPERTY = "p";

	static PlanData readObject( org.jdom.Element root)
	{
		PlanData planData = new PlanData();
		planData.m_EntityIdProperty = root.getAttributeValue( "EntityIdProperty");;
		planData.m_AttributeIdProperty = root.getAttributeValue( "AttributeIdProperty");;
		planData.m_SubAttributeIdProperty = root.getAttributeValue( "SubAttributeIdProperty");
		if( planData.m_EntityIdProperty == null) planData.m_EntityIdProperty = "id";
		if( planData.m_AttributeIdProperty == null) planData.m_AttributeIdProperty = "id";
		if( planData.m_SubAttributeIdProperty == null) planData.m_SubAttributeIdProperty = "id";

		// ENTITES
		java.util.List lst = root.getChildren( ENTITY);
		int size = lst == null ? 0 : lst.size();
		for( int i = 0; i < size; ++i)
		{
			Element elem =  ( org.jdom.Element)lst.get( i);
			// Cr�ation de l'entit�
			Entity entity = planData.createEntity( elem.getAttributeValue( planData.m_EntityIdProperty));
			List atts = elem.getAttributes();
			int attsize = atts == null ? 0 : atts.size();
			for( int j = 0; j < attsize; ++j)
			{
				org.jdom.Attribute a = (org.jdom.Attribute) atts.get( j);
				entity.addProperty( a.getName(), a.getValue());
			}
		}
		// ATTRIBUTS
		lst = root.getChildren( ATTRIBUTE);
		size = lst == null ? 0 : lst.size();
		for( int i = 0; i < size; ++i)
		{
			Element elem =  ( org.jdom.Element)lst.get( i);
			// Cr�ation de l'attribut
			Attribute attribute = planData.createAttribute( elem.getAttributeValue( planData.m_AttributeIdProperty));
			// Ajout des propri�t�s simples
			List atts = elem.getAttributes();
			int attsize = atts == null ? 0 : atts.size();
			for( int j = 0; j < attsize; ++j)
			{
				org.jdom.Attribute a = (org.jdom.Attribute) atts.get( j);
				attribute.addProperty( a.getName(), a.getValue());
			}
			// Parcours des sous �l�ments
			Hashtable<String, Object> complexProperties = new Hashtable<String, Object>();
			java.util.List lst2 = elem.getChildren();
			int size2 = lst2 == null ? 0 : lst2.size();
			for( int j = 0; j < size2; ++j)
			{
				Element subelem = ( org.jdom.Element)lst2.get( j);
				if( subelem.getName().equals( ANALYSIS_PROPERTY))
				{   // Ajout des propri�t�s d'analyse
					atts = subelem.getAttributes();
					attsize = atts == null ? 0 : atts.size();
					for( int k = 0; k < attsize; ++k)
					{
						org.jdom.Attribute a = (org.jdom.Attribute) atts.get( k);
						attribute.addAnalysisProperties( a.getName(), a.getValue());
					}
				}
				else if( subelem.getName().equals( LINK_SUBATT))
				{ // Ajout des liens de sous-attributs
					String ponderation = subelem.getAttributeValue( LINK_SUBATT_PONDERATION_PROPERTY);
					attribute.addSubAttribute( subelem.getAttributeValue( LINK_SUBATT_ATTRIBUTE_PROPERTY), ( ponderation == null ? (float)1 : Float.parseFloat( ponderation)));
				}
				else
				{   // Complex properties
					String name = subelem.getName();
					ArrayList props = ( ArrayList)complexProperties.get( name);
					if( props == null)
					{
						props = new ArrayList();
						complexProperties.put( name, props);
					}
					props.add( subelem.getText());
				}
			}
			if( complexProperties.size() > 0)
			{
				for( Enumeration enumvar = complexProperties.keys(); enumvar.hasMoreElements(); )
				{
					String propName = ( String) enumvar.nextElement();
					ArrayList props = ( ArrayList)complexProperties.get( propName);
					attribute.addProperty( propName, props.toArray( new String[0]));
				}
			}
		}
		// SOUS-ATTRIBUTS
		lst = root.getChildren( SUBATTRIBUTE);
		size = lst == null ? 0 : lst.size();
		for( int i = 0; i < size; ++i)
		{
			Element elem =  ( org.jdom.Element)lst.get( i);
			// Cr�ation de l'attribut
			SubAttribute subattribute = planData.createSubAttribute( elem.getAttributeValue( planData.m_SubAttributeIdProperty));
			// Ajout des propri�t�s
			List atts = elem.getAttributes();
			int attsize = atts == null ? 0 : atts.size();
			for( int j = 0; j < attsize; ++j)
			{
				org.jdom.Attribute a = (org.jdom.Attribute) atts.get( j);
				subattribute.addProperty( a.getName(), a.getValue());
			}
		}
		// LIENS ENTITES-ATTRIBUTS
		lst = root.getChildren( LINK);
		size = lst == null ? 0 : lst.size();
		for( int i = 0; i < size; ++i)
		{
			Element elem =  ( org.jdom.Element)lst.get( i);
			Entity entity = planData.createEntity( elem.getAttributeValue( LINK_ENTITY_PROPERTY));
			String ponderation = elem.getAttributeValue( LINK_PONDERATION_PROPERTY);
			entity.addLink( elem.getAttributeValue( LINK_ATTRIBUTE_PROPERTY), ( ponderation == null ? (float) 1 : Float.parseFloat( ponderation)));
		}

		return planData;
	}

	public String m_EntityIdProperty = null;
	public String m_AttributeIdProperty = null;
	public String m_SubAttributeIdProperty = null;

	private Hashtable<String, Entity> m_Entities = new Hashtable<String, Entity>();
	private Hashtable<String, Attribute> m_Attributes = new Hashtable<String, Attribute>();
	private Hashtable<String, SubAttribute> m_SubAttributes = new Hashtable<String, SubAttribute>();

	public class Entity implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8224413670171038624L;
		public String id = null;
		private Hashtable<String, Object> properties = new Hashtable<String, Object>();
		private ArrayList<AttributeEnumeratorItem> AttLinks = new ArrayList<AttributeEnumeratorItem>();
		public Entity( String id)
		{
			this.id = id;
		}
		public Hashtable<String, Object> getProperties()
		{
			return properties;
		}
		public void addProperty( String name, Object value)
		{
			properties.put( name, value);
		}
		public ArrayList<AttributeEnumeratorItem> getLinks()
		{
			return AttLinks;
		}
		public void addLink( String attributeId, float ponderation)
		{
			AttributeEnumeratorItem item = new AttributeEnumeratorItem();
			item.m_Id = attributeId;
			item.m_Ponderation = ponderation;
			AttLinks.add( item);
		}
	}
	public class Attribute implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2826717930486701728L;
		public String id = null;
		private Hashtable<String, Object> properties = new Hashtable<String, Object>();
		private Hashtable<String, Object> analysisProperties = new Hashtable<String, Object>();
		private ArrayList<SubAttributeEnumeratorItem> subAttLinks = new ArrayList<SubAttributeEnumeratorItem>();
		public Attribute( String id)
		{
			this.id = id;
		}
		public Hashtable<String, Object> getProperties()
		{
			return properties;
		}
		public Object getProperty( String name)
		{
			return properties.get( name);
		}
		public void addProperty( String name, Object value)
		{
			properties.put( name, value);
		}
		public Hashtable<String, Object> getAnalysisProperties()
		{
			return analysisProperties;
		}
		public void addAnalysisProperties( String name, Object value)
		{
			analysisProperties.put( name, value);
		}
		public ArrayList<SubAttributeEnumeratorItem> getSubAttributes()
		{
			return subAttLinks;
		}
		public void addSubAttribute( String subAttributeId, float ponderation)
		{
			SubAttributeEnumeratorItem item = new SubAttributeEnumeratorItem();
			item.m_Id = subAttributeId;
			item.m_Ponderation = ponderation;
			subAttLinks.add( item);
		}
	}
	public class SubAttribute implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1932334092995466475L;
		public String id = null;
		private Hashtable<String, Object> properties = new Hashtable<String, Object>();
		public SubAttribute( String id)
		{
			this.id = id;
		}
		public Hashtable<String, Object> getProperties()
		{
			return properties;
		}
		public void addProperty( String name, Object value)
		{
			properties.put( name, value);
		}
	}
	public Entity createEntity( String id)
	{
		Entity entity = (Entity) m_Entities.get( id);
		if( entity == null)
		{
			entity = new Entity( id);
			m_Entities.put( id, entity);
		}
		return entity;
	}
	public Attribute createAttribute( String id)
	{
		Attribute attribute = (Attribute) m_Attributes.get( id);
		if( attribute == null)
		{
			attribute = new Attribute( id);
			m_Attributes.put( id, attribute);
		}
		return attribute;
	}
	public SubAttribute createSubAttribute( String id)
	{
		SubAttribute subattribute = (SubAttribute) m_SubAttributes.get( id);
		if( subattribute == null)
		{
			subattribute = new SubAttribute( id);
			m_SubAttributes.put( id, subattribute);
		}
		return subattribute;
	}

	public Hashtable<String, Entity> getEntities( )
	{
		return m_Entities;
	}
	public Entity getEntity( String entityId) throws WPSConnectorException
	{
		if( entityId == null) return null;
		Entity entity = ( Entity) m_Entities.get( entityId);
		if( entity == null) throw new WPSConnectorException( "Java connector : Entity '" + entityId + "' not found");
		return entity;
	}
	public Hashtable<String, Attribute> getAttributes( )
	{
		return m_Attributes;
	}
	public Attribute getAttribute( String attributeId)
	{
		return ( Attribute) m_Attributes.get( attributeId);
	}
	public SubAttribute getSubAttribute( String subAttributeId)
	{
		return ( SubAttribute) m_SubAttributes.get( subAttributeId);
	}
	public ArrayList<AttributeEnumeratorItem> getEntityLinks( String entityId) throws WPSConnectorException
	{
		if( entityId == null) return null;
		return getEntity( entityId).getLinks();
	}
}
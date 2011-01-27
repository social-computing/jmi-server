package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.socialcomputing.wps.server.plandictionary.connectors.MultiAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.MultiProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;

public class JDBCEntityConnector implements iEntityConnector, Serializable
{
	static final long serialVersionUID = 7560468521228097945L;

	public String m_Name = null;
	public String m_Description = null;

	public JDBCConnectionProfile m_ConnectionProfile = null;
	private transient Connection m_Connection = null;

	// JDBCAffinityGroupReader TreeMap, <default> classifier is mandatory !!!!
	public TreeMap m_AffinityGroupReaders = null;

	// JDBCProfileConnector TreeMap, <default> classifier is mandatory !!!!
	public TreeMap m_Profiles = null;

	// JDBCEntityClassifier TreeMap, <default> classifier with one rule is mandatory !!!!
	public TreeMap m_Classifiers = null;

	// JDBCSelectionConnector TreeMap
	public Hashtable  m_Selections = null;

	// Propriétés
	public JDBCProperties m_Properties = null;

	// Stockage propriétés (pour accélération)
	public transient Hashtable m_StockedProperties = null;

	static JDBCEntityConnector readObject( org.jdom.Element element)
	{
		JDBCEntityConnector connector = new JDBCEntityConnector( element.getAttributeValue( "name"));
		connector.m_Description = element.getChildText( "comment");
		// Connection
		connector.m_ConnectionProfile = JDBCConnectionProfile.readObject( element.getChild( "JDBC-connection"));
		//if( connector.m_ConnectionProfile == null) Exception;

		{     // Default Classifier
			  // Il est identifié car la propriété name == null (transformé en "<default>")
			  JDBCEntityClassifier cl = JDBCEntityClassifier.readObject( element.getChild( "JDBC-default-classifier"));
			  connector.m_Classifiers.put( cl.m_Name, cl);
		}
		{     // Others Classifiers
			List lst = element.getChildren( "JDBC-classifier");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				  JDBCEntityClassifier cl = JDBCEntityClassifier.readObject( ( org.jdom.Element) lst.get( i));
				  //if( cl.m_Name.equalsIgnoreCase( "<default>") Exception
				  connector.m_Classifiers.put( cl.m_Name, cl);
			}
		}

		// Properties
		connector.m_Properties = JDBCProperties.readObject( JDBCProperties.ENTITY_PROPS, element);

		{   // Affinity Groups
			List lst = element.getChildren( "JDBC-affinity-reader");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				JDBCAffinityGroupReader grp = JDBCAffinityGroupReader.readObject(  ( org.jdom.Element) lst.get( i));
				connector.m_AffinityGroupReaders.put( grp.m_Name, grp);
			}
		}

		{   // Profiles
			List lst = element.getChildren( "JDBC-attributes");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				JDBCProfileConnector prof = JDBCProfileConnector.readObject(  ( org.jdom.Element) lst.get( i));
				connector.m_Profiles.put( prof.m_Name, prof);
			}
		}

		{   // Sélections
			List lst = element.getChildren( "JDBC-selection");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				JDBCSelectionConnector sel = JDBCSelectionConnector.readObject(  JDBCSelectionConnector.ENTITY_SELECTION, ( org.jdom.Element) lst.get( i));
				connector.m_Selections.put( sel.m_Name, sel);
			}
		}
		return connector;
	}

	public JDBCEntityConnector(String name)
	{
		m_Name = name;
		m_Classifiers = new TreeMap();
		m_Profiles = new TreeMap();
		m_Selections = new Hashtable();
		m_Properties = new JDBCProperties();
		m_AffinityGroupReaders = new TreeMap();
	}

	// iEntityConnector interface
	public  String getName()
	{
		return m_Name;
	}

	public  String getDescription()
	{
		return m_Description;
	}

	public void openConnections( Hashtable wpsparams) throws WPSConnectorException
	{
		m_Connection = m_ConnectionProfile.getConnection();
		Iterator it = null;
		if( m_AffinityGroupReaders.size() > 0)
		{
			it = m_AffinityGroupReaders.values().iterator();
			while( it.hasNext())
			{
				JDBCAffinityGroupReader j = ( JDBCAffinityGroupReader) it.next();
				j.openConnections( wpsparams, m_Connection);
			}
		}
		it = m_Profiles.values().iterator();
		while( it.hasNext())
		{
			JDBCProfileConnector j = ( JDBCProfileConnector) it.next();
			j.openConnections( wpsparams, m_Connection);
		}
		it = m_Classifiers.values().iterator();
		while( it.hasNext())
		{
			JDBCEntityClassifier j = ( JDBCEntityClassifier) it.next();
			j.openConnections( wpsparams, m_Connection);
		}
		it = m_Selections.values().iterator();
		while( it.hasNext())
		{
			JDBCSelectionConnector j = ( JDBCSelectionConnector) it.next();
			j.openConnections( wpsparams, m_Connection);
		}
		m_Properties.openConnections( wpsparams, m_Connection);
		m_StockedProperties = new Hashtable();
	}

	public void closeConnections() throws WPSConnectorException
	{
		try {
			Iterator it = null;
			if( m_AffinityGroupReaders.size() > 0)
			{
				it = m_AffinityGroupReaders.values().iterator();
				while( it.hasNext())
				{
					JDBCAffinityGroupReader j = ( JDBCAffinityGroupReader) it.next();
					j.closeConnections();
				}
			}
			it = m_Profiles.values().iterator();
			while( it.hasNext())
			{
				JDBCProfileConnector j = ( JDBCProfileConnector) it.next();
				j.closeConnections();
			}
			it = m_Classifiers.values().iterator();
			while( it.hasNext())
			{
				JDBCEntityClassifier j = ( JDBCEntityClassifier) it.next();
				j.closeConnections();
			}
			it = m_Selections.values().iterator();
			while( it.hasNext())
			{
				JDBCSelectionConnector j = ( JDBCSelectionConnector) it.next();
				j.closeConnections();
			}
			m_Properties.closeConnections();
			m_StockedProperties = null;
			m_Connection.close();
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "JDBC connector can't close JDBCEntityConnector connection", e);
		}
	}

/**
  * Load the entity properties (image, age, income, ...). */
	public  Hashtable getProperties( String entityId ) throws WPSConnectorException
	{
		Hashtable table = ( Hashtable) m_StockedProperties.get( entityId);
		if(table == null)
		{
			table = new Hashtable();
			m_Properties.getProperties( table, entityId, true, null, null);
			m_StockedProperties.put( entityId, table);
		}
		return table;
	}

   public iIdEnumerator getEnumerator() throws WPSConnectorException
   {
		JDBCEntityClassifier classifier = ( JDBCEntityClassifier) m_Classifiers.get( "<default>");
		JDBCRuleConnector rule = ( JDBCRuleConnector) classifier.getRules().iterator().next(); // first and unique rule
		return rule.getEnumerator();
   }

   public Collection getAffinityGroupReaders()
   {
		return m_AffinityGroupReaders.values();
   }

	public iAffinityGroupReader getAffinityGroupReader( String full_reader) throws WPSConnectorException
	{
		if( full_reader.indexOf( '=') == -1 && full_reader.indexOf( '&') == -1)
			// Cas fréquent : attributs homogènes
			return ( JDBCAffinityGroupReader) m_AffinityGroupReaders.get( full_reader);
		// Attributes hétérogènes
		return MultiAffinityGroupReader.GetMultiAffinityGroupReader( this, full_reader);
	}

/**
  * Retrieve a collection of interface iProfileConnector    */
	public  Collection getProfiles()
	{
	  return m_Profiles.values();
	}

/*
	full_profile = profile [&profile]*
	profile = jdbcname[=prefix]
	*/
	public  iProfileConnector getProfile( String full_profile ) throws WPSConnectorException
	{
		if( full_profile.indexOf( '=') == -1 && full_profile.indexOf( '&') == -1)
			// Cas fréquent : attributs homogènes
			return ( JDBCProfileConnector) m_Profiles.get( full_profile);
		// Attributes hétérogènes
		return MultiProfileConnector.getProfile( this, full_profile );
	}

	public Collection getClassifiers()
	{
	  return m_Classifiers.values();
	}

	public iClassifierConnector getClassifier( String classifier)
	{
	  return ( JDBCEntityClassifier) m_Classifiers.get( classifier);
	}

	public Collection getSelections()
	{
		return m_Selections.values();
	}

	public iSelectionConnector getSelection( String selectionId)
	{
		return ( JDBCSelectionConnector) m_Selections.get( selectionId);
	}
}

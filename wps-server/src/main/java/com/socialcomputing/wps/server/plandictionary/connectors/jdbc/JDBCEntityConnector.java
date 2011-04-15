package com.socialcomputing.wps.server.plandictionary.connectors.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.MultiAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.MultiProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierRuleConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;

public class JDBCEntityConnector implements iEntityConnector, Serializable {
	static final long serialVersionUID = 7560468521228097945L;

	public String m_Name = null;
	public String m_Description = null;

	public JDBCConnectionProfile m_ConnectionProfile = null;
	private transient Connection m_Connection = null;

	// JDBCAffinityGroupReader TreeMap, <default> classifier is mandatory !!!!
	public TreeMap<String, JDBCAffinityGroupReader> m_AffinityGroupReaders = null;

	// JDBCProfileConnector TreeMap, <default> classifier is mandatory !!!!
	public TreeMap<String, JDBCProfileConnector> m_Profiles = null;

	// JDBCEntityClassifier TreeMap, <default> classifier with one rule is
	// mandatory !!!!
	public TreeMap<String, JDBCEntityClassifier> m_Classifiers = null;

	// JDBCSelectionConnector TreeMap
	public Hashtable<String, JDBCSelectionConnector> m_Selections = null;

	// Proprietes
	public JDBCProperties m_Properties = null;

	// Stockage proprietes (pour acceleration)
	public transient Hashtable<String, Hashtable<String, Object>> m_StockedProperties = null;

	static JDBCEntityConnector readObject(org.jdom.Element element) {
		JDBCEntityConnector connector = new JDBCEntityConnector(element.getAttributeValue("name"));
		connector.m_Description = element.getChildText("comment");
		// Connection
		connector.m_ConnectionProfile = JDBCConnectionProfile.readObject(element.getChild("JDBC-connection"));
		// if( connector.m_ConnectionProfile == null) Exception;

		{ // Default Classifier
			// Il est identifie car la propriete name == null (transforme en
			// "<default>")
			JDBCEntityClassifier cl = JDBCEntityClassifier.readObject(element.getChild("JDBC-default-classifier"));
			connector.m_Classifiers.put(cl.m_Name, cl);
		}
		{ // Others Classifiers
			List lst = element.getChildren("JDBC-classifier");
			int size = lst.size();
			for (int i = 0; i < size; ++i) {
				JDBCEntityClassifier cl = JDBCEntityClassifier.readObject((org.jdom.Element) lst.get(i));
				// if( cl.m_Name.equalsIgnoreCase( "<default>") Exception
				connector.m_Classifiers.put(cl.m_Name, cl);
			}
		}

		// Properties
		connector.m_Properties = JDBCProperties.readObject(JDBCProperties.ENTITY_PROPS, element);

		{ // Affinity Groups
			List lst = element.getChildren("JDBC-affinity-reader");
			int size = lst.size();
			for (int i = 0; i < size; ++i) {
				JDBCAffinityGroupReader grp = JDBCAffinityGroupReader.readObject((org.jdom.Element) lst.get(i));
				connector.m_AffinityGroupReaders.put(grp.m_Name, grp);
			}
		}

		{ // Profiles
			List lst = element.getChildren("JDBC-attributes");
			int size = lst.size();
			for (int i = 0; i < size; ++i) {
				JDBCProfileConnector prof = JDBCProfileConnector.readObject((org.jdom.Element) lst.get(i));
				connector.m_Profiles.put(prof.m_Name, prof);
			}
		}

		{ // S�lections
			List lst = element.getChildren("JDBC-selection");
			int size = lst.size();
			for (int i = 0; i < size; ++i) {
				JDBCSelectionConnector sel = JDBCSelectionConnector.readObject(JDBCSelectionConnector.ENTITY_SELECTION,
						(org.jdom.Element) lst.get(i));
				connector.m_Selections.put(sel.m_Name, sel);
			}
		}
		return connector;
	}

	public JDBCEntityConnector(String name) {
		m_Name = name;
		m_Classifiers = new TreeMap<String, JDBCEntityClassifier>();
		m_Profiles = new TreeMap<String, JDBCProfileConnector>();
		m_Selections = new Hashtable<String, JDBCSelectionConnector>();
		m_Properties = new JDBCProperties();
		m_AffinityGroupReaders = new TreeMap<String, JDBCAffinityGroupReader>();
	}

	@Override
	public String getName() {
		return m_Name;
	}

	@Override
	public String getDescription() {
		return m_Description;
	}

	@Override
	public void openConnections( int planType,Hashtable<String, Object> wpsparams) throws WPSConnectorException {
		m_Connection = m_ConnectionProfile.getConnection();
		if (m_AffinityGroupReaders.size() > 0) {
			for (JDBCAffinityGroupReader j : m_AffinityGroupReaders.values()) {
				j.openConnections(wpsparams, m_Connection);
			}
		}
		for (JDBCProfileConnector j : m_Profiles.values()) {
			j.openConnections(wpsparams, m_Connection);
		}
		for (JDBCEntityClassifier j : m_Classifiers.values()) {
			j.openConnections(wpsparams, m_Connection);
		}
		for (JDBCSelectionConnector j : m_Selections.values()) {
			j.openConnections(wpsparams, m_Connection);
		}
		m_Properties.openConnections(wpsparams, m_Connection);
		m_StockedProperties = new Hashtable<String, Hashtable<String, Object>>();
	}

	public void closeConnections() throws WPSConnectorException {
		try {
			if (m_AffinityGroupReaders.size() > 0) {
				for (JDBCAffinityGroupReader j : m_AffinityGroupReaders.values()) {
					j.closeConnections();
				}
			}
			for (JDBCProfileConnector j : m_Profiles.values()) {
				j.closeConnections();
			}
			for (JDBCEntityClassifier j : m_Classifiers.values()) {
				j.closeConnections();
			}
			for (JDBCSelectionConnector j : m_Selections.values()) {
				j.closeConnections();
			}
			m_Properties.closeConnections();
			m_StockedProperties = null;
			m_Connection.close();
		} catch (SQLException e) {
			throw new WPSConnectorException("JDBC connector can't close JDBCEntityConnector connection", e);
		}
	}

	/**
	 * Load the entity properties (image, age, income, ...).
	 */
	@Override
	public Hashtable<String, Object> getProperties(String entityId) throws WPSConnectorException {
		Hashtable<String, Object> table = m_StockedProperties.get( entityId);
		if (table == null) {
			table = new Hashtable<String, Object>();
			m_Properties.getProperties(table, entityId, true, null, null);
			m_StockedProperties.put(entityId, table);
		}
		return table;
	}

	@Override
	public iEnumerator<String> getEnumerator() throws WPSConnectorException {
		JDBCEntityClassifier classifier = m_Classifiers.get(WPSDictionary.DEFAULT_NAME);
		iClassifierRuleConnector rule = classifier.getRules().iterator().next(); 
		// first and unique rule
		return rule.iterator();
	}

	@Override
	public Collection getAffinityGroupReaders() {
		return m_AffinityGroupReaders.values();
	}

	@Override
	public iAffinityGroupReader getAffinityGroupReader(String full_reader) throws WPSConnectorException {
		if (full_reader.indexOf('=') == -1 && full_reader.indexOf('&') == -1) {
			// Cas frequent : attributs homogenes
			return (JDBCAffinityGroupReader) m_AffinityGroupReaders.get(full_reader);
		}
		// Attributes heterogenes
		return MultiAffinityGroupReader.GetMultiAffinityGroupReader(this, full_reader);
	}

	/**
	 * Retrieve a collection of interface iProfileConnector
	 */
	@Override
	public Collection getProfiles() {
		return m_Profiles.values();
	}

	/*
	 * full_profile = profile [&profile]* profile = jdbcname[=prefix]
	 */
	@Override
	public iProfileConnector getProfile(String full_profile) throws WPSConnectorException {
		if (full_profile.indexOf('=') == -1 && full_profile.indexOf('&') == -1)
			// Cas fréquent : attributs homogenes
			return (JDBCProfileConnector) m_Profiles.get(full_profile);
		// Attributes h�t�rogenes
		return MultiProfileConnector.getProfile(this, full_profile);
	}

	@Override
	public Collection getClassifiers() {
		return m_Classifiers.values();
	}

	@Override
	public iClassifierConnector getClassifier(String classifier) {
		return (JDBCEntityClassifier) m_Classifiers.get(classifier);
	}

	@Override
	public Collection getSelections() {
		return m_Selections.values();
	}

	@Override
	public iSelectionConnector getSelection(String selectionId) {
		return (JDBCSelectionConnector) m_Selections.get(selectionId);
	}
}

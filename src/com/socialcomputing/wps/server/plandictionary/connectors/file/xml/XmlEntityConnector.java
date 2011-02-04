package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.file.FileEntityConnector;

/**
 * <p>
 * Title: WPS Connectors
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: MapStan
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class XmlEntityConnector extends FileEntityConnector {
	protected	Element m_Root = null;
	protected	String m_EntityId = null, m_EntityMarkup = null, m_AttributeId = null, m_AttributeMarkup = null;
	protected 	Set<String> entityProperties = new HashSet<String>();
	protected 	Set<String> attributeProperties = new HashSet<String>();
	protected 	List<XmlAffinityGroupReader> 	affinityGroupReaders = new ArrayList<XmlAffinityGroupReader>();
	protected 	List<XmlProfileConnector> 		profileConnectors = new ArrayList<XmlProfileConnector>();
	
	static XmlEntityConnector readObject(org.jdom.Element element) {
		XmlEntityConnector connector = new XmlEntityConnector( element.getAttributeValue("name"));
		connector._readObject( element);
		
		Element entity = element.getChild( "XML-entity");
		connector.m_EntityMarkup = entity.getAttributeValue( "markup");
		connector.m_EntityId = entity.getAttributeValue( "id");

		Element attribute = element.getChild( "XML-attribute");
		connector.m_AttributeMarkup = attribute.getAttributeValue( "markup");
		connector.m_AttributeId = attribute.getAttributeValue( "id");
	
		connector.affinityGroupReaders.add( XmlAffinityGroupReader.readObject( element));
		connector.profileConnectors.add( XmlProfileConnector.readObject( element));
		return connector;
	}

	public XmlEntityConnector(String name) {
		super(name);
	}

	@Override
	public void openConnections(Hashtable<String, Object> wpsparams) throws WPSConnectorException {
		super.openConnections(wpsparams);
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(false);
			org.jdom.Document doc = builder.build(m_Stream);
			m_Root = doc.getRootElement();
		} catch (Exception e) {
		}
		List<Element> entities = m_Root.getChildren( m_EntityMarkup);
		for( XmlAffinityGroupReader affinityGroupReader : affinityGroupReaders) {
			affinityGroupReader.openConnections( wpsparams, m_EntityId, entities); 
		}
	}

	@Override
	public void closeConnections() throws WPSConnectorException {
		super.closeConnections();
		for( XmlAffinityGroupReader affinityGroupReader : affinityGroupReaders) {
			affinityGroupReader.closeConnections();
		}
	}

	/**
	 * Load the entity properties (image, age, income, ...).
	 */
	@Override
	public Hashtable<String, Object> getProperties(String entityId) {
		Hashtable<String, Object> table = new Hashtable<String, Object>();
		return table;
	}

	@Override
	public iEnumerator<String> getEnumerator() {
		XmlIdEnumerator enumvar = new XmlIdEnumerator( m_Root.getChildren("o1"), "id");
		return enumvar;
	}

	@Override
	public Collection getAffinityGroupReaders() {
		return affinityGroupReaders;
	}

	@Override
	public iAffinityGroupReader getAffinityGroupReader(String affGrpReader) {
		return affinityGroupReaders.get( 0);
	}

	@Override
	public Collection getProfiles() {
		return profileConnectors;
	}

	@Override
	public iProfileConnector getProfile(String profile) {
		return profileConnectors.get( 0);
	}

	@Override
	public Collection<iClassifierConnector> getClassifiers() {
		return null;
	}

	@Override
	public iClassifierConnector getClassifier(String classifier) {
		return null;
	}

	@Override
	public Collection<iSelectionConnector> getSelections() {
		return null;
	}

	@Override
	public iSelectionConnector getSelection(String selectionId) {
		return null;
	}
}
package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;
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
	protected	String entityId = null, attributeId = null;
	protected 	Set<String> entityProperties = new HashSet<String>();
	protected 	Set<String> attributeProperties = new HashSet<String>();
	
	static XmlEntityConnector readObject(org.jdom.Element element) {
		XmlEntityConnector connector = new XmlEntityConnector(element.getAttributeValue("name"));
		connector._readObject(element);
		
		
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
	}

	@Override
	public void closeConnections() throws WPSConnectorException {
		super.closeConnections();
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
	public iIdEnumerator getEnumerator() {
		XmlIdEnumerator enumvar = new XmlIdEnumerator( m_Root.getChildren("o1"), "id");
		return enumvar;
	}

	@Override
	public Collection<iAffinityGroupReader> getAffinityGroupReaders() {
		return null;
	}

	@Override
	public iAffinityGroupReader getAffinityGroupReader(String affGrpReader) {
		return null;
	}

	@Override
	public Collection<iProfileConnector> getProfiles() {
		return null;
	}

	@Override
	public iProfileConnector getProfile(String profile) {
		return null;
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
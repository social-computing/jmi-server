package com.socialcomputing.wps.server.plandictionary.connectors.datastore.file.xml;

import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.AttributePropertyDefinition;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Entity;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.file.FileEntityConnector;

public class XmlEntityConnector extends FileEntityConnector {
	protected	Element m_Root = null;
	protected	String m_EntityId = null, m_EntityMarkup = null, m_AttributeId = null, m_AttributeMarkup = null;
	
	static XmlEntityConnector readObject(org.jdom.Element element) {
		XmlEntityConnector connector = new XmlEntityConnector( element.getAttributeValue("name"));
		connector._readObject( element);
		
		Element entity = element.getChild( "XML-entity");
		connector.m_EntityMarkup = entity.getAttributeValue( "markup");
		connector.m_EntityId = entity.getAttributeValue( "id");
		for( Element property: (List<Element>)entity.getChildren( "XML-property")) {
			connector.entityProperties.add( property.getAttributeValue( "id"));
		}

		Element attribute = element.getChild( "XML-attribute");
		connector.m_AttributeMarkup = attribute.getAttributeValue( "markup");
		connector.m_AttributeId = attribute.getAttributeValue( "id");
		for( Element property: (List<Element>)attribute.getChildren( "XML-property")) {
			connector.attributeProperties.add( new AttributePropertyDefinition( property.getAttributeValue( "id"), property.getAttributeValue( "entity")));
		}
	
		return connector;
	}

	public XmlEntityConnector(String name) {
		super(name);
	}

	@Override
	public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
		super.openConnections( planType, wpsparams);
		try {
			// TODO SAX Parser => faster
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(false);
			org.jdom.Document doc = builder.build(m_Stream);
			m_Root = doc.getRootElement();
		} catch (Exception e) {
		}
		for( Element el: (List<Element>)m_Root.getChildren( m_EntityMarkup)) {
			Entity entity = addEntity( el.getAttributeValue( m_EntityId));
			for( String property : entityProperties) {
				entity.addProperty( property, el.getAttributeValue( property));
			}
			
			for( Element el2: (List<Element>)el.getChildren( m_AttributeMarkup)) {
				Attribute attribute = addAttribute( el2.getAttributeValue( m_AttributeId));
				entity.addAttribute( attribute, 1);
			}
		}
		for( Element el: (List<Element>)m_Root.getChildren( m_AttributeMarkup)) {
			Attribute attribute = addAttribute( el.getAttributeValue( m_AttributeId));
			for( AttributePropertyDefinition property : attributeProperties) {
				if( property.isSimple()) {
					attribute.addProperty( property, el.getAttributeValue( property.getName()));
				}
			}
			addEntityProperties( attribute);
		}
	}

	@Override
	public void closeConnections() throws WPSConnectorException {
		super.closeConnections();
	}

}
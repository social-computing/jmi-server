package com.socialcomputing.wps.server.plandictionary.connectors.datastore.file.rest;

import java.util.Hashtable;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.AttributePropertyDefinition;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Entity;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.file.FileEntityConnector;

public class RESTEntityConnector extends FileEntityConnector {
    protected   String m_Type = null;
    protected	String m_EntityId = null, m_EntityMarkup = null, m_AttributeId = null, m_AttributeMarkup = null;
	
	static RESTEntityConnector readObject(org.jdom.Element element) {
		RESTEntityConnector connector = new RESTEntityConnector( element.getAttributeValue("name"));
		connector._readObject( element);
		connector.m_Type = element.getAttributeValue( "type");
		
		Element entity = element.getChild( "REST-entity");
		connector.m_EntityMarkup = entity.getAttributeValue( "markup");
		connector.m_EntityId = entity.getAttributeValue( "id");
		for( Element property: (List<Element>)entity.getChildren( "REST-property")) {
			connector.entityProperties.add( property.getAttributeValue( "id"));
		}

		Element attribute = element.getChild( "REST-attribute");
		connector.m_AttributeMarkup = attribute.getAttributeValue( "markup");
		connector.m_AttributeId = attribute.getAttributeValue( "id");
		for( Element property: (List<Element>)attribute.getChildren( "REST-property")) {
			connector.attributeProperties.add( new AttributePropertyDefinition( property.getAttributeValue( "id"), property.getAttributeValue( "entity")));
		}
	
		return connector;
	}

	public RESTEntityConnector(String name) {
		super(name);
	}

	@Override
	public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
		super.openConnections( planType, wpsparams);
		String type = urlHelper.getContentType();
		if( type == null) {
		    type = m_Type;
		}
		if( type != null && (type.equalsIgnoreCase("json") || type.equalsIgnoreCase( MediaType.APPLICATION_JSON)))
            readJSON( planType, wpsparams);
		else
		    readXml( planType, wpsparams);
	}
	
    private void readJSON(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree( urlHelper.getStream());
            ArrayNode entities = (ArrayNode) node.get( m_EntityMarkup);
            if( entities != null) {
                for( JsonNode jsonentity: entities) {
                    Entity entity = addEntity( jsonentity.get( m_EntityId).getTextValue());
                    for( String property : entityProperties) {
                        entity.addProperty( property, jsonentity.get( property).getTextValue());
                    }
                    
                    ArrayNode attributes = (ArrayNode) jsonentity.get( m_AttributeMarkup);
                    if( attributes != null) {
                        for( JsonNode jsonattribute: attributes) {
                            Attribute attribute = addAttribute( jsonattribute.get( m_AttributeId).getTextValue());
                            entity.addAttribute( attribute, 1);
                        }
                    }
                }
            }
            ArrayNode attributes = (ArrayNode) node.get( m_AttributeMarkup);
            if( attributes != null) {
                for( JsonNode jsonattribute: attributes) {
                    Attribute attribute = addAttribute( jsonattribute.get( m_AttributeId).getTextValue());
                    for( AttributePropertyDefinition property : attributeProperties) {
                        if( property.isSimple()) {
                            attribute.addProperty( property, jsonattribute.get( property.getName()).getTextValue());
                        }
                    }
                    addEntityProperties( attribute);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new WPSConnectorException( "openConnections", e);
        }
    }
    
	private void readXml(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
	    Element root;
	    try {
			// TODO SAX Parser => faster
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(false);
			org.jdom.Document doc = builder.build( urlHelper.getStream());
			root = doc.getRootElement();
		} catch (Exception e) {
            throw new WPSConnectorException( "openConnections", e);
		}
		for( Element el: (List<Element>)root.getChildren( m_EntityMarkup)) {
			Entity entity = addEntity( el.getAttributeValue( m_EntityId));
			for( String property : entityProperties) {
				entity.addProperty( property, el.getAttributeValue( property));
			}
			
			for( Element el2: (List<Element>)el.getChildren( m_AttributeMarkup)) {
				Attribute attribute = addAttribute( el2.getAttributeValue( m_AttributeId));
				entity.addAttribute( attribute, 1);
			}
		}
		for( Element el: (List<Element>)root.getChildren( m_AttributeMarkup)) {
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
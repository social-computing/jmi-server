package com.socialcomputing.wps.server.plandictionary.connectors.datastore.feeds;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.DatastoreEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Entity;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.PropertyDefinition;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper;

public class FeedsEntityConnector extends DatastoreEntityConnector {
    protected   String m_InvertedDef = null;
    protected   List<UrlHelper> m_feeds = null;

	static FeedsEntityConnector readObject(org.jdom.Element element) {
		FeedsEntityConnector connector = new FeedsEntityConnector( element.getAttributeValue("name"));
		connector._readObject( element);
        connector.m_InvertedDef = element.getAttributeValue( "invert");

        List<Element> defs = (List<Element>)element.getChildren( UrlHelper.DTD_DEFINITION);
        connector.m_feeds = new ArrayList<UrlHelper>( defs.size());
        for( Element def : defs) {
            UrlHelper feed = new UrlHelper();
            feed.readObject( def);
            connector.m_feeds.add( feed);
        }
        connector.entityProperties.add( new PropertyDefinition( "poss_id", "id"));
        connector.entityProperties.add( new PropertyDefinition( "poss_name", "name"));
        connector.attributeProperties.add( new PropertyDefinition( "poss_id", "id"));
        connector.attributeProperties.add( new PropertyDefinition( "poss_name", "name"));

        return connector;
	}

	public FeedsEntityConnector(String name) {
		super(name);
	}

	@Override
	public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
		super.openConnections( planType, wpsparams);

		m_inverted =  UrlHelper.ReplaceParameter( m_InvertedDef, wpsparams).equalsIgnoreCase( "true");
		for( UrlHelper feed : m_feeds) {
		    feed.openConnections( planType, wpsparams);
	        readXml( feed, planType, wpsparams);
		}
	}
	

	private void readXml(UrlHelper feed, int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
	    Element root;
	    try {
			// TODO SAX Parser => faster
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(false);
			org.jdom.Document doc = builder.build( feed.getStream());
			root = doc.getRootElement();
		} catch (Exception e) {
            throw new WPSConnectorException( "openConnections", e);
		}
		for( Element item : (List<Element>)root.getChild("channel").getChildren( "item")) {
			Attribute attribute = addAttribute( item.getChildText( "link"));
			attribute.addProperty( "name", item.getChildText( "title"));
			
			for( Element category : (List<Element>)item.getChildren( "category")) {
				Entity entity = addEntity( category.getText());
				entity.addProperty( "name", entity.getId());
				entity.addAttribute( attribute, 1);
			}
		}
		for( Attribute attribute : m_Attributes.values()) {
			if( !isInverted())
			    addEntityProperties( attribute);
		}
        if( isInverted()) {
            for( Entity entity : m_Entities.values()) { 
                addAttributeProperties( entity);
            }
        }
	}

	@Override
	public void closeConnections() throws WPSConnectorException {
		super.closeConnections();
        for( UrlHelper feed : m_feeds) {
            feed.closeConnections();
        }
	}

}
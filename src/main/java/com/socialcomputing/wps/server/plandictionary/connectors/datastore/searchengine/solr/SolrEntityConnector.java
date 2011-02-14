package com.socialcomputing.wps.server.plandictionary.connectors.datastore.searchengine.solr;

import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Entity;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.searchengine.SearchengineEntityConnector;

public class SolrEntityConnector extends SearchengineEntityConnector {
	protected String m_QueryParam = null;

	static SolrEntityConnector readObject(org.jdom.Element element) {
		SolrEntityConnector connector = new SolrEntityConnector( element.getAttributeValue("name"));
		connector._readObject( element);
		connector.m_QueryParam = element.getAttributeValue( "queryParam");
		return connector;
	}
	
	public SolrEntityConnector(String name) {
		super(name);
	}

	@Override
	public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
		super.openConnections( planType, wpsparams);
		
		// Query
		String query = ( String)wpsparams.get( m_QueryParam);
		query = "test";

		// Mots clés
		Entity entity = addEntity( "1");
		entity.addProperty( "name", "Mot clé");
		
		// Pages résultat
		Attribute attribute = addAttribute( "1");
		attribute.addProperty( "name", "Page 1");
		entity.addAttribute( attribute, 1);

		attribute = addAttribute( "2");
		attribute.addProperty( "name", "Page 2");		
		entity.addAttribute( attribute, 1);

		
	}
	
	@Override
	public void closeConnections() throws WPSConnectorException {
		super.closeConnections();
	}
	
}

package com.socialcomputing.wps.server.plandictionary.connectors.datastore.searchengine.solr;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.http.BasicAuthHttpClient;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.AttributePropertyDefinition;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Entity;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.searchengine.SearchengineEntityConnector;

/**
 * 
 * @author Jonathan Dray <jonathan@social-computing.com>
 * @author Franck Valetas <franck@social-computing.com>
 *
 * Entity connector that queries a Solr search server.
 * 
 */
public class SolrEntityConnector extends SearchengineEntityConnector {

    private final static Logger LOG = LoggerFactory.getLogger(SolrEntityConnector.class);
    
	private final CommonsHttpSolrServer solrClient;
	private final QueryParameters queryParameters;
	private final String entityField;
	private final String attributeId;
	
	
	/**
	 * Static method to construct a SolrEntityConnector from a JDOM Tree
	 * Used to map the XML Configuration stored in database to a solr connector instance
	 * 
	 * @param element a JDOM element
	 * @return a SolrEntityConnector instance
	 * @throws WPSConnectorException 
	 */
	static SolrEntityConnector readObject(Element element) 
	        throws WPSConnectorException {

	    LOG.info("Reading Solr configuration from JDOM tree : {}", element.toString());
	    String name = element.getAttributeValue("name");
	    LOG.debug("  - name = {}", name);
	    

	    // Reading Solr connection element
	    Element connectionElement = element.getChild("connection");
	    String connectionUrl = connectionElement.getChildText("url");
	    LOG.debug("  - url = {}", connectionUrl);
	    Element authenticationElement = connectionElement.getChild("authentication");
	    CommonsHttpSolrServer solrClient;
	    try {
    	    if(authenticationElement != null) {
    	        String username = authenticationElement.getChildText("username");
    	        String password = authenticationElement.getChildText("password");
    	        LOG.debug("  - authentication with user '{}' and password '{}'",
    	                  username, password);
    	        solrClient = new CommonsHttpSolrServer(connectionUrl, 
                        new BasicAuthHttpClient(username, password));
    	    }
    	    else {
    	        LOG.debug("  - authentication is not set");
    	        solrClient = new CommonsHttpSolrServer(connectionUrl);
    	    }
	    } 
        catch (MalformedURLException e) {
            throw new WPSConnectorException("Invalid Solr connection URL in dictionary configuration file", e);
        }
        
        
        // Reading Solr query parameters names and default values
        Element queryElement = element.getChild("query");
        Element queryString = queryElement.getChild("query-string");
        Element maxResults = queryElement.getChild("max-results");
        QueryParameters queryParameters = 
            new QueryParameters(new QueryParameter<String>(queryString.getAttributeValue("param"),
                                                           queryString.getAttributeValue("default")),
                                new QueryParameter<Integer>(maxResults.getAttributeValue("param"),
                                                            Integer.valueOf(maxResults.getAttributeValue("default")))
                               );
        LOG.debug("  - query parameters= {}", queryParameters);
	    
	    
        // Reading entity and attribute definition
        Element entity = element.getChild("entity");
        Element attribute = element.getChild("attribute");
        String entityField = entity.getAttributeValue("field");
        String attributeId = attribute.getAttributeValue("id"); 
        
        // Initialize the Solr connector
		SolrEntityConnector connector 
		    = new SolrEntityConnector(
		            name,
		            solrClient,
		            queryParameters,
		            entityField,
		            attributeId);
		connector._readObject(element);

		// TODO : change this !
		for(Element property : (List<Element>) attribute.getChildren("property")) {
            connector.attributeProperties.add(
                    new AttributePropertyDefinition(property.getAttributeValue("id"),
                                                    property.getAttributeValue("field")));
        }
		
		return connector;
	}
	
	
	/**
	 * Default Constructor
	 * 
	 * @param name name of the connector instance
     * @param solrClient A Solr client instance
	 * @param queryParameters List of query parameters to read and their default values
	 */
	public SolrEntityConnector(String name,
	                           CommonsHttpSolrServer solrClient,
	                           QueryParameters queryParameters,
	                           String entityField,
	                           String attributeId) {
		super(name);
		this.solrClient = solrClient;
		this.queryParameters = queryParameters;
		this.entityField = entityField;
		this.attributeId = attributeId;
	}

	
	@Override
	public void openConnections(int planType, Hashtable<String, Object> wpsparams)
	        throws WPSConnectorException {
		
	    super.openConnections(planType, wpsparams);
	    LOG.info("Query remote search server to get attributes and entities");
	    
	    // Query parameters 
	    QueryParameter<String> queryString = this.queryParameters.getQueryString();
	    String query = (String) wpsparams.get(queryString.getName());
	    LOG.debug("  - query: {}", query);
	    
	    QueryParameter<Integer> maxResults = this.queryParameters.getMaxResults();
	    Integer max = Integer.valueOf((String) wpsparams.get(maxResults.getName()));
	    LOG.debug("  - max results: {}", max);
	    
	    SolrQuery solrQuery = 
	        new SolrQuery((query != null) ? query : queryString.getValue())
	            .setRows((max != null) ? max : maxResults.getValue());

	    // Query the solr server
	    QueryResponse response;
	    try {
            response = this.solrClient.query(solrQuery);
        } 
	    catch (SolrServerException e) {
            throw new WPSConnectorException("Erreur while connecting to Solr server", e);
        }

	    // Iterate through answers and populate entities and attributes
	    // For now, entities are values of a field type and attributes are documents matching the query
	    LOG.info("Loading elements and attributes");
	    
	    for(SolrDocument document : response.getResults()) {
	        
	        String attributeId = 
	            (String)document.getFieldValue(this.attributeId);
	        Attribute attribute = addAttribute(attributeId);
	        
	        // Mapping document properties to attribute properties
	        // TODO : change this !
	        for(AttributePropertyDefinition property : this.attributeProperties) {
	            String field;
                if(property.isSimple()) {
                    field = property.getName();
                }
                else {
                    field = property.getEntity();
                }
                String fieldValue = (String)document.getFieldValue(field);
                attribute.addProperty(property, (fieldValue == null) ? "" : fieldValue);
            }
	        LOG.debug("Adding an attribute : {}", attribute);
	        
	        // Get a list of elements from each document
	        Collection<Object> elements = document.getFieldValues(this.entityField);
	       
	        if(elements != null) {
    	        for(Object element : elements) {
    	            String elementStr = (String) element;
    	            Entity entity = addEntity(elementStr);
    	            
    	            // Sub optimal ? Erase entity name each time it is found in a document ... 
    	            // Use the tag value as the name to display
    	            entity.addProperty("name", elementStr);
    	            entity.addAttribute(attribute, 1);
    	            
    	            LOG.debug("Adding or updating an entity : {}", entity);
    	        }
	        }
	    }
	}
	
	
	@Override
	public void closeConnections() throws WPSConnectorException {
		super.closeConnections();
		// Nothing to do here as 
	}	
}

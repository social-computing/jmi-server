package com.socialcomputing.wps.server.plandictionary.connectors.datastore.searchengine.solr;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

    private static final Logger LOG = LoggerFactory.getLogger(SolrEntityConnector.class);
    private static final String INVERT_PARAM = "invert";
    
	private final CommonsHttpSolrServer solrClient;
	private final QueryParameters queryParameters;
	private final String entityField;
	private final String attributeId;
	private final boolean invert;

	
    /**
     * Static method to construct a SolrEntityConnector from a JDOM Tree used to
     * map the XML Configuration stored in database to a solr connector instance
     * 
     * @param element
     *            a JDOM element
     * @return a SolrEntityConnector instance
     * @throws WPSConnectorException
     */
	public static SolrEntityConnector readObject(Element element) 
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
        LOG.debug("  - query parameters = {}", queryParameters);
	    
        Element invertElement = element.getChild("invert");
        boolean invert = (invertElement == null ) ? false : true;
        LOG.debug("  - invert = {}", invert);
        
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
		            attributeId,
		            invert);
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
     * @param name
     *            name of the connector instance
     * @param solrClient
     *            A Solr client instance
     * @param queryParameters
     *            List of query parameters to read and their default values
     */
	public SolrEntityConnector(String name,
	                           CommonsHttpSolrServer solrClient,
	                           QueryParameters queryParameters,
	                           String entityField,
	                           String attributeId) {
		this(name, solrClient, queryParameters, entityField, attributeId, false);
	}
	
	
    /**
     * Constructor with inversion parameter
     * 
     * @param name
     *            name of the connector instance
     * @param solrClient
     *            A Solr client instance
     * @param queryParameters
     *            List of query parameters to read and their default values
     */
    public SolrEntityConnector(String name,
                               CommonsHttpSolrServer solrClient,
                               QueryParameters queryParameters,
                               String entityField,
                               String attributeId,
                               boolean invert) {
        super(name);
        this.solrClient = solrClient;
        this.queryParameters = queryParameters;
        this.entityField = entityField;
        this.attributeId = attributeId;
        this.invert = invert;
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
	    
	    Object invertParam = wpsparams.get(SolrEntityConnector.INVERT_PARAM);
	    boolean invert = (invertParam != null) ? (Boolean.parseBoolean((String) invertParam)) : this.invert;

	    LOG.debug("  - invert: {}", invert);
	    
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
	    LOG.info("Number of documents found : {} in {}", response.getResults().getNumFound(), response.getQTime());
	    
	       
	    for(SolrDocument document : response.getResults()) {
	        
	        String documentId = 
	            (String)document.getFieldValue(this.attributeId);
	        
	        // Reading document properties
            // TODO : change this, it shouldn't use AttributePropertyDefinition
	        LOG.debug("Reading document properties: ");
            Map<String, String> documentProperties = new HashMap<String, String>();
            for(AttributePropertyDefinition property : this.attributeProperties) {
                String field;
                if(property.isSimple()) {
                    field = property.getName();
                }
                else {
                    field = property.getEntity();
                }
                String fieldValue = (String)document.getFieldValue(field);
                documentProperties.put(property.getName(), (fieldValue == null) ? "" : fieldValue);
                LOG.debug(" - {} : {}", property.getName(), documentProperties.get(property.getName()));
            }
            
            // Get a list of relational field elements from each document
            Collection<Object> relFieldValues = document.getFieldValues(this.entityField);
	        
            if(invert) {
                Entity entity = addEntity(documentId);
                entity.addProperties(documentProperties);
                if(relFieldValues != null) {
                    for(Object relField : relFieldValues) {
                        String relFieldValue = (String) relField;
                        Attribute attribute = addAttribute(relFieldValue);

                        attribute.addProperty("name", relFieldValue);
                        entity.addAttribute(attribute, 1);
                    }
                }
                LOG.debug("Entity added: {}", entity);
            }
            else {
                Attribute attribute = addAttribute(documentId);
                attribute.addProperties(documentProperties);
                if(relFieldValues != null) {
                    for(Object relField : relFieldValues) {
                        String relFieldValue = (String) relField;
                        Entity entity = addEntity(relFieldValue);
                        
                        entity.addProperty("name", relFieldValue);
                        entity.addAttribute(attribute, 1);                        
                    }
                }
                LOG.debug("Attribute added: {}", attribute);
            }
	    }
	}
	
	
	@Override
	public void closeConnections() throws WPSConnectorException {
		super.closeConnections();
		// Nothing to do here as 
	}	
}
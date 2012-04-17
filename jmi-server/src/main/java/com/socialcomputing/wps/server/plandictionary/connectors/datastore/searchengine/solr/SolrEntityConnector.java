package com.socialcomputing.wps.server.plandictionary.connectors.datastore.searchengine.solr;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.http.BasicAuthHttpClient;
import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.planDictionnary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.planDictionnary.connectors.datastore.Entity;
import com.socialcomputing.wps.server.planDictionnary.connectors.datastore.PropertyDefinition;
import com.socialcomputing.wps.server.planDictionnary.connectors.utils.UrlHelper;
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
    private static final String SEARCH_DOCUMENT_ID_PARAM = "searchDocumentId";
    private static final String FILTER_QUERY_PARAM = "fq";
     
	//private final CommonsHttpSolrServer solrClient;
    private final String url;
	private final QueryParameters queryParameters;
	private final String entityField;
	private final String attributeId;
	private final boolean invert;

	private boolean authentication = false;
	private String username;
	private String password;
	
	private Set<PropertyDefinition> attributePropertiesDefinitions;
	private Set<PropertyDefinition> entityPropertiesDefinitions;
	

    /**
     * Static method to construct a SolrEntityConnector from a JDOM Tree used to
     * map the XML Configuration stored in database to a solr connector instance
     * 
     * @param element
     *            a JDOM element
     * @return a SolrEntityConnector instance
     * @throws JMIException
     */
	public static SolrEntityConnector readObject(Element element) 
	        throws JMIException {

	    LOG.info("Reading Solr configuration from JDOM tree : {}", element.toString());
	    String name = element.getAttributeValue("name");
	    LOG.debug("  - name = {}", name);
	    

	    // Reading Solr connection element
	    Element connectionElement = element.getChild("connection");
	    String connectionUrl = connectionElement.getChildText("url");
	    LOG.debug("  - url = {}", connectionUrl);
	    

        
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
	    
        // Reading invert parameter
        Element invertElement = element.getChild(INVERT_PARAM);
        boolean invert = (invertElement == null ) ? false : true;
        LOG.debug("  - invert = {}", invert);
        
        // Reading entity and attribute definition
        Element entity = element.getChild("entity");
        Element attribute = element.getChild("attribute");
        String entityField = entity.getAttributeValue("field");
        String attributeId = attribute.getAttributeValue("id"); 

        
        // Initialize the Solr connector
	    Element authenticationElement = connectionElement.getChild("authentication");
	    SolrEntityConnector connector;
	    if(authenticationElement != null) {
	        String username = authenticationElement.getChildText("username");
	        String password = authenticationElement.getChildText("password");
	        LOG.debug("  - authentication with user '{}' and password '{}'", username, password);
	        connector = new SolrEntityConnector(name, connectionUrl, username, password, queryParameters, entityField, attributeId, invert);
	    }
	    else {
	    	connector = new SolrEntityConnector(name, connectionUrl, queryParameters, entityField, attributeId, invert);
	    }
	    
		
		// Call the initialisation in parent classes
		connector._readObject(element);

		// Read attribute properties definitions
	    // If a propery definition contains an entity attribute, it refers to the id of the entity property to map 
		// to the attribute as a property
		for(Element property : (List<Element>) attribute.getChildren("property")) {
            if(property.getAttributeValue("entity") !=  null) {
                connector.attributeProperties.add(
                    new PropertyDefinition(property.getAttributeValue("id"),
                                                    property.getAttributeValue("entity")));    
            }
            else{
                addPropertyDefinition(connector.attributePropertiesDefinitions, 
                                      new PropertyDefinition(property.getAttributeValue("id"),
                                                             property.getAttributeValue("field")));
            }
        }
		
		// Read entity properties definitions
		for(Element property : (List<Element>) entity.getChildren("property")) {
		    addPropertyDefinition(connector.entityPropertiesDefinitions, 
                                  new PropertyDefinition(property.getAttributeValue("id"),
                                                         property.getAttributeValue("field")));
		}
		
		return connector;
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
                               String url,
                               QueryParameters queryParameters,
                               String entityField,
                               String attributeId,
                               boolean invert) {
        super(name);
        this.url = url;
        this.queryParameters = queryParameters;
        this.entityField = entityField;
        this.attributeId = attributeId;
        this.invert = invert;
        this.attributePropertiesDefinitions = new HashSet<PropertyDefinition>();
        this.entityPropertiesDefinitions = new HashSet<PropertyDefinition>();
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
                               String url,
                               String username,
                               String password,
                               QueryParameters queryParameters,
                               String entityField,
                               String attributeId,
                               boolean invert) {
    	this(name, url, queryParameters, entityField, attributeId, invert);
        this.authentication = true;
        this.username = username;
        this.password = password;
    }
	
  

    /**
     * Add a property definition 
     * 
     * @param definitions a set of properties defintions 
     * @param definition a property definition read from configuration
     */
    
    public static void addPropertyDefinition(Set<PropertyDefinition> definitions, PropertyDefinition definition) {
        if(definition == null) {
            throw new IllegalArgumentException("propertyDefinition");
        }
        definitions.add(definition);
    }
    

    
	@Override
	public void openConnections(int planType, Hashtable<String, Object> wpsparams)
	        throws JMIException {
		
	    super.openConnections(planType, wpsparams);
	    LOG.info("Query remote search server to get attributes and entities");
	    
	    
	    CommonsHttpSolrServer solrClient;
	    try {
		    String connectionUrl = UrlHelper.ReplaceParameter(this.url, wpsparams);
		    LOG.debug("  - connection url {}", connectionUrl);
	        if(this.authentication) {
	        	String username = UrlHelper.ReplaceParameter(this.username, wpsparams);
	        	String password = UrlHelper.ReplaceParameter(this.password, wpsparams);
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
	    	throw new JMIException("Invalid Solr connection URL", e);
	    }

	    
	    /*  Reading query parameters */
        Object invertParam = wpsparams.get(INVERT_PARAM);
        boolean invert = (invertParam != null) ? (Boolean.parseBoolean((String) invertParam)) : this.invert;
        LOG.debug("  - invert: {}", invert);
        
        String searchDocumentId = (String) wpsparams.get(SEARCH_DOCUMENT_ID_PARAM);
        LOG.debug("  - search document id: {}", searchDocumentId);
	    
        /*
        String rawQuery = null;
	    if(wpsparams.containsKey(RAW_QUERY_PARAM)) {
	         rawQuery = (String) wpsparams.get(RAW_QUERY_PARAM);
	    }
	    */
	    
	    QueryParameter<String> queryString = this.queryParameters.getQueryString();
	    String query = (String) wpsparams.get(queryString.getName());
	    LOG.debug("  - query: {}", query);
	    
	    QueryParameter<Integer> maxResults = this.queryParameters.getMaxResults();
	    Object maxResultsParam = wpsparams.get(maxResults.getName());
	    Integer max = (maxResultsParam != null) ?  Integer.valueOf((String) maxResultsParam) : maxResults.getValue(); 
        LOG.debug("  - max results: {}", max);
        
        String filterQuery = (String) wpsparams.get(FILTER_QUERY_PARAM);
        LOG.debug("  - filter query: {}", filterQuery);
	    


	    // Construct the query
	    SolrDocumentList documents;
        if (searchDocumentId != null) {
            SolrQuery solrQuery = new SolrQuery("uid:" + searchDocumentId)
                                      .setRows(1);
            
            SolrDocumentList docs = query(solrClient, solrQuery);
            if(docs.size() < 1) {
                throw new JMIException("Invalid number of results");
            }
            SolrDocument document = docs.get(0);
            
            solrQuery = 
                new SolrQuery().setQueryType("/" + MoreLikeThisParams.MLT);
                solrQuery.setRows(max)
                         .setQuery("uid:" + searchDocumentId)
                         .set(MoreLikeThisParams.MATCH_INCLUDE, true)
                         .set(MoreLikeThisParams.MIN_DOC_FREQ, 1)
                         .set(MoreLikeThisParams.MIN_TERM_FREQ, 1)
                         .set(MoreLikeThisParams.SIMILARITY_FIELDS, "title, content");
            documents = query(solrClient, solrQuery);
            documents.add(document);
        }
        else {
        	String finalQuery = (query != null) ? query : queryString.getValue();
            SolrQuery solrQuery = 
                new SolrQuery(ClientUtils.escapeQueryChars(finalQuery))
                    .setRows(max);
            if(filterQuery != null) {
                String[] filterQueries = filterQuery.split("\\|\\|");
                solrQuery.setFilterQueries(filterQueries);
            }
            documents = query(solrClient, solrQuery);
        }
	   

	    // Iterate through answers and populate entities and attributes
	    // Entities are values of a field type and attributes are documents matching the query
	    for(SolrDocument document : documents) {
	        String documentId = (String)document.getFieldValue(this.attributeId);
	        
	        // Read attribute properties
            Map<String, String> attributeProperties = new HashMap<String, String>();
            for(PropertyDefinition property : this.attributePropertiesDefinitions) {
                String field;
                if(property.isSimple()) {
                    field = property.getName();
                }
                else {
                    field = property.getId();
                }
                String fieldValue = (String)document.getFieldValue(field);
                attributeProperties.put(property.getName(), (fieldValue == null) ? "" : fieldValue);
            }
            
            // Get a list of entity values = list of relational field elements from each document
            Collection<Object> entityField = document.getFieldValues(this.entityField);
            ArrayList<Object> entityValues = null;
            Map<String, ArrayList<Object>> entityProperties = new HashMap<String, ArrayList<Object>>();
            
            // If the specified entity field contains values, also read the configured entity properties
            if(entityField != null) {
                entityValues = new ArrayList<Object>(entityField);
                
                // Read entity properties
                for(PropertyDefinition property : this.entityPropertiesDefinitions) {
                    String field;
                    if(property.isSimple()) {
                        field = property.getName();
                    }
                    else {
                        field = property.getId();
                    }
                    
                    // Make sure that the entity property has the same number of values as the entity itself 
                    Collection<Object> entityProperty = document.getFieldValues(field);
                    int size = (entityProperty == null) ? 0 : entityProperty.size();
                    if(entityProperty == null || entityProperty.size() != entityField.size()) {
                        throw new JMIException(field + "should have the same number of values as " + this.entityField 
                                                              + ", found: " + size + ", expected: " + entityField.size());
                    }
                    entityProperties.put(property.getName(), new ArrayList<Object>(entityProperty));
                }
            }

            if(invert) {
                Entity entity = addEntity(documentId);
                entity.addProperties(attributeProperties);
                if(entityValues != null) {
                    
                    for(int i = 0 ; i < entityValues.size() ; i++) {
                        String relFieldValue = (String) entityValues.get(i);
                        Attribute attribute = addAttribute(relFieldValue);
                        for(String attributeProperty : entityProperties.keySet()) {
                            attribute.addProperty(attributeProperty, entityProperties.get(attributeProperty).get(i));
                        }
                        LOG.debug("  - Attribute added: {}", attribute);
                        entity.addAttribute(attribute, 1);
                    }
                }
                LOG.debug("Entity added: {}", entity);
            }
            else {
                Attribute attribute = addAttribute(documentId);
                attribute.addProperties(attributeProperties);
                if(entityValues != null) {
                    for(int i = 0 ; i < entityValues.size() ; i++) {
                        String relFieldValue = (String) entityValues.get(i);
                        Entity entity = addEntity(relFieldValue);
                        for(String entityProperty : entityProperties.keySet()) {
                            entity.addProperty(entityProperty, entityProperties.get(entityProperty).get(i));
                        }
                        entity.addAttribute(attribute, 1);
                        LOG.debug("  - Entity added: {}", entity);
                    }
                }
                LOG.debug("Attribute added: {}", attribute);
            }
	    }
	    
	    // Set attribute properties (POSS_ID and POSS_NAME)
	    for(Attribute attribute : this.m_Attributes.values()) {
	        this.addEntityProperties(attribute);
	    }
	}
	
	
	@Override
	public void closeConnections() throws JMIException {
		super.closeConnections();
		// Nothing to do here
	}
	
	
	/**
	 * Query the Solr server  
	 * 
	 * @param solrQuery a valid Solr query
	 * @return A list of Solr documents
	 * @throws JMIException if something bad happened when connecting to the Solr server
	 */
	private static SolrDocumentList query(CommonsHttpSolrServer solrClient, SolrQuery solrQuery) 
			throws JMIException {
        QueryResponse response;
        try {
            response = solrClient.query(solrQuery);
        } 
        catch (SolrServerException e) {
            throw new JMIException("Erreur while connecting to Solr server", e);
        }
        LOG.info("{} documents found in {} seconds", response.getResults().getNumFound(), response.getQTime());
        return response.getResults();
	}
}

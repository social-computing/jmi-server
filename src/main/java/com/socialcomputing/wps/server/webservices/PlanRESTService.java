package com.socialcomputing.wps.server.webservices;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;

/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 * 
 *         Rest service to manipulate plans
 * 
 */
// Will be hosted at the URI path "/"
@Path("/")
public class PlanRESTService {

    private final static Logger LOG = LoggerFactory.getLogger(PlanRESTService.class);
    private final static ObjectMapper mapper = new ObjectMapper();

 
    /**
     * The method will process HTTP GET requests
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonGenerationException 
     * 
     */
    @GET 
    @Path("maps.json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMaps(
            @HeaderParam("User-Agent") String userAgent,
            @Context UriInfo ui) throws JsonGenerationException, JsonMappingException, IOException {
        LOG.info("REST API : /maps");
        ArrayNode jsonResults = mapper.createArrayNode();
        DictionaryManager manager = new DictionaryManagerImpl();
        for( Dictionary dictionary : manager.findAll()) {
            jsonResults.add( dictionary.getName());
        }
        return mapper.writeValueAsString(jsonResults);
    }

    /**
     * The method will process HTTP GET requests
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonGenerationException 
     * 
     */
    @GET 
    @Path("maps/{map}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMap(
            @HeaderParam("User-Agent") String userAgent,
            @PathParam("map") String planName,
            @Context UriInfo ui) throws JsonGenerationException, JsonMappingException, IOException {
        LOG.info("REST API : /maps");
        ObjectNode jsonResults = mapper.createObjectNode();
        DictionaryManager manager = new DictionaryManagerImpl();
        Dictionary dictionary  = manager.findByName( planName);
        jsonResults.put( "name", dictionary.getName());
        jsonResults.put( "definition", dictionary.getDefinition());
        return mapper.writeValueAsString(jsonResults);
    }
    
}
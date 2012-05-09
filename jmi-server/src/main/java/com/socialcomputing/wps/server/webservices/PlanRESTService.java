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
import org.slf4j.MDC;

import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;
import com.socialcomputing.wps.server.utils.log.DiagnosticContext;

/**
 * @author Jonathan Dray <jonathan.dray@social-computing.com>
 * @author Franck Valetas <franck.valetas@social-computing.com>
 * 
 * <p>
 * A RESTFul web service to manipulate map configurations
 * </p>
 * 
 */
@Path("/rest") // Will be hosted at the URI path "/rest"
public class PlanRESTService {

    private final static Logger LOG = LoggerFactory.getLogger(PlanRESTService.class);
    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * <p>
     * Show the list of configured maps
     * For now the result is serialized as JSON
     * </p>
     * <p>
     * The method will process HTTP GET requests
     * </p>
     * 
     * @param userAgent extracted from the request header as  {@link HeaderParam}
     * @param ui the request {@link Context} extracted by jersey 
     * @return a JSON array containing the found maps
     */
    @GET 
    @Path("maps.json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMaps(
            @HeaderParam("User-Agent") String userAgent,
            @Context UriInfo ui) 
            throws JsonGenerationException, JsonMappingException, IOException {
        // TODO : configure exception mapping
        MDC.put(DiagnosticContext.ENTRY_POINT_CTX.name, "GET /maps.json");
        try {
            ArrayNode jsonResults = mapper.createArrayNode();
            DictionaryManager manager = new DictionaryManagerImpl();
            for( Dictionary dictionary : manager.findAll()) {
                jsonResults.add( dictionary.getName());
            }            
            return mapper.writeValueAsString(jsonResults);
        }
        finally {
            MDC.remove(DiagnosticContext.ENTRY_POINT_CTX.name);
        }
    }

    
    /**
     * <p>
     * Show informations of a specific map
     * For now the result is serialized as JSON
     * The result is retuned as a JSON Object
     * </p>
     * <p>
     * The method will process HTTP GET requests
     * </p>
     * 
     * @param userAgent extracted from the request header as {@link HeaderParam}
     * @param ui the request {@link Context} extracted by jersey 
     * @param map the name of the map as a {@link PathParam}
     * @return a JSON object containing the map data
     */
    @GET 
    @Path("maps/{map}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMap(
            @HeaderParam("User-Agent") String userAgent,
            @PathParam("map") String map,
            @Context UriInfo ui)
                throws JsonGenerationException, JsonMappingException, IOException {
        // TODO : configure exception mapping
        MDC.put(DiagnosticContext.ENTRY_POINT_CTX.name, "GET /maps/" + map  + ".json");
        try {
            ObjectNode jsonResults = mapper.createObjectNode();
            DictionaryManager manager = new DictionaryManagerImpl();
            Dictionary dictionary  = manager.findByName(map);
            // TODO : if not dictionary is found return a 404
            jsonResults.put( "name", dictionary.getName());
            jsonResults.put( "definition", dictionary.getDefinition());
            return mapper.writeValueAsString(jsonResults);
        }
        finally {
            MDC.remove(DiagnosticContext.ENTRY_POINT_CTX.name);
        }    
    }
}
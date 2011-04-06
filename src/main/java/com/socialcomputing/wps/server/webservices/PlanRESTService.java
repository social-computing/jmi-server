package com.socialcomputing.wps.server.webservices;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;
import com.socialcomputing.wps.server.webservices.maker.BeanPlanMaker;
import com.socialcomputing.wps.server.webservices.maker.PlanMaker;

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

 
    /**
     * The method will process HTTP GET requests
     * 
     */
    @GET 
    @Path("maps.json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMaps(
            @HeaderParam("User-Agent") String userAgent,
            @Context UriInfo ui) {
        LOG.info("REST API : /maps");
        JSONArray jsonResults = new JSONArray();
        DictionaryManager manager = new DictionaryManagerImpl();
        for( Dictionary dictionary : manager.findAll()) {
            jsonResults.add( dictionary.getName());
        }
        return jsonResults.toJSONString();
    }

    /**
     * The method will process HTTP GET requests
     * 
     */
    @GET 
    @Path("maps/{map}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMap(
            @HeaderParam("User-Agent") String userAgent,
            @PathParam("map") String planName,
            @Context UriInfo ui) {
        LOG.info("REST API : /maps");
        JSONObject jsonResults = new JSONObject();
        DictionaryManager manager = new DictionaryManagerImpl();
        Dictionary dictionary  = manager.findByName( planName);
        jsonResults.put( "name", dictionary.getName());
        jsonResults.put( "definition", dictionary.getDefinition());
        return jsonResults.toJSONString();
    }
    
}
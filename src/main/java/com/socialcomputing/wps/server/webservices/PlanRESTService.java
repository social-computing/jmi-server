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

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.webservices.maker.BeanPlanMaker;
import com.socialcomputing.wps.server.webservices.maker.PlanMaker;

/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 * 
 *         Rest service to manipulate plans
 * 
 */
// Will be hosted at the URI path "/plan"
@Path("{account}/maps")
public class PlanRESTService {

    private final static Logger LOG = LoggerFactory.getLogger(PlanRESTService.class);

    /**
     * Map query parameters from a Jersey MultivaluedMap to an HashTable
     * 
     * @param ui UriInfo path and query information wrapper
     * @return HashTable of parameters
     */
    private Hashtable<String, Object> getQueryParameters(String userAgent, UriInfo ui) {
        MultivaluedMap<String, String> pathParams = ui.getQueryParameters();
        Hashtable<String, Object> queryParameters = new Hashtable<String, Object>();
        for(String key :  pathParams.keySet()) {
            LOG.info("  - query parameter {} = {}", key, pathParams.get(key).get(0));
            queryParameters.put(key, pathParams.get(key).get(0));
        }
        if( userAgent != null)
            queryParameters.put( "User-Agent", userAgent);
        return queryParameters;
    }

    /**
     * The method will process HTTP GET requests
     * 
     * @param planName name of the plan to get
     * @param ui
     * @return a plan result in Xml
     */
    @GET 
    @Path("{planName}")
    @Produces(MediaType.TEXT_XML)
    public String getPlan(
            @HeaderParam("User-Agent") String userAgent,
            @PathParam("account") String account,
            @PathParam("planName") String planName,
            @Context UriInfo ui) {
        return "";
    }

    /**
     * The method will process HTTP GET requests
     * 
     * @param planName name of the plan to get
     * @param ui
     * @return a plan result in Java serialized objects
     */
    @GET
    @Path("{planName}/java")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getBinaryPlan(
                                  @HeaderParam("User-Agent") String userAgent,
                                  @PathParam("account") String account,
                                  @PathParam("planName") String planName,
                                  @Context UriInfo ui) {
        LOG.info("Plan name = {}", planName);
        PlanMaker planMaker = new BeanPlanMaker();
        Hashtable<String, Object> planParameters = this.getQueryParameters( userAgent,ui);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream( baos);
        try {
            planParameters.put("planName", planName);
            planParameters.put( PlanMaker.PLAN_MIME, MediaType.APPLICATION_OCTET_STREAM);

            Hashtable<String, Object> results = planMaker.createPlan( planParameters);
            byte[] bplan = ( byte[])results.get( PlanMaker.PLAN);
            outputStream.writeInt( bplan.length);
            outputStream.write( bplan);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            try {
                outputStream.writeInt( -1);
                StringWriter    writer  = new StringWriter();
                e.printStackTrace( new PrintWriter( writer ));
                outputStream.writeUTF( writer.toString());
            }
            catch (IOException e1) {
            }
        }
        return Response.ok( baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM).build();
    }
    
    /**
     * The method will process HTTP GET requests
     * And will produce content identified by the JSON exchange format
     * 
     * @param planName name of the plan to get
     * @param ui
     * @return a plan result in JSON format
     */
    @GET 
    @Path("{planName}/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJSonPlan(
                              @HeaderParam("User-Agent") String userAgent,
                              @PathParam("account") String account,
                              @PathParam("planName") String planName,
                              @Context UriInfo ui) {
        LOG.info("Plan name = {}", planName);
        PlanMaker planMaker = new BeanPlanMaker();
        Hashtable<String, Object> planParameters = this.getQueryParameters(userAgent, ui);
        JSONObject jsonResults = new JSONObject();
        try {
            planParameters.put("planName", planName);
            planParameters.put( PlanMaker.PLAN_MIME, MediaType.APPLICATION_JSON);

            Hashtable<String, Object> wpsresults = planMaker.createPlan(planParameters);
            for( String key : wpsresults.keySet()) {
                jsonResults.put( key, wpsresults.get( key));
            }
        }
        catch (RemoteException e) {
            LOG.error(e.getMessage(), e);
            jsonResults.put( "name", planName);
            jsonResults.put( "error", e.getMessage());
        }
        return jsonResults.toJSONString();
    }

}
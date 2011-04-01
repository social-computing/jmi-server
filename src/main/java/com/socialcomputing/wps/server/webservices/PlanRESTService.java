package com.socialcomputing.wps.server.webservices;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.JsonNodeDeserializer;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.generator.PlanContainer;
import com.socialcomputing.wps.server.generator.ProtoPlan;
import com.socialcomputing.wps.server.generator.json.PlanJSONProvider;
import com.socialcomputing.wps.server.generator.json.impl.JacksonPlanJSONProvider;
import com.socialcomputing.wps.server.webservices.maker.BeanPlanMaker;

/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 * 
 *         Rest service to manipulate plans
 * 
 */
// Will be hosted at the URI path "/plan"
@Path("/plan/{planName}")
public class PlanRESTService {

    private final static Logger LOG = LoggerFactory.getLogger(PlanRESTService.class);

    /**
     * Map query parameters from a Jersey MultivaluedMap to an HashTable
     * 
     * @param ui UriInfo path and query information wrapper
     * @return HashTable of parameters
     */
    private Hashtable<String, Object> getQueryParameters(UriInfo ui) {
        MultivaluedMap<String, String> pathParams = ui.getQueryParameters();
        Hashtable<String, Object> queryParameters = new Hashtable<String, Object>();
        for(String key :  pathParams.keySet()) {
            LOG.info("  - query parameter {} = {}", key, pathParams.get(key).get(0));
            queryParameters.put(key, pathParams.get(key).get(0));
        }
        return queryParameters;
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
    @Produces(MediaType.APPLICATION_JSON)
    public String getPlan(
                          @PathParam("planName") String planName,
                          @Context UriInfo ui) {
        LOG.info("Plan name = {}", planName);
        
        BeanPlanMaker bpm = new BeanPlanMaker();
        try {
            Hashtable<String, Object> planParameters = this.getQueryParameters(ui);
            planParameters.put("planName", planName);
            planParameters.put("PLAN_MIME", "text/java");

            Hashtable<String, Object> results = bpm.createPlan(planParameters);
            PlanContainer planContainer = (PlanContainer) results.get("PLAN");
            ProtoPlan protoPlan = planContainer.m_protoPlan;

            // TODO Construct a JSON formatted result from the BeanPlanMaker
            // TODO Get implementation by DI
            PlanJSONProvider planJSONProvider = new JacksonPlanJSONProvider();
            return planJSONProvider.planToString(protoPlan, planName);
        }
        catch (RemoteException e) {
            LOG.error(e.getMessage(), e);
            // TODO : CHANGE THIS !
            // IT is a fake for now, map exceptions to http error codes
            return "{}";
        }
    }

}
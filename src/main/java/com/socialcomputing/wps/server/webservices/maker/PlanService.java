package com.socialcomputing.wps.server.webservices.maker;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 * 
 *         Rest service to manipulate plans
 * 
 */
// Will be hosted at the URI path "/plan"
@Path("/plan")
public class PlanService {

    // The method will process HTTP GET requests
    // And will produce content identified by the JSON exchange format
    
    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getPlan() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("element1", "value1");
        values.put("element2", "value2");
        return values;
    }

}

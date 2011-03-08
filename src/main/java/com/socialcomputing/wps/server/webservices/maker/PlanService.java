package com.socialcomputing.wps.server.webservices.maker;

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
    public String getPlan() {
        return "test";
    }

}

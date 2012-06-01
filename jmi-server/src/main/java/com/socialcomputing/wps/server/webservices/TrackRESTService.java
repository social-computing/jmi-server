package com.socialcomputing.wps.server.webservices;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;

import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.persistence.hibernate.Track;

/**
 * @author Franck Valetas <franck.valetas@social-computing.com>
 * 
 * <p>
 * A RESTFul web service to manipulate track
 * </p>
 * 
 */
@Path("/tracks") // Will be hosted at the URI path "/tracks"
public class TrackRESTService {

    @GET 
    @Path("{id}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Track getMaps(@PathParam("id") long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return (Track) session.get(Track.class, id);
    }

    
}
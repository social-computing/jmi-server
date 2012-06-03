package com.socialcomputing.wps.server.webservices;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.HibernateException;
import org.hibernate.Query;
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
    @Path("last.json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Track> lastJson( @DefaultValue("0") @QueryParam("start") int start, @DefaultValue("-1") @QueryParam("max") int max, @QueryParam("success") String success) {
        List<Track> tracks = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            max = max == -1 ? 100 : Math.min( max, 1000);
            Query query = null;
            if( success == null || success.equals( "*")) {
                query = session.createQuery( "from Track as track order by track.date desc");
            }
            else {
                query = session.createQuery( "from Track as track where track.success = :success order by track.date desc");
                query.setBoolean( "success", success.equalsIgnoreCase( "true"));
            }
            query.setFirstResult( start);
            query.setMaxResults( max);
            tracks = query.list();
            Response.ok();
        }
        catch (HibernateException e) {
            Response.status( HttpServletResponse.SC_BAD_REQUEST);
        }
        return tracks;
    }
    
    @GET 
    @Path("/track/{id}.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Track getMaps(@PathParam("id") long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return (Track) session.get(Track.class, id);
    }

    
}
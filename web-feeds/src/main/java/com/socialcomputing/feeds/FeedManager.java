package com.socialcomputing.feeds;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.feeds.utils.HibernateUtil;


@Path("/feeds")
public class FeedManager {
    private static final Logger LOG = LoggerFactory.getLogger(FeedManager.class);

    /**
     * @param ui
     */
    @GET
    @Path("record.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Feed record( @Context UriInfo ui) {
        Feed feed = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            MultivaluedMap<String, String> params = ui.getQueryParameters();
            
            String url = params.getFirst( "url");
            if( url != null) {
                url = url.trim();
                feed = (Feed) session.get(Feed.class, url);
                if( feed == null) {
                    feed = new Feed( url, params.getFirst( "title"), Integer.parseInt( params.getFirst( "count")) > 0);
                    session.save( feed);
                }
                else {
                    feed.incrementUpdate(params.getFirst( "title"), Integer.parseInt( params.getFirst( "count")) > 0);
                    session.update( feed);
                }
            }
            Response.ok();
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
            Response.status( HttpServletResponse.SC_BAD_REQUEST);
        }
        return feed;
    }
    

    @GET
    @Path("top.json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Feed> topJson( @Context UriInfo ui) {
        return top( ui);
    }
    
    @GET
    @Path("top.xml")
    @Produces(MediaType.APPLICATION_XML)
    public List<Feed> topXml( @Context UriInfo ui) {
        return top( ui);
    }
    
    private List<Feed> top( @Context UriInfo ui) {
        List<Feed> feeds = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            MultivaluedMap<String, String> params = ui.getQueryParameters();
            
            String smax = params.getFirst( "max");
            int max = smax == null ? 100 : Math.min( Integer.parseInt( smax), 100);
            feeds = session.createQuery( "from Feed as feed order by feed.count desc").list();
            while( feeds.size() > max)
                feeds.remove( feeds.size()-1);
            Response.ok();
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
            Response.status( HttpServletResponse.SC_BAD_REQUEST);
        }
        return feeds;
    }
    
    @GET
    @Path("last.json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Feed> lastJson( @Context UriInfo ui) {
        return last( ui);
    }

    @GET
    @Path("last.xml")
    @Produces(MediaType.APPLICATION_XML)
    public List<Feed> lastXml( @Context UriInfo ui) {
        return last( ui);
    }
    
    private List<Feed> last( @Context UriInfo ui) {
        List<Feed> feeds = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            MultivaluedMap<String, String> params = ui.getQueryParameters();
            
            String smax = params.getFirst( "max");
            int max = smax == null ? 100 : Math.min( Integer.parseInt( smax), 100);
            feeds = session.createQuery( "from Feed as feed order by feed.updated desc").list();
            while( feeds.size() > max)
                feeds.remove( feeds.size()-1);

            Response.ok();
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
            Response.status( HttpServletResponse.SC_BAD_REQUEST);
        }
        return feeds;
    }
}

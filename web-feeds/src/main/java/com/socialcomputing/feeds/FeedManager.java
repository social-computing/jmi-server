package com.socialcomputing.feeds;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.feeds.utils.HibernateUtil;
import com.sun.jersey.api.Responses;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;


@Path("/feeds")
public class FeedManager {
    private static final Logger LOG = LoggerFactory.getLogger(FeedManager.class);

    /**
     * @param ui
     */
    @GET
    @Path("record.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Feed record( @QueryParam("url") String url, @QueryParam("title") String title, @QueryParam("count") int count) {
        Feed feed = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            if( url != null) {
                url = url.trim();
                feed = (Feed) session.get(Feed.class, url);
                if( feed == null) {
                    feed = new Feed( url, title, count > 0);
                    session.save( feed);
                }
                else {
                    feed.incrementUpdate( title, count > 0);
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
    public List<Feed> topJson( @DefaultValue("-1") @QueryParam("max") int max, @DefaultValue("true") @QueryParam("success") String success) {
        return top( max, success);
    }
    
    @GET
    @Path("top.xml")
    @Produces(MediaType.APPLICATION_XML)
    public List<Feed> topXml( @DefaultValue("-1") @QueryParam("max") int max, @DefaultValue("true") @QueryParam("success") String success) {
        return top( max, success);
    }
    
    public List<Feed> top( int max, String success) {
        List<Feed> feeds = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            max = max == -1 ? 100 : Math.min( max, 1000);
            Query query = null;
            if( success == null || success.equals( "*")) {
                query = session.createQuery( "from Feed as feed order by feed.count desc");
            }
            else {
                query = session.createQuery( "from Feed as feed where feed.success = :success order by feed.count desc");
                query.setBoolean( "success", success.equalsIgnoreCase( "true"));
            }
            query.setFirstResult( 0);
            query.setMaxResults( max);
            feeds = query.list();
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
    public List<Feed> lastJson( @DefaultValue("-1") @QueryParam("max") int max, @DefaultValue("true") @QueryParam("success") String success) {
        return last( max, success);
    }

    @GET
    @Path("last.xml")
    @Produces(MediaType.APPLICATION_XML)
    public List<Feed> lastXml( @DefaultValue("-1") @QueryParam("max") int max, @DefaultValue("true") @QueryParam("success") String success) {
        return last( max, success);
    }
    
    public List<Feed> last( int max, String success) {
        List<Feed> feeds = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            max = max == -1 ? 100 : Math.min( max, 1000);
            Query query = null;
            if( success == null || success.equals( "*")) {
                query = session.createQuery( "from Feed as feed order by feed.updated desc");
            }
            else {
                query = session.createQuery( "from Feed as feed where feed.success = :success order by feed.updated desc");
                query.setBoolean( "success", success.equalsIgnoreCase( "true"));
            }
            query.setFirstResult( 0);
            query.setMaxResults( max);
            feeds = query.list();
            Response.ok();
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
            Response.status( HttpServletResponse.SC_BAD_REQUEST);
        }
        return feeds;
    }

    @GET
    @Path("feed.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Feed feedJson(  @QueryParam("url") String url) {
        return feed( url);
    }
    
    @GET
    @Path("feed.xml")
    @Produces(MediaType.APPLICATION_XML)
    public Feed feedXml( @QueryParam("url") String url) {
        return feed( url);
    }
    
    public Feed feed( String url) {
        Feed feed = null;
        try {
            if( url != null) {
                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                url = url.trim();
                feed = (Feed) session.get(Feed.class, url);
                Response.ok();
            }
            else
                Response.status( Responses.NOT_FOUND);
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
            Response.status( HttpServletResponse.SC_BAD_REQUEST);
        }
        return feed;
    }
    
    @GET
    @Path("/feed/thumbnail.png")
    @Produces( "image/png") 
    public Response getThumbnail( @QueryParam("url") String url) {
        Feed feed = feed( url); 
        if( feed != null) {
            return Response.ok( feed.getThumbnail()).build();
        }
        else
            Response.status( Responses.NOT_FOUND);
        return null;
    }
    
    @POST
    @Path("/feed/thumbnail.png")
    @Consumes( MediaType.MULTIPART_FORM_DATA) 
    public void putThumbnail( 
                   @FormDataParam("url") String url,
                   @FormDataParam("filedata") InputStream uploadedInputStream,
                   @FormDataParam("filedata") FormDataContentDisposition fileDetail,
                   @FormDataParam("width") int width,
                   @FormDataParam("width") int height,
                   @FormDataParam("width") String mime
                   ) {
        Feed feed = feed( url); 
        try {
            if( feed != null) {
                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int b;
                    b = uploadedInputStream.read();
                while( b != -1) {
                    baos.write( b);
                    b = uploadedInputStream.read();
                }
                feed.setThumbnail( baos.toByteArray());
                feed.setThumbnail_date( new Date());
                feed.setThumbnail_height( height);
                feed.setThumbnail_width( width);
                feed.setThumbnail_mime( mime);
                session.update( feed);
            }
            else
                Response.status( Responses.NOT_FOUND);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

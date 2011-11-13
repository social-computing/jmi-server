package com.socialcomputing.feeds;

import java.net.MalformedURLException;
import java.net.URL;
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
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.feeds.utils.HibernateUtil;

@Path("/sites")
public class SiteManager {
    private static final Logger LOG = LoggerFactory.getLogger(SiteManager.class);

    /**
     * @param ui
     */
    @GET
    @Path("record.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Site record( @Context UriInfo ui) {
        Site site = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            MultivaluedMap<String, String> params = ui.getQueryParameters();
            
            String url = params.getFirst( "url");
            if( url != null) {
                url = normalizeUrl( url.trim());
                site = (Site) session.get(Site.class, url);
                if( site == null) {
                    site = new Site( url, params.getFirst( "feed"));
                    session.save( site);
                }
                else {
                    site.incrementUpdate( params.getFirst( "feed"));
                    session.update( site);
                }
            }
            Response.ok();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Response.status( HttpServletResponse.SC_BAD_REQUEST);
        }
        return site;
    }
    private String normalizeUrl( String url) throws MalformedURLException {
        URL u = new URL( url);
        int port = u.getPort();
        return u.getProtocol() + "://" + u.getHost()+ (port != -1 && port != 80 ? ":" + port : "") + u.getPath();
    }
    
    @GET
    @Path("top.json")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Site> topJson( @Context UriInfo ui) {
        MultivaluedMap<String, String> params = ui.getQueryParameters();
        return top( params.getFirst( "max"));
    }
    
    @GET
    @Path("top.xml")
    @Produces(MediaType.APPLICATION_XML)
    public List<Site> topXml( @Context UriInfo ui) {
        MultivaluedMap<String, String> params = ui.getQueryParameters();
        return top( params.getFirst( "max"));
    }
    
    public List<Site> top( String smax) {
        List<Site> sites = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            
            int max = smax == null ? 100 : Math.min( Integer.parseInt( smax), 1000);
            Query query = session.createQuery( "from Site as site order by site.count desc");
            query.setFirstResult( 0);
            query.setMaxResults( max);
            sites = query.list();
            Response.ok();
        }
        catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
            Response.status( HttpServletResponse.SC_BAD_REQUEST);
        }
        return sites;
    }
}

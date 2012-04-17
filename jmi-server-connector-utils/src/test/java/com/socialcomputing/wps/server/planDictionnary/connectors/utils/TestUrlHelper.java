package com.socialcomputing.wps.server.planDictionnary.connectors.utils;

import org.junit.Assert;
import org.junit.Test;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;

public class TestUrlHelper {

    @Test
    public void testWww() throws JMIException {
        UrlHelper urlHelper = new UrlHelper("http://www.lemonde.fr");
        urlHelper.openConnections();
        Assert.assertTrue( urlHelper.getContentType().startsWith("text/html"));
        urlHelper.getResult();
        System.out.println( urlHelper.getContentEncoding());
        urlHelper.closeConnections();
    }

    @Test
    public void testFeedsImage() throws JMIException {
        UrlHelper urlHelper = new UrlHelper("http://feeds.just-map-it.com/rest/feeds/feed/thumbnail.png");
        urlHelper.addParameter( "url", "http://www.social-computing.com/feed/");
        urlHelper.openConnections();
        Assert.assertTrue( urlHelper.getContentType().startsWith("image/png"));
        System.out.println( urlHelper.getContentEncoding());
        urlHelper.closeConnections();
    }
    
    @Test
    public void testLabs() throws JMIException {
        UrlHelper urlHelper = new UrlHelper("http://labs.just-map-it.com/rest/viadeo/maps/contacts.json");
        urlHelper.openConnections();
        Assert.assertTrue( urlHelper.getContentType().startsWith("application/json"));
        System.out.println( urlHelper.getContentEncoding());
        urlHelper.closeConnections();
    }
}

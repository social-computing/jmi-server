package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.io.IOException;
import java.util.Hashtable;

import org.jdom.JDOMException;
import org.junit.Test;

import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.planDictionnary.connectors.utils.UrlHelper;

public class UrlHelperTest {

    @Test
    public void testOk() throws WPSConnectorException, JDOMException, IOException
    {
        UrlHelper u = new UrlHelper( UrlHelper.Type.GET, "http://news.google.com/news?ned=us&topic=h&output=rss");
        u.openConnections( 0, new Hashtable<String, Object>());
        System.out.println( u.getResult());
    }
    
}

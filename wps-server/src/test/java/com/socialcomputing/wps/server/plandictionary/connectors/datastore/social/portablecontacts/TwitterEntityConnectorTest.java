package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

public class TwitterEntityConnectorTest {
    protected String consumer = "7v0Vnjoe1yWD7H40yXp2NA";
    protected String secret = "sWg9k8F8AFLcKPJ70O76aw7hGj8zmpLVcDz4LD0m4";
    protected String callback = "http://denis.social-computing.org:8080/wps/social/twitter.jsp";

    @Test
    public void testGetTwitterAccessToken() throws WPSConnectorException {
        String results = TwitterEntityConnector.GetTwitterRequestToken( consumer, secret, callback);
        for( String result : results.split( "&")) {
            assertTrue( result.startsWith( "oauth_token") 
                        || result.startsWith( "oauth_token_secret")
                        || result.startsWith( "oauth_callback_confirmed"));
        }
    }

}

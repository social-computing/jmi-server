package com.socialcomputing.utils.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

/**
 * @author Jonathan Dray <Jonathan@social-computing.com>
 *
 * Really simple basic authentication with Apache commons http client
 * The username and password are required 
 * No authentication scope is declared
 * 
 */
public class BasicAuthHttpClient extends HttpClient {
    
    public BasicAuthHttpClient() {
        super();
    }
    
    public BasicAuthHttpClient(String username, String pass) {
        super();
        this.getState().setCredentials(AuthScope.ANY, 
                                       new UsernamePasswordCredentials(username, pass));
        this.getParams().setAuthenticationPreemptive(true);
    }
}
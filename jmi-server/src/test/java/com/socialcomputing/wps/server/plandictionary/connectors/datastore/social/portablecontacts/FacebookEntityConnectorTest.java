package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import org.junit.Test;

public class FacebookEntityConnectorTest {

    @Test
    public void testSignedRequest() throws Exception {
        String signed_request = "mUf_Dk1E3tfs9kSLfGFV1MkXVN2bv6nW1k4bEwOi8sI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTMyMTUzNzI1MywidXNlciI6eyJjb3VudHJ5IjoiZnIiLCJsb2NhbGUiOiJmcl9GUiIsImFnZSI6eyJtaW4iOjIxfX19";
        System.out.println( FacebookEntityConnector.GetProperty( signed_request, "algorithm"));
    }

}

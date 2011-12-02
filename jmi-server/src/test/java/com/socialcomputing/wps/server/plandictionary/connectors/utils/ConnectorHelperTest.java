package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Test;

import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.planDictionnary.connectors.utils.ConnectorHelper;


public class ConnectorHelperTest {

    @Test
    public void testOk()
    {
        Hashtable<String, Object> params = new Hashtable<String, Object>();
        params.put( "toto", "toto");
        String input[] = { "{$toto}", "AA{$toto}", "{$toto}BB", "AA{$toto}BB", "{$toto}{$toto}", "{$toto}AA{$toto}", "AA{$toto}BB{$toto}CC" };
        String output[] = { "toto",   "AAtoto",     "totoBB",  "AAtotoBB",     "totototo",       "totoAAtoto",      "AAtotoBBtotoCC"};
        int i = 0;
        for( String in : input) {
            String out = output[i++];
            try {
                Assert.assertTrue( ConnectorHelper.ReplaceParameter( in, params).equalsIgnoreCase( out));
            }
            catch (WPSConnectorException e) {
                Assert.fail( e.getMessage());
            }
        }
    }
    
}

package com.socialcomputing.solr;

import static org.junit.Assert.*;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreLikeThisTest {

    private static final Logger LOG = LoggerFactory.getLogger(MoreLikeThisTest.class);
    private static final String CONNECTION_URL = "http://dev.centreressources.com:8280/solr/liferay/";
    
    @Ignore("It's an integration test to lauch with a solr search server")
	@Test
	public void testDocumentProximity() throws Exception {
	    CommonsHttpSolrServer solrClient = new CommonsHttpSolrServer(CONNECTION_URL);
	    SolrQuery solrQuery = 
	        new SolrQuery().setQueryType("/" + MoreLikeThisParams.MLT);
	        solrQuery.setRows(30)
	                 .setQuery("uid:15_PORTLET_10156_FIELD_31092")
	                 .set(MoreLikeThisParams.MATCH_INCLUDE, false)
	                 .set(MoreLikeThisParams.MIN_DOC_FREQ, 1)
	                 .set(MoreLikeThisParams.MIN_TERM_FREQ, 1)
	                 .set(MoreLikeThisParams.SIMILARITY_FIELDS, "title, content");
	                 
	    QueryResponse response = solrClient.query(solrQuery);
	    assertEquals(30 , response.getResults().getNumFound());
	    
	    for(SolrDocument document : response.getResults()) {
	        LOG.debug("titre: {}", document.getFieldValue("title"));
	        LOG.debug("contenu: {}",  document.getFieldValue("content"));
	    }
	}
}

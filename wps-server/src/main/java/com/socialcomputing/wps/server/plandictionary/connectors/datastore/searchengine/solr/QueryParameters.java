package com.socialcomputing.wps.server.plandictionary.connectors.datastore.searchengine.solr;

/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 * TODO : Modify to a generic version when new parameters will need to be added 
 */
public class QueryParameters {

    private final QueryParameter<String> queryString;
    private final QueryParameter<Integer> maxResults;
    
    public QueryParameters(QueryParameter<String> queryString,
                           QueryParameter<Integer> maxResults) {
        
        this.queryString = queryString;
        this.maxResults = maxResults;
    }

    /**
     * @return the queryString
     */
    public QueryParameter<String> getQueryString() {
        return queryString;
    }

    /**
     * @return the maxResults
     */
    public QueryParameter<Integer> getMaxResults() {
        return maxResults;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("query string = ").append(queryString)
          .append("max results = ").append(maxResults);
        return sb.toString();
    }
}
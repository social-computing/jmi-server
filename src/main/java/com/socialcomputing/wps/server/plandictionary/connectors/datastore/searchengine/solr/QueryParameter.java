/**
 * 
 */
package com.socialcomputing.wps.server.plandictionary.connectors.datastore.searchengine.solr;

/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 *
 */
public class QueryParameter<T> {

    private final String name;
    private final T value;
    
    public QueryParameter(String name, T value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public T getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "{name => " + this.name 
               + ", value =>" + this.value 
               + "}";
    }
}

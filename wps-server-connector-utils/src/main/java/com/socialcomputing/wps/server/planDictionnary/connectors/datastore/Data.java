package com.socialcomputing.wps.server.planDictionnary.connectors.datastore;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 
 * @author Franck Valetas <franck.valetas@social-computing.com>
 * @author Jonathan Dray <jonathan.dray@social-computing.com>
 * 
 *         Representation of some data with a String identifier and a Map of
 *         parameters
 * 
 */
public abstract class Data {
    protected final String m_Id;
    protected final Hashtable<String, Object> m_Properties;

    public Data(String id) {
        this.m_Id = id;
        this.m_Properties = new Hashtable<String, Object>();
        addProperty("id", id);
    }

    /**
     * Add a property to this element
     * 
     * @param name
     *            name of the property
     * @param value
     *            property's value
     */
    public void addProperty(String name, Object value) {
        this.m_Properties.put(name, value);
    }

    /**
     * Add multiple properties to this element
     * 
     * @param properties
     *            a map of properties with keys and values
     */
    public void addProperties(Map<String, ? extends Object> properties) {
        this.m_Properties.putAll(properties);
    }

    public String getId() {
        return this.m_Id;
    }

    public Hashtable<String, Object> getProperties() {
        return this.m_Properties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(this.m_Id).append(" {");

        // Adding list of properties to the list of displayed values
        for (Entry<String, Object> property : this.m_Properties.entrySet()) {
            sb.append(property.getKey()).append(" => ");
            Object value = property.getValue();
            if (value instanceof String[]) {
                String[] new_name = (String[]) value;

                sb.append("[");
                for (String s : new_name) {
                    sb.append(s).append(", ");
                }
                sb.append("]");

            }
            else {
                sb.append(property.getValue());
            }
            sb.append(",");
        }

        sb.append("}");
        return sb.toString();
    }
    
    public StringBuilder toJson( StringBuilder sb) {
        boolean first = true;
        for (Entry<String, Object> p : this.m_Properties.entrySet()) {
            if( first) first = false;
            else sb.append(',');
            sb.append( "\"").append(p.getKey()).append("\":\"").append(p.getValue()).append("\"");
        }
        return sb;
    }
}

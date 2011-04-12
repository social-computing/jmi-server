package com.socialcomputing.wps.server.generator.json.impl;

import java.io.IOException;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.client.applet.ActiveZone;
import com.socialcomputing.wps.client.applet.BagZone;
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.Env;
import com.socialcomputing.wps.client.applet.LinkZone;
import com.socialcomputing.wps.client.applet.Plan;
import com.socialcomputing.wps.client.applet.Satellite;
import com.socialcomputing.wps.client.applet.Swatch;
import com.socialcomputing.wps.server.generator.PlanContainer;
import com.socialcomputing.wps.server.generator.json.PlanJSONProvider;

/**
 * @author "Jonathan Dray <jonathan@social-computing.com>" FRV : ce n'est pas le
 *         protoplan qu'il faut traiter....
 */
public class JacksonPlanJSONProvider implements PlanJSONProvider {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(JacksonPlanJSONProvider.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.socialcomputing.wps.server.generator.json.PlanJSONProvider#planToString
     * (com.socialcomputing.wps.server.generator.ProtoPlan)
     */
    @Override
    public String planToString(PlanContainer container) {

        LOG.info("Generating JSON representation of the plan");
        // Create root object
        ObjectNode rootNode = getJSON(container);

        String result = null;
        try {
            result = mapper.writeValueAsString(rootNode);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("End JSON");
        return result;

    }

    protected ObjectNode getJSON(PlanContainer container) {
        ObjectNode root = mapper.createObjectNode();
        if (container.m_env == null) { return root; }
        root.put("env", toJSON( container.m_env));
        root.put("plan", toJSON( container.m_plan));
        return root;
    }

    private ObjectNode toJSON( Env env) {
        ObjectNode node = mapper.createObjectNode();
        node.put("inColor", toJSON( env.m_inCol));
        node.put("outColor", toJSON( env.m_outCol));
        node.put("filterColor", toJSON( env.m_filterCol));
        ObjectNode props = node.putObject( "props");
        for( String key : (Set<String>)env.m_props.keySet()) {
            props.put( key, (String)env.m_props.get( key));
        }
        return node;
    }
    
    private ObjectNode toJSON( Plan plan) {
        ObjectNode node = mapper.createObjectNode();
        // Add the array of nodes
        ArrayNode nodes = node.putArray( "nodes");
        for (ActiveZone zone : plan.m_nodes) {
            nodes.add( toJSON( zone));
        }
        // Add the array of links
        ArrayNode links = node.putArray( "links");
        for (ActiveZone zone : plan.m_links) {
            ObjectNode attNode = nodes.addObject();
        }
        return node;
    }
    
    private ObjectNode toJSON( ColorX color) {
        ObjectNode node = mapper.createObjectNode();
        node.put( "color", String.valueOf( color.m_color));
        if( color.m_scolor == null)
            node.putNull( "scolor");
        else
            node.put( "scolor", color.m_scolor);
        return node;
    }

    private ObjectNode toJSON( ActiveZone zone) {
        ObjectNode node = null;
        if( zone instanceof BagZone)
            node = toJSON(( BagZone)zone);
        if( zone instanceof LinkZone)
            node = toJSON(( LinkZone)zone);
        if( node == null)
            node = mapper.createObjectNode();
        node.put( "curSwatch", toJSON( zone.getCurSwatch()));
        node.put( "restSwatch", toJSON( zone.getRestSwatch()));
        return node;
    }
    
    private ObjectNode toJSON( BagZone zone) {
        ObjectNode node = mapper.createObjectNode();
        return node;
    }
    
    private ObjectNode toJSON( LinkZone zone) {
        ObjectNode node = mapper.createObjectNode();
        return node;
    }

    private ObjectNode toJSON( Swatch swatch) {
        ObjectNode node = mapper.createObjectNode();
        ArrayNode sats = node.putArray( "satellites");
        for (Satellite sat : swatch.getSatellites()) {
            sats.add( toJSON( sat));
        }
        return node;
    }
    
    private ObjectNode toJSON( Satellite satellite) {
        ObjectNode node = mapper.createObjectNode();
        return node;
    }
}

package com.socialcomputing.wps.server.generator.json.impl;

import java.io.IOException;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.client.applet.ActiveZone;
import com.socialcomputing.wps.client.applet.BagZone;
import com.socialcomputing.wps.client.applet.Base;
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.Env;
import com.socialcomputing.wps.client.applet.FontX;
import com.socialcomputing.wps.client.applet.HTMLText;
import com.socialcomputing.wps.client.applet.LinkZone;
import com.socialcomputing.wps.client.applet.Plan;
import com.socialcomputing.wps.client.applet.Satellite;
import com.socialcomputing.wps.client.applet.ShapeX;
import com.socialcomputing.wps.client.applet.Slice;
import com.socialcomputing.wps.client.applet.Swatch;
import com.socialcomputing.wps.client.applet.Transfo;
import com.socialcomputing.wps.client.applet.VContainer;
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
        root.put("env", toJSON(container.m_env));
        root.put("plan", toJSON(container.m_plan));
        return root;
    }

    private ObjectNode toJSON(Env env) {
        ObjectNode node = mapper.createObjectNode();
        node.put("group_bit", Env.GROUP_BIT);
        node.put("flags", env.m_flags);
        node.put("inColor", toJSON(env.m_inCol));
        node.put("outColor", toJSON(env.m_outCol));
        node.put("filterColor", toJSON(env.m_filterCol));
        node.put("transfo", toJSON(env.m_transfo));
        ObjectNode props = node.putObject("props");
        for (String key : (Set<String>) env.m_props.keySet()) {
            props.put(key, (String) env.m_props.get(key));
        }
        ObjectNode sel = node.putObject("selections");
        for (String key : (Set<String>) env.m_selections.keySet()) {
            sel.put(key, env.m_selections.get(key).toString());
        }
        return node;
    }

    private ObjectNode toJSON(Plan plan) {
        ObjectNode node = mapper.createObjectNode();
        // Add the array of nodes
        ArrayNode nodes = node.putArray("nodes");
        for (ActiveZone zone : plan.m_nodes) {
            nodes.add(toJSON(zone));
        }
        // Add the array of links
        ArrayNode links = node.putArray("links");
        for (ActiveZone zone : plan.m_links) {
            links.add(toJSON(zone));
        }
        return node;
    }

    // TODO à checker
    private ObjectNode toJSON(ColorX color) {
        ObjectNode node = mapper.createObjectNode();
        node.put("color", String.valueOf(color.m_color));
        if (color.m_scolor == null)
            node.putNull("scolor");
        else
            node.put("scolor", color.m_scolor);
        return node;
    }

    /*
     * LinkZone / BagZone : ActiveZone subclasses
     */
    // Ok
    private ObjectNode toJSON(ActiveZone zone) {
        ObjectNode node = null;
        if (zone == null)
            node = mapper.createObjectNode();
        else if (zone instanceof BagZone)
            node = toJSON((BagZone) zone);
        else if (zone instanceof LinkZone)
            node = toJSON((LinkZone) zone);
        else 
            node = mapper.createObjectNode();
        
        node.put("flags", zone.m_flags);
        node.put("curSwatch", toJSON(zone.getCurSwatch()));
        node.put("restSwatch", toJSON(zone.getRestSwatch()));
        return node;
    }

    // Ok
    private ObjectNode toJSON(BagZone zone) {
        ObjectNode node = mapper.createObjectNode();
        ArrayNode subzone = node.putArray("subZones");
        if (zone != null&& zone.m_subZones != null) {
            for (ActiveZone az : zone.m_subZones) {
                subzone.add(toJSON(az));
            }
        }
        return node;
    }

    // Ok
    private ObjectNode toJSON(LinkZone zone) {
        ObjectNode node = mapper.createObjectNode();
        node.put("from", toJSON(zone.m_from));
        node.put("to", toJSON(zone.m_to));
        return node;
    }

    // Ok
    private ObjectNode toJSON(Satellite satellite) {
        ObjectNode node = mapper.createObjectNode();
        node.put("shapex", toJSON(satellite.getShape()));
        ArrayNode slices = node.putArray("slices");
        for (Slice slice : satellite.getSlices()) {
            slices.add(toJSON(slice));
        }
        return node;
    }

    // Ok
    private ObjectNode toJSON(Transfo transfo) {
        ObjectNode node = mapper.createObjectNode();
        node.put("dir", transfo.m_dir);
        node.put("pos", transfo.m_pos);
        node.put("scl", transfo.m_scl);
        node.put("flags", transfo.m_flags);
        return node;
    }
    
    /*
     * ShapeX / Slice / Swatch / HTMLText / FontX : Base subclasses
     */
    // Ok
    private ObjectNode toJSON(ShapeX shape) {
        ObjectNode node = mapper.createObjectNode();
        return toJSON( shape, node);
    }
    
    // Ok
    private ObjectNode toJSON(Slice slice) {
        ObjectNode node = mapper.createObjectNode();
        return toJSON( slice, node);
    }
    
    // Ok
    private ObjectNode toJSON(Swatch swatch) {
        ObjectNode node = mapper.createObjectNode();
        ObjectNode refs = node.putObject("refs");
        for (String key : (Set<String>) swatch.m_refs.keySet()) {
            refs.put(key, swatch.m_refs.get(key).toString());
        }
        ArrayNode sats = node.putArray("satellites");
        for (Satellite sat : swatch.getSatellites()) {
            sats.add(toJSON(sat));
        }
        return toJSON( swatch, node);
    }
    
    // Ok
    private ObjectNode toJSON(HTMLText text) {
        ObjectNode node = mapper.createObjectNode();
        return toJSON( text, node);
    }
    
    // Ok
    private ObjectNode toJSON(FontX font) {
        ObjectNode node = mapper.createObjectNode();
        return toJSON( font, node);
    }
    
   // Ok
    private ObjectNode toJSON(Base base, ObjectNode node) {
        ArrayNode containers = node.putArray("containers");
        for (VContainer container : base.m_containers) {
            if( container != null) {
                containers.add(toJSON(container));
            }
        }
        return node;
    }

    // TODO Presque OK !
    private ObjectNode toJSON(VContainer container) {
        ObjectNode node = mapper.createObjectNode();
        // TODO à checker !!!
        if( container.m_value != null) {
            if( container.m_value instanceof Integer)
                node.put("value", (Integer)container.m_value); 
            else if( container.m_value instanceof Float)
                node.put("value", (Float)container.m_value); 
            else if( container.m_value instanceof String)
                node.put("value", (String)container.m_value); 
            else if( container.m_value instanceof ColorX)
                node.put("value", toJSON((ColorX)container.m_value)); 
            else if( container.m_value instanceof Transfo)
                node.put("value", toJSON((Transfo)container.m_value)); 
            else if( container.m_value instanceof HTMLText)
                node.put("value", toJSON((HTMLText)container.m_value)); 
            else if( container.m_value instanceof FontX)
                node.put("value", toJSON((FontX)container.m_value)); 
            else {
                node.put("value", "error"); 
                System.out.println( "Class not bound : toJSON.VContainer.value " + container.m_value.getClass().getSimpleName());
            }
        }
        node.put("bound", container.isBound());
        return node;
    }
 }

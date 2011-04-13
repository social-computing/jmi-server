package com.socialcomputing.wps.server.generator.json;

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
import com.socialcomputing.wps.client.applet.Base;
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.Env;
import com.socialcomputing.wps.client.applet.FontX;
import com.socialcomputing.wps.client.applet.HTMLText;
import com.socialcomputing.wps.client.applet.LinkZone;
import com.socialcomputing.wps.client.applet.MenuX;
import com.socialcomputing.wps.client.applet.Plan;
import com.socialcomputing.wps.client.applet.Satellite;
import com.socialcomputing.wps.client.applet.ShapeX;
import com.socialcomputing.wps.client.applet.Slice;
import com.socialcomputing.wps.client.applet.Swatch;
import com.socialcomputing.wps.client.applet.Transfo;
import com.socialcomputing.wps.client.applet.VContainer;
import com.socialcomputing.wps.server.generator.PlanContainer;

/**
 * @author "Jonathan Dray <jonathan@social-computing.com>"
 */
public class PlanJSONProvider {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(PlanJSONProvider.class);

    
    public static ObjectMapper GetMapper() {
        return mapper;
    }

    static public String planToString(ObjectNode node) {
        String result = null;
        try {
            result = mapper.writeValueAsString(node);
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
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.socialcomputing.wps.server.generator.json.PlanJSONProvider#planToJSON
     * 
     */
    static public ObjectNode planToJSON(PlanContainer container) {
        LOG.info("Generating JSON representation of the plan");
        // Create root object
        return getJSON(container);
    }

    static public void putValue(ObjectNode node, String key, Object value) {
        if( value instanceof String)
            node.put(key, (String) value);
        else if( value instanceof Integer)
            node.put(key, (Integer) value);
        else if( value instanceof Float)
            node.put(key, (Float) value);
        else if( value instanceof Long)
            node.put(key, (Long) value);
        else if( value instanceof ObjectNode)
            node.put(key, (ObjectNode) value);
        else if( value instanceof Slice)
            node.put(key, toJSON((Slice)value));
        else if( value instanceof MenuX)
            node.put(key, toJSON((MenuX)value));
        else if( value instanceof ColorX)
            node.put(key, toJSON((ColorX)value)); 
        else if( value instanceof Transfo)
            node.put(key, toJSON((Transfo)value)); 
        else if( value instanceof HTMLText)
            node.put(key, toJSON((HTMLText)value)); 
        else if( value instanceof FontX)
            node.put(key, toJSON((FontX)value)); 
        else {
            System.out.println( "Class not bound: putValue: " + value.getClass().getSimpleName());
        }
    }
    
    static protected ObjectNode getJSON(PlanContainer container) {
        ObjectNode root = mapper.createObjectNode();
        if (container.m_env == null) { return root; }
        root.put("env", toJSON(container.m_env));
        root.put("plan", toJSON(container.m_plan));
        return root;
    }

    static private ObjectNode toJSON(Env env) {
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
            putValue(sel, key, env.m_selections.get(key));
        }
        return node;
    }

    static private ObjectNode toJSON(Plan plan) {
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
    static private ObjectNode toJSON(ColorX color) {
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
    static private ObjectNode toJSON(ActiveZone zone) {
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
    static private ObjectNode toJSON(BagZone zone) {
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
    static private ObjectNode toJSON(LinkZone zone) {
        ObjectNode node = mapper.createObjectNode();
        node.put("from", toJSON(zone.m_from));
        node.put("to", toJSON(zone.m_to));
        return node;
    }

    // Ok
    static private ObjectNode toJSON(Satellite satellite) {
        ObjectNode node = mapper.createObjectNode();
        node.put("shapex", toJSON(satellite.getShape()));
        ArrayNode slices = node.putArray("slices");
        for (Slice slice : satellite.getSlices()) {
            slices.add(toJSON(slice));
        }
        return node;
    }

    // Ok
    static private ObjectNode toJSON(Transfo transfo) {
        ObjectNode node = mapper.createObjectNode();
        node.put("dir", transfo.m_dir);
        node.put("pos", transfo.m_pos);
        node.put("scl", transfo.m_scl);
        node.put("flags", transfo.m_flags);
        return node;
    }
    
    /*
     * ShapeX / Slice / Swatch / HTMLText / FontX / MenuX : Base subclasses
     */
    // Ok
    static private ObjectNode toJSON(ShapeX shape) {
        ObjectNode node = mapper.createObjectNode();
        return toJSON( shape, node);
    }
    
    // Ok
    static private ObjectNode toJSON(Slice slice) {
        ObjectNode node = mapper.createObjectNode();
        return toJSON( slice, node);
    }
    
    // Ok
    static private ObjectNode toJSON(Swatch swatch) {
        ObjectNode node = mapper.createObjectNode();
        ObjectNode refs = node.putObject("refs");
        for (String key : (Set<String>) swatch.m_refs.keySet()) {
            putValue(refs, key, swatch.m_refs.get(key));
        }
        ArrayNode sats = node.putArray("satellites");
        for (Satellite sat : swatch.getSatellites()) {
            sats.add(toJSON(sat));
        }
        return toJSON( swatch, node);
    }
    
    // Ok
    static private ObjectNode toJSON(HTMLText text) {
        ObjectNode node = mapper.createObjectNode();
        return toJSON( text, node);
    }
    
    // Ok
    static private ObjectNode toJSON(FontX font) {
        ObjectNode node = mapper.createObjectNode();
        return toJSON( font, node);
    }

    // Ok
    static private ObjectNode toJSON(MenuX menu) {
        ObjectNode node = mapper.createObjectNode();
        ArrayNode sats = node.putArray("menu");
        if( menu != null && menu.m_items != null) {
            for (MenuX submenu : menu.m_items) {
                sats.add(toJSON(submenu));
            }
        }
        return toJSON( menu, node);
    }
    
   // Ok
    static private ObjectNode toJSON(Base base, ObjectNode node) {
        ArrayNode containers = node.putArray("containers");
        for (VContainer container : base.m_containers) {
            if( container != null) {
                containers.add(toJSON(container));
            }
        }
        return node;
    }

    // TODO Presque OK !
    static private ObjectNode toJSON(VContainer container) {
        ObjectNode node = mapper.createObjectNode();
        // TODO à checker !!!
        if( container.m_value != null) {
            putValue(node, "value", container.m_value);
        }
        node.put("bound", container.isBound());
        return node;
    }
 }

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
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.Env;
import com.socialcomputing.wps.client.applet.LinkZone;
import com.socialcomputing.wps.client.applet.Plan;
import com.socialcomputing.wps.client.applet.Satellite;
import com.socialcomputing.wps.client.applet.ShapeX;
import com.socialcomputing.wps.client.applet.Slice;
import com.socialcomputing.wps.client.applet.Swatch;
import com.socialcomputing.wps.client.applet.Transfo;
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

    private ObjectNode toJSON(ColorX color) {
        ObjectNode node = mapper.createObjectNode();
        node.put("color", String.valueOf(color.m_color));
        if (color.m_scolor == null)
            node.putNull("scolor");
        else
            node.put("scolor", color.m_scolor);
        return node;
    }

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
        
        node.put("side_bit", ActiveZone.SIDE_BIT);
        node.put("left_bit", ActiveZone.LEFT_BIT);
        node.put("invisible_bit", ActiveZone.INVISIBLE_BIT);
        node.put("flags", zone.m_flags);
        node.put("curSwatch", toJSON(zone.getCurSwatch()));
        node.put("restSwatch", toJSON(zone.getRestSwatch()));
        return node;
    }

    private ObjectNode toJSON(BagZone zone) {
        ObjectNode node = mapper.createObjectNode();

        ArrayNode subzone = node.putArray("subZones");
        if (zone != null) {
            if (zone.m_subZones != null) {
                for (ActiveZone az : zone.m_subZones) {
                    subzone.add(toJSON(az));
                }
            }
        }
        return node;
    }

    private ObjectNode toJSON(LinkZone zone) {
        ObjectNode node = mapper.createObjectNode();
        node.put("fakefrom_bit", LinkZone.FAKEFROM_BIT);
        node.put("faketo_bit", LinkZone.FAKETO_BIT);
        node.put("from", toJSON(zone.m_from));
        node.put("to", toJSON(zone.m_to));
        return node;
    }

    private ObjectNode toJSON(Swatch swatch) {
        ObjectNode node = mapper.createObjectNode();
        node.put("title_val", Swatch.TITLE_VAL);
        node.put("link_bit", Swatch.LINK_BIT);
        ObjectNode refs = node.putObject("refs");
        for (String key : (Set<String>) swatch.m_refs.keySet()) {
            refs.put(key, swatch.m_refs.get(key).toString());
        }
        ArrayNode sats = node.putArray("satellites");
        for (Satellite sat : swatch.getSatellites()) {
            sats.add(toJSON(sat));
        }
        return node;
    }

    private ObjectNode toJSON(Satellite satellite) {
        ObjectNode node = mapper.createObjectNode();
        node.put("transfo_val", Satellite.TRANSFO_VAL);
        node.put("hover_val", Satellite.HOVER_VAL);
        node.put("click_val", Satellite.CLICK_VAL);
        node.put("dbclick_val", Satellite.DBLCLICK_VAL);
        node.put("selection_val", Satellite.SELECTION_VAL);
        node.put("link_drk_col_val", Satellite.LINK_DRK_COL_VAL);
        node.put("link_nrm_col_val", Satellite.LINK_NRM_COL_VAL);
        node.put("link_lit_col_val", Satellite.LINK_LIT_COL_VAL);
        node.put("visible_bit", Satellite.VISIBLE_BIT);
        node.put("link_bit", Satellite.LINK_BIT);
        node.put("super_bit", Satellite.SUPER_BIT);
        node.put("sub_bit", Satellite.SUB_BIT);
        node.put("cur_bit", Satellite.CUR_BIT);
        node.put("rest_bit", Satellite.REST_BIT);
        node.put("back_bit", Satellite.BACK_BIT);
        node.put("sel_bit", Satellite.SEL_BIT);
        node.put("tip_bit", Satellite.TIP_BIT);
        node.put("nosided_bit", Satellite.NOSIDED_BIT);
        node.put("sel_typ", Satellite.SEL_TYP);
        node.put("tip_typ", Satellite.TIP_TYP);
        node.put("base_typ", Satellite.BASE_TYP);
        node.put("all_typ", Satellite.ALL_TYP);
        node.put("shapex", toJSON(satellite.getM_shape()));
        ArrayNode slices = node.putArray("slices");
        for (Slice slice : satellite.getM_slices()) {
            slices.add(toJSON(slice));
        }
        return node;
    }
    

    private JsonNode toJSON(Transfo transfo) {
        ObjectNode node = mapper.createObjectNode();
        node.put("cart_bit", Transfo.CART_BIT);
        node.put("abs_bit", Transfo.ABS_BIT);
        node.put("inter_bit", Transfo.INTER_BIT);
        node.put("dir", transfo.m_dir);
        node.put("pos", transfo.m_pos);
        node.put("scl", transfo.m_scl);
        node.put("flags", transfo.m_flags);
        return node;
    }
    
    private JsonNode toJSON(ShapeX shape) {
        ObjectNode node = mapper.createObjectNode();
        node.put("polygon_val", ShapeX.POLYGON_VAL);
        node.put("scale_val", ShapeX.SCALE_VAL);
        node.put("ctr_lnk_bit", ShapeX.CTR_LNK_BIT);
        node.put("sec_lnk_bit", ShapeX.SEC_LNK_BIT);
        node.put("tan_lnk_bit", ShapeX.TAN_LNK_BIT);
        return node;
    }
    
    private JsonNode toJSON(Slice slice) {
        ObjectNode node = mapper.createObjectNode();
        node.put("transfo_val", Slice.TRANSFO_VAL);
        node.put("in_col_val", Slice.IN_COL_VAL);
        node.put("out_col_val", Slice.OUT_COL_VAL);
        node.put("image_val", Slice.IMAGE_VAL);
        node.put("text_val", Slice.TEXT_VAL);
        node.put("alpha_val", Slice.ALPHA_VAL);
        node.put("delay_val", Slice.DELAY_VAL);
        node.put("length_val", Slice.LENGTH_VAL);
        node.put("visible_bit", Slice.VISIBLE_BIT);
        return node;
    }
    
}

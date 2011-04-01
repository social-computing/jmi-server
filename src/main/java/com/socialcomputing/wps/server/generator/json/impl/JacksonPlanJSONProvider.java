package com.socialcomputing.wps.server.generator.json.impl;

import java.awt.Point;
import java.io.IOException;
import java.util.Collection;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.generator.AttributeLink;
import com.socialcomputing.wps.server.generator.NodeMapData;
import com.socialcomputing.wps.server.generator.ProtoAttribute;
import com.socialcomputing.wps.server.generator.ProtoPlan;
import com.socialcomputing.wps.server.generator.RecommendationGroup;
import com.socialcomputing.wps.server.generator.json.PlanJSONProvider;

/**
 * @author "Jonathan Dray <jonathan@social-computing.com>"
 */
public class JacksonPlanJSONProvider implements PlanJSONProvider {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(JacksonPlanJSONProvider.class);
   
    
    /* (non-Javadoc)
     * @see com.socialcomputing.wps.server.generator.json.PlanJSONProvider#planToString(com.socialcomputing.wps.server.generator.ProtoPlan)
     */
    @Override
    public String planToString(ProtoPlan plan, String planName) {
        
        LOG.info("Generating JSON representation of the plan");
        // Create root object
        ObjectNode rootNode = mapper.createObjectNode();
        // Add the plan name
        rootNode.put("planName", planName);

        // Add the array of nodes
        ArrayNode nodes = mapper.createArrayNode();
        for(ProtoAttribute attribute : plan.m_attributes) {
            
            // Construct a node json node with basic information
            ObjectNode attNode = nodes.addObject();
            attNode.put("id", attribute.m_strId);
            attNode.put("size", attribute.m_size);
            attNode.put("weight", attribute.m_weight);
            
            // If it is a base node, add the node position
            NodeMapData nodeMapData = attribute.getMapData();
            if(nodeMapData != null) {
                Point p = nodeMapData.getM_clientPos();
                ObjectNode positionNode = attNode.putObject("position");
                positionNode.put("x", p.x);
                positionNode.put("y", p.y);
            }
            // Else, add parent id
            else {
                attNode.put("parent", attribute.m_parent.m_strId);
            }
        }
        rootNode.put("nodes", nodes);
        
        // Add the array of links
        ArrayNode links = mapper.createArrayNode();
        for(AttributeLink link : plan.m_attLinks) {
            ObjectNode attLink = links.addObject();
            attLink.put("id", link.getStrId());
            ObjectNode nodesNode = attLink.putObject("nodes");
            nodesNode.put("from", link.m_from.m_strId);
            nodesNode.put("to", link.m_to.m_strId);
            Collection<String> recommendations = link.getRecommendations(RecommendationGroup.ENTITIES_RECOM);
            if(recommendations.size() > 0) {
                ArrayNode recommendationsNode = mapper.createArrayNode();
                for(String recommendation : recommendations) {
                    recommendationsNode.add(recommendation);
                }
                attLink.put("recommendations", recommendationsNode);
            }
        }
        rootNode.put("links", links);
        
        
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

}

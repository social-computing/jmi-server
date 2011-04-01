/**
 * 
 */
package com.socialcomputing.wps.server.generator.json;

import com.socialcomputing.wps.server.generator.ProtoPlan;

/**
 * @author "Jonathan Dray <jonathan@social-computing.com>"
 *
 */
public interface PlanJSONProvider {

    String planToString(ProtoPlan plan, String planName);
}

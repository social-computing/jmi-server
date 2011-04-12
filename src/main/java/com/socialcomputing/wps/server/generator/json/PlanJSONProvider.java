/**
 * 
 */
package com.socialcomputing.wps.server.generator.json;

import com.socialcomputing.wps.server.generator.PlanContainer;

/**
 * @author "Jonathan Dray <jonathan@social-computing.com>"
 *
 */
public interface PlanJSONProvider {

    String planToString(PlanContainer container);
}

package com.socialcomputing.wps.server.generator;

import java.io.Serializable;

import com.socialcomputing.wps.client.applet.Env;
import com.socialcomputing.wps.client.applet.Plan;

/**
 * <p>Title: PlanContainer</p>
 * <p>Description: A simple container that encapsulates a couple Plan/Env.<br>
 * Don't remember why?</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class PlanContainer implements Serializable
{
	static final long serialVersionUID = -3929474091372046838L;

	public Plan m_plan  = null;
	public Env  m_env   = null;

	public PlanContainer( Env env, Plan plan )
	{
		m_env = env;
		m_plan = plan;
	}
	
	public String toJson() {
	    return "";
	}
}
package com.socialcomputing.wps.server.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPOutputStream;

import org.codehaus.jackson.node.ObjectNode;

import com.socialcomputing.wps.client.applet.Env;
import com.socialcomputing.wps.client.applet.Plan;
import com.socialcomputing.wps.server.generator.json.PlanJSONProvider;
import com.socialcomputing.wps.server.generator.json.PlanJSONProvider;

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
	
	public byte[] toBinary() throws IOException {
        if (m_env != null) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(32768);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(new GZIPOutputStream(bout));
            objectOutStream.writeObject( m_env);
            objectOutStream.writeObject( m_plan);
            objectOutStream.close();
            return bout.toByteArray();
        }
        else {
            return new byte[0];
        }
	}
	
	public ObjectNode toJson() {
        PlanJSONProvider provider = new PlanJSONProvider();
        return provider.planToJSON(this);
	}
}
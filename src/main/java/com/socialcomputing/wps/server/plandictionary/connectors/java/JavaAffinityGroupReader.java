package com.socialcomputing.wps.server.plandictionary.connectors.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;

import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.utils.StringAndFloat;

public class JavaAffinityGroupReader implements iAffinityGroupReader, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6780176564827483647L;
	private transient PlanData m_planData = null;

	public JavaAffinityGroupReader()
	{
	}

	public boolean openConnections( PlanData planData)
	{
		m_planData = planData;
		return true;
	}

	public StringAndFloat[] retrieveAffinityGroup( String id, int affinityThreshold, int max)
	{
		boolean normalizeIt = false;
		float pond;
		float maxPond = Float.MIN_VALUE;

		ArrayList<StringAndFloat> eList = new ArrayList<StringAndFloat>();

		Enumeration<String> enumvar = m_planData.getEntities().keys();
		for( int i = 0; (i < max) && (enumvar.hasMoreElements()); ++i)
		{
			pond = 1;
			eList.add( new StringAndFloat( enumvar.nextElement(), pond));

			if ((!normalizeIt) && (pond>1.0))
				normalizeIt = true;
			if ((normalizeIt) && (pond>maxPond) )
				maxPond=pond;
		}

		if (normalizeIt)
		{
			StringAndFloat obj;
			float threshold=(float)affinityThreshold/(float)100;

			int size = eList.size();
			for( int i = 0; i < size; )
			{
				obj= (StringAndFloat) eList.get( i);
				obj.m_value=(float)((maxPond-obj.m_value)/maxPond);
				if (obj.m_value>threshold)
				{
				   eList.remove( i);
				   --size;
				}
				else
					++i;
			}
		}
		else
		{
			StringAndFloat obj;
			float threshold=(float)affinityThreshold/(float)100;

			int size = eList.size();
			for( int i = 0; i < size; )
			{
				obj= (StringAndFloat) eList.get( i);
				if (obj.m_value>threshold)
				{
				   eList.remove( i);
				   --size;
				}
				else
					++i;
			}
		}
		return ( StringAndFloat[]) eList.toArray( new StringAndFloat[0]);
	}
}
package com.socialcomputing.wps.server.affinityengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.socialcomputing.wps.server.generator.RecommendationGroup;
import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.plandictionary.AffinityReaderProfile;
import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.utils.StringAndFloat;
import com.socialcomputing.wps.server.webservices.PlanRequest;

/**
  * describes object which read affinity group and compute recommendation (real time)
  *
  * **********************************************************
  * Java Class Name : RecommendationEngine
  * ---------------------------------------------------------
  * Filetype: (SOURCE)
  * Filepath: C:\Dvpt\src\com\voyezvous\wps\server\affinityengine\RecommendationEngine.java
  *
  *
  * ********************************************************** */

public class RecommendationInterface
{
	private RecommendationProcess m_RecommendationProcess=null;
	private PlanRequest m_PlanRequest = null;

	/**
	*  */
	public  RecommendationInterface( PlanRequest planRequest )
	{
		m_PlanRequest = planRequest;
	}

	/**
	* Compute recommandation for a given list of attributes */
	public  void computeRecommendations( Collection attributes ) throws JMIException
	{
		m_RecommendationProcess.compute((String [])attributes.toArray());
	}

	public RecommendationProcess getRecommendationProcess()
	{
		return m_RecommendationProcess;
	}

	/**
	* Get subattributes to recommand for a given entity and a attribut */
	public Collection<String> getRecommendations( String attribut )
	{
		if( m_RecommendationProcess == null) return null;
		
		// entity
		Collection<StringAndFloat> ret = m_RecommendationProcess.getRecommendations(attribut);

		Collection<String> aColl = new ArrayList<String>();
		for(StringAndFloat recommendation : ret) {
		    aColl.add(recommendation.m_Id);
		}
		return aColl;
	}

	/**
	*/
	public Collection<String> retrieveAffinityGroup() throws JMIException
	{
		Collection<String> retVal = new ArrayList<String>();
		AffinityReaderProfile affinityProfile = m_PlanRequest.getAffinityReaderProfile();
		AnalysisProfile profile = m_PlanRequest.getAnalysisProfile();
		if( affinityProfile != null)
		{
			StringAndFloat[]  m_entities = affinityProfile.getConnector( m_PlanRequest.m_Dictionary).retrieveAffinityGroup( (profile.m_planType == AnalysisProfile.DISCOVERY_PLAN) ? m_PlanRequest.m_discoveryAttributeId : m_PlanRequest.m_entityId, affinityProfile.m_AffinityThreshold, affinityProfile.m_AffinityMaxNb);
			if( profile.m_RecomProfiles[ RecommendationGroup.SATTRIBUTES_RECOM] != null)
			{
					m_RecommendationProcess = new RecommendationProcess( m_PlanRequest, m_entities);
				//   m_RecommendationProcess.precompute();
			}

			if (affinityProfile.m_ReaderRef.equalsIgnoreCase( WPSDictionary.DEFAULT_NAME))
				retVal.add( m_PlanRequest.m_entityId);

			for (int i=0; i<m_entities.length; ++i)
			{
				retVal.add( m_entities[i].m_Id);
			}
		}
		return retVal;
	}
}

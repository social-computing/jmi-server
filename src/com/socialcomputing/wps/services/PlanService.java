package com.socialcomputing.wps.services;

import java.rmi.RemoteException;
import java.util.Hashtable;

import com.socialcomputing.wps.server.generator.PlanContainer;
import com.socialcomputing.wps.server.generator.PlanContainerFlex;
import com.socialcomputing.wps.server.webservices.maker.BeanPlanMaker;

/**
 * PlanService
 * Classe d'interface pour Flex. Cr�e le plan et l'envoie
 * @author anteika
 *
 */
public class PlanService {
	
	private PlanContainer 		_plan;
	private PlanContainerFlex	_planFlex;
	
	/**
	 * getMap() : Creates the PlanContainerFlex, which is readable by flex
	 * @param planName
	 * @param width
	 * @param height
	 * @param entityId
	 * @param attributeId
	 * @param classifyId
	 * @param analysisProfile
	 * @param affinityReaderProfile
	 * @param displayProfile
	 * @return PlanContainerFlex
	 */
	public PlanContainerFlex getPlan(String planName, Integer width, Integer height,String entityId,String attributeId, String classifyId, String analysisProfile,String affinityReaderProfile,String displayProfile)
	{
		// build params from Flex parameters
		Hashtable<String, Object> params = new Hashtable<String, Object>() ;
		params.put("planName", planName);
		params.put("width", String.valueOf(width));
		params.put("height", String.valueOf(height));
		if (entityId!=null) 
			params.put("entityId", entityId);
		if (attributeId!=null) params.put("attributeId", attributeId);
		if (classifyId!=null) params.put("classifyId", classifyId);
		if (analysisProfile!=null) params.put("analysisProfile", analysisProfile);
		if (affinityReaderProfile!=null) params.put("affinityReaderProfile", affinityReaderProfile);
		if (displayProfile!=null) params.put("displayProfile", displayProfile);
		params.put("PLAN_MIME", "text/java");
		
		BeanPlanMaker bpm = new BeanPlanMaker();

		// Build the PlanContainerFlex 
		_planFlex = new PlanContainerFlex();
		
		// Build the PlanContainer
		try {
			_planFlex.planName = planName;
			
			Hashtable<String, Object> results = bpm.createPlan( params);
			_plan = ( PlanContainer) results.get( "PLAN");
			
		} catch (RemoteException e) {
			_planFlex.errorMessage=e.getMessage();			
			e.printStackTrace();
		}
		
			
		_planFlex.parsePlan(_plan);
					
		return _planFlex;
	}
}

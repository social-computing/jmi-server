package com.socialcomputing.wps.server.analysisengine;

//import java.sql.Connection;
//import java.sql.DriverManager;
import java.util.Collection;

import com.socialcomputing.wps.server.affinityengine.RecommendationInterface;
import com.socialcomputing.wps.server.generator.ProtoPlan;
import com.socialcomputing.wps.server.webservices.PlanRequest;
//import com.socialcomputing.wps.server.plandictionary.Model;
/**
  * The analysis engine compute the entities and attributes, and all the statistical informations which are necessary to generate plan.

**********************************************************
Java
  * Class Name : AnalysisEngine
  * ---------------------------------------------------------
  * Filetype: (SOURCE)
  * Filepath: C:\Dvpt\com\voyezvous\wps\server\analysisengine\AnalysisEngine.java
  *
  *
  * ---------------------------------------------------
  * Author         : fvaletas
  * Creation Date  : Thur - Dec 28, 2000
  *
  * Change Log     :
  *
  * ********************************************************** */

public class Test
{


// Test method
public static void main(String [] args)
{
	//Connection connection = null;
	try {
		Class.forName( "org.gjt.mm.mysql.Driver");
		//connection = DriverManager.getConnection( "jdbc:mysql://saturne:3306/WPS?user=root");
	}
	catch( Exception e)
	{
		e.printStackTrace();
	}

	try {
		PlanRequest pr= null;//PlanRequest.CreateTestInstance( connection, "dhenin philippe");
		//pr.m_Dictionary.m_AffinityThreshold=50;

	/*	Model model = pr.getModel();
		model.m_AttributesMaxNb=150;
		model.m_EntitiesMaxNb=70;
		model.m_RecommendationMaxNb=10;
		model.m_RecommendationThreshold=50;
		model.m_AffinityMaxNb=2000;*/

		long t1= System.currentTimeMillis();

		RecommendationInterface rec= new RecommendationInterface(pr);

		Collection coll = rec.retrieveAffinityGroup();
		System.out.println("AffinityGrp:"+coll.size());


		long t2= System.currentTimeMillis();

		AnalysisProcess data= new AnalysisProcess(pr, coll, rec);
		long t3= System.currentTimeMillis();

		ProtoPlan plan=data.getProtoPlan();
		System.out.println("ProtoPlan:"+plan.m_attributes.length+":"+plan.m_entities.length+":"+plan.m_attLinks.length);

		long t4= System.currentTimeMillis();
		System.out.println("Time1:"+(t2-t1));
		System.out.println("Time2:"+(t3-t2));
		System.out.println("Time3:"+(t4-t3));
		System.out.println("GLOBALTime:"+(t4-t1));

		//System.out.println("ACnt"+m_RadData.getAttributesCnt());
		//System.out.println("Value"+m_RadData.getRadiation(1,46));
	}
	catch( Exception e)
	{
		e.printStackTrace();
	}
}
}

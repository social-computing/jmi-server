package com.socialcomputing.wps.server.webservices.maker;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.zip.GZIPOutputStream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.socialcomputing.utils.EZDebug;
import com.socialcomputing.utils.EZTimer;
import com.socialcomputing.utils.database.DatabaseHelper;
import com.socialcomputing.wps.server.affinityengine.RecommendationInterface;
import com.socialcomputing.wps.server.analysisengine.AnalysisProcess;
import com.socialcomputing.wps.server.generator.PlanContainer;
import com.socialcomputing.wps.server.generator.PlanGenerator;
import com.socialcomputing.wps.server.generator.ProtoPlan;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;
import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.webservices.PlanRequest;

public class BeanPlanMaker implements PlanMaker {

	private DataSource m_DataSource = null;
	//private boolean    m_IsMySQL = false;

	private class Steps
	{
		static final int PlanMakerStarted       = 0x00000001;
		static final int DictionaryLoaded       = 0x00000002;
		static final int DictionaryOpened       = 0x00000004;
		static final int AffinityGroupComputed  = 0x00000010;
		static final int AnalysisPassed         = 0x00000100;
		static final int PlanGenerated          = 0x00001000;
		static final int EnvInitialized         = 0x00002000;
	}

	public Hashtable createBinaryPlan( Hashtable params) throws RemoteException
	{
		Hashtable result = new Hashtable();
		try {
			EZDebug.setVerbosity( EZDebug.NONE );
			EZDebug.push();
			EZTimer timer = new EZTimer();

			ByteArrayOutputStream bout = new ByteArrayOutputStream( 32768);
			PlanContainer planContainer = createPlan( params, result);
			if( planContainer.m_env != null)
			{
				ObjectOutputStream objectOutStream = new ObjectOutputStream( new GZIPOutputStream( bout));
				objectOutStream.writeObject( planContainer.m_env);
				objectOutStream.writeObject( planContainer.m_plan);
				objectOutStream.close();
			}

			timer.showElapsedTime( "ALL STEPS" );
			EZDebug.pop();
			result.put( "PLAN", bout.toByteArray());
			result.put( "PLAN_MIME", "application/octet-stream");
			return result;
		}
		catch( Exception e)
		{
			throw new RemoteException( "WPS can't create plan " + (String)params.get( "planName") + " : " + e.getMessage());
		}
	}
	
	public PlanContainer createPlanContainer( Hashtable params) throws RemoteException
	{
		String result = new String();
		try {
			EZDebug.setVerbosity( EZDebug.NONE );
			EZDebug.push();
			EZTimer timer = new EZTimer();

			ByteArrayOutputStream bout = new ByteArrayOutputStream( 32768);
			PlanContainer planContainer = createPlan( params, null);

			timer.showElapsedTime( "ALL STEPS" );
			EZDebug.pop();

			return planContainer;
		}
		catch( Exception e)
		{
			throw new RemoteException( "WPS can't create plan " + (String)params.get( "planName") + " : " + e.getMessage());
		}		
	}
	
	public String createXmlPlan( Hashtable params) throws RemoteException
	{
		String result = new String();
		try {
			ProtoPlan proto = createProtoPlan( params, null);

			result += proto.getXML();

			return result;
		}
		catch( Exception e)
		{
			throw new RemoteException( "WPS can't create plan " + (String)params.get( "planName") + " : " + e.getMessage());
		}
	}

	private ProtoPlan createProtoPlan( Hashtable params, Hashtable result) throws RemoteException
	{
		EZDebug.setVerbosity( EZDebug.NONE );
		int 	status 		= Steps.PlanMakerStarted;
		Connection connection = null;
		WPSDictionary  dico = null;
		//PlanContainer container = null;
		PlanRequest planRequest = null;
		ProtoPlan proto;
		
		long startTime = System.currentTimeMillis();

		String name = ( String) params.get( "planName");
		if( name == null)
			throw new RemoteException( "WPS parameter 'planName' missing.");
		String x = ( String)params.get( "width");
		if( x != null && Integer.parseInt( x) == 0)
			throw new RemoteException( "WPS parameter 'width' can't be 0.");
		x = ( String) params.get( "height");
		if( x != null && Integer.parseInt( x) == 0)
		  throw new RemoteException( "WPS parameter 'height' can't be 0.");

		String useragent = ( String) params.get( "User-Agent");
		if( useragent == null) useragent = "<unknown>";

		x = ( String) params.get( "wpsDebugLevel");
		if( x != null)
		{
			switch( Integer.parseInt( x))
			{
				case 1:
					EZDebug.setVerbosity( EZDebug.LOW);
					break;
				case 2:
					EZDebug.setVerbosity( EZDebug.MED);
					break;
				case 3:
					EZDebug.setVerbosity( EZDebug.HIGH);
					break;
			}
		}

		try
		{
			connection = getConnection();

			
			
			
			DictionaryManagerImpl manager=  new DictionaryManagerImpl();
			Dictionary dictionaryLoader = manager.findByName(name);
			if (result!=null)
				result.put( "PLAN_NAME", name);

			// DICTIONARY RETRIEVAL
			dico = dictionaryLoader.getDictionary();
			status = Steps.DictionaryLoaded;

			// PLANREQUEST CREATION
			planRequest = new PlanRequest( connection, dico, params);
			if (result!=null)
			switch( planRequest.getAnalysisProfile().m_planType)
			{
				case AnalysisProfile.PERSONAL_PLAN:
					 result.put( "PLAN_TYPE", "PERSONAL");
					 break;
				case AnalysisProfile.GLOBAL_PLAN:
					 result.put( "PLAN_TYPE", "GLOBAL");
					 break;
				case AnalysisProfile.DISCOVERY_PLAN:
					 result.put( "PLAN_TYPE", "DISCOVERY");
					 break;
			}

			// PLANREQUEST CREATION
			dico.openConnections( params);
			status = Steps.DictionaryOpened;

			// AFFINITY GROUP RETRIEVAL
			RecommendationInterface  affinity = new RecommendationInterface ( planRequest);
			Collection affinityGroup = affinity.retrieveAffinityGroup();
			status = Steps.AffinityGroupComputed;

			// ANALYSIS MOTOR
			AnalysisProcess analysisEngine = new AnalysisProcess( planRequest, affinityGroup, affinity);
			proto = analysisEngine.getProtoPlan();
			status = Steps.AnalysisPassed;

			// PLAN GENERATOR
			PlanGenerator   planGenerator	= new PlanGenerator();
			planGenerator.generatePlan( proto, false );
			
			}
		catch(Exception e)
		{
			e.printStackTrace();
			recordPlanCreationInHistory( connection, name, null, null, params, useragent, System.currentTimeMillis() - startTime, status, e.getMessage());
			throw new RemoteException( e.getMessage());
		}
		finally
		{
			try {
				if( connection != null) connection.close();
				if( dico != null) dico.closeConnections();
			}
			catch(Exception e)
			{
			}
		}

		return proto;
	}	

	
	private PlanContainer createPlan( Hashtable params, Hashtable result) throws RemoteException
	{
		EZDebug.setVerbosity( EZDebug.NONE );
		int 	status 		= Steps.PlanMakerStarted;
		boolean	isVisual	= false;
		Connection connection = null;
		WPSDictionary  dico = null;
		PlanContainer container = null;
		PlanRequest planRequest = null;

		long startTime = System.currentTimeMillis();

		String name = ( String) params.get( "planName");
		if( name == null)
			throw new RemoteException( "WPS parameter 'planName' missing.");
		String x = ( String)params.get( "width");
		if( x != null && Integer.parseInt( x) == 0)
			throw new RemoteException( "WPS parameter 'width' can't be 0.");
		x = ( String) params.get( "height");
		if( x != null && Integer.parseInt( x) == 0)
		  throw new RemoteException( "WPS parameter 'height' can't be 0.");

		String useragent = ( String) params.get( "User-Agent");
		if( useragent == null) useragent = "<unknown>";

		x = ( String) params.get( "wpsDebugRelaxation");
		if( x != null && Integer.parseInt( x) == 1)
			isVisual = true;

		x = ( String) params.get( "wpsDebugLevel");
		if( x != null)
		{
			switch( Integer.parseInt( x))
			{
				case 1:
					EZDebug.setVerbosity( EZDebug.LOW);
					break;
				case 2:
					EZDebug.setVerbosity( EZDebug.MED);
					break;
				case 3:
					EZDebug.setVerbosity( EZDebug.HIGH);
					break;
			}
		}

		try
		{
			connection = getConnection();

			// EJB DICTIONARY RETRIEVAL
			DictionaryManagerImpl manager=  new DictionaryManagerImpl();
			Dictionary dictionaryLoader =manager.findByName(name);
			if (result!=null)
				result.put( "PLAN_NAME", name);

			// DICTIONARY RETRIEVAL
			dico = dictionaryLoader.getDictionary();
			status = Steps.DictionaryLoaded;

			// PLANREQUEST CREATION
			planRequest = new PlanRequest( connection, dico, params);
			if (result!=null)
			switch( planRequest.getAnalysisProfile().m_planType)
			{
				case AnalysisProfile.PERSONAL_PLAN:
					 result.put( "PLAN_TYPE", "PERSONAL");
					 break;
				case AnalysisProfile.GLOBAL_PLAN:
					 result.put( "PLAN_TYPE", "GLOBAL");
					 break;
				case AnalysisProfile.DISCOVERY_PLAN:
					 result.put( "PLAN_TYPE", "DISCOVERY");
					 break;
			}

			// PLANREQUEST CREATION
			dico.openConnections( params);
			status = Steps.DictionaryOpened;

			// AFFINITY GROUP RETRIEVAL
			RecommendationInterface  affinity = new RecommendationInterface ( planRequest);
			Collection affinityGroup = affinity.retrieveAffinityGroup();
			status = Steps.AffinityGroupComputed;

			// ANALYSIS MOTOR
			AnalysisProcess analysisEngine = new AnalysisProcess( planRequest, affinityGroup, affinity);
			ProtoPlan proto = analysisEngine.getProtoPlan();
			status = Steps.AnalysisPassed;

			// PLAN GENERATOR
			PlanGenerator   planGenerator	= new PlanGenerator();

			planGenerator.generatePlan( proto, isVisual );

			status = Steps.PlanGenerated;

			container = new PlanContainer( planGenerator.getEnv(), planGenerator.getPlan());

			recordPlanCreationInHistory( connection, name, planRequest.getAnalysisProfile().m_planType, planRequest.m_entityId, params, useragent, System.currentTimeMillis() - startTime);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			recordPlanCreationInHistory( connection, name, null, null, params, useragent, System.currentTimeMillis() - startTime, status, e.getMessage());
			throw new RemoteException( e.getMessage());
		}
		finally
		{
			try {
				if( connection != null) connection.close();
				if( dico != null) dico.closeConnections();
			}
			catch(Exception e)
			{
			}
		}

		return container;
	}


	private void recordPlanCreationInHistory( Connection connection, String plan, int type, String user, Hashtable params, String useragent, long duration)
	{
		String stype = null;
		switch ( type)
		{
			case AnalysisProfile.DISCOVERY_PLAN : stype = "DISCOVERY"; break;
			case AnalysisProfile.GLOBAL_PLAN : stype = "GLOBAL"; break;
			case AnalysisProfile.PERSONAL_PLAN : stype="PERSONAL"; break;
		}
		recordPlanCreationInHistory( connection, plan, stype, user, params, useragent, duration, 0, "");
	}
	private void recordPlanCreationInHistory( Connection connection, String plan, String type, String user, Hashtable params, String useragent, long duration, int status, String info)
	{
		try {
			InetAddress local = InetAddress.getLocalHost();
			PreparedStatement st = null;
			switch( DatabaseHelper.GetDbType( connection))
			{
				case DatabaseHelper.DB_MYSQL:
					st = connection.prepareStatement( "insert into " + WPSDictionary.getHistoryTableName( plan) + " (iduser, type, status, duration, server, parameters, info, agent, date) values( ?, ?, ?, ?, ?, ?, ?, ?, NOW())");
					break;
				case DatabaseHelper.DB_SQLSERVER:
					st = connection.prepareStatement( "insert into " + WPSDictionary.getHistoryTableName( plan) + " (iduser, type, status, duration, server, parameters, info, agent, date) values( ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())");
					break;
				case DatabaseHelper.DB_HSQL:
					st = connection.prepareStatement( "insert into " + WPSDictionary.getHistoryTableName( plan) + " (iduser, type, status, duration, server, parameters, info, agent, date) values( ?, ?, ?, ?, ?, ?, ?, ?, NOW())");
					break;
			}
			if( st != null)
			{
				st.setString( 1, ((user==null) ? "" : user));
				st.setString( 2, ((type==null) ? "UNDEFINED" : type));
				st.setInt( 3, status);
				st.setLong( 4, duration);
				st.setString( 5, local.getHostAddress());
				st.setString( 6, params.toString());
				st.setString( 7, ((info==null) ? "No information available" : info));
				st.setString( 8, ((useragent==null) ? "" : useragent));
				st.executeUpdate();
				st.close();
			}
			else
				throw new RemoteException( "DB Type not supported");
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	private Connection getConnection() throws SQLException, RemoteException
	{
		if( m_DataSource == null)
		{
			try {
				Context context = new InitialContext();
				m_DataSource = (DataSource) context.lookup("java:comp/env/jdbc/WPSPooledDS");
			}
			catch (NamingException e)
			{
				throw new RemoteException("Could not obtain WPS DataSource: " + e.getMessage());
			}
		}
		Connection connection = m_DataSource.getConnection();
		//if( connection.getMetaData().getDatabaseProductName().equalsIgnoreCase( "MySQL"))
		//	m_IsMySQL = true;
		return connection;
	}
	
}

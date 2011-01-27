package com.socialcomputing.wps.server.affinityengine.scheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import javax.naming.Context;
import javax.naming.InitialContext;
//import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

import com.socialcomputing.wps.server.plandictionary.WPSDictionary;

/**
 * Title:        Affinity Scheduler
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class PlanAffinityThread extends Thread
{
	private String m_Name = null;

	public PlanAffinityThread( String name)
	{
		m_Name = name;
	}

	public void run()
	{
		//AffinitySchedulerHome  affinitySchedulerHome = null;
		//AffinityScheduler      scheduler = null;
		BeanAffinityScheduler scheduler = new BeanAffinityScheduler();

		Connection             connection = null;
		Statement              stmt = null;
		try {
			Context context = new InitialContext();

			DataSource dataSource = (DataSource) context.lookup("java:comp/env/jdbc/WPSPooledDS");
			connection = dataSource.getConnection();

			StringBuffer sbDelete = new StringBuffer( "delete from " + WPSDictionary.getCoefficientQueuingTableName( m_Name) + " where id in(");
			stmt = connection.createStatement();

			HashSet set = new HashSet();
			boolean doAll = false, bFirst = true;
			ResultSet rs = stmt.executeQuery( "select id from " + WPSDictionary.getCoefficientQueuingTableName( m_Name));
			while( rs.next() && !doAll)
			{
				String id = rs.getString( 1);
				if( id.equals(""))
					doAll = true;
				else
				{
					set.add( id);
					if( !bFirst)
						sbDelete.append( ',');
					else
						bFirst = false;
					sbDelete.append( id);
				}
			}
			rs.close();
			if( !doAll && !set.isEmpty())
			{
				long t1= System.currentTimeMillis();
				System.out.println( (new java.util.Date()).toString() + " Affinity Scheduler : " + m_Name + " : " + set.toString());
				// Delete ids
				sbDelete.append( ')');
				stmt.execute( sbDelete.toString());
				// Compute
				//scheduler = affinitySchedulerHome.create();
				scheduler.updateNow( m_Name, set);
				long t2= System.currentTimeMillis();
				System.out.println( (new java.util.Date()).toString() + " Affinity Scheduler : " + m_Name + " ended " + (t2-t1) + "ms");
			}
			if( doAll)
			{
				long t1= System.currentTimeMillis();
				System.out.println( "Affinity Scheduler : " + m_Name + " : [all]");
				// Delete all ids
				stmt.executeUpdate( "delete from " + WPSDictionary.getCoefficientQueuingTableName( m_Name));
				// Compute
				//scheduler = affinitySchedulerHome.create();
				scheduler.updateAllNow( m_Name);
				long t2= System.currentTimeMillis();
				System.out.println( "Affinity Scheduler : " + m_Name + " ended " + (t2-t1) + "ms");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				/*affinitySchedulerHome = null;
				if( scheduler != null)
					scheduler.remove();*/
				if( connection != null)
					connection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}





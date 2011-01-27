package com.socialcomputing.wps.server.plandictionary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.utils.StringAndFloat;
import com.socialcomputing.wps.server.webservices.PlanRequest;
import com.socialcomputing.utils.database.DatabaseHelper;

/**
  * Default WPS affinity group reader
  *
  * **********************************************************
  * Java Class Name : AffinityEngine
  * ---------------------------------------------------------
  * Filetype: (SOURCE)
  * Filepath: C:\Dvpt\src\com\voyezvous\wps\server\affinityengine\AffinityEngine.java
  *
  *
  * ********************************************************** */

public class DefaultAffinityGroupReader implements iAffinityGroupReader, java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7591179911869122351L;
	private PlanRequest m_PlanRequest = null;
	private transient DatabaseHelper m_databaseHelper = null;

	public void instanciate( Connection connection, PlanRequest planRequest)
	{
		m_databaseHelper = new DatabaseHelper( connection, false);
		m_PlanRequest = planRequest;
	}

/**
  * select id2 in NomdelaTable where id1 = 'entity' and coeff <= 'seuil' order by coeff
  * +
  * select id1 in NomdelaTable where id2 = 'entity' and coeff <= 'seuil' order by coeff
  *
  */
	public  StringAndFloat [] retrieveAffinityGroup( String entity, int affinityThreshold, int affinityMaxNb)
	{
		ArrayList eList = new ArrayList();
		float threshold= (float)affinityThreshold/(float)100;

		try
		{
			StringBuffer query1 = new StringBuffer( 512);
			StringBuffer query2 = new StringBuffer( 512);
			switch( m_databaseHelper.GetDbType())
			{
			case DatabaseHelper.DB_MYSQL:
				query1.append( "select HIGH_PRIORITY SQL_SMALL_RESULT id1, ponderation from ").append( WPSDictionary.getCoefficientTableName( m_PlanRequest.m_Dictionary.m_Name)).append( " where (id2 = '").append( entity).append( "') and (ponderation <").append( threshold).append( ") order by 2 limit 0,").append( affinityMaxNb);
				query2.append( "select HIGH_PRIORITY SQL_SMALL_RESULT id2, ponderation from ").append( WPSDictionary.getCoefficientTableName( m_PlanRequest.m_Dictionary.m_Name)).append( " where (id1 = '").append( entity).append( "') and (ponderation <").append( threshold).append( ") order by 2 limit 0,").append( affinityMaxNb);
				break;
			case DatabaseHelper.DB_SQLSERVER:
				query1.append( "select top ").append( affinityMaxNb).append( " id1, ponderation from ").append( WPSDictionary.getCoefficientTableName( m_PlanRequest.m_Dictionary.m_Name)).append( " where (id2 = '").append( entity).append( "') and (ponderation <").append( threshold).append( ") order by 2");
				query2.append( "select top ").append( affinityMaxNb).append( " id2, ponderation from ").append( WPSDictionary.getCoefficientTableName( m_PlanRequest.m_Dictionary.m_Name)).append( " where (id1 = '").append( entity).append( "') and (ponderation <").append( threshold).append( ") order by 2");
				break;
			}
			Statement stat1 = m_databaseHelper.connection.createStatement();
			ResultSet rs1 = stat1.executeQuery( query1.toString());
			Statement stat2 = m_databaseHelper.connection.createStatement();
			ResultSet rs2 = stat2.executeQuery( query2.toString());

			int cnt=0;
			float pond1 = Float.MAX_VALUE, pond2 = Float.MAX_VALUE;
			boolean go1 = true, go2 = true;
			while (++cnt <= affinityMaxNb)
			{
				if( go1)
					pond1 =  rs1.next() ? rs1.getFloat( 2) : Float.MAX_VALUE;
				if( go2)
					pond2 = rs2.next() ? rs2.getFloat( 2) : Float.MAX_VALUE;

				if ((pond1==Float.MAX_VALUE) && (pond2==Float.MAX_VALUE))
				   break;
				if ( pond1 < pond2)
				{
				   eList.add( new StringAndFloat( rs1.getString( 1), pond1));
				   go1 = true;
				   go2 = false;
			   }
				else
				{
					eList.add( new StringAndFloat( rs2.getString( 1), pond2));
					go1 = false;
					go2 = true;
				}
			}

			rs1.close();
			rs2.close();
			stat1.close();
			stat2.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		m_databaseHelper.close();
		return ( StringAndFloat[]) eList.toArray( new StringAndFloat[0]);
	}

	public static void main(String args[])
	{
		try
		{
			Connection connection = null;
			Class.forName( "org.gjt.mm.mysql.Driver");
			connection = DriverManager.getConnection( "jdbc:mysql://saturne:3306/WPS?user=boosol&password=boosol");

			int test[] = { 7,49,88,125,149,168,205,568,345,412,589,1024,1587,1789,2001,2005,2312,2587,2688,3000,3001};

			DefaultAffinityGroupReader ar = new DefaultAffinityGroupReader();
			WPSDictionary dico = WPSDictionary.CreateTestInstance( "BooSol");
			ar.instanciate( connection, new PlanRequest( null, dico, new Hashtable()));

			Statement st  = connection.createStatement();
			long t1 = System.currentTimeMillis();
			for( int i = 0; i < 1000; ++i)
				st.executeUpdate( "insert into " + WPSDictionary.getCoefficientTableName( dico.m_Name) + "(id1, id2, ponderation) values( '"+i+"', '333', 0)");
			//for( int i = 0; i < test.length; ++i)
			//	ar.retrieveAffinityGroup( "777" , 100, 1000);
			long t2 = System.currentTimeMillis();
			//System.out.println( "Temps moyen " + test.length + " requ�tes : " + (t2 - t1)/test.length + "ms");
			System.out.println( "Temps moyen " + test.length + " requ�tes : " + (t2 - t1) + "ms");
			connection.close();
		}
		 catch (Exception e) { e.printStackTrace();}
		 System.exit(1);
	}

}

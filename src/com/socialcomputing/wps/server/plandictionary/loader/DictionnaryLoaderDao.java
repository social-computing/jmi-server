package com.socialcomputing.wps.server.plandictionary.loader;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.utils.database.DatabaseHelper;

/**
 * Title:        DictionaryLoader
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class DictionnaryLoaderDao 
{
	
	//private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private Connection getConnection() throws SQLException, RemoteException
	{
		DataSource m_DataSource = null;
		try {
			Context context = new InitialContext();
			m_DataSource = (DataSource) context.lookup("java:comp/env/jdbc/WPSPooledDS");
		}
		catch (NamingException e)
		{
			throw new RemoteException("WPS : could not obtain DataSource: " + e);
		}
		
		return m_DataSource.getConnection();
	}
	
	public Collection findAll() throws RemoteException
	{
		ArrayList cdl = new ArrayList ();;
		
		BeanDictionaryLoader dl = null;		
		String query ="select * from dictionaries";
		Connection conn = null;
		PreparedStatement ps =null;
		ResultSet  rs = null;
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			rs= ps.executeQuery();
			while (rs.next()) {
				dl =  new BeanDictionaryLoader(rs.getString("name"), rs.getString("dictionary"), rs.getString("filteringdate"));
				cdl.add(dl);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("",e);
		}
		finally{
			try { 
				rs.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try { 
				ps.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		
		return cdl;
	}
	
	public BeanDictionaryLoader findByName(String name) throws RemoteException{
		BeanDictionaryLoader dl = null;
		String query ="select * from dictionaries where name=?";
		Connection conn = null;
		PreparedStatement ps =null;
		ResultSet  rs = null;
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, name);
			rs= ps.executeQuery();
			if (rs.next()) {
				dl =  new BeanDictionaryLoader(rs.getString("name"), rs.getString("dictionary"), rs.getString("filteringdate"));
			}
		}
		catch (Exception e) {
			throw new RemoteException("",e);
		}
		finally{
			try { 
				rs.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try { 
				ps.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dl;
	}
	
	public DictionaryLoader create(String name) throws RemoteException{
		
		String query ="insert into  dictionaries (name) values (?)";
		Connection conn = null;
		PreparedStatement ps =null;
		Statement st  =null;
		
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			
			ps.setString(1, name);
			
			ps.executeUpdate();
			String coefTable = WPSDictionary.getCoefficientTableName( name);
			String queueTable = WPSDictionary.getCoefficientQueuingTableName( name);
			String histoTable = WPSDictionary.getHistoryTableName( name);
			
			st = conn.createStatement();
			st.execute( "create table " +  coefTable + " (id1 varchar(255) not null, id2 varchar(255) not null, ponderation float)" );
			st.execute( "create index id1 on " + coefTable + " (id1 , ponderation)" );
			st.execute( "create index id2 on " + coefTable + " (id2 , ponderation)" );
			switch( DatabaseHelper.GetDbType( conn))
			{
				case DatabaseHelper.DB_MYSQL:
					 st.execute( "create table " + queueTable + " (id varchar(255) not null, date timestamp)" );
					 st.execute( "create table " + histoTable + " (iduser varchar(255) not null, type varchar(255) not null default 'UNDEFINED', date timestamp, status integer, duration integer default 0, server varchar(50) default null, parameters text, info text, agent varchar(200))" );
					 break;
				case DatabaseHelper.DB_SQLSERVER:
					 st.execute( "create table " + queueTable + " (id varchar(255) not null, date DATETIME DEFAULT (getdate()))" );
					 st.execute( "create table " + histoTable + " (iduser varchar(255) not null, type varchar(255) not null default 'UNDEFINED', date DATETIME, status integer, duration integer default 0, server varchar(50) default null, parameters text, info text, agent varchar(200))" );
					 break;
				case DatabaseHelper.DB_HSQL:
					st.execute( "create table " + queueTable + " (id varchar(255) not null, date timestamp)" );
					st.execute( "create table " + histoTable + " (iduser varchar(255) not null, type varchar(255) default 'UNDEFINED' not null, date timestamp, status integer, duration integer default 0, server varchar(50) default null, parameters LONGVARCHAR(8000), info LONGVARCHAR(8000), agent varchar(200))" );
			}
			st.execute( "create index id on " + queueTable + " (id)" );
			st.execute( "create index iduser on " + histoTable + " (iduser)" );
			st.execute( "create index type on " + histoTable + " (type)" );
			//conn.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			/*try { 
				conn.rollback();
			} catch (SQLException se) {
				se.printStackTrace();
			}*/
			throw new RemoteException("Unable to create dictionary", e);
		}
		finally{
			try { 
				ps.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try { 
				st.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new BeanDictionaryLoader(name, null, null);
	}
	
	public void update(DictionaryLoader dl) throws RemoteException{
		String query ="update  dictionaries set dictionary=? ,filteringdate=? where name=?";
		Connection conn = null;
		PreparedStatement ps =null;	
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, dl.getDictionaryDefinition());
			String date =  "";
			try {
				date = dl.getNextFilteringDate().toString();
			} catch (RuntimeException e) {
				
			}
			ps.setString(2, date);
			ps.setString(3, dl.getName());
			ps.executeUpdate();
		}
		catch (Exception e) {
			try { 
				conn.rollback();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			throw new RemoteException("Unable to create dictionary", e);
		}
		finally{
			try { 
				ps.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void delete(String name) {
		
		String query ="delete from dictionaries where name=?";
		Connection conn = null;
		PreparedStatement ps =null;
		Statement st = null;
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, name);
			ps.executeUpdate();
			st = conn.createStatement();
			st.execute( "drop table " + WPSDictionary.getCoefficientTableName( name));
			st.execute( "drop table " + WPSDictionary.getCoefficientQueuingTableName( name));
			st.execute( "drop table " + WPSDictionary.getHistoryTableName( name));
			conn.commit();
		}
		catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
		}
		finally{
			try { 
				ps.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try { 
				st.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
}

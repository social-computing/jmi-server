package com.socialcomputing.wps.server.swatchs.loader;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//import com.socialcomputing.wps.server.plandictionary.loader.DictionaryLoader;

//import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
//import com.socialcomputing.wps.server.plandictionary.loader.BeanDictionaryLoader;

public class SwatchLoaderDao {

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
	
	public BeanSwatchLoader findByName(String name) throws RemoteException
	{
		BeanSwatchLoader dl = null;
		String query ="select * from swatchs where name=?";
		Connection conn = null;
		PreparedStatement ps =null;
		ResultSet  rs = null;
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, name);
			rs= ps.executeQuery();
			if (rs.next()) {
				String sname = rs.getString("name");
				String sswatch = rs.getString("swatch");
				dl =  new BeanSwatchLoader(sname, sswatch);
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

	public Collection findAll() throws RemoteException
	{
		ArrayList cdl = new ArrayList();
		
		BeanSwatchLoader dl = null;		
		String query ="select * from swatchs";
		Connection conn = null;
		PreparedStatement ps =null;
		ResultSet  rs = null;
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			rs= ps.executeQuery();
			while (rs.next()) {
				dl =  new BeanSwatchLoader(rs.getString("name"), rs.getString("swatch"));
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
	
	public BeanSwatchLoader create(String name) throws RemoteException{
		
		String query ="insert into swatchs (name) values (?)";
		Connection conn = null;
		PreparedStatement ps =null;
		
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);			
			ps.setString(1, name);	
			//ps.executeQuery();
			ps.executeUpdate();
			//conn.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			/*try { 
				conn.rollback();
			} catch (SQLException se) {
				se.printStackTrace();
			}*/
			throw new RemoteException("Unable to create swatch", e);
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
		return new BeanSwatchLoader(name, null);
	}	
	
	public void update(SwatchLoader dl) throws RemoteException{
		String query ="update  swatchs set swatch=? where name=?";
		Connection conn = null;
		PreparedStatement ps =null;	
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, dl.getSwatchDefinition() );			
			ps.setString(2, dl.getName());
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
		
		String query ="delete from swatchs where name=?";
		Connection conn = null;
		PreparedStatement ps =null;
		Statement st = null;
		try {
			conn =  getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, name);
			ps.executeUpdate();
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

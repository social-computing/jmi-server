//
// -- Java Code Generation Process --

package com.socialcomputing.wps.server.web;

// Import Statements
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;

//import javax.naming.NamingException;

import com.socialcomputing.wps.server.webservices.maker.BeanPlanMaker;
import com.socialcomputing.wps.server.webservices.maker.PlanMaker;
//import com.socialcomputing.wps.server.webservices.maker.PlanMaker;
//import com.socialcomputing.wps.server.webservices.maker.PlanMakerHome;

import javax.servlet.*;
import javax.servlet.http.*;
//import javax.xml.registry.DeleteException;

public class WPSPlanMakerServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1343674940328653700L;
	static Hashtable m_bufferedPlans = new Hashtable();

	/*
	   Method:  doGet
	*/
	public void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost( request, response);
	}

	/*
	   Method:  doPost
	*/
	public void doPost( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String outputType = (String) request.getParameter( "WPSOutputType");
		if( outputType == null)
			doPostBinary( request, response);
		else if( outputType.equals( "text/html"))
			doPostHTML( request, response);
		else if( outputType.equals( "xml"))
			doPostXML( request, response);
		else
			doPostBinary( request, response);
		response.setStatus( HttpServletResponse.SC_OK);
	}

	private void doPostBinary( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		DataOutputStream out = new DataOutputStream( response.getOutputStream());

		try
		{
			PlanMaker planmaker = new BeanPlanMaker();
			
			Hashtable<String, String> params = new Hashtable<String, String>();
			Enumeration enumvar = request.getParameterNames();
			while( enumvar.hasMoreElements())
			{
				String name = ( String)enumvar.nextElement();
				params.put( name, request.getParameter( name));
			}
			params.put( "User-Agent", request.getHeader( "User-Agent"));
			params.put( "User-Agent", request.getHeader( "User-Agent") == null ? "unknown" : request.getHeader( "User-Agent"));

			Hashtable<String, Object> result = null;
			String  bufferName = request.getParameter( "bufferedPlan");
			if( bufferName!=null) // Si demande de bufferisation globale
			{
				String timeOutStr = request.getParameter( "bufferTimeout");
				long timeout = timeOutStr!=null ? Integer.parseInt( timeOutStr) : 3600*24;

				long bufferedPlanDate = 0;
				Hashtable bufferedPlan=(Hashtable)m_bufferedPlans.get( bufferName);
				if ( bufferedPlan != null)
				   bufferedPlanDate=((Long)m_bufferedPlans.get(bufferName+"Date")).longValue();

				if (( bufferedPlan == null) || (System.currentTimeMillis() - bufferedPlanDate> 1000*timeout))
				{
					result = planmaker.createPlan( params);
					m_bufferedPlans.put(bufferName, result);
					m_bufferedPlans.put(bufferName+"Date", new Long(System.currentTimeMillis()));
				}
				else
				{
					result=bufferedPlan;
				}
			}
			else  result = planmaker.createPlan( params);


			response.setContentType( (String)result.get( "PLAN_MIME"));
			byte[] bplan = ( byte[])result.get( "PLAN");
			out.writeInt( bplan.length);
			out.write( bplan);

			//planmaker.remove();
		}
		catch( Exception e)
		{
			response.setContentType("application/octet-stream");
			out.writeInt( -1);
			StringWriter    writer  = new StringWriter();
			e.printStackTrace( new PrintWriter( writer ));
			out.writeUTF( writer.toString());
			throw new ServletException( "Servlet : create plan error : " + e.getMessage());
		}
		finally
		{
			out.close();
		}
	}

	private void doPostXML( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		
		out.println("<wpsplan>");

		try
		{
			PlanMaker planmaker = new BeanPlanMaker();
			
			Hashtable<String, String> params = new Hashtable<String, String>();
			Enumeration<String> enumvar = request.getParameterNames();
			while( enumvar.hasMoreElements())
			{
				String name = enumvar.nextElement();
				String value = request.getParameter( name);
				params.put( name, value);
			}
			params.put( "User-Agent", request.getHeader( "User-Agent"));
			params.put( "User-Agent", request.getHeader( "User-Agent") == null ? "unknown" : request.getHeader( "User-Agent"));
			params.put("PLAN_MIME", "text/xml");
			
			Hashtable<String, Object> result = planmaker.createPlan( params);
			out.println( ( String) result.get("PLAN"));
		}
		catch(Exception e)
		{
			out.println("<error>"+e.getMessage()+"</error>");
		}
		
		out.println("</wpsplan>");
		out.close();
	}
	
	
	private void doPostHTML( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<head><title>WPS Plan Maker Servlet</title></head>");
		out.println("<body>");
		out.println("<br><br><br>");

		try
		{
			PlanMaker planmaker = new BeanPlanMaker();
			
			String searchName = (String) request.getParameter( "planName");
			if ( (searchName == null) || (searchName.length() == 0))
			{
				out.println("<H3><CENTER>"+"Please enter input "+"</CENTER></H3>");
			}
			else
			{
				out.println("<H3><CENTER> Recherche : " + searchName + "</CENTER></H3>");
				Hashtable<String, String> params = new Hashtable<String, String>();
				Enumeration enumvar = request.getParameterNames();
				while( enumvar.hasMoreElements())
				{
					String name = ( String)enumvar.nextElement();
					String value = request.getParameter( name);
					out.println("<H3><CENTER> Param�tre : "+ name + " : "+ value + "</CENTER></H3>");
					if( value != null)
						params.put( name, value);
				}
				params.put( "User-Agent", request.getHeader( "User-Agent"));
				planmaker.createPlan( params);
				out.println("<H3><CENTER>"+ "Plan created" + "</CENTER></H3>");
			}
			//planmaker.remove();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			out.println("An exception occurred during EJB call " + e.getMessage());
		}
		finally
		{
			out.println("</BODY>");
			out.println(" </HTML>");
			out.close();
		}
	}
}

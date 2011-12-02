//
// -- Java Code Generation Process --

package com.socialcomputing.wps.server.web;

// Import Statements
import java.io.IOException;
import java.util.HashSet;

//import javax.naming.NamingException;
import javax.servlet.http.*;
import javax.servlet.*;

//import com.socialcomputing.wps.server.affinityengine.scheduler.AffinityScheduler;
//import com.socialcomputing.wps.server.affinityengine.scheduler.AffinitySchedulerHome;
import com.socialcomputing.wps.server.affinityengine.scheduler.BeanAffinityScheduler;

public class WPSSchedulerServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7439982138709806871L;
	//AffinitySchedulerHome m_AffinitySchedulerHome = null;

	public void init()
	{
		/*try
		{
			javax.naming.Context  context = new javax.naming.InitialContext();
			Object ref = context.lookup( "java:comp/env/ejb/WPSAffinityScheduler");
			m_AffinitySchedulerHome = ( AffinitySchedulerHome) javax.rmi.PortableRemoteObject.narrow(ref, AffinitySchedulerHome.class);
		}
		catch( NamingException e)
		{
			  log( "WPSSchedulerServlet unable to find AffinityScheduler EJB");
		}*/
	}

	public void destroy()
	{
		//m_AffinitySchedulerHome = null;
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost( request, response);
	}

	public void doPost( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doSchedule( request, response);
		response.setStatus( HttpServletResponse.SC_OK);
	}

	private void doSchedule( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//AffinityScheduler scheduler = null;
		BeanAffinityScheduler scheduler = new BeanAffinityScheduler();
		String plan = request.getParameter( "plan");
		if( plan == null)
			throw new ServletException( "WPSSchedulerServlet : missing plan name.");
		String action = request.getParameter( "action");
		if( action == null)
			throw new ServletException( "WPSSchedulerServlet : missing action.");
		String id = request.getParameter( "id");
		try
		{
			//scheduler = m_AffinitySchedulerHome.create();
			if( action.equalsIgnoreCase( "allNow"))
			{
				scheduler.updateAllNow( plan);
			}
			else if( action.equalsIgnoreCase( "all"))
			{
				scheduler.requestUpdateAll( plan);
			}
			else if( action.equalsIgnoreCase( "delete"))
			{
				scheduler.deleteNow( plan, id);
			}
			else if( action.equalsIgnoreCase( "thisNow"))
			{
				HashSet s = new HashSet();
				s.add( id);
				scheduler.updateNow( plan, s);
			}
			else if( action.equalsIgnoreCase( "this"))
			{
				scheduler.requestUpdate( plan, id);
			}
			//scheduler.remove();
		}
		catch( Exception e)
		{
			e.printStackTrace();
			throw new ServletException( "WPSSchedulerServlet : schedule error : " + e.getMessage());
		}
		finally
		{
			//out.close();
			scheduler = null;
		}
	}

}

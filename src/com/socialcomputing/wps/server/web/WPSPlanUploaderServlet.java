//
// -- Java Code Generation Process --

package com.socialcomputing.wps.server.web;

// Import Statements
import java.io.CharArrayWriter;
import java.io.IOException;
import java.rmi.RemoteException;

//import javax.naming.NamingException;
import javax.servlet.http.*;
import javax.servlet.*;

import com.socialcomputing.wps.server.plandictionary.loader.DictionaryLoader;
import com.socialcomputing.wps.server.plandictionary.loader.DictionnaryLoaderDao;
import com.socialcomputing.wps.server.plandictionary.loader.DictionnaryManager;
import com.socialcomputing.utils.servlet.ExtendedRequest;
import com.socialcomputing.utils.servlet.UploadedFile;
//import com.socialcomputing.wps.server.plandictionary.loader.DictionaryLoaderHome;

public class WPSPlanUploaderServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2684650116528241125L;
	//DictionaryLoaderHome m_DicLoaderHome = null;

	public void init()
	{
		/*try
		{
			javax.naming.Context  context = new javax.naming.InitialContext();
			Object ref = context.lookup( "java:comp/env/ejb/WPSDictionaryLoader");
			m_DicLoaderHome = ( DictionaryLoaderHome) javax.rmi.PortableRemoteObject.narrow(ref, DictionaryLoaderHome.class);
		}
		catch( NamingException e)
		{
			  log( "WPSPlanUploaderServlet unable to find WPSDictionaryLoader EJB");
		}*/
	}

	public void destroy()
	{
		//m_DicLoaderHome = null;
	}

	/*
	   Method:  doPost
	*/
	public void doPost( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ExtendedRequest exRequest = new ExtendedRequest( request);
		int status = HttpServletResponse.SC_BAD_REQUEST;
		String action = exRequest.getParameter( "action");
		if( action != null)
		{
			if( action.equalsIgnoreCase( "set"))
			{
				doUploadDictionary( exRequest, response);
				status = HttpServletResponse.SC_OK;
			}
			if( action.equalsIgnoreCase( "delete"))
			{
				doDeleteDictionary( exRequest, response);
				status = HttpServletResponse.SC_OK;
			}
		}
		response.setStatus( status);
	}

	private void doUploadDictionary( ExtendedRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		DictionaryLoader loader = null;
		DictionnaryManager manager = null;
		try
		{
			UploadedFile uf = request.getFileParameter( "dictionary");
			if( uf != null)
			{
				org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( true);
				org.jdom.Document doc = builder.build( uf.getInputStream(), "file:./applications/WPS/META-INF/");

				org.jdom.Element dicoNode = doc.getRootElement();

				String name = dicoNode.getAttributeValue( "name");
				try {
					//loader = m_DicLoaderHome.findByPrimaryKey( name);
					manager =  new DictionnaryManager();
					loader = manager.findByName(name);
				}
				catch( RemoteException e)
				{
					loader = manager.create(name);
					//loader = m_DicLoaderHome.create( name);
				}

				//ByteArrayOutputStream bout = new ByteArrayOutputStream();
				CharArrayWriter bout = new CharArrayWriter();
				org.jdom.output.XMLOutputter op = new org.jdom.output.XMLOutputter();
				op.output( dicoNode, bout);

				loader.setDictionaryDefinition( bout.toString());
				
			}
			else
				throw new ServletException( "WPSPlanUploaderServlet : No dictionary");
		}
		catch( Exception e)
		{
			e.printStackTrace();
			throw new ServletException( "WPSPlanUploaderServlet : upload plan error : " + e.getMessage());
		}
		finally
		{
			loader = null;
		}
	}

	private void doDeleteDictionary( ExtendedRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//DictionaryLoader loader = null;
		try
		{
			String name = request.getParameter( "dictionary");
			if( name != null)
			{
				//try {
					DictionnaryManager manager =  new DictionnaryManager();
					manager.delete(name);
					//loader = m_DicLoaderHome.findByPrimaryKey( name);
					//loader.remove();
//				}
//				catch( javax.ejb.ObjectNotFoundException e)
//				{
//				}
			}
			else
				throw new ServletException( "WPSPlanUploaderServlet : No dictionary");
		}
		catch( Exception e)
		{
			e.printStackTrace();
			throw new ServletException( "WPSPlanUploaderServlet : upload plan error : " + e.getMessage());
		}
		finally
		{
			//loader = null;
		}
	}



}

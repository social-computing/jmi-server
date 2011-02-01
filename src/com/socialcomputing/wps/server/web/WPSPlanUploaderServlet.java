//
// -- Java Code Generation Process --

package com.socialcomputing.wps.server.web;

// Import Statements
import java.io.CharArrayWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.utils.servlet.ExtendedRequest;
import com.socialcomputing.utils.servlet.UploadedFile;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;

public class WPSPlanUploaderServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2684650116528241125L;

	/*
	   Method:  doPost
	*/
	public void doPost( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HibernateUtil.currentSession();
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
		HibernateUtil.closeSession();
		response.setStatus( status);
	}

	private void doUploadDictionary( ExtendedRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			UploadedFile uf = request.getFileParameter( "dictionary");
			if( uf != null)
			{
				org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( true);
				org.jdom.Document doc = builder.build( uf.getInputStream(), "file:./applications/WPS/META-INF/");

				org.jdom.Element dicoNode = doc.getRootElement();

				String name = dicoNode.getAttributeValue( "name");
				DictionaryManager manager =  new DictionaryManagerImpl();
				Dictionary dictionary = manager.findByName(name);
				if( dictionary == null) {
					dictionary = manager.create( name, null);
				}

				//ByteArrayOutputStream bout = new ByteArrayOutputStream();
				CharArrayWriter bout = new CharArrayWriter();
				org.jdom.output.XMLOutputter op = new org.jdom.output.XMLOutputter();
				op.output( dicoNode, bout);

				dictionary.setDefinition( bout.toString());
				manager.update( dictionary);
			}
			else
				throw new ServletException( "WPSPlanUploaderServlet : No dictionary");
		}
		catch( Exception e)
		{
			e.printStackTrace();
			throw new ServletException( "WPSPlanUploaderServlet : upload plan error : " + e.getMessage());
		}
	}

	private void doDeleteDictionary( ExtendedRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			String name = request.getParameter( "dictionary");
			if( name != null)
			{
				DictionaryManager manager =  new DictionaryManagerImpl();
				manager.remove( name);
			}
			else
				throw new ServletException( "WPSPlanUploaderServlet : No dictionary");
		}
		catch( Exception e)
		{
			e.printStackTrace();
			throw new ServletException( "WPSPlanUploaderServlet : upload plan error : " + e.getMessage());
		}
	}



}

package com.socialcomputing.wps.server.web;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.utils.servlet.ExtendedRequest;
import com.socialcomputing.utils.servlet.UploadedFile;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.DictionaryManager;
import com.socialcomputing.wps.server.persistence.Swatch;
import com.socialcomputing.wps.server.persistence.SwatchManager;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;
import com.socialcomputing.wps.server.persistence.hibernate.SwatchManagerImpl;
/**
 * Title:        Users
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VOYEZ VOUS
 * @author
 * @version 1.0
 */

public class WPSUploadServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1362617337569904439L;

	public long getLastModified(HttpServletRequest request)
	{
		return System.currentTimeMillis();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String action = request.getParameter( "action");
		if( action != null)
		{
		}
		response.sendError( HttpServletResponse.SC_BAD_REQUEST);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HibernateUtil.currentSession();
		ExtendedRequest exrequest = new ExtendedRequest( request);
		String action = exrequest.getParameter( "action");
		if( action != null)
		{
			if( action.equalsIgnoreCase( "uploadSearchFile"))
			{
				InternalReport report = uploadDefinitionFile( exrequest.getFileParameter( "definitionFile"));
				HttpSession session = request.getSession();
				session.setAttribute( "UploadDefinitionFileResults", report);

				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.print( "<html><head><meta http-equiv=\"Refresh\" content=\"0; URL=");
				out.print( exrequest.getParameter( "redirect"));
				out.print( "\"></head></html>");
				out.close();
				HibernateUtil.closeSession();
				response.setStatus( HttpServletResponse.SC_OK);
				return;
			}
		}
		HibernateUtil.closeSession();
		response.sendError( HttpServletResponse.SC_BAD_REQUEST);
	}

	private InternalReport uploadDefinitionFile( UploadedFile file) throws ServletException, IOException
	{
		InternalReport report = new InternalReport();
		if( file == null) return report;
		try
		{
			if( file.getContentFilename().endsWith( ".xml"))
			{
				org.jdom.input.SAXBuilder saxBuilder = new org.jdom.input.SAXBuilder( true);
				saxBuilder.setEntityResolver( new WPSResolver());
				uploadFile( saxBuilder, report, file.getContentFilename(), new String( file.getBytes()));
			}
			else
			   uploadZipFile( report, file.getBytes());
		}
		catch( Exception e)
		{
			e.printStackTrace();
			report.setLastActionResult( "Error uploadDefinitionFile : " + e.getMessage());
		}
		return report;
	}
	private class WPSResolver implements EntityResolver
	{
		Hashtable<String, String> m_dtds = null;
		public WPSResolver()
		{
			m_dtds = new Hashtable<String, String>();
		}
		public WPSResolver( Hashtable<String, String> dtds)
		{
			m_dtds = dtds;
		}
		public InputSource resolveEntity (String publicId, String systemId) throws java.io.FileNotFoundException
		{
			InputSource iSource = null;
			String file = m_dtds.get( extractPath( systemId));
			if( file != null)
			{
				iSource = new InputSource( new StringReader( file));
				iSource.setSystemId( ".");
			}
			else
			{
				FileReader fr = new java.io.FileReader(getServletContext().getRealPath( "dtd/"+extractPath( systemId) ));
				iSource = new InputSource( fr);// new java.io.FileReader( "/" + extractPath( systemId)));
				iSource.setSystemId( ".");
			}
			return iSource;
		}
	}
	static public String extractPath( String filePath)
	{
		int pos = filePath.lastIndexOf( '\\');
		String file = (pos > -1) ? filePath.substring( pos+1) : filePath;
		pos = file.lastIndexOf( '/');
		return (pos > -1) ? file.substring( pos+1) : file;
	}
	static public String extractFile( JarInputStream is) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		byte []b = new byte[512];
		while( is.available() > 0)
		{
			int n = is.read( b);
			if( n > 0)
				sb.append( new String( b, 0, n));
		}
		return sb.toString();
	}
	private void uploadZipFile( InternalReport output, byte[] definitionFile)  throws Exception
	{
		Hashtable<String, String> dtds = new Hashtable<String, String>();
		Hashtable<String, String> files = new Hashtable<String, String>();
		String name = null;
		output.addAction( 0, "Reading definition file");
		JarInputStream input = new JarInputStream( new ByteArrayInputStream( definitionFile));
		// La parserXML ferme le stream : donc lecture et d�coupage du fichier maintenant
		ZipEntry entry = input.getNextEntry();
		if( entry == null)
		{   // Not a jar file
			output.setLastActionResult( "bad file format or corrupted file!");
			return;
		}
		while( input.available() > 0)
		{
			name = entry.getName();
			if( name.endsWith( ".dtd"))
				dtds.put( extractPath( name), extractFile( input));
			else
				files.put( extractPath( name), extractFile( input));
			input.closeEntry();
			entry = input.getNextEntry();
		}
		org.jdom.input.SAXBuilder saxBuilder = new org.jdom.input.SAXBuilder( true);
		saxBuilder.setEntityResolver( new WPSResolver( dtds));
		output.setLastActionResult( "done.");
		output.skipLine();

		for( String name1 : files.keySet())
		{
			if( name1.endsWith( ".xml"))
			{
				uploadFile( saxBuilder, output, name1, files.get( name1));
			}
		}
	}
	private void uploadFile( org.jdom.input.SAXBuilder saxBuilder, InternalReport output, String name, String definition)  throws Exception
	{
		output.addAction( 0, "Reading definition file '" + name + "'");
		org.jdom.Document doc = saxBuilder.build( new StringReader( definition), ".");
		org.jdom.Element root = doc.getRootElement();

		CharArrayWriter bout = new CharArrayWriter();
		org.jdom.output.XMLOutputter op = new org.jdom.output.XMLOutputter();

		if( root.getName().equalsIgnoreCase( "dictionaries"))
		{
			List lst = root.getChildren( "dictionary");
			for( int i = 0; i < lst.size(); ++i)
			{
				org.jdom.Element subelem = ( org.jdom.Element) lst.get( i);
				op.output( subelem, bout);
				uploadDictionary( subelem.getAttributeValue( "name"), bout.toString(), output);
				bout.reset();
			}
		}
		else if( root.getName().equalsIgnoreCase( "dictionary"))
		{
			op.output( root, bout);
			uploadDictionary( root.getAttributeValue( "name"), bout.toString(), output);
		}
		else if( root.getName().equalsIgnoreCase( "swatch"))
		{
			op.output( root, bout);
			uploadSwatch( root.getAttributeValue( "name"), bout.toString(), output);
		}
	}
	private void uploadDictionary( String name, String definition, InternalReport output)
	{
		output.addAction( 1,  "Dictionary '" + name + "'");
		try {
			DictionaryManager manager = new DictionaryManagerImpl();
			Dictionary dictionary = manager.findByName(name);
			if (dictionary == null) {
				dictionary = manager.create( name, definition);
				output.setLastActionResult( "created.");
			}
			else {
				dictionary.setDefinition( definition);
				manager.update( dictionary);
				output.setLastActionResult( "updated.");
			}
		}
		catch( Exception e)
		{
			output.setLastActionResult( e.getMessage());
			e.printStackTrace();
		}
	}
	private void uploadSwatch( String name, String definition, InternalReport output)
	{
		output.addAction( 1,  "Swatch '" + name + "'");
		try {
			SwatchManager swatchManager = new SwatchManagerImpl();			
			Swatch swatch = swatchManager.findByName( name);
			if (swatch == null) {
				swatch = swatchManager.create( name, definition);
				output.setLastActionResult( "created.");
			}
			else  {
				swatch.setDefinition( definition);
				swatchManager.update( swatch);
				output.setLastActionResult( "updated.");
			}
		}
		catch( Exception e)
		{
			output.setLastActionResult( e.getMessage());
			e.printStackTrace();
		}
	}
}
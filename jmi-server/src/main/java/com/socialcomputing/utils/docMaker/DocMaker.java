package com.socialcomputing.utils.docMaker;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.jdom.*;
import org.jdom.output.*;
import org.jdom.transform.*;

/**
 * <p>Title: DocMaker</p>
 * <p>Description: .</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class DocMaker
{

	public static void transformXMLDoc( String xmlName, String xslName, String htmlName )
	{
		try
		{
			TransformerFactory	tFactory	= TransformerFactory.newInstance();
			Transformer			transformer	= tFactory.newTransformer( new StreamSource( xslName ));
			JDOMResult 			result		= new JDOMResult();
			XMLOutputter		XMLOut		= new XMLOutputter();

			transformer.transform( new StreamSource( xmlName ), result );

			Document			doc			= result.getDocument();
			Element				docElm		= doc.getRootElement();
			OutputStream		htmlOS		= new FileOutputStream( htmlName );

			XMLOut.output( docElm, htmlOS );
			System.out.println( "transform " + xmlName + " to " +  htmlName + " using " + xslName );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	public static void main( String[] args )
	{
		String		xmlName, htmlName,
					devRoot	= "c:/dvpt/src/",
					xslName	= devRoot + "doc2html.xsl";
		String[]	xmlDocs	=
		{
			"com/voyezvous/wps/client/WPSApplet",
			"com/voyezvous/wps/server/generator/generator",
			"com/voyezvous/wps/server/swatchs/swatch",
		};
		int			i, n	= xmlDocs.length;

		for ( i = 0; i < n; i ++ )
		{
			xmlName		= devRoot + xmlDocs[i] + ".xml";
			htmlName	= devRoot + xmlDocs[i] + ".html";
			transformXMLDoc( xmlName, xslName, htmlName );
		}
	}
}


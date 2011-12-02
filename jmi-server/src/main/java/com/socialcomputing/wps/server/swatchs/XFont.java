package com.socialcomputing.wps.server.swatchs;

import java.util.*;
import java.awt.*;
//import java.io.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.FontX;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class XFont extends XMLBase
{
//	private String      m_name;
//	private int         m_style;
//	private static final    String[]    NAME_TYPES   = { "slice" };

/**
	 * 
	 */
	private static final long serialVersionUID = 5941597547174250719L;
	/*
	<font		    id="rankFnt"
					name="SansSerif"
					size="@rankToSizeIpl"
					style="plain"/>

<!ELEMENT font EMPTY>
	<!ATTLIST font      name    CDATA   "SansSerif"
						size    CDATA   "9"
						style   ( plain | bold | italic | bold-italic ) "plain"
						id      ID      #IMPLIED>
*/
/*
<!-- font style const -->
<!ENTITY plain          "0">
<!ENTITY bold           "1">
<!ENTITY italic         "2">
<!ENTITY bold-italic    "3">
*/
	private	Integer		m_flags;

	public static XFont readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XFont  	font    	= new XFont();
		String	styleStr	= elem.getAttributeValue( "style" );
		int		flags		= 0;

		if ( styleStr.equals( "plain" ))			flags = Font.PLAIN;
		else if ( styleStr.equals( "bold" ))		flags = Font.BOLD;
		else if ( styleStr.equals( "italic" ))		flags = Font.ITALIC;
		else if ( styleStr.equals( "bold-italic" ))	flags = Font.BOLD | Font.ITALIC;

		font.m_flags	= new Integer( flags );
		font.putAttRef( "name", String.class, elem, root, refs, CDATA | IDREF );
		font.putAttRef( "size", Integer.class, elem, root, refs, CDATA | IDREF );

//		ArrayList   list = new ArrayList();
//		font.addPropsToList( list );

		return font;
	}

	public Object toClient( Hashtable refs )
	{
		FontX   font = new FontX();

		font.m_containers = new VContainer[]
		{
			new VContainer( m_flags, false ),
			toClientCont( "name", refs ),
			toClientCont( "size", refs )
		};

		return font;
	}
}
package com.socialcomputing.wps.server.swatchs;

import java.util.*;
//import java.awt.*;
import java.io.*;

import org.jdom.*;
import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.Satellite;
import com.socialcomputing.wps.client.applet.Swatch;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * <p>Title: XSwatch</p>
 * <p>Description: An XSwatch describes how WPSApplet zones should be displayed and how to interact ith them.<br>
 * XSwatch are the server-side equivalent of WPSApplet's Swatch.
 * But they also manage how properties should be retrieved from the DB/Analysis/Generator.
 * And they can be read from an XML definition.<br>
 * The expression 'prop' that means 'property' is often used in the name of the methods.
 * In fact props are Containers that reference properties.
 * For each attribute each container is evaluated by retrieving its associated property in the DB.
 * Sorry if this is confusing, name should be refactored.<br>
 * An XSwatch holds references because of the XML definition ('@refId' and <ref id=refId/>).
 * Those references also exists on the Client-side in the WPSApplet Swatchs.
 * Thay are simply converted and stored in a hashtable.
 * So when the Applet Satellite must execute a popup "myMenu" it just get it from the Swatch reference Hashtable.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class XSwatch extends XMLBaseList implements Serializable
{
	static final long serialVersionUID = -281426764561853726L;

	//==========================================================================
	//====================== SWATCH TYPES & STYLES =============================
	//==========================================================================

	/**
	 * Swatch type for Group zones (Building between the Streets).
	 * deprecated Like Clusters, This has never been implemented.
	 * @see #CLUS_TYP
	 */
	public  static final int GROUP_TYP      = 0;

	/**
	 * Swatch type of Node zones (Places).
	 */
	public  static final int NODE_TYP       = 1;

	/**
	 * Swatch type of Link zones (Streets).
	 */
	public  static final int LINK_TYP       = 2;

	/**
	 * Swatch type for Cluster zones (Empty space between Streets).
	 * deprecated As dual is more efficiently displayed, clusters have never been implemented totaly...
	 */
	public  static final int CLUS_TYP       = 3;

	/**
	 * Swatch type for the "You Are Here" zone.
	 * deprecated Like Clusters, This has never been implemented.
	 * @see #CLUS_TYP
	 */
	public  static final int YAH_TYP        = 4;

	/**
	 * Swatch type for Advertisement zone.
	 * deprecated Like Clusters, This has never been implemented.
	 * @see #CLUS_TYP
	 */
	public  static final int ADD_TYP        = 5;

	/**
	 * Swatch type for a Node Selection zone.
	 * deprecated Selection zone is managed inside Node Swatch Satellites now.
	 * @see XSat
	 */
	public  static final int NODE_SEL_TYP   = 6;

	/**
	 * Swatch type for a Link Selection zone.
	 * deprecated Selection zone is managed inside Link Swatch Satellites now.
	 * @see XSat
	 */
	public  static final int GROUP_SEL_TYP  = 7;

	//==========================================================================
	//====================== MEMBERS ===========================================
	//==========================================================================

	/**
	 * Swatch Name ID
	 */
/*	static final int    DEF_ID      = 0;
	static final int    FR_ID       = 1;

	static final int    NODE_ID     = 0;
	static final int    LINK_ID     = 1;

	static final int    REST_ID     = 0;
	static final int    CUR_ID      = 1;

	static final int    NORM_ID     = 0;
	static final int    REF_ID      = 1;

	static final int    MY_ID       = 0;
	static final int    DISCO_ID    = 1;
	static final int    DAILY_ID    = 2;
	static final int    SEARCH_ID   = 3;*/

	/**
	 * Name used as a unique identifier for each Swatch.
	 * This is defined in the XML file. Ex : MPST_DEF_NODE_REST_NORM_DISCO.
	 */
	private String                  m_name      = null;

	/**
	 * A table holding this Swatch references.
	 * It's possible to define references in XML document using the '@refId' attribute or <ref id=refID/> element.
	 * These references are then stored in this table.
	 */
	private Hashtable               m_refs;

	/**
	 * A buffer hoding all the ValueContainer of this Swatch including those of the children elements.
	 * This buffer is generated at the first call to getProps.
	 */
	private ValueContainer[]        m_valBuf    = null;

	/**
	 * A buffer hoding all the adaptative InterValues of this Swatch including those of the children elements.
	 * This buffer is generated at the first call to getAdaptativeInterValues.
	 */
	private InterValue[]            m_interBuf  = null;

	/**
	 * Flags passed to the WPSApplet Swatch.
	 * @see Swatch#LINK_BIT
	 */
	private int                     m_flags;

	/**
	 *
	 */
//	public XSwatch( ){}

/*	public static String getSwatchName( int lang, int shape, int activity, int style, int plan )
	{
		String  name = "MPST";

		name += lang == FR_ID       ? "_FR"     : "_DEF";
		name += shape == NODE_ID    ? "_NODE"   : "_LINK";
		name += activity == REST_ID ? "_REST"   : "_CUR";
		name += style == NORM_ID    ? "_NORM"   : "_REF";

		switch ( plan )
		{
			case MY_ID:     name += "_MY";  break;
			case DISCO_ID:  name += "_DISCO";  break;
			case DAILY_ID:  name += "_DAILY";  break;
			case SEARCH_ID: name += "_SEARCH";  break;
		}

		return name;
	}*/

   /**
	* Gets this name as defined in the name attribute of the swatch XML document.
	* @return	This identifier.
	*/
	public String getName()
	{
		return m_name;
	}

	/**
	 * Gets the table of adaptative properties in this Swatch.
	 * Those special properties (adaptive InterValues) have bounds that needs to be automaticaly calculated.
	 * So to process them in a different way, they need to be retrieved separately.
	 * @return	An array of adaptive InterValues.
	 */
	public InterValue[] getAdaptiveInterValues()
	{
		if ( m_interBuf == null )
		{
			ValueContainer[]    props   = getProps();
			ValueContainer      prop;
			InterValue          inter;
			int                 i, n    = props.length;
			ArrayList           list    = new ArrayList();

			for ( i = 0; i < n; i ++ )
			{
				prop = props[i];

				if ( prop instanceof InterValue )
				{
					inter = (InterValue)prop;

					if ( inter.isAdaptive())   list.add( inter );
				}
			}

			m_interBuf = (InterValue[])list.toArray( new InterValue[list.size()] );
		}

		return m_interBuf;
	}

	/**
	 * Gets this Swatch containers including its children ones.
	 * This is necessary to retrieve only the used properties from the DB.
	 * @return	An array of ValueContainer that reference DB properties.
	 */
	public ValueContainer[] getProps()
	{
		if ( m_valBuf == null )
		{
			ArrayList list = new ArrayList();

			addPropsToList( list );

			Enumeration     elems   = m_refs.elements();
			Object          obj;
			Propable        container;

			while ( elems.hasMoreElements())
			{
				obj = elems.nextElement();

				if ( obj instanceof Propable )
				{
					container = (Propable)obj;
					container.addPropsToList( list );
				}
			}

			m_valBuf = (ValueContainer[])list.toArray( new ValueContainer[list.size()] );
		}

		return m_valBuf;
	}

	/**
	 * Converts this to a WPSApplet swatch, So it can be passed to the Applet using serialization.
	 * @return	A WPSApplet Swatch matching this.
	 */
	public Object toClient( )
	{
		return toClient( m_refs );
	}

	/**
	 * Converts this to a WPSApplet swatch using the reference table.
	 * All satellites of this are first converted to WPSApplet Satellite class
	 * and stored to a newly created WPSApplet Swatch.
	 * This Swatch VContainer table is then filled with this flags and title.
	 * Finally references are converted to WPSApplet objects and stored in the new Swatch ref table.
	 * @param refs	Inner reference table.
	 * @return		A WPSApplet Swatch matching this.
	 */
	public Object toClient( Hashtable refs )
	{
		Swatch          swatch;
		int             i, n        = m_list.size();
//		boolean         hasLinks    = false;
		XSat            sat;
		Satellite[]     sats        = new Satellite[n];
		VContainer      title       = toClientCont( "title", refs ),
						flags;

		for ( i = 0; i < n; i ++ )
		{
			sat = (XSat)m_list.get( i );
			sats[i] = (Satellite)sat.toClient( refs );

			if(( sat.m_flags.intValue() & Satellite.LINK_BIT )!= 0 )
			{
				m_flags |= Swatch.LINK_BIT;
			}
		}

		flags               = new VContainer( new Integer( m_flags ), false );
		swatch              = new Swatch( sats );
		swatch.m_containers = new VContainer[]{ flags, title };
		swatch.m_refs       = new Hashtable();
		addClientRefs( swatch.m_refs );

		return swatch;
	}

	/**
	 * Adds references from this table to a table of WPSApplet references.
	 * The references are converted using the Clientable interface.
	 * @param refs	A new table to fill with the converted references.
	 * @see Clientable#toClient
	 */
	private void addClientRefs( Hashtable refs )
	{
		ArrayList       cRefs   = (ArrayList)m_refs.get( "CLIENT_REFS" );
		ListIterator    it      = cRefs.listIterator();
		Object          key, value;

		while ( it.hasNext())
		{
			key     = it.next();
			value   = m_refs.get( key );
			value   = ((Clientable)value ).toClient( m_refs );

			if ( value instanceof VContainer )
			{
				value = ((VContainer)value ).m_value;//.getValue( null );
			}

			refs.put( key, value );
		}
	}

	/**
	 * Reads a swatch from an XML <swatch> element.
	 * First <sat> elements are parsed and stored in this list.
	 * Then the name of the Swatch and its title and subtitle are parsed.
	 * The last two should be deprecated as they are no more used by the WPSApplet.
	 * @param root	Swatch Element to parse.
	 * @return		a new XSWatch matching the XML definition.
	 * @throws JDOMException
	 * @see			Swatch#TITLE_VAL
	 */
	public static XSwatch readObject( Element root )
	throws JDOMException
	{
		XSwatch         swatch  = new XSwatch();
		Hashtable       refs    = new Hashtable();
		java.util.List  satLst  = root.getChildren( "sat" );
		ListIterator    it      = satLst.listIterator();

		refs.put( "CLIENT_REFS", new ArrayList());

		while ( it.hasNext())
		{
			swatch.addElemObj((Element)it.next(), root, refs );
		}

		swatch.m_name = root.getAttributeValue( "name" );
		swatch.putAttRef( "title", TextValue.class, root, root, refs, CDATA | IDREF );
		swatch.putAttRef( "subTitle", TextValue.class, root, root, refs, CDATA | IDREF );
		swatch.m_refs = refs;

		return swatch;
	}

	/**
	 * Just a test method for debugging.
	 * @param args
	 */
	public static void main( String[] args )
	{
		try
		{
//			System.out.println( System.getProperty( "user.dir" ));
			SAXBuilder      builder = new SAXBuilder( true );
			Document        doc     = builder.build( new File( "XMLSwatchs\\Search\\def_node_rest_ref_search.xml" ));
//			Document        doc     = builder.build( new File( "swatch.xml" ));
			Element         root    = doc.getRootElement();
			XSwatch         swatch;

			swatch  = XSwatch.readObject( root );
			System.out.println( swatch.m_name + " read" );

//			ValueContainer[]    conts   = swatch.getProps();
//			ColorX  color = (ColorX)( conts[4].getValue( "~INTER#1", new Integer( 1500 )));
			System.out.println( "props from " + swatch.m_name + " retreived" );

			InterValue[]        inters  = swatch.getAdaptiveInterValues();

			for ( int i = 0; i < inters.length; i ++ )
			{
				System.out.println( inters[i] );
			}
			inters[0].updateRange( new Integer( 5 ));
			inters[0].updateRange( new Integer( 564 ));
			inters[0].updateRange( new Integer( -265 ));
			inters[0].updateRange( new Integer( -24 ));
			inters[0].updateRange( new Integer( 56 ));
			inters[0].updateRange( new Integer( 568 ));
			inters[0].updateRange( new Integer( -5568 ));

			swatch.toClient();
			System.out.println( swatch.m_name + " converted to Client" );
		}
		catch ( JDOMException e )
		{
			System.err.println( e.getMessage());
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}

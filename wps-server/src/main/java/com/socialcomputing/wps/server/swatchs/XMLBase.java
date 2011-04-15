package com.socialcomputing.wps.server.swatchs;

import java.awt.Point;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.socialcomputing.wps.client.applet.Base;
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.HTMLText;
import com.socialcomputing.wps.client.applet.Transfo;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * <p>Title: XMLBase</p>
 * <p>Description: A basic table of ValueContainers that should be subclassed to become a Swatch part.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public abstract class XMLBase extends HashMap implements Serializable, Propable, Clientable
{
	/**
	 * Flag indicating that an XML attribute value must be of type CDATA.
	 */
	protected static final  int         CDATA       = 1;

	/**
	 * Flag indicating that an XML attribute value must be of type IDREF.
	 */
	protected static final  int         IDREF       = 2;

	/**
	 * Gets the ValueContainer corresponding to the specified key.
	 * This is a convenient mathod to avoid casting.
	 * @param key	Key associated to the value.
	 * @return		The ValueContainer associated with key.
	 */
	protected ValueContainer getValue( String key )
	{
		return (ValueContainer)get( key );
	}

	/**
	 * Gets the ValueContainer raw value corresponding to the specified key.
	 * Some ValueContainers references Object in the Swatch ref table.
	 * So it's necessary to give this table to retrieve the associated raw value.
	 * @param key	Key associated to the value.
	 * @param refs	Table of reference coming from the XSwatch using this.
	 * @return		The raw value associated with key.
	 */
	protected Object getRawValue( String key, Hashtable refs )
	{
		Object  value = get( key );

		return value instanceof ValueContainer ?
				((ValueContainer)value ).getRawValue( refs ):
				value;
	}

	/**
	 * Adds all the stored Propable Values (Xxxxx  classes) of this in a list.
	 * @param list	A list to complete.
	 */
	public void addPropsToList( ArrayList list )
	{
		Iterator	it		= values().iterator();
		Object      obj;
		Propable    container;

		while ( it.hasNext())
		{
			obj = it.next();

			if ( obj instanceof Propable )
			{
				container = (Propable)obj;
				container.addPropsToList( list );
			}
		}
	}

	/**
	 * Parse an XML element to produce an Object that is put into this table.
	 * @param name	Name of the element to read.
	 * @param elem	Parent element of the element to read.
	 * @param root	Swatch root element containing all the other elements.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>)
	 * @return		True if the element was found.
	 */
	protected boolean putElemObj( String name, Element elem, Element root, Hashtable refs )
	{
		Element objElm  = elem.getChild( name );

		if ( objElm != null )
		{
			put( name, getElement( objElm, root, refs ));

			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 *
	 * @param ref
	 * @param cls
	 * @param elem
	 * @param root
	 * @param refs
	 * @param type
	 * @return
	 * @throws JDOMException
	 */
	protected static Object getAttRefValue( String ref, Class cls, Element elem, Element root, Hashtable refs, int type )
	throws JDOMException
	{
		String  valueStr    = getAttributeRef( ref, cls, elem, root, refs, type );
		Object  value       = null;

		if (( valueStr != null ) && ( valueStr.length()!=0) )
		{
			if ( valueStr.charAt( 0 )== '@' )  // this is a ref
			{
				value = new RefValue( valueStr.substring( 1 ));
			}
			else
			{
				if ( cls == Boolean.class )
				{
					// 1 bit boolean true if valueStr = 'true'
					value = Boolean.valueOf( valueStr );
				}
				else if ( cls == Integer.class )
				{
					// 32bit int
					value = Integer.valueOf( valueStr );
				}
				else if ( cls == Float.class )
				{
					// 32bit float
					value = Float.valueOf( valueStr );
				}
				else if ( cls == ColorX.class )
				{
					// hexa or decimal 24 bit color code
					value = parseColor( valueStr );
				}
				else if ( cls == TextValue.class )
				{
					// parametrized string
					value = new TextValue( replaceNL( valueStr ));
				}
				else
				{
					// String.class, just replace the substring "\n" with the character '\n'
					value = replaceNL( valueStr );
				}
			}
		}

		return value;
	}

	/**
	 *
	 * @param ref
	 * @param cls
	 * @param elem
	 * @param root
	 * @param refs
	 * @param type
	 * @return
	 * @throws JDOMException
	 */
	protected boolean putAttRef( String ref, Class cls, Element elem, Element root, Hashtable refs, int type )
	throws JDOMException
	{
		Object	value	= getAttRefValue( ref, cls, elem, root, refs, type );

		if ( value != null )
		{
			put( ref, value );

			return true;
		}

		return false;
	}

/*	protected boolean putAttRef( String ref, Class cls, Element elem, Element root, Hashtable refs, int type )
	throws JDOMException
	{
		String  valueStr = getAttributeRef( ref, cls, elem, root, refs, type );

		if ( valueStr != null )
		{
			Object  value = valueStr;

			if ( valueStr.charAt( 0 )== '@' )  // this is a ref
			{
				value = new RefValue( valueStr.substring( 1 ));
			}
			else
			{
				if ( cls == Boolean.class )
				{
					// 1 bit boolean true if valueStr = 'true'
					value = Boolean.valueOf( valueStr );
				}
				else if ( cls == Integer.class )
				{
					// 32bit int
					value = Integer.valueOf( valueStr );
				}
				else if ( cls == Float.class )
				{
					// 32bit float
					value = Float.valueOf( valueStr );
				}
				else if ( cls == ColorX.class )
				{
					// hexa or decimal 24 bit color code
					value = parseColor( valueStr );
				}
				else if ( cls == TextValue.class )
				{
					// parametrized string
					value = new TextValue( replaceNL( valueStr ));
				}
				else
				{
					// STRING_CLS, don't do anything
					value = replaceNL( valueStr );
				}
			}

			put( ref, value );

			return true;
		}
		else
		{
			return false;
		}
	}*/

	/**
	 * Gets a String Element and transform it to a TextValue.
	 * The soft newlines "\n" are replaced with hard newlines '\n'.
	 * @param elem	The <string> Element to parse.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A TextValue matching elem.
	 */
	private static Object getString( Element elem, Element root, Hashtable refs )
	{
		return new TextValue( replaceNL( elem.getAttributeValue( "label" )));
	}

	/**
	 * Gets a string representation of a <pop tooltip='tipId'/> element.
	 * As events to be executed by the WPSApplet are stored in a string, they must first be formated.
	 * A reference to the Tip whose id is 'tipId' is created.
	 * @param elem	The <pop> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		"pop tipId".
	 * @throws JDOMException
	 */
	private static Object getPop( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		String      tipNam  = elem.getAttributeValue( "tooltip" );
		//Object      tip     = 
		createElementRef( tipNam, root, refs );
		ArrayList   cRefs   = (ArrayList)refs.get( "CLIENT_REFS" );

		cRefs.add( tipNam );

		return "pop " + tipNam;
	}

	/**
	 * Gets a string representation of a <open url='pageURL' track='trackURL'/> element.
	 * As events to be executed by the WPSApplet are stored in a string, they must first be formated.
	 * As tracking is managed the same way as a normal URL, it should be deprecated.
	 * @param elem	The <open> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		"open pageURL[SEP+trackURL]".
	 * @throws JDOMException
	 */
	private static Object getOpen( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		String  track   = getAttString( "track", elem, root, refs ),
				openStr = "open " + getAttString( "url", elem, root, refs );

		return track == null ? openStr : openStr + HTMLText.SUBSEP + track;
	}

	/**
	 * Gets a string representation of a <play sound='soundURL'/> element.
	 * As events to be executed by the WPSApplet are stored in a string, they must first be formated.
	 * @param elem	The <play> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		"play soundURL".
	 * @throws JDOMException
	 */
	private static Object getPlay( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		return "play " + getAttString( "sound", elem, root, refs );
	}

	/**
	 * Gets a string representation of a <popup menu='menuId'/> element.
	 * As events to be executed by the WPSApplet are stored in a string, they must first be formated.
	 * A reference to the Menu whose id is 'menuId' is created.
	 * @param elem	The <popup> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		"popup menuId".
	 * @throws JDOMException
	 */
	private static Object getPopup( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		String      menuNam = elem.getAttributeValue( "menu" );
		//Object      popup   = 
		createElementRef( menuNam, root, refs );
		ArrayList   cRefs   = (ArrayList)refs.get( "CLIENT_REFS" );

		cRefs.add( menuNam );

		return "popup " + menuNam;
	}

	/**
	 * Gets a string representation of a <show status='text'/> element.
	 * As events to be executed by the WPSApplet are stored in a string, they must first be formated.
	 * @param elem	The <show> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		"show text".
	 * @throws JDOMException
	 */
	private static Object getShow( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		return "show " + getAttString( "status", elem, root, refs );
	}

	/**
	 * Gets a string representation of a <dump text='message'/> element.
	 * As events to be executed by the WPSApplet are stored in a string, they must first be formated.
	 * @param elem	The <dump> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		"dump message".
	 * @throws JDOMException
	 */
	private static Object getDump( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		return "dump " + getAttString( "text", elem, root, refs );
	}

	/**
	 * Gets a string Attribute from an Element.
	 * Such an Attribute can be a reference to a <string> element if it starts with '@'.
	 * In this case, the corresponding Element is created if it doesn't already exists.
	 * @param ref	The text or reference ('@refId').
	 * @param elem	An element containing the string Attribute.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		The attribute (or it's reference) text.
	 * @throws JDOMException
	 */
	private static String getAttString( String ref, Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		String  attVal  = elem.getAttributeValue( ref );

		if ( attVal == null )   return null;

		boolean isRef   = attVal.charAt( 0 )== '@';
		Object  string  = getAttRefValue( ref, isRef ? TextValue.class : String.class, elem, root, refs, CDATA | IDREF );

		return isRef ? (String)(((RefValue)string ).toClient( refs )): (String)string;
	}

	/**
	 * Parses a 24 bits RGB Color string representation.
	 * @param rgb	An hexa 'rrggbb' definition or a decimal 'red green blue' definition. All components are 8 bits coded [0,255].
	 * @return		a new ColorX matching rgb.
	 * @throws JDOMException
	 */
	private static ColorX parseColor( String rgb )
	throws JDOMException
	{
		if( rgb.indexOf( '{') != -1 || rgb.indexOf( '[') != -1)
			return new ColorX( rgb);
		else
		{
			int pos0 = rgb.indexOf( ' ' );
			if ( pos0 == -1 )  // hexa "rrggbb"
			{
				int     col = Integer.parseInt( rgb, 16 );

				return new ColorX( col );
			}
			else                            // decimal "red green blue"
			{
				int pos1    = rgb.lastIndexOf( ' ' );

				if ( pos1 == -1 )
				{
					throw ( new JDOMException( "Bad Color format '" + rgb + "'" ));
				}
				else
				{
					int red     = Integer.parseInt( rgb.substring( 0, pos0 )),
						green   = Integer.parseInt( rgb.substring( pos0 + 1, pos1 )),
						blue    = Integer.parseInt( rgb.substring( pos1 + 1 ));

					return new ColorX( red, green, blue );
				}
			}
		}
	}

	/**
	 * Gets a ColorX from a <color rgb='color'/> element.
	 * The color can be in hexa or decimal format.
	 * @param elem	The <color> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A new ColorX matching the rgb Attribute.
	 * @throws JDOMException
	 * @see			parseColor
	 */
	private static Object getColor( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		String  rgb = elem.getAttributeValue( "rgb" );

		return parseColor( rgb );
	}

	/**
	 * Gets a string representation of a <call method='function'><arg value='value'/>...</call> element.
	 * As events to be executed by the WPSApplet are stored in a string, they must first be formated.
	 * @param elem	The <call> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		"function(value,...)".
	 * @deprecated	The direct javascript:func(args) URL should be used instead.
	 */
	private static Object getCall( Element elem, Element root, Hashtable refs )
	{
		java.util.List  argLst  = elem.getChildren( "arg" );
		ListIterator    li      = argLst.listIterator();
		String          call    = elem.getAttributeValue( "method" ) + '(';

		while ( li.hasNext())
		{
			call += ((Element)li.next()).getAttributeValue( "value" );
			if ( li.hasNext())  call += ',';
		}

		return call + ')';
	}

	/**
	 * Gets a Transfo from a <transfo map='mapTyp' scale='scl'><cart x='X' y='Y'/>|<polar dir='D' rad='R'/></transfo> element.
	 * A transfo can be cartesian or polar (angle is in degree).
	 * @param elem	The <transfo> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A new Transfo Object matching elem.
	 */
	private static Object getTransfo( Element elem, Element root, Hashtable refs )
	{
		final float DEG2RAD = (float)Math.PI / 180.f;
		Element subElm      = elem.getChild( "cart" );
		float   x, y, scl   = Float.parseFloat( elem.getAttributeValue( "scale" ));
		int     flags       = 0;
		String  flagStr     = elem.getAttributeValue( "map" );

		if ( flagStr.equals( "interpol" ))      flags = Transfo.INTER_BIT;
		else if ( flagStr.equals( "absolute" )) flags = Transfo.ABS_BIT;

		if ( subElm != null )   // cart
		{
			x       = Float.parseFloat( subElm.getAttributeValue( "x" ));
			y       = Float.parseFloat( subElm.getAttributeValue( "y" ));
			flags   |= Transfo.CART_BIT;
		}
		else if (( subElm = elem.getChild( "polar" ))!= null )
		{
			x       = DEG2RAD * Float.parseFloat( subElm.getAttributeValue( "dir" ));
			y       = Float.parseFloat( subElm.getAttributeValue( "rad" ));
		}
		else                    // default to cart
		{
			x = y = 0.f;
			flags   |= Transfo.CART_BIT;
		}

		return new Transfo( x, y, scl, flags );
	}

	/**
	 * Gets a Point from a <point x='X' y='Y'/> element.
	 * @param elem	The <point> element.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A new Point Object matching elem.
	 */
	private static Object getPoint( Element elem, Element root, Hashtable refs )
	{
		int x   = Integer.parseInt( elem.getAttributeValue( "x" )),
			y   = Integer.parseInt( elem.getAttributeValue( "y" ));

		return new Point( x, y );
	}

	/**
	 * Gets an element knowing its ID by recursively looking into the children of a given root element.
	 * @param id	Id to find.
	 * @param root	Root element to start from.
	 * @return		The Element corresponding to the id or null if there is none.
	 */
	private static Element getElementById( String id, Element root )
	{
		String  idStr = root.getAttributeValue( "id" );

		if ( idStr != null && idStr.equals( id ))   // we found the right Element
		{
			return root;
		}
		else    // search in subelements
		{
			Element         elem;
			java.util.List  elemLst = root.getChildren();
			int             i, n    = elemLst.size();

			for ( i = 0; i < n; i ++ )
			{
				elem = getElementById( id, (Element)elemLst.get( i ));

				if ( elem != null ) return elem;
			}

			return null;
		}
	}

	/**
	 * Gets the Object corresponding to an XML element using its name and the reflection API.
	 * First the name of the element is used to find an X... class. For example class XShape for element <shape>.
	 * Then the readObject method of the X... class is invoked to creates a new X... class instance.
	 * @param elem	The element to read.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A new XClass Object matching elem if no exception occures.
	 * @throws Throwable	If no XClass matching the element can be found.
	 */
	private static Object getObject( Element elem, Element root, Hashtable refs )
	throws Throwable
	{
		String      elemNam     = elem.getName(),
					classNam    = Character.toUpperCase( elemNam.charAt( 0 )) + elemNam.substring( 1 );
		Class[]     params      = new Class[]{ Element.class, Element.class, Hashtable.class };
		Object[]    args        = new Object[]{ elem, root, refs };

		try
		{
			Class   cls         = Class.forName( "com.socialcomputing.wps.server.swatchs.X" + classNam );
			Method  method      = cls.getDeclaredMethod( "readObject", params );

			return method.invoke( null, args );
		}// This data can't be read this way
		catch ( ClassNotFoundException e )      { return null; }
		catch ( NoSuchMethodException e )       { return null; }
		catch ( IllegalAccessException e )      { return null; }
		catch ( InvocationTargetException e )
		{
			throw( e.getTargetException());
		}
	}

	/**
	 * Gets the Object corresponding to an XML simple element using its name and the reflection API.
	 * The name of the element is used to find the corresponding get... method in this class.
	 * Then it is invoked to creates a new Object instance corresponding to the XML element definition.
	 * @param elem	The element to read.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A new Object matching elem or null if an exception occures.
	 */
	private static Object getSimpleObject( Element elem, Element root, Hashtable refs )
	{
		String      elemNam     = elem.getName(),
					classNam    = Character.toUpperCase( elemNam.charAt( 0 )) + elemNam.substring( 1 );
		Class       cls         = XMLBase.class;
		Class[]     params      = new Class[]{ Element.class, Element.class, Hashtable.class };
		Object[]    args        = new Object[]{ elem, root, refs };

		try
		{
			Method  method      = cls.getDeclaredMethod( "get" + classNam, params );

			return method.invoke( null, args );
		}
		catch ( Exception e )
		{
			System.out.println( "Unknown method : XMLBase.get" + classNam );
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the Object corresponding to an XML element using its name and the reflection API.
	 * First a try to find an XClass matching the element and invoke its readObject method.
	 * If this fails, try to find this class getXXX method matching the element and invoke it.
	 * @param elem	The element to read.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A new XClass Object or simple Object matching elem or null if an exception occures.
	 */
	protected static Object getElement( Element elem, Element root, Hashtable refs )
	{
		Object  ref = null;

		try
		{
			ref = getObject( elem, root, refs );
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
		}

		if ( ref == null )  // This data must be read using non class methods
		{
			ref = getSimpleObject( elem, root, refs );
		}

		return ref;
	}

	/**
	 * Creates and stores an Object knowing its XML element ID.
	 * If the Object can be created, it is added to the reference table.
	 * @param name	Name of the id attribute whose element must be read.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A new XClass Object or simple Object matching the element whose id is name.
	 * @throws JDOMException	If no reference with an id of name can be found.
	 */
	private static Object createElementRef( String name, Element root, Hashtable refs )
	throws JDOMException
	{
		Element     elem    = getElementById( name, root );

		if ( elem != null )
		{
			Object  ref = getElement( elem, root, refs );
			refs.put( name, ref );

			return ref;
		}
		else
		{
			throw ( new JDOMException( "Unknown reference '" + name + "'" ));
		}
	}

	/**
	 *
	 * @param obj
	 * @param classes
	 * @return
	 */
	private static boolean isInstance( Object obj, Class[] classes )
	{
		int i, n = classes.length;

		if ( obj instanceof InterValue )
		{
			Class   cls = ((InterValue)obj ).m_class;

			for ( i = 0; i < n; i ++ )
			{
				if ( classes[i] == cls )  break;
			}
			// TextValue is not interpolated, but String are!
			if ( i == n && cls == String.class )
			{
				cls = TextValue.class;

				for ( i = 0; i < n; i ++ )
				{
					if ( classes[i] == cls )  break;
				}
			}
		}
		else
		{
			for ( i = 0; i < n; i ++ )
			{
				if ( classes[i].isInstance( obj ))  break;
			}
		}

		return i < n;
	}

	private static boolean isInstance( Object obj, Class cls )
	{
		if ( obj instanceof InterValue )
		{
			Class   objCls = ((InterValue)obj ).m_class;

			if ( objCls == cls )
			{
				return true;
			}
			else
			{   // TextValue is not interpolated, but String are!
				return objCls == String.class && cls == TextValue.class;
			}
		}
		return obj instanceof InterValue ?
				((InterValue)obj ).m_class == cls :
				cls.isInstance( obj );
	}

	/**
	 * Gets the name of a <ref name=id/> element after storing it in the reference table.
	 * As references type can't be checked when they are called <@id>, it must be done there.
	 * The matching reference Class must match one of classes.
	 * @param classes	An array of Classes.
	 * @param elem		The <ref> element to read.
	 * @param root		The <swatch> Element holding elem.
	 * @param refs		Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return			The name of the elem id Attribute if no exceptions occures.
	 * @throws JDOMException	If the reference Class doesn't match any of the allowed types.
	 */
	protected static String getElementRef( Class[] classes, Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		String  name    = elem.getAttributeValue( "name" );
		Object  obj     = refs.get( name );

		if ( obj == null )                              // this ref still not exists
		{
			obj = createElementRef( name, root, refs );	// we must create and store it
		}

		if ( !isInstance( obj, classes ))
		{
			Element parent  = (Element) elem.getParent();
			String  idStr   = parent.getAttributeValue( "id" );

			name    = parent.getName();

			throw ( new JDOMException( "<" + name + " id=" + idStr + "> does not allow reference of type '" + obj.getClass().getName() + '\'' ));
		}

		return name;
	}

	/**
	 * Gets a string or reference Attribute whose type can be specified.
	 * As DTD CDATA are not typed, it's not possible to check if a CDATA is a reference before parsing the attributes.
	 * There are three possible types of Attribute:
	 * <ul>
	 * <li>string : The type should be CDATA.</li>
	 * <li>@refID : The type should be CDATA|IDREF.</li>
	 * <li>XMLref : The type should be IDREF.</li>
	 * </ul>
	 * For example, a <color> Attribute can be a string : color="255 0 127" or a reference : color="@greenCol" so its type is CDATA|IDREF.
	 * In a <sat> Element, the shape Attribute is an XML reference : shape="myShape" so its type is IDREF.
	 * So it's possible to specify whether this Attribute can be a string(CDATA), a reference(IDREF) or both(CDATA|IDREF).
	 * @param name	Name of the Attribute to retrieve.
	 * @param cls	Class of the Attribute.
	 * @param elem	The element to get the 'name' Attribute from.
	 * @param root	The <swatch> Element holding elem.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @param type	Allowed type for the Attribute [CDATA,IDREF,CDATA|IDREF]
	 * @return		The attribute string or if it's a reference, it's name preceeded by '@' (even if it's a real IDREF).
	 * @throws JDOMException	If this attribute is a reference and it's type doesn't match the specified one.
	 */
	protected static String getAttributeRef( String name, Class cls, Element elem, Element root, Hashtable refs, int type )
	throws JDOMException
	{
		String      attStr  = elem.getAttributeValue( name );

		if (( attStr != null ) && (attStr.length()!=0) )
		{
			boolean hasID   = ( type & IDREF )!= 0,
					hasCD   = ( type & CDATA )!= 0;

			if (( type == IDREF )||( hasID && attStr.charAt( 0 )=='@' ))    // this attribute is a reference
			{                                                               // Eventually throw away '@'
				String  elemName    = hasCD ? attStr.substring( 1 ) : attStr;
				Object  obj         = refs.get( elemName );

				if ( obj == null )                              			// this ref still not exists
				{
					obj = createElementRef( elemName, root, refs );			// we must create and store it
				}

				if ( !isInstance( obj, cls ))
				{
					String  idStr   = elem.getAttributeValue( "id" );

					throw ( new JDOMException( "<" + elem.getName() + " id=" + idStr + " " + name + "='" + attStr + "'> type mismatch" ));
				}

				attStr = "@" + elemName;
			}
			// else                                                         // this is just a parametrized String
		}

		return attStr;
	}

	/**
	 * Converts an Object in this table to a WPSApplet Object.
	 * Objects implementing the Clientable interface are converted using the toClient method.
	 * Some WPSApplet classes use Objects as members, not VContainers because it's more efficient.
	 * For example Satellite.m_shape member should be converted as a Client ShapeX using the Server XShape toClient method.
	 * @param name	Name of the Object to convert (usualy a ValueContainer).
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		An Object that can be stored in a WPSApplet swatch or null if no Object match 'name'.
	 * @see 		Clientable
	 */
	protected Object toClient( String name, Hashtable refs )
	{
		Object      obj = get( name );

		if ( obj instanceof Clientable )
		{
			return ((Clientable)obj ).toClient( refs );
		}
		else
		{
			return obj;
		}
	}

	/**
	 * Converts an Object in this table to a WPSApplet VContainer.
	 * Objects implementing the Clientable interface are converted using the toClient method.
	 * Many WPSApplet classes use VContainers as members so their value can be changed on the fly using zone properties.
	 * For example Satellite hover event is stored at the HOVER_VAL index in the m_containers array.
	 * So XSat "hover" member should be converted as a Client VContainer holding a String using the toClientCont method.
	 * @param name	Name of the Object to convert (usualy a ValueContainer).
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return		A VContainer that can be stored in a WPSApplet Base m_containers field or null if no Object match 'name'.
	 * @see 		Clientable
	 * @see			Base
	 */
	protected VContainer toClientCont( String name, Hashtable refs )
	{
		Object      obj = get( name );

		if ( obj instanceof Clientable )
		{
			return ((Clientable)obj ).toClientCont( refs );
		}
		else
		{
			return obj != null ? new VContainer( obj, false ) : null;
		}
	}

	/**
	 * An X... Class is a server equivalent of a Client ....X, use toClient to convert the first to the second.
	 * @param refs	Table containing all the referenced Object ('@refId' attribute or <ref name=refId/>).
	 * @return	The converted Object to put into a WPSApplet swatch.
	 */
	public abstract Object toClient( Hashtable refs );

	public VContainer toClientCont( Hashtable refs )
	{
		return new VContainer( toClient( refs ), false );
	}

	/*private static String nameToClass( String name )
	{
		return Character.toUpperCase( name.charAt( 0 )) + name.substring( 1 );
	}*/

	/**
	 * returns the lowercase name of a class without 'X'.
	 * If this class is XInter, returns its internal class name.
	 */
	/*private static String classToName( Object obj )
	{
		String  name    = obj instanceof XInter ?
							((XInter)obj ).m_class.getName() :
							obj.getClass().getName();
		int     beg     = name.charAt( 0 )== 'X' ? 1 : 0;

		return Character.toLowerCase( name.charAt( beg )) + name.substring( beg + 1 );
	}*/

	private static String replaceNL( String text )
	{
		int     i, j, n = text.length();
		char[]  buf     = text.toCharArray(),
				dst     = new char[n];

		for ( j = i = 0; i < n; i ++, j ++ )
		{
			if (( buf[i] == '\\' )&&( i + 1 < n)&&( buf[i+1] == 'n' ))
			{
				dst[j] = '\n';
				i ++;
			}
			else
			{
				dst[j] = buf[i];
			}
		}

		return new String( dst, 0, j );
	}
	
	private static void getItem(Element elem, Element root, Hashtable refs)
	{
	}
	
}
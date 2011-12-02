package com.socialcomputing.wps.server.swatchs;

import java.awt.Font;
import java.io.Serializable;
import java.util.Hashtable;

import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.FontX;
import com.socialcomputing.wps.client.applet.Transfo;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public abstract class ValueContainer implements Serializable, Clientable
{
	/**
	 * The Class in wich this propertie will be cast. Supported Classes are:
	 * 0- Boolean
	 * 1- Integer
	 * 2- Float
	 * 5- Color, RGB color
	 * 6- Transfo, 2D real polar vector
	 * 7- Font
	 */
	protected Class m_class = null;

	public Object toClient( Hashtable refs )
	{
		System.out.println( getClass().toString()+ " does not implements toClient( Hashtable refs )");
		return null;
	}

	public String[] getProps( )
	{
		System.out.println( "getProps NYI for this ValueContainer " + this );
		return null;
	}

	public boolean hasProps( )
	{
		System.out.println( "hasProps NYI for this ValueContainer " + this );
		return false;
	}

	public String[] getSubProps( )
	{
		return null;
	}

	public boolean isAdaptive()
	{
		return false;
	}

	protected void setClass( Class cls )
	{
		m_class = cls;
	}

	protected abstract Object getRawValue( Hashtable refs );

//	public VContainer toClient( )
//	{
//		System.out.println( "toClient NYI for this ValueContainer" );
//		return null;
//	}
//
//	public Object getRawValue( )
//	{
//		System.out.println( "getRawValue NYI for this ValueContainer" );
//		return null;
//	}

//	public static VContainer toClient( ValueContainer val )
//	{
//		return val != null ? val.toClient() : null;
//	}

	/**
	 * Gets this Container Value corresponding to the raw property coming from the DB.
	 * It's the value that will be stored in the WPSApplet zones, in the props table.
	 * @param propName	Name of property to retrieve, in case this ValueContainer contains many.
	 * @param rawProp	Raw DB property value.
	 * @return			The transformed value to store in a zone of the WPSApplet's Plan.
	 */
	public Object getValue( String propName, Object rawProp )
	{
		if ( rawProp != null )
		{
			if ( m_class.equals( String.class ))
			{
				return getString( rawProp );
			}
			else if ( m_class.equals( Integer.class ))
			{
				return getInteger( rawProp );
			}
			else if ( m_class.equals( Float.class ))
			{
				return getFloat( rawProp );
			}
			else if ( m_class.equals( ColorX.class ))
			{
				return getColor( rawProp );
			}
			else if ( m_class.equals( Transfo.class ))
			{
				return getTransfo( rawProp );
			}
			else if ( m_class.equals( FontX.class ))
			{
				return getFont( rawProp );
			}
			else if ( m_class.equals( Boolean.class ))
			{
				return getBoolean( rawProp );
			}
			else
			{
				System.out.println( "ValueContainer.getValue : Unknown Class type " + m_class );
				return null;
			}
		}
		else    // default values
		{
			return getDefaultValue( propName );
		}
	}

	protected Object getDefaultValue( String propName )
	{
		System.out.println( "ValueContainer.getValue : rawProp for " + propName + " is null" );

		if ( m_class.equals( String.class ))
		{
			return "";
		}
		else if ( m_class.equals( Integer.class ))
		{
			return new Integer( 0 );
		}
		else if ( m_class.equals( Float.class ))
		{
			return new Float( 0 );
		}
		else if ( m_class.equals( ColorX.class ))
		{
			return new ColorX( 0 );
		}
		else if ( m_class.equals( Transfo.class ))
		{
			return new Transfo( 0, 0, 1, Transfo.ABS_BIT | Transfo.CART_BIT );
		}
		else if ( m_class.equals( FontX.class ))
		{
			FontX   font = new FontX();

			font.m_containers = new VContainer[]
			{
				new VContainer( new Integer( Font.PLAIN ), false ),
				new VContainer( "SansSerif", false ),
				new VContainer( new Integer( 12 ), false ),
			};

			return font;
		}
		else if ( m_class.equals( Boolean.class ))
		{
			return new Boolean( false );
		}
		else
		{
			System.out.println( "ValueContainer.getValue : Unknown Class type " + m_class );
			return null;
		}
	}

	/**
	 * Methodes getXXXX are called to retrieve properties from the DB
	 */
	protected Boolean getBoolean( Object rawProp )
	{
		System.out.println( "getBoolean NYI for this ValueContainer" );
		return null;
	}

	protected Integer getInteger( Object rawProp )
	{
		System.out.println( "getInteger NYI for this ValueContainer" );
		return null;
	}

	protected Float getFloat( Object rawProp )
	{
		System.out.println( "getFloat NYI for this ValueContainer" );
		return null;
	}

	protected String getString( Object rawProp )
	{
		System.out.println( "getString NYI for this ValueContainer" );
		return rawProp.toString();
	}

	protected ColorX getColor( Object rawProp )
	{
		System.out.println( "getColor NYI for this ValueContainer" );
		return null;
	}

	protected Transfo getTransfo( Object rawProp )
	{
		System.out.println( "getTransfo NYI for this ValueContainer" );
		return null;
	}

	protected FontX getFont( Object rawProp )
	{
		System.out.println( "getFont NYI for this ValueContainer" );
		return null;
	}
}

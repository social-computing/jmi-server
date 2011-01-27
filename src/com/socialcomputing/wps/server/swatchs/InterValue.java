package com.socialcomputing.wps.server.swatchs;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.Transfo;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * <p>Title: InterValue</p>
 * <p>Description: A value container holding an interpolated property.<br>
 * First the property must be normalized using fixed bounds or automatic bounds.
 * Then the normalized value is mapped to key values using specified method.
 * The keys can be of different type:
 * <ul>
 * <li>Integer, Float : simple numerical interpolation.</li>
 * <li>String, Boolean : stairs interpolation. Returns the nearest String or Boolean.</li>
 * <li>Color, Transfo : numerical interpolation of each components (R,G,B) or (dir,pos,scl).</li>
 * </ul>
 * If no interpolator is specified (no property), a random one is created.
 * </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 * @see	XInter
 */
public class InterValue extends ValueContainer implements Serializable, Propable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8338652394974676325L;

	/**
	 * The XInter that do the real interpolation job.
	 */
	private XInter      m_inter;

	/**
	 * An automaticaly generated property name.
	 * Because each Inter container must reference a value through a new property generated on-ze-fly.
	 */
	private String      m_name;

	/**
	 * The interpolation normalized position in [0,1].
	 */
	private float       m_pos;

	/**
	 * An index used to generate a unique name for each container automatic prop.
	 */
	private static int  s_id = 0;

	/**
	 * Creates a new InterValue using an XInter XML element reader.
	 * The new property name is generated.
	 * @param inter	The XInter containing the interpolation information.
	 */
	public InterValue( XInter inter )
	{
		m_inter     = inter;
		m_name      = getNewName();
		m_class     = inter.m_class;
	}

	/**
	 * Gets a new property name.
	 * To avoid conflicts with user properties, the name begins with '~' that is fobidden for user.
	 * @return	~INTER#XXX, a unique id for this inner automatic prop.
	 */
	private String getNewName()
	{
		s_id	= s_id + 1 > 0 ? s_id + 1 : 0;

		return "~INTER#" + s_id;
	}

	/**
	 * Gets the properties referenced by this container : the inner one and eventually the interpolator.
	 * @return	An array containing "~INTER#XXX" and the interpolator name if it exists.
	 */
	public String[] getProps()
	{
		return m_inter.m_binding != null ? new String[]{ m_name, m_inter.m_binding } : new String[]{ m_name };
	}

	/**
	 * Returns wether this property bounds are automaticaly evaluated.
	 * @return	True if this use adaptative bounds.
	 */
	public boolean isAdaptive()
	{
		return m_inter.m_isAdaptive && m_inter.m_binding != null;
	}

	/**
	 * Gets the interpolation of the raw property.
	 * @param propName	Name of the only property to retrieve : "~INTER#XXX"
	 * @param rawProp	A numerical interpolator value.
	 * @return			Interpolated or random value or default value if rawProp == null.
	 */
	public Object getValue( String propName, Object rawProp )
	{
		if ( rawProp != null )
		{
			return propName.equals( m_name ) ? super.getValue( propName, rawProp ) : rawProp;
		}
		else    // default values
		{
			return getDefaultValue( propName );
		}
	}

	/**
	 * Evaluate and set the interpolation position correponding to the raw property value.
	 * If there's no interpolator prop, the position is randomly generated.
	 * @param rawProp	A numerical interpolator value.
	 */
	private void setInterpolationFactor( Object rawProp )
	{
		m_pos = m_inter == null ? (float)Math.random() : m_inter.getNum( rawProp );
	}

	/**
	 * Gets the index of the nearest key corresponding to the interpolator value.
	 * The key pos is always less than or equal to the normalized interpolator value.
	 * The inner m_pos local interpolator is updated.
	 * @param rawProp	A numerical interpolator value.
	 * @return			A key index to find the first key to interpolate.
	 */
	private int getKeyFromPos( Object rawProp )
	{
		setInterpolationFactor( rawProp );

		float[] keysPos = m_inter.m_keysPos;
		float   beg, end;
		int     i       = Arrays.binarySearch( keysPos, m_pos );

		if ( i < 0 )        // pos is between keys
		{
			if ( i == -1 )  // pos if between 0 and the first key.
			{
				i       = 0;
				m_pos   = 0.f;
			}
			else
			{
				i   = -i - 2;

				if ( i + 1 < keysPos.length )
				{
					beg     = keysPos[i];
					end     = keysPos[i+1];
					m_pos   = ( m_pos - beg )/(	end - beg );
				}
				else        // pos is between the last key and 1
				{
					i --;
					m_pos   = 1.f;
				}
			}
		}
		else                // pos is a key
		{
			if ( i  + 1 < keysPos.length )
			{
				m_pos   = 0.f;
			}
			else
			{
				i --;
				m_pos   = 1.f;
			}
		}

		return i;
	}

	/**
	 * Gets an Integer as result of the interpolation at a given position.
	 * @param rawProp	A numerical interpolator value.
	 * @return			The interpolation of Integer keys.
	 */
	protected Integer getInteger( Object rawProp )
	{
		//ArrayList   keys    = m_inter;
		int         i       = getKeyFromPos( rawProp );
		Object[]    iKeys   = getInterKeys( i );
		float		B       = ((Integer)iKeys[1]).floatValue(),
					C       = ((Integer)iKeys[2]).floatValue(),
					AC      = 0.f,
					BD      = 0.f,
					dPos;
		float[]     keysPos = m_inter.m_keysPos;

		if ( iKeys[0] != null )
		{
			dPos    = keysPos[i+1] - keysPos[i-1];
			AC      = ( C -((Integer)iKeys[0]).floatValue())/ dPos;
		}

		if ( iKeys[3] != null )
		{
			dPos    = keysPos[i+2] - keysPos[i];
			BD      = (((Integer)iKeys[3]).floatValue()- B )/ dPos;
		}

		return new Integer( Math.round( m_inter.interpolate( AC, B, C, BD, m_pos )));
	}

	/**
	 * Gets a Transfo as result of the interpolation at a given position.
	 * The dir, pos and scl component of the Transfo are interpolated separately.
	 * @param rawProp	A numerical interpolator value.
	 * @return			The interpolation of Transfo keys.
	 */
	protected Transfo getTransfo( Object rawProp )
	{
		//ArrayList   keys    = m_inter;
		int         i       = getKeyFromPos( rawProp );

		Object[]    iKeys   = getInterKeys( i );
		Transfo		B       = (Transfo)iKeys[1],
					C       = (Transfo)iKeys[2],
					A       = null,
					D       = null;
		float		AC      = 0.f,
					BD      = 0.f,
					ACPos   = 0.f,
					BDPos   = 0.f,
					dir, pos, scl;
		float[]     keysPos = m_inter.m_keysPos;

		if ( iKeys[0] != null )
		{
			A       = (Transfo)iKeys[0];
			ACPos   = keysPos[i+1] - keysPos[i-1];
		}

		if ( iKeys[3] != null )
		{
			D       = (Transfo)iKeys[3];
			BDPos   = keysPos[i+2] - keysPos[i];
		}

		if ( A != null )    AC  = ( C.m_dir - A.m_dir )/ ACPos;
		if ( D != null )    BD  = ( D.m_dir - B.m_dir )/ BDPos;

		dir	= m_inter.interpolate( AC, B.m_dir, C.m_dir, BD, m_pos );

		if ( A != null )    AC  = ( C.m_pos - A.m_pos )/ ACPos;
		if ( D != null )    BD  = ( D.m_pos - B.m_pos )/ BDPos;

		pos	= m_inter.interpolate( AC, B.m_pos, C.m_pos, BD, m_pos );

		if ( A != null )    AC  = ( C.m_scl - A.m_scl )/ ACPos;
		if ( D != null )    BD  = ( D.m_scl - B.m_scl )/ BDPos;

		scl	= m_inter.interpolate( AC, B.m_scl, C.m_scl, BD, m_pos );

		return new Transfo( dir, pos, scl, B.m_flags );
	}

	/**
	 * Gets an array of component from an RGB ColorX Object.
	 * @param key	ColorX key.
	 * @return		An int array containing the red, green and blue components.
	 */
	/*private int[] getRGBFromKey( Object key )
	{
		int c   = ((ColorX)key ).m_color;

		return new int[]
		{
			( c >> 16 )& 0xff,
			( c >> 8 )& 0xff,
			c & 0xff,
		};
	}*/

	private float[] getComponentsFromKey( Object key, boolean isHSB )
	{
		float[] comps	= new float[3];
		int 	c   	= ((ColorX)key ).m_color,
				r		= ( c >> 16 )& 0xff,
				g		= ( c >> 8 )& 0xff,
				b		= c & 0xff;

		if ( isHSB )
		{
			Color.RGBtoHSB( r, g, b, comps );
		}
		else
		{
			comps[0]	= r;
			comps[1]	= g;
			comps[2]	= b;
		}

		return comps;
	}

	/**
	 * Gets a ColorX as result of the interpolation at a given position.
	 * The components of the ColorX are interpolated separately.
	 * These components are RGB or HSB if m_isHSB is 'true'.
	 * @param rawProp	A numerical interpolator value.
	 * @return			The interpolation of ColorX keys.
	 */
	protected ColorX getColor( Object rawProp )
	{
		//ArrayList   keys    = m_inter;
		int         i       = getKeyFromPos( rawProp );
		boolean		isHSB	= m_inter.m_isHSB;
		Object[]    iKeys   = getInterKeys( i );
		float[]     B       = getComponentsFromKey( iKeys[1], isHSB ),
					C       = getComponentsFromKey( iKeys[2], isHSB ),
					A       = null,
					D       = null,
					comps	= new float[3];
		float		AC      = 0.f,
					BD      = 0.f,
					ACPos   = 0.f,
					BDPos   = 0.f;
		float[]     keysPos = m_inter.m_keysPos;

		if ( iKeys[0] != null )
		{
			A       = getComponentsFromKey( iKeys[0], isHSB );
			ACPos   = keysPos[i+1] - keysPos[i-1];
		}

		if ( iKeys[3] != null )
		{
			D       = getComponentsFromKey( iKeys[3], isHSB );
			BDPos   = keysPos[i+2] - keysPos[i];
		}

		for ( i = 0; i < 3; i ++ )
		{
			if ( A != null )    AC  = ( C[i] - A[i] )/ ACPos;
			if ( D != null )    BD  = ( D[i] - B[i] )/ BDPos;

			comps[i]	= m_inter.interpolate( AC, B[i], C[i], BD, m_pos );
		}

		return new ColorX( getColorFronComponents( comps, isHSB ));
	}

	protected int getColorFronComponents( float[] comps, boolean isHSB )
	{
		if ( isHSB )
		{
			return Color.HSBtoRGB ( comps[0], comps[1], comps[2] );
		}
		else
		{
			int	i, comp, color	= 0;

			for ( i = 0; i < 3; i ++ )
			{
				comp	= Math.round( comps[i] );

				if ( comp > 255 )   comp = 255;
				if ( comp < 0 )     comp = 0;

				color |= comp <<(( 2 - i )<< 3 );
			}

			return color;
		}
	}

	/**
	 * Gets a Boolean as result of the interpolation at a given position.
	 * As Boolean are discret values, The returned value is the nearest Key (True or False).
	 * @param rawProp	A numerical interpolator value.
	 * @return			The interpolation of Boolean keys.
	 */
	protected Boolean getBoolean( Object rawProp )
	{
		ArrayList   keys    = m_inter;
		int         begKey  = getKeyFromPos( rawProp );

		return m_pos < .5f ? (Boolean)keys.get( begKey ) : (Boolean)keys.get( begKey + 1 );
	}

	/**
	 * Gets a Float as result of the interpolation at a given position.
	 * @param rawProp	A numerical interpolator value.
	 * @return			The interpolation of Float keys.
	 */
	protected Float getFloat( Object rawProp )
	{
		//ArrayList   keys    = m_inter;
		int         i       = getKeyFromPos( rawProp );
		Object[]    iKeys   = getInterKeys( i );
		float		B       = ((Float)iKeys[1]).floatValue(),
					C       = ((Float)iKeys[2]).floatValue(),
					AC      = 0.f,
					BD      = 0.f,
					dPos;
		float[]     keysPos = m_inter.m_keysPos;

		if ( iKeys[0] != null )
		{
			dPos    = keysPos[i+1] - keysPos[i-1];
			AC      = ( C -((Float)iKeys[0]).floatValue())/ dPos;
		}

		if ( iKeys[3] != null )
		{
			dPos    = keysPos[i+2] - keysPos[i];
			BD      = (((Float)iKeys[3]).floatValue()- B )/ dPos;
		}

		return new Float( m_inter.interpolate( AC, B, C, BD, m_pos ));
	}

	/**
	 * Gets a String as result of the interpolation at a given position.
	 * As Strings are discret values, The returned value is the nearest Key (no String mix!).
	 * @param rawProp	A numerical interpolator value.
	 * @return			The interpolation of String keys.
	 */
	protected String getString( Object rawProp )
	{
		ArrayList   keys    = m_inter;
		int         begKey  = getKeyFromPos( rawProp );

		return m_pos < .5f ? (String)keys.get( begKey ) : (String)keys.get( begKey + 1 );
	}

	/**
	 * Convert this to a WPSApplet bound Container.
	 * The container is bound to the inner property '~INTER#XXX'
	 * @param refs	Not used.
	 * @return		A new VContainer referencing the inner prop.
	 */
	public VContainer toClientCont( Hashtable refs )
	{
		return new VContainer( m_name, true );
	}

	public Object getRawValue( Hashtable refs )
	{
		return m_name;
	}

	/**
	 * Add this prop to a list.
	 * @param list	A list of props retrieved from the Swatch holding this.
	 */
	public void addPropsToList( ArrayList list )
	{
		list.add( this );
	}

	/**
	 * Updates the range of the interpolator if it's an adaptative one.
	 * @param rawProp	A numerical interpolator value.
	 */
	public void updateRange( Object rawProp )
	{
		double  val;

		if ( rawProp instanceof Number )
		{
			val  = ((Number)rawProp ).doubleValue();
		}
		else if ( rawProp instanceof Boolean )
		{
			val  = ((Boolean)rawProp ).booleanValue() ? 1 : 0 ;
		}
		else if ( rawProp instanceof String )
		{
			try
			{
				val  = Double.parseDouble((String)rawProp );
			}
			catch( NumberFormatException e )
			{
				val = 0;
			}
		}
		else    // default value
		{
			val = 0;
		}

		if ( val < m_inter.m_min )  m_inter.m_min = val;
		if ( val > m_inter.m_max )  m_inter.m_max = val;
	}

	/**
	 * Gets the key surrounding a given key.
	 * @param i	Index of the referenced key.
	 * @return	An array containing the i-1, i, i+1, i+2 keys. Keys outside the array are replaced with null.
	 */
	private Object[] getInterKeys( int i )
	{
		ArrayList   keys    = m_inter;
		Object[]    iKeys   = new Object[4];


		iKeys[0] = i > 0 ? keys.get( i - 1 ) : null;
		iKeys[1] = keys.get( i );
		iKeys[2] = keys.get( i + 1 );
		iKeys[3] = i + 2 < keys.size() ? keys.get( i + 2 ) : null;

		return iKeys;
	}
}

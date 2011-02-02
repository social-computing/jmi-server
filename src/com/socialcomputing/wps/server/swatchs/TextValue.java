package com.socialcomputing.wps.server.swatchs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.client.applet.Base;
import com.socialcomputing.wps.client.applet.Token;
import com.socialcomputing.wps.client.applet.VContainer;
import com.socialcomputing.utils.EZDebug;

/**
 * <p>Title: TextValue</p>
 * <p>Description: A ValueContainer holding a text with or without properies.<br>
 * There is no limitation concerning the number/type of properties that can be referenced in this.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class TextValue extends ValueContainer implements Serializable, Propable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3104134468985535077L;

	private static final Logger log = LoggerFactory.getLogger(TextValue.class);
	
	/**
	 * Raw text value.
	 */
	protected   String m_val;

	/**
	 * The text/prop token list of this.
	 * @see Token
	 */
	private transient Token[]   m_tokens = null;

	/**
	 * Creates a new TextValue using a text.
	 * @param val	Text to encapsulate.
	 */
	public  TextValue( String val )
	{
		m_val   = val;
		m_class = String.class;
//		this( val, String.class );
	}

//	public  TextValue( String val, Class cls )
//	{
//		m_val   = val;
//		m_class = cls;
//	}

	protected Object getRawValue( Hashtable refs )
	{
		return m_val;
	}

	/**
	 * This is a shortcut because m_class is always a String.
	 * It is nearly a pass-through, returning rawProp or rawProp.toString().
	 */
	public Object getValue( String propName, Object rawProp )
	{
		if ( rawProp != null )
		{
			if ( isListProp( propName ))
			{
				if ( rawProp instanceof String[] )
				{
					return rawProp;
				}
				else if ( rawProp instanceof String )
				{    // Facility
					String  [] as = new String[1];
					as[0] = ( String) rawProp;
					return as;
				}
				else if ( rawProp instanceof Object[] )
				{
					return rawProp;
				}
				else
				{
					log.debug("TextValue.getValue : This prop is not a list!");
					return null;
				}
			}
			else
			{
				return m_class.isInstance( rawProp ) ? rawProp : rawProp.toString();
			}
		}
		else    // default values
		{
			return getDefaultValue( propName );
		}
	}

	/**
	 * Gets the DB properties name referenced in this text.
	 * For example : "The rank of [%10sSite] {%sSite_loc} is [%2dRank]" reference 3 properties : 'Site', 'Site_loc' and 'Rank'.
	 * This information is necessary to retrieve the properties value from the DB.
	 * @return	An array containing the properties name.
	 */
	public String[] getProps( )
	{
		Vector      tokens  = Base.parseTokens( m_val );
		ArrayList   props   = new ArrayList();
		String      prop;

		m_tokens    = (Token[])tokens.toArray( new Token[0] );
		prop        = Base.getNextTokenProp( tokens, Token.PROP_BIT, 0 );//Token.SUB_BIT );

		while ( prop != null )
		{
			props.add( prop );
			prop = Base.getNextTokenProp( tokens, Token.PROP_BIT, 0 );//Token.SUB_BIT );
		}

		return (String[])props.toArray( new String[props.size()] );
	}

	/**
	 * Gets the Analysis properties name referenced in this text.
	 * Those properties are suggestions that are generated dynamicaly by the Analysis.
	 * So they must be differenciated from the others to be handled correctly.
	 * Such properties starts with an '_' like '_OFFERS_NAME'.
	 * @return	An array containing the Analysis properties name.
	 */
/*	public String[] getSubProps( )
	{
		Vector      tokens  = Base.parseTokens( m_val );
		ArrayList   props   = new ArrayList();
		String      prop;

		m_tokens    = (Token[])tokens.toArray( new Token[0] );
		prop        = Base.getNextTokenProp( tokens, Token.SUB_BIT, 0 );

		while ( prop != null )
		{
			props.add( prop );
			prop = Base.getNextTokenProp( tokens, Token.SUB_BIT, 0  );
		}

		return (String[])props.toArray( new String[props.size()] );
	}*/

	public void addPropsToList( ArrayList list )
	{
		if ( hasProps()) list.add( this );
	}

	public VContainer toClientCont( Hashtable refs )
	{
		return new VContainer( m_val, false );
	}

	public Object toClient( Hashtable refs )
	{
		return m_val;
	}

	public boolean hasProps( )
	{
		return hasProps( m_val );
	}

	/**
	 * Returns whether this references properties or is just a static text.
	 * @param text	The text to test.
	 * @return		True if test contains at least one normal or list property reference ({prop} or [prop]).
	 */
	public static boolean hasProps( String text  )
	{
		int     i, n = text.length();
		char    c, cPrev = ' ';

		for ( i = 0; i < n; i ++ )
		{
			c = text.charAt( i );
			if (( c == '{' || c == '[' )&& cPrev != '\\' )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether a property referenced by this is a list one.
	 * @param text	The text to test.
	 * @return		True if test contains at least one normal or list property reference ({prop} or [prop]).
	 */
	/**
	 * Returns whether a property referenced by this is a list one.
	 * @param propName	The name of the property to test.
	 * @return			True if the property is referenced and is a list one ([prop]).
	 */
	private boolean isListProp( String propName )
	{
		if ( m_tokens != null )
		{
			int     i, n = m_tokens.length;
			Token   token;

			for ( i = 0; i < n; i ++ )
			{
				token = m_tokens[i];

				if ( propName.equals( token.m_buffer.toString()))
				{
					return ( token.m_flags & Token.LIST_BIT )!= 0;
				}
			}
			log.debug("TextValue.getValue : unknown prop'{}', use TextValue.getProps", propName);
			return false;
		}
		else
		{
			log.debug( "Call TextValue.getProps before TextValue.getValue");
			return false;
		}
	}

/*	public static void main( String[] args )
	{
		String          text        = "Hello [sNAME]. You are {3dAGE} years old and comes from {sCountry}";
		ValueContainer  serverVal   = new TextValue( text );
		String[]        props       = serverVal.getProps();

		for ( int i = 0; i < props.length; i ++ )
			System.out.println( props[i] + ", " );
	}*/
}

package com.socialcomputing.utils;

import java.util.*;

/**
 * Title:        EZParams
 * Description:  Manage parameters extracted from a String
 * Copyright:    Copyright (c) 2002
 * Company:      MapStan
 * @author
 * @version 1.0
 */

public class EZParams
{
	private Hashtable   m_args;

	public EZParams( String[] args )
	{
		String  arg;
		int     i, pos,
				n   = args.length;

		m_args  = new Hashtable( args.length );

		for ( i = 0; i < n; i ++ )
		{
			arg = args[i];
			pos = arg.indexOf( '=' );

			if ( pos != -1 )
			{
				m_args.put( arg.substring( 0, pos ), arg.substring( pos + 1 ));
			}
		}
	}

	public boolean isEnabled( String key )
	{
		String	value	= (String)m_args.get( key );

		return value != null ? value.equalsIgnoreCase( "true" ): false;
	}

	public boolean isEnabled( String key, boolean defVal )
	{
		String	value	= (String)m_args.get( key );

		return value != null ? value.equalsIgnoreCase( "true" ): defVal;
	}

	public String getParameter( String key )
	{
		return (String)m_args.get( key );
	}

	public String getParameter( String key, String defVal )
	{
		String  value   = (String)m_args.get( key );

		return value != null ? value : defVal;
	}

	public int getIntParameter( String key )
	{
		return getIntParameter( key, 0 );
	}

	public int getIntParameter( String key, int defVal )
	{
		int     iVal    = defVal;
		String  value   = (String)m_args.get( key );

		if ( value != null )
		{
			try
			{
				iVal    = Integer.parseInt( value );
			}
			catch ( NumberFormatException e )
			{
				System.out.println( "Value for key " + key + " is not an int : " + value );
			}
		}

		return iVal;
	}
}

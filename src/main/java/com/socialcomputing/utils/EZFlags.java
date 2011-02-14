package com.socialcomputing.utils;

/**
 * Title:        EZFlags
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VOYEZ VOUS
 * @author
 * @version 1.0
 */

public final class EZFlags
{
	private static final int	s_mask	= 1 << 31;
	private int m_flags;

	public EZFlags()
	{
		m_flags = 0;
	}

	public EZFlags( int flags )
	{
		m_flags = flags;
	}

	public EZFlags( EZFlags flags )
	{
		m_flags = flags.m_flags;
	}

	public boolean isEnabled( int bit )
	{
		return ( m_flags & bit )!= 0;
	}

	public boolean isDisabled( int bit )
	{
		return ( m_flags & bit )== 0;
	}

	public void setEnabled( int bit, boolean isEnabled )
	{
		if ( isEnabled )
		{
			m_flags |= bit;
		}
		else
		{
			m_flags &= ~bit;
		}
	}

	public void enable( int bit )
	{
		m_flags |= bit;
	}

	public void disable( int bit )
	{
		m_flags &= ~bit;
	}

	public void toggle( int bit )
	{
		if (( m_flags & bit )== 0 ) // the bit is disabled
		{
			m_flags |= bit;
		}
		else
		{
			m_flags &= ~bit;
		}
	}

	public void parseBoolean( String boolStr )
	{
		char[]	buf		= new char[32];
		int		i, n	= Math.min( boolStr.length(), 32 ),
				mask	= 1;

		m_flags	= 0;
		boolStr.getChars( 0, n, buf, 0 );

		for ( i = 31; i >= 0; i -- )
		{
			if ( buf[i]	== '1' )	m_flags |= mask;
			mask <<= 1;
		}
	}

	public String toString()
	{
		char[]	buf		= new char[32];
		int		i, bit	= m_flags;

		for ( i = 0; i < 32; i ++ )
		{
			buf[i]	= ( bit & s_mask )== 0 ? '0' : '1';
			bit   <<= 1;
		}

		return new String( buf );
	}

	public static boolean isEnabled( int flags, int bit )
	{
		return ( flags & bit )!= 0;
	}

	public static boolean isDisabled( int flags, int bit )
	{
		return ( flags & bit )== 0;
	}

	public static void setEnabled( int flags, int bit, boolean isEnabled )
	{
		if ( isEnabled )
		{
			flags |= bit;
		}
		else
		{
			flags &= ~bit;
		}
	}

	public static void enable( int flags, int bit )
	{
		flags |= bit;
	}

	public static void disable( int flags, int bit )
	{
		flags &= ~bit;
	}

	public static void toggle( int flags, int bit )
	{
		if (( flags & bit )== 0 ) // the bit is disabled
		{
			flags |= bit;
		}
		else
		{
			flags &= ~bit;
		}
	}

	public static void main( String[] args )
	{
		EZFlags	flags	= new EZFlags( 6549842 );
		System.out.println( "flags = " + flags );

		flags.parseBoolean( "00000000011000111111000101010010" );
		System.out.println( "flags = " + flags );
	}
}
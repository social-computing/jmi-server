package com.socialcomputing.utils;

/**
 * Title:        EZDebug
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VOYEZ VOUS
 * @author
 * @version 1.0
 */

public final class EZDebug
{
	/**
	 * No debug info is printed
	 */
	public static final int NONE  = -1;

	/**
	 * Only most important debug info is printed
	 */
	public static final int LOW   = 0;

	/**
	 * Most important and Basic debug info is printed
	 */
	public static final int MED   = 1;

	/**
	 * All debug info is printed
	 */
	public static final int HIGH  = 2;

	public static final boolean PUSH    = true;
	public static final boolean POP     = false;

	/**
	 * Current depth of display
	 */
	private static int  s_depth = 0;

	public static int  s_level = NONE;   // No debug by default

	public static void setVerbosity( int level )
	{
		if ( level < NONE || level > HIGH )
		{
			System.out.println( "Invalid verbosity level : " + level );
		}
		else
		{
			s_level = level;
		}
	}

	public static void push()
	{
		println( " ->", LOW );
		s_depth ++;
	}

	public static void pop()
	{
		if ( s_depth == 0 )
		{
			println( "Timer : pop count > push count", HIGH );
		}
		else
		{
			s_depth --;
			println( " <-", LOW );
		}
	}

	public static void print( String s )
	{
		print( s, LOW );
	}

	public static void println( String s )
	{
		print( s + '\n', LOW );
	}

	public static void println( String s, int level )
	{
		print( s + '\n', level );
	}

	public static void print( String s, int level )
	{
		if ( level < LOW || level > HIGH )
		{
			System.out.println( "Invalid debug level : " + level + " -> " + s );
		}
		else if ( level >= HIGH - s_level )
		{
			String  offset = new String();

			for ( int i = 0; i < s_depth; i ++ )
			{
				offset += "   ";
			}

			System.out.print( offset + s );
		}
	}

	public static boolean isPrintable()
	{
		return isPrintable( LOW );
	}

	public static boolean isPrintable( int level )
	{
		return level >= HIGH - s_level;
	}

	public static void readLevelFromParams( EZParams params )
	{
		int debug   = params.getIntParameter( "dbg", EZDebug.MED );

		setVerbosity( debug );
	}

	public static String getLevelUsage()
	{
		String  msg = "      dbg='VERBOSITY LEVEL'  to sets the verbosity level:\n"
					+ "                     [-1] : no debug message are printed\n"
					+ "                     [0] : low verbosity, only the critical debug messages are printed\n"
					+ "                     [1] : medium verbosity, most of the debug messages are printed\n"
					+ "                     [2] : high verbosity, every debug messages are printed\n";

		return msg;
	}

	public static String getTrace( Object obj, String msg )
	{
		return obj.getClass().getName()+ ':' + msg;
	}

	public static void main( String[] args )
	{
		System.out.println( getLevelUsage());

		for ( int i = -2; i < 4; i ++ )
		{
			System.out.println( "Verbosity is set to " + i );
			EZDebug.setVerbosity( i );
			EZDebug.println( "println" );
			EZDebug.print( " print -1", -1 );
			EZDebug.print( " print 3", 3 );
			EZDebug.print( " print LOW", EZDebug.LOW );
			EZDebug.print( " print MED", EZDebug.MED );
			EZDebug.print( " print HIGH", EZDebug.HIGH );
			System.out.println( "" );
		}
	}
}
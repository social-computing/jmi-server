package com.socialcomputing.utils.math;

import java.util.Random;

/**
 * <p>Title: EZMath</p>
 * <p>Description: Simple math library with useful constants.</p>
 * <p>Copyright: Copyright (c) 2001-2002 MapStan</p>
 * <p>Company: MapStan (VoyezVous)</p>
 * @author flugue@mapstan.com
 * @version 1.8
 */
public class EZMath
{
	/**
	 * PI ~ 3.14.....
	 */
	public static final float       PI      = (float)Math.PI;

	/**
	 * 2PI ~ 6.28.....
	 */
	public static final float       PI2     = (float)( 2 * Math.PI );

	/**
	 * SQRT(2) ~ 1.414.....
	 */
	public static final float       SQRT2   = (float)( Math.sqrt( 2 ));

	/**
	 * random generator seed.
	 */
	private static int              s_seed  = 0;

	/**
	 * General purpose random generator.
	 */
	public static final    Random   s_rand  = new Random( s_seed );

	/**
	 * Map a value from [0,1] to [beg,end] using a linear interpolation.
	 * This is the inverse of normalize.
	 * @see			normalize
	 * @param x		value in [0,1]
	 * @param beg	output value for x=0
	 * @param end	output value for x=1
	 * @return		a value in [beg,end] depending on x
	 */
	public static float interLin( float x, float beg, float end )
	{
		return beg + x *( end - beg );
	}

	/**
	 * Normalize a value between [beg,end] to [0,1].
	 * This is the inverse of interLin.
	 * If beg == end, return 1.
	 * @see			interLin
	 * @param x		value in [beg,end]
	 * @param beg	maximum value for x to return 1
	 * @param end	minimum value for x to return 0
	 * @return		a value in [0,1] depending on x
	 */
	public static float normalize( float x, float beg, float end )
	{
		float   size = end - beg;

		return size == 0 ? .5f : ( x - beg )/ size;
	}

	/**
	 * Normalize a value between [beg,end] to [0,1] using tension.
	 * This is the inverse of interLin.
	 * If beg == end, return 1.
	 * @see			interLin
	 * @param x		value in [beg,end]
	 * @param beg	maximum value for x to return 1
	 * @param end	minimum value for x to return 0
	 * @param t 	tension factor : .5 = sqrt, 1 = linear, 2 = quadratic....
	 * @return		a value in [0,1] depending on x
	 */
	public static float normalize( float x, float beg, float end, float t )
	{
		float   size = end - beg;

		return size == 0 ? .5f : (float)Math.pow(( x - beg )/ size, t );
	}

	/**
	 * Generates a random number normalized in [0,1[
	 * @return a float in [0,1[
	 */
	public static float random()
	{
		return s_rand.nextFloat();
	}

	/**
	 * Generates a random number normalized in [0,x[
	 * @param x		maximum value to return
	 * @return 		a float in [0,x[
	 */
	public static float random( float x )
	{
		return x * s_rand.nextFloat();
	}

	/**
	 * Generates a random number normalized in [beg,end[
	 * @param beg	minimum value to return
	 * @param end	maximum value to return
	 * @return 		a float in [beg,end[
	 */
	public static float random( float beg, float end )
	{
		return beg + s_rand.nextFloat() *( end - beg );
	}

	/**
	 * Generates an integer equivalent staticaly to a float.
	 * @param value	float value to convert in a statistical int
	 * @return 		floor(value) or roof(value) randomly
	 */
	public static int getStatInt( float value )
	{
		int		iVal	= (int)value;
		float	q		= value - iVal;

		return ( q != 0 && s_rand.nextFloat()< q )?	iVal + 1 : iVal;
	}

	/**
	 * Reset the random generator seed to the current seed.
	 * @see		random
	 */
	public static void resetSeed()
	{
		s_rand.setSeed( s_seed );
	}

	/**
	 * Sets the random generator seed.
	 * @see			random
	 * @param seed	a number used as a seed
	 */
	public static void setSeed( int seed )
	{
		s_seed = seed;
	}

	public static void main( String[] args )
	{
		int		i, n		= 1000,
				iVal, sum	= 0;
		float	val			= 2.56f;


		for ( i = 0; i < n; i ++ )
		{
			iVal	= getStatInt( val );
			sum		+= iVal;
//			System.out.println( "iVal = " + iVal );
		}

		System.out.println( "Average val = " +( sum /(float)n ));
	}
}
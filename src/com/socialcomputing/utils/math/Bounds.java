package com.socialcomputing.utils.math;

import java.io.Serializable;
import java.util.Arrays;

import com.socialcomputing.utils.EZDebug;
import com.socialcomputing.utils.ValueGetter;

/**
 * Title:        Plan Generator
 * Description:  classes used to generate a Plan that will be sent to the applet.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class Bounds implements Serializable
{
	static final long serialVersionUID  = 2403692454644032415L;

	public  float   m_min;
	public  float   m_max;

	public Bounds( )
	{
		reset();
	}

	public Bounds( int min, int max )
	{
		this((float)min, (float)max );
	}

	public Bounds( float min, float max )
	{
		m_min = min;
		m_max = max;
	}

	public Bounds( Bounds bounds )
	{
		m_min = bounds.m_min;
		m_max = bounds.m_max;
	}

	public void setBounds( float min, float max )
	{
		m_min = min;
		m_max = max;
	}

	public void check( int val )
	{
		float   fVal = val;

		if ( fVal < m_min )     m_min = fVal;
		if ( fVal > m_max )     m_max = fVal;
	}

	public void check( float val )
	{
		if ( val < m_min )      m_min = val;
		if ( val > m_max )      m_max = val;
	}

	public void sizedCheck( float val, float size )
	{
		float   min = val - size;
		float   max = val + size;

		if ( min < m_min )      m_min = min;
		if ( max > m_max )      m_max = max;
	}

	public float randomize()
	{
		return EZMath.interLin( EZMath.random(), m_min, m_max );
	}

	public void reset()
	{
		m_min = Float.POSITIVE_INFINITY;
		m_max = Float.NEGATIVE_INFINITY;
	}

	public void centeredScale( float s )
	{
		float   ctr = .5f *( m_min + m_max );

		m_min = s *( m_min - ctr )+ ctr;
		m_max = s *( m_max - ctr )+ ctr;
	}

	public void scale( float s )
	{
		m_min *= s;
		m_max *= s;
	}

	public float normalize( float x )
	{
		return EZMath.normalize( x, m_min, m_max );
	}

	public float map( float x )
	{
		return EZMath.interLin( x, m_min, m_max );
	}

	public float project( float x, Bounds to )
	{
		return to.map( normalize( x ));
	}

	public boolean contains( float x )
	{
		return ( x > m_min )&&( x < m_max );
	}

	public float normalize( float x, float t )
	{
		return EZMath.normalize( x, m_min, m_max, t );
	}

	public float normalizeLen( float x )
	{
		return x /( m_max - m_min );
	}

	public float getWidth()
	{
		return m_max - m_min;
	}

	public float getCenter()
	{
		return .5f *( m_min + m_max );
	}

	public void showHisto( String msg, int sCnt, float tresh, Object[] vals, ValueGetter getter )
	{
		showHisto( msg, sCnt, tresh, vals, 0, vals.length, getter );
	}

	public void showHisto( String msg, int sCnt, float tresh, Object[] vals, int beg, int end, ValueGetter getter )
	{
		int         i, n;
		float[]     histo   = new float[sCnt];
		float       val, step    = 100.f /( end - beg );

		System.out.println( toString());

		Arrays.fill( histo, 0 );

		for ( i = beg; i < end; i ++ )
		{
			val = getter.getValue( vals[i] );
			n   = (int)( sCnt * .9999f * normalize( val ));
			histo[n] += step;
		}

		tresh  *= 100.f;

		for ( i = 0; i < sCnt; i ++ )
		{
			System.out.println( "" + (( i * 100 )/ sCnt )+ "\t| " + histo[i] );
		}
	}

	public void clipBounds( int sCnt, float tresh, Object[] vals, ValueGetter getter )
	{
		clipBounds( sCnt, tresh, vals, 0, vals.length, getter );
	}

	public void clipBounds( int sCnt, float tresh, Object[] vals, int beg, int end, ValueGetter getter )
	{
		int     i, n, max, h, hMax;
		int     vCnt    = end - beg;
		int[]   histo   = new int[sCnt];
		float   val;

		Arrays.fill( histo, 0 );

		// Increments histogram bars
		for ( i = beg; i < end; i ++ )
		{
			val = getter.getValue( vals[i] );
			n   = (int)( sCnt * .9999f * normalize( val ));
			histo[n] ++;
		}

		// Finds the maximum histogram bar
		for ( i = max = hMax = 0; i < sCnt; i ++ )
		{
			h = histo[i];

			if ( h > hMax )
			{
				max = i;
				hMax = h;
			}
		}

		int     keepCnt = (int)( tresh * vCnt );
		int     keepMin = (int)( .75f * vCnt )/ sCnt;
		int     hBeg = max;
		int     hEnd = max;
		boolean isBeg = true;

		n = histo[max];

		while ( n < vCnt )
		{
			isBeg = hBeg - 1 >= 0 && hEnd + 1 < sCnt ?
					histo[hBeg-1] > histo[hEnd+1] :
					hBeg - 1 >= 0;

			h = isBeg ? histo[--hBeg] : histo[++hEnd];
			n += h;

			if ( n > keepCnt && h < keepMin )
			{
				if ( isBeg )    hBeg ++;
				else            hEnd --;
				break;
			}
		}
		float   step    = 1.f /( sCnt - 1 );

		EZDebug.print( this.toString() + " -> " );
		m_min = map( step * hBeg );
		m_max = map( step * hEnd );
		EZDebug.println( this.toString());
	}

	public void clipValues( Object[] vals, ValueGetter getter )
	{
		clipValues( vals, 0, vals.length, getter );
	}

	public void clipValues( Object[] vals, int beg, int end, ValueGetter getter )
	{
		int     i;
		float   val;

		for ( i = beg; i < end; i ++ )
		{
			val = getter.getValue( vals[i] );
			if ( val < m_min )
			{
				getter.setValue( vals[i], m_min );
			}
			if ( val > m_max )
			{
				getter.setValue( vals[i], m_max );
			}
		}
	}

	public String toString()
	{
		return new String( "[ " + m_min + ", " + m_max + " ]" );
	}

	public void adjustBounds( int sCnt, float tresh, Object[] vals, ValueGetter getter )
	{
		adjustBounds( sCnt, tresh, vals, 0, vals.length, getter );
	}

	public void remapValues( Object[] vals, ValueGetter getter, float tens )
	{
		int     i, beg  = 0,
				end     = vals.length;
		float   val;

		m_min	= (float)Math.pow( m_min, tens );
		m_max	= (float)Math.pow( m_max, tens );

		for ( i = beg; i < end; i ++ )
		{
			val = getter.getValue( vals[i] );
			getter.setValue( vals[i], (float)Math.pow( val, tens ));
		}
	}

//	public void remapValues( Object[] vals, ValueGetter getter )
//	{
//		int     i, j, beg  = 0,
//				end     = vals.length;
//		float   val, prev = -1.f;
//		float[] fVals   = new float[end];
//
//		for ( i = beg; i < end; i ++ )
//		{
//			fVals[i] = getter.getValue( vals[i] );
//		}
//
//		Arrays.sort( fVals );
//
//		for ( i = j = beg; i < end; i ++ )
//		{
//			val = fVals[i];
//
//			if ( val != prev )
//			{
//				fVals[j] = val;
//				prev = val;
//				j ++;
//			}
//		}
//
//		for ( i = beg; i < end; i ++ )
//		{
//			val = getter.getValue( vals[i] );
//			j = Arrays.binarySearch( fVals, val );
//			getter.setValue( vals[i], j );//fVals[j]);
//		}
//	}

	public void adjustBounds( int sCnt, float tresh, Object[] vals, int beg, int end, ValueGetter getter )
	{
		int     i, n, min, max, h;
		int     vCnt    = end - beg;
		int[]   histo   = new int[sCnt];
		float   val;
		float   avg, med, sum = 0.f;

		Arrays.fill( histo, 0 );

		// Increments histogram bars
		for ( i = beg; i < end; i ++ )
		{
			val = getter.getValue( vals[i] );
			sum+= val;
			n   = (int)( sCnt * .9999f * normalize( val ));
			histo[n] ++;
		}

		// eval the average value and check wether this must be adjusted or not
		avg     = sum /( end - beg );
		med     = getCenter();
		float   t = Math.abs(( avg - med )/ avg );
		if ( t > 1.f )
		{
			EZDebug.println( "avg = " + avg + ", med = " + med + ", var = " + t );

			// Finds the average histogram bar
			max     = (int)( normalize( avg ) * sCnt );
			//hMax    = histo[max];

			int     keepCnt = (int)( tresh * vCnt );
			int     keepMin = (int)( .75f * vCnt )/ sCnt;
			int     hBeg = max;
			int     hEnd = max;
			boolean isBeg = true;

			n = histo[max];

			while ( n < vCnt )
			{
				isBeg = hBeg - 1 >= 0 && hEnd + 1 < sCnt ?
						histo[hBeg-1] > histo[hEnd+1] :
						hBeg - 1 >= 0;

				h = isBeg ? histo[--hBeg] : histo[++hEnd];
				n += h;

				if ( n > keepCnt && h < keepMin )
				{
					if ( isBeg )    hBeg ++;
					else            hEnd --;
					break;
				}
			}
			float   step    = 1.f /( sCnt - 1 );

			EZDebug.print( toString() + " -> " );
			m_min = map( step * hBeg );
			m_max = map( step * hEnd );
			EZDebug.println( this.toString());

			min = max = 0;

			for ( i = beg; i < end; i ++ )
			{
				val = getter.getValue( vals[i] );
				if ( val < m_min )
				{
					getter.setValue( vals[i], m_min );
					min ++;
				}
				if ( val > m_max )
				{
					getter.setValue( vals[i], m_max );
					max ++;
				}
			}
			EZDebug.println( "Clamped vals : " + min + " < avg < " + max + " over " + vCnt );
		}
	}
}
package com.socialcomputing.utils.math;

import java.awt.Rectangle;
import java.io.Serializable;

import com.socialcomputing.utils.geom.Vertex;

/**
 * Title:        Plan Generator
 * Description:  classes used to generate a Plan that will be sent to the applet.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class Bounds2D implements Serializable
{
	static final long serialVersionUID  = 2334546846546464535L;

	private Bounds  m_xBnd;
	private Bounds  m_yBnd;

	public Bounds2D( )
	{
		m_xBnd  = new Bounds();
		m_yBnd  = new Bounds();
	}

	public Bounds2D( Rectangle rect )
	{
		m_xBnd  = new Bounds( rect.x, rect.x + rect.width );
		m_yBnd  = new Bounds( rect.y, rect.y + rect.height );
	}

	public Bounds2D( float xmin, float xmax, float ymin, float ymax )
	{
		m_xBnd  = new Bounds( xmin, xmax );
		m_yBnd  = new Bounds( ymin, ymax );
	}

	public Bounds2D( Bounds xbnd, Bounds ybnd )
	{
		m_xBnd  = new Bounds( xbnd );
		m_yBnd  = new Bounds( ybnd );
	}

	public Bounds2D( Bounds2D bounds )
	{
		m_xBnd  = new Bounds( bounds.m_xBnd );
		m_yBnd  = new Bounds( bounds.m_yBnd );
	}

	public void setBounds( float xmin, float xmax, float ymin, float ymax )
	{
		m_xBnd.setBounds( xmin, xmax );
		m_yBnd.setBounds( ymin, ymax );
	}

	public void check( Vertex v )
	{
		m_xBnd.check( v.x );
		m_yBnd.check( v.y );
	}

	public void sizedCheck( Vertex v, float size )
	{
		if ( Float.isInfinite( v.x ) || Float.isInfinite( v.y ) || Float.isNaN( v.x ) || Float.isNaN( v.y ))
		{
			throw ( new RuntimeException( "sizedCheck v=" + v ));
		}
		m_xBnd.sizedCheck( v.x, size );
		m_yBnd.sizedCheck( v.y, size );
	}

	public Vertex randomize()
	{
		return new Vertex( m_xBnd.randomize(), m_yBnd.randomize());
	}

	public void reset()
	{
		m_xBnd.reset();
		m_yBnd.reset();
	}

	/**
	 * Rotate this by 90° CCW
	 * @return	this vertex after it was turned
	 */
	public Bounds2D turn()
	{
		float	xMin = m_xBnd.m_min,
				xMax = m_xBnd.m_max;

		m_xBnd.m_min	= -m_yBnd.m_max;
		m_xBnd.m_max	= -m_yBnd.m_min;
		m_yBnd.m_min	= xMin;
		m_yBnd.m_max	= xMax;

		return this;
	}

	public Bounds2D swapThis( boolean xSwap, boolean ySwap )
	{
		float	tmp;

		if ( xSwap )
		{
			tmp				= m_xBnd.m_min;
			m_xBnd.m_min	= -m_xBnd.m_max;
			m_xBnd.m_max	= -tmp;
		}
		if ( ySwap )
		{
			tmp				= m_yBnd.m_min;
			m_yBnd.m_min	= -m_yBnd.m_max;
			m_yBnd.m_max	= -tmp;
		}

		return this;
	}

	public Vertex normalize( Vertex v )
	{
		return new Vertex( m_xBnd.normalize( v.x ), m_yBnd.normalize( v.y ));
	}

	public Vertex map( Vertex v )
	{
		return new Vertex( m_xBnd.map( v.x ), m_yBnd.map( v.y ));
	}

	public float getWidth()
	{
		return m_xBnd.getWidth();
	}

	public float getHeight()
	{
		return m_yBnd.getWidth();
	}

	public Vertex getCenter()
	{
		return new Vertex( m_xBnd.getCenter(), m_yBnd.getCenter());
	}

	public String toString()
	{
		return new String( "( " + m_xBnd + ", " + m_yBnd + " )" );
	}

	public float getAspect()
	{
		return m_xBnd.getWidth()/ m_yBnd.getWidth();
	}

	public void updateAspect( Bounds2D dst )
	{
		float   a       = getAspect(),
				aDst    = dst.getAspect();

		if ( a < aDst )
		{
			m_xBnd.centeredScale( aDst / a );
		}
		else
		{
			m_yBnd.centeredScale( a / aDst );
		}
	}

	public Vertex project( Vertex v, Bounds2D to )
	{
		return to.map( normalize( v ));
	}

	public boolean isWidthMin()
	{
		return m_xBnd.getWidth()< m_yBnd.getWidth();
	}

	public float project( float f, Bounds2D to )
	{
		return to.isWidthMin()?
				to.getWidth()* m_xBnd.normalizeLen( f ):
				to.getHeight()* m_yBnd.normalizeLen( f );
//		final float size_fac = 1.2f;
//
//		return to.isWidthMin()?
//			    size_fac * to.getWidth()* m_xBnd.normalizeLen( f ):
//				size_fac * to.getHeight()* m_yBnd.normalizeLen( f );
	}

//	public void scale( float s )
//	{
//		float   ctr = .5f *( m_min + m_max );
//
//		m_min = s *( m_min - ctr )+ ctr;
//		m_max = s *( m_max - ctr )+ ctr;
//	}

//	public boolean contains( float x )
//	{
//		return ( x > m_min )&&( x < m_max );
//	}

//	public float normalize( float x, float t )
//	{
//		return EZMath.normalize( x, m_min, m_max, t );
//	}

//	public float normalizeLen( float x )
//	{
//		return x /( m_max - m_min );
//	}

//	public void showHisto( String msg, int sCnt, float tresh, Object[] vals, ValueGetter getter )
//	{
//		showHisto( msg, sCnt, tresh, vals, 0, vals.length, getter );
//	}
//
//	public void showHisto( String msg, int sCnt, float tresh, Object[] vals, int beg, int end, ValueGetter getter )
//	{
//		int         i, n;
//		float[]     histo   = new float[sCnt];
//		float       val, step    = 100.f /( end - beg );
//
//		System.out.println( toString());
//
//		Arrays.fill( histo, 0 );
//
//		for ( i = beg; i < end; i ++ )
//		{
//			val = getter.getValue( vals[i] );
//			n   = (int)( sCnt * .9999f * normalize( val ));
//			histo[n] += step;
//		}
//
//		tresh  *= 100.f;
//
//		for ( i = 0; i < sCnt; i ++ )
//		{
//			System.out.println( "" + (( i * 100 )/ sCnt )+ "\t| " + histo[i] );
//		}
//	}
//
//	public void clipBounds( int sCnt, float tresh, Object[] vals, ValueGetter getter )
//	{
//		clipBounds( sCnt, tresh, vals, 0, vals.length, getter );
//	}
//
//	public void clipBounds( int sCnt, float tresh, Object[] vals, int beg, int end, ValueGetter getter )
//	{
//		int     i, n, max, h, hMax;
//		int     vCnt    = end - beg;
//		int[]   histo   = new int[sCnt];
//		float   val, hSum = 0.f;
//
//		Arrays.fill( histo, 0 );
//
//		// Increments histogram bars
//		for ( i = beg; i < end; i ++ )
//		{
//			val = getter.getValue( vals[i] );
//			n   = (int)( sCnt * .9999f * normalize( val ));
//			histo[n] ++;
//		}
//
//		// Finds the maximum histogram bar
//		for ( i = max = hMax = 0; i < sCnt; i ++ )
//		{
//			h = histo[i];
//
//			if ( h > hMax )
//			{
//				max = i;
//				hMax = h;
//			}
//		}
//
//		int     keepCnt = (int)( tresh * vCnt );
//		int     keepMin = (int)( .75f * vCnt )/ sCnt;
//		int     hBeg = max;
//		int     hEnd = max;
//		boolean isBeg = true;
//
//		n = histo[max];
//
//		while ( n < vCnt )
//		{
//			isBeg = hBeg - 1 >= 0 && hEnd + 1 < sCnt ?
//					histo[hBeg-1] > histo[hEnd+1] :
//					hBeg - 1 >= 0;
//
//			h = isBeg ? histo[--hBeg] : histo[++hEnd];
//			n += h;
//
//			if ( n > keepCnt && h < keepMin )
//			{
//				if ( isBeg )    hBeg ++;
//				else            hEnd --;
//				break;
//			}
//		}
//		float   step    = 1.f /( sCnt - 1 );
//
//		EZDebug.print( this.toString() + " -> " );
//		m_min = map( step * hBeg );
//		m_max = map( step * hEnd );
//		EZDebug.println( this.toString());
//	}
//
//	public void clipValues( Object[] vals, ValueGetter getter )
//	{
//		clipValues( vals, 0, vals.length, getter );
//	}
//
//	public void clipValues( Object[] vals, int beg, int end, ValueGetter getter )
//	{
//		int     i;
//		float   val;
//
//		for ( i = beg; i < end; i ++ )
//		{
//			val = getter.getValue( vals[i] );
//			if ( val < m_min )
//			{
//				getter.setValue( vals[i], m_min );
//			}
//			if ( val > m_max )
//			{
//				getter.setValue( vals[i], m_max );
//			}
//		}
//	}

//	public void adjustBounds( int sCnt, float tresh, Object[] vals, ValueGetter getter )
//	{
//		adjustBounds( sCnt, tresh, vals, 0, vals.length, getter );
//	}
//
//	public void remapValues( Object[] vals, ValueGetter getter, float tens )
//	{
//		if ( tens < 1 )
//		{
//			int     i, beg  = 0,
//					end     = vals.length;
//			float   val;
//
//			for ( i = beg; i < end; i ++ )
//			{
//				val = getter.getValue( vals[i] );
//				getter.setValue( vals[i], (float)( 1000 * Math.pow( val, tens )));
//			}
//		}
//	}

//	public void adjustBounds( int sCnt, float tresh, Object[] vals, int beg, int end, ValueGetter getter )
//	{
//		int     i, n, min, max, h, hMax;
//		int     vCnt    = end - beg;
//		int[]   histo   = new int[sCnt];
//		float   val, hSum = 0.f;
//		float   avg, med, sum = 0.f;
//
//		Arrays.fill( histo, 0 );
//
//		// Increments histogram bars
//		for ( i = beg; i < end; i ++ )
//		{
//			val = getter.getValue( vals[i] );
//			sum+= val;
//			n   = (int)( sCnt * .9999f * normalize( val ));
//			histo[n] ++;
//		}
//
//		// eval the average value and check wether this must be adjusted or not
//		avg     = sum /( end - beg );
//		med     = getCenter();
//		float   t = Math.abs(( avg - med )/ avg );
//		if ( t > 1.f )
//		{
//			EZDebug.println( "avg = " + avg + ", med = " + med + ", var = " + t );
//
//			// Finds the average histogram bar
//			max     = (int)( normalize( avg ) * sCnt );
//			hMax    = histo[max];
//
//			int     keepCnt = (int)( tresh * vCnt );
//			int     keepMin = (int)( .75f * vCnt )/ sCnt;
//			int     hBeg = max;
//			int     hEnd = max;
//			boolean isBeg = true;
//
//			n = histo[max];
//
//			while ( n < vCnt )
//			{
//				isBeg = hBeg - 1 >= 0 && hEnd + 1 < sCnt ?
//						histo[hBeg-1] > histo[hEnd+1] :
//						hBeg - 1 >= 0;
//
//				h = isBeg ? histo[--hBeg] : histo[++hEnd];
//				n += h;
//
//				if ( n > keepCnt && h < keepMin )
//				{
//					if ( isBeg )    hBeg ++;
//					else            hEnd --;
//					break;
//				}
//			}
//			float   step    = 1.f /( sCnt - 1 );
//
//			EZDebug.print( toString() + " -> " );
//			m_min = map( step * hBeg );
//			m_max = map( step * hEnd );
//			EZDebug.println( this.toString());
//
//			min = max = 0;
//
//			for ( i = beg; i < end; i ++ )
//			{
//				val = getter.getValue( vals[i] );
//				if ( val < m_min )
//				{
//					getter.setValue( vals[i], m_min );
//					min ++;
//				}
//				if ( val > m_max )
//				{
//					getter.setValue( vals[i], m_max );
//					max ++;
//				}
//			}
//			EZDebug.println( "Clamped vals : " + min + " < avg < " + max + " over " + vCnt );
//		}
//	}
}
package com.socialcomputing.utils.geom;

import java.awt.Point;

import com.socialcomputing.utils.math.Bounds;
import com.socialcomputing.utils.math.EZMath;

public final class Vertex implements Localisable
{
	/**
	 * X coordinate of the 2D vector
	 */
	public float    x;

	/**
	 * Y coordinate of the 2D vector
	 */
	public float    y;

	public Vertex( )
	{
		x = 0.f;
		y = 0.f;
	}

	public Vertex( Vertex v )
	{
		x = v.x;
		y = v.y;
	}

	public Vertex( float x, float y )
	{
		this.x = x;
		this.y = y;
	}

	public Vertex getPos()
	{
		return this;
	}

	public void reset()
	{
		x = y = 0.f;
	}

	public static Vertex createPolar( float r, float a )
	{
		return new Vertex((float)( r * Math.cos( a )), (float)( r * Math.sin( a )));
	}

	public static Vertex create( Vertex U, Vertex V )
	{
		return new Vertex( V.x - U.x, V.y - U.y );
	}

	public void setPolar( float r, float a )
	{
		x	= (float)( r * Math.cos( a ));
		y	= (float)( r * Math.sin( a ));
	}

	/**
	 * Was moveTo
	 */
	public Vertex setLocation( Vertex v )
	{
		x = v.x;
		y = v.y;

		return this;
	}

	public Vertex setLocation( float x, float y )
	{
		this.x = x;
		this.y = y;

		return this;
	}

	public void set( Vertex U, Vertex V )
	{
		x = V.x - U.x;
		y = V.y - U.y;
	}

	public boolean equals( Object obj )
	{
		Vertex  vect = (Vertex)obj;

		return x == vect.x && y == vect.y;
	}

	public Vertex add( Vertex v )
	{
		return new Vertex( x + v.x, y + v.y );
	}

	public void addThis( Vertex v )
	{
		x += v.x;
		y += v.y;
	}

	public void addTo( Vertex v, Vertex to )
	{
		to.x = x + v.x;
		to.y = y + v.y;
	}

//	public static Vertex add( Vertex A, Vertex B )
//	{
//		to.x = A.x + B.x;
//		to.y = A.y + B.y;
//	}

	public Vertex sub( Vertex v )
	{
		return new Vertex( x - v.x, y - v.y);
	}

	public Vertex subThis( Vertex v )
	{
		x -= v.x;
		y -= v.y;

		return this;
	}

	public void subTo( Vertex v, Vertex to )
	{
		to.x = x - v.x;
		to.y = y - v.y;
	}

	public static Vertex center( Vertex A, Vertex B )
	{
		Vertex  center = new Vertex();

		center.x = .5f *( A.x + B.x );
		center.y = .5f *( A.y + B.y );

		return center;
	}

	/**
	 * Length of this
	 */
	public float length()
	{
		return (float)Math.sqrt(( x * x )+( y * y ));
	}

	/**
	 * Length between A and B
	 */
	public static float length( Vertex A, Vertex B )
	{
		float   dx = B.x - A.x;
		float   dy = B.y - A.y;

		return (float)Math.sqrt(( dx * dx )+( dy * dy ));
	}

	/**
	 * Squared length of this
	 */
	public float sqrLength()
	{
		return ( x * x )+( y * y );
	}

	/**
	 * Squared length between A and B
	 */
	public static float sqrLength( Vertex A, Vertex B )
	{
		float   dx = B.x - A.x;
		float   dy = B.y - A.y;

		return ( dx * dx )+( dy * dy );
	}

	/**
	 * Normalize this length to 1
	 */
	public void normalize()
	{
		float   len = 1.f /(float)Math.sqrt(( x * x )+( y * y ));

		x *= len;
		y *= len;
	}

	public static float dotProduct( Vertex U, Vertex V )
	{
		return U.x * V.x + U.y * V.y;
	}

	/**
	 * equivalent to dotProduct( BA, BC )
	 */
	public static float dotProduct( Vertex A, Vertex B, Vertex C )
	{
		return ( A.x - B.x )*( C.x - B.x )+( A.y - B.y )*( C.y - B.y );
	}

	/**
	 * equivalent to determinant in 2D
	 */
	public static float crossProduct( Vertex U, Vertex V )
	{
		return U.x * V.y - U.y * V.x;
	}

	/**
	 * equivalent to crossProduct( BA, BC )
	 */
	public static float crossProduct( Vertex A, Vertex B, Vertex C )
	{
		return ( A.x - B.x )*( C.y - B.y )-( A.y - B.y )*( C.x - B.x );
	}

	/**
	 * Return true if angle (U, V) is Counter-Clockwise
	 */
	public static boolean isCCW( Vertex U, Vertex V )
	{
		return U.x * V.y < U.y * V.x;
//      U = BA, V = BC
//		return (A.x * B.y + A.y * C.x + B.x * C.y) > ( A.x * C.y + A.y * B.x + B.y * C.x );
	}

	public void moveTo( Vertex v )
	{
		setLocation( v );
	}
/*
	public float posLength()
	{
		float   len = ( x * x )+( y * y );

		return len == 0 ? Float.MIN_VALUE : (float)Math.sqrt( len );
	}

	public static boolean isCCW(Vertex A, Vertex B, Vertex C)
	{
		return (A.x * B.y + A.y * C.x + B.x * C.y) > ( A.x * C.y + A.y * B.x + B.y * C.x );
	}

	public static boolean isInCircle(Vertex v, Vertex v1, Vertex v2, Vertex v3)
	{
		double d = v.x * v.x + v.y * v.y;
		double d1 = v1.x * v1.x + v1.y * v1.y;
		double d2 = v2.x * v2.x + v2.y * v2.y;
		double d3 = v3.x * v3.x + v3.y * v3.y;
		double d4 = (v1.y * d2 + d1 * v3.y + v2.y * d3) - v1.y * d3 - d1 * v2.y - d2 * v3.y;
		double d5 = (v1.x * d2 + d1 * v3.x + v2.x * d3) - v1.x * d3 - d1 * v2.x - d2 * v3.x;
		double d6 = (v1.x * v2.y + v1.y * v3.x + v2.x * v3.y) - v1.x * v3.y - v1.y * v2.x - v2.y * v3.x;
		double d7 = (v1.x * v2.y * d3 + v1.y * d2 * v3.x + d1 * v2.x * v3.y) - v1.x * d2 * v3.y - v1.y * v2.x * d3 - d1 * v2.y * v3.x;
		double d8 = ((v.x * d4 - v.y * d5) + d * d6) - d7;
		boolean isCCW = isCCW(v, v1, v2);

		return isCCW && d8 > 0.0D || !isCCW && d8 < 0.0D;
	}
*/
	public static boolean isCCW( Vertex A, Vertex B, Vertex C )
	{
		return (A.x - B.x) * (C.y - B.y) < (A.y - B.y) * (C.x - B.x);
	}

	/**
	 * True if P is in the circle defined by A,B,C
	 */
	public static boolean isInCircle( Vertex A, Vertex B, Vertex C, Vertex P )
	{
		float  APx     = A.x - P.x;
		float  APy     = A.y - P.y;
		float  BPx     = B.x - P.x;
		float  BPy     = B.y - P.y;

		float  ACx     = A.x - C.x;
		float  ACy     = A.y - C.y;
		float  BCx     = B.x - C.x;
		float  BCy     = B.y - C.y;

		float  APxBPx  = APx * BPx;
		float  APxBPy  = APx * BPy;
		float  APyBPx  = APy * BPx;
		float  APyBPy  = APy * BPy;

		float  ACxBCx  = ACx * BCx;
		float  ACxBCy  = ACx * BCy;
		float  ACyBCx  = ACy * BCx;
		float  ACyBCy  = ACy * BCy;

		float  Pdp = APxBPx + APyBPy ;
		float  Cdp = ACxBCx + ACyBCy ;

		if ( Cdp < 0 == APxBPy > APyBPx)
		{
			if ( Pdp < 0 != ACxBCy > ACyBCx)
			{
				return false;
			}
			else
			{
				float  Pln = APxBPx * APxBPx + APxBPy * APxBPy + APyBPx * APyBPx + APyBPy * APyBPy;
				float  Cln = ACxBCx * ACxBCx + ACxBCy * ACxBCy + ACyBCx * ACyBCx + ACyBCy * ACyBCy;

				return Pdp * Pdp * Cln > Cdp * Cdp * Pln;
			}
		}
		else
		{
			if ( Pdp < 0 == ACxBCy > ACyBCx)
			{
				return true;
			}
			else
			{
				float  Pln = APxBPx * APxBPx + APxBPy * APxBPy + APyBPx * APyBPx + APyBPy * APyBPy;
				float  Cln = ACxBCx * ACxBCx + ACxBCy * ACxBCy + ACyBCx * ACyBCx + ACyBCy * ACyBCy;

				return Pdp * Pdp * Cln < Cdp * Cdp * Pln;
			}
		}
	}

	public Vertex scale( float f )
	{
		return new Vertex( x * f, y * f );
	}

	public Vertex scaleThis( float f )
	{
		x *= f;
		y *= f;

		return this;
	}

	public void scaleTo( float f, Vertex to )
	{
		to.x = x * f;
		to.y = y * f;
	}

	public Vertex resize( float f )
	{
		float   size = (float)Math.sqrt( x * x + y * y );

		if ( size != 0 )
		{
			size	= f / size;
			x *= size;
			y *= size;
		}


		return this;
	}

	public Vertex translate( Vertex v )
	{
		x += v.x;
		y += v.y;

		return this;
	}

	public Vertex unTranslate( Vertex v )
	{
		x -= v.x;
		y -= v.y;

		return this;
	}

	public void randomizeThis( float len )
	{
		final float INT_MAX_INV = 1.f / Integer.MAX_VALUE;
		int         xInt        = EZMath.s_rand.nextInt();
		boolean     sgn         = ( xInt & 0x1 )!= 0;

		x = INT_MAX_INV * xInt;
		y = (float)Math.sqrt( 1.f - x * x );
		x *= len;
		y *= sgn ? len : -len;
	}


	public static Vertex randomizeIn( Bounds xBnds, Bounds yBnds )
	{
		return new Vertex( xBnds.randomize(), yBnds.randomize());
	}

	public static Vertex randomize( float len )
	{
		Vertex	v	= new Vertex();

		v.jitter( len );

		return v;
	}

	public void jitter( float len )
	{
		final float INT_MAX_INV = 1.f / Integer.MAX_VALUE;
		int         xInt        = EZMath.s_rand.nextInt();
		boolean     sgn         = ( xInt & 0x1 )== 0;
		float       dx          = INT_MAX_INV * xInt,
					dy          = (float)Math.sqrt( 1.f - dx * dx );

		x += dx * len;
		y += dy *( sgn ? len : -len );
	}

	public void sqrRandThis( float len )
	{
		final float INT_MAX_INV = 1.f / Integer.MAX_VALUE;
		int         xInt    = EZMath.s_rand.nextInt();
		boolean     b1      = ( xInt & 0x1 )== 0,
					b2      = (( xInt >> 1 )& 0x1 )== 0;

		y = INT_MAX_INV * xInt;

		if ( b1 )
		{
			y *= len;
			x = b2 ? len : -len;
		}
		else
		{
			x = y * len;
			y = b2 ? len : -len;
		}
	}

	public boolean isNull()
	{
		return ( x == 0.f )&&( y == 0.f );
	}

	public Point toPoint()
	{
		return new Point((int)x, (int)y );
	}

	public float dist( Vertex v )
	{
		float   dx = x - v.x,
				dy = y - v.y;

		return (float)Math.sqrt( dx * dx + dy * dy );
	}

	public Vertex interp( Vertex f, float q )
	{
		x += q *( f.x - x );
		y += q *( f.y - y );

		return this;
	}

	public static void interpTo( Vertex beg, Vertex end, float q, Vertex to )
	{
		to.x = beg.x + q *( end.x - beg.x );
		to.y = beg.y + q *( end.y - beg.y );
	}

	public static Point interp( Point A, Point B, float q )
	{
		Point  P = new Point();

		P.x = A.x +(int)( q *( B.x - A.x ));
		P.y = A.y +(int)( q *( B.y - A.y ));

		return P;
	}

	public Point project( Bounds fromXBnd, Bounds fromYBnd, Bounds toXBnd, Bounds toYBnd )
	{
		Point  p = new Point();

		p.x = (int)toXBnd.map( fromXBnd.normalize( x ));
		p.y = (int)toYBnd.map( fromYBnd.normalize( y ));

		return p;
	}

	/**
	 * Rotate this by 90° CCW
	 * @return	this vertex after it was turned
	 */
	public Vertex turn( )
	{
		float oldX = x;

		x = -y;
		y = oldX;

		return this;
	}

	public Vertex flip( )
	{
		x = -x;
		y = -y;

		return this;
	}

	public Vertex swapThis( boolean xSwap, boolean ySwap )
	{
		if ( xSwap )	x = -x;
		if ( ySwap )	y = -y;

		return this;
	}

	public String toString()
	{
		return new String( "(" + x + "," + y + ")" );
	}

	public Vertex discretize()
	{
		x = (int)x;
		y = (int)y;

		return this;
	}

	public float getXSlope()
	{
		return x / y;
	}

	public float getYSlope()
	{
		return y / x;
	}

	public static Vertex intersectLineCircle( Vertex P, Vertex C, float r, boolean isNear )
	{
		Vertex  CP      = Vertex.create( C, P );
		float   s, x, y;

		if ( CP.y != 0.f )
		{
			s = CP.getXSlope();
			y = ( CP.y > 0 )== isNear ? r /(float)Math.sqrt( 1.f + s * s ) : -r /(float)Math.sqrt( 1.f + s * s );
			x = s * y;
		}
		else
		{
			s = CP.getYSlope();
			x = ( CP.x > 0 )== isNear ? r /(float)Math.sqrt( 1.f + s * s ) : -r /(float)Math.sqrt( 1.f + s * s );
			y = s * x;
		}

		return new Vertex( C.x + x, C.y + y );
	}

	public static Vertex evalCenter( Localisable[] locations, int beg, int end )
	{
		Vertex  pos,
				center = new Vertex();

		int     i, n = 0;

		for ( i = beg; i <= end; i ++ )
		{
			pos = locations[i].getPos();

			if ( pos != null )
			{
				center.addThis( pos );
				n ++;
			}
		}

		if ( n != 0 )
		{
			center.scaleThis( 1.f / n );
		}

		return center;
	}

	public static void main( String[] argv )
	{
		final   int ITER_CNT = 5000000;
		int     i;
		Vertex  v = new Vertex();

		for ( i = 0; i < ITER_CNT; i ++ )
		{
			Vertex.randomize( 10.f );
			v.randomizeThis( 10.f );
			v.sqrRandThis( 10.f );
		}
	}
}

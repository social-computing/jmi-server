package com.socialcomputing.wps.client.applet;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

/**
 * <p>Title: ShapeX</p>
 * <p>Description: A graphical shape that can be transformed and filled.<br>
 * This shape is defined by the number of Points it holds in POLYGON_VAL:
 * <ul>
 * <li>0 : A ghost shape that is not visible.</li>
 * <li>1 : A disk shape whose radius is defined by the SCALE_VAL.</li>
 * <li>2 : A link shape between two points. Its width is defined by the SCALE_VAL.</li>
 * <li>N : A polygon shape defined by its points. The polygon is scaled by the SCALE_VAL.</li>
 * </ul>
 * </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class ShapeX extends Base implements Serializable
{
	/**
	 * Index of the Points table prop in VContainer table.
	 * It can hold 0,1,2 or more points depending on the shape to display.
	 */
	public      static final int    POLYGON_VAL     = 1;

	/**
	 * Index of the scale prop in VContainer table.
	 * This is the radius, width or scale of the shape, depending on the number of Points in POLYGON_VAL.
	 */
	public      static final int    SCALE_VAL       = 2;

	/**
	 * True if this Shape is a link between exactly its 2 points.
	 * This is now useless because it's always true.
	 */
	public      static final int    CTR_LNK_BIT     = 0x001;

	/**
	 * True if this Shape is a link whose bounds starts at the intersection with the places.
	 * This is useless because the links are drawn under the place now.
	 */
	public      static final int    SEC_LNK_BIT     = 0x002;

	/**
	 * True if this Shape is a link AND its anchor points are tangent to the places.
	 * This is useless because the links are drawn under the place now.
	 */
	public      static final int    TAN_LNK_BIT     = 0x004;

	/**
	 * JDK 1.1 serialVersionUID
	 */
	protected   static final long   serialVersionUID = -3782082884171779124L;

	/**
	 * Eval the position resulting of the transformation of this by transfo.
	 * In fact only the case of a disk (1 point) is handled.
	 * @param zone		BagZone holding the Points table.
	 * @param transfo	A polar transformation.
	 * @return			The Point translation produced by the transfo on this.
	 */
	protected Point transformOut( ActiveZone zone, Transfo transfo )
	{
		if ( isDefined( SCALE_VAL ))    // else it is just a void frame
		{
			Point   p;
			float   scale   = getFloat( SCALE_VAL, zone );
			int     x, y;

			scale   *= transfo.m_pos;
			p       = getCenter( zone );

			x = p.x + (int)( scale * Math.cos( transfo.m_dir ));
			y = p.y + (int)( scale * Math.sin( transfo.m_dir ));

			return new Point( x, y );
		}

		return null;
	}

	/**
	 * Returns the center of this shape.
	 * @param zone	BagZone holding the Points table.
	 * @return		The barycentric center of all points.
	 */
	protected Point getCenter( ActiveZone zone )
	{
		Point[] points  = (Point[])getValue( POLYGON_VAL, zone );
		Point   p, c    = new Point( points[0] );
		int     i, n    = points.length;

		if ( n > 1 )
		{
			for ( i = 1; i < n; i ++ )
			{
				p   = points[i];
				c.x += p.x;
				c.y += p.y;
			}

			c.x /= n;
			c.y /= n;
		}

		return c;
	}

	/**
	 * Return wether a point is inside this shape after it has been transformed.
	 * @param zone		BagZone holding the Points table.
	 * @param transfo	A transformation to scale or translate this shape.
	 * @param center	The center of the shape before the transformation.
	 * @param pos		A point position to test.
	 * @return			True if this contains pos.
	 */
	protected boolean contains( ActiveZone zone, Transfo transfo, Point center, Point pos )
	{
		if ( isDefined( SCALE_VAL ))    // else it is just a void frame
		{
			Point[] points      = (Point[])getValue( POLYGON_VAL, zone );
			Point   p           = getCenter( zone ),
					shapePos    = new Point();
			int     size        = (int)getShapePos( zone, transfo, center, p, shapePos ),
					n           = points.length;

			switch ( n )
			{
				case 1:     // dot      => Place
					int     dx2     = p.x + shapePos.x - pos.x,
							dy2     = p.y + shapePos.y - pos.y;

					return ( dx2 * dx2 )+( dy2 * dy2 )< size * size;

				case 2:     // segment  => Street
				{
					Point   A       = addPnts( points[0], shapePos ),
							B       = addPnts( points[1], shapePos );
					Polygon poly    = getLinkPoly( zone, A, B, size );

					return poly.contains( pos );
				}
			}
		}

		return false;
	}

	/**
	 * Sets this bounds by updating an already created Rectangle.
	 * @param zone		BagZone holding the Points table.
	 * @param transfo	A transformation to scale or translate this shape.
	 * @param center	The center of the shape before the transformation.
	 * @param bounds	A Rectangle to merge with this bounds.
	 */
	protected void setBounds( ActiveZone zone, Transfo transfo, Point center, Rectangle bounds )
	{
		if ( isDefined( SCALE_VAL ))    // else it is just a void frame
		{
			Point[]     points      = (Point[])getValue( POLYGON_VAL, zone );
			Point       p           = getCenter( zone ),
						shapePos    = new Point();
			Rectangle   rect        = null;
			int         n           = points.length,
						size        = (int)getShapePos( zone, transfo, center, p, shapePos );

			switch ( n )
			{
				case 1:     // disk
					int width   = size << 1;

					rect    = new Rectangle
					(
						p.x + shapePos.x - size,
						p.y + shapePos.y - size,
						width,
						width
					);
					break;

				case 2:     // segment
					Point       A       = addPnts( points[0], shapePos ),
								B       = addPnts( points[1], shapePos );

					rect    = getLinkPoly( zone, A, B, size ).getBounds();
					break;
			}

			merge( bounds, rect );
		}
	}

	/**
	 * Draws this shape on a Graphics.
	 * It's position and size is evaluated using a transfo and a center point.
	 * The polygon case is not handled. Only disks (1 point) and links (2 points) are drawn.
	 * @param g			A graphics to draw this in.
	 * @param zone		The zone that holds the properties used by this shape.
	 * @param slice		The slice that use this shape.
	 * @param transfo	A transformation to scale or translate this shape.
	 * @param center	The center of the shape before the transformation.
	 * @throws UnsupportedEncodingException 
	 */
	protected void paint( Graphics gi, ActiveZone supZone, ActiveZone zone, Slice slice, Transfo transfo, Point center ) // throws UnsupportedEncodingException
	{
		if ( isDefined( SCALE_VAL ))    // else it is just a void frame
		{
;
			//ON recup alpha val ? 
			//boolean test = slice.isDefined(slice.ALPHA_VAL);
			//if (test!=false) System.out.print("test "+test+"\n");
			//float alpha = slice.getFloat(slice.ALPHA_VAL, supZone);

			Graphics2D g = (Graphics2D)gi;
			
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
					
			Composite composite ;
			
			//Float alpha = slice.getFloat(prop, props);
			
			Point[] points      = (Point[])getValue( POLYGON_VAL, supZone );
			Point   p           = points[0],
					shapePos    = new Point();
			int     n           = points.length,
					size        = (int)getShapePos( supZone, transfo, center, p, shapePos );

			switch ( n )
			{
				case 1:     // dot      => Place
				{
					composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);					
					g.setComposite(composite);
					
					int     x =     p.x + shapePos.x - size,
							y =     p.y + shapePos.y - size;

					size <<= 1;

					if ( slice.setColor( g, Slice.IN_COL_VAL, zone ))   g.fillOval( x, y, size, size );
					if ( WPSApplet.s_hasGfxInc )                        size --;
					if ( slice.setColor( g, Slice.OUT_COL_VAL, zone ))  g.drawOval( x, y, size, size );
					break;
				}

				case 2:     // segment  => Street
				{
					/*composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f;					
					g.setComposite(composite);*/
					Stroke stroke = g.getStroke();
					g.setStroke(new BasicStroke(size+3));
					
					QuadCurve2D q = new QuadCurve2D.Float();
					
					if ( slice.setColor( g, Slice.OUT_COL_VAL, supZone ))     //g.fillPolygon( poly );
					{
						Point   A       = addPnts( p, shapePos ),
								B       = addPnts( points[1], shapePos );
						//Polygon poly    = getLinkPoly( supZone, A, B, size );
		

						q.setCurve(A.x, A.y, (A.x+B.x)/2, (A.y+B.y)/2 , B.x, B.y);
						g.draw(q);					
					}

					g.setStroke(new BasicStroke(size));
					
					if (slice.setColor( g, Slice.IN_COL_VAL, supZone ))
					{
						g.draw(q);	
					}
					
					g.setStroke(stroke);
					
					//if ( slice.setColor( g, Slice.OUT_COL_VAL, supZone ))    g.drawPolygon( poly );
					break;
				}
			}
		}
	}

	/**
	 * Creates a Polygon corresponding to a Link.
	 * The Link is defined by 2 points and a width.
	 * This methode still handle the cases where the links stops before their ends (SEC_LNK_BIT & TAN_LNK_BIT).
	 * But it's no more usefull as the links are drawn under the places.
	 * @param zone		BagZone holding the Points table.
	 * @param A			A Point of the link.
	 * @param B			The other Point of the link.
	 * @param width		The width in pixels of this link.
	 * @return			A new 4 Points Polygon.
	 */
	private Polygon getLinkPoly( ActiveZone zone, Point A, Point B, int width )
	{
		int         flags   = getFlags( zone );
		LinkZone    link    = (LinkZone)zone;
		BagZone     from    = link.m_from,
					to      = link.m_to;
		int         fromOff = 0,
					toOff   = 0;
		Polygon     poly;

		if ( from != null  && to != null )
		{
			if ( isEnabled( flags, TAN_LNK_BIT | SEC_LNK_BIT ))
			{
				fromOff = ((Float)from.get( "_SCALE" )).intValue();
				toOff   = ((Float)to.get( "_SCALE" )).intValue();
			}
			if ( isEnabled( flags, SEC_LNK_BIT ))
			{
				int w2  = width * width;
				fromOff = (int)(.9 * Math.sqrt( fromOff * fromOff - w2 ));
				toOff   = (int)(.9 * Math.sqrt( toOff * toOff - w2 ));
			}
		}

		poly    = new Polygon();

		Point	N	= new Point( B.x - A.x, B.y - A.y );
		int		len = (int)Math.sqrt( N.x * N.x + N.y * N.y );

		if ( len != 0 )
		{
			N.x = ( N.x << 16 )/ len;
			N.y = ( N.y << 16 )/ len;
			len	= ( len - fromOff - toOff )>> 1;

			Point       C       = scalePnt( N, fromOff + len ),
						U       = scalePnt( N, len ),
						V       = scalePnt( N, width );

			C.translate( A.x, A.y );
			pivotPnt( V );
			addLinkPoint( poly, -1.f, -1.f, C, U, V );
			addLinkPoint( poly, -1.f, 1.f, C, U, V );
			addLinkPoint( poly, 1.f, 1.f, C, U, V );
			addLinkPoint( poly, 1.f, -1.f, C, U, V );
		}
		else
		{
			poly.addPoint( A.x, A.y );
		}

		return poly;
	}

	/**
	 * Adds a new Point to a polygon using UV bilinear coordinates.
	 * The origin and base vectors are given.
	 * This is usefull to draw funny links that can be rotated.
	 * @param poly		The polygon to add a Point to.
	 * @param u			Units in the U vector direction.
	 * @param v			Units in the V vector direction.
	 * @param center	Location of the origin of coordinates.
	 * @param U			Vector U.
	 * @param V			Vector V.
	 */
	private void addLinkPoint( Polygon poly, float u, float v, Point center, Point U, Point V )
	{
		poly.addPoint((int)( center.x + u * U.x + v * V.x ), (int)( center.y + u * U.y + v * V.y ));
	}

	/**
	 * Draws an Image in a shape using a transformation to locate and scale it inside.
	 * Only the disk case (1 point) is handled.
	 * The image is stored in the Env media table if not already.
	 * The next call to draw the same image will simply retrieve it from the table, not the net.
	 * @param applet		The Applet that owns this.
	 * @param g				A graphics to draw on.
	 * @param zone			The zone that holds the properties used by this shape.
	 * @param imageNam		The path of the image to retrieve.
	 * @param transfo		A transformation of this shape to put the image inside.
	 * @param center		This shape center before the transformation.
	 */
	protected void drawImage( WPSApplet applet, Graphics g, ActiveZone zone, String imageNam, Transfo transfo, Point center )
	{
		if ( isDefined( SCALE_VAL ))    // else it is just a void frame
		{
			Hashtable   medias  = applet.m_env.m_medias;
			Image       image   = (Image)medias.get( imageNam ),
						scaledImg;

			if ( image == null )
			{
			    if (imageNam.startsWith("http") || imageNam.startsWith("file")) {
			        // Absolute URL
			        try {
    			        URL url = new URL(imageNam);
    			        image = applet.getImage(url);
			        } catch (MalformedURLException mue) {
			            mue.printStackTrace();
			        }
			    } else {
			        // Relative URL
			        image = applet.getImage(applet.getCodeBase(), imageNam);
			    }
	            applet.prepareImage( image, applet );
	            medias.put( imageNam, image );
			}

			if (( applet.checkImage( image, applet )& ImageObserver.ALLBITS )!= 0 )  // the image can be drawn now
			{
				Point   p           = getCenter( zone ),
						shapePos    = new Point();
				float   scale       = getShapePos( zone, transfo, center, p, shapePos );
				int     x, y,
						imgWid      = image.getWidth( null ),
						w           = imgWid;

				if ( scale > 0.f )    // disk
				{
					w = (int)( 1.414f * scale );
				}

				if ( imgWid != w )
				{
					imageNam    += w;
					scaledImg   = (Image)medias.get( imageNam );

					if ( scaledImg == null )
					{
						scaledImg   = image.getScaledInstance( w, w, Image.SCALE_AREA_AVERAGING );
						applet.prepareImage( scaledImg, applet );
						medias.put( imageNam, scaledImg );
					}

					image   = scaledImg;
				}

				w >>= 1;
				x = p.x + shapePos.x - w;
				y = p.y + shapePos.y - w;

				g.drawImage( image, x, y, applet );
			}
		}
	}

	/**
	 * Evaluate the transformation of a point using a transformation on this shape and return its scale.
	 * @param zone		BagZone holding this props.
	 * @param transfo	A transformation to scale or translate this shape.
	 * @param center	The center of this shape(satellite) before the tranformation.
	 * @param p0		The center of this parent satellite.
	 * @param pos		The location to transform.
	 * @return			The scale of this shape after transformation.
	 */
	private float getShapePos( ActiveZone zone, Transfo transfo, Point center, Point p0, Point pos )
	{
		float   scale   = getFloat( SCALE_VAL, zone );

		if ( center != null )   // we are drawing a real Sat!
		{
			pos.x = center.x - p0.x;
			pos.y = center.y - p0.y;
		}

		if ( transfo != null )
		{
			Point   p   = transfo.getCart();

			pos.x   += p.x;
			pos.y   += p.y;
			scale   *= transfo.m_scl;
		}

		return scale;
	}

	/**
	 * Merge 2 Rectangles.
	 * If the dest Rectangle has one null dimension then copy the source on it.
	 * @param dst	Destination Rectangle that will hold its union with src.
	 * @param src	Source Rectangle.
	 */
	protected static void merge( Rectangle dst, Rectangle src )
	{
		if ( dst.width * dst.height != 0 )
		{
			int xMax    = dst.x + dst.width,
				yMax    = dst.y + dst.height;

			dst.x = Math.min( dst.x, src.x );
			dst.y = Math.min( dst.y, src.y );
			dst.width   = Math.max( xMax, src.x + src.width )- dst.x;
			dst.height  = Math.max( yMax, src.y + src.height )- dst.y;
		}
		else    dst.setBounds( src );
	}

	/**
	 * Sum two vectors.
	 * @param A		A vector.
	 * @param B		Another Vector.
	 * @return		A new Point : A + B
	 */
	protected static Point addPnts( Point A, Point B )
	{
		return new Point( A.x + B.x, A.y + B.y );
	}

	/**
	 * Scales a Point previously normalized to 2^16.
	 * This is usefull to avoid using floats when scaling Vectors.
	 * @param P		A Point already normalized.
	 * @param len	The scale factor.
	 * @return		a new Point that is len x P unnormailzed.
	 */
	protected static Point scalePnt( Point P, int len )
	{
		return new Point(( P.x * len )>> 16, ( P.y * len )>> 16 );
	}

	/**
	 * Rotates a Vector 90� CCW.
	 * Useful to create a 2D ortho basis of vectors.
	 * @param P		A Point to rotate in-place.
	 */
	protected static void pivotPnt( Point P )
	{
		P.x  -= P.y;
		P.y  += P.x;
		P.x  -= P.y;
	}
}
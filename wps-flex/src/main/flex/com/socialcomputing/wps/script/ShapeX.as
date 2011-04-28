package com.socialcomputing.wps.script  {
    import flash.display.Graphics;
    import flash.display.Sprite;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    
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
    public class ShapeX extends Base
    {
        /**
         * Index of the Points table prop in VContainer table.
         * It can hold 0,1,2 or more points depending on the shape to display.
         */
        public const POLYGON_VAL:int= 1;
        
        /**
         * Index of the scale prop in VContainer table.
         * This is the radius, width or scale of the shape, depending on the number of Points in POLYGON_VAL.
         */
        public const SCALE_VAL:int= 2;
        
        /**
         * True if this Shape is a link between exactly its 2 points.
         * This is now useless because it's always true.
         */
        public const CTR_LNK_BIT:int= 0x001;
        
        /**
         * True if this Shape is a link whose bounds starts at the intersection with the places.
         * This is useless because the links are drawn under the place now.
         */
        public const SEC_LNK_BIT:int= 0x002;
        
        /**
         * True if this Shape is a link AND its anchor points are tangent to the places.
         * This is useless because the links are drawn under the place now.
         */
        public const TAN_LNK_BIT:int= 0x004;
        
        /**
         * Eval the position resulting of the transformation of this by transfo.
         * In fact only the case of a disk (1 point) is handled.
         * @param zone		BagZone holding the Points table.
         * @param transfo	A polar transformation.
         * @return			The Point translation produced by the transfo on this.
         */
        protected function transformOut( zone:ActiveZone, transfo:Transfo):Point {
            if ( isDefined( SCALE_VAL ))    // else it is just a void frame
            {
                var p:Point;
                var scale:Number= getFloat( SCALE_VAL, zone );
                var x:int, y;
                
                scale   *= transfo.m_pos;
                p       = getCenter( zone );
                
                x = p.x + int(( scale * Math.cos( transfo.m_dir )));
                y = p.y + int(( scale * Math.sin( transfo.m_dir )));
                
                return new Point( x, y );
            }
            
            return null;
        }
        
        /**
         * Returns the center of this shape.
         * @param zone	BagZone holding the Points table.
         * @return		The barycentric center of all points.
         */
        protected function getCenter( zone:ActiveZone):Point {
            var points:Array = new Array(getValue( POLYGON_VAL, zone ));
            var p:Point, c    = new Point( points[0] );
            var i:int, n    = points.length;
            
            if ( n > 1)
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
        protected function contains( zone:ActiveZone, transfo:Transfo, center:Point, pos:Point):Boolean {
            if ( isDefined( SCALE_VAL ))    // else it is just a void frame
            {
                var points:Array= new Array(getValue( POLYGON_VAL, zone ));
                var p:Point= getCenter( zone ),
                    shapePos    = new Point();
                var size:int= int(getShapePos( zone, transfo, center, p, shapePos )),
                    n           = points.length;
                
                switch ( n )
                {
                    case 1:     // dot      => Place
                        var dx2:int= p.x + shapePos.x - pos.x,
                        dy2     = p.y + shapePos.y - pos.y;
                        
                        return ( dx2 * dx2 )+( dy2 * dy2 )< size * size;
                        
                    case 2:     // segment  => Street
                    {
                        var A:Point= addPnts( points[0], shapePos ),
                            B       = addPnts( points[1], shapePos );
                        var poly:Polygon= getLinkPoly( zone, A, B, size );
                        
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
        protected function setBounds( zone:ActiveZone, transfo:Transfo, center:Point, bounds:Rectangle):void {
            if ( isDefined( SCALE_VAL ))    // else it is just a void frame
            {
                var points:Array= new Array(getValue( POLYGON_VAL, zone ));
                var p:Point= getCenter( zone ),
                    shapePos    = new Point();
                var rect:Sprite= null;
                var n:int= points.length,
                    size        = int(getShapePos( zone, transfo, center, p, shapePos ));
                
                switch ( n )
                {
                    case 1:     // disk
                        var width:int= size << 1;
                        
                        rect    = new Sprite();
                        rect.x = p.x + shapePos.x - size;
                        rect.y = p.y + shapePos.y - size;
                        rect.width = width;
                        rect.height = width;
                        break;
                    
                    case 2:     // segment
                        var A:Point= addPnts( points[0], shapePos ),
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
        protected function paint( gi:Graphics, supZone:ActiveZone, zone:ActiveZone, slice:Slice, transfo:Transfo, center:Point):void // throws UnsupportedEncodingException
        {
            if ( isDefined( SCALE_VAL ))    // else it is just a void frame
            {
                ;
                //ON recup alpha val ? 
                //boolean test = slice.isDefined(slice.ALPHA_VAL);
                //if (test!=false) System.out.print("test "+test+"\n");
                //float alpha = slice.getFloat(slice.ALPHA_VAL, supZone);
                
                var g:Graphics2D= Graphics2D(gi);
                
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
                
                var composite:Composite;
                
                //Float alpha = slice.getFloat(prop, props);
                
                var points:Array= new Array(getValue( POLYGON_VAL, supZone ));
                var p:Point= points[0],
                    shapePos    = new Point();
                var n:int= points.length,
                    size        = int(getShapePos( supZone, transfo, center, p, shapePos ));
                
                switch ( n )
                {
                    case 1:     // dot      => Place
                    {
                        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0);					
                        g.setComposite(composite);
                        
                        var x:int=     p.x + shapePos.x - size,
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
                        var stroke:Stroke= g.getStroke();
                        g.setStroke(new BasicStroke(size+3));
                        
                        var q:QuadCurve2D= new QuadCurve2D.Float();
                        
                        if ( slice.setColor( g, Slice.OUT_COL_VAL, supZone ))     //g.fillPolygon( poly );
                        {
                            var A:Point= addPnts( p, shapePos ),
                                B       = addPnts( points[1], shapePos );
                            //Polygon poly    = getLinkPoly( supZone, A, B, size );
                            
                            
                            q.setCurve(A.x, A.y, (A.x+B.x)/2, (A.y+B.y)/2, B.x, B.y);
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
        /*private function getLinkPoly( zone:ActiveZone, A:Point, B:Point, width:int):Polygon {
            var flags:int= getFlags( zone );
            var link:LinkZone= LinkZone(zone);
            var from:BagZone= link.m_from,
                to      = link.m_to;
            var fromOff:int= 0,
                toOff   = 0;
            var poly:Polygon;
            
            if ( from != null  && to != null )
            {
                if ( isEnabled( flags, TAN_LNK_BIT | SEC_LNK_BIT ))
                {
                    fromOff = (Float(from.get( "_SCALE" ))).intValue();
                    toOff   = (Float(to.get( "_SCALE" ))).intValue();
                }
                if ( isEnabled( flags, SEC_LNK_BIT ))
                {
                    var w2:int= width * width;
                    fromOff = int((.9 * Math.sqrt( fromOff * fromOff - w2 )));
                    toOff   = int((.9 * Math.sqrt( toOff * toOff - w2 )));
                }
            }
            
            poly    = new Polygon();
            
            var N:Point= new Point( B.x - A.x, B.y - A.y );
            var len:int= int(Math.sqrt( N.x * N.x + N.y * N.y ));
            
            if ( len != 0)
            {
                N.x = ( N.x << 16)/ len;
                N.y = ( N.y << 16)/ len;
                len	= ( len - fromOff - toOff )>> 1;
                
                var C:Point= scalePnt( N, fromOff + len ),
                    U       = scalePnt( N, len ),
                    V       = scalePnt( N, width );
                
                C.translate( A.x, A.y );
                pivotPnt( V );
                addLinkPoint( poly, -1., -1., C, U, V );
                addLinkPoint( poly, -1., 1., C, U, V );
                addLinkPoint( poly, 1., 1., C, U, V );
                addLinkPoint( poly, 1., -1., C, U, V );
            }
            else
            {
                poly.addPoint( A.x, A.y );
            }
            
            return poly;
        }*/
        
        // Version sans java.awt.Polygon
        private function getLinkPoly( zone:ActiveZone, A:Point, B:Point, width:int):Sprite {
            var flags:int= getFlags( zone );
            var link:LinkZone= LinkZone(zone);
            var from:BagZone= link.m_from,
                to      = link.m_to;
            var fromOff:int= 0,
                toOff   = 0;
            var poly:Sprite = new Sprite();
            //TODO: mettre le style en parametre
            poly.graphics.lineStyle(2,0x000000);
            
            if ( from != null  && to != null )
            {
                if ( isEnabled( flags, TAN_LNK_BIT | SEC_LNK_BIT ))
                {
                    fromOff = Number(from["_SCALE"]);
                    toOff   = Number(to["_SCALE"]);
                }
                if ( isEnabled( flags, SEC_LNK_BIT ))
                {
                    var w2:int= width * width;
                    fromOff = int((.9 * Math.sqrt( fromOff * fromOff - w2 )));
                    toOff   = int((.9 * Math.sqrt( toOff * toOff - w2 )));
                }
            }
            
            var N:Point= new Point( B.x - A.x, B.y - A.y );
            var len:int= int(Math.sqrt( N.x * N.x + N.y * N.y ));
            
            if ( len != 0)
            {
                N.x = ( N.x << 16)/ len;
                N.y = ( N.y << 16)/ len;
                len	= ( len - fromOff - toOff )>> 1;
                
                var C:Point= scalePnt( N, fromOff + len ),
                    U       = scalePnt( N, len ),
                    V       = scalePnt( N, width );
                
                C.translate( A.x, A.y );
                pivotPnt( V );
                addLinkPoint( poly, -1., -1., C, U, V , true);
                addLinkPoint( poly, -1., 1., C, U, V , false);
                addLinkPoint( poly, 1., 1., C, U, V , false);
                addLinkPoint( poly, 1., -1., C, U, V , false);
            }
            else
            {
                poly.graphics.moveTo( A.x, A.y );
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
        /*private function addLinkPoint( poly:Sprite, u:Number, v:Number, center:Point, U:Point, V:Point):void {
            poly.addPoint(int(( center.x + u * U.x + v * V.x )), int(( center.y + u * U.y + v * V.y )));
        }*/
        private function addLinkPoint( poly:Sprite, u:Number, v:Number, center:Point, U:Point, V:Point, first:Boolean):void {
            if (first)
                poly.graphics.moveTo(Number( center.x + u * U.x + v * V.x ), Number( center.y + u * U.y + v * V.y ));
            else
                poly.graphics.lineTo(Number( center.x + u * U.x + v * V.x ), Number( center.y + u * U.y + v * V.y ));
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
        protected function drawImage(applet:WPSApplet, g:Graphics, zone:ActiveZone, imageNam:String, transfo:Transfo, center:Point):void {
            if ( isDefined( SCALE_VAL ))    // else it is just a void frame
            {
                var medias:Array= applet.m_env.m_medias;
                var image:Image= Image(medias.get( imageNam )),
                    scaledImg;
                
                if ( image == null )
                {
                    image   = applet.getImage( applet.getCodeBase(), imageNam );
                    applet.prepareImage( image, applet );
                    medias.put( imageNam, image );
                }
                
                if (( applet.checkImage( image, applet )& ImageObserver.ALLBITS )!= 0)  // the image can be drawn now
                {
                    var p:Point= getCenter( zone ),
                        shapePos    = new Point();
                    var scale:Number= getShapePos( zone, transfo, center, p, shapePos );
                    var x:int, y,
                    imgWid      = image.getWidth( null ),
                        w           = imgWid;
                    
                    if ( scale > 0.)    // disk
                    {
                        w = int(( 1.414 * scale ));
                    }
                    
                    if ( imgWid != w )
                    {
                        imageNam    += w;
                        scaledImg   = Image(medias.get( imageNam ));
                        
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
        private function getShapePos( zone:ActiveZone, transfo:Transfo, center:Point, p0:Point, pos:Point):Number {
            var scale:Number= getFloat( SCALE_VAL, zone );
            
            if ( center != null )   // we are drawing a real Sat!
            {
                pos.x = center.x - p0.x;
                pos.y = center.y - p0.y;
            }
            
            if ( transfo != null )
            {
                var p:Point= transfo.getCart();
                
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
        protected static function merge( dst:Rectangle, src:Rectangle):void {
            if ( dst.width * dst.height != 0)
            {
                var xMax:int= dst.x + dst.width,
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
        protected static function addPnts( A:Point, B:Point):Point {
            return new Point( A.x + B.x, A.y + B.y );
        }
        
        /**
         * Scales a Point previously normalized to 2^16.
         * This is usefull to avoid using floats when scaling Vectors.
         * @param P		A Point already normalized.
         * @param len	The scale factor.
         * @return		a new Point that is len x P unnormailzed.
         */
        protected static function scalePnt( P:Point, len:int):Point {
            return new Point(( P.x * len )>> 16, ( P.y * len )>> 16);
        }
        
        /**
         * Rotates a Vector 90� CCW.
         * Useful to create a 2D ortho basis of vectors.
         * @param P		A Point to rotate in-place.
         */
        protected static function pivotPnt( P:Point):void {
            P.x  -= P.y;
            P.y  += P.x;
            P.x  -= P.y;
        }
    }
}
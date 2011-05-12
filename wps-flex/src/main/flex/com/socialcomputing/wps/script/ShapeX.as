package com.socialcomputing.wps.script  {
	import com.socialcomputing.wps.components.PlanComponent;
	
	import flash.display.Graphics;
	import flash.display.GraphicsStroke;
	import flash.display.Loader;
	import flash.display.Sprite;
	import flash.geom.ColorTransform;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.net.URLRequest;
	
	import mx.controls.Image;
	
	import org.osmf.layout.PaddingLayoutFacet;
    
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
        public static const POLYGON_VAL:int= 1;
        
        /**
         * Index of the scale prop in VContainer table.
         * This is the radius, width or scale of the shape, depending on the number of Points in POLYGON_VAL.
         */
        public static const SCALE_VAL:int= 2;
        
        /**
         * True if this Shape is a link between exactly its 2 points.
         * This is now useless because it's always true.
         */
        public static const CTR_LNK_BIT:int= 0x001;
        
        /**
         * True if this Shape is a link whose bounds starts at the intersection with the places.
         * This is useless because the links are drawn under the place now.
         */
        public static const SEC_LNK_BIT:int= 0x002;
        
        /**
         * True if this Shape is a link AND its anchor points are tangent to the places.
         * This is useless because the links are drawn under the place now.
         */
        public static const TAN_LNK_BIT:int= 0x004;
        
        /**
         * Eval the position resulting of the transformation of this by transfo.
         * In fact only the case of a disk (1 point) is handled.
         * @param zone		BagZone holding the Points table.
         * @param transfo	A polar transformation.
         * @return			The Point translation produced by the transfo on this.
         */
        public function transformOut( zone:ActiveZone, transfo:Transfo):Point {
            if ( isDefined( SCALE_VAL ))    // else it is just a void frame
            {
                var p:Point;
                var scale:Number= getFloat( SCALE_VAL, zone.m_props );
                var x:int, y:int;
                
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
        public function getCenter( zone:ActiveZone):Point {
            var points:Array = getValue( POLYGON_VAL, zone.m_props ) as Array;
            var p:Point, c:Point    = new Point( points[0].x,  points[0].y);
            var i:int, n:int    = points.length;
            
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
         * Return wether a point is inside this shape after it has been transformed
		 * 
         * @param zone		BagZone holding the Points table. ???? 
         * @param transfo	A transformation to scale or translate this shape.
         * @param center	The center of the shape before the transformation.
         * @param pos		A point position to test.
		 * 
         * @return			True if this contains pos, false otherwise
         */
        public function contains(g:Graphics, zone:ActiveZone, transfo:Transfo, center:Point, pos:Point):Boolean {
			trace("[Shape contains method begin]");
			if(!isDefined(SCALE_VAL)) return false; // it is just a void frame
			
            var points:Array = getValue(POLYGON_VAL, zone.m_props) as Array;
            var shapeCenter:Point   = getCenter(zone),
                shapePosition:Point = new Point();
            var size:Number = this.getShapePos(zone, transfo, center, shapeCenter, shapePosition),
                nbPoint:int = points.length;
            
            switch(nbPoint) {
				// 1 point = circle => Place
				case 1: 
					// Dirty hack : see why this is needed
					size = size * 2;
					trace("  - 1 point situation : circle");
					var distance:Point = shapeCenter.add(shapePosition).subtract(pos);
					trace("  - size = " + size);
					trace("  - (dx = " + distance.x + ", dy = " + distance.y  + ")"); 
					
					// DEBUG
					// Drawing sensitive zone
					/*
					var t:Point = shapeCenter.add(shapePosition);
					g.beginFill(0xFF0000);
					g.drawRect(t.x, t.y, 3, 3);
					g.drawEllipse(t.x - (size/2), t.y - (size/2), size, size );
					g.endFill();
					
					g.lineStyle(1, 0x000000);
					g.beginFill(0x0000FF);
					g.drawRect(shapeCenter.x, shapeCenter.y, 3, 3);
					g.endFill();
					*/
					// DEBUG END
					
					// We check if the position is located inside the circle
					// Another way to express it : is the distance between the circle center and the position < circle (rayon)
					trace("[Shape contains end]");
                    return (distance.x * distance.x) + (distance.y * distance.y) < (size * size);
                    
				// 2 points = segment => Street
                case 2:     
					trace("  - 2 points situation : polygon");
					var fromPoint:Point = (points[0] as Point).add(shapePosition);
                    var toPoint:Point = (points[1] as Point).add(shapePosition);
					var poly:Polygon = getLinkPoly(zone, fromPoint, toPoint, size);
					
					// DEBUG
					// Drawing sensitive zone
					/*
					g.lineStyle(1, 0x000000);
					g.beginFill(0x0000FF);
					g.drawRect(fromPoint.x, fromPoint.y, 3, 3);
					g.drawRect(toPoint.x, toPoint.y, 3, 3);
					g.endFill();
					
					
					g.moveTo(poly.xpoints[0], poly.ypoints[0]);
					g.beginFill(0xFF0000);
					for(var ip:int = 1 ; ip < poly.npoints ; ip++) {
						g.lineTo(poly.xpoints[ip], poly.ypoints[ip]);
					}
					g.endFill();
					*/
					// DEBUG END
					
					trace("[Shape contains end]");
                    return poly.contains2(pos);
				default:
					throw new Error("Should never happen, a shape can only have 1 or 2 points");
            }
        }
        
        /**
         * Sets this bounds by updating an already created Rectangle.
         * @param zone		BagZone holding the Points table.
         * @param transfo	A transformation to scale or translate this shape.
         * @param center	The center of the shape before the transformation.
         * @param bounds	A Rectangle to merge with this bounds.
         */
        public function setBounds( zone:ActiveZone, transfo:Transfo, center:Point, bounds:Rectangle):void {
            if ( isDefined( SCALE_VAL ))    // else it is just a void frame
            {
                var points:Array= getValue( POLYGON_VAL, zone.m_props ) as Array;
                var p:Point= getCenter( zone ),
                    shapePos:Point    = new Point();
                var rect:Rectangle= null;
                var n:int= points.length,
                    size:int= int(getShapePos( zone, transfo, center, p, shapePos ));
                
                switch ( n )
                {
                    case 1:     // disk
                        var width:int= size << 1;
                        
                        rect    = new Rectangle();
                        rect.x = p.x + shapePos.x - size;
                        rect.y = p.y + shapePos.y - size;
                        rect.width = width;
                        rect.height = width;
                        break;
                    
                    case 2:     // segment
                        var A:Point = (points[0] as Point).add(shapePos),
                            B:Point = (points[1] as Point).add(shapePos);
                        rect = getLinkPoly(zone, A, B, size).getBounds();
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
        public function paint(s:Sprite, supZone:ActiveZone, zone:ActiveZone, slice:Slice, transfo:Transfo, center:Point):void {
            trace("[ShapeX paint method called]");
			
			if(isDefined(SCALE_VAL))    // else it is just a void frame
            {
                //ON recup alpha val ? 
                //boolean test = slice.isDefined(slice.ALPHA_VAL);
                //if (test!=false) System.out.print("test "+test+"\n");
                //float alpha = slice.getFloat(slice.ALPHA_VAL, supZone);
                
                /*var g:Graphics2D= Graphics2D(gi);
                
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);                
                
                var composite:Composite;*/
                
                //Float alpha = slice.getFloat(prop, props);
                
                var points:Array = getValue(POLYGON_VAL, supZone.m_props ) as Array;
                var p:Point = points[0] as Point,
                    shapePos:Point = new Point();
                var n:int = points.length, i:int,
                    size:Number = getShapePos( supZone, transfo, center, p, shapePos );
				var color:ColorTransform;
				
				// Manage each case of number of points to draw for this shape
                switch(n) {
                    case 1:     // dot => Place ??
                    {
						trace("Dot shape detected: ");
                        //composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0);
                        //g.setComposite(composite);
                        var x:int = p.x + shapePos.x - size/2,
                            y:int = p.y + shapePos.y - size/2;
                        
						// Doubling size value .... need to find why ... 
                        //size <<= 1;

						color = slice.getColor( Slice.IN_COL_VAL, zone.m_props);
						if(color != null) {
							s.graphics.lineStyle( size, color.color);
						}
						color = slice.getColor( Slice.OUT_COL_VAL, zone.m_props);
						if(color != null) {
                            s.graphics.beginFill(color.color);
						}
                        s.graphics.drawEllipse(x, y, size, size );
                        s.graphics.endFill();
                        break;
                    }
                        
                    case 2:     // segment  => Street
                    {
						trace("Segment shape detected: ");
                        /*composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f;					
                        g.setComposite(composite);*/
						
						// Half size value .... need to find why ... 
						size >>= 1;
						
						var fromPoint:Point = (points[0] as Point).add(shapePos),
							toPoint:Point = (points[1] as Point).add(shapePos);
						var poly:Polygon = getLinkPoly( supZone, fromPoint, toPoint, size/2 );
						
						color = slice.getColor( Slice.OUT_COL_VAL, supZone.m_props);
                        if ( color != null)     
                        {
                            s.graphics.lineStyle( size + 3, color.color);
                            s.graphics.beginFill( color.color);
                            s.graphics.moveTo( poly.xpoints[poly.npoints-1], poly.ypoints[poly.npoints-1]);
                            for( i = 0 ; i < poly.npoints; ++i) {
                                s.graphics.lineTo( poly.xpoints[i], poly.ypoints[i]);
							}
                            s.graphics.endFill();
                        }
                        
						color = slice.getColor( Slice.IN_COL_VAL, supZone.m_props);
                        if ( color != null)
                        {
                            s.graphics.lineStyle( size, color.color);
                            s.graphics.beginFill( color.color);
                            s.graphics.moveTo( poly.xpoints[poly.npoints-1], poly.ypoints[poly.npoints-1]);
							for( i = 0 ; i < poly.npoints; ++i) {
                                s.graphics.lineTo( poly.xpoints[i], poly.ypoints[i]);
							}
                            s.graphics.endFill();
                        }
                        break;
                    }
                }
            }
			trace("[ShapeX paint end]");
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
        private function getLinkPoly( zone:ActiveZone, A:Point, B:Point, width:int):Polygon {
            var flags:int= getFlags( zone.m_props);
            var link:LinkZone= LinkZone(zone);
            var from:BagZone= link.m_from,
                to:BagZone      = link.m_to;
            var fromOff:int= 0,
                toOff:int   = 0;
            var poly:Polygon;
            
            if ( from != null  && to != null )
            {
                if ( isEnabled( flags, TAN_LNK_BIT | SEC_LNK_BIT ))
                {
                    fromOff = Number(from.m_props["_SCALE" ]);
                    toOff   = Number(to.m_props["_SCALE" ]);
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
                
                var C:Point		= scalePnt( N, fromOff + len ),
                    U:Point     = scalePnt( N, len ),
                    V:Point     = scalePnt( N, width );
                
                C.offset( A.x, A.y );
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
        private function addLinkPoint( poly:Polygon, u:Number, v:Number, center:Point, U:Point, V:Point):void {
            poly.addPoint(center.x + u * U.x + v * V.x, center.y + u * U.y + v * V.y);
            /*if (first)
                poly.graphics.moveTo(Number( center.x + u * U.x + v * V.x ), Number( center.y + u * U.y + v * V.y ));
            else
                poly.graphics.lineTo(Number( center.x + u * U.x + v * V.x ), Number( center.y + u * U.y + v * V.y ));*/
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
        public function drawImage(applet:PlanComponent, g:Graphics, zone:ActiveZone, imageNam:String, transfo:Transfo, center:Point):void {
            if ( isDefined( SCALE_VAL ))    // else it is just a void frame
            {
                var medias:Array = applet.env.m_medias;
				var scaledImg:Image;
				// TODO : Fix this : for now applet.env.m_medias is null, not empty
                //var image:Image = medias[imageNam];
				var image:Image = null;
				
                if ( image == null )
                {
					var ldr:Loader = new Loader();
					// TODO applet.getCodeBase() ?????
/*					var urlReq:URLRequest = new URLRequest(imageNam);
					ldr.load(urlReq);
                    image   = applet.getImage( applet.getCodeBase(), imageNam );
                    applet.prepareImage( image, applet );
                   medias.push( imageNam, image);
*/                }
                
                if (false) 
					// TODO 
					//( applet.checkImage( image, applet )& ImageObserver.ALLBITS )!= 0)  // the image can be drawn now
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
                        scaledImg   = Image(medias[ imageNam ]);
                        
                        if ( scaledImg == null )
                        {
                            scaledImg   = image.getScaledInstance( w, w, Image.SCALE_AREA_AVERAGING );
                            applet.prepareImage( scaledImg, applet );
                            medias[imageNam] = scaledImg;
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
		 * 
         * @param zone		BagZone holding this props.
         * @param transfo	A transformation to scale or translate this shape.
         * @param center	The center of this shape(satellite) before the tranformation.
         * @param p0		The center of this parent satellite.
         * @param pos		The location to transform.
         * @return			The scale of this shape after transformation.
         */
        private function getShapePos(zone:ActiveZone, transfo:Transfo, center:Point, p0:Point, pos:Point):Number {
            var scale:Number = getFloat(SCALE_VAL, zone.m_props);
			trace("[GetShapePos, scale = " + scale + "]");
			
			var p:Point;
			
			// We are drawing a real Sat!
            if(center != null) {
				p = center.subtract(p0); 
				pos.x = p.x;
				pos.y = p.y;
            }
            
			
            if(transfo != null){
				p =  pos.add(transfo.getCart());
				pos.x = p.x;
				pos.y = p.y;
                scale *= transfo.m_scl;
				trace("  - transformation scale: " + transfo.m_scl); 
            }
			
			trace("[GetShapePos end, scale = " + scale + "]");
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
                    yMax:int    = dst.y + dst.height;
                
                dst.x = Math.min( dst.x, src.x );
                dst.y = Math.min( dst.y, src.y );
                dst.width   = Math.max( xMax, src.x + src.width )- dst.x;
                dst.height  = Math.max( yMax, src.y + src.height )- dst.y;
            }
            //else    dst.set.setBounds( src );
            else {
				dst.x = src.x;
				dst.y = src.y;
				dst.width = src.width;
				dst.height = src.height;
            }
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
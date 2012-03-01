package com.socialcomputing.wps.script  {
    import com.socialcomputing.wps.components.Map;
    import com.socialcomputing.wps.util.ApplicationUtil;
    import com.socialcomputing.wps.util.LoaderEx;
    import com.socialcomputing.wps.util.URLHelper;
    import com.socialcomputing.wps.util.controls.ImageUtil;
    import com.socialcomputing.wps.util.shapes.RectangleUtil;
    
    import flash.display.Bitmap;
    import flash.display.BitmapData;
    import flash.display.DisplayObject;
    import flash.display.Graphics;
    import flash.display.Loader;
    import flash.display.LoaderInfo;
    import flash.display.MovieClip;
    import flash.display.PixelSnapping;
    import flash.display.Sprite;
    import flash.events.Event;
    import flash.events.IOErrorEvent;
    import flash.geom.ColorTransform;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    import flash.net.URLRequest;
    import flash.utils.getDefinitionByName;
    
    import mx.controls.Image;
    import mx.utils.URLUtil;
    
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
                var scale:Number = getFloat(SCALE_VAL, zone.m_props);
                var x:int;
                var y:int;
                
                scale *= transfo.m_pos;
                p     = getCenter(zone);
                
                x = p.x + Math.round(scale * Math.cos(transfo.m_dir));
                y = p.y + Math.round(scale * Math.sin(transfo.m_dir));
				
                return new Point(x, y);
            }
            
            return null;
        }
        
        /**
         * Returns the center of this shape.
         * @param zone	BagZone holding the Points table.
         * @return		The barycentric center of all points.
         */
        public function getCenter(zone:ActiveZone):Point {
            var points:Array     = getValue(POLYGON_VAL, zone.m_props) as Array;
            var p:Point;
            var c:Point = new Point(points[0].x,  points[0].y);
            var i:int;
            var n:int     = points.length;
            
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
            if(!isDefined(SCALE_VAL)) return false; // it is just a void frame
            
            var points:Array = getValue(POLYGON_VAL, zone.m_props) as Array;
            var shapeCenter:Point   = this.getCenter(zone);
            var shapePosition:Point = new Point();
            var size:Number = Math.round(this.getShapePos(zone, transfo, center, shapeCenter, shapePosition));
            var nbPoint:int = points.length;
            var ret:Boolean;
            
            switch(nbPoint) {
                // 1 point = circle => Place
                case 1: 
                    var distance:Point = shapeCenter.add(shapePosition).subtract(pos);
                    
                    // We check if the position is located inside the circle
                    // Another way to express it : is the distance between the circle center and the position < circle radius
                    ret = (distance.x * distance.x) + (distance.y * distance.y) < (size * size);
                    return ret;
                    
                    // 2 points = segment => Street
                case 2:     
                    var fromPoint:Point = (points[0] as Point).add(shapePosition);
                    var toPoint:Point = (points[1] as Point).add(shapePosition);
                    var poly:Polygon = getLinkPoly(zone, fromPoint, toPoint, size);
                    
                    // DEBUG
                    /*
                    g.lineStyle(1, 0x0000FF);
                    g.moveTo(poly.xpoints[0], poly.ypoints[0]);
                    for(var p:int = 1; p < poly.npoints ; p++) {
                    g.lineTo(poly.xpoints[p], poly.ypoints[p]);
                    } 
                    g.lineTo(poly.xpoints[0], poly.ypoints[0]);
                    */
                    // EN DEBUG
                    
                    ret = poly.contains(pos); 
                    return ret;
                default:
                    throw new Error("Should never happen, a shape can only have 1 or 2 points");
            }
        }
        
        /**
         * Sets this bounds by updating an already created Rectangle.
         * 
         * @param zone		BagZone holding the Points table.
         * @param transfo	A transformation to scale or translate this shape.
         * @param center	The center of the shape before the transformation.
         * @param bounds	A Rectangle to merge with this bounds.
         */
        public function setBounds(g:Graphics, zone:ActiveZone, transfo:Transfo, center:Point, bounds:Rectangle):void {
            // else it is just a void frame
            if (isDefined(SCALE_VAL)) {
                var points:Array = getValue(POLYGON_VAL, zone.m_props) as Array;
                var shapeCenter:Point = getCenter(zone);
                var shapePos:Point = new Point();
                var rect:Rectangle = null;
                var n:int          = points.length;
                var size:Number    = Math.round(getShapePos(zone, transfo, center, shapeCenter, shapePos));
                
                switch (n) {
                    // 1 point = circle => Place
                    case 1:     
                        // var width:int = size << 1;
                        size = size * 2;
                        rect = new Rectangle(shapeCenter.x + shapePos.x - size / 2 ,
                            shapeCenter.y + shapePos.y - size / 2 ,
                            size,
                            size);
                        // DEBUG 
                        // g.lineStyle(1, 0xFF00000);
                        // g.drawRect(rect.x, rect.y, rect.width, rect.height);
                        // END DEBUG
                        break;
                    
                    // 2 points = segment => Street
                    case 2:     
                        var A:Point = (points[0] as Point).add(shapePos);
                        var B:Point = (points[1] as Point).add(shapePos);
                        rect = getLinkPoly(zone, A, B, size).getBounds();
                        
                        // DEBUG
                        // g.lineStyle(1, 0x0000FF);
                        // g.drawRect(rect.x, rect.y,
                        //	       rect.width, rect.height);
                        // END DEBUG
                        break;
                }
                
                RectangleUtil.merge(bounds, rect);
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
            
            // else it is just a void frame
            if(isDefined(SCALE_VAL)) {
                
                // Old Applet instructions
                /*
                //ON recup alpha val ? 
                //boolean test = slice.isDefined(slice.ALPHA_VAL);
                //if (test!=false) System.out.print("test "+test+"\n");
                //float alpha = slice.getFloat(slice.ALPHA_VAL, supZone);
                
                var g:Graphics2D= Graphics2D(gi);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);                
                var composite:Composite;
                //Float alpha = slice.getFloat(prop, props);
                */
                
                var points:Array = getValue(POLYGON_VAL, supZone.m_props) as Array;
                var p:Point = points[0] as Point;
                var shapePos:Point = new Point();
                var n:int = points.length;
                var i:int;
                var size:Number = Math.round(getShapePos(supZone, transfo, center, p, shapePos));
                var color:ColorTransform;
                
                // Manage each case of number of points to draw for this shape
                switch(n) {
                    // dot => Place ??
                    case 1:     
                    {
                        //composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0);
                        //g.setComposite(composite);
                        var x:int = p.x + shapePos.x - size;
                        var y:int = p.y + shapePos.y - size;
                        
                        // Doubling size value : needed because we are using the  
                        // drawEllipse method that needs a height and width from the top,left starting point
                        // which means we have to double the radius value.
                        size = size * 2;
                        
                        color = slice.getColor( Slice.OUT_COL_VAL, zone.m_props);
                        if(color != null) {
                            s.graphics.lineStyle(1, color.color, 1.0, true);
                        }
                        else {
                            // Set an empty line style
                            s.graphics.lineStyle();
                        }
                        
                        color = slice.getColor(Slice.IN_COL_VAL, zone.m_props);
                        if(color != null) {
                            s.graphics.beginFill(color.color);
                        }
                        s.graphics.drawEllipse(x, y, size, size);
                        s.graphics.endFill();
                        break;
                    }
                        
                    // segment  => Street
                    case 2:     
                    {
                        /*composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f;					
                        g.setComposite(composite);*/
                        
						var fromPoint:Point = (points[0] as Point).add(shapePos);
                        var toPoint:Point = (points[1] as Point).add(shapePos);
                        var poly:Polygon = getLinkPoly(supZone, fromPoint, toPoint, (((size + 3) / 2)));
						
						color = slice.getColor(Slice.OUT_COL_VAL, supZone.m_props);
						if (color != null) {
							s.graphics.lineStyle(1, color.color, 1.0, true);
						}
                        color = slice.getColor(Slice.IN_COL_VAL, supZone.m_props);
						if (color != null) s.graphics.beginFill(color.color);
						s.graphics.moveTo( poly.xpoints[poly.npoints-1], poly.ypoints[poly.npoints-1]);
						for( i = 0 ; i < poly.npoints; ++i) {
							s.graphics.lineTo( poly.xpoints[i], poly.ypoints[i]);
						}
						if (color != null) s.graphics.endFill();
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
        private function getLinkPoly( zone:ActiveZone, A:Point, B:Point, width:int):Polygon {
            var flags:int= getFlags( zone.m_props);
            var link:LinkZone = zone as LinkZone;
            var from:BagZone  = link.m_from;
            var to:BagZone    = link.m_to;
            var fromOff:int   = 0;
            var toOff:int     = 0;
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
                    fromOff = Math.round((.9 * Math.sqrt( fromOff * fromOff - w2 )));
                    toOff   = Math.round((.9 * Math.sqrt( toOff * toOff - w2 )));
                }
            }
            
            poly    = new Polygon();
            
            var N:Point= new Point( B.x - A.x, B.y - A.y );
            var len:int= Math.round(Math.sqrt( N.x * N.x + N.y * N.y ));
            
            if ( len != 0)
            {
                N.x = ( N.x << 16)/ len;
                N.y = ( N.y << 16)/ len;
                len	= ( len - fromOff - toOff )>> 1;
                
                var C:Point		= scalePnt( N, fromOff + len );
                var U:Point     = scalePnt( N, len );
                var V:Point     = scalePnt( N, width );
                
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
         * The image is kept in the image bulk loader if not already.
         * The next call to draw the same image will simply retrieve it from the loader, not the net.
		 * 
         * @param s			    A sprite to draw on.
         * @param zone			The zone that holds the properties used by this shape.
         * @param imageNam		The path of the image to retrieve.
         * @param transfo		A transformation of this shape to put the image inside.
         * @param center		This shape center before the transformation.
         */
        public function drawImage(applet:Map, s:Sprite, zone:ActiveZone, imageNam:String, transfo:Transfo, center:Point):void {
			// Else it is just a void frame
			if (isDefined(SCALE_VAL)) {
				var image:Bitmap;
				var imageUrl:String;

				if( imageNam.search( "embedded:") == 0) {
					var classReference:Class = getDefinitionByName( imageNam.substr(9)) as Class;
					var embeded:Object = new classReference();
					if( embeded is Bitmap) {
						image = embeded as Bitmap;
					}
					else { // SWF
						// Ne marche pas....
						var swf:MovieClip = embeded as MovieClip;
						var bd:BitmapData = new BitmapData(swf.height, swf.width);
						bd.draw(swf);
						image = new Bitmap(bd,PixelSnapping.ALWAYS,true);
					}
				}
				else {
					// Check if it is an absolute url starting with http(s) or file scheme
					// Else get ressources from a path relative to the flash application hosting URL
					/*if(URLUtil.isHttpURL(imageNam) || URLHelper.isFileURL(imageNam)) {
						imageUrl = imageNam;
					}
					else {
						imageUrl = URLHelper.getFullURL(ApplicationUtil.getSwfRoot(), imageNam);
					}*/
					imageUrl = imageNam;
					image = applet.env.getMedia(imageNam) as Bitmap;
				}

				// Check if the image has already been loaded
				var env:Env = applet.env;
                if (image == null && !applet.env.getBadMedia(imageNam)) {
					if(env.getLoader(imageUrl) != null) return;
					var loader:LoaderEx = new LoaderEx();
					env.addLoader( imageUrl, loader);
					loader.contentLoaderInfo.addEventListener(Event.COMPLETE, function (e:Event):void {
						loader = env.getLoader( imageUrl);
						if( loader && !loader.stop) {
							if( (e.target as LoaderInfo).content is Bitmap) {
								image = (e.target as LoaderInfo).content as Bitmap;
							}
							else  if( (e.target as LoaderInfo).content is DisplayObject) {
								var swf:DisplayObject = (e.currentTarget as LoaderInfo).content;
								var bd:BitmapData = new BitmapData(swf.height+28, swf.width+28, true, 0x00000000);
								bd.draw(swf);
								image = new Bitmap(bd,PixelSnapping.ALWAYS,true);
							}
							if( image != null) {
								//drawLoadedImage(applet, image, s, zone, imageNam, transfo, center, true);
								env.putMedia(imageNam, image);
							}
						}
						env.removeLoader( imageUrl);
						if( env.m_loaders.length == 0) {
							applet.plan.init();
							applet.renderShape(applet.restDrawingSurface, applet.width, applet.height);
						}
					});
					loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, function (e:Event):void {
						env.removeLoader( imageUrl);
						if( env.m_loaders.length == 0) {
							applet.plan.init();
							applet.renderShape(applet.restDrawingSurface, applet.width, applet.height);
						}
						trace('Load image ' + imageUrl + ' failed');
						applet.env.putBadMedia( imageUrl);
					});
					var fileRequest:URLRequest = new URLRequest( imageUrl);
					loader.load(fileRequest);
                }

				// Draw the image if it has already been loaded
				if( image != null) {
					drawLoadedImage(applet, image, s, zone, imageNam, transfo, center, false);
                }
            }
        }
        
		protected function drawLoadedImage(applet:Map, image:Bitmap, s:Sprite, zone:ActiveZone, imageNam:String, transfo:Transfo, center:Point, render:Boolean):void {
			// Not cloning the bitmapData itself
			var scaledImg:Image;
			var imageClone:Bitmap = new Bitmap(image.bitmapData,PixelSnapping.AUTO,true);
			
			var p:Point        = getCenter(zone);
			var shapePos:Point = new Point();
			var scale:Number   = getShapePos(zone, transfo, center, p, shapePos);
			var imageWidth:int = imageClone.width;
			var imageScale:int = imageWidth;
			
			// Disk
			if (scale > 0.0) {
				imageScale = Math.round(1.414 * scale);
			}
			
			// Rescale image
			if (imageWidth != imageScale) {
				if (scaledImg == null) {
					imageClone.scaleX = imageScale / imageClone.width;
					imageClone.scaleY = imageScale / imageClone.height;
				}
			}
			
			// Upadate image coordinates after rescale
			imageScale >>= 1;
			imageClone.x = p.x + shapePos.x - imageScale;
			imageClone.y = p.y + shapePos.y - imageScale;
			
			ImageUtil.drawBitmap(imageClone, s.graphics);
			if( render) {
				applet.renderShape( s, imageClone.width, imageClone.height, new Point( imageClone.x, imageClone.y));
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
            }
            return scale;
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
         * Rotates a Vector 90°C CCW.
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

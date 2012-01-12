JMI.namespace("script.ShapeX");
/*
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
JMI.script.ShapeX = (function() {
	
	// default constructor
	var ShapeX = function(color) {
	    this._color = color;
	    this._scolor = null;
	};
	
    ShapeX.prototype = {
        constructor : JMI.script.ShapeX,

	/*
	 * Eval the position resulting of the transformation of this by transfo.
	 * In fact only the case of a disk (1 point) is handled.
	 * @param zone		BagZone holding the Points table.
	 * @param transfo	A polar transformation.
	 * @return			The Point translation produced by the transfo on this.
	 */
	transformOut: function( zone, transfo) {
	    if ( isDefined( SCALE_VAL ))    // else it is just a void frame
	    {
	        var p;
	        var scale = getFloat(SCALE_VAL, zone.m_props);
	        var x;
	        var y;
	        
	        scale *= transfo.m_pos;
	        p     = getCenter(zone);
	        
	        x = p.x + Math.round(scale * Math.cos(transfo.m_dir));
	        y = p.y + Math.round(scale * Math.sin(transfo.m_dir));
			
	        return new JMI.script.Point(x, y);
	    }
	    
	    return null;
	},
	
	/*
	 * Returns the center of this shape.
	 * @param zone	BagZone holding the Points table.
	 * @return		The barycentric center of all points.
	 */
	getCenter: function(zone) {
	    var points = getValue(POLYGON_VAL, zone.m_props);
	    var p ;
	    var c = new JMI.script.Point(points[0].x,  points[0].y);
	    var i ;
	    var n = points.length;
	    
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
	},
	
	/*
	 * Return wether a point is inside this shape after it has been transformed
	 * 
	 * @param zone		BagZone holding the Points table. ???? 
	 * @param transfo	A transformation to scale or translate this shape.
	 * @param center	The center of the shape before the transformation.
	 * @param pos		A point position to test.
	 * 
	 * @return			True if this contains pos, false otherwise
	 */
	contains: function(g, zone, transfo, center, pos) {
	    if(!isDefined(SCALE_VAL)) return false; // it is just a void frame
	    
	    var points = getValue(POLYGON_VAL, zone.m_props);
	    var shapeCenter = this.getCenter(zone);
	    var shapePosition = new JMI.script.Point(0,0);
	    var size = Math.round(this.getShapePos(zone, transfo, center, shapeCenter, shapePosition));
	    var nbPoint = points.length;
	    var ret;
	    
	    switch(nbPoint) {
	        // 1 point = circle => Place
	        case 1: 
	            var distance = new JMI.script.Point( shapeCenter.x, shapeCenter.y);
	            distance.add(shapePosition);
	            distance.subtract(pos);
	            
	            // We check if the position is located inside the circle
	            // Another way to express it : is the distance between the circle center and the position < circle radius
	            ret = (distance.x * distance.x) + (distance.y * distance.y) < (size * size);
	            return ret;
	            
	            // 2 points = segment => Street
	        case 2:     
	            var fromPoint = new JMI.script.Point( points[0].x, points[0].y);
	            fromPoint.add(shapePosition);
	            var toPoint = new JMI.script.Point( points[1].x, points[1].y);
	            toPoint.add(shapePosition);
	            var poly = getLinkPoly(zone, fromPoint, toPoint, size);
	            
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
	},
	
	/*
	 * Sets this bounds by updating an already created Rectangle.
	 * 
	 * @param zone		BagZone holding the Points table.
	 * @param transfo	A transformation to scale or translate this shape.
	 * @param center	The center of the shape before the transformation.
	 * @param bounds	A Rectangle to merge with this bounds.
	 */
	setBounds: function(g , zone , transfo , center, bounds ) {
	    // else it is just a void frame
	    if (isDefined(SCALE_VAL)) {
	        var points = getValue(POLYGON_VAL, zone.m_props);
	        var shapeCenter = getCenter(zone);
	        var shapePos = new JMI.script.Point(0, 0);
	        var rect = com.socialcomputing.jmi.script.Rectangle;
	        var n = points.length;
	        var size = Math.round(getShapePos(zone, transfo, center, shapeCenter, shapePos));
	        
	        switch (n) {
	            // 1 point = circle => Place
	            case 1:     
	                // var width:int = size << 1;
	                size = size * 2;
	                rect = new com.socialcomputing.jmi.script.Rectangle(shapeCenter.x + shapePos.x - size / 2 ,
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
	                var A = new JMI.script.Point( points[0].x, points[0].y);
	                A.add(shapePos);
	                var B = new JMI.script.Point( points[1].x, points[1].y);
	                B.add(shapePos);
	                rect = getLinkPoly(zone, A, B, size).getBounds();
	                
	                // DEBUG
	                // g.lineStyle(1, 0x0000FF);
	                // g.drawRect(rect.x, rect.y,
	                //	       rect.width, rect.height);
	                // END DEBUG
	                break;
	        }
	        
	        bounds.merge( rect);
	    }
	},
	
	/*
	 * Draws this shape on a canevas
	 * It's position and size is evaluated using a transfo and a center point.
	 * The polygon case is not handled. Only disks (1 point) and links (2 points) are drawn.
	 * 
	 * @param canevas	A canevas to draw the shape in.
	 * @param zone		The zone that holds the properties used by this shape.
	 * @param slice		The slice that use this shape.
	 * @param transfo	A transformation to apply to this shape.
	 * @param center	The center of the shape before the transformation. 
	 */
	paint: function(canevas, supZone, zone, slice, transfo, center) {
	    
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
	        
	        var points = getValue(POLYGON_VAL, supZone.m_props);
	        var p = points[0];
	        var shapePos = new JMI.script.Point( 0, 0);
	        var n = points.length;
	        var i ;
	        var radius = Math.round(getShapePos(supZone, transfo, center, p, shapePos));
	        var color; //:ColorTransform;
	        
	        // Get canevas drawing context
	        var gDrawingContext = canevas.getContext("2d");
	        
	        // Manage each case of number of points to draw for this shape
	        switch(n) {
	            // dot => Place ??
	            case 1:     
	            {
	                //composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0);
	                //g.setComposite(composite);
	                
	                // Jonathan Dray : I removed the size offset, as drawing a circle on canevas starts at the middle 
	                var x = p.x + shapePos.x;
	                var y = p.y + shapePos.y;
	                
	                // Doubling size value : needed because we are using the  
	                // drawEllipse method that needs a height and width from the top,left starting point
	                // which means we have to double the radius value.
	                // Jonathan Dray : do not double the size anymore, the arc drawing method takes the radius
	                // size = size * 2;
	                
	                color = slice.getColor(Slice.OUT_COL_VAL, zone.m_props);
	
	                /*
	                 * TODO : replace this with canevas line style equivalent 
	                 *
	                if(color != null) {
	                    s.graphics.lineStyle(1, color.color);
	                    
	                }
	                else {
	                    // Set an empty line style
	                    s.graphics.lineStyle();
	                }
	                
	                color = slice.getColor(Slice.IN_COL_VAL, zone.m_props);
	                if(color != null) {
	                    s.graphics.beginFill(color.color);
	                }
	                */
	                gDrawingContext.beginPath();
	                gDrawingContext.arc(x, y, radius, 0, Math.PI * 2, false);
	                gDrawingContext.closePath();
	                gDrawingContext.strokeStyle = color.color;
	                gDrawingContext.stroke();
	                break;
	            }
	                
	            // segment  => Street
	            case 2:     
	            {
	                /*composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f;					
	                g.setComposite(composite);*/
	                
					var fromPoint = new JMI.script.Point( points[0].x, points[0].y);
					fromPoint.add(shapePos);
	                var toPoint = new JMI.script.Point( points[1].x, points[1].y);
	                toPoint.add(shapePos);
	                var poly = getLinkPoly(supZone, fromPoint, toPoint, (((size + 3) / 2)));
					
					color = slice.getColor(Slice.OUT_COL_VAL, supZone.m_props);
					if (color != null) {
						s.graphics.lineStyle(1, color.color);
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
	},
	
	/*
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
	getLinkPoly: function( zone, A, B, width) {
	    var flags = getFlags( zone.m_props);
	    var link = zone;
	    var from = link.m_from;
	    var to = link.m_to;
	    var fromOff = 0;
	    var toOff = 0;
	    var poly = com.socialcomputing.jmi.script.Polygon;
	    
	    if ( from != null  && to != null )
	    {
	        if ( isEnabled( flags, TAN_LNK_BIT | SEC_LNK_BIT ))
	        {
	            fromOff = Number(from.m_props["_SCALE" ]);
	            toOff   = Number(to.m_props["_SCALE" ]);
	        }
	        if ( isEnabled( flags, SEC_LNK_BIT ))
	        {
	            var w2 = width * width;
	            fromOff = Math.round((.9 * Math.sqrt( fromOff * fromOff - w2 )));
	            toOff   = Math.round((.9 * Math.sqrt( toOff * toOff - w2 )));
	        }
	    }
	    
	    poly    = new Polygon();
	    
	    var N = new JMI.script.Point( B.x - A.x, B.y - A.y );
	    var len = Math.round(Math.sqrt( N.x * N.x + N.y * N.y ));
	    
	    if ( len != 0)
	    {
	        N.x = ( N.x << 16)/ len;
	        N.y = ( N.y << 16)/ len;
	        len	= ( len - fromOff - toOff )>> 1;
	        
	        var C 		= scalePnt( N, fromOff + len );
	        var U		= scalePnt( N, len );
	        var V	    = scalePnt( N, width );
	        
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
	},
	
	/*
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
	addLinkPoint: function( poly, u, v, center, U, V) {
	    poly.addPoint(center.x + u * U.x + v * V.x, center.y + u * U.y + v * V.y);
	    /*if (first)
	    poly.graphics.moveTo(Number( center.x + u * U.x + v * V.x ), Number( center.y + u * U.y + v * V.y ));
	    else
	    poly.graphics.lineTo(Number( center.x + u * U.x + v * V.x ), Number( center.y + u * U.y + v * V.y ));*/
	},
	
	/*
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
	drawImage: function(applet, s, zone, imageNam, transfo, center) {
		// Else it is just a void frame
		if (isDefined(SCALE_VAL)) {
			var image;//:Bitmap;
			var imageUrl;//:String;
	
			// Check if it is an absolute url starting with http(s) or file scheme
			// Else get ressources from a path relative to the flash application hosting URL
			if(URLUtil.isHttpURL(imageNam) || URLHelper.isFileURL(imageNam)) {
				imageUrl = imageNam;
			}
			else {
				imageUrl = URLHelper.getFullURL(ApplicationUtil.getSwfRoot(), imageNam);
			}
			
			image = applet.env.getMedia(imageNam); // as Bitmap;
	
			// Check if the image has already been loaded
	        if (image == null) {
	        	image = new Image();
	        	image.addEventListener( 'load', function() {
	        		// TODO complete
	        	}, false);
				image.src = imageUrl;	
				//TODO portage
				/*var loader:LoaderEx = new LoaderEx();
				var env:Env = applet.env;
				env.addLoader( imageUrl, loader);
				loader.contentLoaderInfo.addEventListener(Event.COMPLETE, function (e:Event):void {
					loader = env.getLoader( imageUrl);
					if( loader && !loader.stop) {
						image = Bitmap( LoaderInfo(e.target).content);
						drawLoadedImage(applet, image, s, zone, imageNam, transfo, center, true);
						env.putMedia(imageNam, image);
					}
					env.removeLoader( imageUrl);
				});
				loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, function (e:Event):void {
					trace('Load image ' + imageUrl + ' failed');
				});
				var fileRequest:URLRequest = new URLRequest( imageUrl);
				loader.load(fileRequest);*/
	        }
	
			// Draw the image if it has already been loaded
			else {
				drawLoadedImage(applet, image, s, zone, imageNam, transfo, center, false);
	        }
	    }
	},
	
	drawLoadedImage: function(applet , image, s, zone, imageNam, transfo, center, render) {
		// Not cloning the bitmapData itself
		var scaledImg; //:Image;
		var imageClone; // TODO :Bitmap = new Bitmap(image.bitmapData);
		
		var p				= getCenter(zone);
		var shapePos		= new JMI.script.Point(0, 0);
		var scale 		   = getShapePos(zone, transfo, center, p, shapePos);
		var imageWidth	   = imageClone.width;
		var imageScale	   = imageWidth;
		
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
	},
	
	/*
	 * Evaluate the transformation of a point using a transformation on this shape and return its scale.
	 * 
	 * @param zone		BagZone holding this props.
	 * @param transfo	A transformation to scale or translate this shape.
	 * @param center	The center of this shape(satellite) before the tranformation.
	 * @param p0		The center of this parent satellite.
	 * @param pos		The location to transform.
	 * @return			The scale of this shape after transformation.
	 */
	getShapePos: function(zone, transfo, center, p0, pos) {
	    var scale = getFloat(SCALE_VAL, zone.m_props);
	    
	    var p = JMI.script.Point;
	    
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
    };

	return ShapeX;
}());


/*
 * Index of the Points table prop in VContainer table.
 * It can hold 0,1,2 or more points depending on the shape to display.
 */
JMI.script.ShapeX.POLYGON_VAL = 1;

/*
 * Index of the scale prop in VContainer table.
 * This is the radius, width or scale of the shape, depending on the number of Points in POLYGON_VAL.
 */
JMI.script.ShapeX.SCALE_VAL = 2;

/*
 * True if this Shape is a link between exactly its 2 points.
 * This is now useless because it's always true.
 */
JMI.script.ShapeX.CTR_LNK_BIT = 0x001;

/*
 * True if this Shape is a link whose bounds starts at the intersection with the places.
 * This is useless because the links are drawn under the place now.
 */
JMI.script.ShapeX.SEC_LNK_BIT = 0x002;

/*
 * True if this Shape is a link AND its anchor points are tangent to the places.
 * This is useless because the links are drawn under the place now.
 */
JMI.script.ShapeX.TAN_LNK_BIT = 0x004;

/**
 * Scales a Point previously normalized to 2^16.
 * This is usefull to avoid using floats when scaling Vectors.
 * @param P		A Point already normalized.
 * @param len	The scale factor.
 * @return		a new Point that is len x P unnormailzed.
 */
JMI.script.ShapeX.scalePnt = function( P, len) {
    return new JMI.script.Point(( P.x * len )>> 16, ( P.y * len )>> 16);
}

/**
 * Rotates a Vector 90°C CCW.
 * Useful to create a 2D ortho basis of vectors.
 * @param P		A Point to rotate in-place.
 */
JMI.script.ShapeX.pivotPnt = function( P) {
    P.x  -= P.y;
    P.y  += P.x;
    P.x  -= P.y;
}

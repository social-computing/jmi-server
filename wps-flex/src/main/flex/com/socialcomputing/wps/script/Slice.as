package com.socialcomputing.wps.script  {
	import com.socialcomputing.wps.components.PlanComponent;
	import com.socialcomputing.wps.util.shapes.RectangleUtil;
	
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.geom.ColorTransform;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.text.TextFormat;
	    
    /**
     * <p>Title: Slice</p>
     * <p>Description: An elementary slice to fill with graphics.<br>
     * </p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class Slice extends Base
    {
        /**
         * Index of the Transfo prop in VContainer table.
         * If it doesn't exists this Slice will have the same shape as its satellite.
         */
        public static const TRANSFO_VAL:int= 1;
        
        /**
         * Index of the inside Color prop in VContainer table.
         */
        public static const IN_COL_VAL:int= 2;
        
        /**
         * Index of the border Color prop in VContainer table.
         */
        public static const OUT_COL_VAL:int= 3;
        
        /**
         * Index of the image URL prop in VContainer table.
         */
        public static const IMAGE_VAL:int= 4;
        
        /**
         * Index of the text (HTML or not) prop in VContainer table.
         */
        public static const TEXT_VAL:int= 5;
        
        /**
         * Index of the text (HTML or not) prop in VContainer table.
         */
        public static const ALPHA_VAL:int= 6;
        
        /**
         * Index of the delay (ms) prop for a tooltip Slice.
         */
        public static const DELAY_VAL:int= 7;
        
        /**
         * Index of the length (ms) prop for a tooltip Slice.
         */
        public static const LENGTH_VAL:int= 8;
        
        /**
         * True if this Slice is visible.
         * Probably a useless bit...
         */
        public static const VISIBLE_BIT:int= 0x01;
        
        /**
         * A buffer to store the HTMLText associated with this if it has one.
         */
        [transient]
        private var m_htmlTxt:HTMLText;
        
        /**
         * Draws this slice on a Graphics.
         * It's position and size is evaluated by its parent satellite and transfo.
         * The inner parts (when they exists) are drawn with respect to the following order:
         * <ul>
         * <li>IN_COL_VAL : The background of the shape.</li>
         * <li>OUT_COL_VAL : The outside of the shape.</li>
         * <li>IMAGE_VAL : The bitmap (icon).</li>
         * <li>TEXT_VAL : The text, standard or HTML.</li>
         * </ul>
         * @param applet		The Applet that owns this.
         * @param g				A graphics to draw this in.
         * @param supZone		The parent zone of this zone.
         * @param zone			The zone that holds the properties used by this slice.
         * @param satShp		The shape of this Slice
         * @param satCtr		This slice center.
         * @param supCtr		This parent satellite center.
         * @throws UnsupportedEncodingException 
         */
        public function paint(applet:PlanComponent, s:Sprite, supZone:ActiveZone, zone:ActiveZone, satShp:ShapeX, satCtr:Point, supCtr:Point):void {
            trace("[Slice paint method called]");
			
			var text:HTMLText= getText( TEXT_VAL, zone.m_props );
            
            // Patch for IE old JVM JIT bug (build < 3000).
            /*ON if ( satShp == null )
            {	// Very interesting do-nothing instruction that will never be called!
            try{ Thread.sleep( 0 );} catch ( InterruptedException e ){}
            }*/
            
            var transfo:Transfo= getTransfo( TRANSFO_VAL, zone.m_props );
            
			// Draw a satellite with primitives
            if(isDefined(IN_COL_VAL) || isDefined(OUT_COL_VAL)) {
                satShp.paint( s, supZone, zone, this, transfo, satCtr );
            }
            
			// Draw a satellite's image it is set
			
            if(isDefined(IMAGE_VAL)) {
				//throw new Error("not yet implemented");
                 
				var imageNam:String= parseString(IMAGE_VAL, zone.m_props )[0];
                if (imageNam != null) {
                    satShp.drawImage(applet, s, supZone, imageNam, transfo, satCtr);
                }
				
            }
            
            if(text != null) {
                if ( HTMLText.isEnabled( text.getFlags( zone.m_props ), HTMLText.URL_BIT ))
                {
                    var textUrls:Vector.<String> = text.parseString( HTMLText.TEXT_VAL, zone.m_props );
                    var hLine:String = "";
                    var hTxt:String = "";
                    var i:int;
                    var n:int = textUrls.length;
                    
                    try
                    {
                        for ( i = 0; i < n; i ++ )
                        {
                            /*var istream:InputStream= WPSApplet.getBinaryStream( applet, textUrls[i], true);
                            
                            if ( istream != null )
                            {
                                var reader:BufferedReader= new BufferedReader( new InputStreamReader(istream ));
                                while (( hLine = reader.readLine())!= null )	hTxt += hLine;
                                if ( i < n - 1)    hTxt += "<br>";
                            }
                            
                            istream.close();*/
                        }
                        
                        if ( hTxt.length > 0)
                        {
                            
                            m_htmlTxt = new HTMLText();
                            var white:ColorTransform = new ColorTransform();
                            white.color = 0xFFFFFF;
                            var black:ColorTransform = new ColorTransform();
                            black.color = 0x000000;
                            m_htmlTxt.initValues(white, black, 0, 12, 0, "SansSerif", 0, new Insets( 0, 4, 0, 4));
                            
                            m_htmlTxt.parseText( new TextFormat(), hTxt );
                            m_htmlTxt.drawText( s, applet.size, text.getFlags( zone.m_props )>> 16);//HTMLText.SOUTH_WEST );
                            
                            return;
                        }
                    }
                    catch ( e:Error){}
                    
                    m_htmlTxt = null;
                }
                else
                {
                    supCtr	= supZone.m_restSwh.m_satellites[0].m_shape.getCenter( supZone );
                    var htmlTxt:HTMLText= text.getHText( applet, s, zone, transfo, satCtr, supCtr, text );
                    
                    if ( htmlTxt != null )
                    {
                        htmlTxt.drawText2( s, applet.size);
                        zone.m_datas[text] = htmlTxt;
                    }
                }
            }
			trace("[Slice paint end]");
        }
        
		
        /**
         * Return wether a point is inside this slice
		 * 
         * If there is a border or a background, tests if the point is inside the shape.
         * TODO : Else, if there is a Text or HTMLText, tests if the point is inside the text bounds. 
         * TODO : The image are not considered because of the complexity (retrieving size), but should...
		 * 
         * @param applet		The PlanComponent that owns this.
         * @param g				A graphics to get the FontMetrics used by this.
         * @param supZone		The parent of zone or null if it have none.
         * @param zone			The zone that holds the properties used by this slice.
         * @param satShp		This slice shape, get from its satellite.
         * @param satCtr		This slice center, get from its satellite.
         * @param supCtr		This parent satellite center.
         * @param pos			A point position to test.
		 * 
         * @return				True if the cursor's position is inside this slice, false otherwise
         */
        public function contains(planComponent:PlanComponent, g:Graphics, supZone:ActiveZone, zone:ActiveZone, 
								 satShp:ShapeX, satCtr:Point, supCtr:Point, pos:Point):Boolean {
			var transfo:Transfo = getTransfo(TRANSFO_VAL, zone.m_props);
            
            if(supZone == null)	supZone = zone;
            
            if((isDefined(IN_COL_VAL ) || isDefined(OUT_COL_VAL)) &&
				satShp.contains(g, supZone, transfo, satCtr, pos)) {
                return true;
            }
            
            var text:HTMLText= getText( TEXT_VAL, zone.m_props);
            if ( text != null )
            {
				// TODIO null à remplacer
                var htmlTxt:HTMLText= text.getHText( planComponent, null, zone, transfo, satCtr, supCtr, text);
                return htmlTxt != null ? htmlTxt.m_bounds.contains( pos.x, pos.y ): false;
            }
            return false;
        }
        
		
        /**
         * Sets this bounds by updating an already created Rectangle.
         * If there is a border or a background, the bounds of the shape are considered.
         * If there is a Text or HTMLText, the bounds of the text are considered.
         * The image bounds are not considered because of the complexity (retrieving size), but they should...
         * @param applet		The Applet that owns this.
         * @param g				A graphics to get the FontMetrics used by this.
         * @param supZone		The parent of zone or null if it have none.
         * @param zone			The zone that holds the properties used by this slice.
         * @param satShp		This slice shape, get from its satellite.
         * @param satCtr		This slice center, get from its satellite.
         * @param supCtr		This parent satellite center.
         * @param bounds		A Rectangle to merge with this bounds.
         */
        public function setBounds(applet:PlanComponent, g:Graphics, supZone:ActiveZone, zone:ActiveZone,
								  satShp:ShapeX, satCtr:Point, supCtr:Point, bounds:Rectangle):void {
            var transfo:Transfo = getTransfo(TRANSFO_VAL, zone.m_props);
            
            if (supZone == null) supZone = zone;
            
            try {
                if (isDefined(IN_COL_VAL) || isDefined(OUT_COL_VAL)) {
                    satShp.setBounds(g, supZone, transfo, satCtr, bounds);
                }
            }
            catch (e:Error) {
                var errorMessage:String = "getCenter supZone=" + supZone;
                if ( supZone != null )
                {
                    var points:Array= satShp.getValue( ShapeX.POLYGON_VAL, supZone.m_props ) as Array;
					errorMessage += " zName=" + supZone.m_props[ "NAME" ] + " pKey=" + satShp.m_containers[ShapeX.POLYGON_VAL].m_value + " pnts=" + points + " p[0]=" + points[0];
                }
                throw(new Error(errorMessage));
            }

			var text:HTMLText= getText( TEXT_VAL, zone.m_props);
            if ( text != null )
            {
                if ( HTMLText.isEnabled( text.getFlags( zone.m_props), HTMLText.URL_BIT ))
                {
                    if ( m_htmlTxt != null )
						RectangleUtil.copy( bounds, m_htmlTxt.m_bounds );
                }
                else
                {
                    supCtr	= supZone.m_restSwh.m_satellites[0].m_shape.getCenter( supZone );
                    var htmlTxt:HTMLText;
                    
					// TODO null à remplacer par Sprite
                    htmlTxt = text.getHText( applet, null, zone, transfo, satCtr, supCtr, text);
                    if ( htmlTxt != null )
                    {
                        RectangleUtil.merge( bounds, htmlTxt.m_bounds );
                    }
                    
                }
            }
        }
    }
}
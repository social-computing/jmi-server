package com.socialcomputing.jmi.script  {
	
	import com.socialcomputing.jmi.components.Map;
	import com.socialcomputing.jmi.util.shapes.RectangleUtil;
	
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	    
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
        public function paint(applet:Map, s:Sprite, supZone:ActiveZone, zone:ActiveZone, satShp:ShapeX, satCtr:Point, supCtr:Point):void {
			var text:HTMLText= getText( TEXT_VAL, zone.m_props );
            
            var transfo:Transfo= getTransfo( TRANSFO_VAL, zone.m_props );
            
			// Draw a satellite with primitives
            if(isDefined(IN_COL_VAL) || isDefined(OUT_COL_VAL)) {
                satShp.paint( s, supZone, zone, this, transfo, satCtr );
            }
            
			// Draw a satellite's image it is set
            if(isDefined(IMAGE_VAL)) {
				var imageNam:String = parseString(IMAGE_VAL, zone.m_props )[0];
                if (imageNam != null) {
                    satShp.drawImage(applet, s, supZone, imageNam, transfo, satCtr);
                }
            }
            
            if(text != null) {
                if ( HTMLText.isEnabled( text.getFlags( zone.m_props ), HTMLText.URL_BIT ))
                {
					m_htmlTxt = null;
                    var textUrls:Vector.<String> = text.parseString( HTMLText.TEXT_VAL, zone.m_props );
                    var t:String, hTxt:String = "";
                    var i:int = 0;
					var textLoader:URLLoader = new URLLoader();
					textLoader.addEventListener(Event.COMPLETE, function(e:Event):void {
						t = textLoader.data as String;
						if( t!= null) 
							hTxt = hTxt + t;
						++i;
						if ( hTxt.length > 0 && i == textUrls.length)
						{
							m_htmlTxt = new HTMLText();
							m_htmlTxt.m_text = hTxt;
							m_htmlTxt.init( text, zone);
							m_htmlTxt.updateBounds( applet);
							supCtr	= supZone.m_restSwh.m_satellites[0].m_shape.getCenter( supZone );
							m_htmlTxt.setTextBnds( applet.size, getFlags( zone.m_props), zone.m_flags ,transfo, satCtr, supCtr );
							m_htmlTxt.drawText( s, applet.size, text.getFlags( zone.m_props )>> 16);//HTMLText.SOUTH_WEST );
							applet.renderShape( s, m_htmlTxt.m_bounds.width, m_htmlTxt.m_bounds.height, new Point(m_htmlTxt.m_bounds.x, m_htmlTxt.m_bounds.y));
						}
					});
					for each ( var url:String in textUrls)
					{
						var textReq:URLRequest = new URLRequest(url);
						textLoader.load(textReq);
					}
                }
                else
                {
                    supCtr	= supZone.m_restSwh.m_satellites[0].m_shape.getCenter( supZone );
                    var htmlTxt:HTMLText= text.getHText( applet, s, zone, transfo, satCtr, supCtr, text );
                    
                    if ( htmlTxt != null && htmlTxt.m_text.length > 0)
                    {
                        htmlTxt.drawText2( s, applet.size);
                        zone.m_datas[text] = htmlTxt;
                    }
                }
            }
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
        public function contains(planComponent:Map, g:Graphics, supZone:ActiveZone, zone:ActiveZone, 
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
        public function setBounds(applet:Map, g:Graphics, supZone:ActiveZone, zone:ActiveZone,
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
						bounds.copy( m_htmlTxt.m_bounds );
                }
                else
                {
                    supCtr	= supZone.m_restSwh.m_satellites[0].m_shape.getCenter( supZone );
                    var htmlTxt:HTMLText;
                    
					// TODO null à remplacer par Sprite
                    htmlTxt = text.getHText( applet, null, zone, transfo, satCtr, supCtr, text);
                    if ( htmlTxt != null && htmlTxt.m_text.length > 0)
                    {
                        bounds.merge( htmlTxt.m_bounds );
                    }
                    
                }
            }
        }
    }
}
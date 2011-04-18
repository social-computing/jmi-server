package {
    import java.awt.Color;
    import java.awt.Font;
    import java.awt.Graphics;
    import java.awt.Insets;
    import java.awt.Point;
    import java.awt.Rectangle;
    import java.io.BufferedReader;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.Serializable;
    import java.io.UnsupportedEncodingException;
    
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
        public const TRANSFO_VAL:int= 1;
        
        /**
         * Index of the inside Color prop in VContainer table.
         */
        public const IN_COL_VAL:int= 2;
        
        /**
         * Index of the border Color prop in VContainer table.
         */
        public const OUT_COL_VAL:int= 3;
        
        /**
         * Index of the image URL prop in VContainer table.
         */
        public const IMAGE_VAL:int= 4;
        
        /**
         * Index of the text (HTML or not) prop in VContainer table.
         */
        public const TEXT_VAL:int= 5;
        
        /**
         * Index of the text (HTML or not) prop in VContainer table.
         */
        public const ALPHA_VAL:int= 6;
        
        /**
         * Index of the delay (ms) prop for a tooltip Slice.
         */
        public const DELAY_VAL:int= 7;
        
        /**
         * Index of the length (ms) prop for a tooltip Slice.
         */
        public const LENGTH_VAL:int= 8;
        
        /**
         * True if this Slice is visible.
         * Probably a useless bit...
         */
        public const VISIBLE_BIT:int= 0x01;
        
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
        protected synchronized function paint( applet:WPSApplet, g:Graphics, supZone:ActiveZone, zone:ActiveZone, satShp:ShapeX, satCtr:Point, supCtr:Point):void {
            var text:HTMLText= getText( TEXT_VAL, zone );
            
            // Patch for IE old JVM JIT bug (build < 3000).
            /*ON if ( satShp == null )
            {	// Very interesting do-nothing instruction that will never be called!
            try{ Thread.sleep( 0 );} catch ( InterruptedException e ){}
            }*/
            
            var transfo:Transfo= getTransfo( TRANSFO_VAL, zone );
            
            if ( isDefined( IN_COL_VAL ) || isDefined( OUT_COL_VAL ))
            {
                satShp.paint( g, supZone, zone, this, transfo, satCtr );
            }
            
            if ( isDefined( IMAGE_VAL ))
            {
                var imageNam:String= parseString( IMAGE_VAL, zone )[0];
                
                if ( imageNam != null )
                {
                    satShp.drawImage( applet, g, supZone, imageNam, transfo, satCtr );
                }
            }
            
            if ( text != null )
            {
                if ( HTMLText.isEnabled( text.getFlags( zone ), HTMLText.URL_BIT ))
                {
                    var textUrls:Array= text.parseString( HTMLText.TEXT_VAL, zone );
                    var hLine:String, hTxt	= "";
                    var i:int, n        = textUrls.length;
                    
                    try
                    {
                        for ( i = 0; i < n; i ++ )
                        {
                            var istream:InputStream= WPSApplet.getBinaryStream( applet, textUrls[i], true);
                            
                            if ( istream != null )
                            {
                                var reader:BufferedReader= new BufferedReader( new InputStreamReader(istream ));
                                while (( hLine = reader.readLine())!= null )	hTxt += hLine;
                                if ( i < n - 1)    hTxt += "<br>";
                            }
                            
                            istream.close();
                        }
                        
                        if ( hTxt.length()> 0)
                        {
                            m_htmlTxt = new HTMLText( Color.white, Color.black, 0, 12, Font.PLAIN, "SansSerif", 0, new Insets( 0, 4, 0, 4));
                            
                            m_htmlTxt.parseText( g, hTxt );
                            m_htmlTxt.drawText( g, applet.getSize(), text.getFlags( zone )>> 16);//HTMLText.SOUTH_WEST );
                            return;
                        }
                    }
                    catch ( e:Exception){}
                    
                    m_htmlTxt = null;
                }
                else
                {
                    var key:Long= getKey( text.hashCode());
                    
                    supCtr	= supZone.m_restSwh.m_satellites[0].m_shape.getCenter( supZone );
                    var htmlTxt:HTMLText= text.getHText( applet, g, zone, transfo, satCtr, supCtr, key );
                    
                    if ( htmlTxt != null )
                    {
                        htmlTxt.drawText( g, applet.getSize());
                        zone.m_datas.put( key, htmlTxt );
                    }
                }
            }
        }
        
        /**
         * Return wether a point is inside this.
         * If there is a border or a background, tests if the point is inside the shape.
         * Else, if there is a Text or HTMLText, tests if the point is inside the text bounds.
         * The image are not considered because of the complexity (retrieving size), but should...
         * @param applet		The Applet that owns this.
         * @param g				A graphics to get the FontMetrics used by this.
         * @param supZone		The parent of zone or null if it have none.
         * @param zone			The zone that holds the properties used by this slice.
         * @param satShp		This slice shape, get from its satellite.
         * @param satCtr		This slice center, get from its satellite.
         * @param supCtr		This parent satellite center.
         * @param pos			A point position to test.
         * @return				True if this contains pos.
         * @throws UnsupportedEncodingException 
         */
        protected function contains( applet:WPSApplet, g:Graphics, supZone:ActiveZone, zone:ActiveZone, satShp:ShapeX, satCtr:Point, supCtr:Point, pos:Point):Boolean {
            var transfo:Transfo= getTransfo( TRANSFO_VAL, zone );
            
            if ( supZone == null )  supZone = zone;
            
            if (( isDefined( IN_COL_VAL ) || isDefined( OUT_COL_VAL ))&& satShp.contains( supZone, transfo, satCtr, pos ))
            {
                return true;
            }
            
            var text:HTMLText= getText( TEXT_VAL, zone );
            
            
            if ( text != null )
            {
                var htmlTxt:HTMLText= text.getHText( applet, g, zone, transfo, satCtr, supCtr, getKey( text.hashCode()));
                
                return htmlTxt != null ? htmlTxt.m_bounds.contains( pos ): false;
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
         * @throws UnsupportedEncodingException 
         */
        protected function setBounds( applet:WPSApplet, g:Graphics, supZone:ActiveZone, zone:ActiveZone, satShp:ShapeX, satCtr:Point, supCtr:Point, bounds:Rectangle):void {
            var transfo:Transfo= getTransfo( TRANSFO_VAL, zone );
            
            if ( supZone == null )  supZone     = zone;
            
            try
            {
                if ( isDefined( IN_COL_VAL ) || isDefined( OUT_COL_VAL ))
                {
                    satShp.setBounds( supZone, transfo, satCtr, bounds );
                }
            }
            catch ( e:NullPointerException)
            {
                applet.m_error = "getCenter supZone=" + supZone;
                if ( supZone != null )
                {
                    var points:Array= satShp.getValue( ShapeX.POLYGON_VAL, supZone );
                    applet.m_error += " zName=" + supZone.get( "NAME" ) + " pKey=" + satShp.m_containers[ShapeX.POLYGON_VAL].m_value + " pnts=" + points + " p[0]=" + points[0];
                }
                
                throw ( new RuntimeException( e.getMessage()));
            }
            
            var text:HTMLText= getText( TEXT_VAL, zone );
            
            if ( text != null )
            {
                if ( HTMLText.isEnabled( text.getFlags( zone ), HTMLText.URL_BIT ))
                {
                    if ( m_htmlTxt != null )
                        bounds.setBounds( m_htmlTxt.m_bounds );
                }
                else
                {
                    supCtr	= supZone.m_restSwh.m_satellites[0].m_shape.getCenter( supZone );
                    var htmlTxt:HTMLText;
                    
                    htmlTxt = text.getHText( applet, g, zone, transfo, satCtr, supCtr, getKey( text.hashCode()));
                    if ( htmlTxt != null )
                    {
                        ShapeX.merge( bounds, htmlTxt.m_bounds );
                    }
                    
                }
            }
        }
        
        /**
         * Return a unique identifier for a sub part of a Slice.
         * Use a combined hashcode of this Slice and the sub part.
         * @param hashcode	A unique ID for the sub part.
         * @return	An ID that is a unique combination of the txo hashcodes.
         */
        private function getKey( hashcode:Number):Long {
            return new Long( hashCode()+( hashcode << 32));
        }
    }
}
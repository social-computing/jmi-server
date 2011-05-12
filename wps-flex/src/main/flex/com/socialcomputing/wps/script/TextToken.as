package com.socialcomputing.wps.script {
    import flash.display.BitmapData;
    import flash.display.Graphics;
    import flash.display.Sprite;
    import flash.geom.ColorTransform;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    import flash.text.Font;
    import flash.text.TextField;
    import flash.text.TextFormat;
    

    /**
     * <p>Title: TextToken</p>
     * <p>Description: A piece of text that can be changed to simulate HTML rendering.<br>
     * To achieve this, it can be located, have a foreground and background color and a font.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class TextToken
    {
        /**
         * Text to write.
         */
        public var m_text:String;
        
        /**
         * Bounding box of this text.
         * This is used to locate the text and to draw it's background color if it has.
         */
        public var m_bounds:Rectangle;
        
        /**
         * A Font object describing the size, style and name of the typeFace or null to use the current one.
         */
        public var m_font:TextFormat;
        
        /**
         * The color of the text or null to use the current one.
         */
        public var m_color:ColorTransform;
        
        /**
         * The color of the backgroud or null if there is none.
         */
        public var m_bkCol:ColorTransform;
        
        /**
         * Paint this at a specified location.
         * The inner location is used to offset the fonts.
         * @param g		The graphics to draw in.
         * @param pos	The position where this should be drawn before its internal translation is added.
         */
        public function paint( s:Sprite, pos:Point):void {
            var x:int= m_bounds.x + pos.x,
                y:int = m_bounds.y + pos.y;
            
            if ( m_bkCol != null )
            {
                //g.setColor( m_bkCol );
                //g.fillRect( x, y - g.getFontMetrics().getAscent(), m_bounds.width, m_bounds.height );
                s.graphics.beginFill( m_bkCol.color );
                s.graphics.drawRect(x, y , m_bounds.width, m_bounds.height);
                s.graphics.endFill();    
            }
            
            /*if ( m_color != null )  g.beginFill( m_color );
            if ( m_font != null )   g.setFont( m_font );
            g.drawString( m_text, x, y );*/
            var text:TextField = new TextField();
            text.text = m_text;
            text.x = x;
            text.y = y;
            var formatter:TextFormat = new TextFormat();
            if ( m_color != null ) formatter.color = m_color.color; 
            if ( m_font != null ) formatter.font = m_font.font;
            text.setTextFormat(formatter);
            s.addChild(text);
        }
    }
}
/**
 * <p>Title: TextToken</p>
 * <p>Description: A piece of text that can be changed to simulate HTML rendering.<br>
 * To achieve this, it can be located, have a foreground and background color and a font.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * 
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.namespace("com.socialcomputing.wps.script.TextToken") = (function() {

    /**
     * Text to write.
     */
    // :String 
    var m_text,
    
        /**
         * Bounding box of this text.
         * This is used to locate the text and to draw it's background color if it has.
         */
        // :Rectangle
        m_bounds,
    
        /**
         * A Font object describing the size, style and name of the typeFace or null to use the current one.
         */
        //:TextFormat
        m_font,
        
        /**
         * The color of the text or null to use the current one.
         */
        //:ColorTransform
        m_color,
        
        /**
         * The color of the backgroud or null if there is none.
         */
        //:ColorTransform
        m_bkCol;
    
    /**
     * Paint this at a specified location.
     * The inner location is used to offset the fonts.
     * 
     * @param s		The sprite to draw in. //:Sprite
     * @param pos	The position where this should be drawn before its internal translation is added. //:Point
     * @param blur  a blur value //:int
     * @return //:void
     */
    this.prototype.paint = function(s, pos, blur) {
        // x:int, y:int
        var x = m_bounds.x + pos.x,
            y = m_bounds.y + pos.y;
        
        // TODO : portage, find a javascript equivalent to TextField
        // :TextField
		var text = new TextField();
		if(blur != -1) {
			text.filters = [new BlurFilter(blur, blur)];
		}
        if (m_bkCol != null) {
            //g.setColor( m_bkCol );
            //g.fillRect( x, y - g.getFontMetrics().getAscent(), m_bounds.width, m_bounds.height );
			text.background = true;
			text.backgroundColor = m_bkCol.color;
        }
        
		/*g.beginFill( m_color );
        if ( m_font != null )   g.setFont( m_font );
        g.drawString( m_text, x, y );*/
        text.text = m_text;
        text.x = x;
        text.y = y - m_bounds.y;
		if (m_color != null) {
			m_font.color = m_color.color;
		}
		text.setTextFormat( m_font);
		text.autoSize = TextFieldAutoSize.LEFT;
        text.antiAliasType = AntiAliasType.ADVANCED;
		text.border = false;
		// TODO : portage, actionscript specific, no sprite object in javascript 
        s.addChild(text);
    };
    
    // Public API
    return  {
        paint: paint
    };
}());
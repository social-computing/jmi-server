JMI.namespace("script.TextToken");

/**
 * <p>Title: TextToken</p>
 * <p>Description: A piece of text that can be changed to simulate HTML rendering.<br>
 * To achieve this, it can be located, have a foreground and background color and a font.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * 
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.TextToken = (function() {

    var TextToken = function () {
        /**
         * Text to write.
         */
        // :String 
        this.text = null;
    
        /**
         * Bounding box of this text.
         * This is used to locate the text and to draw it's background color if it has.
         */
        // JMI.script.Rectangle,
        this.bounds = null;
    
        /**
         * A Font object describing the size, style and name of the typeFace or null to use the current one.
         */
        //:TextFormat
        this.font = null;
        
        /**
         * The color of the text or null to use the current one.
         */
        //:ColorTransform
        this.color = null;
        
        /**
         * The color of the backgroud or null if there is none.
         */
        //:ColorTransform
        this.bkCol = null;
    };

    TextToken.prototype = {
        constructor: JMI.script.TextToken,
        /**
         * Paint this at a specified location.
         * The inner location is used to offset the fonts.
         * 
         * @param s     The sprite to draw in. //:Sprite
         * @param pos   The position where this should be drawn before its internal translation is added. //:Point
         * @param blur  a blur value //:int
         * @return //:void
         */
        paint: function(s, pos, blur) {
            // x:int, y:int
            var x = this.bounds.x + pos.x,
                y = this.bounds.y + pos.y;
            
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
                text.backgroundColor = this.bkCol.color;
            }
            
            /*g.beginFill( m_color );
            if ( m_font != null )   g.setFont( m_font );
            g.drawString( m_text, x, y );*/
            text.text = this.text;
            text.x = x;
            text.y = y - this.bounds.y;
            if (this.color != null) {
                this.font.color = this.color.color;
            }
            text.setTextFormat(this.font);
            text.autoSize = TextFieldAutoSize.LEFT;
            text.antiAliasType = AntiAliasType.ADVANCED;
            text.border = false;
            // TODO : portage, actionscript specific, no sprite object in javascript 
            s.addChild(text);
        }
        
    };
    
    return TextToken;
}());

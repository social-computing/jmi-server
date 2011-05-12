package com.socialcomputing.wps.script{
    import com.socialcomputing.wps.components.PlanComponent;
    
    import flash.display.GradientType;
    import flash.display.Graphics;
    import flash.display.Sprite;
    import flash.geom.ColorTransform;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    import flash.text.Font;
    import flash.text.FontStyle;
    import flash.text.TextFormat;
    import flash.text.engine.ElementFormat;
    import flash.text.engine.FontDescription;
    import flash.text.engine.FontMetrics;
	
    /**
     * <p>Title: HTMLText</p>
     * <p>Description: A piece of text that can be single or multilined and hold basic HTML-like tags.<br>
     * The supported tags are:
     * <ul>
     * <li>&lt;c|k=#RRGGBB&gt;	Sets the text typeface/background color.</li>
     * <li>&lt;p t=px l=px b=px r=px a=l|c|r&gt; Starts a new paragraph with a margin at each borders.
     * 						t, l, b, r are optional<br>
     * 						a is the text alignment in this paragraph. Default is left.</li>
     * <li>&lt;b> &lt;i&gt;	Sets font style to BOLD or ITALIC.</li>
     * <li>&lt;br&gt;			Jumps to a new line. There's no need to close this Tag.</li>
     * <li>&lt;f=name&gt;		Sets the font familly name.</li>
     * <li>&lt;s=size&gt;		Sets the font size.</li>
     * </ul>
     * </p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class HTMLText extends Base
    {
        /**
         * Index of the bit flag prop in VContainer table
         */
        //	public  static final int    FLAGS_VAL           = 0;
        
        /**
         * Index of the font prop in VContainer table
         */
        public static const FONT_VAL:int= 1;
        
        /**
         * Index of the text prop in VContainer table
         */
        public static const TEXT_VAL:int= 2;
        
        /**
         * Index of the inside Color prop in VContainer table
         */
        public static const IN_COL_VAL:int= 3;
        
        /**
         * Index of the border Color prop in VContainer table
         */
        public static const OUT_COL_VAL:int= 4;
        
        /**
         * Index of the text Color prop in VContainer table
         */
        public static const TEXT_COL_VAL:int= 5;
        
        /**
         * True if this text is anchored by a corner.(like subZones tips).
         */
        public static const CORNER_BIT:int= 0x0100;	// Be carefull with this flags, they must not override Fonts ones (0x1, 0x2)!
        
        /**
         * True if this text is right aligned (multiline).
         */
        public static const RIGHT_BIT:int= 0x0200;
        
        /**
         * True if this text is centered (multiline).
         */
        public static const CENTER_BIT:int= 0x0400;
        
        /**
         * True if this text is floating inside the window (tooltip).
         */
        public static const FLOAT_BIT:int= 0x0800;
        
        /**
         * True if this text is read from an URL.
         */
        public static const URL_BIT:int= 0x1000;
        
        /**
         * Cardinal orientation of the Tip on the screen        X x Y y
         */
        public static const NORTH:int= 0x4;  // 0 1 0 0;
        public static const NORTH_EAST:int= 0x8;  // 1 0 0 0;
        public static const EAST:int= 0x9;  // 1 0 0 1;
        public static const SOUTH_EAST:int= 0xA;  // 1 0 1 0;
        public static const SOUTH:int= 0x6;  // 0 1 1 0;
        public static const SOUTH_WEST:int= 0x2;  // 0 0 1 0;
        public static const WEST:int= 0x1;  // 0 0 0 1;
        public static const NORTH_WEST:int= 0x0;  // 0 0 0 0;

		public static const BOLD:int= 0x1;  // 0 1 0 0;
		public static const ITALIC:int= 0x2;  // 0 1 0 0;
	
        /**
         * This bounding box, stored to avoid CPU overhead.
         */
        [transient]
        public var m_bounds:Rectangle;
        
        [transient]
        private var _m_heap:Vector.<String>;

        /**
         * Heap of tags to manage the opening and closing of tags.
         */
        public function set m_heap(value:Vector.<String>):void
        {
            _m_heap = value;
        }
        
        [transient]
        private var _m_tokens:Vector;

        /**
         * List of graphical instructions (tokens) to draw this.
         */
        public function set m_tokens(value:Vector):void
        {
            _m_tokens = value;
        }

        [transient]
        private var _m_inCol:ColorTransform;

        public function get m_inCol():ColorTransform
        {
            return _m_inCol;
        }


        /**
         * Color of the bounding box background.
         */
        public function set m_inCol(value:ColorTransform):void
        {
            _m_inCol = value;
        }

        [transient]
        private var _m_outCol:ColorTransform;

        public function get m_outCol():ColorTransform
        {
            return _m_outCol;
        }


        /**
         * Color of the bounding box border.
         */
        public function set m_outCol(value:ColorTransform):void
        {
            _m_outCol = value;
        }

        [transient]
        private var _m_color:int;

        public function get m_color():int
        {
            return _m_color;
        }


        /**
         * Color of the text.
         */
        public function set m_color(value:int):void
        {
            _m_color = value;
        }

        [transient]
        private var _m_bkCol:int;

        public function get m_bkCol():int
        {
            return _m_bkCol;
        }


        /**
         * Color of the text background.
         */
        public function set m_bkCol(value:int):void
        {
            _m_bkCol = value;
        }

        [transient]
        private var _m_size:int;

        public function get m_size():int
        {
            return _m_size;
        }


        /**
         * Size of the typeface.
         */
        public function set m_size(value:int):void
        {
            _m_size = value;
        }

        [transient]
        private var _m_style:int;

        public function get m_style():int
        {
            return _m_style;
        }


        /**
         * Style of the text (bold, italic,...)
         */
        public function set m_style(value:int):void
        {
            _m_style = value;
        }

        [transient]
        private var _m_name:String;

        public function get m_name():String
        {
            return _m_name;
        }


        /**
         * Name of the font.
         */
        public function set m_name(value:String):void
        {
            _m_name = value;
        }

        [transient]
        private var _m_wCur:int;

        /**
         * Width of the current line of text while processing it.
         */
        public function set m_wCur(value:int):void
        {
            _m_wCur = value;
        }

        [transient]
        private var _m_body:FormatToken;

        /**
         * Format of the whole text (HTML body). This is also the default format for new tokens.
         */
        public function set m_body(value:FormatToken):void
        {
            _m_body = value;
        }

        
        /**
         * Format of the current Token.
         */
        [transient]
        private var m_curTok:FormatToken;
        
        /**
         * Fake default constructor. It's called by Server Swatchs.
         */
        public function HTMLText(){}
        
        /**
         * Creates a new HTMLText and sets its default formatting properties.
         * @param inCol		Color of the bounding box background.
         * @param outCol	Color of the bounding box border.
         * @param textCol	Default text color.
         * @param fontSiz	Default font size.
         * @param fontStl	Default font style.
         * @param fontNam	Default font name.
         * @param flags		Default text alignment flags.
         * @param margin	Default margins size.
         */
        public function initValues( inCol:ColorTransform, outCol:ColorTransform, textCol:int, fontSiz:int, fontStl:int, fontNam:String, flags:int, margin:Insets):void
        {
            m_body          = new FormatToken();
            m_body.m_flags  = flags;
            m_body.m_margin = margin;
            
            m_inCol     = inCol;
            m_outCol    = outCol;
            m_color     = textCol;
            m_bkCol     = -1;
            m_style     = fontStl;
            m_size      = fontSiz;
            m_name      = fontNam;
            m_wCur      = 0;
            m_tokens    = new Vector();
            m_heap      = new Vector.<String>();
            m_heap.push( "c=#" + uint2hex( textCol ));
            m_heap.push( "s=" + fontSiz );
            m_heap.push( "f=" + fontNam );
            
            if (( fontStl & BOLD )!= 0)   m_heap.push( "b" );
            if (( fontStl & ITALIC )!= 0) m_heap.push( "i" );
        }
        
        /**
         * Gets a new or existing HTMLText and initialize its bounding box.
         * If this hasn't previously been generated, its processed now by initializing its format tokens.
         *
         * @param applet	Applet holding this. This is used to locate text in the window.
         * @param g			Graphics to retrieve the font metrics from.
         * @param zone		Zone holding this properties.
         * @param transfo	Transformation to locate this.
         * @param center	Center of this before transformation.
         * @param supCtr	Center of this parent Satellite (Place center).
         * @param key		A key to get this HTMLText if it's already stored in zone.
         * @return			A new or existing HTMLText whose bounds are initilized.
         * @throws UnsupportedEncodingException 
         */
        protected function getHText( applet:PlanComponent, g:Graphics, zone:ActiveZone, transfo:Transfo, center:Point, supCtr:Point, key:Number):HTMLText // throws UnsupportedEncodingException
        {
            var htmlTxt:HTMLText= null;
            var data:Object= zone.m_datas[ key ];
            
            if ( center == null )	center = supCtr;
            
            if ( data == null )
            {
                var lines:String= parseString2( TEXT_VAL, zone.m_props, true );
                
                if ( lines.length> 0)
                {
                    var font:FontX= getFont( FONT_VAL, zone.m_props);
                    
                    htmlTxt = new HTMLText();
                    htmlTxt.m_body = new FormatToken();
                    htmlTxt.m_body.m_flags = getFlags( zone.m_props );
                    htmlTxt.m_body.m_margin = new Insets( 0, 2, 0, 2);
                    
                    htmlTxt.m_inCol = getColor( IN_COL_VAL, zone.m_props);
                    htmlTxt.m_outCol = getColor( OUT_COL_VAL, zone.m_props);
                    htmlTxt.m_color = (ColorX(getValue( TEXT_COL_VAL, zone.m_props))).m_color;
                    htmlTxt.m_bkCol = -1;
                    htmlTxt.m_style = font.getFlags( zone.m_props);
                    htmlTxt.m_size = font.getInt( FontX.SIZE_VAL, zone.m_props);
                    htmlTxt.m_name = font.getString( FontX.NAME_VAL, zone.m_props);
                    htmlTxt.m_wCur = 0;
                    htmlTxt.m_tokens = new Vector();
                    var heapElements:Vector.<String> = new Vector.<String>();
                    heapElements.push("c#" + (ColorX(getValue( TEXT_COL_VAL, zone.m_props))).m_color.toString(16));
                    heapElements.push("s=" + font.getInt( FontX.SIZE_VAL, zone.m_props));
                    heapElements.push("f=" +   font.getString( FontX.NAME_VAL, zone.m_props));
                    // Font Bold
                    if (( font.getFlags( zone.m_props) & 1 )!= 0)   heapElements.push( "b" );
                    //Font Italic
                    if (( font.getFlags( zone.m_props) & 2 )!= 0) heapElements.push( "i" );
                    htmlTxt.m_heap = heapElements;
                    
                    htmlTxt.parseText( g, lines );
                    htmlTxt.setTextBnds( applet.size, getFlags( zone.m_props), zone.m_flags ,transfo, supCtr, center );
                }
            }
            else
            {
                htmlTxt = HTMLText(data);
                htmlTxt.setTextBnds( applet.size, getFlags( zone.m_props), zone.m_flags, transfo, supCtr, center );
            }
            
            return htmlTxt;
        }
        
        /**
         * Parses this to extract the Tokens using a line of text.
         * This is necessary to evaluate the rendering of the text (color, size, alignment...).
         * @param g			The graphics used to retrieve the font metrics.
         * @param htmlText	A string of text with or without HTML tags to parse.
         */
        protected function parseText( g:Graphics, htmlText:String):void {
            var tokenizer:StringTokenizer= new StringTokenizer( htmlText, "<>" );
            var tokenStr:String, nextStr:String,
            prevStr:String     = tokenizer.nextToken();
            var hasMore:Boolean= tokenizer.hasMoreTokens(),
                isText:Boolean      = false;
            //var font:Font= new Font( m_name, m_style, m_size );
            var font:TextFormat = new TextFormat();
            font.font = m_name;
            font.size = m_size;
            if (m_style == 1) font.bold = true;
            else if (m_style == 2) font.italic = true;
            
            var textTok:TextToken= new TextToken();
            
            textTok.m_color = new ColorTransform( m_color );
            textTok.m_font  = font;
            
            m_curTok            = new FormatToken();
            m_curTok.m_flags    = m_body.m_flags;
            m_curTok.m_margin   = m_body.m_margin;
            
            m_tokens.addElement( m_curTok );
            
            //g.setFont( font );
            
            while ( hasMore )
            {
                tokenStr    = tokenizer.nextToken();
                hasMore     = tokenizer.hasMoreTokens();
                
                // A start of Tag
                if ( prevStr == ( "<" ))
                {
                    nextStr = hasMore ? tokenizer.nextToken(): null;
                    
                    // A closed Tag
                    if ( hasMore && nextStr == ( ">" )) // tag
                    {
                        textTok = updateTag( g, tokenStr );
                        
                        // An real Tag
                        if ( textTok != null )
                        {
                            isText  = false;
                        }
                            // An unknown Tag. Handle it as normal text.
                        else
                        {
                            textTok = new TextToken();
                            updateText( g, "<" + tokenStr + ">", textTok, isText );
                            isText  = true;
                        }
                        
                        prevStr = tokenizer.hasMoreTokens() ? tokenizer.nextToken(): null;
                    }
                        // An unclosed Tag. Handle it as normal text.
                    else
                    {
                        updateText( g, "<" + tokenStr, textTok, isText );
                        prevStr = nextStr;
                        isText  = true;
                    }
                }
                    // Normal text
                else
                {
                    updateText( g, prevStr, textTok, isText );
                    prevStr = tokenStr;
                    isText  = true;
                }
                
                hasMore = tokenizer.hasMoreTokens();
            }
            
            // Don't forget the last or only piece of text
            if ( prevStr != null )
            {
                updateText( g, prevStr, textTok, isText );
            }
            
            updateTag( g, "br" );  // to set last line position
            
            updateBounds();
        }
        
        /**
         * Evaluate this bounding box using margins.
         */
        private function updateBounds():void {
            var margin:Insets= m_body.m_margin;
            var i:int, n:int    = m_tokens.size(),
                x:int       = 0,
                y:int       = margin.top;
            var token:Object;
            var tTok:TextToken= null;
            var fTok:FormatToken= null;
            
            for ( i = 0; i < n; i ++ )
            {
                token   = m_tokens.elementAt( i );
                
                if ( token is FormatToken ) // a <br> or <p> or </p>
                {
                    if ( fTok != null )
                    {
                        y += fTok.m_dMax + ( fTok.m_margin != null ? fTok.m_margin.bottom : 0);
                    }
                    
                    fTok    = FormatToken(token);
                    
                    var leftLen:int= m_body.m_width - fTok.m_width;
                    
                    x   = ( fTok.m_flags & CENTER_BIT )!= 0? leftLen >> 1:(( fTok.m_flags & RIGHT_BIT )!= 0? leftLen : 0);
                    x  += margin.left +( fTok.m_margin != null ? fTok.m_margin.left : 0);
                    y  += fTok.m_aMax +( fTok.m_margin != null ? fTok.m_margin.top : 0);
                    m_tokens.removeElement( token );
                    i --;
                    n --;
                }
                else
                {
                    tTok    = TextToken(token);
                    tTok.m_bounds.x = x;
                    tTok.m_bounds.y = y;
                    x += tTok.m_bounds.width;
                }
            }
            
            m_bounds = new Rectangle( 0, 0, m_body.m_width + margin.left + margin.right-1, y + margin.bottom );
        }
        
        /**
         * Process this text Token to optimize tokens and evaluate the token bounding box.
         * @param g			Graphics to get the font metrics.
         * @param text		Text to add to the current textTokan.
         * @param textTok	Current TextToken.
         * @param isText	True if the previous textToken was a Text Token so we can merge it with this.
         */
        private function updateText( g:Graphics, text:String, textTok:TextToken, isText:Boolean):void {
            // The text exists!
            if ( text.length > 0)
            {
				trace( "HTMLText updateText à finir");
                
                
/*				var fd:FontDescription = new FontDescription();
				fd.fontName = "Garamond";
				fd.fontWeight = flash.text.engine.FontWeight.BOLD;
				var ef1:ElementFormat = new ElementFormat(fd);
                var fm:FontMetrics= ef1.getFontMetrics();
*/                var a:int	= 0,//fm.getAscent(),
                    d:int   = 0,//fm.getDescent(),
                    w:int   = 0,//fm.stringWidth( text ),
                    h:int   = 0;//fm.getHeight();
                
                // The previous token was a text too so we must merge it with this new one.
                if ( isText )
                {
                    textTok = TextToken(m_tokens.lastElement());
                    
                    textTok.m_text     += text;
                    textTok.m_bounds.width += w;
                    if ( textTok.m_bounds.height < h ) textTok.m_bounds.height = h;
                }
                    // The previous token was a formating one.
                else
                {
                    m_tokens.addElement( textTok );
                    
                    textTok.m_text      = text;
                    textTok.m_bounds    = new Rectangle ( 0, 0, w, h );
                }
                
                if ( a > m_body.m_aMax )   m_body.m_aMax  = a;
                if ( d > m_body.m_dMax )   m_body.m_dMax  = d;
                
                m_wCur             += w;
            }
        }
        
        /**
         * Creates a new TextToken by parsing a pseudo-HTML tag.
         * @param g		Graphics used to retrieve the font metric.
         * @param tag	A pseudo HTML tag without '<' and '>'.
         * @return		a new TextToken initialized according to the tag.
         */
        private function updateTag( g:Graphics, tag:String):TextToken {
            var tempTag:String;
            var textTok:TextToken= null;
            var begChar:String;
            
            if( tag.length > 0)
            {
                tag     = tag.toLowerCase();
                begChar = tag.charAt( 0);
                
                // End of Tag, we returns except for the case </p>
                if ( begChar == '/' )
                {
                    var nxtChar:String= tag.charAt( 1);
                    
                    tempTag = String(m_heap.pop());
                    
                    if ( tempTag.charAt( 0)== nxtChar ) // ! very simple verification !
                    {
                        textTok = closeTag( g, tempTag );
                        if ( nxtChar != 'p' )   return textTok;
                    }
                    else
                    {
                        trace( "[updateTag] no corresponding opened Tag : " + tag );
                        return null;
                    }
                }
                
                var prevTok:FormatToken= m_curTok;
                var prevMrg:Insets= prevTok.m_margin,
                    margin:Insets = m_body.m_margin;
                var flags:int= m_body.m_flags,
                    width:int= m_wCur +( prevMrg != null ? prevMrg.left + prevMrg.right : 0);
                
                // Start of Tag	+ </p>
                if ( tag == ( "br" )|| begChar == 'p' || tag == ( "/p" ))
                {
                    if ( tag == ( "br" ))
                    {
                        if ( prevMrg != null )
                            margin  = prevMrg;
                        flags   = prevTok.m_flags;
                    }
                    
                    // We specify new margins
                    if ( begChar == 'p' )
                    {
                        var alignStr:String= readAtt( tag, "a" );
                        
                        if ( alignStr != null )
                        {
                            var align:String= alignStr.charAt(0).toLowerCase();
                            
                            flags   = align == 'r' ? RIGHT_BIT :( align == 'c' ? CENTER_BIT : 0);
                        }
                        
                        margin  = readMargin( tag );
                        
                        if ( tag.length > 1&& alignStr == null && margin == null )
                        {
                            flags   = m_body.m_flags;
                            trace( "[updateTag] syntax error Tag : " + tag );
                            return null;
                        }
                        else
                        {
                            m_heap.push(tag);
                        }
                    }
                    
                    // update pr�vious format Token
                    prevTok.m_aMax  = m_body.m_aMax;
                    prevTok.m_dMax  = m_body.m_dMax;
                    prevTok.m_width = width;
                    
                    // reset current vars
                    m_body.m_aMax  = 0;
                    m_body.m_dMax  = 0;
                    m_wCur  = 0;
                    
                    // Stores the max width of all lines including its margins
                    if ( width > m_body.m_width )   m_body.m_width  = width;
                    
                    m_curTok            = new FormatToken();
                    m_curTok.m_flags    = flags;
                    m_curTok.m_margin   = margin == null ? m_body.m_margin : margin;
                    
                    m_tokens.addElement( m_curTok );
                    
                    textTok = new TextToken();
                    textTok.m_color = new ColorTransform( m_color );
                    textTok.m_font  = new TextFormat();
					textTok.m_font.font = m_name;
					textTok.m_font.size = m_size;
					if( m_style & BOLD)
						textTok.m_font.bold = true;
					else if( m_style & ITALIC)
						textTok.m_font.italic = true;
                }
                else if ( isGfx( begChar ))
                {
                    textTok = updateGfx( g, tag );
                    m_heap.push( tag );
                }
                else
                {
                    trace( "[updateTag] Unknown Tag : " + tag );
                    textTok = null;
                }
            }
            return textTok;
        }
        
        /**
         * Creates a TextToken using a graphical tag.
         * Such a tag is color or font related.
         * @param g		Graphics used to retrieve the font metrics.
         * @param tag	A pseudo HTML tag without '<', '</' and '>'.
         * @return		a new TextToken initialized according to the tag.
         */
        private function updateGfx( g:Graphics, tag:String):TextToken {
            var textTok:TextToken= new TextToken();
            
            if ( startsWith(tag, "c=" )|| startsWith(tag, "k=" ))
            {
                var color:ColorTransform = startsWith(tag, "c=" )? textTok.m_color : textTok.m_bkCol;
                var rgb:int= 0;
                
                //textTok.m_font = new Font( m_name, m_style, m_size );
                textTok.m_font = new TextFormat();
                textTok.m_font.font = m_name;
                textTok.m_font.size = m_size;
                if (m_style == 1) textTok.m_font.bold = true;
                else if (m_style == 2) textTok.m_font.italic = true;
                
                //g.setFont( textTok.m_font );
                
                try
                {
                    rgb = int(tag.substring(2));// #RRGGBB
                    
                    if ( color == null || color.color == rgb )
                    {
                        if ( startsWith( tag, "c=" ))
                        {
                            m_color = rgb;
                            //textTok.m_color = new Color( rgb );
                            //textTok.m_bkCol = m_bkCol != -1? new Color( m_bkCol ) : null;
                        }
                        else
                        {
                            m_bkCol = rgb;
                            //textTok.m_bkCol = new Color( rgb );
                            //textTok.m_color = new Color( m_color );
                        }
                    }
                }
                catch ( e:Error)
                {
                    trace( "[updateTag] Wrong color format : " + tag );
                    return null;
                }
            }
            else
            {
                var col:ColorTransform = new ColorTransform();
                col.color = m_color;
                textTok.m_color = col;
                
                if ( tag == ( "b" ))
                {
                    //m_style |= Font.BOLD;
                    m_style |= 0x1;
                }
                else if ( tag == ( "i" ))
                {
                    //m_style |= Font.ITALIC;
                    m_style |= 0x2;
                }
                else if ( startsWith( tag, "s=" ))
                {
                    m_size  = Number(tag.substring(2));
                }
                else if ( startsWith( tag, "f=" ))
                {
                    m_name  = tag.substring( 2);
                }
                else
                {
                    trace( "[updateGfx] Syntax error Tag : " + tag );
                    return null;
                }
                //textTok.m_font = new Font( m_name, m_style, m_size );
                textTok.m_font.font = m_name;
                if (m_style == 0x1)
                    textTok.m_font.bold =  true;
                else if (m_style == 0x2)
                    textTok.m_font.italic =  true;
                textTok.m_font.size = m_size;
                
                //g.setFont( textTok.m_font );
            }
            return textTok;
        }
        
        /**
         * Handle the closing of a tag.
         * @param g		Graphics used to retrieve the font metrics.
         * @param tag	The closing tag without '</' and '>'.
         * @return		A new TextToken corresponding to the new state.
         */
        private function closeTag( g:Graphics, tag:String):TextToken {
            var textTok:TextToken= new TextToken();
            var i:int= m_heap.length - 1;
            var c:String= tag.charAt( 0);
            var prevTag:String;
            
            //m_heap.removeElement( tag );
            var tag_index:int = m_heap.indexOf(tag);
            m_heap.splice( tag_index , 1 );
            
            if ( c == 'b' )         m_style &= ~0x1;
            else if ( c == 'i' )    m_style &= ~0x2;
            
            if ( isGfx( c ))
            {
                while (i-- > 0)
                {   // find previous Tag of the same type in the heap
                    prevTag = String(m_heap[i]);
                    
                    if ( prevTag.charAt( 0)== c )
                    {
                        return updateGfx( g, prevTag );
                    }
                }
            }
            //textTok.m_font = new Font( m_name, m_style, m_size );
            textTok.m_font.font = m_name;
            if (m_style == 0x1)
                textTok.m_font.bold =  true;
            else if (m_style == 0x2)
                textTok.m_font.italic =  true;
            textTok.m_font.size = m_size;
            
            //g.setFont( textTok.m_font );
            
            return textTok;
        }
        
        /**
         * Draws this using a cardinal direction.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         * @param dir	One of the following directions:<br>
         * NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST.
         */
        protected function drawText( s:Sprite, size:Dimension, dir:int):void {
            var pos:Point= new Point();
            var xMax:int= size.width - m_bounds.width - 1,
                yMax:int= size.height - m_bounds.height - 1;
            
            if (( dir & 8)!= 0)         pos.x = xMax;
            else if (( dir & 4)!= 0)    pos.x = xMax >> 1;
            if (( dir & 2)!= 0)         pos.y = yMax;
            else if (( dir & 1)!= 0)    pos.y = yMax >> 1;
            
            drawText3( s, size, pos );
        }
        
        /**
         * Draws this using a previously evaluated position.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         */
        protected function drawText2( s:Sprite, size:Dimension):void {
            drawText3( s, size, new Point( m_bounds.x, m_bounds.y ));
        }
        
        /**
         * Draws this at a position.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         * @param pos	Where to draw this.
         */
        protected function drawText3( s:Sprite, size:Dimension, pos:Point):void {
            //var g:Graphics2D= Graphics2D(gi);
            
			trace( "HTMLText drawText3 à fnir");
/*            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            
            var composite:Composite= AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8);
            var cb:Composite= g.getComposite();
            g.setComposite(composite);
*/            
            var textTok:TextToken;
            var i:int, n:int    = m_tokens.size();
            
            if ( m_inCol != null )
            {
                var black:ColorTransform = new ColorTransform();
                black.color = 0x000000;
                var colors:Array = [m_inCol.color, black.color];
                var alphas:Array = [1, 1];
                var ratios:Array = [0, 255];
                s.graphics.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios);
                s.graphics.drawRoundRect(pos.x, pos.y, m_bounds.width, m_bounds.height, 10, 10);
                s.graphics.endFill();
              
            }
            
            if ( m_outCol != null )
            {
                s.graphics.beginFill(m_inCol.color);
                s.graphics.drawRoundRect(pos.x, pos.y, m_bounds.width, m_bounds.height, 5, 5);
                s.graphics.endFill();
            }
            
            for ( i = 0; i < n; i ++ )
            {
                textTok = TextToken(m_tokens.elementAt( i ));
                textTok.paint( s, pos );
            }
            
 /*           composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2);					
            g.setComposite(composite);
*/
            if ( n==1) // draw reflection only for one line boxes
            {
                var white:ColorTransform = new ColorTransform();
                white.color = 0xFFFFFF;
                s.graphics.beginFill(white.color);
                s.graphics.drawRoundRect(pos.x+2, pos.y+2, m_bounds.width-4, m_bounds.height/3, 5, 5);
                s.graphics.endFill();
            }
/*            
            g.setComposite(cb);
*/            
            m_bounds.x  = pos.x;
            m_bounds.y  = pos.y;
        }
        
        /**
         * Evaluate this bounds.
         * The bounds member is updated.
         * @param size		Size of the Window to draw in.
         * @param flags		Text Alignment flags.
         * @param posFlags	Subzones Side bits.
         * @param transfo	A transformation to locate this relatively to a center.
         * @param supCtr	Center of the parent satellite (Place center).
         * @param center	Center of this before the transformation.
         */
        protected function setTextBnds( size:Dimension, flags:int, posFlags:int, transfo:Transfo, supCtr:Point, center:Point):void {
            var isFloat:Boolean= Base.isEnabled( flags, FLOAT_BIT );
            var dx:int= 0,
                dy:int	= 0,
                x:int   = center.x,
                y:int   = center.y,
                w:int   = m_bounds.width,
                h:int   = m_bounds.height,
                w2:int  = w >> 1,
                h2:int  = h >> 1;
            
            if ( supCtr != null )
            {
                dx = x - supCtr.x;
                dy = y - supCtr.y;
            }
            
            if ( Base.isEnabled( flags, CORNER_BIT ) && supCtr != null )
            {
                if (( posFlags & ActiveZone.SIDE_BIT )!= 0)
                {
                    x += ( posFlags & ActiveZone.LEFT_BIT )!= 0? w2 : -w2;
                }
                else
                {
                    if ( dx != 0)
                    {
                        x += dx > 0? w2 : -w2;
                    }
                }
                
                if ( dy != 0)
                {
                    y += dy > 0? h2 : -h2;
                }
            }
            else if ( isFloat )
            {
                x += w2;
                y += h2 + 32; // cursor height
            }
            
            if ( transfo != null )
            {
                var dp:Point= transfo.getCart();
                x += dp.x;
                y += dp.y;
            }
            
            x -= w2;
            y -= h2;
            
            if ( isFloat )  // avoid boundaries!
            {
                if ( x < 0)    x = 4;
                else if ( x + w-1> size.width ) x = size.width - 4- w-1;
                
                if ( y < 0)    y = 4;
                else if ( y + h > size.height )  y = size.height - 4- h;
            }
            
            m_bounds.x = x;
            m_bounds.y = y;
        }
        
        /**
         * Read margin attributes in a &lt;p&gt; tag.
         * @param tag	A tag holding some margin attributes.
         * @return		An inset of t, l, b, r margins or null if none are defined.
         */
        private function readMargin( tag:String):Insets {
            var t:String, l:String, b:String, r:String;
            
            t = readAtt( tag, "t" );
            l = readAtt( tag, "l" );
            b = readAtt( tag, "b" );
            r = readAtt( tag, "r" );
            
            if ( l == null )
                l = readAtt( tag, "x" );
            
            if ( t == null && l == null && b == null && r == null )
            {
                return null;
            }
            else
            {
                var bc:Insets;
                bc.top = t == null ? 0: int(t);
                bc.bottom = b == null ? 0: int(b);
                bc.left = l == null ? 0: int(l);
                bc.right = r == null ? 0: int(r);
                return bc;
            }
        }
        
        /**
         * Reads a tag attribute.
         * @param tag	Tag holding the attribute.
         * @param att	Name of the attribute to find.
         * @return		The value of the attribute or null if none where defined in this tag.
         */
        private function readAtt( tag:String, att:String):String {
            var beg:int, end:int;
            
            if (( beg = tag.indexOf( att + '=' ))!= -1)
            {
                beg += att.length + 1;    // don't forget to skip '='
                end = tag.indexOf( ' ', beg );
                return end == -1? tag.substring( beg ) : tag.substring( beg, end );
            }
            
            return null;
        }
        
        /**
         * Returns wether a character is a graphical tag.
         * @param c	One letter tag to check.
         * @return	True if this tag is color or font related.
         */
        private function isGfx( c:String):Boolean {
            return c == 'c' || c == 'k' || c == 's' || c == 'f' || c == 'b' || c == 'i';
        }
        
        public static function startsWith( string:String, pattern:String):Boolean
        {
            string  = string.toLowerCase();
            pattern = pattern.toLowerCase();
            
            return pattern == string.substr( 0, pattern.length );
        }

        public function get m_body():FormatToken
        {
            return _m_body;
        }

        public function get m_tokens():Vector
        {
            return _m_tokens;
        }

        public function get m_heap():Vector.<String>
        {
            return _m_heap;
        }

        public function get m_wCur():int
        {
            return _m_wCur;
        }

		private function uint2hex(dec:uint):String {
			var digits:String = "0123456789ABCDEF";
			var hex:String = '';
			while (dec > 0) {
				var next:uint = dec & 0xF;
				dec >>= 4;
				hex = digits.charAt(next) + hex;
			}
			if (hex.length == 0) hex = '0';
			return hex;
		}
    }
    
}
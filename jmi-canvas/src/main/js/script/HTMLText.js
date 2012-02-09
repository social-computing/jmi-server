JMI.namespace("script.HTMLText");

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
JMI.script.HTMLText = (function() {

	var bounds,//:Rectangle;
		text, //:String;
		inCol, //:ColorTransform;
		font, //:TextFormat;
		oneLine, //:Boolean;
		outCol, //:ColorTransform;
		blur, //:int;
		rounded; //:int;
		
	var HTMLText = function() {
		JMI.script.Base.call( this);
	};
	
	HTMLText.prototype = {
		constructor: JMI.script.HTMLText,
		
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
        initValues: function(inCol, outCol, textCol, fontSiz, fontStl, fontNam, blur, rounded, flags, margin) {
			// TODO à finir ????
            this.inCol     = inCol;
            this.outCol    = outCol;
			this.blur		= blur;
			this.rounded   = rounded;
			
			this.font = new Object();
			this.font.font = fontNam;
			this.font.size = fontSiz;
			this.font.color = textCol;
			if (( fontStl & BOLD )!= 0)  this.font.bold = true;
			if (( fontStl & ITALIC )!= 0) this.font.italic = true;
			
			this.font.leftMargin = margin.left;
			this.font.rightMargin = margin.right;
        },
		
		init: function( base, zone) {
			this.font = base.getFont( JMI.script.HTMLText.FONT_VAL, zone.props);
			this.font.init( zone.props);
			this.inColor = base.getColor( JMI.script.HTMLText.IN_COL_VAL, zone.props);
			this.outColor = base.getColor( JMI.script.HTMLText.OUT_COL_VAL, zone.props);
			this.blur = parseInt( base.parseString( JMI.script.HTMLText.BLUR_COL_VAL, zone.props )[0]);
			this.rounded = base.getInt( JMI.script.HTMLText.ROUNDED_COL_VAL, zone.props);
			var color = base.getValue( JMI.script.HTMLText.TEXT_COL_VAL, zone.props);
			if( color != null)
				this.font.color = color.getColor();

			this.body          = new JMI.script.FormatToken();
			this.body.flags  = base.getFlags( zone.props );
			this.body.margin = new JMI.script.Insets( 0, 2, 0, 2 );
			
			this.tokens = [];
			this.heap = [];
			this.heap.push( "c=#" + this.font.color);
			this.heap.push( "s=" + this.font.size);
			this.heap.push( "f=" + this.font.name);
			this.wCur = 0;
	
			if (( this.font.style & JMI.script.FontX.BOLD )!= 0 )   this.heap.push( "b" );
			if (( this.font.style & JMI.script.FontX.ITALIC )!= 0 ) this.heap.push( "i" );
		},
        
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
        getHText: function( applet, gDrawingContext, zone, transfo, center, supCtr, textKey) // throws UnsupportedEncodingException
        {
        	// Voir Slice.js ligne 101 : textKey
        	// TODO later : difficulté à trouver la bonne clé (textKey), voir Slice.js paint
            var htmlTxt = null;//zone.datas[ textKey ];
            
            if ( center == null )	center = supCtr;
            
            if ( htmlTxt == null )
            {
				htmlTxt = new JMI.script.HTMLText();
				var lines = this.parseString2( JMI.script.HTMLText.TEXT_VAL, zone.props, true );
                
                if ( lines.length> 0)
                {
					htmlTxt.init( this, zone);
					htmlTxt.parseText( applet, gDrawingContext, lines);
                    //htmlTxt.updateBounds( applet, gDrawingContext);
                    htmlTxt.setTextBnds( applet.size, this.getFlags( zone.props), zone.flags ,transfo, supCtr, center );
                }
                else {
					htmlTxt.bounds = new JMI.script.Rectangle();
					htmlTxt.tokens = [];
				}
            }
            else
            {
                htmlTxt.setTextBnds( applet.size, this.getFlags( zone.props), zone.flags, transfo, supCtr, center );
            }
            
            return htmlTxt;
        },
        
        tokenize: function( text, sep, index) {
        	if( !index)
        		index = 0;
        	var tokens = [];
        	if( index >= sep.length) {
        		tokens.push( text);
        		return tokens;
        	}
        	var separator = sep[index];
        	var tks = text.split(separator);
        	if( text[0] == separator)
        		tokens.push(separator);
        	for( var i = 0 ; i < tks.length; ++i) {
        		tokens = tokens.concat( this.tokenize( tks[i], sep, index+1));
        		if( i < tks.length-1)
        			tokens.push(separator);
        	}
        	if( text[text.length-1] == separator)
        		tokens.push(separator);
        	return tokens;
        },
        removeElement: function( array, value) {
        	var found = false;
        	for( var i = 0; i < array.length; ++i) {
        		if( array[i] == value) 
        			found = true;
       			if( found  && i < array.length-1)
        			array[i] = array[i+1];
        	}
       		if( found)
        		array.pop();
		},        
	 	/**
		 * Parses this to extract the Tokens using a line of text.
		 * This is necessary to evaluate the rendering of the text (color, size, alignment...).
		 * @param g			The graphics used to retrieve the font metrics.
		 * @param htmlText	A string of text with or without HTML tags to parse.
		 */
		parseText: function ( applet, gDrawingContext, htmlText )
		{
			var 			tokenizer   = this.tokenize( htmlText, "<>");
			var 			tokenStr, nextStr,
							prevStr     = 0 < tokenizer.length ? tokenizer[0] : null;
			var 			hasMore     = 1 < tokenizer.length,
							isText      = false;
			var 			textTok     = new JMI.script.TextToken();
	
			textTok.font  = this.font;
	
			this.curTok          = new JMI.script.FormatToken();
			this.curTok.flags    = this.body.flags;
			this.curTok.margin   = this.body.margin;
	
			this.tokens.push( this.curTok );

			gDrawingContext.font = this.font.canvas;

			var itokenizer = 1; 
			while ( hasMore )
			{
				tokenStr    = itokenizer < tokenizer.length ? tokenizer[itokenizer] : null;
				++itokenizer;
				hasMore     = itokenizer < tokenizer.length;
	
				// A start of Tag
				if ( prevStr == "<" )
				{
					nextStr = hasMore ? tokenizer[itokenizer]: null;
					++itokenizer;
	
					// A closed Tag
					if ( hasMore && nextStr == ">" ) // tag
					{
						textTok = this.updateTag( applet, gDrawingContext, tokenStr );
	
						// An real Tag
						if ( textTok != null )
						{
							isText  = false;
						}
						// An unknown Tag. Handle it as normal text.
						else
						{
							textTok = new JMI.script.TextToken();
							this.updateText( gDrawingContext, "<" + tokenStr + ">", textTok, isText );
							isText  = true;
						}
	
						prevStr = itokenizer < tokenizer.length ? tokenizer[itokenizer]: null;
						++itokenizer;
					}
					// An unclosed Tag. Handle it as normal text.
					else
					{
						this.updateText( gDrawingContext, "<" + tokenStr, textTok, isText );
						prevStr = nextStr;
						isText  = true;
					}
				}
				// Normal text
				else
				{
					this.updateText( gDrawingContext, prevStr, textTok, isText );
					prevStr = tokenStr;
					isText  = true;
				}
	
				hasMore = itokenizer < tokenizer.length;
			}
	
			// Don't forget the last or only piece of text
			if ( prevStr != null )
			{
				this.updateText( gDrawingContext, prevStr, textTok, isText );
			}
	
			this.updateTag( applet, gDrawingContext, "br" );  // to set last line position
	
			this.updateBounds();
		},

		/**
		 * Evaluate this bounding box using margins.
		 */
		updateBounds: function() {
			var 		margin  = this.body.margin;
			var         i, n    = this.tokens.length,
						x       = 0,
						y       = margin.top;
			var 	    token;
			var 		tTok    = null;
			var 		fTok    = null;
	
			for ( i = 0; i < n; i ++ )
			{
				token   = this.tokens[i];
	
				if ( token instanceof JMI.script.FormatToken) // a <br> or <p> or </p>
				{
					if ( fTok != null )
					{
						y += fTok.dMax + ( fTok.margin != null ? fTok.margin.bottom : 0 );
					}
	
					fTok    = token;
	
					var leftLen = this.body.width - fTok.width;
	
					x   = ( fTok.flags & JMI.script.HTMLText.CENTER_BIT )!= 0 ? leftLen >> 1 :(( fTok.flags & JMI.script.HTMLText.RIGHT_BIT )!= 0 ? leftLen : 0 );
					x  += margin.left +( fTok.margin != null ? fTok.margin.left : 0 );
					y  += fTok.aMax +( fTok.margin != null ? fTok.margin.top : 0 );
					this.removeElement( this.tokens, token);
					i --;
					n --;
				}
				else
				{
					tTok    = token;
					tTok.bounds.x = x;
					tTok.bounds.y = y;
					x += tTok.bounds.width;
				}
			}
	
			this.bounds = new JMI.script.Rectangle( 0, 0, this.body.width + margin.left + margin.right-1, y + margin.bottom );
			this.bounds.add( JMI.script.HTMLText.MARGIN*2, JMI.script.HTMLText.MARGIN*2);
			if( this.outColor != null) {
			 	this.bounds.add( JMI.script.HTMLText.BORDER_WIDTH*2, JMI.script.HTMLText.BORDER_WIDTH*2);
			 }
		},
		/**
		 * Process this text Token to optimize tokens and evaluate the token bounding box.
		 * @param g			Graphics to get the font metrics.
		 * @param text		Text to add to the current textTokan.
		 * @param textTok	Current TextToken.
		 * @param isText	True if the previous textToken was a Text Token so we can merge it with this.
		 */
		updateText: function( gDrawingContext, text, textTok, isText )
		{
			// The text exists!
			if ( text.length > 0 )
			{
				var metrics = gDrawingContext.measureText( text);
				// TODO
				var             a       = 0 //fm.getAscent(),
								d       = Math.round( this.font.size * 96 / 72); //fm.getDescent(),
								w       = metrics.width,
								h       = Math.round( this.font.size * 96 / 72);

				// The previous token was a text too so we must merge it with this new one.
				if ( isText )
				{
					textTok = this.tokens[this.tokens.length-1];
	
					textTok.text     += text;
					textTok.bounds.width += w;
					if ( textTok.bounds.height < h ) textTok.bounds.height = h;
				}
				// The previous token was a formating one.
				else
				{
					this.tokens.push( textTok );
	
					textTok.text      = text;
					textTok.bounds    = new JMI.script.Rectangle ( 0, 0, w, h );
				}
	
				if ( a > this.body.aMax )   this.body.aMax  = a;
				if ( d > this.body.dMax )   this.body.dMax  = d;
	
				this.wCur             += w;
			}
		},
	
		/**
		 * Creates a new TextToken by parsing a pseudo-HTML tag.
		 * @param g		Graphics used to retrieve the font metric.
		 * @param tag	A pseudo HTML tag without '<' and '>'.
		 * @return		a new TextToken initialized according to the tag.
		 */
		updateTag: function( applet, gDrawingContext, tag ) {
			var      tempTag;
			var		 textTok = null;
			var      begChar;
	
			if( tag.length > 0)
			{
				tag     = tag.toLowerCase();
				begChar = tag.charAt( 0 );
	
				// End of Tag, we returns except for the case </p>
				if ( begChar == '/' )
				{
					var    nxtChar = tag.charAt( 1 );
	
					tempTag = this.heap[this.heap.length-1];
	
					if ( tempTag.charAt( 0 )== nxtChar ) // ! very simple verification !
					{
						textTok = closeTag( applet, gDrawingContext, tempTag );
						if ( nxtChar != 'p' )   return textTok;
					}
					else
					{
						applet.log("[updateTag] no corresponding opened Tag : " + tag );
						return null;
					}
				}
	
				var 		prevTok = this.curTok;
				var			prevMrg = prevTok.margin,
							margin  = this.body.margin;
				var         flags   = this.body.flags,
							width   = this.wCur +( prevMrg != null ? prevMrg.left + prevMrg.right : 0 );
	
				// Start of Tag	+ </p>
				if ( tag=="br" || tag=="br/"  || begChar == 'p' || tag == "/p" )
				{
					if ( tag == "br" || tag == "br/")
					{
						if ( prevMrg != null )
							margin  = prevMrg;
						flags   = prevTok.flags;
					}
	
					// We specify new margins
					if ( begChar == 'p' )
					{
						var  alignStr    = readAtt( tag, "a" );
	
						if ( alignStr != null )
						{
							var    align   = alignStr.charAt( 0 ).toLowerCase();
	
							flags   = align == 'r' ? RIGHT_BIT :( align == 'c' ? CENTER_BIT : 0 );
						}
	
						margin  = readMargin( tag );
	
						if ( tag.length > 1 && alignStr == null && margin == null )
						{
							flags   = m_body.flags;
							applet.log( "[updateTag] syntax error Tag : " + tag );
							return null;
						}
						else
						{
							m_heap.addElement( tag );
						}
					}
	
					// update pr�vious format Token
					prevTok.aMax  = this.body.aMax;
					prevTok.dMax  = this.body.dMax;
					prevTok.width = width;
	
					// reset current vars
					this.body.aMax  = 0;
					this.body.dMax  = 0;
					this.wCur  = 0;
	
					// Stores the max width of all lines including its margins
					if ( width > this.body.width )   this.body.width  = width;
	
					this.curTok          = new JMI.script.FormatToken();
					this.curTok.flags    = flags;
					this.curTok.margin   = margin == null ? this.body.margin : margin;
	
					this.tokens.push( this.curTok );
	
					textTok = new JMI.script.TextToken();
					textTok.font  = this.font;
				}
				else if ( this.isGfx( begChar ))
				{
					textTok = this.updateGfx( applet, gDrawingContext, tag );
					this.heap.push( tag );
				}
				else
				{
					applet.log("[updateTag] Unknown Tag : " + tag );
					textTok = null;
				}
			}
			return textTok;
		},
	
		/**
		 * Creates a TextToken using a graphical tag.
		 * Such a tag is color or font related.
		 * @param g		Graphics used to retrieve the font metrics.
		 * @param tag	A pseudo HTML tag without '<', '</' and '>'.
		 * @return		a new TextToken initialized according to the tag.
		 */
		updateGfx: function( applet, gDrawingContext, tag ) {
			var textTok = new JMI.script.TextToken();
	
			var subtag = tag.substr(0, 3);
			if ( subtag == "c=" || subtag == "k=")
			{
				var 	color   = (subtag == "c=" )? textTok.color : textTok.bkCol;
				var     rgb     = 0;
	
				textTok.font = this.font;
				gDrawingContext.font = textTok.font.canvas;
	
				try
				{
					rgb = parseInt( tag.substr( 2 ), 16);// #RRGGBB
	
					if ( color == null || color.getRGB()== rgb )
					{
						if ( subtag == "c=" )
						{
							this.color = rgb;
							textTok.color = new JMI.script.ColorX( rgb );
							textTok.bkCol = this.bkCol != -1 ? new JMI.script.ColorX( this.bkCol ) : null;
						}
						else
						{
							this.bkCol = rgb;
							textTok.bkCol = new JMI.script.ColorX( rgb );
							textTok.color = new JMI.script.ColorX( this.color );
						}
					}
				}
				catch ( e )
				{
					applet.log( "[updateTag] Wrong color format : " + tag );
					return null;
				}
			}
			else
			{
				textTok.color = new JMI.script.ColorX( this.color );
	
				if ( tag == "b" )
				{
					this.font.style |= JMI.script.FontX.BOLD;
				}
				else if ( tag == "i" )
				{
					this.font.style |= JMI.script.FontX.ITALIC;
				}
				else if ( subtag == "s=" )
				{
					this.font.size  = parseInt( tag.substr( 2 ));
				}
				else if ( subtag == "f=" )
				{
					this.font.name  = tag.substr( 2 );
				}
				else
				{
					applet.log( "[updateGfx] Syntax error Tag : " + tag );
					return null;
				}
				textTok.font = this.font;
				gDrawingContext.font = textTok.font.canvas;
			}
			return textTok;
		},
	
		/**
		 * Handle the closing of a tag.
		 * @param g		Graphics used to retrieve the font metrics.
		 * @param tag	The closing tag without '</' and '>'.
		 * @return		A new TextToken corresponding to the new state.
		 */
		closeTag: function( applet, gDrawingContext, tag ) {
			var			textTok = new JMI.script.TextToken();
			var         i       = this.heap.size()- 1;
			var         c       = tag.charAt( 0 );
			var		    prevTag;
	
			this.removeElement( heap, tag );
	
			if ( c == 'b' )         this.style &= ~Font.BOLD;
			else if ( c == 'i' )    this.style &= ~Font.ITALIC;
	
			if ( isGfx( c ))
			{
				while ( i -- > 0 )
				{   // find previous Tag of the same type in the heap
					prevTag = this.heap[i];
	
					if ( prevTag.charAt( 0 )== c )
					{
						return updateGfx( applet, gDrawingContext, prevTag );
					}
				}
			}
			textTok.font = this.font;
			gDrawingContext.font = textTok.font.canvas;
			return textTok;
		},
        
        /**
         * Draws this using a cardinal direction.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         * @param dir	One of the following directions:<br>
         * NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST.
         */
        drawText: function( gDrawingContext, size, dir) {
            var pos = new JMI.script.Point();
            var xMax = size.width - this.bounds.width - 1,
                yMax = size.height - this.bounds.height - 1;
            
            if (( dir & 8)!= 0)         pos.x = xMax;
            else if (( dir & 4)!= 0)    pos.x = xMax >> 1;
            if (( dir & 2)!= 0)         pos.y = yMax;
            else if (( dir & 1)!= 0)    pos.y = yMax >> 1;
            
            this.drawText3( gDrawingContext, size, pos );
        },
        
        /**
         * Draws this using a previously evaluated position.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         */
        drawText2: function( gDrawingContext, size) {
            this.drawText3( gDrawingContext, size, new JMI.script.Point( this.bounds.x, this.bounds.y ));
        },
        
        /**
         * Draws this at a position.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         * @param pos	Where to draw this.
         */
        drawText3: function( gDrawingContext, size, pos) {
			var borderWidth = 0;
			if ( this.outColor != null )
				borderWidth = JMI.script.HTMLText.BORDER_WIDTH;
            if ( this.inColor != null )
            {
            	gDrawingContext.lineWidth = '1';
				if ( this.outColor != null ) {
					gDrawingContext.strokeStyle = this.outColor;
					if( this.rounded == -1)
						gDrawingContext.strokeRect(pos.x, pos.y, this.bounds.width, this.bounds.height);
					else {
						JMI.util.ImageUtil.roundRect( gDrawingContext, pos.x, pos.y, this.bounds.width, this.bounds.height, this.rounded);
						gDrawingContext.stroke();
					}
				}
				gDrawingContext.fillStyle = this.inColor;
				if( this.rounded == -1)
					gDrawingContext.fillRect(pos.x+borderWidth, pos.y+borderWidth, this.bounds.width-2*borderWidth, this.bounds.height-2*borderWidth);
				else {
					JMI.util.ImageUtil.roundRect( gDrawingContext, pos.x+borderWidth, pos.y+borderWidth, this.bounds.width-2*borderWidth, this.bounds.height-2*borderWidth, this.rounded);
					gDrawingContext.fill();
				}
           }
			
			var n = this.tokens.length;
			for ( i = 0; i < n; i ++ )
			{
				textTok = this.tokens[i];
				textTok.paint( gDrawingContext, pos, borderWidth);
			}

			if ( this.oneLine && this.inColor == null) // draw reflection only for one line boxes
            {
            	// TODO portage Non !!!
/*                var white:ColorTransform = new ColorTransform();
                white.color = 0xFFFFFF;
                s.graphics.beginFill(white.color, 0.2);
				s.graphics.lineStyle();
                s.graphics.drawRoundRect(pos.x+borderWidth, pos.y+3+borderWidth, bounds.width-2*borderWidth, (bounds.height/3)-2*borderWidth, 5, 5);
                s.graphics.endFill();*/
            }
            this.bounds.x  = pos.x;
            this.bounds.y  = pos.y;
        },
        
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
        setTextBnds: function( size, flags, posFlags, transfo, supCtr, center) {
            var isFloat = JMI.script.Base.isEnabled( flags, JMI.script.HTMLText.FLOAT_BIT );
            var dx = 0,
                dy	= 0,
                x = center.x,
                y = center.y,
                w   = this.bounds.width,
                h   = this.bounds.height,
                w2  = w >> 1,
                h2  = h >> 1;
            
            if ( supCtr != null )
            {
                dx = x - supCtr.x;
                dy = y - supCtr.y;
            }
            
            if ( JMI.script.Base.isEnabled( flags, JMI.script.HTMLText.CORNER_BIT ) && supCtr != null )
            {
                if (( posFlags & JMI.script.ActiveZone.SIDE_BIT )!= 0)
                {
                    x += ( posFlags & JMI.script.ActiveZone.LEFT_BIT )!= 0? w2 : -w2;
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
                var dp = transfo.getCart();
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
            
            this.bounds.x = x;
            this.bounds.y = y;
        },
		/**
		 * Reads a tag attribute.
		 * @param tag	Tag holding the attribute.
		 * @param att	Name of the attribute to find.
		 * @return		The value of the attribute or null if none where defined in this tag.
		 */
		readAtt: function( tag, att ) {
			var beg, end;
	
			if (( beg = tag.indexOf( att + '=' ))!= -1)
			{
				beg += att.length + 1;    // don't forget to skip '='
				end = tag.indexOf( ' ', beg );
				return end == -1 ? tag.substr( beg ) : tag.substr( beg, end );
			}
			return null;
		},
		/**
		 * Returns wether a character is a graphical tag.
		 * @param c	One letter tag to check.
		 * @return	True if this tag is color or font related.
		 */
		isGfx: function( c )
		{
			return c == 'c' || c == 'k' || c == 's' || c == 'f' || c == 'b' || c == 'i';
		}
 	};
	
	// Héritage
	for (var element in JMI.script.Base.prototype ) {
		if( !HTMLText.prototype[element])
			HTMLText.prototype[element] = JMI.script.Base.prototype[element];
	}
	
	return HTMLText;
}());
		
JMI.script.HTMLText.MARGIN = 2;
JMI.script.HTMLText.BORDER_WIDTH = 2;
/**
 * Index of the bit flag prop in VContainer table
 */
//	public  static final int    FLAGS_VAL           = 0;

/**
 * Index of the font prop in VContainer table
 */
JMI.script.HTMLText.FONT_VAL = 1;

/**
 * Index of the text prop in VContainer table
 */
JMI.script.HTMLText.TEXT_VAL = 2;

/**
 * Index of the inside Color prop in VContainer table
 */
JMI.script.HTMLText.IN_COL_VAL = 3;

/**
 * Index of the border Color prop in VContainer table
 */
JMI.script.HTMLText.OUT_COL_VAL = 4;

/**
 * Index of the text Color prop in VContainer table
 */
JMI.script.HTMLText.TEXT_COL_VAL = 5;

/**
 * Index of the Blur prop in VContainer table
 */
JMI.script.HTMLText.BLUR_COL_VAL = 6;

/**
 * Index of the Rounded prop in VContainer table
 */
JMI.script.HTMLText.ROUNDED_COL_VAL = 7;
	
   /**
 * True if this text is anchored by a corner.(like subZones tips).
 */
JMI.script.HTMLText.CORNER_BIT = 0x0100;	// Be carefull with this flags, they must not override Fonts ones (0x1, 0x2)!

/**
 * True if this text is right aligned (multiline).
 */
JMI.script.HTMLText.RIGHT_BIT = 0x0200;

/**
 * True if this text is centered (multiline).
 */
JMI.script.HTMLText.CENTER_BIT = 0x0400;

/**
 * True if this text is floating inside the window (tooltip).
 */
JMI.script.HTMLText.FLOAT_BIT = 0x0800;

/**
 * True if this text is read from an URL.
 */
JMI.script.HTMLText.URL_BIT = 0x1000;

/**
 * Cardinal orientation of the Tip on the screen        X x Y y
 */
JMI.script.HTMLText.NORTH = 0x4;  // 0 1 0 0;
JMI.script.HTMLText.NORTH_EAST = 0x8;  // 1 0 0 0;
JMI.script.HTMLText.EAST = 0x9;  // 1 0 0 1;
JMI.script.HTMLText.SOUTH_EAST = 0xA;  // 1 0 1 0;
JMI.script.HTMLText.SOUTH = 0x6;  // 0 1 1 0;
JMI.script.HTMLText.SOUTH_WEST = 0x2;  // 0 0 1 0;
JMI.script.HTMLText.WEST = 0x1;  // 0 0 0 1;
JMI.script.HTMLText.NORTH_WEST = 0x0;  // 0 0 0 0;

JMI.script.HTMLText.BOLD = 0x1;  // 0 1 0 0;
JMI.script.HTMLText.ITALIC = 0x2;  // 0 1 0 0;

/**
 * <p>Title: TextToken</p>
 * <p>Description: A piece of text that can be changed to simulate HTML rendering.<br>
 * To achieve this, it can be located, have a foreground and background color and a font.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.TextToken = (function() {

    var TextToken = function() {
    };

	TextToken.prototype = {
		constructor: JMI.script.TextToken,
		/**
		 * Paint this at a specified location.
		 * The inner location is used to offset the fonts.
		 * @param g		The graphics to draw in.
		 * @param pos	The position where this should be drawn before its internal translation is added.
		 */
		paint: function( gDrawingContext, pos, borderWidth)
		{
			var x = this.bounds.x + pos.x,
				y = this.bounds.y + pos.y;

/*			if ( this.bkCol != null )
			{
				g.setColor( this.bkCol );
				g.fillRect( x, y - g.getFontMetrics().getAscent(), this.bounds.width, this.bounds.height );
			}
*/

			gDrawingContext.textBaseline = "top";
			gDrawingContext.textAlign = "left";
			if( this.font)
				gDrawingContext.font = this.font.canvas;
			if( this.font.color)
				gDrawingContext.fillStyle = this.font.color; 
			gDrawingContext.fillText( this.text, x + borderWidth + JMI.script.HTMLText.MARGIN, y + borderWidth + JMI.script.HTMLText.MARGIN);
		}
	};
	
	return TextToken;
}());

JMI.script.FormatToken = (function() {

    var FormatToken = function() {
    	this.aMax = 0;
    	this.dMax = 0;
    	this.width = 0;
    };

	FormatToken.prototype = {
		constructor: JMI.script.FormatToken
	};

	return FormatToken;
}());

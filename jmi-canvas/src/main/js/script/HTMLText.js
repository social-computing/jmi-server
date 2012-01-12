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

	var bound,//:Rectangle;
		text, //:String;
		inCol, //:ColorTransform;
		font, //:TextFormat;
		oneLine, //:Boolean;
		outCol, //:ColorTransform;
		blur, //:int;
		rounded; //:int;
		
	var HTMLText = function() {
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
			var font = base.getFont( FONT_VAL, zone.props);
			this.font = font.getTextFormat( zone.props);
			this.inCol = base.getColor( IN_COL_VAL, zone.props);
			this.outCol = base.getColor( OUT_COL_VAL, zone.props);
			//this.m_blur = base.getInt(BLUR_COL_VAL, zone.props);
			this.blur = parseInt( base.parseString(BLUR_COL_VAL, zone.props )[0]);
			this.rounded = base.getInt(ROUNDED_COL_VAL, zone.props);
			var color = base.getValue( HTMLText.TEXT_COL_VAL, zone.props);
			if( color != null)
				this.font.color = color.color;
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
        getHText: function( applet, s, zone, transfo, center, supCtr, textKey) // throws UnsupportedEncodingException
        {
            var htmlTxt = null;
            var data = zone.m_datas[ textKey ];
            
            if ( center == null )	center = supCtr;
            
            if ( data == null )
            {
				htmlTxt = new JMI.script.HTMLText();
				htmlTxt.text = parseString2( TEXT_VAL, zone.props, true );
                
                if ( htmlTxt.text.length> 0)
                {
					htmlTxt.init( this, zone);
                    htmlTxt.updateBounds( applet);
                    htmlTxt.setTextBnds( applet.size, getFlags( zone.props), zone.flags ,transfo, supCtr, center );
                }
            }
            else
            {
                htmlTxt = data;
                htmlTxt.setTextBnds( applet.size, getFlags( zone.props), zone.flags, transfo, supCtr, center );
            }
            
            return htmlTxt;
        },
        
        /**
         * Evaluate this bounding box using margins.
		 * 
         * @param g			The graphics used to retrieve the font metrics.
         * @param htmlText	A string of text with or without HTML tags to parse.
         */
        updateBounds: function( applet) {
			 // The text exists!
			oneLine = true;
			if ( this.text.length > 0)
			 {
			 	// TODO portage
/*				 var textField:TextField = new TextField();
				 textField.defaultTextFormat = m_font;
				 textField.multiline = true;
				 textField.htmlText = this.m_text;
				 textField.autoSize = TextFieldAutoSize.LEFT;
				 textField.antiAliasType = AntiAliasType.ADVANCED;
				 this.bounds = new com.socialcomputing.jmi.script.Rectangle();
				 this.bounds.copy( textField.getBounds( applet));
				 if( this.m_outCol != null) {
					 this.bounds.width += (BORDER_WIDTH*2);
					 this.bounds.height += (BORDER_WIDTH*2);
				 }					 
				 
				 try {
					 textField.getLineText(1);
					 m_oneLine = false;
				 }catch( e : RangeError){
					 m_oneLine = true;
				 }*/
			 }
       },
        
        /**
         * Draws this using a cardinal direction.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         * @param dir	One of the following directions:<br>
         * NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST.
         */
        drawText: function( s, size, dir) {
            var pos = new JMI.script.Point();
            var xMax = size.width - bounds.width - 1,
                yMax = size.height - bounds.height - 1;
            
            if (( dir & 8)!= 0)         pos.x = xMax;
            else if (( dir & 4)!= 0)    pos.x = xMax >> 1;
            if (( dir & 2)!= 0)         pos.y = yMax;
            else if (( dir & 1)!= 0)    pos.y = yMax >> 1;
            
            drawText3( s, size, pos );
        },
        
        /**
         * Draws this using a previously evaluated position.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         */
        drawText2: function( s, size) {
            drawText3( s, size, new JMI.script.Point( bounds.x, bounds.y ));
        },
        
        /**
         * Draws this at a position.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         * @param pos	Where to draw this.
         */
        drawText3: function( s, size, pos) {
			var borderWidth = 0;
			if ( m_outCol != null )
				borderWidth = 2;
            if ( m_inCol != null )
            {
				s.graphics.lineStyle();
				if ( m_outCol != null ) {
					//s.graphics.lineStyle( 2, m_outCol.color);
					s.graphics.beginFill(m_outCol.color);
					if( m_rounded == -1)
						s.graphics.drawRect(pos.x, pos.y, bounds.width, bounds.height);
					else
						s.graphics.drawRoundRect(pos.x, pos.y, bounds.width, bounds.height, m_rounded, m_rounded);
					s.graphics.endFill();
				}
				// TODO gérer le gradient dans les swatchs
/*				var colors:Array = [m_inCol.color, 0x000000];
				var alphas:Array = [1, 1];
				var ratios:Array = [0x00, 0xFF];
				var matr:Matrix = new Matrix();
				matr.createGradientBox(bounds.width, bounds.height * 2, Math.PI / 2, pos.x, pos.y);
                s.graphics.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios, matr, SpreadMethod.PAD);
*/				s.graphics.beginFill(m_inCol.color);
				if( m_rounded == -1)
					s.graphics.drawRect(pos.x+borderWidth, pos.y+borderWidth, bounds.width-2*borderWidth, bounds.height-2*borderWidth);
				else
                	s.graphics.drawRoundRect(pos.x+borderWidth, pos.y+borderWidth, bounds.width-2*borderWidth, bounds.height-2*borderWidth, m_rounded, m_rounded);
                s.graphics.endFill();
           }
			
			paint( s, pos, borderWidth);

			if ( m_oneLine && m_inCol == null) // draw reflection only for one line boxes
            {
            	// TODO portage
/*                var white:ColorTransform = new ColorTransform();
                white.color = 0xFFFFFF;
                s.graphics.beginFill(white.color, 0.2);
				s.graphics.lineStyle();
                s.graphics.drawRoundRect(pos.x+borderWidth, pos.y+3+borderWidth, bounds.width-2*borderWidth, (bounds.height/3)-2*borderWidth, 5, 5);
                s.graphics.endFill();*/
            }
            bounds.x  = pos.x;
            bounds.y  = pos.y;
        },
        
		/**
		 * Paint this at a specified location.
		 * The inner location is used to offset the fonts.
		 * @param g		The graphics to draw in.
		 * @param pos	The position where this should be drawn before its internal translation is added.
		 */
		paint: function( s, pos, borderWidth) {
			// TODO Portage
			/*var textField:TextField = new TextField();
			if( m_blur != -1) {
				textField.filters = [new BlurFilter(m_blur, m_blur)];
			}*/
			if( font != null)
				textField.defaultTextFormat = font;
			textField.multiline = true;
			textField.htmlText = m_text;
			textField.x = pos.x + borderWidth;
			textField.y = pos.y + borderWidth; 
			textField.autoSize = TextFieldAutoSize.LEFT;
			textField.antiAliasType = AntiAliasType.ADVANCED;
			textField.border = false;
			ImageUtil.drawTextField( textField, s.graphics);
			//s.addChild(textField);
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
            var isFloat = Base.isEnabled( flags, FLOAT_BIT );
            var dx = 0,
                dy	= 0,
                x = center.x,
                y = center.y,
                w   = bounds.width,
                h   = bounds.height,
                w2  = w >> 1,
                h2  = h >> 1;
            
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
            
            bounds.x = x;
            bounds.y = y;
        }
 	};
	
	return HTMLText;
}());
		
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

   // static properties/methods aren't inherited in AS3
// http://help.adobe.com/en_US/ActionScript/3.0_ProgrammingAS3/WS5b3ccc516d4fbf351e63e3d118a9b90204-7fcd.html
// http://www.davidarno.org/2009/09/25/actionscript-3-inheritance-developers-beware/
JMI.script.HTMLText.isEnabled = function( flags, bit) {
    return Base.isEnabled(flags, bit); // TODO portage
}
        

package com.socialcomputing.wps.script{
    import com.socialcomputing.wps.components.PlanComponent;
	import com.socialcomputing.wps.util.controls.ImageUtil;
    import com.socialcomputing.wps.util.shapes.RectangleUtil;
    
    import flash.display.GradientType;
    import flash.display.Graphics;
    import flash.display.SpreadMethod;
    import flash.display.Sprite;
    import flash.filters.BlurFilter;
    import flash.geom.ColorTransform;
    import flash.geom.Matrix;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    import flash.text.AntiAliasType;
    import flash.text.Font;
    import flash.text.FontStyle;
    import flash.text.TextField;
    import flash.text.TextFieldAutoSize;
    import flash.text.TextFormat;
    import flash.text.TextLineMetrics;
    import flash.text.engine.ElementFormat;
    import flash.text.engine.FontDescription;
    import flash.text.engine.FontMetrics;
    
    import org.flexunit.internals.namespaces.classInternal;
    
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
		public static const BORDER_WIDTH:int= 2;
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
		 * Index of the text Color prop in VContainer table
		 */
		public static const BLUR_COL_VAL:int= 6;
		
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
        public var m_bounds:Rectangle;

		private var _m_text:String;
		private var _m_inCol:ColorTransform;
		private var _m_font:TextFormat;
		private var _m_oneLine:Boolean;
		private var _m_outCol:ColorTransform;
		private var _m_blur:Boolean;
		
		
		public function get m_oneLine():Boolean
		{
			return _m_oneLine;
		}

		public function set m_oneLine(value:Boolean):void
		{
			_m_oneLine = value;
		}

		public function get m_font():TextFormat
		{
			return _m_font;
		}

		public function set m_font(value:TextFormat):void
		{
			_m_font = value;
		}

		public function get m_text():String
		{
			return _m_text;
		}

		public function set m_text(value:String):void
		{
			_m_text = value;
		}

		public function get m_blur():Boolean
		{
			return _m_blur;
		}

		public function set m_blur(value:Boolean):void
		{
			_m_blur = value;
		}

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
        
        /**
         */
        public function HTMLText(){
		}
        
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
        public function initValues(inCol:ColorTransform, outCol:ColorTransform, textCol:int, fontSiz:int, fontStl:int, fontNam:String, blur:Boolean, flags:int, margin:Insets):void
        {
            m_inCol     = inCol;
            m_outCol    = outCol;
			m_blur		= blur;
			
			this._m_font = new TextFormat();
			this._m_font.font = fontNam;
			this._m_font.size = fontSiz;
			this._m_font.color = textCol;
			if (( fontStl & BOLD )!= 0)  this._m_font.bold = true;
			if (( fontStl & ITALIC )!= 0) this._m_font.italic = true;
			
			this.m_font.leftMargin = margin.left;
			this.m_font.rightMargin = margin.right;
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
        public function getHText( applet:PlanComponent, s:Sprite, zone:ActiveZone, transfo:Transfo, center:Point, supCtr:Point, textKey:HTMLText):HTMLText // throws UnsupportedEncodingException
        {
            var htmlTxt:HTMLText= null;
            var data:Object= zone.m_datas[ textKey ];
            
            if ( center == null )	center = supCtr;
            
            if ( data == null )
            {
				htmlTxt = new HTMLText();
				htmlTxt.m_text = parseString2( TEXT_VAL, zone.m_props, true );
                
                if ( htmlTxt.m_text.length> 0)
                {
                    var font:FontX= getFont( FONT_VAL, zone.m_props);
                    
                    htmlTxt.m_inCol = getColor( IN_COL_VAL, zone.m_props);
                    htmlTxt.m_outCol = getColor( OUT_COL_VAL, zone.m_props);
					htmlTxt.m_blur = getBool(BLUR_COL_VAL, zone.m_props);
					htmlTxt._m_font = font.getTextFormat( zone.m_props);
					var color:ColorX = getValue( HTMLText.TEXT_COL_VAL, zone.m_props) as ColorX;
					if( color != null)
						htmlTxt._m_font.color = color.m_color;
                    
                    htmlTxt.updateBounds( applet);
                    htmlTxt.setTextBnds( applet.size, getFlags( zone.m_props), zone.m_flags ,transfo, supCtr, center );
                }
            }
            else
            {
                htmlTxt = data as HTMLText;
                htmlTxt.setTextBnds( applet.size, getFlags( zone.m_props), zone.m_flags, transfo, supCtr, center );
            }
            
            return htmlTxt;
        }
        
        /**
         * Evaluate this bounding box using margins.
		 * 
         * @param g			The graphics used to retrieve the font metrics.
         * @param htmlText	A string of text with or without HTML tags to parse.
         */
        public function updateBounds( applet:PlanComponent):void {
			 // The text exists!
			m_oneLine = true;
			 if ( this.m_text.length > 0)
			 {
				 var textField:TextField = new TextField();
				 textField.defaultTextFormat = m_font;
				 textField.multiline = true;
				 textField.htmlText = this.m_text;
				 textField.autoSize = TextFieldAutoSize.LEFT;
				 textField.antiAliasType = AntiAliasType.ADVANCED;
				 this.m_bounds = new Rectangle();
				 RectangleUtil.copy( this.m_bounds, textField.getBounds( applet));
				 if( this.m_outCol != null) {
					 this.m_bounds.width += (BORDER_WIDTH*2);
					 this.m_bounds.height += (BORDER_WIDTH*2);
				 }					 
				 
				 try {
					 textField.getLineText(1);
					 m_oneLine = false;
				 }catch( e : RangeError){
					 m_oneLine = true;
				 }
			 }
        }
        
        /**
         * Draws this using a cardinal direction.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         * @param dir	One of the following directions:<br>
         * NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST.
         */
        public function drawText( s:Sprite, size:Dimension, dir:int):void {
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
        public function drawText2( s:Sprite, size:Dimension):void {
            drawText3( s, size, new Point( m_bounds.x, m_bounds.y ));
        }
        
        /**
         * Draws this at a position.
         * @param g		Graphics to draw in.
         * @param size	Size of the Window to draw in.
         * @param pos	Where to draw this.
         */
        protected function drawText3( s:Sprite, size:Dimension, pos:Point):void {
			var borderWidth:int = 0;
			if ( m_outCol != null )
				borderWidth = 2;
            if ( m_inCol != null )
            {
				if ( m_outCol != null ) {
					//s.graphics.lineStyle( 2, m_outCol.color);
					s.graphics.beginFill(m_outCol.color);
					s.graphics.drawRoundRect(pos.x, pos.y, m_bounds.width, m_bounds.height, 10, 10);
				}
				else
					s.graphics.lineStyle();
				// TODO gérer le gradient dans les swatchs
/*				var colors:Array = [m_inCol.color, 0x000000];
				var alphas:Array = [1, 1];
				var ratios:Array = [0x00, 0xFF];
				var matr:Matrix = new Matrix();
				matr.createGradientBox(m_bounds.width, m_bounds.height * 2, Math.PI / 2, pos.x, pos.y);
                s.graphics.beginGradientFill(GradientType.LINEAR, colors, alphas, ratios, matr, SpreadMethod.PAD);
*/				s.graphics.beginFill(m_inCol.color);
                s.graphics.drawRoundRect(pos.x+borderWidth, pos.y+borderWidth, m_bounds.width-2*borderWidth, m_bounds.height-2*borderWidth, 10, 10);
                s.graphics.endFill();
           }
			
			paint( s, pos, borderWidth);

			if ( m_oneLine && m_inCol == null) // draw reflection only for one line boxes
            {
                var white:ColorTransform = new ColorTransform();
                white.color = 0xFFFFFF;
                s.graphics.beginFill(white.color, 0.2);
				s.graphics.lineStyle();
                s.graphics.drawRoundRect(pos.x+borderWidth, pos.y+3+borderWidth, m_bounds.width-2*borderWidth, (m_bounds.height/3)-2*borderWidth, 5, 5);
                s.graphics.endFill();
            }
            m_bounds.x  = pos.x;
            m_bounds.y  = pos.y;
        }
        
		/**
		 * Paint this at a specified location.
		 * The inner location is used to offset the fonts.
		 * @param g		The graphics to draw in.
		 * @param pos	The position where this should be drawn before its internal translation is added.
		 */
		public function paint( s:Sprite, pos:Point, borderWidth:int):void {
			var textField:TextField = new TextField();
			if( m_blur) {
				textField.filters = [new BlurFilter(6, 6)];
			}
/*			if ( false && !m_oneLine && m_inCol != null )
			{
				text.background = true;
				text.backgroundColor = m_inCol.color;
			}
*/			
			//text.text = m_text;
			if( m_font != null)
				textField.defaultTextFormat = m_font;
			textField.multiline = true;
			textField.htmlText = m_text;
			textField.x = pos.x + borderWidth;
			textField.y = pos.y + borderWidth; 
			textField.autoSize = TextFieldAutoSize.LEFT;
			textField.antiAliasType = AntiAliasType.ADVANCED;
			textField.border = false;
			ImageUtil.drawTextField( textField, s.graphics);
			//s.addChild(textField);
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
        
       // static properties/methods aren't inherited in AS3
        // http://help.adobe.com/en_US/ActionScript/3.0_ProgrammingAS3/WS5b3ccc516d4fbf351e63e3d118a9b90204-7fcd.html
        // http://www.davidarno.org/2009/09/25/actionscript-3-inheritance-developers-beware/
        public static function isEnabled( flags:int, bit:int):Boolean {
            return Base.isEnabled(flags, bit);
        }
            
    }
    
}
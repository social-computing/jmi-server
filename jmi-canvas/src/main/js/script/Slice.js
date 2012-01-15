JMI.namespace("script.Slice");

/*
 * <p>Title: Slice</p>
 * <p>Description: An elementary slice to fill with graphics.<br>
 * </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.Slice =  (function() {
    
	var Slice = function() {
	    /**
         * A buffer to store the HTMLText associated with this if it has one.
         * //:HTMLText;
         */
	    this.htmlTxt = null;

		JMI.script.Base.call( this);
	};
	
	Slice.prototype = {
		constructor: JMI.script.Slice,
		
		/*
         * Draws this slice on a Graphics.
         * It's position and size is evaluated by its parent satellite and transfo.
         * The inner parts (when they exists) are drawn with respect to the following order:
         * <ul>
         * <li>IN_COL_VAL : The background of the shape.</li>
         * <li>OUT_COL_VAL : The outside of the shape.</li>
         * <li>IMAGE_VAL : The bitmap (icon).</li>
         * <li>TEXT_VAL : The text, standard or HTML.</li>
         * </ul>
         * @param applet        The Applet that owns this.
         * @param s             A sprite to draw this in.
         * @param supZone       The parent zone of this zone.
         * @param zone          The zone that holds the properties used by this slice.
         * @param satShp        The shape of this Slice
         * @param satCtr        This slice center.
         * @param supCtr        This parent satellite center.
         */
        paint: function(applet, s, supZone, zone, satShp, satCtr, supCtr) {
            var text = this.getText( JMI.script.Slice.TEXT_VAL, zone.props );
            
            var transfo = this.getTransfo( JMI.script.Slice.TRANSFO_VAL, zone.props );
            
            // Draw a satellite with primitives
            if(this.isDefined(JMI.script.Slice.IN_COL_VAL) || this.isDefined(JMI.script.Slice.OUT_COL_VAL)) {
                satShp.paint(s, supZone, zone, this, transfo, satCtr );
            }
            
            // Draw a satellite's image it is set
            if(isDefined(JMI.script.Slice.IMAGE_VAL)) {
                var imageNam = parseString(JMI.script.Slice.IMAGE_VAL, zone.props )[0];
                if (imageNam != null) {
                    satShp.drawImage(applet, s, supZone, imageNam, transfo, satCtr);
                }
            }
            
            if(text != null) {
                if (JMI.script.HTMLText.isEnabled(text.getFlags(zone.props), JMI.script.HTMLText.URL_BIT)) {
                    m_htmlTxt = null;
                    var textUrls  = text.parseString(JMI.script.HTMLText.TEXT_VAL, zone.props);
                    var t , hTxt = "";
                    var i = 0;
                    // TODO portage
                    /*  var textLoader:URLLoader = new URLLoader();
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
                            supCtr  = supZone.m_restSwh.satellites[0].m_shape.getCenter( supZone );
                            m_htmlTxt.setTextBnds( applet.size, getFlags( zone.m_props), zone.m_flags ,transfo, satCtr, supCtr );
                            m_htmlTxt.drawText( s, applet.size, text.getFlags( zone.m_props )>> 16);//HTMLText.SOUTH_WEST );
                            applet.renderShape( s, m_htmlTxt.m_bounds.width, m_htmlTxt.m_bounds.height, new Point(m_htmlTxt.m_bounds.x, m_htmlTxt.m_bounds.y));
                        }
                    });
                    for each ( var url:String in textUrls)
                    {
                        var textReq:URLRequest = new URLRequest(url);
                        textLoader.load(textReq);
                    }*/
                }
                else {
                    supCtr = supZone.restSwatch.satellites[0]._shape.getCenter(supZone);
                    var htmlTxt = text.getHText(applet, s, zone, transfo, satCtr, supCtr, text);
                    
                    if (htmlTxt != null && htmlTxt._text.length > 0) {
                        htmlTxt.drawText2(s, applet.size);
                        zone.m_datas[text] = htmlTxt;
                    }
                }
            }
        },
        
        /**
         * Return wether a point is inside this slice
         * 
         * If there is a border or a background, tests if the point is inside the shape.
         * TODO : Else, if there is a Text or HTMLText, tests if the point is inside the text bounds. 
         * TODO : The image are not considered because of the complexity (retrieving size), but should...
         * 
         * @param applet        The PlanComponent that owns this.
         * @param g             A graphics to get the FontMetrics used by this.
         * @param supZone       The parent of zone or null if it have none.
         * @param zone          The zone that holds the properties used by this slice.
         * @param satShp        This slice shape, get from its satellite.
         * @param satCtr        This slice center, get from its satellite.
         * @param supCtr        This parent satellite center.
         * @param pos           A point position to test.
         * 
         * @return              True if the cursor's position is inside this slice, false otherwise
         */
        contains: function(planComponent, g, supZone, zone, satShp, satCtr, supCtr, pos){
            var transfo = getTransfo(JMI.script.Slice.TRANSFO_VAL, zone.props);
            
            if(supZone == null) supZone = zone;
            
            if((isDefined(JMI.script.Slice.IN_COL_VAL ) || isDefined(JMI.script.Slice.OUT_COL_VAL)) &&
                satShp.contains(g, supZone, transfo, satCtr, pos)) {
                return true;
            }
            
            var text = getText(JMI.script.Slice.TEXT_VAL, zone.props);
            if (text != null) {
                // TODO : null à remplacer
                var htmlTxt = text.getHText(planComponent, null, zone, transfo, satCtr, supCtr, text);
                return htmlTxt != null ? htmlTxt._bounds.contains(pos._x, pos._y): false;
            }
            return false;
        },
        
        /*
         * Sets this bounds by updating an already created Rectangle.
         * If there is a border or a background, the bounds of the shape are considered.
         * If there is a Text or HTMLText, the bounds of the text are considered.
         * The image bounds are not considered because of the complexity (retrieving size), but they should...
         * 
         * @param applet        The Applet that owns this.
         * @param g             A graphics to get the FontMetrics used by this.
         * @param supZone       The parent of zone or null if it have none.
         * @param zone          The zone that holds the properties used by this slice.
         * @param satShp        This slice shape, get from its satellite.
         * @param satCtr        This slice center, get from its satellite.
         * @param supCtr        This parent satellite center.
         * @param bounds        A Rectangle to merge with this bounds.
         */
        setBounds: function(applet, g, supZone, zone, satShp, satCtr, supCtr, bounds) {
            var transfo = this.getTransfo(JMI.script.Slice.TRANSFO_VAL, zone.props);
            
            if (supZone == null) supZone = zone;
            
            try {
                if ( this.isDefined(JMI.script.Slice.IN_COL_VAL) || this.isDefined(JMI.script.Slice.OUT_COL_VAL)) {
                    satShp.setBounds(g, supZone, transfo, satCtr, bounds);
                }
            }
            catch (e) {
                var errorMessage = "getCenter supZone=" + supZone;
                if (supZone != null) {
                    var points = satShp.getValue(JMI.script.ShapeX.POLYGON_VAL, supZone.props);
                    errorMessage += " zName=" + supZone.props[ "NAME" ] + " pKey=" + satShp.containers[JMI.script.ShapeX.POLYGON_VAL]._value + " pnts=" + points + " p[0]=" + points[0];
                }
                throw(new Error(errorMessage));
            }
        
            var text = this.getText( JMI.script.Slice.TEXT_VAL, zone.props);
            if (text != null) {
                if (JMI.script.HTMLText.isEnabled(text.getFlags(zone.props), JMI.script.HTMLText.URL_BIT)) {
                    if (this.htmlTxt != null) bounds.copy(this.htmlTxt._bounds);
                }
                else {
                    supCtr = supZone.restSwatch.satellites[0].shapex.getCenter(supZone);
                    var htmlTxt;
                    
                    // TODO null à remplacer par Sprite
                    // TODO voir à quel propriété se réfère bounds
                    htmlTxt = text.getHText(applet, null, zone, transfo, satCtr, supCtr, text);
                    if (htmlTxt != null && htmlTxt._text.length > 0) {
                        bounds.merge(htmlTxt._bounds);
                    }
                }
            }
        }

	};
	
	// Héritage
	for (var element in JMI.script.Base.prototype ) {
		if( !Slice.prototype[element])
			Slice.prototype[element] = JMI.script.Base.prototype[element];
	}
	
	return Slice;
}());


// Constants
/**
 * Index of the Transfo prop in VContainer table.
 * If it doesn't exists this Slice will have the same shape as its satellite.
 */
JMI.script.Slice.TRANSFO_VAL = 1;

/**
 * Index of the inside Color prop in VContainer table.
 */
JMI.script.Slice.IN_COL_VAL = 2;

/**
 * Index of the border Color prop in VContainer table.
 */
JMI.script.Slice.OUT_COL_VAL = 3;

/**
 * Index of the image URL prop in VContainer table.
 */
JMI.script.Slice.IMAGE_VAL = 4;

/**
 * Index of the text (HTML or not) prop in VContainer table.
 */
JMI.script.Slice.TEXT_VAL = 5;

/**
 * Index of the text (HTML or not) prop in VContainer table.
 */
JMI.script.Slice.ALPHA_VAL = 6;

/**
 * Index of the delay (ms) prop for a tooltip Slice.
 */
JMI.script.Slice.DELAY_VAL = 7;

/**
 * Index of the length (ms) prop for a tooltip Slice.
 */
JMI.script.Slice.LENGTH_VAL = 8;

/**
 * True if this Slice is visible.
 * Probably a useless bit...
 */
JMI.script.Slice.VISIBLE_BIT = 0x01;
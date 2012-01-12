JMI.namespace("script.LinkZone");

/*
 * <p>Title: LinkZone</p>
 * <p>Description: A graphical Link holding properties.<br>
 * The link is tied to 2 Nodes (BagZones) or just one if it's a fake one.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.LinkZone = (function() {  

	/*
     * Creates a Link between two Places.
     * @param from  The Place to start from.
     * @param to    The Place to end to.
     */
	var LinkZone = function(from, to) {
	    /*
         * The Place from which the link start.
         * JMI.script.BagZone
         */
		this._from = null;
		/*
         * The Place to which the link end.
         * JMI.script.BagZone
         */
		this._to = null;
		/*
         * Index of Bagzone (temporary during JSON desrialization).
         */
		this._fromIndex  = from;
		this._toIndex    = to;
	};
	
	LinkZone.prototype = {
		constructor: JMI.script.LinkZone,
		/*
         * Perform precalc and basic initialisation.
         * Initialize the BBox of this zone, the max BBox of all zones and the BBox of the plan.
         * It also perform basic initialisation through inheritance.
         * @param applet    WPSApplet owning this.
         * @param g         A graphics compatible with the one that will be used for painting.
         * @param isFirst   True if init called for the first time.
         */
        init: function(applet, s, isFirst) {
            super.init(applet, s, isFirst );// TODO portage
            
            this._parent = null;
            if (!Base.isEnabled(this._flags, FAKEFROM_BIT | FAKETO_BIT)) {
                this._bounds = this._restSwh.getBounds(applet, s.graphics, this, false);
                if (this._curSwh != null) {
                    this._bounds = this._bounds.union(this._curSwh.getBounds(applet, s.graphics, this, true));
                }
                this._bounds.inflate(2, 2);
                
                var w = this._bounds.width,
                    h = this._bounds.height;
                var maxBox = applet.plan.maxBox;
                
                if (w > maxBox.width)     maxBox.width    = w;
                if (h > maxBox.height)    maxBox.height   = h;
                
                this._bounds = this._bounds.intersection(applet.size.toRectangle());
            }
        },
        
        /*
         * Paint this Link when the cursor hover it.
         * This is achieved by blitting the basic background, drawing the cur swatch base satellites and the two places over it.
         * Then the Tip and Sel satellites are drawn over the places.
         * These operations are made in another buffer that is finaly blitted on the Applet's Graphics.
         * This reduce CPU overhead and avoid screen flickering.
         * 
         * @param applet    WPSApplet owning this zone.
         */
        paintCur: function(applet){
            if((this._flags & INVISIBLE_BIT) != 0) return;
            
            ImageUtil.clear(applet.curDrawingSurface);
            this._curSwh.paint(applet, applet.curDrawingSurface, this, true, true, Satellite.BASE_TYP, true);
            
            this._from.paint(applet, applet.curDrawingSurface, false, true, Satellite.ALL_TYP, true);
            this._to.paint(applet, applet.curDrawingSurface, false, true, Satellite.ALL_TYP, true);
            this._curSwh.paint(applet, applet.curDrawingSurface, this, true, true, Satellite.TIP_TYP, true);
            this._curSwh.paint(applet, applet.curDrawingSurface, this, true, true, Satellite.SEL_TYP, true);
        
            applet.renderShape(applet.curDrawingSurface, this._bounds.width, this._bounds.height, new JMI.script.Point(this._bounds.x, this._bounds.y));
        }
	};
	return LinkZone;
}());

// Constants
/*
 * Bit indicating that this is a Link whose 'From' node is a fake one (out of the window).
 */
JMI.script.LinkZone.FAKEFROM_BIT = 0x01;

/*
 * Bit indicating that this is a Link whose 'To' node is a fake one (out of the window).
 */
JMI.script.LinkZone.FAKETO_BIT = 0x02;
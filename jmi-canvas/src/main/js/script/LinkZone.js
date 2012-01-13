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
	var LinkZone = function() {
	    /*
         * The Place from which the link start.
         * JMI.script.BagZone
         */
		this.from = JMI.script.BagZone;
		/*
         * The Place to which the link end.
         * JMI.script.BagZone
         */
		this.to = JMI.script.BagZone;

        JMI.script.ActiveZone.call( this);
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
            //super.init(applet, s, isFirst );// TODO portage
            
            this.parent = null;
            if (!JMI.script.Base.isEnabled(this.flags, JMI.script.LinkZone.FAKEFROM_BIT | JMI.script.LinkZone.FAKETO_BIT)) {
                this.bounds = this.restSwatch.getBounds(applet, s.graphics, this, false);
                if (this.curSwatch != null) {
                    this.bounds = this.bounds.union(this.curSwatch.getBounds(applet, s.graphics, this, true));
                }
                this.bounds.inflate(2, 2);
                
                var w = this.bounds.width,
                    h = this.bounds.height;
                var maxBox = applet.plan.maxBox;
                
                if (w > maxBox.width)     maxBox.width    = w;
                if (h > maxBox.height)    maxBox.height   = h;
                
                this.bounds = this.bounds.intersection(applet.size.toRectangle());
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
            if((this.flags & JMI.script.ActiveZone.INVISIBLE_BIT) != 0) return;
            
            ImageUtil.clear(applet.curDrawingSurface);
            this.curSwatch.paint(applet, applet.curDrawingSurface, this, true, true, Satellite.BASE_TYP, true);
            
            this.from.paint(applet, applet.curDrawingSurface, false, true, Satellite.ALL_TYP, true);
            this.to.paint(applet, applet.curDrawingSurface, false, true, Satellite.ALL_TYP, true);
            this.curSwatch.paint(applet, applet.curDrawingSurface, this, true, true, Satellite.TIP_TYP, true);
            this.curSwatch.paint(applet, applet.curDrawingSurface, this, true, true, Satellite.SEL_TYP, true);
        
            applet.renderShape(applet.curDrawingSurface, this.bounds.width, this.bounds.height, new JMI.script.Point(this.bounds.x, this.bounds.y));
        }
	};
	
	// Héritage
	for (var element in JMI.script.ActiveZone.prototype ) {
		LinkZone.prototype[element] = JMI.script.ActiveZone.prototype[element];
	}
	
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
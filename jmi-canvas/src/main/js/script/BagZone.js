JMI.namespace("script.BagZone");

/*
 * <p>Title: BagZone</p>
 * <p>Description: A graphical Place holding properties.<br>
 * This is a parent zone including subzones. It's also a "place".</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.BagZone = (function() {
	
	var BagZone = function(subZones) {
	    // Clusterized subZones table.
	    //:Array = null;
        this._subZones = []; 
    
        /*
         * Initial angular direction of Satellites.
         * It change depending on the distance to window borders.
         * The main zone will start at this angle. Evaluated during init.
         */
        this._dir = null;
    
        /*
         * Angular step between two subZones.
         * Basicaly 2PI/subs, but in case of half circle (borders) it's PI/subs!
         * Evaluated during init.
         */
        this._stp = null;
    
        /*
         * Creates a BagZone with its subZones.
         * Its size and position should be initialized by setting the "_SCALE" (float) and "_VERTICES" (Point[1]) properties;
         * @param subs  A subZone table whose parent is this.
         */
		this._subZones = subZones;
	};
	
	BagZone.prototype = {
		constructor: JMI.script.BagZone,

        /*
         * Perform precalc and basic initialisation.
         * Initialize the BBox of this zone, the max BBox of all zones and the BBox of the plan.
         * Eval the border distance and set the initial direction and side bits.
         * It also perform basic initialisation through inheritance.
         * 
         * @param applet    applet owning this zone.
         * @param s         A Sprite on which this must be painted.
         * @param isFirst   True if this init is the first one. False if this is a "refresh" init.
         */
        init: function(applet, s, isFirst) {
            var i,
                nbSubZones = this._subZones != null ? this._subZones.length : 0;
            // TODO super.init(applet, s, isFirst);
            var restSwhBounds, curSwhBounds; 
            
            // First time init
            if (isFirst) {
                this._parent = null;
        
                if (nbSubZones > 0) this._stp = JMI.script.Base.Pi2 / (nbSubZones + 1);
                this._dir = 10.0;
        
                for (i = 0 ; i < nbSubZones ; i ++) {
                    this._subZones[i]._parent = this;
                }
        
                restSwhBounds = this._restSwh.getBounds(applet, s.graphics, this, false);          
                curSwhBounds  = this._curSwh.getBounds(applet, s.graphics, this, true);
                this._bounds = restSwhBounds.union(curSwhBounds);
                
                var isLeft = this._bounds.x < 0;
        
                if (nbSubZones > 0) {
                    // float dir = 0.f,
                    var stp = 0.25 * Base.Pi2;
                    // isLeft || isRight
                    if (isLeft || (this._bounds.x + this._bounds.width > applet.width)) {
                        // TODO : portage, regarder si le décalage de bit fonctionne avec le même opérateur
                        this._stp = JMI.script.Base.Pi2 / (nbSubZones << 1);
        
                        if (isLeft) {
                            this._dir = -stp;
                            this._subZones[nbSubZones - 1]._flags |= SIDE_BIT | LEFT_BIT;
                        }
                        else {
                            this._dir = stp;
                            this._subZones[nbSubZones - 1]._flags |= SIDE_BIT;
                        }
                    }
                }
                
                // isLeft || isRight
                if (isLeft || (this._bounds.x + this._bounds.width > applet.width)) {
                    this._flags |= isLeft ? SIDE_BIT | LEFT_BIT : SIDE_BIT;
                }
            }
        
            restSwhBounds     = this._restSwh.getBounds(applet, s.graphics, this, false);
            var win           = applet.plan._prevBox.union(restSwhBounds);
            curSwhBounds      = this._curSwh.getBounds(applet, s.graphics, this, true);
            this._bounds     = restSwhBounds.union(curSwhBounds);
            
            /*
            this.m_bounds = this.m_restSwh.getBounds(applet, s.graphics, this, false);
            var win:Rectangle = applet.plan.prevBox.union(m_bounds);
            this.m_bounds = this.m_bounds.union(m_curSwh.getBounds(applet, s.graphics, this, true));
            */
            
            if (win.y > this._bounds.y) {
                win.height += win.y - this._bounds.y;
                win.y      = this._bounds.y;
            }
            else if (win.y + win.height < this._bounds.y + this._bounds.height) {
                win.height  = this._bounds.y + this._bounds.height - win.y;
            }
        
            applet.plan._prevBox = win;
            this._bounds.inflate(2, 2);
        
            var w = this._bounds.width;
            var h = this._bounds.height;
            var maxBox = applet.plan._maxBox;
        
            if (w > maxBox.width)  maxBox.width  = w;
            if (h > maxBox.height) maxBox.height = h;
        
            this._bounds = this._bounds.intersection(applet.size.toRectangle());
        },
        
        /**
         * Paint this Place when the cursor hover it.
         * This is achieved by blitting the basic background and drawing the cur swatch over it.
         * Those operation are made in another buffer that is finaly blitted on the Applet's Graphics.
         * This reduce CPU overhead and avoid screen flickering.
         * 
         * @param applet    applet owning this zone.
         */
        paintCur: function(applet) {
            // Copy backDrawingSurface hovered zone to curDrawingSurface
            // Use this method instead of ImageUtil.copy to improve performance  
            var backBitmap = new BitmapData(this._bounds.width + this._bounds.x, this._bounds.height + this._bounds.y);
            backBitmap.draw(applet.backDrawingSurface, null, null, null, 
                            new JMI.script.Rectangle(this._bounds.x, this._bounds.y, 
                                                     this._bounds.width + this._bounds.x, this._bounds.height + this._bounds.y));
            applet.curDrawingSurface.graphics.beginBitmapFill(backBitmap);
            applet.curDrawingSurface.graphics.drawRect(this._bounds.x, this._bounds.y, this._bounds.width, this._bounds.height);
            applet.curDrawingSurface.graphics.endFill();
            
            this._curSwh.paint(applet, applet.curDrawingSurface, this, true, true, JMI.script.Satellite.ALL_TYP, true);
            /*
                bufGfx.translate( m_bounds.x, m_bounds.y );
                g.setClip( m_bounds.x, m_bounds.y, m_bounds.width, m_bounds.height );
                g.drawImage( applet.m_plan.blitBuf, m_bounds.x, m_bounds.y, null );
            */
            applet.renderShape(applet.curDrawingSurface, this._bounds.width, this._bounds.height, 
                               new JMI.script.Point(this._bounds.x, this._bounds.y));
        }

	};
	
	return BagZone;
}());
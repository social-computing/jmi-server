JMI.namespace("script.Polygon"); 

JMI.script.Polygon = (function() {

    var Polygon = function() {
        this.xpoints = []; //:Vector.<int>;
        this.ypoints = []; //:Vector.<int>;
        this.npoints = 0;  //:int;
        this.bounds = null; //JMI.script.Rectangle;
    };
	
	Polygon.prototype = {
		constructor: JMI.script.Polygon,
		
		/*
         * Resets this <code>Polygon</code> object to an empty polygon.
         * The coordinate arrays and the data in them are left untouched
         * but the number of points is reset to zero to mark the old
         * vertex data as invalid and to start accumulating new vertex
         * data at the beginning.
         * All internally-cached data relating to the old vertices
         * are discarded.
         * Note that since the coordinate arrays from before the reset
         * are reused, creating a new empty <code>Polygon</code> might
         * be more memory efficient than resetting the current one if
         * the number of vertices in the new polygon data is significantly
         * smaller than the number of vertices in the data from before the
         * reset.
         * @see java.awt.Polygon#invalidate
         * @since 1.4
         */
        reset: function() {
            this.npoints = 0;
            this.bounds = null;
            return this;
        },
        
        /*
         * Invalidates or flushes any internally-cached data that depends
         * on the vertex coordinates of this <code>Polygon</code>.
         * This method should be called after any direct manipulation
         * of the coordinates in the <code>xpoints</code> or
         * <code>ypoints</code> arrays to avoid inconsistent results
         * from methods such as <code>getBounds</code> or <code>contains</code>
         * that might cache data from earlier computations relating to
         * the vertex coordinates.
         * @see java.awt.Polygon#getBounds
         * @since 1.4
         */
        invalidate: function() {
            this.bounds = null;
            return this;
        },
        
        /*
         * Resizes the bounding box to accomodate the specified coordinates.
         * @param x,&nbsp;y the specified coordinates
         */
        updateBounds: function(x, y) {
            if (x < this.bounds.x) {
                this.bounds.width = this.bounds.width + (this.bounds.x - x);
                this.bounds.x = x;
            }
            else {
                this.bounds.width = Math.max(this.bounds.width, x - this.bounds.x);
                // bounds.x = bounds.x;
            }
        
            if (y < this.bounds.y) {
                this.bounds.height = this.bounds.height + (this.bounds.y - y);
                this.bounds.y = y;
            }
            else {
                this.bounds.height = Math.max(this.bounds.height, y - this.bounds.y);
                // bounds.y = bounds.y;
            }
            
            return this;
        },
        
        /*
         * Appends the specified coordinates to this <code>Polygon</code>.
         * <p>
         * If an operation that calculates the bounding box of this
         * <code>Polygon</code> has already been performed, such as
         * <code>getBounds</code> or <code>contains</code>, then this
         * method updates the bounding box.
         * @param x the specified x coordinate
         * @param y the specified y coordinate
         * @see java.awt.Polygon#getBounds
         * @see java.awt.Polygon#contains
         */
        addPoint: function(x, y) {
            this.xpoints[this.npoints] = x;
            this.ypoints[this.npoints] = y;
            this.npoints++;
            if (this.bounds != null) {
                this.updateBounds(x, y);
            }
            
            return this;
        },
        
        /*
         * Gets the bounding box of this <code>Polygon</code>.
         * The bounding box is the smallest {@link Rectangle} whose
         * sides are parallel to the x and y axes of the coordinate space, 
         * and can completely contain the <code>Polygon</code>.
         * 
         * @return a <code>Rectangle</code> that defines the bounds of this <code>Polygon</code>.
         */
        getBounds: function() {
            if (this.npoints == 0) {
                return new JMI.script.Rectangle();
            }
            if (this.bounds == null) {
                this.calculateBounds(this.xpoints, this.ypoints, this.npoints);
            }
            return this.bounds;
        },
        
        
        /*
         * Determines whether the specified {@link Point} is inside this <code>Polygon</code>.
         * 
         * @param p the specified <code>Point</code> to be tested
         * 
         * @return <code>true</code> if the <code>Polygon</code> contains the
         * <code>Point</code>; <code>false</code> otherwise.
         */
        contains: function(p) {
            return this.containsCrossing(p);
        },

        /**
         * Determines if the specified coordinates are inside this <code>Polygon</code>. 
         * It uses the crossing number test for a point in a polygon
         * 
         * @param p the specified <code>Point</code> to be tested
         * 
         * @return <code>true</code> if the <code>Polygon</code> contains the
         * specified point; <code>false</code> otherwise.
         */
        containsCrossing: function(p) {
        
            // The crossing number counter
            var cn = 0;
            
            // Loop through all edges of the polygon
            for (var i = 0 ; i < this.npoints ; i++) {
                
                var i2 = ((i + 1) == this.npoints) ? 0 : i + 1;
                
                // Edge from p[i] to p[i+1]
                if (((this.ypoints[i] <= p.y) && (this.ypoints[i2] > p.y))    // An upward crossing
                    || ((this.ypoints[i] > p.y) && (this.ypoints[i2] <= p.y))) {      // Or a downward crossing
                    
                    // Compute the actual edge-ray intersect x-coordinate
                    var vt = (p._y - this.ypoints[i]) / (this.ypoints[i2] - this.ypoints[i]);
                    if (p._x < this.xpoints[i] + vt * (this.xpoints[i2] - this.xpoints[i])) // p.x < intersect
                        ++cn;   // a valid crossing of y = p.y right of p.x
                }
            }
            // TODO : portage : à changer
            return new Boolean(cn & 1);    // 0 if even (out), and 1 if odd (in)
        },
        
        /**
         * Calculates the bounding box of the points passed to the constructor.
         * Sets <code>bounds</code> to the result.
         * 
         * @param xpoints[] array of <i>x</i> coordinates
         * @param ypoints[] array of <i>y</i> coordinates
         * @param npoints the total number of points
         */
        calculateBounds: function(xpoints, ypoints, npoints) {
            var minPoint = new JMI.script.Point(xpoints[0], ypoints[0]);
            var maxPoint = new JMI.script.Point(xpoints[0], ypoints[0]);
            
            for (var i = 1 ; i < npoints ; i++) {
                minPoint.x = Math.min(minPoint.x, xpoints[i]);
                minPoint.y = Math.min(minPoint.y, ypoints[i]);
                maxPoint.x = Math.max(maxPoint.x, xpoints[i]);
                maxPoint.y = Math.max(maxPoint.y, ypoints[i]);
            }
            
            this.bounds = new Rectangle(minPoint.x, minPoint.y,
                                        maxPoint.x - minPoint.x,
                                        maxPoint.y - minPoint.y);
            return this;
        }
	};
	
	return Polygon;
}());
















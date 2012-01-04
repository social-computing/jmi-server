JMI.namespace("com.socialcomputing.jmi.script.Polygon") = (function() {

	var npoints, //:int;
		xpoints, //:Vector.<int>;
		ypoints, //:Vector.<int>;

		bounds = com.socialcomputing.jmi.script.Rectangle,
		Constr;
	
	Constr = function() {
	    xpoints = new Array();
	    ypoints = new Array();
	}
	Constr.prototype = {
		constructor: com.socialcomputing.jmi.script.Polygon,
		version: "2.0"
	}
	return Constr;
}());

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
com.socialcomputing.jmi.script.Polygon.prototype.reset = function() {
    npoints = 0;
    bounds = null;
}

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
com.socialcomputing.jmi.script.Polygon.prototype.invalidate = function() {
    bounds = null;
}

/*
 * Resizes the bounding box to accomodate the specified coordinates.
 * @param x,&nbsp;y the specified coordinates
 */
com.socialcomputing.jmi.script.Polygon.prototype.updateBounds = function(x, y) {
    if (x < bounds.x) {
        bounds.width = bounds.width + (bounds.x - x);
        bounds.x = x;
    }
    else {
        bounds.width = Math.max(bounds.width, x - bounds.x);
        // bounds.x = bounds.x;
    }

    if (y < bounds.y) {
        bounds.height = bounds.height + (bounds.y - y);
        bounds.y = y;
    }
    else {
        bounds.height = Math.max(bounds.height, y - bounds.y);
        // bounds.y = bounds.y;
    }
}

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
com.socialcomputing.jmi.script.Polygon.prototype.addPoint = function(x, y) {
    xpoints[npoints] = x;
    ypoints[npoints] = y;
    npoints++;
    if (bounds != null) {
        updateBounds(x, y);
    }
}

/*
 * Gets the bounding box of this <code>Polygon</code>.
 * The bounding box is the smallest {@link Rectangle} whose
 * sides are parallel to the x and y axes of the coordinate space, 
 * and can completely contain the <code>Polygon</code>.
 * 
 * @return a <code>Rectangle</code> that defines the bounds of this <code>Polygon</code>.
 */
com.socialcomputing.jmi.script.Polygon.prototype.getBounds = function() {
    if (npoints == 0) {
        return new com.socialcomputing.jmi.script.Rectangle( 0, 0, 0, 0);
    }
    if (bounds == null) {
        this.calculateBounds(xpoints, ypoints, npoints);
    }
	return this.bounds;
}


/*
 * Determines whether the specified {@link Point} is inside this <code>Polygon</code>.
 * 
 * @param p the specified <code>Point</code> to be tested
 * 
 * @return <code>true</code> if the <code>Polygon</code> contains the
 * <code>Point</code>; <code>false</code> otherwise.
 */
com.socialcomputing.jmi.script.Polygon.prototype.contains = function(p) {
    return containsCrossing(p);
}


/**
 * Determines if the specified coordinates are inside this <code>Polygon</code>. 
 * It uses the crossing number test for a point in a polygon
 * 
 * @param p the specified <code>Point</code> to be tested
 * 
 * @return <code>true</code> if the <code>Polygon</code> contains the
 * specified point; <code>false</code> otherwise.
 */
com.socialcomputing.jmi.script.Polygon.prototype.containsCrossing = function(p) {

	// The crossing number counter
	var cn = 0;
	
	// Loop through all edges of the polygon
	for (var i = 0 ; i < this.npoints ; i++) {
		
		var i2 = ((i + 1) == this.npoints) ? 0 : i + 1;
		
		// Edge from p[i] to p[i+1]
		if (((ypoints[i] <= p.y) && (ypoints[i2] > p.y))    // An upward crossing
			|| ((ypoints[i] > p.y) && (ypoints[i2] <= p.y))) {      // Or a downward crossing
			
			// Compute the actual edge-ray intersect x-coordinate
			var vt = (p.y - ypoints[i]) / (ypoints[i2] - ypoints[i]);
			if (p.x < xpoints[i] + vt * (xpoints[i2] - xpoints[i])) // p.x < intersect
				++cn;   // a valid crossing of y = p.y right of p.x
		}
	}
	return new Boolean(cn & 1);    // 0 if even (out), and 1 if odd (in)
}

/**
 * Calculates the bounding box of the points passed to the constructor.
 * Sets <code>bounds</code> to the result.
 * 
 * @param xpoints[] array of <i>x</i> coordinates
 * @param ypoints[] array of <i>y</i> coordinates
 * @param npoints the total number of points
 */
com.socialcomputing.jmi.script.Polygon.prototype.calculateBounds = function(xpoints, ypoints, npoints) {
	var minPoint = new com.socialcomputing.jmi.script.Point(xpoints[0], ypoints[0]);
	var maxPoint = new com.socialcomputing.jmi.script.Point(xpoints[0], ypoints[0]);
	
	for (var i = 1 ; i < npoints ; i++) {
		minPoint.x = Math.min(minPoint.x, xpoints[i]);
		minPoint.y = Math.min(minPoint.y, ypoints[i]);
		maxPoint.x = Math.max(maxPoint.x, xpoints[i]);
		maxPoint.y = Math.max(maxPoint.y, ypoints[i]);
	}
	
	this.bounds = new Rectangle(minPoint.x,	minPoint.y,
		                        maxPoint.x - minPoint.x,
		                        maxPoint.y - minPoint.y);
}

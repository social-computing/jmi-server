package com.socialcomputing.wps.script {
    import flash.geom.Point;
    import flash.geom.Rectangle;

public class Polygon {

    public var npoints:int;


    public var xpoints:Vector.<int>;

    public var ypoints:Vector.<int>;

    protected var bounds:Rectangle;

    /**
     * Creates an empty polygon.
     */
    public function Polygon() {
        xpoints = new Vector.<int>();
        ypoints = new Vector.<int>();
    }

    /**
     * Constructs and initializes a <code>Polygon</code> from the specified
     * parameters.
     * @param xpoints an array of <i>x</i> coordinates
     * @param ypoints an array of <i>y</i> coordinates
     * @param npoints the total number of points in the
     * <code>Polygon</code>
     * @exception NegativeArraySizeException if the value of
     * <code>npoints</code> is negative.
     * @exception IndexOutOfBoundsException if <code>npoints</code> is
     * greater than the length of <code>xpoints</code>
     * or the length of <code>ypoints</code>.
     * @exception NullPointerException if <code>xpoints</code> or
     * <code>ypoints</code> is <code>null</code>.
     */
    /*public function Polygon(xpoints:Array, ypoints:Array, npoints:int) {
        if (npoints > xpoints.length || npoints > ypoints.length) {
            throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
        }
        this.npoints = npoints;
        this.xpoints = new int[npoints];
        this.ypoints = new int[npoints];
        System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
        System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
    }*/

    /**
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
    public function reset():void {
        npoints = 0;
        bounds = null;
    }

    /**
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
    public function invalidate():void {
        bounds = null;
    }

    /*
     * Calculates the bounding box of the points passed to the constructor.
     * Sets <code>bounds</code> to the result.
     * @param xpoints[] array of <i>x</i> coordinates
     * @param ypoints[] array of <i>y</i> coordinates
     * @param npoints the total number of points
     */
    public function calculateBounds(xpoints:Vector.<int>, ypoints:Vector.<int>, npoints:int):void {
		
		var minPoint:Point = new Point(xpoints[0], ypoints[0]);
		var maxPoint:Point = new Point(xpoints[0], ypoints[0]);
		

		for (var i:int = 1 ; i < npoints ; i++) {
			minPoint.x = Math.min(minPoint.x, xpoints[i]);
			minPoint.y = Math.min(minPoint.y, xpoints[i]);
			maxPoint.x = Math.max(maxPoint.x, xpoints[i]);
			maxPoint.y = Math.max(maxPoint.y, xpoints[i]);
		}
		
		this.bounds = new Rectangle(minPoint.x,
			                        minPoint.y,
									maxPoint.x - minPoint.x,
									maxPoint.y - minPoint.y);
		/*
        var boundsMinX:uint= uint.MAX_VALUE;
        var boundsMinY:uint= uint.MAX_VALUE;
        var boundsMaxX:uint= uint.MIN_VALUE;
        var boundsMaxY:uint= uint.MIN_VALUE;

        for (var i:int = 0 ; i < npoints ; i++) {
            var x:int= xpoints[i];
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);
            var y:int= ypoints[i];
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }
        bounds = new Rectangle(boundsMinX, boundsMinY,
                boundsMaxX - boundsMinX,
                boundsMaxY - boundsMinY);
		*/
    }

    /*
     * Resizes the bounding box to accomodate the specified coordinates.
     * @param x,&nbsp;y the specified coordinates
     */
    public function updateBounds(x:int, y:int):void {
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

    /**
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
    public function addPoint(x:int, y:int):void {
        xpoints[npoints] = x;
        ypoints[npoints] = y;
        npoints++;
        if (bounds != null) {
            updateBounds(x, y);
        }
    }

    /**
     * Gets the bounding box of this <code>Polygon</code>.
     * The bounding box is the smallest {@link Rectangle} whose
     * sides are parallel to the x and y axes of the
     * coordinate space, and can completely contain the <code>Polygon</code>.
     * @return a <code>Rectangle</code> that defines the bounds of this
     * <code>Polygon</code>.
     * @since JDK1.1
     */
    public function getBounds():Rectangle {
        return getBoundingBox();
    }

    /**
     * Returns the bounds of this <code>Polygon</code>.
     * @return the bounds of this <code>Polygon</code>.
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getBounds()</code>.
     */
    public function getBoundingBox():Rectangle {
            if (npoints == 0) {
                return new Rectangle();
            }
            if (bounds == null) {
                calculateBounds(xpoints, ypoints, npoints);
            }
			// TODO ???
            //return bounds.getBounds();
			return bounds;
        }

    /**
     * Determines whether the specified {@link Point} is inside this
     * <code>Polygon</code>.
     * @param p the specified <code>Point</code> to be tested
     * @return <code>true</code> if the <code>Polygon</code> contains the
     * <code>Point</code>; <code>false</code> otherwise.
     * @see #contains(double, double)
     */
    public function contains2(p:Point):Boolean {
        return contains(p.x, p.y);
    }

    /**
     * Determines if the specified coordinates are inside this
     * <code>Polygon</code>. For the definition of
     * <i>insideness</i>, see the class comments of {@link Shape}.
     * @param x the specified x coordinate
     * @param y the specified y coordinate
     * @return <code>true</code> if the <code>Polygon</code> contains the
     * specified coordinates; <code>false</code> otherwise.
     */
    public function contains(x:Number, y:Number):Boolean {
        if (npoints <= 2|| !getBoundingBox().contains(x, y)) {
            return false;
        }
        var hits:int= 0;

        var lastx:int= xpoints[npoints - 1];
        var lasty:int= ypoints[npoints - 1];
        var curx:int, cury:int;

        // Walk the edges of the polygon
        for (var i:int= 0; i < npoints; lastx = curx, lasty = cury, i++) {
            curx = xpoints[i];
            cury = ypoints[i];

            if (cury == lasty) {
                continue;
            }

            var leftx:int;
            if (curx < lastx) {
                if (x >= lastx) {
                    continue;
                }
                leftx = curx;
            } else {
                if (x >= curx) {
                    continue;
                }
                leftx = lastx;
            }

            var test1:Number, test2:Number;
            if (cury < lasty) {
                if (y < cury || y >= lasty) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - curx;
                test2 = y - cury;
            } else {
                if (y < lasty || y >= cury) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - lastx;
                test2 = y - lasty;
            }

            if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
    }

}
}
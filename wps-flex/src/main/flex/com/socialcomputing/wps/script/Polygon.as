package com.socialcomputing.wps.script {
    import flash.geom.Rectangle;

public class Polygon {

    public var npoints:int;


    public var xpoints:Array;

    public var ypoints:Array;

    protected var bounds:Rectangle;

    /**
     * Creates an empty polygon.
     */
    /*public function Polygon() {
        xpoints = new int[4];
        ypoints = new int[4];
    }*/

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
    public function Polygon(xpoints:Array, ypoints:Array, npoints:int) {
        if (npoints > xpoints.length || npoints > ypoints.length) {
            throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
        }
        this.npoints = npoints;
        this.xpoints = new int[npoints];
        this.ypoints = new int[npoints];
        System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
        System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
    }

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

    /**
     * Translates the vertices of the <code>Polygon</code> by
     * <code>deltaX</code> along the x axis and by
     * <code>deltaY</code> along the y axis.
     * @param deltaX the amount to translate along the <i>x</i> axis
     * @param deltaY the amount to translate along the <i>y</i> axis
     * @since JDK1.1
     */
    public function translate(deltaX:int, deltaY:int):void {
        for (var i:int= 0; i < npoints; i++) {
            xpoints[i] += deltaX;
            ypoints[i] += deltaY;
        }
        if (bounds != null) {
            bounds.translate(deltaX, deltaY);
        }
    }

    /*
     * Calculates the bounding box of the points passed to the constructor.
     * Sets <code>bounds</code> to the result.
     * @param xpoints[] array of <i>x</i> coordinates
     * @param ypoints[] array of <i>y</i> coordinates
     * @param npoints the total number of points
     */
    function calculateBounds(xpoints:Array, ypoints:Array, npoints:int):void {
        var boundsMinX:int= Integer.MAX_VALUE;
        var boundsMinY:int= Integer.MAX_VALUE;
        var boundsMaxX:int= Integer.MIN_VALUE;
        var boundsMaxY:int= Integer.MIN_VALUE;

        for (var i:int= 0; i < npoints; i++) {
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
    }

    /*
     * Resizes the bounding box to accomodate the specified coordinates.
     * @param x,&nbsp;y the specified coordinates
     */
    function updateBounds(x:int, y:int):void {
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
        if (npoints == xpoints.length) {
            var tmp:Array;

            tmp = new int[npoints * 2];
            System.arraycopy(xpoints, 0, tmp, 0, npoints);
            xpoints = tmp;

            tmp = new int[npoints * 2];
            System.arraycopy(ypoints, 0, tmp, 0, npoints);
            ypoints = tmp;
        }
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
            return bounds.getBounds();
        }

    /**
     * Determines whether the specified {@link Point} is inside this
     * <code>Polygon</code>.
     * @param p the specified <code>Point</code> to be tested
     * @return <code>true</code> if the <code>Polygon</code> contains the
     * <code>Point</code>; <code>false</code> otherwise.
     * @see #contains(double, double)
     */
    public function contains(p:Point):Boolean {
        return contains(p.x, p.y);
    }

    /**
     * Determines whether the specified coordinates are inside this
     * <code>Polygon</code>.
     * <p>
     * @param x the specified x coordinate to be tested
     * @param y the specified y coordinate to be tested
     * @return <code>true</code> if this <code>Polygon</code> contains
     y coordinate to be tested
     * @return <code>true</code> if this <code>Polygon</code> contains
     * the specified coordinates, (<i>x</i>,&nbsp;<i>y</i>);
     * <code>false</code> otherwise.
     * @see #contains(double, double)
     * @deprecated As of JDK version 1.1,
     * replaced by <code>contains(int, int)</code>.
     */
    public function inside(x:int, y:int):Boolean {
            return contains(double(x), double(y));
        }

    /**
     * Returns the high precision bounding box of the {@link Shape}.
     * @return a {@link Rectangle2D} that precisely
     * bounds the <code>Shape</code>.
     */
    public function getBounds2D():Rectangle2D {
        return getBounds();
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
        var curx:int, cury;

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

            var test1:Number, test2;
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

    private function getCrossings(xlo:Number, ylo:Number,
            xhi:Number, yhi:Number):Crossings {
        var cross:Crossings= new Crossings.EvenOdd(xlo, ylo, xhi, yhi);
        var lastx:int= xpoints[npoints - 1];
        var lasty:int= ypoints[npoints - 1];
        var curx:int, cury;

        // Walk the edges of the polygon
        for (var i:int= 0; i < npoints; i++) {
            curx = xpoints[i];
            cury = ypoints[i];
            if (cross.accumulateLine(lastx, lasty, curx, cury)) {
                return null;
            }
            lastx = curx;
            lasty = cury;
        }

        return cross;
    }

    /**
     * Tests if a specified {@link Point2D} is inside the boundary of this
     * <code>Polygon</code>.
     * @param p a specified <code>Point2D</code>
     * @return <code>true</code> if this <code>Polygon</code> contains the
     * specified <code>Point2D</code>; <code>false</code>
     * otherwise.
     * @see #contains(double, double)
     */
    public function contains(p:Point2D):Boolean {
        return contains(p.getX(), p.getY());
    }

    /**
     * Tests if the interior of this <code>Polygon</code> intersects the
     * interior of a specified set of rectangular coordinates.
     * @param x the x coordinate of the specified rectangular
     * shape's top-left corner
     * @param y the y coordinate of the specified rectangular
     * shape's top-left corner
     * @param w the width of the specified rectangular shape
     * @param h the height of the specified rectangular shape
     * @return <code>true</code> if the interior of this
     * <code>Polygon</code> and the interior of the
     * specified set of rectangular
     * coordinates intersect each other;
     * <code>false</code> otherwise
     * @since 1.2
     */
    public function intersects(x:Number, y:Number, w:Number, h:Number):Boolean {
        if (npoints <= 0|| !getBoundingBox().intersects(x, y, w, h)) {
            return false;
        }

        var cross:Crossings= getCrossings(x, y, x+w, y+h);
        return (cross == null || !cross.isEmpty());
    }

    /**
     * Tests if the interior of this <code>Polygon</code> intersects the
     * interior of a specified <code>Rectangle2D</code>.
     * @param r a specified <code>Rectangle2D</code>
     * @return <code>true</code> if this <code>Polygon</code> and the
     * interior of the specified <code>Rectangle2D</code>
     * intersect each other; <code>false</code>
     * otherwise.
     */
    public function intersects(r:Rectangle2D):Boolean {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Tests if the interior of this <code>Polygon</code> entirely
     * contains the specified set of rectangular coordinates.
     * @param x the x coordinate of the top-left corner of the
     * specified set of rectangular coordinates
     * @param y the y coordinate of the top-left corner of the
     * specified set of rectangular coordinates
     * @param w the width of the set of rectangular coordinates
     * @param h the height of the set of rectangular coordinates
     * @return <code>true</code> if this <code>Polygon</code> entirely
     * contains the specified set of rectangular
     * coordinates; <code>false</code> otherwise
     * @since 1.2
     */
    public function contains(x:Number, y:Number, w:Number, h:Number):Boolean {
        if (npoints <= 0|| !getBoundingBox().intersects(x, y, w, h)) {
            return false;
        }

        var cross:Crossings= getCrossings(x, y, x+w, y+h);
        return (cross != null && cross.covers(y, y+h));
    }

    /**
     * Tests if the interior of this <code>Polygon</code> entirely
     * contains the specified <code>Rectangle2D</code>.
     * @param r the specified <code>Rectangle2D</code>
     * @return <code>true</code> if this <code>Polygon</code> entirely
     * contains the specified <code>Rectangle2D</code>;
     * <code>false</code> otherwise.
     * @see #contains(double, double, double, double)
     */
    public function contains(r:Rectangle2D):Boolean {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Returns an iterator object that iterates along the boundary of this
     * <code>Polygon</code> and provides access to the geometry
     * of the outline of this <code>Polygon</code>. An optional
     * {@link AffineTransform} can be specified so that the coordinates
     * returned in the iteration are transformed accordingly.
     * @param at an optional <code>AffineTransform</code> to be applied to the
     * coordinates as they are returned in the iteration, or
     * <code>null</code> if untransformed coordinates are desired
     * @return a {@link PathIterator} object that provides access to the
     * geometry of this <code>Polygon</code>.
     */
    public function getPathIterator(at:AffineTransform):PathIterator {
        return new PolygonPathIterator(this, at);
    }

    /**
     * Returns an iterator object that iterates along the boundary of
     * the <code>Shape</code> and provides access to the geometry of the
     * outline of the <code>Shape</code>. Only SEG_MOVETO, SEG_LINETO, and
     * SEG_CLOSE point types are returned by the iterator.
     * Since polygons are already flat, the <code>flatness</code> parameter
     * is ignored. An optional <code>AffineTransform</code> can be specified
     * in which case the coordinates returned in the iteration are transformed
     * accordingly.
     * @param at an optional <code>AffineTransform</code> to be applied to the
     * coordinates as they are returned in the iteration, or
     * <code>null</code> if untransformed coordinates are desired
     * @param flatness the maximum amount that the control points
     * for a given curve can vary from colinear before a subdivided
     * curve is replaced by a straight line connecting the
     * endpoints. Since polygons are already flat the
     * <code>flatness</code> parameter is ignored.
     * @return a <code>PathIterator</code> object that provides access to the
     * <code>Shape</code> object's geometry.
     */
    public function getPathIterator(at:AffineTransform, flatness:Number):PathIterator {
        return getPathIterator(at);
    }
}

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

    
internal class PolygonPathIterator implements PathIterator {
        var poly:Polygon;
        var transform:AffineTransform;
        var index:int;

        public function PolygonPathIterator(pg:Polygon, at:AffineTransform) {
            poly = pg;
            transform = at;
            if (pg.npoints == 0) {
                // Prevent a spurious SEG_CLOSE segment
                index = 1;
            }
        }

        /**
         * Returns the winding rule for determining the interior of the
         * path.
         * @return an integer representing the current winding rule.
         * @see PathIterator#WIND_NON_ZERO
         */
        public function getWindingRule():int {
            return WIND_EVEN_ODD;
        }

        /**
         * Tests if there are more points to read.
         * @return <code>true</code> if there are more points to read;
         * <code>false</code> otherwise.
         */
        public function isDone():Boolean {
            return index > poly.npoints;
        }

        /**
         * Moves the iterator forwards, along the primary direction of
         * traversal, to the next segment of the path when there are
         * more points in that direction.
         */
        public function next():void {
            index++;
        }

        /**
         * Returns the coordinates and type of the current path segment in
         * the iteration.
         * The return value is the path segment type:
         * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE.
         * A <code>float</code> array of length 2 must be passed in and
         * can be used to store the coordinates of the point(s).
         * Each point is stored as a pair of <code>float</code> x,&nbsp;y
         * coordinates. SEG_MOVETO and SEG_LINETO types return one
         * point, and SEG_CLOSE does not return any points.
         * @param coords a <code>float</code> array that specifies the
         * coordinates of the point(s)
         * @return an integer representing the type and coordinates of the
         * current path segment.
         * @see PathIterator#SEG_MOVETO
         * @see PathIterator#SEG_LINETO
         * @see PathIterator#SEG_CLOSE
         */
        public function currentSegment(coords:Array):int {
            if (index >= poly.npoints) {
                return SEG_CLOSE;
            }
            coords[0] = poly.xpoints[index];
            coords[1] = poly.ypoints[index];
            if (transform != null) {
                transform.transform(coords, 0, coords, 0, 1);
            }
            return (index == 0? SEG_MOVETO : SEG_LINETO);
        }

        /**
         * Returns the coordinates and type of the current path segment in
         * the iteration.
         * The return value is the path segment type:
         * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE.
         * A <code>double</code> array of length 2 must be passed in and
         * can be used to store the coordinates of the point(s).
         * Each point is stored as a pair of <code>double</code> x,&nbsp;y
         * coordinates.
         * SEG_MOVETO and SEG_LINETO types return one point,
         * and SEG_CLOSE does not return any points.
         * @param coords a <code>double</code> array that specifies the
         * coordinates of the point(s)
         * @return an integer representing the type and coordinates of the
         * current path segment.
         * @see PathIterator#SEG_MOVETO
         * @see PathIterator#SEG_LINETO
         * @see PathIterator#SEG_CLOSE
         */
        public function currentSegment(coords:Array):int {
            if (index >= poly.npoints) {
                return SEG_CLOSE;
            }
            coords[0] = poly.xpoints[index];
            coords[1] = poly.ypoints[index];
            if (transform != null) {
                transform.transform(coords, 0, coords, 0, 1);
            }
            return (index == 0? SEG_MOVETO : SEG_LINETO);
        }
    }
}
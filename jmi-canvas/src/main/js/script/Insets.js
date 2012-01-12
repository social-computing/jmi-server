JMI.namespace("script.Insets");

/*
 * An <code>Insets</code> object is a representation of the borders 
 * of a container. It specifies the space that a container must leave 
 * at each of its edges. The space can be a border, a blank space, or 
 * a title. 
 *
 */
JMI.script.Insets = (function() {
	
	var Insets = function( top, left, bottom, right) {
        /*
         * The inset from the top.
         * This value is added to the Top of the rectangle
         * to yield a new location for the Top.
         */
	    this._top = top;

        /*
         * The inset from the left.
         * This value is added to the Left of the rectangle
         * to yield a new location for the Left edge.
         */
	    this._left = left;

        /*
         * The inset from the bottom.
         * This value is subtracted from the Bottom of the rectangle
         * to yield a new location for the Bottom.
         */	    
	    this._bottom = bottom;
	    
	    /*
         * The inset from the right.
         * This value is subtracted from the Right of the rectangle
         * to yield a new location for the Right edge.
         *
         */
	    this._right = right;
	};
	
	Insets.prototype = {
		constructor: JMI.script.Insets,
		
		/*
         * Creates and initializes a new <code>Insets</code> object with the 
         * specified top, left, bottom, and right insets. 
         * 
         * @param       top   the inset from the top.
         * @param       left   the inset from the left.
         * @param       bottom   the inset from the bottom.
         * @param       right   the inset from the right.
         */
        init: function(top, left, bottom, right){
            this._top    = top;
            this._left   = left;
            this._bottom = bottom;
            this._right  = right;
        },
        
        /*
         * Set top, left, bottom, and right to the specified values
         *
         * @param       top   the inset from the top.
         * @param       left   the inset from the left.
         * @param       bottom   the inset from the bottom.
         * @param       right   the inset from the right.
         * @since 1.5
         */
        set: function(top, left, bottom, right) {
            this._top = top;
            this._left = left;
            this._bottom = bottom;
            this._right = right;
        }
	};
	
	return Insets;
}());
JMI.namespace("script.Rectangle");

JMI.script.Rectangle = (function() {
	var Rectangle = function(x, y, width, height) {
	    this._x = x;
	    this._y = y;
	    this._width = width;
	    this._height = height;
	};
	
	Rectangle.prototype = {
		constructor: JMI.script.Rectangle,
		
		/*
         * Merge 2 Rectangles.
         * If the dest Rectangle has one null dimension then copy the source on it.
         * 
         * @param src   Source Rectangle.
         */
        merge: function(src) {
            if(this._width * this._height != 0) {
                this.union(src);
            }
            else {
                this.copy(src);
            }
            return this;
        },
        
        union: function(src) {
            this._x = Math.min(this._x, src._x);
            this._y = Math.min(this._y, src._y);
            this._width = this._width + src._width - Math.min(this._x + this._width - src._x, src._x + src._width - this._x);
            this._height = this._height + src._height - Math.min(this._y + this._height - src._y, src._y + src._height - this._y);
            return this;
        },
        
        /*
         * Copy the source <code>Rectangle</code> properties to the destination <code>Rectangle</code>
         * 
         * @param src Source Rectangle to copy the values from
         * 
         */
        copy: function(src) {
            this._x = src._x;
            this._y = src._y;
            this._width = src._width;
            this._height = src._height;
            return this;
        }
        		
	};
	
	return Rectangle;
}());
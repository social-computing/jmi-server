JMI.namespace("script.Rectangle");

JMI.script.Rectangle = (function() {
	var Rectangle = function(x, y, width, height) {
	    this.x = x;
	    this.y = y;
	    this.width = width;
	    this.height = height;
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
            if(this.width * this.height != 0) {
                this.union(src);
            }
            else {
                this.copy(src);
            }
            return this;
        },
        
        union: function(src) {
            this.x = Math.min(this.x, src._x);
            this.y = Math.min(this.y, src._y);
            this.width = this.width + src._width - Math.min(this.x + this.width - src._x, src._x + src._width - this.x);
            this.height = this.height + src._height - Math.min(this.y + this.height - src._y, src._y + src._height - this.y);
            return this;
        },
        
        /*
         * Copy the source <code>Rectangle</code> properties to the destination <code>Rectangle</code>
         * 
         * @param src Source Rectangle to copy the values from
         * 
         */
        copy: function(src) {
            this.x = src._x;
            this.y = src._y;
            this.width = src._width;
            this.height = src._height;
            return this;
        }
        		
	};
	
	return Rectangle;
}());
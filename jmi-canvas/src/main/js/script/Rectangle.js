JMI.namespace("script.Rectangle");

JMI.script.Rectangle = (function() {
	var Rectangle = function(x, y, width, height) {
	    // If no arguments are given in the constructor call, initialize the instance with 0 values;
	    if(arguments.length == 0) {
	        x = 0;
	        y = 0;
	        width = 0;
	        height = 0;
	    }
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
            this.x = Math.min(this.x, src.x);
            this.y = Math.min(this.y, src.y);
            this.width = this.width + src.width - Math.min(this.x + this.width - src.x, src.x + src.width - this.x);
            this.height = this.height + src.height - Math.min(this.y + this.height - src.y, src.y + src.height - this.y);
            return this;
        },
        
        /*
         * Copy the source <code>Rectangle</code> properties to the destination <code>Rectangle</code>
         * 
         * @param src Source Rectangle to copy the values from
         * 
         */
        copy: function(src) {
            this.x = src.x;
            this.y = src.y;
            this.width = src.width;
            this.height = src.height;
            return this;
        },
        
        inflate: function(dx, dy) {
			this.x -= dx;
			this.width += 2 * dx;
			this.y -= dy;
			this.height += 2 * dy;
            return this;
        }
        
        		
	};
	
	return Rectangle;
}());
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
        // if 2 arguments are given : assume it is the width and height of the rectangle
        else if(arguments.length == 2) {
            width = x;
            height = y;
            x = 0;
            y = 0;
        }  
        // if 1 argument is given : assume it is another rectangle to copy values from
        if(arguments.length == 1 && x instanceof JMI.script.Rectangle) {
            this.copy(x);
        }
        else { 
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    };


	Rectangle.prototype = {
		constructor: JMI.script.Rectangle,
		
		/*
         * Merge 2 Rectangles.
         * If the current Rectangle has one null dimension then copy the source on it.
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
            var x1 = Math.min(this.x, src.x),
                y1 = Math.min(this.y, src.y),
                x2 = Math.max(this.x + this.width, src.x + src.width),
                y2 = Math.max(this.y + this.height, src.y + src.height);
            this.x = x1;
            this.y = y1;
            this.width  = x2 - x1;
            this.height = y2 - y1;
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
        },
        
        add: function(x, y) {
			this.width += x;
			this.height += y;
            return this;
        },
        
        intersection: function(src) {
			var	x1 = Math.max(this.x, src.x),
				y1 = Math.max(this.y, src.y),
				x2 = Math.min(this.x + this.width, src.x + src.width),
				y2 = Math.min(this.y + this.height, src.y + src.height);
			return new JMI.script.Rectangle(x1, y1, x2 - x1, y2 - y1);
        },

        contains: function(x, y) {
        	return x >= this.x && x <= this.x + this.width && y >= this.y && y <= y + this.height;
        }
	};
	
	return Rectangle;
}());
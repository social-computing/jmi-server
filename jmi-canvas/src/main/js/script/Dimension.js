JMI.namespace("script.Dimension");

JMI.script.Dimension = (function() {
	
	var Dimension = function(width, height) {
	    // If no arguments are given in the constructor call, initialize the instance with 0 values;
	    if(arguments.length === 0) {
            width = 0;
            height = 0;
        }
        // if 1 argument is given : assume it is & rectangle to copy values from
        if(arguments.length == 1 && width instanceof JMI.script.Rectangle) {
            height = width.height;
            width  = width.width;
        }
		this.width  = width;
		this.height = height;
	};
	
    Dimension.prototype = {
        constructor: JMI.script.Dimension,
		
		resize: function(d) {
			return new JMI.script.Dimension(Math.max(this.width, d.width), Math.max(this.height, d.height));
		},
		
		/**
         * Create a <code>Rectangle</code> instance with height and width of the current Dimension
         */
		toRectangle: function(){
            return new JMI.script.Rectangle(this.width, this.height);
        }
	};
	
	return Dimension;
}());
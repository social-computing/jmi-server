JMI.namespace("script.Dimension");

JMI.script.Dimension = (function() {
	
	var width, height;
	
	var Dimension = function(w, h) {
		width = w;
		height = h;
	};
	
    Dimension.prototype = {
        contructor: JMI.script.Dimension,
		
		resize: function( d) {
			return new JMI.script.Dimension(Math.max(this.width, d.width), Math.max(this.height, d.height));
		}
	};
	
	return Dimension;
}());

// static
/**
 * Create a <code>Rectangle</code> instance with height and width of the current Dimension
 */
com.socialcomputing.jmi.script.Dimension.toRectangle = function(){
	return new JMI.script.Rectangle(0, 0, this._width, this._height);
}

/**
 *  Construct a new Dimension Object from a <code>Rectangle</code>
 * 	It is done this way instead of a specific constructor because of ActionScript3
 * contructor overload limitations : on class can only have one constructor signature. 
 */
com.socialcomputing.jmi.script.Dimension.fromRectangle = function( rect) {
	return new JMI.script.Dimension(rect.width, rect.height);
}
		

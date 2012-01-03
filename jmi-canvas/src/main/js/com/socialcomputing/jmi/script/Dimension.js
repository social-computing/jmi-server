JMI_MAP.namespace("JMI_MAP.com.socialcomputing.jmi.script.Dimension");

JMI_MAP.com.socialcomputing.jmi.script.Dimension = (function() {
	
	var width, height,
		Constr;
	
	Constr = function( w, h) {
		width = w;
		height = h;
	}
	Constr.prototype = {
		constructor: JMI_MAP.com.socialcomputing.jmi.script.Dimension,
		version: "2.0"
	}
	return Constr;
}());

JMI_MAP.com.socialcomputing.jmi.script.Dimension.prototype.resize = function(d) {
	return new Dimension(Math.max(this.width, d.width), 
					     Math.max(this.height, d.height));
}
		
// static
/**
 * Create a <code>Rectangle</code> instance with height and width of the current Dimension
 */
JMI_MAP.com.socialcomputing.jmi.script.Dimension.toRectangle = function(){
	return new Rectangle(0, 0, this._width, this._height);
}

/**
 *  Construct a new Dimension Object from a <code>Rectangle</code>
 * 	It is done this way instead of a specific constructor because of ActionScript3
 * contructor overload limitations : on class can only have one constructor signature. 
 */
JMI_MAP.com.socialcomputing.jmi.script.Dimension.fromRectangle = function( rect) {
	return new Dimension(rect.width, rect.height);
}
		

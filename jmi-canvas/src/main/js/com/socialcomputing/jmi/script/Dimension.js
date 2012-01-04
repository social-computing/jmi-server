
JMI.namespace("com.socialcomputing.jmi.script.Dimension") = (function() {
	
	var width, height,
		Constr;
	
	Constr = function( w, h) {
		width = w;
		height = h;
	}
	Constr.prototype = {
		constructor: com.socialcomputing.jmi.script.Dimension,
		version: "2.0"
	}
	return Constr;
}());

com.socialcomputing.jmi.script.Dimension.prototype.resize = function(d) {
	return new com.socialcomputing.jmi.script.Dimension(Math.max(this.width, d.width), Math.max(this.height, d.height));
}
		
// static
/**
 * Create a <code>Rectangle</code> instance with height and width of the current Dimension
 */
com.socialcomputing.jmi.script.Dimension.toRectangle = function(){
	return new com.socialcomputing.jmi.script.Rectangle(0, 0, this._width, this._height);
}

/**
 *  Construct a new Dimension Object from a <code>Rectangle</code>
 * 	It is done this way instead of a specific constructor because of ActionScript3
 * contructor overload limitations : on class can only have one constructor signature. 
 */
com.socialcomputing.jmi.script.Dimension.fromRectangle = function( rect) {
	return new com.socialcomputing.jmi.script.Dimension(rect.width, rect.height);
}
		

JMI.namespace("com.socialcomputing.jmi.script.Point") = (function() {
	var x, y, width, height,
		Constr;
	
	Constr = function( x, y) {
	    this.x = x;
	    this.y = y;
	}
	Constr.prototype = {
		constructor: com.socialcomputing.jmi.script.Point,
		version: "2.0"
	}
	return Constr;
}());

com.socialcomputing.jmi.script.Point.prototype.add = function( p) {
	this.x = this.x + p.x;
	this.y = this.y + p.y;
}

com.socialcomputing.jmi.script.Point.prototype.substract = function( p) {
	this.x = this.x > p.x ? this.x - p.x : p.x - this.x;
	this.y = this.y > p.y ? this.y - p.y : p.y - this.y;
}

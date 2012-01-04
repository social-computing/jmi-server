JMI.namespace("com.socialcomputing.wps.script.Rectangle") = (function() {
	var x, y, width, height,
		Constr;
	
	Constr = function( x, y, width, height) {
	    this.x = x;
	    this.y = y;
	    this.width = width;
	    this.height = height;
	}
	Constr.prototype = {
		constructor: com.socialcomputing.jmi.script.Rectangle,
		version: "2.0"
	}
	return Constr;
}());

/*
 * Merge 2 Rectangles.
 * If the dest Rectangle has one null dimension then copy the source on it.
 * 
 * @param dst	Destination Rectangle that will hold its union with src.
 * @param src	Source Rectangle.
 */
com.socialcomputing.wps.script.Rectangle.prototype.merge = function(src) {
	if(this.width * this.height != 0) {
		this.union( src);
	}
	else {
		this.copy( src);
	}
}

com.socialcomputing.wps.script.Rectangle.prototype.union = function(src) {
	this.x = Math.min( this.x, src.x);
	this.y = Math.min( this.y, src.y);
	this.width = this.width + src.width - Math.min( this.x + this.width - src.x, src.x + src.width - this.x);
	this.height = this.height + src.height - Math.min( this.y + this.height - src.y, src.y + src.height - this.y);
}

/*
 * Copy the source <code>Rectangle</code> properties to the destination <code>Rectangle</code>
 * 
 * @param dst Destination Rectangle to copy the values to 
 * @param src Source Rectangle to copy the values from
 * 
 */
com.socialcomputing.wps.script.Rectangle.prototype.copy = function(src) {
	this.x = src.x;
	this.y = src.y
	this.width = src.width;
	this.height = src.height;
}

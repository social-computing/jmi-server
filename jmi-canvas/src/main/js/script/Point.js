JMI.namespace("script.Point");

JMI.script.Point = (function() {
    
    var Point = function(x, y) {
	    this._x = x;
	    this._y = y;
	};
	
	Point.prototype = {
		constructor: JMI.script.Point,
		
        add: function(p) {
            this._x = this._x + p._x;
            this._y = this._y + p._y;
            return this;
        },
        
        substract: function(p) {
            this._x = this._x > p._x ? this._x - p._x : p._x - this._x;
            this._y = this._y > p._y ? this._y - p._y : p._y - this._y;
            return this;
        },
	};
	
	return Point;
}());


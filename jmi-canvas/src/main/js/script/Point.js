JMI.namespace("script.Point");

JMI.script.Point = (function() {
    
    var Point = function(x, y) {
	    this.x = x;
	    this.y = y;
	};
	
	Point.prototype = {
		constructor: JMI.script.Point,
		
        add: function(p) {
            this.x = this.x + p._x;
            this.y = this.y + p._y;
            return this;
        },
        
        substract: function(p) {
            this.x = this.x > p._x ? this.x - p._x : p._x - this.x;
            this.y = this.y > p._y ? this.y - p._y : p._y - this.y;
            return this;
        },
	};
	
	return Point;
}());


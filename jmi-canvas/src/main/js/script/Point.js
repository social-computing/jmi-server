JMI.namespace("script.Point");

JMI.script.Point = (function() {
    
    var Point = function(x, y) {
        
        if (typeof y === "undefined") {
            if (typeof x === "undefined") {
                this.x = 0;
                this.y = 0;
            }
            else if (x instanceof JMI.script.Point) {
                this.x = x.x;
                this.y = x.y;  
            }
        }
        else {
            this.x = x;
            this.y = y;
        }
	};
	
	Point.prototype = {
		constructor: JMI.script.Point,
		
        add: function(p) {
            this.x = this.x + p.x;
            this.y = this.y + p.y;
            return this;
        },
        
        substract: function(p) {
            this.x = this.x > p.x ? this.x - p.x : p.x - this.x;
            this.y = this.y > p.y ? this.y - p.y : p.y - this.y;
            return this;
        },
        
        /**
         * Rotates the vector (this point) 90°C CCW.
         * Useful to create a 2D ortho basis of vectors.
         */
        pivot: function() {
            this.x -= this.y;
            this.y += this.x;
            this.x -= this.y;
            return this;            
        },
        
        clone: function() {
            return new JMI.script.Point(this.x, this.y);
        }
	};
	
	return Point;
}());


// Public static functions
/*
 * Scales a Point previously normalized to 2^16.
 * This is usefull to avoid using floats when scaling Vectors.
 * 
 * @param v      a vector to scale
 * @param scale  a scale value
 * @return       new vector scaled by the given value    
 */
JMI.script.Point.Scale = function(v, scale) {
    return new JMI.script.Point((v.x * scale) >> 16, (v.y * scale) >> 16);
};
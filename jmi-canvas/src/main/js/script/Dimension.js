JMI.namespace("script.Dimension");

JMI.script.Dimension = (function() {
	
	var Dimension = function(w, h) {
		this._width = w;
		this._height = h;
	};
	
    Dimension.prototype = {
        contructor: JMI.script.Dimension,
		
		resize: function(d) {
			return new JMI.script.Dimension(Math.max(this._width, d._width), Math.max(this._height, d._height));
		},
		
		/**
         * Create a <code>Rectangle</code> instance with height and width of the current Dimension
         */
		toRectangle: function(){
            return new JMI.script.Rectangle(0, 0, this._width, this._height);
        }
	};
	
	return Dimension;
}());


// Public static methods
/**
 *  Construct a new Dimension Object from a <code>Rectangle</code>
 * 	It is done this way instead of a specific constructor because of ActionScript3
 * contructor overload limitations : on class can only have one constructor signature.
 *  
 */
// TODO : Try to handle this with the class constructor
JMI.script.Dimension.fromRectangle = function(rect) {
	return new JMI.script.Dimension(rect.width, rect.height);
};
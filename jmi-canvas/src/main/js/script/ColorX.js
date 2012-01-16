JMI.namespace("script.ColorX");


JMI.script.ColorX = (function() {
	
	function convert(integer) { 
        var str = Number(integer).toString(16); 
        return str.length == 1 ? "0" + str : str; 
    }


	// default constructor
	/* Creates an RGB color with the specified combined RGB value
     * consisting of the red component in bits 16-23, the green component
     * in bits 8-15, and the blue component in bits 0-7.  The actual color
     * used in rendering depends on finding the best match given the
     * color space available for a particular output device.  
     */
	var ColorX = function(color) {
	    this.color = color;
	    this.scolor = null;
	};
	
    ColorX.prototype = {
        constructor: JMI.script.ColorX,

        getColor: function(props) {
            if(this.scolor == null) {
                return this.toHex();
            } 
            else {
                var str = JMI.script.Base.parseString4(this.scolor, props, false);
                // TODO : portage, supprimer parseInt et renvoyer un code couleur hexadecimal
                var colValue =  (str == null) ? 0 : parseInt(str);
                return new JMI.script.ColorX(colValue).toHex();
            }
        },
        
        getRed: function() {
            return (this.color >> 16) & 0xFF; 
        },
        
        getGreen: function() {
            return (this.color >> 8) & 0xFF; 
        },
        
        getBlue: function() {
            return (this.color >> 0) & 0xFF; 
        },
        
        getRGBString: function() {
            return 'rgb(' + this.getRed() + ', ' + this.getGreen() + ', ' + this.getBlue() + ')';
        },
        
        toHex: function() {
            return "#" + convert(this.getRed()) + convert(this.getGreen()) + convert(this.getBlue());
        }
    };

	return ColorX;
}());


/**
 * Creates a new ColorX using swatch properties.
 * @param color	The swtach property format.
 */
/*public function ColorX(color:String)
{
    m_scolor = color;
}*/

/**
 * Creates a new ColorX using its color components.
 * @param red		Red component as a 8 bits value (from 0 to 255).
 * @param green		Green component as a 8 bits value (from 0 to 255).
 * @param blue		Blue component as a 8 bits value (from 0 to 255).
 */
/*public function ColorX(red:int, green:int, blue:int)
{
    m_color = ( red << 16)|( green << 8)| blue;
}*/


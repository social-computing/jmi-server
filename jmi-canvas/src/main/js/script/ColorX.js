JMI.namespace("script.ColorX");


JMI.script.ColorX = (function() {
	// default constructor
	var ColorX = function(color) {
	    this._color = color;
	    this._scolor = null;
	};
	
    ColorX.prototype = {
        constructor : JMI.script.ColorX,

        getColor2 : function(props) {
            if(this._scolor == null) {
                return this._color;
            } else {
                var str = JMI.script.Base.parseString4(this._scolor, props, false);
                // TODO : portage, supprimer parseInt et renvoyer un code couleur hexadecimal
                return (str == null) ? 0 : parseInt(str);
            }
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


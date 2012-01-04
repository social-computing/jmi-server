/*
 * <p>Title: ColorX</p>
 * <p>Description: A wrapper for the java.awt.Color class.<br>
 * Because Serializtion is not compatible between client and server for the original class.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.namespace("com.socialcomputing.jmi.script.ColorX") = (function() {
/*
 * A 32 bit int that hold Color invormation in ARGB format.
 * Each component is a 8 bits value:
 * <ul>
 * <li>Alpha	from bit 31 to bit 24.</li>
 * <li>Red		from bit 23 to bit 16.</li>
 * <li>Green	from bit 15 to bit 8.</li>
 * <li>Blue		from bit 7 to bit 0.</li>
 * </ul>
 * Warning! Alpha is no more used.
 */
	var m_color, 
		m_scolor = null;

	// default constructor
	Constr;
	
	Constr = function( color) {
    	m_color = color;
	}
	Constr.prototype = {
		constructor: com.socialcomputing.jmi.script.ColorX,
		version: "2.0"
	}
	return Constr;
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

/**
 * Convert this ColorX to a java.awt.Color.
 * @return	a new Color equivalent to this.
 */
com.socialcomputing.jmi.script.BagZone.prototype.getColor = function() {
    var color = new ColorTransform();
	color.color = m_color;
	return color;
}

com.socialcomputing.jmi.script.BagZone.prototype.getColor2 = function( props) {
	var color = new ColorTransform();
    if( m_scolor == null) { 
		color.color = m_color;
	}
	else {
        var str = Base.parseString4( m_scolor, props, false);
        color.color = str == null ? 0 : parseInt( str);
	}
	return color;
}

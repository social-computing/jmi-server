/*
 * <p>Title: FormatToken</p>
 * <p>Description: A simple container to hold the current format of the tokens.<br>
 * It's also used to evaluated the current line min and max text position (ascent and descent)</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.namespace("com.socialcomputing.wps.script.FormatToken") = (function() {
    /**
     * Current line margins.
     */
    var m_margin = com.socialcomputing.wps.script.Insets,
    
    /**
     * Current line maximum ascent (Top of the highest letter)
     */
    m_aMax,
    
    /**
     * Current line maximum descent (Bottom of the lowest letter)
     */
    m_dMax,
    
    /**
     * Current line text alignment flags.
     */
    m_flags,
    
    /**
     * Current line width.
     */
    m_width,
    	
	Constr;
	
	Constr = function() {
	}
	Constr.prototype = {
		constructor: com.socialcomputing.jmi.script.Dimension,
		version: "2.0"
	}
	return Constr;
}());

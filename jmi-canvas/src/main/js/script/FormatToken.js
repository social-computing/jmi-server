JMI.namespace("script.FormatToken");

/*
 * <p>Title: FormatToken</p>
 * <p>Description: A simple container to hold the current format of the tokens.<br>
 * It's also used to evaluated the current line min and max text position (ascent and descent)</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.FormatToken = (function() {
	var FormatToken = function() {
        /**
         * Current line margins.
         * :Jmi.script.Insets
         */
        this.margin = null;
        
        /**
         * Current line maximum ascent (Top of the highest letter)
         */
        this.aMax = null;
        
        /**
         * Current line maximum descent (Bottom of the lowest letter)
         */
        this.dMax = null;
        
        /**
         * Current line text alignment flags.
         */
        this.flags = null;
        
        /**
         * Current line width.
         */
        this.width = null;
	    
	};
	
	FormatToken.prototype = {
		constructor: JMI.script.FormatToken
	};
	
	return FormatToken;
}());
/*
 * <p>Title: FontX</p>
 * <p>Description: A wrapper for the java.awt.Font class.<br>
 * Because Serializtion is not compatible between client and server for the original class.</p>
 * All the fields are containers so this Font can be easily changed by Swatchs.
 * This is the way a FontX is initialized after being created by the default constructor.
 * The syle bits are stored in the default flag container (FLAG_VAL).</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.namespace("com.socialcomputing.jmi.script.FontX") = (function() {

	Constr;
	
	Constr = function( w, h) {
		width = w;
		height = h;
	}
	Constr.prototype = {
		constructor: com.socialcomputing.jmi.script.FontX,
		version: "2.0"
	}
	return Constr;
}());

/**
 * Convert this FontX to a java.awt.Font.
 * @param props		A property table that should hold props referenced by this containers.
 * @return			a new Font equivalent to this.
 */
com.socialcomputing.jmi.script.FontX.prototype.getTextFormat = function( props) {
    var flags = getFlags( props ),
        size = getInt( SIZE_VAL, props );
    var name = getString( NAME_VAL, props ),
        key = name + flags + size;
    
	var font = s_fontBuf[ key ];
    if ( font == null )
    {
        font = new TextFormat();// Portage
        font.font = name;
        font.size = size;

        s_fontBuf[key] = font;
    }
	if (( flags & HTMLText.BOLD )!= 0)  font.bold = true;
	if (( flags & HTMLText.ITALIC )!= 0) font.italic = true;
    
    return font;
}

/**
 * Index of the bit flag prop in VContainer table
 */
//	public      static final int    FLAGS_VAL           = 0;

/**
 * Index of the Font name prop in VContainer table
 */
com.socialcomputing.jmi.script.NAME_VAL = 1;

/**
 * Index of the Font size prop in VContainer table
 */
com.socialcomputing.jmi.script.SIZE_VAL = 2;
    
/**
 * A Font Buffer to reduce temporary Font object creation.
 */
com.socialcomputing.jmi.script.s_fontBuf = new Object();
    

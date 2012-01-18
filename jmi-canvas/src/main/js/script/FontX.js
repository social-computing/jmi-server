JMI.namespace("script.FontX");

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
JMI.script.FontX = (function() {

	var FontX = function() {
		JMI.script.Base.call( this);
	};
	FontX.prototype = {
		constructor: JMI.script.FontX,
		
		/**
		 * Convert this FontX to a java.awt.Font.
		 * @param props		A property table that should hold props referenced by this containers.
		 * @return			a new Font equivalent to this.
		 */
		init: function( props) {
		    var flags = this.getFlags( props );
		    
		    this.size = this.getInt( JMI.script.FontX.SIZE_VAL, props );
			if (( flags & JMI.script.HTMLText.BOLD )!= 0)  this.bold = true;
			if (( flags & JMI.script.HTMLText.ITALIC )!= 0) this.italic = true;
			this.font = this.getString( JMI.script.FontX.NAME_VAL, props );

			this.canvas = '';
			if( this.bold)
				this.canvas = 'bold ' + this.canvas;
			if( this.italic)
				this.canvas = 'italic ' + this.canvas;
		    this.canvas = this.canvas + this.getString( JMI.script.FontX.SIZE_VAL, props ) + 'pt ' + this.getString( JMI.script.FontX.NAME_VAL, props );
		}
	};
	
	// Héritage
	for (var element in JMI.script.Base.prototype ) {
		if( !FontX.prototype[element])
			FontX.prototype[element] = JMI.script.Base.prototype[element];
	}
	
	return FontX;
}());


/**
 * Index of the bit flag prop in VContainer table
 */
//	public      static final int    FLAGS_VAL           = 0;

/**
 * Index of the Font name prop in VContainer table
 */
JMI.script.FontX.NAME_VAL = 1;

/**
 * Index of the Font size prop in VContainer table
 */
JMI.script.FontX.SIZE_VAL = 2;
    
    

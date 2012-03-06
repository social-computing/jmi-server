/*global define, JMI */
JMI.namespace("script.Token");

/**
 * <p>Title: Token</p>
 * <p>Description: A Token is an atomic part of a text holding properties.<br>
 * It is created by parsing a text to get a list of Tokens.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.Token = ( function() {

	/**
	 * Constructor
	 */
	var Token = function() {
		/**
		 * The label of the Token.
		 * It can be a simple text or the name of a property.
		 * //:String = null;
		 */
		this.buffer = "";

		/**
		 * A bit table holding all the XXX_BITs.
		 */
		// :int
		this.flags = 0;

		/**
		 * Number of digit of the integer part if this is a number property.
		 */
		// :int
		this.intSize = 0;

		/**
		 * Number of digit of the fractional part if this is a float number property.
		 */
		// :int
		this.floatSize = 0;

		/**
		 * Maximum nuber of lines in a list property.
		 */
		// :int
		this.lineMax = 0;
	};

	Token.prototype = {
		constructor : JMI.script.Token,

		/**
		 * Gets the number of line of a list property.
		 * If this is not a list property returns 1.
		 * If the list property starts with '/N' then N is the maximum line count.
		 *
		 * @param props     Table that holds this property (if it is one). // :Array
		 * @return          The size of the property list array or the maximum line number. //:int
		 */
		getListSize : function(props) {
			// :int
			var size = 1;

			if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.LIST_BIT)) {
				// :Array
				var list = props[this.buffer];
				size = list ? list.length : 0;
				if(this.lineMax > 0 && size > this.lineMax) {
					size = this.lineMax;
				}
			}
			return size;
		},
		/**
		 * Creates a textual representation of this Token.
		 * If it's a property Token, return the value of the property.
		 * If it's a property list Token return the line of the list corresponding to 'i'.
		 * Float properties are not shown...but should.
		 *
		 * @param i         Index to retrieve in a list property. //:int
		 * @param props     Table holding the property. //:Array
		 * @return          A String representation of this Token after parsing. //:String
		 */
		toString : function(i, props) {
			//:String
			var tokenStr;

			// Properties
			if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.PROP_BIT)) {
				//:Object
				var rawProp;

				// Global Properties
				if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.GLOBAL_BIT)) {
					props = props._ENV;
				}

				// List Properties
				if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.LIST_BIT)) {
					rawProp = props[this.buffer];

					if(rawProp !== null) {
						rawProp = (props[ this.buffer])[i];
					} else {
						rawProp = null;
					}
				}

				// simple Properties
				else {
					rawProp = props[this.buffer];
				}

				// prop exists!
				if(rawProp !== null) {
					// :String
					var prop = rawProp.toString();

					// numerical prop
					if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.NUM_BIT)) {
						// float prop
						if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.FLOAT_BIT)) {
							tokenStr = prop;
							/*  if ( this.intSize > 0 )            // fixed size
							 {
							 if ( this.intSize > prop.length()) //
							 {
							 tokenStr = extendWS( prop, this.intSize - prop.length());
							 }
							 else
							 {
							 tokenStr = prop.substring( 0, this.intSize );
							 }
							 }
							 else*/
						}

						// int prop
						else {
							// if (rawProp is Number )
							if(rawProp) {
								prop = rawProp + '';
							}

							// fixed size
							if(this.intSize > 0) {
								if(prop.length < this.intSize) {
									if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.BOUND_BIT)) {
										tokenStr = prop;
									} else {
										tokenStr = this.extendWS(prop, this.intSize - prop.length);
									}
								} else {
									tokenStr = prop.substring(0, this.intSize);
								}
							} else {
								tokenStr = prop;
							}
						}
					}

					// text prop
					else {
						// fixed size
						if(this.intSize > 0) {
							if(prop.length < this.intSize) {
								if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.BOUND_BIT)) {
									tokenStr = prop;
								} else {
									tokenStr = this.extendWS(prop, this.intSize - prop.length);
								}
							} else {
								tokenStr = prop.substring(0, this.intSize);
								if(this.floatSize > 0) {
									tokenStr += "...";
								}
							}
						}

						// variable size
						else {
							tokenStr = prop;
						}
					}
				}

				// unknown properties
				else {
					tokenStr = JMI.script.Base.isEnabled(this.flags, JMI.script.Token.NEEDED_BIT) ? null : "";
				}
			}

			// simple text
			else {
				tokenStr = this.buffer;
			}

			// We must URLEncode this text !
			if(JMI.script.Base.isEnabled(this.flags, JMI.script.Token.URLCOD_BIT)) {
				// portage , replace escape by encodeURIComponent that does a better job
				// see : https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/encodeURIComponent
				tokenStr = encodeURIComponent(tokenStr);
			}

			return tokenStr;
		},
		/**
		 * Finds the property flags (XXX_BITs) and returns the number of caracters read to find them.
		 * This number is also the index of the first character of the textual part.
		 * This is used to parse all the tokens in the same String sequentialy.
		 *
		 * @param text  A String containing a raw Token description. //:String
		 * @param i     The index to start from in the text String. //:int
		 * @return      The number of character read. // :int
		 */
		findFlags : function(text, i) {
			//String  num = "";
			//:String
			var c = text.charAt(i);
			//:int
			var beg;

			if(c === '!') {
				this.flags |= JMI.script.Token.NEEDED_BIT;
				c = text.charAt(i++);
			}

			if(c === '?') {
				this.flags |= JMI.script.Token.URLCOD_BIT;
				c = text.charAt(i++);
			}

			if(c === '/' && JMI.script.Base.isEnabled(this.flags, JMI.script.Token.LIST_BIT)) {
				beg = ++i;
				// Character.isDigit(...)
				while(text.charAt(i) >= '0' && text.charAt(i) <= '9') {
					i++;
				}
				this.lineMax = parseInt(text.substring(beg, i), 10);
			}

			if(c === '-') {
				this.flags |= JMI.script.Token.RIGHT_BIT;
				i++;
			} else if(c === '+') {
				this.flags |= JMI.script.Token.BOUND_BIT;
				i++;
			}
			beg = i;

			// Character.isDigit(...)
			while(text.charAt(i) >= '0' && text.charAt(i) <= '9') {
				i++;
			}

			if(text.charAt(i) === '.') {
				i++;
				// Character.isDigit(...)
				while(text.charAt(i) >= '0' && text.charAt(i) <= '9') {
					i++;
				}
			}

			if(i > beg) {
				// :Number
				var sizes = text.substring(beg, i);
				this.intSize = Math.floor(sizes);
				this.floatSize = Math.round(10 * (sizes - this.intSize));
			}
			c = text.charAt(i);

			if(c === 'd' || c === 'f') {
				this.flags |= JMI.script.Token.NUM_BIT;

				if(c === 'f') {
					this.flags |= JMI.script.Token.FLOAT_BIT;
				}
			}

			// text
			else {
				c = text.charAt(i + 1);

				/*if ( c == '_' )
				 {
				 this.flags |= SUB_BIT;
				 }
				 else*/
				if(c === '$') {
					this.flags |= JMI.script.Token.GLOBAL_BIT;
				}
			}

			return i;
		},
		/**
		 * Creates an extended String by adding leading or trailling spaces.
		 * If the Token is right aligned, the whitespaces are added at the beginning.
		 * Else they are added at the end.
		 *
		 * @param prop  The text representation of the property to extend. //:String
		 * @param ws    Number of whitespaces to add. //:int
		 * @return      A new String begining or ending with ws blank chars depending on the flags of this Token. //:String
		 */
		extendWS : function(prop, ws) {
			//var spaces:Array= new char[ws];

			//var spaces:Array= new Array();
			var spaces = [];

			while(--ws >= 0) {
				spaces[ws] = ' ';
			}

			//:String
			var wsStr = spaces;

			return JMI.script.Base.isEnabled(this.flags, JMI.script.Token.RIGHT_BIT) ? wsStr + prop : // Right justificated
			prop + wsStr;
			// Right justificated
		}
	};

	return Token;
}());

// Constants
/**
 * True if this is bound to a property.
 * This means the Token value is the name of a property to retrieve in a table.
 * This lookup mecanism is also used in swatchs and find the properties in Zones table.
 */
// :int
JMI.script.Token.PROP_BIT = 0x0001;

/**
 * True if this is a list property.
 * A list property is an Array of Objects used to create multi-lines GUI like menus or tips.
 */
// :int
JMI.script.Token.LIST_BIT = 0x0002;

/**
 * True if this is a numerical property.
 * Sometimes a property is retrieved as a Number (int, float...) so we need to convert it to a String.
 */
// :int
JMI.script.Token.NUM_BIT = 0x0004;

/**
 * True if this is a floating point property.
 */
// :int
JMI.script.Token.FLOAT_BIT = 0x0008;

/**
 * True if this is right justificated.
 * Default text alignment in list properties is left.
 */
// :int
JMI.script.Token.RIGHT_BIT = 0x0010;

/**
 * True if this length is bound.
 * That means the number of characters of the property is limited.
 */
// :int
JMI.script.Token.BOUND_BIT = 0x0020;

/**
 * True if this is a SubProp.
 * Internal only, the user never sets this. Only used by the Server part.
 * @deprecated  This has been replaced by an automatic mecanism.
 * @see         RecommendationGroup
 */
// :int
JMI.script.Token.SUB_BIT = 0x0040;

/**
 * True if this must be URLEncoded.
 * Usefull for creating CGI URLs using props.
 */
// :int
JMI.script.Token.URLCOD_BIT = 0x0080;

/**
 * True if this property is global for the Plan.
 * This means the property should be retrieved from the Env table.
 */
// :int
JMI.script.Token.GLOBAL_BIT = 0x0100;

/**
 * True if this list property is required.
 * If the list is void then The Token will be null. Else it is just void.
 * This is usefull to avoid displaying empty submenus or tips.
 */
// :int
JMI.script.Token.NEEDED_BIT = 0x0200;

/**
 * <p>Title: Token</p>
 * <p>Description: A Token is an atomic part of a text holding properties.<br>
 * It is created by parsing a text to get a list of Tokens.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.namespace("jmi.script.Token") = (function() {


    /**
     * Constructor
     */
    var Token = function() {
        /**
         * The label of the Token.
         * It can be a simple text or the name of a property.
         * //:String = null;
         */
        this._buffer = "";
        
        /**
         * A bit table holding all the XXX_BITs.
         */
        // :int 
        this._flags = 0, 
        
        /**
         * Number of digit of the integer part if this is a number property.
         */
        // :int
        this._intSize = 0,
        
        /**
         * Number of digit of the fractional part if this is a float number property.
         */
        // :int
        this._floatSize = 0,
        
        /**
         * Maximum nuber of lines in a list property.
         */
        // :int
        this._lineMax = 0;
    };
        
    Token.prototype = {
        contructor: jmi.script.Token,
        
        /**
         * Gets the number of line of a list property.
         * If this is not a list property returns 1.
         * If the list property starts with '/N' then N is the maximum line count.
         * 
         * @param props     Table that holds this property (if it is one). // :Array
         * @return          The size of the property list array or the maximum line number. //:int
         */
        getListSize: function(props) {
            // :int
            var size = 1;
            
            // TODO portage : namespace prefix function Base.isEnabled 
            if (Base.isEnabled(this._flags, LIST_BIT)) {
                // :Array
                var list = props[m_buffer];
                
                // TODO portage : see if the ternary operator works as expected
                size = list != null ? list.length : 0;
                if (this._lineMax > 0 && size > this._lineMax) size = this._lineMax;
            }
            return size;
        }
    };
    
    return Token;

		
    
        
    /**
     * Creates a textual representation of this Token.
     * If it's a property Token, return the value of the property.
     * If it's a property list Token return the line of the list corresponding to 'i'.
     * Float properties are not shown...but should.
     * 
     * @param i			Index to retrieve in a list property. //:int
     * @param props		Table holding the property. //:Array
     * @return			A String representation of this Token after parsing. //:String 
     */
    this.prototype.toString = function(i, props) {
        //:String
        var tokenStr;
        
        // Properties
        if (Base.isEnabled(m_flags, PROP_BIT)) {
            //:Object
            var rawProp;
            
            // Global Properties
            if (Base.isEnabled(m_flags, GLOBAL_BIT)) {
                // TODO portage : gestion du cast ?
                // props = props["ENV"] as Array;
                props = props["ENV"];
            }
            
            // List Properties
            if (Base.isEnabled(m_flags, LIST_BIT)) {
                rawProp = props[m_buffer];
                
                if (rawProp != null) {
                    rawProp = (props[ m_buffer])[i];
                }
                else {
                    rawProp = null;
                }
            }
            
            // simple Properties
            else  {
                rawProp = props[m_buffer];
            }
            
            // prop exists!
            if (rawProp != null) {
                // :String
                // TODO portage : revoir toString d'un array
                var prop= rawProp.toString();
                
                // numerical prop
                if (Base.isEnabled(m_flags, NUM_BIT)) {
                    // float prop
                    if (Base.isEnabled(m_flags, FLOAT_BIT)) {
                        // TODO portage : ????
                        tokenStr = prop;
                        /*	if ( m_intSize > 0 )            // fixed size
                        {
                        if ( m_intSize > prop.length()) //
                        {
                        tokenStr = extendWS( prop, m_intSize - prop.length());
                        }
                        else
                        {
                        tokenStr = prop.substring( 0, m_intSize );
                        }
                        }
                        else*/                            
                    }
                    
                    // int prop
                    else {
                        // TODO portage : toujours les casts
                        // if (rawProp is Number )
                        // prop = "" + int(rawProp);
                        if (rawProp) prop = "" + rawProp;
                        
                        // fixed size
                        if (m_intSize > 0) {
                            if (prop.length < m_intSize ) {
                                // TODO : portage : namespace prefix
                                if (Base.isEnabled(m_flags, BOUND_BIT)) {
                                    tokenStr = prop;
                                }
                                else {
                                    // TODO : portage : besoin de prefixer par this sur les appels de functions de la classe ?
                                    tokenStr = extendWS(prop, m_intSize - prop.length);
                                }
                            }
                            else {
                                // TODO : portage : vérifier le fonctionne de substring en javascript
                                tokenStr = prop.substring(0, m_intSize);
                            }
                        }
                        else {
                            tokenStr = prop;
                        }
                    }
                }
                
                // text prop
                else {
                    // fixed size
                    if (m_intSize > 0) {
                        if (prop.length < m_intSize) {
                            // TODO : portage : namespace prefix
                            if (Base.isEnabled(m_flags, BOUND_BIT)) {
                                tokenStr = prop;
                            }
                            else {
                                // TODO : portage : besoin de prefixer par this sur les appels de functions de la classe ?
                                tokenStr = extendWS(prop, m_intSize - prop.length);
                            }
                        }
                        else {
                            // TODO : portage : vérifier le fonctionne de substring en javascript
                            tokenStr = prop.substring( 0, m_intSize );
                            if( m_floatSize > 0) tokenStr += "...";
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
                // TODO : portage : namespace prefix
                tokenStr = Base.isEnabled(m_flags, NEEDED_BIT ) ? null : "";
            }
        }
        
        // simple text
        else                                            {
            tokenStr = m_buffer;
        }
        
        // We must URLEncode this text !
        if (Base.isEnabled(m_flags, URLCOD_BIT)) {
            // portage , replace escape by encodeURIComponent that does a better job
            // see : https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/encodeURIComponent
        	tokenStr = encodeURIComponent(tokenStr);
        }
        
        return tokenStr;
    };
        
    /**
     * Finds the property flags (XXX_BITs) and returns the number of caracters read to find them.
     * This number is also the index of the first character of the textual part.
     * This is used to parse all the tokens in the same String sequentialy.
     * 
     * @param text	A String containing a raw Token description. //:String
     * @param i		The index to start from in the text String. //:int
     * @return		The number of character read. // :int
     */
    
    this.prototype.findFlags = function(text, i) {
        //String  num = "";
        //:String
        var c = text.charAt(i);
        //:int
        var beg;
        
        if (c == '!') {
            m_flags |= NEEDED_BIT;
            c = text.charAt(i++);
        }
        
        if (c == '?') {
            m_flags |= URLCOD_BIT;
            c = text.charAt( i ++ );
        }
        
        if (c == '/' && Base.isEnabled(m_flags, Token.LIST_BIT)) {
            beg = ++ i;
			// Character.isDigit(...)
            while (text.charAt(i) >= '0' && text.charAt(i) <= '9') i ++;
            m_lineMax = parseInt(text.substring(beg, i));
        }
        
        if (c == '-') {
            m_flags |= RIGHT_BIT;
            i ++;
        }
        else if (c == '+') {
            m_flags |= BOUND_BIT;
            i ++;
        }
        
        beg = i;
        
		// Character.isDigit(...)
        while (text.charAt( i ) >= '0' && text.charAt( i ) <= '9') i ++;
        
        if (text.charAt( i ) == '.') {
            i ++;
			// Character.isDigit(...)
            while (text.charAt( i ) >= '0' && text.charAt( i ) <= '9') i ++;
        }
        
        if (i > beg) {
            // :Number
            // TODO : portage : cast
            // var sizes = parseFloat( text.substring( beg, i ));
            var sizes = text.substring(beg, i);
            
            // TODO : portage : cast
            // m_intSize = int(sizes);
            m_intSize = sizes;
            m_floatSize = Math.round(10. * (sizes - m_intSize));
        }
        
        c = text.charAt( i );
        
        if (c == 'd' || c == 'f') {
            m_flags |= NUM_BIT;
            
            if (c == 'f') {
                m_flags |= FLOAT_BIT;
            }
        }
        
        // text
        else {
            c = text.charAt(i + 1);
            
            /*if ( c == '_' )
            {
            m_flags |= SUB_BIT;
            }
            else*/ 
            if (c == '$'){
                m_flags |= GLOBAL_BIT;
            }
        }
        
        return i;
    };
        
    /**
     * Creates an extended String by adding leading or trailling spaces.
     * If the Token is right aligned, the whitespaces are added at the beginning.
     * Else they are added at the end.
     * 
     * @param prop	The text representation of the property to extend. //:String
     * @param ws	Number of whitespaces to add. //:int
     * @return		A new String begining or ending with ws blank chars depending on the flags of this Token. //:String
     */
    this.prototype.extendWS = function(prop, ws) {
            //var spaces:Array= new char[ws];
            
            //var spaces:Array= new Array();
            var spaces = [];
            
            while (--ws >= 0) {
                spaces[ws] = ' ';
            }
            
            //:String 
            // TODO : portage : conversion from array to string
            var wsStr = spaces;
            
            // TODO portage : namespace prefix
            return Base.isEnabled(m_flags, RIGHT_BIT) ?
                wsStr + prop :		// Right justificated
                prop + wsStr;       // Right justificated
    };
   
}());




// Constants 
/**
 * True if this is bound to a property.
 * This means the Token value is the name of a property to retrieve in a table.
 * This lookup mecanism is also used in swatchs and find the properties in Zones table.
 */ 
 // :int
jmi.script.Token.PROP_BIT = 0x0001;
        
/**
 * True if this is a list property.
 * A list property is an Array of Objects used to create multi-lines GUI like menus or tips.
 */
 // :int
jmi.script.Token.LIST_BIT = 0x0002;

/**
 * True if this is a numerical property.
 * Sometimes a property is retrieved as a Number (int, float...) so we need to convert it to a String.
 */
 // :int
jmi.script.Token.NUM_BIT = 0x0004;

/**
 * True if this is a floating point property.
 */
 // :int
jmi.script.Token.FLOAT_BIT = 0x0008;

/**
 * True if this is right justificated.
 * Default text alignment in list properties is left.
 */
 // :int
jmi.script.Token.RIGHT_BIT = 0x0010;

/**
 * True if this length is bound.
 * That means the number of characters of the property is limited.
 */
 // :int
jmi.script.Token.BOUND_BIT = 0x0020;

/**
 * True if this is a SubProp.
 * Internal only, the user never sets this. Only used by the Server part.
 * @deprecated  This has been replaced by an automatic mecanism.
 * @see         RecommendationGroup
 */
 // :int
jmi.script.Token.SUB_BIT = 0x0040;

/**
 * True if this must be URLEncoded.
 * Usefull for creating CGI URLs using props.
 */
 // :int
jmi.script.Token.URLCOD_BIT = 0x0080;

/**
 * True if this property is global for the Plan.
 * This means the property should be retrieved from the Env table.
 */
 // :int
jmi.script.Token.GLOBAL_BIT = 0x0100;

/**
 * True if this list property is required.
 * If the list is void then The Token will be null. Else it is just void.
 * This is usefull to avoid displaying empty submenus or tips.
 */
 // :int
jmi.script.Token.NEEDED_BIT = 0x0200;
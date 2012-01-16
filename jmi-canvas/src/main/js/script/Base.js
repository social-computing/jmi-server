JMI.namespace("script.Base");

/*
 * <p>Title: Base</p>
 * <p>Description: Base class of all the Swatch types. It holds field of those class in a VContainer array.
 * This generic storage allow Swatch to be easily processed on the server.
 * To retreive fields in the client, use the matching ID (XXXX_VAL).<br>
 * ex : FLAGS_VAL = 0 -> A flag of bits is always stored as an int in the first cell of the array.<br>
 * WARNING! As there's no constructor, m_containers should be allocated with the necessary size before filling it.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.Base = (function() {

    var Base = function(subZones) {
         // Containers table holding the values of all inheriting class fields.
        this.containers = {}; //:Array; = > Object ?
        this.subZones = subZones;
    };
    

	Base.prototype = {
		constructor: JMI.script.Base,
		
		/*
         * Returns wether a property exists.
         * Use this when the prop is a type (not an object) and then can't be null.
         * For example, getFloat(prop) will raise an exception if prop doesn't exists!
         * 
         * @param prop  Index of the property
         * @return True if prop exists.
         */
        isDefined: function(property) {
            return this.containers[property] != null;
        },
        
        /*
         * Gets the flags always stored whith each Base.
         * 
         * @param   props   Props table
         * @return  an int containing the bits of the flag.
         */
        getFlags: function(props) {
            return this.getInt(JMI.script.Base.FLAGS_VAL, props);
        },

        /*
         * Gets the object embedded in a Container. It can be the Container itself or a referenced property.
         * 
         * @param   property    Index of the container in the table (XXX_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the Object corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        getValue: function(property, props) {
            var container = this.containers[property];
            return container != 'null' ? (container.isBound ? props[container.value]: container.value ): null;
        },
        
        /*
         * Gets the boolean value embedded in a Container. It can be the Container itself or a referenced property.
         * 
         * @param   property    Index of the container in the table (XXX_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the boolean corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        getBool: function(property, props) {
            return this.getValue(property, props);
        },
        
        /*
         * Gets the int value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * 
         * @param   property    Index of the container in the table (SIZE_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the int corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        getInt: function(property, props) {
            return this.getValue(property, props);
        },
        
        /*
         * Gets the float value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * 
         * @param   property    Index of the container in the table (SCALE_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the float corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        getFloat: function(property, props) {
            return this.getValue(property, props);
        },
        
        /*
         * Gets the String value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * 
         * @param   property    Index of the container in the table (NAME_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the String corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        getString: function(property, props) {
            return this.getValue(property, props);
        },
        
        /*
         * Gets the HTMLText value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * 
         * @param   property    Index of the container in the table (ex:TEXT_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the HTMLText corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        getText: function(property, props) {
            return this.getValue(property, props);
        },
        
        /*
         * Gets the FontX value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * 
         * @param   property    Index of the container in the table (ex:FONT_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the FontX corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        getFont: function(property, props) {
            return this.getValue(property, props);
        },
        
        /*
         * Gets the Color value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * The real Object embedded or referenced is a ColorX because of serialization problem with Color class.
         * So this method also translate the ColorX in java.awt.Color.
         * 
         * @param   prop    Index of the container in the table (ex:IN_COL_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the Color corresponding to the ColorX field whose index is prop or null if the property doesn't exists or is void.
         */
        // TODO : portage, instanceof
        getColor: function(property, props) {
            var value = this.getValue(property, props);
            if(value instanceof JMI.script.ColorX) { 
                return value.getColor(props);
            }
            return null;
        },
        
        /*
         * Gets the Transfo value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * 
         * @param   property    Index of the container in the table (ex:TRANSFO_VAL).
         * @param   props   If this contains a referenced property, props is the table that hold the property.
         * @return  the Transfo corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        getTransfo: function(property, props) {
            var value = this.getValue(property, props);
            if (value instanceof JMI.script.Transfo) {
                return value;
            }
            return null;
        },
        
        /*
         * Sets the Graphics pen color using a Color Container.
         * 
         * @param  s       A Graphical context whose pen color must be updated.
         * @param  prop    Index of the container in the table (ex:OUT_COL_VAL).
         * @param  props   If this contains a referenced property, props is the table that hold the property.
         * @return          True if the property exist and has a value. False Otherwise.
         */
        // TODO : portage, methode graphique
        setColor: function(s, property, props) {
            var color = this.getColor(property, props);
            if (color != null) {
                s.graphics.beginFill(color.color);
                return true;
            }
            return false;
        },
        
        /*
         * Parses a String containing bound properties and replace them by their String representation.
         * If some properties are lists, returns a String for each member of the smalest list.
         * This is used in menu, tips...
         * 
         * @param   prop    Index of the container in the table (ex:TEXT_VAL).
         * @param   props   If this contains referenced properties, props is the table that hold the property.
         * @return          An array of String or null if the property doesn't exists or is void.
         */
        parseString: function(property, props) {
            var text = this.getString(property, props);
            return text != null ? this.parseString3(text, props) : null;
        },
        
        /*
         * Parses a String containing bound properties and replace them by their String representation.
         * If some properties are lists, returns a line of text for each member of the smalest list finished by an HTML <br>.
         * Warning! The isHtm parameter is not read because this method consider it's always true!
         * This is used in tips...
         * 
         * @param   prop    Index of the container in the table (ex:TEXT_VAL).
         * @param   props   If this contains referenced properties, props is the table that hold the property.
         * @param   isHtm   Not used in this HTML-only implementation!
         * @return          A String containing <br> between each lines or null if the property doesn't exists or is void.
         */
        //protected function parseString(prop:int, props:Hashtable, isHtm:Boolean):String // throws UnsupportedEncodingException
        // Renommage nom fonction
        // TODO : portage voir si on peut refusionner avec la method parseString
        parseString2: function(property, props, isHtm) {
            var text = this.getString(property, props);
            return text != undefined ? JMI.script.Base.parseString4(text, props, isHtm) : undefined;
        },
        
        /*
         * Parses a String containing bound properties and replace them by their String representation.
         * If some properties are lists, returns a String for each member of the smalest list.
         * This is used in menu, tips...
         * @param   text    A String containing or not bound properties.
         * @param   props   If this contains referenced properties, props is the table that hold the property.
         * @return          An array of String.
         * @throws UnsupportedEncodingException 
         */
        //protected function parseString(text:String, props:Hashtable):Array
        // Renommage nom fonction
        // TODO : portage voir si on peut refusionner avec la method parseString
        parseString3: function(text, props) {
            // Bug fix : javascript
            var javascript = text.substring(0, 10) === "javascript";
            var tokens = JMI.script.Base.parseTokens(text);
            var j, n, max = 0;
            for each(var token in tokens) {
                n = token.getListSize(props);
                if (n == 0) {
                    max = 0;
                    break;
                }
                max = Math.max(n, max);
            }
            var dst = [];
            var prop;
            for (j = 0; j < max; j ++) {
                dst[j] = "";
                
                for (token in tokens) {
                    if (javascript) {
                        // TODO portage : String.fromCharCode
                        token.buffer = token.buffer.split( ",").join(String.fromCharCode(0xFFFC));
                    }
                    prop = token.toString(j, props);
                    
                    if (prop == null) {
                        dst[j] = null;
                        break;
                    }
                    // TODO portage : concat 
                    dst[j] = dst[j].concat(prop);
                }
            }
            
            return dst;
        }
	};
	
	return Base;
}());


// Constants
/**
 * String representation of a new-line.
 * Separator used by Swatchs to separate URL/Track in "menu, event, item"
 */
JMI.script.Base.SEP = "\n";

/**
 * String representation of a tab.
 * Separator used by Swatchs to separate URL/Track in "open page"
 */
JMI.script.Base.SUBSEP = "\t";

/**
 * 2 Pi
 */
JMI.script.Base.Pi2 = 6.2831853;

/**
 * Index of the bit flag prop in VContainer table.
 */
JMI.script.Base.FLAGS_VAL = 0;


// Public static methods
/*
 * Parses a String containing bound properties and replace them by their String representation.
 * If some properties are lists, returns a line of text for each member of the smalest list finished by an HTML.<br>
 * Warning! The isHtm parameter is not read because this method consider it's always true!
 * This is used in tips...
 * 
 * @param   text    A String containing or not bound properties.
 * @param   props   If this contains referenced properties, props is the table that hold the property.
 * @param   isHtm   Not used in this HTML-only implementation!
 * @return          A String containing <br> between each lines.
 */
JMI.script.Base.parseString4 = function(text, props, isHtm) {
    var tokens = JMI.script.Base.parseTokens(text),
        token, j, n, max = 0;
    
    for each(var token in tokens) {
        n = token.getListSize(props);
        if (n === 0) {
            max = 0;
            break;
        }
        max = Math.max(n, max);
    }
    var dst= "";
    var prop;
    
    for (j = 0; j < max; j ++) {
        for each(var token in tokens) {
            prop = token.toString(j, props);
            dst += prop === null ? " ? " : prop;
        }
        if (j < max - 1) dst += "<br/>";
    }
    
    return dst;
};

/*
 * getNextTokenProp( tokens )       => getNextTokenProp( tokens, token.PROP_BIT, token.SUB_BIT )
 * getNextSubTokenProp( tokens )    => getNextTokenProp( tokens, token.SUB_BIT, 0 )
 */
/*
 * Gets the next token String representation matching some flags.
 * This is used only by PlanGenerator to sort "database" and "analysis" properties.
 * The tokens remaining are the ones that include some bits and exclude others.
 * Each call iterate to the next Token in the tokens list.<br>
 * Warning! This method remove Tokens from tokens while iterating.
 * ex:
 * <ul>
 * <li><code>getNextTokenProp( tokens, token.PROP_BIT, token.SUB_BIT )</code> retrieve only "database" properties.</li>
 * <li><code>getNextTokenProp( tokens, token.SUB_BIT, 0 )</code> retrieve only "analysis" properties.</li>
 * </ul>
 * 
 * @param tokens        A list of Token.
 * @param includeBit    The Token to retreive must include those bits (Token.XXX_BIT).
 * @param excludeBit    The Token to retreive must exclude those bits (Token.XXX_BIT).
 * @return              A String representation of the next matching Token.
 */
JMI.script.Base.getNextTokenProp = function(tokens, includeBit, excludeBit) {
    while(tokens.length > 0) {
        var token = tokens.shift();
        
        if ((token._flags & includeBit) != 0 && (token._flags & excludeBit) == 0) {
            return token.buffer;
        }
    }
    return null;
};

/*
 * Parse a text String to extract the Tokens inside.
 * A token can be of 3 kinds:
 * <ul>
 * <li>textual      a simple text</li>
 * <li>prop         a bound property that should be replaced by it's value</li>
 * <li>list         a bound array property that should be replaced by it's values</li>
 * </ul>
 * 
 * @param text  a text String containing or not properties.
 * @return      a Vector of Tokens matching text.
 */
JMI.script.Base.parseTokens = function(text) {
    var i, j = 0, len = text.length,
        c = '0';
    var isAfterBS = false;
    var token = null;
    var tokens = [];
    
    for (i = 0; i < len; i++) {
        // TODO : portage charAt
        c = text.charAt(i);
        if(c == '\\') {
            isAfterBS = true;
        }
        else {
            if (isAfterBS ) {
                if (token == null) {
                    token = new JMI.script.Token();
                    j = 0;
                }

                token.buffer = JMI.script.Base.setCharAt(token.buffer, c, j++);
                isAfterBS = false;
            }
            else {
                // a new prop or list Token begins
                if (c == '{' || c == '[') {
                    // there was a text Token previously,
                    if (token != null) {
                        //token.buffer.setLength( j );
                        // TODO : portage push
                        tokens.push(token);    // store it
                    }
                    
                    // and create a new prop or list Token
                    token = new JMI.script.Token();
                    token._flags = c == '{' ? JMI.script.Token.PROP_BIT : JMI.script.Token.PROP_BIT | JMI.script.Token.LIST_BIT;
                    i = token.findFlags(text, i + 1);
                    j = 0;
                }
                
                // a new prop or list Token ends
                else if (c == '}' || c == ']') {
                    //token.buffer.setLength( j );
                    // TODO : portage push
                    tokens.push(token);        // store the previous token
                    token = null;              // a new one must be created
                }
                else {
                    
                    // a new text Token begins
                    if (token == null) {
                        // create it
                        token = new JMI.script.Token();
                        j = 0;
                    }

                    token.buffer = JMI.script.Base.setCharAt(token.buffer, c, j++);
                }
            }
        }
    }
    
    // don't forget the last one!
    if (c != '}' && c != ']') {
        //token.buffer.setLength( j );
        // TODO : portage push
        // store the previous token
        tokens.push( token );        
    }
    
    return tokens;
};


JMI.script.Base.setCharAt = function(str, c, index) {
    return str.substring(0, index)+ c + str.substring(index + 1);
};

/*
 * Convenient methode to check if a bit is enabled in a flag.
 * 
 * @param flags The int holding the bits.
 * @param bit   Index of the bit in [0,31]
 * 
 * @return      true if the bit is 1, false otherwise.
 */
JMI.script.Base.isEnabled = function(flags, bit) {
    return (flags & bit) != 0;
};
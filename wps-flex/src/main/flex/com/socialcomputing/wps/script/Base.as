package com.socialcomputing.wps.script  {
    import flash.display.Graphics;
    import flash.geom.ColorTransform;
    
    /**
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
    public class Base
    {
        /**
         * String representation of a new-line.
         * Separator used by Swatchs to separate URL/Track in "menu, event, item"
         */
        public static const SEP:String= "" + '\n';
        
        /**
         * String representation of a tab.
         * Separator used by Swatchs to separate URL/Track in "open page"
         */
        public static const SUBSEP:String= "" + '\t';
        
        /**
         * 2 Pi
         */
        public static const Pi2:Number= 6.2831853;
        
        /**
         * Index of the bit flag prop in VContainer table.
         */
        public static const FLAGS_VAL:int= 0;
        
        /**
         * Containers table holding the values of all inheriting class fields.
         */
        public var m_containers:Array;
        
        /**
         * Returns wether a property exists.
         * Use this when the prop is a type (not an object) and then can't be null.
         * For example, getFloat(prop) will raise an exception if prop doesn't exists!
         * @param prop  Index of the property
         * @return True if prop exists.
         */
        public function isDefined( prop:int):Boolean {
            return m_containers[prop] != null;
        }
        
        /**
         * Convenient methode to check if a bit is enabled in a flag.
         * @param flags	The int holding the bits.
         * @param bit	Index of the bit in [0,31]
         * @return		true if the bit is 1, false otherwise.
         */
        public static function isEnabled( flags:int, bit:int):Boolean {
            return ( flags & bit )!= 0;
        }
        
        /**
         * Gets the flags always stored whith each Base.
         * @param	props	Props table
         * @return	an int containing the bits of the flag.
         */
        public function getFlags( props:Array):int {
            return getInt( FLAGS_VAL, props );
        }
        
        /**
         * Gets the object embedded in a Container. It can be the Container itself or a referenced property.
         * @param	prop	Index of the container in the table (XXX_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the Object corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        public function getValue( prop:int, props:Array):Object {
            var container:VContainer= m_containers[prop];
            
            return container != null ?( container.m_isBound ? props[ container.m_value] : container.m_value ): null;
        }
        
        /**
         * Gets the boolean value embedded in a Container. It can be the Container itself or a referenced property.
         * @param	prop	Index of the container in the table (XXX_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the boolean corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        /*	protected final boolean getBool( int prop, Hashtable props )
        {
        return ((Boolean)getValue( prop, props )).booleanValue();
        }*/
        
        /**
         * Gets the int value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * @param	prop	Index of the container in the table (SIZE_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the int corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        public function getInt( prop:int, props:Array):int {
            return int(getValue( prop, props ));
        }
        
        /**
         * Gets the float value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * @param	prop	Index of the container in the table (SCALE_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the float corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        public function getFloat( prop:int, props:Array):Number {
            return Number(getValue( prop, props ));
        }
        
        /**
         * Gets the String value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * @param	prop	Index of the container in the table (NAME_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the String corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        public function getString( prop:int, props:Array):String {
            return String(getValue( prop, props ));
        }
        
        /**
         * Gets the HTMLText value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * @param	prop	Index of the container in the table (ex:TEXT_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the HTMLText corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        public function getText( prop:int, props:Array):HTMLText {
            return HTMLText(getValue( prop, props ));
        }
        
        /**
         * Gets the FontX value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * @param	prop	Index of the container in the table (ex:FONT_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the FontX corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        public function getFont( prop:int, props:Array):FontX {
            return FontX(getValue( prop, props ));
        }
        
        /**
         * Gets the Color value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * The real Object embedded or referenced is a ColorX because of serialization problem with Color class.
         * So this method also translate the ColorX in java.awt.Color.
         * @param	prop	Index of the container in the table (ex:IN_COL_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the Color corresponding to the ColorX field whose index is prop or null if the property doesn't exists or is void.
         * @throws UnsupportedEncodingException 
         */
        public function getColor( prop:int, props:Array):ColorTransform {
            var color:ColorX= ColorX(getValue( prop, props ));
            var ContainerColor:ColorTransform= null;
            
            if (color==null)
                return null;
            
            try {
                ContainerColor = color.getColor2( props);
            } catch (e:Error) {
				trace( e.message);
            }
            
            return ContainerColor;
        }
        
        /**
         * Gets the Transfo value embedded in a Container.
         * It can be the Container itself or a referenced property.
         * @param	prop	Index of the container in the table (ex:TRANSFO_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return	the Transfo corresponding to the field whose index is prop or null if the property doesn't exists or is void.
         */
        public function getTransfo( prop:int, props:Array):Transfo {// What's that strange test?????
			var res:Object = getValue( prop, props );
            if ( res is Transfo )
            {
				return res as Transfo;
            }
			return null;
        }
        
        /**
         * Sets the Graphics pen color using a Color Container.
         * @param g			A Graphical context whose pen color must be updated.
         * @param	prop	Index of the container in the table (ex:OUT_COL_VAL).
         * @param	props	If this contains a referenced property, props is the table that hold the property.
         * @return			True if the property exist and has a value. False Otherwise.
         * @throws UnsupportedEncodingException 
         */
        public function setColor( g:Graphics, prop:int, props:Array):Boolean // throws UnsupportedEncodingException
        {
            var color:ColorTransform = getColor( prop, props );
            if ( color != null )
            {
                g.beginFill( color.color );
                return true;
            }
            return false;
        }
                
        /**
         * Parses a String containing bound properties and replace them by their String representation.
         * If some properties are lists, returns a String for each member of the smalest list.
         * This is used in menu, tips...
         * @param	prop	Index of the container in the table (ex:TEXT_VAL).
         * @param	props	If this contains referenced properties, props is the table that hold the property.
         * @return			An array of String or null if the property doesn't exists or is void.
         * @throws UnsupportedEncodingException 
         */
        public function parseString(prop:int, props:Array):Vector.<String>
        {
            var text:String= getString( prop, props );
            
            return text != null ? parseString3( text, props ) : null;
        }
        
        /**
         * Parses a String containing bound properties and replace them by their String representation.
         * If some properties are lists, returns a line of text for each member of the smalest list finished by an HTML <br>.
         * Warning! The isHtm parameter is not read because this method consider it's always true!
         * This is used in tips...
         * @param	prop	Index of the container in the table (ex:TEXT_VAL).
         * @param	props	If this contains referenced properties, props is the table that hold the property.
         * @param	isHtm	Not used in this HTML-only implementation!
         * @return			A String containing <br> between each lines or null if the property doesn't exists or is void.
         * @throws UnsupportedEncodingException 
         */
        //protected function parseString(prop:int, props:Hashtable, isHtm:Boolean):String // throws UnsupportedEncodingException
        // Renommage nom fonction
        public function parseString2(prop:int, props:Array, isHtm:Boolean):String // throws UnsupportedEncodingException
        {
            var text:String= getString( prop, props );
            var returnstring:String= null;
            
            try {
                returnstring = parseString4( text, props, isHtm );
            } catch (e:Error) {
				trace( e.message);
            }
            
            return text != null ? returnstring : null;
        }
        
        /**
         * Parses a String containing bound properties and replace them by their String representation.
         * If some properties are lists, returns a String for each member of the smalest list.
         * This is used in menu, tips...
         * @param	text	A String containing or not bound properties.
         * @param	props	If this contains referenced properties, props is the table that hold the property.
         * @return			An array of String.
         * @throws UnsupportedEncodingException 
         */
        //protected function parseString(text:String, props:Hashtable):Array
        // Renommage nom fonction
        public function parseString3(text:String, props:Array):Vector.<String>
        {
            var tokens:Vector.<Token> = parseTokens( text );
            var j:int, n:int, max:int = 0;
			for each( var token:Token in tokens)
            {
                n       = token.getListSize( props );
                if ( n == 0)
                {
                    max = 0;
                    break;
                }
                max     = Math.max( n, max );
            }
            var dst:Vector.<String> = new Vector.<String>();
            var prop:String;
            
            for ( j = 0; j < max; j ++ )
            {
                dst[j] = "";
                
				for each( token in tokens)
				{
                    prop    = token.toString( j, props );
                    
                    if ( prop == null )
                    {
                        dst[j] = null;
                        break;
                    }
                    
					dst[j] = dst[j].concat( prop);
                }
            }
            
            return dst;
        }
        
        /**
         * Parses a String containing bound properties and replace them by their String representation.
         * If some properties are lists, returns a line of text for each member of the smalest list finished by an HTML.<br>
         * Warning! The isHtm parameter is not read because this method consider it's always true!
         * This is used in tips...
         * @param	text	A String containing or not bound properties.
         * @param	props	If this contains referenced properties, props is the table that hold the property.
         * @param	isHtm	Not used in this HTML-only implementation!
         * @return			A String containing <br> between each lines.
         * @throws UnsupportedEncodingException 
         */
        static public function parseString4( text:String, props:Array, isHtm:Boolean):String {
            var tokens:Vector.<Token>= parseTokens( text );
            var j:int, n:int, max:int = 0;
            
			for each( var token:Token in tokens)
            {
                n       = token.getListSize( props );
                if ( n == 0)
                {
                    max = 0;
                    break;
                }
                max     = Math.max( n, max );
            }
            var dst:String= "";
            var prop:String;
            
            for ( j = 0; j < max; j ++ )
            {
				for each( token in tokens)
                {
                    prop    = token.toString( j, props );
                    
                    dst += prop == null ? " ? " : prop;
                }
                if ( j < max - 1)  dst += "<br>";
            }
            
            return dst;
        }
        
        /**
         * getNextTokenProp( tokens )       => getNextTokenProp( tokens, token.PROP_BIT, token.SUB_BIT )
         * getNextSubTokenProp( tokens )    => getNextTokenProp( tokens, token.SUB_BIT, 0 )
         */
        /**
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
         * @param tokens		A list of Token.
         * @param includeBit	The Token to retreive must include those bits (Token.XXX_BIT).
         * @param excludeBit	The Token to retreive must exclude those bits (Token.XXX_BIT).
         * @return				A String representation of the next matching Token.
         */
        public static function getNextTokenProp( tokens:Vector, includeBit:int, excludeBit:int):String {
			while( tokens.length > 0)
            {
                var token:Token = tokens.shift();
                
                if (( token.m_flags & includeBit )!= 0&& ( token.m_flags & excludeBit )== 0)
                {
                    return token.m_buffer;
                }
            }
            return null;
        }
        
        /**
         * Parse a text String to extract the Tokens inside.
         * A token can be of 3 kinds:
         * <ul>
         * <li>textual		a simple text</li>
         * <li>prop			a bound property that should be replaced by it's value</li>
         * <li>list			a bound array property that should be replaced by it's values</li>
         * </ul>
         * @param text	a text String containing or not properties.
         * @return		a Vector of Tokens matching text.
         */
        public static function parseTokens( text:String):Vector.<Token> {
            var i:int, j:int = 0, len:int = text.length;
            var c:String = '0';
            var isAfterBS:Boolean= false;
            var token:Token= null;
            var tokens:Vector.<Token>= new Vector.<Token>();
            
            for ( i = 0; i < len; i ++ )
            {
                c = text.charAt( i );
                if ( c == '\\' )
                {
                    //				System.out.println( c );
                    isAfterBS = true;
                }
                else
                {
                    if ( isAfterBS )
                    {
                        //					System.out.println( "afterBS" );
                        if ( token == null )
                        {
                            //						System.out.println( "new Token[" +(len - i)+ ']' );
                            token = new Token();
                            j = 0;
                        }
                        //token.m_buffer.setCharAt( j ++, c );
                        //Actionscript
                        token.m_buffer = setCharAt(token.m_buffer, c, j++);
                        isAfterBS = false;
                    }
                    else
                    {
                        if ( c == '{' || c == '[' )      // a new prop or list Token begins
                        {
                            //						System.out.println( c );
                            if ( token != null )        // there was a text Token previously,
                            {
                                //							System.out.println( "add Token: " + token.m_buffer );
                                //token.m_buffer.setLength( j );
                                tokens.push( token );    // store it
                            }
                            // and create a new prop or list Token
                            //						System.out.println( "new Token[" +(len - i)+ ']' );
                            token = new Token();
							token.m_flags = c == '{' ? Token.PROP_BIT : Token.PROP_BIT | Token.LIST_BIT;
                            i = token.findFlags( text, i + 1);
                            j = 0;
                        }
                        else if ( c == '}' || c == ']' ) // a new prop or list Token ends
                        {
                            //						System.out.println( c );
                            //						System.out.println( "add Token: " + token.m_buffer );
                            //token.m_buffer.setLength( j );
                            tokens.push( token );        // store the previous token
                            token = null;               // a new one must be created
                        }
                        else
                        {
                            //						System.out.println( c );
                            if ( token == null )        // a new text Token begins
                            {                           // create it
                                //							System.out.println( "new Token[" +(len - i)+ ']' );
                                token = new Token();
                                j = 0;
                            }
                            //token.m_buffer.setCharAt( j ++, c ); // copy this char in the current Token
                            //Actionscript
                            token.m_buffer = setCharAt(token.m_buffer, c, j++);
                        }
                    }
                }
            }
            if ( c != '}' && c != ']' ) // don't forget the last one!
            {
                //			System.out.println( "add Token: " + token.m_buffer );
                //token.m_buffer.setLength( j );
                tokens.push( token );        // store the previous token
            }
            
            return tokens;
        }
        
        private static function setCharAt(str:String, char:String,index:int):String {
            return str.substr(0,index) + char + str.substr(index + 1);
        }

    }
}
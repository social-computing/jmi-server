package  {
    import java.io.UnsupportedEncodingException;
    import java.net.URLEncoder;
    import java.util.Hashtable;
    
    /**
     * <p>Title: Token</p>
     * <p>Description: A Token is an atomic part of a text holding properties.<br>
     * It is created by parsing a text to get a list of Tokens.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class Token
    {
        /**
         * True if this is bound to a property.
         * This means the Token value is the name of a property to retrieve in a table.
         * This lookup mecanism is also used in swatchs and find the properties in Zones table.
         */
        public const PROP_BIT:int= 0x0001;
        
        /**
         * True if this is a list property.
         * A list property is an Array of Objects used to create multi-lines GUI like menus or tips.
         */
        public const LIST_BIT:int= 0x0002;
        
        /**
         * True if this is a numerical property.
         * Sometimes a property is retrieved as a Number (int, float...) so we need to convert it to a String.
         */
        public const NUM_BIT:int= 0x0004;
        
        /**
         * True if this is a floating point property.
         */
        public const FLOAT_BIT:int= 0x0008;
        
        /**
         * True if this is right justificated.
         * Default text alignment in list properties is left.
         */
        public const RIGHT_BIT:int= 0x0010;
        
        /**
         * True if this length is bound.
         * That means the number of characters of the property is limited.
         */
        public const BOUND_BIT:int= 0x0020;
        
        /**
         * True if this is a SubProp.
         * Internal only, the user never sets this. Only used by the Server part.
         * @deprecated	This has been replaced by an automatic mecanism.
         * @see			RecommendationGroup
         */
        public const SUB_BIT:int= 0x0040;
        
        /**
         * True if this must be URLEncoded.
         * Usefull for creating CGI URLs using props.
         */
        public const URLCOD_BIT:int= 0x0080;
        
        /**
         * True if this property is global for the Plan.
         * This means the property should be retrieved from the Env table.
         */
        public const GLOBAL_BIT:int= 0x0100;
        
        /**
         * True if this list property is required.
         * If the list is void then The Token will be null. Else it is just void.
         * This is usefull to avoid displaying empty submenus or tips.
         */
        public const NEEDED_BIT:int= 0x0200;
        
        /**
         * The label of the Token.
         * It can be a simple text or the name of a property.
         */
        public var m_buffer:StringBuffer = null;
        
        /**
         * A bit table holding all the XXX_BITs.
         */
        public var m_flags:int = 0;
        
        /**
         * Number of digit of the integer part if this is a number property.
         */
        private var m_intSize:int = 0;
        
        /**
         * Number of digit of the fractional part if this is a float number property.
         */
        private var m_floatSize:int = 0;
        
        /**
         * Maximum nuber of lines in a list property.
         */
        private var m_lineMax:int = 0;
        
        /**
         * Creates a new Token knowing the length of its textual part and its flags.
         * @param length	The number of characters in the label (text or property name).
         * @param flags		An integer to hold the bits of this.
         */
        public function Token(length:int, flags:int)
        {
            m_buffer = new StringBuffer( length );
            m_buffer.setLength( length );
            m_flags = flags;
        }
        
        /**
         * Gets the number of line of a list property.
         * If this is not a list property returns 1.
         * If the list property starts with '/N' then N is the maximum line count.
         * @param props		Table that holds this property (if it is one).
         * @return			The size of the property list array or the maximum line number.
         */
        function getListSize( props:Array):int {
            var size:int= 1;
            
            if ( Base.isEnabled( m_flags, LIST_BIT ))
            {
                var list:Array = props.get( m_buffer.toString());
                
                size = list != null ? list.length : 0;
                if ( m_lineMax > 0&& size > m_lineMax )    size = m_lineMax;
            }
            
            return size;
        }
        
        /**
         * Creates a textual representation of this Token.
         * If it's a property Token, return the value of the property.
         * If it's a property list Token return the line of the list corresponding to 'i'.
         * Float properties are not shown...but should.
         * @param i			Index to retrieve in a list property.
         * @param props		Table holding the property.
         * @return			A String representation of this Token after parsing.
         * @throws UnsupportedEncodingException 
         */
        function toString(i:int, props:Array):String {
            var tokenStr:String= null;
            
            if ( Base.isEnabled( m_flags, PROP_BIT ))       // Propertie
            {
                var rawProp:Object;
                
                if ( Base.isEnabled( m_flags, GLOBAL_BIT ))    // Global Propertie
                {
                    props = Hashtable(props.get( "ENV"));
                }
                
                if ( Base.isEnabled( m_flags, LIST_BIT ))    // List Propertie
                {
                    rawProp = props.get( m_buffer.toString());
                    
                    if ( rawProp != null )
                    {
                        rawProp = (props.get( m_buffer.toString()))[i];
                    }
                    else
                    {
                        rawProp = null;
                    }
                }
                else                                // simple Propertie
                {
                    rawProp = props.get( m_buffer.toString());
                }
                
                if ( rawProp != null )                 // prop exists!
                {
                    var prop:String= rawProp.toString();
                    
                    if ( Base.isEnabled( m_flags, NUM_BIT ))    // numerical prop
                    {
                        if ( Base.isEnabled( m_flags, FLOAT_BIT ))  // float prop
                        {
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
                            {
                                tokenStr = prop;
                            }
                        }
                        else                                // int prop
                        {
                            if ( rawProp is Number )
                                prop = "" + (Number(rawProp )).intValue();
                            if ( m_intSize > 0)            // fixed size
                            {
                                if ( prop.length() < m_intSize ) //
                                {
                                    if ( Base.isEnabled( m_flags, BOUND_BIT ))
                                    {
                                        tokenStr = prop;
                                    }
                                    else
                                    {
                                        tokenStr = extendWS( prop, m_intSize - prop.length());
                                    }
                                }
                                else
                                {
                                    tokenStr = prop.substring( 0, m_intSize );
                                }
                            }
                            else
                            {
                                tokenStr = prop;
                            }
                        }
                    }
                    else                                // text prop
                    {
                        if ( m_intSize > 0)            // fixed size
                        {
                            if ( prop.length() < m_intSize ) //
                            {
                                if ( Base.isEnabled( m_flags, BOUND_BIT ))
                                {
                                    tokenStr = prop;
                                }
                                else
                                {
                                    tokenStr = extendWS( prop, m_intSize - prop.length());
                                }
                            }
                            else
                            {
                                tokenStr = prop.substring( 0, m_intSize );
                                if( m_floatSize > 0)
                                    tokenStr += "...";
                            }
                        }
                        else                            // variable size
                        {
                            tokenStr = prop;
                        }
                    }
                }
                else                                        // unknown propertie
                {
                    tokenStr = Base.isEnabled( m_flags, NEEDED_BIT )? null : "";
                }
            }
            else                                            // simple text
            {
                tokenStr = m_buffer.toString();
            }
            
            if ( Base.isEnabled( m_flags, URLCOD_BIT ))       // We must URLEncode this text!
            {
                try {
                    tokenStr = URLEncoder.encode( tokenStr , "UTF-8" );
                } catch (e:UnsupportedEncodingException) {
                    e.printStackTrace();
                }
            }
            
            return tokenStr;
        }
        
        /**
         * Finds the property flags (XXX_BITs) and returns the number of caracters read to find them.
         * This number is also the index of the first character of the textual part.
         * This is used to parse all the tokens in the same String sequentialy.
         * @param text	A String containing a raw Token description.
         * @param i		The index to start from in the text String.
         * @return		The number of character read.
         */
        function findFlags( text:String, i:int):int {
            //String  num = "";
            var c:String= text.charAt( i );
            var beg:int;
            
            if ( c == '!' )
            {
                m_flags |= NEEDED_BIT;
                c = text.charAt( i ++ );
            }
            
            if ( c == '?' )
            {
                m_flags |= URLCOD_BIT;
                c = text.charAt( i ++ );
            }
            
            if ( c == '/' && Base.isEnabled( m_flags, Token.LIST_BIT ))
            {
                beg = ++ i;
                while ( Character.isDigit( text.charAt( i )))   i ++;
                try { m_lineMax = Integer.parseInt( text.substring( beg, i ));}
                catch ( e:NumberFormatException) {}
            }
            
            if ( c == '-' )
            {
                m_flags |= RIGHT_BIT;
                i ++;
            }
            else if ( c == '+' )
            {
                m_flags |= BOUND_BIT;
                i ++;
            }
            
            beg = i;
            
            while ( Character.isDigit( text.charAt( i )))   i ++;
            
            if ( text.charAt( i ) == '.' )
            {
                i ++;
                while ( Character.isDigit( text.charAt( i )))   i ++;
            }
            
            if ( i > beg )
            {
                var sizes:Number= ( Float.valueOf( text.substring( beg, i ))).floatValue();
                
                m_intSize = int(sizes);
                m_floatSize = Math.round( 10.*( sizes - m_intSize ));
            }
            
            c = text.charAt( i );
            
            if ( c == 'd' || c == 'f' )
            {
                m_flags |= NUM_BIT;
                
                if ( c == 'f' )
                {
                    m_flags |= FLOAT_BIT;
                }
            }
            else // text
            {
                c = text.charAt( i + 1);
                
                /*if ( c == '_' )
                {
                m_flags |= SUB_BIT;
                }
                else*/ if ( c == '$' )
                {
                    m_flags |= GLOBAL_BIT;
                }
            }
            
            return i;
        }
        
        /**
         * Creates an extended String by adding leading or trailling spaces.
         * If the Token is right aligned, the whitespaces are added at the beginning.
         * Else they are added at the end.
         * @param prop	The text representation of the property to extend.
         * @param ws	Number of whitespaces to add.
         * @return		A new String begining or ending with ws blank chars depending on the flags of this Token.
         */
        private function extendWS( prop:String, ws:int):String {
            var spaces:Array= new char[ws];
            
            while ( -- ws >= 0)
            {
                spaces[ws] = ' ';
            }
            
            var wsStr:String= new String( spaces );
            
            return Base.isEnabled( m_flags, RIGHT_BIT ) ?
                wsStr + prop :		// Right justificated
                prop + wsStr;       // Right justificated
        }
    }
}
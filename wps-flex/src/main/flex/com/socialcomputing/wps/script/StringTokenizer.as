package com.socialcomputing.wps.script {
    import flash.errors.IllegalOperationError;

    public class StringTokenizer implements Iterator, IEnumeration
    {
        /**
         *
         * The source <code>String</code> from which tokens are extracted
         * from based on the specified delimiter
         *
         */
        protected var source:String;
        
        /**
         *
         * Defines the delimiter from which the <code>source</code> String
         * tokens are to be extracted
         *
         */
        protected var delimiter:String;
        
        /**
         *
         * Defines the token <code>Array</code> which contains each token
         * String extracted by the <code>StringTokenizer</code> instance
         *
         */
        protected var tokens:Array;
        
        /**
         *
         * Stores the current position (cursor) in the tokens array from
         * which calls to <code>nextToken();</code> are based on
         *
         */
        protected var cursor:int;
        
        /**
         *
         * Creates a new instance of <code>StringTokenizer</code> and
         * processes the source String into an array of tokens based
         * on the specified delimiter
         *
         * @example
         * <listing version="3.0">
         *
         * var tokens:StringTokenizer = new StringTokenizer("This is a test", " ");
         *
         * trace( tokens.countTokens() );
         *
         * </listing>
         *
         * @param the source String from which the tokens are to be extracted
         * @param the delimiter on which the tokens are extracted
         *
         */
        public function StringTokenizer(source:String, delimiter:String)
        {
            this.source = source;
            this.delimiter = delimiter;
            
            this.tokens = source.split( delimiter );
        }
        
        /**
         *
         * Static Factory method which creates a new <code>Iterator</code>
         * StringTokenizer implementation
         *
         * @example
         * <listing version="3.0">
         *
         * var tokens:Iterator = StringTokenizer.createIterator("This is a test", " ");
         *
         * while ( tokens.hasNext() )
         * {
         *     trace( tokens.next() );
         * }
         *
         * </listing>
         *
         * @param  the source String from which the tokens are to be extracted
         * @param  the delimiter on which the tokens are extracted
         * @return a new <code>StringTokenizer</code> as an <code>Iterator</code>
         *
         */
        public static function createIterator(source:String, delimiter:String) : Iterator
        {
            return new StringTokenizer( source, delimiter );
        }
        
        /**
         *
         * Static Factory method which creates a new <code>IEnumeration</code>
         * StringTokenizer implementation
         *
         * @example
         * <listing version="3.0">
         *
         * var tokens:IEnumeration = StringTokenizer.createEnumeration("This is a test", " ");
         *
         * while ( tokens.hasMoreElements() )
         * {
         *     trace( tokens.nextElement() );
         * }
         *
         * </listing>
         *
         * @param  the source String from which the tokens are to be extracted
         * @param  the delimiter on which the tokens are extracted
         * @return a new <code>StringTokenizer</code> as an <code>IEnumeration</code>
         *
         */
        public static function createEnumeration(source:String, delimiter:String) : IEnumeration
        {
            return new StringTokenizer( source, delimiter );
        }
        
        /**
         *
         * Retrieves the length of tokens extracted from the source
         * String
         *
         * @return the length of tokens in the source
         *
         */
        public function countTokens() : int
        {
            return tokens.length;
        }
        
        /**
         *
         * Determines if there are more tokens which have yet to be
         * retrieved via calls to <code>nextToken</code>
         *
         * @return true if more tokens remain, otherwise false
         *
         */
        public function hasMoreTokens() : Boolean
        {
            return cursor < tokens.length;
        }
        
        /**
         *
         * Retrieves the next token in the <code>StringTokenizer</code>
         * instance.
         *
         * <p>
         * If the current position of the <code>StringTokenizer</code>
         * instance is greater than or equal to the length of the source
         * a null value is returned
         * </p>
         *
         * @return the next token in the source String
         *
         */
        public function nextToken() : String
        {
            var token:String;
            
            if ( hasMoreTokens() )
            {
                token = tokens[cursor];
                cursor++;
            }
            
            return token;
        }
        
        /**
         *
         * <code>Iterator</code> implementation which determines if
         * there are more tokens which have yet to be retrieved via
         * calls to <code>nextToken</code>
         *
         * @return true if more tokens remain, otherwise false
         *
         */
        public function hasNext() : Boolean
        {
            return hasMoreTokens();
        }
        
        /**
         *
         * Retrieves the next element in the <code>StringTokenizer</code>
         * instance
         *
         * @return the next token based on the current position
         *
         */
        public function next() : *
        {
            return nextToken();
        }
        
        /**
         *
         * Resets the position of the <code>StringTokenizer</code> to
         * zero
         *
         */
        public function reset() : void
        {
            cursor = 0;
        }
        
        
        /**
         *
         * Determines the current position within the token
         *
         * @return the current index of the token
         *
         */
        public function position() : int
        {
            return cursor;
        }
        
        /**
         *
         * Remove must be implemented to provide a standard iterator
         * implementation.
         *
         * <p>
         * Typically this method would not provide an actual implementation
         * as a concrete iterator is not to perform modifications, but rather
         * simply provide a mechanism for iterating the object, thus being
         * considered as read-only. Therefore concrete implementationx should
         * throw an Exception
         * </p>
         *
         * @throws flash.errors.IllegalOperationError
         *
         */
        public function remove() : void
        {
            throw new IllegalOperationError("IllegalOperationError: remove is not supported");
        }
        
        /**
         *
         * <code>IEnumeration</code> implementation which determines if
         * there are more tokens which have yet to be retrieved via calls
         * to <code>nextToken</code>
         *
         * @return true if more tokens remain, otherwise false
         *
         */
        public function hasMoreElements() : Boolean
        {
            return hasMoreTokens();
        }
        
        /**
         *
         * Retrieves the next token in the <code>StringTokenizer</code>
         * instance
         *
         * @return the next token in the source String
         *
         */
        public function nextElement() : *
        {
            return nextToken();
        }
    }
}

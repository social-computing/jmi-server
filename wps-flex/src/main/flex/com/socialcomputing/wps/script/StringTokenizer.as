package com.socialcomputing.wps.script {
    import flash.errors.IllegalOperationError;

    public class StringTokenizer 
		
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
        protected var delimiters:String;
        
        /**
         *
         * Defines the token <code>Array</code> which contains each token
         * String extracted by the <code>StringTokenizer</code> instance
         *
         */
        protected var tokens:Vector.<String>;
        
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
        public function StringTokenizer(source:String, delimiters:String)
        {
            this.source = source;
            this.delimiters = delimiters;
            
			this.tokens  = new Vector.<String>();
			tokenize( source, delimiters);
        }

		private function tokenize(source:String, delimiters:String):void
		{
			var start:int = 0;
			for( var i:int = 0; i < source.length; ++i) {
				var token:String = tokenizeDelimiter( source, i, delimiters);
				if( token != null) {
					if( start <= i) {
						this.tokens.push( source.slice( start, i)); 
					}
					this.tokens.push( token); 
					start = i+1;
				}
			}
			if( start < source.length) {
				this.tokens.push( source.slice( start, source.length)); 
			}
		}
		
		private function tokenizeDelimiter(source:String, pos:int, delimiters:String):String {
			var car:String = source.charAt( pos);
			for( var i:int = 0; i < delimiters.length; ++i) {
				if( car == delimiters.charAt( i)) {
					return delimiters.charAt( i);
				}
			}
			return null;
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
	}        
}

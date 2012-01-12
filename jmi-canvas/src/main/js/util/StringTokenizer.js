JMI.namespace("util.StringTokenizer");

JMI.util.StringTokenizer = (function () {
    
    var tokenizeDelimiter = function(source, pos, delimiters) {
        var car = source.charAt(pos);
        var i;
        for(i = 0; i < delimiters.length; ++i) {
            if( car == delimiters.charAt(i)) {
                return delimiters.charAt(i);
            }
        }
        return null;
    };    
    
    var tokenize = function(source, delimiters) {
        var start = 0;
        var i;
        for (i = 0 ; i < source.length ; ++i) {
            var token = tokenizeDelimiter(source, i, delimiters);
            if(token != null) {
                if(start <= i) {
                    this.tokens.push(source.slice(start, i)); 
                }
                this.tokens.push(token); 
                start = i + 1;
            }
        }
        if(start < source.length) {
            this.tokens.push(source.slice(start, source.length)); 
        }
    };
        

    
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
     * @param the source String from which the tokens are to be extracted :String
     * @param the delimiter on which the tokens are extracted :String
     *
     */
    var StringTokenizer = function(source, delimiters) {
        /**
         *
         * The source <code>String</code> from which tokens are extracted
         * from based on the specified delimiter
         * :String;
         */
        this.source = source;
        
        /**
         *
         * Defines the delimiter from which the <code>source</code> String
         * tokens are to be extracted
         * :String;
         */
        this.delimiters = delimiters;
        
        
        /**
         *
         * Defines the token <code>Array</code> which contains each token
         * String extracted by the <code>StringTokenizer</code> instance
         * :Vector.<String>;
         */  
        // this.tokens  = new Vector.<String>();
        this.tokens = [];
            
        
        /**
         *
         * Stores the current position (cursor) in the tokens array from
         * which calls to <code>nextToken();</code> are based on
         * :int;
         */
        this.cursor = 0;
        tokenize(source, delimiters);
    };
    
    StringTokenizer.prototype = {
        constructor: JMI.util.StringTokenizer,

        /**
         * Retrieves the length of tokens extracted from the source
         * String
         *
         * @return the length of tokens in the source :int
         *
         */
        countTokens: function() {
            return this.tokens.length;
        },
        
        /**
         * Determines if there are more tokens which have yet to be
         * retrieved via calls to <code>nextToken</code>
         *
         * @return true if more tokens remain, otherwise false :Boolean
         *
         */
        hasMoreTokens: function()  {
            return this.cursor < this.tokens.length;
        },
        
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
         * @return the next token in the source String :String
         *
         */
         nextToken: function() {
            // :String
            var token = null;
            if (this.hasMoreTokens()) {
                token = tokens[this.cursor];
                this.cursor++;
            }
            return token;
        }
    };
    
    return StringTokenizer;
})();
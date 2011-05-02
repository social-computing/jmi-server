package com.socialcomputing.wps.script {
    import flash.geom.Rectangle;

    public class FormatToken
    {
        /**
         * Current line margins.
         */
        public var m_margin:Rectangle;
        
        /**
         * Current line maximum ascent (Top of the highest letter)
         */
        public var m_aMax:int;
        
        /**
         * Current line maximum descent (Bottom of the lowest letter)
         */
        public var m_dMax:int;
        
        /**
         * Current line text alignment flags.
         */
        public var m_flags:int;
        
        /**
         * Current line width.
         */
        public var m_width:int;
    }
}
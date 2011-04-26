package com.socialcomputing.wps.script {
    import spark.components.BorderContainer;

    public class FormatToken
    {
        /**
         * Current line margins.
         */
        protected var m_margin:BorderContainer;
        
        /**
         * Current line maximum ascent (Top of the highest letter)
         */
        protected var m_aMax:int;
        
        /**
         * Current line maximum descent (Bottom of the lowest letter)
         */
        protected var m_dMax:int;
        
        /**
         * Current line text alignment flags.
         */
        protected var m_flags:int;
        
        /**
         * Current line width.
         */
        protected var m_width:int;
    }
}
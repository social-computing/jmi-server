package com.socialcomputing.wps.script {
    /**
     * <p>Title: FormatToken</p>
     * <p>Description: A simple container to hold the current format of the tokens.<br>
     * It's also used to evaluated the current line min and max text position (ascent and descent)</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class FormatToken
    {
        /**
         * Current line margins.
         */
        public var m_margin:Insets;
        
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
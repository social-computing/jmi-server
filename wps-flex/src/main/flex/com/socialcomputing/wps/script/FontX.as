package com.socialcomputing.wps.script  {
    import flash.text.Font;
    import flash.text.TextFormat;
		
    /**
     * <p>Title: FontX</p>
     * <p>Description: A wrapper for the java.awt.Font class.<br>
     * Because Serializtion is not compatible between client and server for the original class.</p>
     * All the fields are containers so this Font can be easily changed by Swatchs.
     * This is the way a FontX is initialized after being created by the default constructor.
     * The syle bits are stored in the default flag container (FLAG_VAL).</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class FontX extends Base
    {
        /**
         * Index of the bit flag prop in VContainer table
         */
        //	public      static final int    FLAGS_VAL           = 0;
        
        /**
         * Index of the Font name prop in VContainer table
         */
        public static const NAME_VAL:int= 1;
        
        /**
         * Index of the Font size prop in VContainer table
         */
        public static const SIZE_VAL:int= 2;
        
        /**
         * A Font Buffer to reduce temporary Font object creation.
         */
        private const s_fontBuf:Array= new Array();
        
        /**
         * Convert this FontX to a java.awt.Font.
         * @param props		A property table that should hold props referenced by this containers.
         * @return			a new Font equivalent to this.
         */
        //function getFont( props:Hashtable):Font {
        // Renommage fonction
        function getFont2( props:Array):TextFormat {
            var font:TextFormat;
            var flags:int= getFlags( props ),
                size    = getInt( SIZE_VAL, props );
            var name:String= getString( NAME_VAL, props ),
                key     = name + flags + size;
            
            if (( font = TextFormat(s_fontBuf.get( key )))== null )
            {
                //font = new Font( name, flags, size );
                font = new TextFormat();
                font.font = name;
                font.size = size;
                if (flags == 1) font.bold = true;
                else if (flags == 2) font.italic = true;
                
                s_fontBuf.put( key, font );
            }
            
            return font;
        }
    }
}
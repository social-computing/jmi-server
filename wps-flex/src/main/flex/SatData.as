package  {
    /**
     * <p>Title: SatData</p>
     * <p>Description: Bufferized satellites data of the swatchs.<vr>
     * Each satellite have its SatData buffer.
     * So 2 arrays are created, one for each swatch of an ActiveZone.
     * This avoid CPU overhead when accessing to props.
     * Initally the goal was to store much more properties and graphical values.
     * As the Applet is not that slow, optimization was quickly stoped...</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class SatData
    {
        /**
         * Flags of the Satellite after they were retieved using properties.
         */
        protected var m_flags:int;
        
        /**
         * True if this Satellite is visible.
         */
        protected var m_isVisible:Boolean;
        
        /*public String getXML()
        {
        String result = new String();
        
        result = "<flag visible='"+(m_isVisible==true?"true":"false")+"'>\n";
        result += m_flags;
        result = "</flag>\n";
        
        return result;
        }	*/
    }
}
package com.socialcomputing.wps.script  {
    
    /**
     *
     * <p>Title: VContainer</p>
     * <p>Description: A Container holding an Object or the name of a property.<br>
     * A property is an entry in a Hashtable hold by a Zone (Node or link).</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    
    public class VContainer
    {
        /**
         * Object this container hold or name (String) of the property.
         */
        public var m_value:Object;
        
        /**
         * True if this is a reference. So the container hold a property name.
         */
        public var m_isBound:Boolean;
        
        /**
         * Returns XML describing the satellite
         */
        /*public String getXML()
        {
        String result = new String();
        
        result = "<value isbound='"+((m_isBound==true)?"true":"false")+"'>\n";
        result += m_value.toString()+"\n";
        result += "</value>\n";
        
        return result;
        }*/
        
        public function isBound():Boolean {
            return m_isBound;
        }
        
        /**
         * Create a Container holding a value or a reference.
         * @param value		Object or property name to hold.
         * @param isBound	True if the value is a property name.
         */
        public function VContainer(value:Object, isBound:Boolean)
        {
            m_value     = value;
            m_isBound   = isBound;
        }
    }
}
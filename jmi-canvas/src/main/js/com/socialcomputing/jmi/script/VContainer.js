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
JMI.namespace("com.socialcomputing.jmi.script.Vcontainer") = (function() {
         
    //Object this container hold or name (String) of the property.
    //:Object;
    var m_value;
        
    // True if this is a reference. So the container hold a property name.
    //:Boolean;
    var m_isBound;
        
    
    this.prototype.isBound() = function () {
        return m_isBound;
    };
        
    /**
     * Constructor
     * Create a Container holding a value or a reference.
     * 
     * @param value		Object or property name to hold.
     * @param isBound	True if the value is a property name.
     */
    this.prototype.init = function(value, isBound) {
        m_value = value;
        m_isBound = isBound;
    };
    
   
    // Public API
    return  {
        isBound: isBound,
        init: init
    };
}());
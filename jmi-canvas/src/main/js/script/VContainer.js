JMI.namespace("script.VContainer");

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
JMI.script.VContainer = (function() {

    /**
     * Constructor
     * Create a Container holding a value or a reference.
     * 
     * @param value     Object or property name to hold.
     * @param isBound   true if the value is a property name, false overwise.
     */
    var VContainer = function(value, isBound) {
        //Object this container hold or name (String) of the property.
        this.value = value;

        // True if this is a reference. So the container hold a property name.
        this.isBound = isBound || false;
    };
    VContainer.prototype = {
        constructor: JMI.script.VContainer
    };
    return VContainer;
}());
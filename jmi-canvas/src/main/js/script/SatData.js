JMI.namespace("script.SatData");

/*
 * <p>Title: SatData</p>
 * <p>Description: Bufferized satellites data of the swatchs.<vr>
 * Each satellite have its SatData buffer.
 * So 2 arrays are created, one for each swatch of an ActiveZone.
 * This avoid CPU overhead when accessing to props.
 * Initally the goal was to store much more properties and graphical values.
 * As the Applet is not that slow, optimization was quickly stoped...</p>
 * 
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * 
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.SatData = (function() {

    /**
     * SatData constructor
     * 
     * @param flags {int} a valid flags integer value 
     */    
    var SatData = function (flags) {
        if (arguments.length == 0) {
            flags = null;
        }
        this.flags = flags;
        this.isVisible = false;
    };

    SatData.prototype = {
        constructor: JMI.script.SatData
    };
    
    return SatData;
}());
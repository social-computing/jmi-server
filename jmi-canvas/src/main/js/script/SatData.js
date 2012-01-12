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
    
    // Jonathan Dray 2012.01.09
    // TODO : Voir comment sont faits les appels au constructeur
    // et modifier en conséquence 
    var SatData = function () {
        // :int;
        this.flags = null;
        //:Boolean
        this.isVisible = false;
    };

    SatData.prototype = {
        constructor: JMI.script.SatData
    };
    
    return SatData;
}());
/*
 * <p>Title: SatData</p>
 * <p>Description: Bufferized satellites data of the swatchs.<vr>
 * Each satellite have its SatData buffer.
 * So 2 arrays are created, one for each swatch of an ActiveZone.
 * This avoid CPU overhead when accessing to props.
 * Initally the goal was to store much more properties and graphical values.
 * As the Applet is not that slow, optimization was quickly stoped...</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * 
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.namespace("com.socialcomputing.jmi.script.SatData") = (function() {

    // :int;
    var _m_flags, 
         //:Boolean
        _m_isVisible;

    /**
     * Flags of the Satellite after they were retieved using properties.
     */
    // public function get m_flags():int
    this.prototype.getFlags = function() {
        return _m_flags;
    };

    // public function set m_flags(value:int):void
    this.prototype.setFlags = function(value) {
        _m_flags = value;
    };

    /**
     * True if this Satellite is visible.
     */
    // public function get m_isVisible():Boolean
    this.prototype.isVisible = function() {
        return _m_isVisible;
    };


    // public function set m_isVisible(value:Boolean):void
    this.prototype.setVisible = function(value) {
        _m_isVisible = value;
    };
    
    // Public API
    return  {
        getFlags: getFlags,
        setFlags: setFlags,
        isVisible: isVisible,
        setVisible: setVisible
    };
}());
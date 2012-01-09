/**
 * <p>Title: Transfo</p>
 * <p>Description: A geometric transformation<br>
 * This is used to transform a shape into another by translation and/or scaling.
 * It is also be used to store coordinates.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * 
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.namespace("script.Transfo") = (function() {

    /**
     * Constructor
     * Creates a new Transfo using parameters whose meaning depends on the flags argument.
     * 
     * @param dir       Angular direction in POLAR coordinates. X pos or WIDTH in cartesian coordinates. // :Number
     * @param pos       Radius in Polar coordinates. Y pos or HEIGHT in cartesian coordinates. // :Number
     * @param scl       Scale of the shape to create using this transfo. // :Number
     * @param flags     An int holding one or more bits in : {CART_BIT,ABS_BIT,INTER_BIT}. // :int
     */
    var Transfo = function(dir, pos, scl, flags) {
        this._direction = dir;
        this._position  = pos;
        this._scale     = scl;
        this._flags     = flags;
    };

    Transfo.prototype = {
        constructor: jmi.script.Transfo,
        /**
         * Transform an already defined Transfo using this one.
         * 
         * @param transfo       A Transfo that define a position and scale to transform. It should be of the same type as this.
         *                      This means its flags should be the same. // :Transfo
         * @param isForward     Useless now. // :Boolean
         * @return              A new Transfo resulting of the compositiong with this.
         *                      Or this if t is null or not compatible.
         */
        transform: function(transfo, isForward) {
            // TODO : portage, add namespace prefix
            if (transfo == null || this._flags != transfo._flags) {
                return new jmi.script.Transfo(this._direction, this._position, this._scale, this._flags);
            }
            else {
                return new jmi.script.Transfo(this._direction + transfo._direction, this._position * transfo._position,
                                              this._scale * transfo._scale, this._flags);
            }
        },
        
        /**
         * Return the translation corresponding to this transformation.
         * This should have the CART_BIT.
         * @return A new Point holding the coordinates of the translation. // :Point 
         */
        getCart: function() {
            return new jmi.script.Point(this._direction, this._position);  
        }
    };
    
    return Transfo;
}());

// Constants
/**
 * True if this translation use cartesian coordinates or false if they are polar.
 * Cartesian are used for bitmaps (font, images) and polar for subzone positioning around the Place.
 */
jmi.script.Transfo.CART_BIT = 0x01;

/**
 * True if the translation is in absolute units(pixels), false if it is relative to the shape center.
 * ABS_BIT is used to locate font at the exact pixel and thus make useful masks.
 */
jmi.script.Transfo.ABS_BIT = 0x02;

/**
 * True if the position is bilineary interpolated.
 * In this case the position depends on the shape who use this Transfo.
 */
jmi.script.Transfo.INTER_BIT = 0x04;
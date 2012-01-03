/*
 * <p>Title: ActiveZone</p>
 * <p>Description: A graphical zone holding properties.<br>
 * This base class can be a clusterized zone (subZone) and
 * through BagZone it can also be a parent zone (superZone).
 * This kind of zone doesn't contains any graphical informations.</p>
 */
JMI.namespace("com.socialcomputing.wps.script.ActiveZone") = (function() {
	// Private methods
	
    	// Swatch used to render this zone at rest.
    var m_restSwh = com.socialcomputing.wps.script.Swatch,
     	//Swatch used to render this zone when it is current (hovered).
    	m_curSwh = com.socialcomputing.wps.script.Swatch,
        // Flags holding the previously defined bits (XXX_BIT).
        m_flags,
		m_props, //Array
        /*
         * Bounding-Box of this zone including its subZones.
         * The BBox is the union of the rest and current swatch BBoxs and a small margin.
         */
        m_bounds, //Rectangle;
         //Flag indicating which of the 32 possible selections are active for this zone.
        m_selection,
         //Parent of this zone if it is clusterized or null if this is already a BagZone.
        m_parent, //:ActiveZone;
        /*
         * Fast graphical data lookup for the rest Swatch Satellites.
         * Not used enough, could improve the performance if more was stored here...
         */
        m_restData, //:Vector.<SatData>;
        /*
         * Fast graphical data lookup for the current Swatch Satellites.
         * Not used enough, could improve the performance if more was stored here...
         */
        m_curData, //:Vector.<SatData>;
         // HTMLText dictionary to avoid unnecessary calcs.
        m_datas, //:Dictionary;

		Constr = function() {
		}
		Constr.prototype = {
			constructor: com.socialcomputing.jmi.script.ActiveZone,
			version: "2.0"
		}
		return Constr;
}());

// Constantes
// Bit indicating that subnodes of this are located on one side.
com.socialcomputing.wps.script.ActiveZone.SIDE_BIT = 0x04;
// Bit indicating that subnodes are located on the left side.
com.socialcomputing.wps.script.ActiveZone.LEFT_BIT = 0x08;
// Bit indicating invisibility.
com.socialcomputing.wps.script.ActiveZone.INVISIBLE_BIT = 0x10;

com.socialcomputing.jmi.script.ActiveZone.prototype.getRestSwatch = function () {
    return m_restSwh;
};
com.socialcomputing.jmi.script.ActiveZone.prototype.getCurSwatch = function () {
    return m_curSwh;
};
/*
 * Sets the two swatchs of this zone.
 * This is used in PlanGenerator to setup this zone's swatchs.
 * @param restSwh   Swatch used to render this when it is at rest.
 * @param curSwh    Swatch used to render this when it is hovered.
 */
com.socialcomputing.jmi.script.ActiveZone.prototype.setSwatchs = function ( restSwh, curSwh) {
    m_restSwh   = restSwh;
    m_curSwh    = curSwh;
};
/*
 * Perform basic buffer initialization to enhance real time performance.
 * This include transforming selection prop to an int flag,
 * copying Env props reference in this prop table and
 * initializing satellite data for both swatchs.
 * TODO : Remove graphics parameter : unused
 * 
 * @param applet    WPSApplet owning this.
 * @param g         A graphics compatible with the one that will be used for painting.
 * @param isFirst   True if init called for the first time.
 */
com.socialcomputing.jmi.script.ActiveZone.prototype.init = function (applet, s, isFirst) {
    if ( isFirst )  // One time init
    {
        var sel = m_props["SELECTION"];
        if ( sel != null )
        {
            m_selection = sel;
        }
        
        // Quick access to Env props
		m_props["ENV"] = applet.env.m_props;
        m_datas = new Object();
    }
    
    var isSuper = this instanceof com.socialcomputing.jmi.script.BagZone;
    
    m_restData = m_restSwh.evalSatData( applet, this, isSuper);
    
    if ( m_curSwh != null )
    {
        m_curData   = m_curSwh.evalSatData( applet, this, isSuper );
    }
};
/*
 * Draw this zone on a specified Graphics using the rest or cur swatch.
 * @param applet    WPSApplet owning this zone.
 * @param g         A Graphics on which this must be painted.
 * @param isCur     True if this is the current zone (hovered) of the plan.
 * @param isFront   True if this zone appears on top of ghosted zones.
 * @param showTyp   The type of Satellite to show (SEL, TIP, BASE, ALL). See Satellite.XXXX_TYP.
 * @param showLinks True if we only wants to paint links.
 */
com.socialcomputing.jmi.script.ActiveZone.prototype.paint = function (applet, s, isCur, isFront, showTyp, showLinks) {
    if( (m_flags & INVISIBLE_BIT) != 0) return;
    var swatch = isCur ? m_curSwh : m_restSwh;
    
    swatch.paint( applet, s, this, isCur, isFront, showTyp, showLinks );
};
 /*
 * Get this parent zone if it exists.
 * @return	The BagZone holding this or null if this is a BagZone.
 */
com.socialcomputing.jmi.script.ActiveZone.prototype.getParent = function () {
    return m_parent == null ? this : m_parent;
};

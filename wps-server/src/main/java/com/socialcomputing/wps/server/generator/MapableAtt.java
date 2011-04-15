package com.socialcomputing.wps.server.generator;

import java.util.ArrayList;

/**
 * <p>Title: MapableAtt</p>
 * <p>Description: Attributes coming from the analysis should implement this interface so Generator can use them.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public interface MapableAtt extends Recommendable
{
	/**
	 * Gets this list of links.
	 */
	public ArrayList        getLinks            ( );

	/**
	 * If this Attribute is clusterized, gets its parent.
	 * @return This parent or null if this is a cluster Attribute.
	 */
	public MapableAtt       getParent           ( );

	/**
	 * If this Attribute is a cluster, gets its children.
	 * @return This children or null if this is a clusterized Attribute.
	 */
	public ArrayList        getChildren         ( );

	/**
	 * Returns wether this is an Attribute from the Plan's base.
	 * @return True if this is in base.
	 */
	public boolean          isBase              ( );

	/**
	 * Gets this size value. This is used as a metric to evaluate the radius of graphical Places.
	 * @return This size. Bounds are free as Generator will automaticaly normalize this value.
	 */
	public float            getSize             ( );

	/**
	 * Gets this weight value. This is used as a metric to evaluate the importance of graphical Places.
	 * @return This weight. Bounds are free as Generator will automaticaly normalize this value.
	 */
	public float            getWeight           ( );

	/**
	 * Sets a reference to this graphical Data.
	 * @param data	A NodeMapData matching this.
	 */
	public void             setMapData          ( NodeMapData data );

	/**
	 * Gets the reference to this graphical Data.
	 * @return	A NodeMapData matching this.
	 */
	public NodeMapData      getMapData          ( );
}

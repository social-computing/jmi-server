package com.socialcomputing.wps.server.generator;


/**
 * <p>Title: MapableLink</p>
 * <p>Description: Links coming from the analysis should implement this interface so Generator can use them.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public interface MapableLink extends Recommendable
{
	/**
	 * Gets one of the two Attribute tied to this. It is not the one returned by getToAtt().
	 * @return	A MapableAtt corresponding to this m_from field.
	 */
	public MapableAtt       getFromAtt          ( );

	/**
	 * Gets one of the two Attribute tied to this. It is not the one returned by getFromAtt().
	 * @return	A MapableAtt corresponding to this m_to field.
	 */
	public MapableAtt       getToAtt            ( );

	/**
	 * Sets a reference to this graphical Data.
	 * @param data	A LinkMapData matching this.
	 */
	public void             setMapData          ( LinkMapData data );

	/**
	 * Gets the reference to this graphical Data.
	 * @return	A LinkMapData matching this.
	 */
	public LinkMapData      getMapData          ( );
}

package com.socialcomputing.utils.geom.relax;

public interface RelaxableLink
{
	public RelaxableNode    getFromNode     ( );

	public RelaxableNode    getToNode       ( );

	/**
	 * Set the associated relaxation data.
	 * @param data	An object used for relaxation purpose that must be stored somewhere
	 */
	public void             setRelaxData    ( LinkRelaxData data );

	/**
	 * Gets the associated relaxation data.
	 * @return		associated relaxation data
	 */
	public LinkRelaxData    getRelaxData    ( );

	/**
	 * Gets this length at rest.
	 * @return		prefered length in units of this link
	 */
	public float            getLength      ( );

	/**
	 * Gets this length at rest.
	 * @return		prefered length in units of this link
	 */
	public float            getStiffness   ( );

	/**
	 * Gets this width.
	 * @return		width in units
	 */
	public float            getWidth      	( );

	/**
	 * Gets this width.
	 * @return		width in units
	 */
	public void				setWidth      	( float width );

	/**
	 * Gets a label to be displayed. Only useful for test.
	 * @return		a String label
	 */
	public String           getLabel        ( );

	/**
	 * Returns false if this must not be relaxed.
	 * Or if one of its node must not be relaxed!
	 * Or if the two nodes inertia is 1
	 * @return		true if this must be relaxed
	 */
	public boolean          isRelaxable     ( );
}

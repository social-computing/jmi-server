package com.socialcomputing.utils.geom.relax;

import com.socialcomputing.utils.geom.Localisable;
import com.socialcomputing.utils.geom.Vertex;

public interface RelaxableNode extends Localisable
{
	/**
	 * Set the associated relaxation data.
	 */
	public void             setRelaxData        ( NodeRelaxData data );

	/**
	 * Gets the associated relaxation data.
	 */
	public NodeRelaxData    getRelaxData        ( );

//	/**
//	 * Gets this current position.
//	 */
//	public Vertex           getPos              ( );

	/**
	 * Gets this radius
	 */
	public float            getSize             ( );

	/**
	 * Gets this radius
	 */
	public float            getMaxSize          ( );

	/**
	 * Gets this radius
	 */
	public float            getSepSize          ( );

	/**
	 * Returns inertia factor. 0 means free moves, 1 means locked.
	 */
	public float            getInertia          ( );

	/**
	 * Returns false if this must not be relaxed.
	 */
	public boolean          isRelaxable         ( );

	/**
	 * Returns false if this doesn't repulse the other nodes.
	 */
	public boolean          isRepulsive         ( );

	/**
	 * Gets this links.
	 */
	public RelaxableLink[]  getLinks            ( );

	/**
	 * Gets this weight. Used for sorting purpose.
	 */
	public float            getWeight           ( );

	/**
	 * Gets a label to be displayed. Only useful for test.
	 */
	public String           getLabel            ( );

	public void             initPos           	( Vertex pos );

	public void             setPos             	( Vertex pos );

	public void             setSize             ( float size );
}

package com.socialcomputing.wps.server.generator;

import java.awt.Point;

import com.socialcomputing.wps.client.applet.ActiveZone;
import com.socialcomputing.wps.client.applet.BagZone;
import com.socialcomputing.wps.client.applet.LinkZone;
import com.socialcomputing.utils.geom.relax.NodeRelaxData;
import com.socialcomputing.utils.geom.triangle.QuadEdge;
import com.socialcomputing.utils.math.Bounds2D;

/**
 * <p>Title: LinkFakeData</p>
 * <p>Description: A Link(street) for aesthetic purpose without any meaning for the Plan.<br>
 * Such a Link have a real Node on one end and nothing on the other one.
 * The fake end is just a Point location outside the screen.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class LinkFakeData
{
	/**
	 * True if the 'from' Node is just a point outside the screen.
	 */
	protected	boolean		m_isFakeFrom;

	/**
	 * A Quad Edge comming from the Delaunay Tesselation and describing this.
	 */
	protected	QuadEdge	m_edge;

	/**
	 * Creates a new LinkFakeData
	 * @param edge			The QuadEdge describing this link.
	 * @param isFakeFrom	True if the 'from' Node is fake.
	 */
	public LinkFakeData( QuadEdge edge, boolean isFakeFrom )
	{
		m_isFakeFrom	= isFakeFrom;
		m_edge			= edge;
	}

	/**
	 * Converts this to a Zone for the WPSApplet.
	 * @param mapper	The mapper holding this.
	 * @return			A new ActiveZone matching this.
	 */
	public ActiveZone toZone( Mapper mapper )
	{
		final Float	length	= new Float( 1 );
		//boolean     isFakeFrom, isFakeTo;
		Point       fromPos, toPos;
		BagZone     from, to;
		MapData		mapDat	= mapper.m_protoPlan.m_mapDat;
		Bounds2D	mapBnds	= mapper.m_relaxer.getBounds(),
					winBnds	= mapDat.m_winBnds;
		//float		norm;
		NodeMapData	node;
		Float 		width	= new Float( mapBnds.project( mapDat.m_bounds[MapData.L_WIDTH_BND].m_min, winBnds ));

		if ( m_isFakeFrom )
		{
			from		= null;
			fromPos		= mapBnds.project( m_edge.getFromPos(), winBnds ).toPoint();

			node		= (NodeMapData)(((NodeRelaxData)m_edge.getTo()).getNode());
			to			= node.m_zone;
			toPos		= node.m_clientPos;
		}
		else
		{
			to			= null;
			toPos		= mapBnds.project( m_edge.getToPos(), winBnds ).toPoint();

			node		= (NodeMapData)(((NodeRelaxData)m_edge.getFrom()).getNode());
			from		= node.m_zone;
			fromPos		= node.m_clientPos;
		}

		LinkZone    linkZone    = new LinkZone( from, to );

		// The length is set to max so they are as weak as possible.
		linkZone.put( "_LENGTH", length );
		linkZone.put( "_SCALE", width );
		linkZone.put( "_VERTICES", new Point[]{ fromPos, toPos });
		linkZone.m_flags = m_isFakeFrom ? LinkZone.FAKEFROM_BIT : LinkZone.FAKETO_BIT;

		return linkZone;
	}
}

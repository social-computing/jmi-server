package com.socialcomputing.wps.server.generator;

import java.awt.Point;
import java.util.Comparator;

import com.socialcomputing.utils.geom.relax.Link;
import com.socialcomputing.wps.client.applet.ActiveZone;
import com.socialcomputing.wps.client.applet.BagZone;
import com.socialcomputing.wps.client.applet.LinkZone;

/**
 * <p>Title: LinkMapData</p>
 * <p>Description: A Link matching an AttributeLink that is relaxable.<br>
 * It holds a reference to the matching AttributeLink and has a mass.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class LinkMapData extends Link
{
	/**
	 * The matching AttributeLink.
	 */
	protected   AttributeLink       m_link;

	/**
	 * True if its two nodes are in base.
	 * A simple speedup.
	 */
	protected   boolean             m_isBase;

	/**
	 * True if one node is in base and the other isn't.
	 * A simple speedup.
	 */
	protected   boolean             m_isMixed;

	/**
	 * True if its two nodes are in base.
	 * A simple speedup.
	 */
	protected   boolean             m_isRef;

	/**
	 * A simple metric to weight this.
	 */
	protected	float				m_mass;

	/**
	 * A Comparator to sort links before sending them to the WPSApplet.
	 * Reference links are always before the others.
	 * Links of the same category are sorted so the shortest are first then for the same length the thickest.
	 */
	protected static final Comparator	s_comp	= new Comparator()
	{
		public int compare( Object o1, Object o2 )
		{
			LinkMapData	l1		= (LinkMapData)o1,
						l2		= (LinkMapData)o2;
			boolean		isRef1	= l1.isRef(),
						isRef2	= l2.isRef();

			if ( isRef1 == isRef2 )
			{
				int	d	= l2.m_link.m_length - l1.m_link.m_length;

				return d == 0 ? l1.m_link.m_size - l2.m_link.m_size : d;
			}
			else	return isRef2 ? -1 : 1;
		}
	};

	/**
	* Creates a new LinkMapData knowing its matching AttributeLink.
	* The link m_mapData field is set to this.
	* @param link		The analysis equivalent of this.
	* @param mapDat		MapData from the Mapper that holds this.
	*/
	public LinkMapData( AttributeLink link, MapData mapDat )
	{
		super( link.getFromAtt().getMapData(), link.getToAtt().getMapData());

		m_link      = link;
		m_isBase    = link.isBase();
		m_isMixed   = link.isMixed();
		m_isRef     = link.isRef();
		m_length    = mapDat.getLinkLen( this );
		m_width    	= mapDat.getLinkWidth( this );
		m_mass   	= mapDat.getLinkMass( this );
		m_stiffness = mapDat.getLinkStiffness( this );

		link.setMapData( this );
	}

//	public LinkMapData( Node from, Node to )
//	{
//		super( from, to );
//
//		m_width    	= 1;
//	}

	static int	s_linkID	= 0;

	/**
	 * Returns a label to be displayed on the GUI when showing the relaxation.
	 * This is for debug purpose.
	 * @return	A text associated with this.
	 */
	public String getLabel()
	{
//		return	String.valueOf( m_link.m_isSep );
		return	String.valueOf( s_linkID ++ );
//		return String.valueOf( m_length );
//		return String.valueOf( m_stiffness );
//		return m_link.isBase() ? "base" :( m_link.isMixed() ? "mixed" : "node" );
	}

	/**
	 * Knowing one Node of this Link, gets the other one.
	 * @param node	The other Node of this Link
	 * @return		The NodeMapData of this link that is not node.
	 */
	protected NodeMapData getOtherNode( NodeMapData node )
	{
		return (NodeMapData)( m_from == node ? m_to : m_from );
	}

	/**
	 * Synchronize this with its relax data.
	 * Sets the exact width of this before creating the Zone for the WPSApplet.
	 * @param mapDat	MapData associated with the Mapper holding this.
	 */
	public void syncRelaxData( MapData mapDat )
	{
		m_relaxDat.setWidth( mapDat.project( m_link.m_size, ProtoPlan.L_ALLWIDTH_BND, MapData.L_WIDTH_BND ));
	}

	/**
	 * Convert this to a Zone for the WPSApplet.
	 * @param mapper	Mapper holding this.
	 * @return	A new LinkZone matching this.
	 */
	public ActiveZone toZone( Mapper mapper, boolean displayEmptyLinks )
	{
		boolean     isFakeFrom, isFakeTo;
		Point       fromPos, toPos;
		BagZone     from, to;
		MapData		mapDat	= mapper.m_protoPlan.m_mapDat;
		float		norm;

		if ( m_from instanceof NodeMapData )
		{
			isFakeFrom  = false;
			from        = ((NodeMapData)m_from ).m_zone;
			fromPos     = ((NodeMapData)m_from ).m_clientPos;
		}
		else
		{
			isFakeFrom  = true;
			from        = null;
			fromPos     = m_from.getPos().toPoint();
		}

		if ( m_to instanceof NodeMapData )
		{
			isFakeTo    = false;
			to          = ((NodeMapData)m_to ).m_zone;
			toPos       = ((NodeMapData)m_to ).m_clientPos;
		}
		else
		{
			isFakeTo    = true;
			to          = null;
			toPos       = m_to.getPos().toPoint();
		}

		LinkZone    linkZone    = new LinkZone( from, to );

		norm	= isFakeFrom || isFakeTo ? 0.f : mapDat.normalize( m_link.m_length, ProtoPlan.L_ALLLENGTH_BND );

		linkZone.put( "_SIZE", new Integer( m_link.m_size ));
		linkZone.put( "_LENGTH", new Float( norm ));
		linkZone.put( "_SCALE", new Float( m_width ));
		linkZone.put( "_VERTICES", new Point[]{ fromPos, toPos });
		linkZone.m_flags = isFakeFrom ? LinkZone.FAKEFROM_BIT :( isFakeTo ? LinkZone.FAKETO_BIT : 0 );

		if( !displayEmptyLinks && m_link.m_size == 0)
			linkZone.m_flags |= ActiveZone.INVISIBLE_BIT;

		return linkZone;
	}

	/**
	 * Returns wether this is a reference link.
	 * @return	True if both of this nodes comes from the reference.
	 */
	public boolean isRef()
	{
		return m_isRef;
	}
}

package com.socialcomputing.wps.server.generator;

import java.awt.Point;
import java.util.ArrayList;

import com.socialcomputing.utils.geom.relax.Node;
import com.socialcomputing.utils.geom.relax.NodeRelaxData;
import com.socialcomputing.wps.client.applet.ActiveZone;
import com.socialcomputing.wps.client.applet.BagZone;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.Bounds2D;

/**
 * <p>Title: NodeMapData</p>
 * <p>Description: A graphical node equivalent to an Analysis ProtoAttribute.<br>
 * Only Clusters are visible so NodeMapData can't be clusterized Attributes.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class NodeMapData extends Node
{
	/**
	 * Matching ProtoAttribute in the ProtoPlan.
	 * This is set in the constructor.
	 */
	protected	ProtoAttribute	m_att      = null;

	/**
	 * Matching BagZone in the WPSApplet Plan.
	 * This is set at the end of the plan generation.
	 */
	protected 	BagZone   		m_zone;

	/**
	 * The final location of this in the Applet.
	 * It is stored so WPSApplet links can reference the same anchor point as the nodes.
	 * So resizing nodes also resize links in the WPSApplet without anymore computations.
	 */
	protected   Point    		m_clientPos;

	/**
	 * True if this is a base node.
	 */
	protected   boolean     	m_isBase;

	/**
	 * Mass evaluated using this size and its links mass.
	 */
	protected   float     		m_mass;

	/**
	 * An ID used for debuging.
	 */
	//private int             m_id;

	/**
	 * Creates a new NodeMapData using its matching Attribute.
	 * Uses a MapData to evaluate the graphical value from the analysis one.
	 * @param att		ProtoPlan equivalent.
	 * @param id		An ID for debug purpose.
	 * @param mapDat	Map data from the Mapper holding this.
	 */
	public NodeMapData( ProtoAttribute att, int id, MapData mapDat )
	{
		super();

		m_att           = att;
		//m_id            = id;
		m_isBase        = att.isBase();
		m_mass   		= mapDat.getNodeMass( this );
	}

	/**
	 * Creates the only node when the Plan is degenerated.
	 * @param att		ProtoPlan only cluster.
	 * @param winBnds	Window bounds to center this.
	 */
	public NodeMapData( ProtoAttribute att, Bounds2D winBnds )
	{
		super();

		m_att           = att;
		m_isBase        = att.isBase();
		m_pos			= winBnds.getCenter();
		m_size			= .1f *( winBnds.isWidthMin() ? winBnds.getWidth() : winBnds.getHeight());
	}

	/**
	 * Initialize this graphical data.
	 * Links must already have been initialized that's why this is not done in the constructor.
	 * The maxSize radius of the repulsion shield is evaluated as the half of this average links length.
	 * The maxSize can't be smaller than the size.
	 * The sepSize is set as 1.5 x maxSize (base) or 2 x maxSize (ext).
	 * The weight is evaluated using mass of this and its links in a recursive process.
	 * @param mapDat	Map data from the Mapper holding this.
	 */
	protected void init( MapData mapDat )
	{
		m_size          = mapDat.getNodeSize( this );
		m_inertia       = 0;

		if ( m_isBase )
		{
			LinkMapData   	linkDat;
			float           sLen    = 0;
			int				i, j, n	= m_links.length;

			for ( i = 0, j = 0; i < n; i ++ )
			{
				linkDat	= (LinkMapData)m_links[i];

				if ( linkDat.m_isBase )
				{
					sLen     += linkDat.getLength();
					j ++;
				}
			}

			if ( j > 0 )
			{
				m_maxSize    = .5f *( sLen / j );
				if ( m_maxSize < m_size )	m_maxSize	= m_size;
			}
			else
			{
				m_maxSize	= m_size;
			}

			m_sepSize	= 1.5f * m_maxSize;
			m_weight	= getWeight( 3, null );
		}
		else
		{
			LinkMapData   	linkDat;
			float           len,
							minLen  = Float.MAX_VALUE;
			int				i, n	= m_links.length;

			for ( i = 0; i < n; i ++ )
			{
				linkDat	= (LinkMapData)m_links[i];

				if ( !linkDat.m_isMixed )
				{
					len     = linkDat.getLength();

					if ( len < minLen )	minLen = len;
				}
			}

			if ( n > 0 && minLen != Float.MAX_VALUE )
			{
				m_maxSize    = .5f * minLen;
				if ( m_maxSize < m_size )	m_maxSize	= m_size;
			}
			else
			{
				m_maxSize	= m_size;
			}

			m_sepSize	= 2.f * m_maxSize;
			m_weight	= getWeight( 3, null );
		}
	}

	/**
	 * A Shortcut to get this weight while avoiding to evaluate it at each call.
	 * The weight is evaluated once and stored in m_weight.
	 * This metric is used to sort nodes. Heavier nodes are relaxed first because they generate the others.
	 * @return	This weight as a weighted sum of it's neighbours weights including its links.
	 */
	public float getWeight()
	{
		return m_weight > 0 ? m_weight : getWeight( 2, null );
	}

	/**
	 * Gets a label that can be displayed in the GUI for debug purpose.
	 * @return	A String label.
	 */
	public String getLabel()
	{
		return String.valueOf( m_weight );
	}

	/**
	 * Gets this weight by crawling the net around until a depth is reach.
	 * @param depth		Number of iterration of the recursive process.
	 * @param father	The node that called this method. Used to avoid coming back while crawling.
	 * @return			This weight as the sum of its neighbouring nodes and links.
	 */
	protected float getWeight( int depth, Node father )
	{
		NodeMapData     node;
		LinkMapData     link;
		int             i, n    = m_links.length;
		float	        w       = m_mass;

		if ( n == 1 )	depth = 1;

		if ( depth > 0 )
		{
			for ( i = 0; i < n; i ++ )
			{
				link    = (LinkMapData)m_links[i];
				node    = (NodeMapData)link.getOtherNode( this );

				if ( node != father && node.m_isBase )
				{
					w  += link.m_mass * node.getWeight( depth - 1, this );
				}
			}
		}

		return w;
	}

	/**
     * @return the m_clientPos
     */
    public Point getM_clientPos() {
        return m_clientPos;
    }

    /**
	 * Gets the link between this and another node.
	 * @param to	The other node of the link to find.
	 * @return		The link between this and 'to' or null if there is none.
	 */
	public AttributeLink getLinkTo( NodeMapData to )
	{
		return ((ProtoAttribute)m_att ).findLink((ProtoAttribute)to.m_att );
	}

	/**
	 * Creates a WPSApplet zone and its children equivalent to this.
	 * The location of the zone is stored in the VERTICE property,
	 * and its size in the SCALE property.
	 * The children (ActiveZones) created are stored directly in the WPSApplet Plan node array.
	 * @param mapper
	 * @return	a new BagZone matching this.
	 */
	public ActiveZone toZone( Mapper mapper, int index)
	{
		m_clientPos	= m_pos.toPoint();

		Point[]     	points  	= { m_clientPos };
		ActiveZone[]    subZones    = null;
		ActiveZone      subZone;
		ArrayList		children	= m_att.getChildren();
		Float       	size    	= new Float( m_size );

		if ( children != null )
		{
			int	i, n	= children.size();

			if ( n > 0 )
			{
				subZones	= new ActiveZone[n];

				for ( i = 0; i < n; i ++ )
				{
					subZone     = new ActiveZone();
					subZone.put( "ATT", children.get( i ));
					subZones[i] = subZone;
					mapper.m_plan.m_nodes[mapper.m_curNode++]	= subZone;
				}
			}
		}
		m_zone = new BagZone( subZones, index);

		m_zone.put( "_SCALE", size );
		m_zone.put( "_VERTICES", points );

		return m_zone;
	}

	/**
	 * Evaluate the initial location of a node to relax.
	 * If the node comes from base, the super method is used.
	 * Else, the position is a projection on the base surounding circle.
	 * The best link to the base is choosen and it's base node is then projected.
	 * @param center	Center of the map bounding box.
	 * @param base		Base surounding pseudo-node or null if the base relax is not finished.
	 */
	public void initPos( Vertex center, NodeRelaxData base )
	{
		if ( m_isBase )
		{
			super.initPos( center );
		}
		else
		{
			NodeMapData	node;
			int         i, n	= m_links.length;
			float		length	= 1.2f * base.getSize();
			Vertex		basePos	= base.getPos();

			for ( i = 0; i < n; i ++ )
			{
				node	= ((LinkMapData)m_links[i]).getOtherNode( this );

				if ( node.m_isBase )
				{
					Vertex	pos	= new Vertex( node.m_relaxDat.getPos());

					if ( base != null )
					{
						pos.subThis( basePos );
						pos.resize( length );
						pos.addThis( basePos );
					}

					m_relaxDat.setPos( pos );
					break;
				}
			}

			if ( i == n )	// no link with base, lets try classic init!
			{
				super.initPos( center );
			}
		}
	}
}

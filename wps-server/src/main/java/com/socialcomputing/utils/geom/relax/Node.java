package com.socialcomputing.utils.geom.relax;

import java.util.ArrayList;

import com.socialcomputing.utils.geom.Localisable;
import com.socialcomputing.utils.geom.Vertex;

public class Node implements RelaxableNode
{
	//private static final float  s_repMin = 1.f;

	protected   ArrayList<Link> m_tmpLinks;
	protected   Vertex          m_pos;
	protected   float           m_size;
	protected   float           m_maxSize;
	protected   float           m_sepSize;
	protected   float           m_inertia;
	protected   boolean         m_isRelaxable;
	protected   boolean         m_isRepulsive;
	protected   Link[]          m_links;
	protected   NodeRelaxData   m_relaxDat;
	protected   float           m_weight;

	public Node()
	{
		this( 1, 0, true, true );
	}

	public Node( Localisable loc )
	{
		m_pos			= loc.getPos();
		m_tmpLinks  	= new ArrayList<Link>();
	}

	public Node( float size, float inertia, boolean isRelaxable, boolean isRepulsive )
	{
		m_pos       	= null;
		m_size      	= size;
		m_inertia   	= inertia;
		m_isRelaxable   = isRelaxable;
		m_isRepulsive   = isRepulsive;
		m_tmpLinks  	= new ArrayList<Link>();
	}

	public void initPos( Vertex center )
	{
		m_relaxDat.initPos( center );
	}

	public NodeRelaxData getRelaxData()
	{
		return m_relaxDat;
	}

	public void setRelaxData( NodeRelaxData data )
	{
		m_relaxDat = data;
	}

//	public void paint( Graphics g, int id, EZFlags flags, int dx, int dy )
//	{
//		m_relaxDat.paint( g, id, flags, dx, dy );
//	}

//	public boolean isReady()
//	{
//		return m_relaxDat.isReady();
//	}

	public RelaxableLink[] getLinks()
	{
		return m_links;
	}

	public Vertex getPos()
	{
		return m_pos;
	}

	public float getSize()
	{
		return m_size;
	}

	public float getMaxSize()
	{
		return m_maxSize;
	}

	public float getSepSize()
	{
		return m_sepSize;
	}

	protected void init()
	{
		// eval repMax
		//LinkRelaxData   linkDat;
		float           len,
						minLen  = Float.MAX_VALUE;
						//sLen    = 0;
		int				i, n	= m_links.length;

		for ( i = 0; i < n; i ++ )
		{
			len     = m_links[i].m_length;

			if ( len < minLen ) minLen = len;
		}

		if ( n > 0 )
		{
			m_maxSize    = .5f * minLen;
			if ( m_maxSize < m_size )	m_maxSize	= m_size;
		}
		else
		{
			m_maxSize	= m_size;
		}

		m_sepSize	= m_maxSize * 2.f;
		m_weight	= getWeight( 3, null );
	}

	public float getInertia()
	{
		return m_inertia;
	}

	public boolean isRelaxable()
	{
		return m_isRelaxable;
	}

	public boolean isRepulsive()
	{
		return m_isRepulsive;
	}

	public float getWeight()
	{
		return m_weight;// > 0 ? m_weight : getWeight( 3, null );
	}

	protected float getWeight( int depth, Node father )
	{
		final   float   lMax    = 100.f;
		Node    node;
		Link    link;
		int     i, n    = m_links.length;
		float	w       = depth;

		if ( father == null && n == 1 )
		{
			w *= lMax / m_links[0].m_length;
		}

		if ( depth > 0 && n > 1)
		{
			for ( i = 0; i < n; i ++ )
			{
				link    = m_links[i];
				node    = (Node)link.getOtherNode( this );

				if ( node != father )
				{
					w  += ( lMax / link.m_length )* node.getWeight( depth - 1, this );
				}
			}
		}

		return w;
	}

	public String getLabel()
	{
		return String.valueOf( getWeight());
//		return m_pos != null ? m_pos.toString(): "void";
	}

	public void initLinks()
	{
		m_links = m_tmpLinks.toArray( new Link[m_tmpLinks.size()]);
	}

	protected void addLink( Link link )
	{
		m_tmpLinks.add( link );
	}

	public void setPos( Vertex pos )
	{
		m_pos	= pos;
	}

	public void setSize( float size )
	{
		m_size	= size;
	}
}

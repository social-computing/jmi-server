package com.socialcomputing.utils.geom.relax;


public class Link implements RelaxableLink
{
	protected   float           m_length;
	protected   float           m_stiffness;
	protected   float           m_width;
	protected   Node            m_from;
	protected   Node            m_to;
	protected   boolean         m_isRelaxable   = true;
	protected   LinkRelaxData   m_relaxDat;

	public Link( Node from, Node to )
	{
		m_from          = from;
		m_to            = to;

		from.addLink( this );
		to.addLink( this );
	}

	public Link( Node from, Node to, float len, float stiffness, float width, boolean isRelaxable )
	{
		m_from			= from;
		m_to			= to;
		m_length		= len;
		m_stiffness		= stiffness;
		m_width			= width;
		m_isRelaxable	= isRelaxable;

		from.addLink( this );
		to.addLink( this );
	}

	public void setRelaxData( LinkRelaxData data )
	{
		m_relaxDat  = data;
	}

	public LinkRelaxData getRelaxData()
	{
		return m_relaxDat;
	}

	public RelaxableNode getFromNode()
	{
		return m_from;
	}

	public RelaxableNode getToNode()
	{
		return m_to;
	}

	public float getLength()
	{
		return m_length;
	}

	public float getStiffness()
	{
		return m_stiffness;
	}

	public float getWidth()
	{
		return m_width;
	}

	public void setWidth( float width )
	{
		m_width	= width;
	}

	public String getLabel()
	{
		return String.valueOf( m_length );
	}

	public boolean isRelaxable()
	{
		return  m_isRelaxable;
	}

//	public void paint( Graphics g, int id, EZFlags flags, int dx, int dy )
//	{
//		m_relaxDat.paint( g, id, flags, dx, dy );
//	}

//	public boolean isReady()
//	{
//		return m_relaxDat.isReady();
//	}

	protected RelaxableNode getOtherNode( RelaxableNode node )
	{
		return m_from == node ? m_to : m_from;
	}
}
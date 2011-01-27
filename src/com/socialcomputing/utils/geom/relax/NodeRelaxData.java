package com.socialcomputing.utils.geom.relax;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.geom.Localisable;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.Bounds2D;

public final class NodeRelaxData extends RelaxData implements Localisable
{
	public	static final	int     TERMINATOR_BIT	= 0x0001;
//	protected   static final    float   s_repMin    = 1.f;

	protected   int             m_linksCnt  = 0;
	protected   float           m_linksLen  = 0;
//	protected   float           m_repMax    = 2.f;

	protected   Vertex          m_rep;
	protected   Vertex          m_tens;
	protected   Vertex          m_rot;
	protected   Vertex          m_linkRep;
	protected   Vertex          m_crossRep;

	protected   Vertex          m_repBak;
	protected   Vertex          m_tensBak;
	protected   Vertex          m_rotBak;
	protected   Vertex          m_linkRepBak;
	protected   Vertex          m_crossRepBak;

	protected   Vertex          m_pos;
	protected   float           m_size;
	protected   float           m_maxSize;
	protected   float           m_sepSize;
	protected   float           m_inertia;
	protected   float           m_weight;
//	protected   String          m_label;
	protected   EZFlags         m_flags		= new EZFlags();
	protected   boolean         m_isRelaxable;
	protected   boolean         m_isRepulsive;
	protected   boolean         m_isLocked  = false;
	protected   RelaxableNode   m_node;
	protected   LinkRelaxData[] m_links;

	public NodeRelaxData( RelaxableNode node )
	{
		super( node.getLabel());

		int	n	= node.getLinks().length;

		m_links         = new LinkRelaxData[n];
		m_node          = node;
//		m_pos           = new Vertex( node.getPos());
		m_size          = node.getSize();
		m_maxSize       = node.getMaxSize();
		m_sepSize       = node.getSepSize();
		m_weight        = node.getWeight();
//		m_label         = node.getLabel();
		m_inertia       = 0;//m_weight / 7.f;
//		m_inertia       = node.getInertia();
		m_isRelaxable   = node.isRelaxable();
		m_isRepulsive   = node.isRepulsive();
		m_rep           = new Vertex();
		m_tens          = new Vertex();
		m_rot           = new Vertex();
		m_linkRep		= new Vertex();
		m_crossRep		= new Vertex();
		m_repBak		= new Vertex();
		m_tensBak		= new Vertex();
		m_rotBak		= new Vertex();
		m_linkRepBak	= new Vertex();
		m_crossRepBak	= new Vertex();
	}

	public NodeRelaxData( Vertex pos, float size, float maxSize, float sepSize, float inertia, boolean isRelaxable, boolean isRepulsive, String label )
	{
		super( label );

		m_pos           = pos;
		m_size          = size;
		m_maxSize       = maxSize;
		m_sepSize       = sepSize;
//		m_repMax        = repMax;
		m_inertia       = inertia;
		m_isRelaxable   = isRelaxable;
		m_isRepulsive   = isRepulsive;
		m_weight        = 0;
//		m_label         = label;
		m_links         = new LinkRelaxData[0];
		m_node          = null;
		m_rep           = new Vertex();
		m_tens          = new Vertex();
		m_rot           = new Vertex();
		m_linkRep		= new Vertex();
		m_crossRep		= new Vertex();
		m_repBak		= new Vertex();
		m_tensBak		= new Vertex();
		m_rotBak		= new Vertex();
		m_linkRepBak	= new Vertex();
		m_crossRepBak	= new Vertex();
	}

	protected void init()
	{
		// Fill LinkRelaxData table
		RelaxableLink[] links   = m_node.getLinks();
		int             i, n    = links.length;

		m_links = new LinkRelaxData[n];

		for ( i = 0; i < n; i ++ )
		{
			m_links[i]  = links[i].getRelaxData();
		}

//		// eval repMax
//		LinkRelaxData   linkDat;
//		float           len,
//						minLen  = Float.MAX_VALUE,
//						sLen    = 0;
//
//		for ( i = 0; i < n; i ++ )
//		{
//			linkDat = m_links[i];
//			len     = linkDat.m_len;
//
//			if ( len < minLen ) minLen = len;
//		}
//
//		if ( n > 0 )
//		{
//			m_repMax    = .5f *( minLen / m_size );
//			if ( m_repMax < s_repMin )  m_repMax    = s_repMin;
//		}
//		else
//		{
//			m_repMax    = s_repMin;
//		}
	}

	public boolean isReady()
	{
		return m_pos != null;
	}

	protected boolean isRelaxable()
	{
		return m_pos != null && m_isRelaxable;
	}

	protected boolean isRepulsive()
	{
		return m_isRepulsive;
	}

	protected boolean contains( Vertex pos )
	{
		return Vertex.sqrLength( m_pos, pos )< m_size * m_size;
	}

	protected void updateFlags()
	{
		LinkRelaxData	link;
		int 			i, n    = m_links.length,
						m		= 0;

		for ( i = 0; i < n; i ++ )
		{
			link	= m_links[i];

			if ( link.m_from.m_pos != null && link.m_to.m_pos != null &&  link.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))	m ++;
			if ( m == 2 )	break;
		}

		m_flags.setEnabled( TERMINATOR_BIT, i == n );
	}

/*	protected boolean isTerminator()
	{
//		boolean	isTerm	= true;
//		LinkRelaxData	link;
//		int 			i, n    = m_links.length,
//						m		= 0;
//
//		for ( i = 0; i < n; i ++ )
//		{
//			link	= m_links[i];
//
//			if ( link.m_from.m_pos != null && link.m_to.m_pos != null &&  link.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))	m ++;
//			if ( m == 2 )	isTerm	= false;
//		}
//
//		if ( isTerm != m_flags.isEnabled( TERMINATOR_BIT ))
//		{
//			Exception e = new Exception();
//			e.printStackTrace();
//		}
		return m_flags.isEnabled( TERMINATOR_BIT );
	}*/

	public LinkRelaxData getLinkDataTo( NodeRelaxData nodeDat )
	{
		int 			i, n    = m_links.length;
		LinkRelaxData	link;

		for ( i = 0; i < n; i ++ )
		{
			link	= m_links[i];

			if (( link.m_from == this && link.m_to == nodeDat )||
				( link.m_to == this && link.m_from == nodeDat ))
					return link;
//			if ( m_links[i].getOtherNodeData( this )== nodeDat )    return m_links[i];
		}

		return null;
	}

	public boolean isLinkedTo( Localisable node )
	{
		int i, n    = m_links.length;

		for ( i = 0; i < n; i ++ )
		{
			if ( m_links[i].getOtherNodeData( this )== node )    return true;
		}

		return false;
	}

	public Vertex getPos()
	{
		return m_pos;
	}

	public float getSize()
	{
		return m_size;
	}

	public RelaxableNode getNode()
	{
		return m_node;
	}

	public void setPos( Vertex pos )
	{
		m_pos	= pos;
	}

	protected void initPos( Vertex center )
	{
		NodeRelaxData   nodeDat = null;
		Vertex			fromPos	= null,
						toPos	= null;
//						data;
		int             i, j    = 0,
						n       = m_links.length;
		float           len     = 0,
						sLen    = 0,
						fromLen	= 0,
						toLen	= 0;

		for ( i = 0; i < n; i ++ )
		{
			nodeDat = m_links[i].getOtherNodeData( this );

			if ( nodeDat.isRelaxable())
			{
				toPos	= fromPos;
				toLen	= fromLen;
				fromPos	= nodeDat.m_pos;
				fromLen	= m_links[i].m_length;
				len     = 1.f / fromLen;
				sLen   += len;
				j ++;
			}
		}

		if ( j > 2 )
		{
			m_pos   = new Vertex();
			sLen    = 1.f / sLen;

			for ( i = 0; i < n; i ++ )
			{
				nodeDat = m_links[i].getOtherNodeData( this );

				if ( nodeDat.isRelaxable())
				{
					m_pos.addThis( nodeDat.m_pos.scale( sLen / m_links[i].m_length ));
				}
			}
		}
		else if ( j == 2 )
		{
			Vertex	V	= Vertex.create( fromPos, toPos );
			V.scaleThis( fromLen /( fromLen + toLen ));
			Vertex	pos	= fromPos.add( V );
			V.turn();
			float	k	= fromLen * fromLen - V.sqrLength();

			k	= k <= 0 ? 10.f : (float)Math.sqrt( k );

			if ( Vertex.dotProduct( V, center.sub( pos ))> 0 )
			{
				V.resize( -k );
			}
			else
			{
				V.resize( k );
			}

			pos.addThis( V );
			m_pos	= pos;
		}
		else if ( j == 1 )
		{
			if ( fromPos.equals( center ))
			{
				m_pos	= Vertex.randomize( fromLen );
			}
			else
			{
				m_pos	= new Vertex( fromPos );
				m_pos.subThis( center );
				m_pos.resize( fromLen );
			}
			m_pos.addThis( fromPos );
		}
		else
		{
			m_pos   = new Vertex( center );
		}

		m_pos.jitter( 1.f );
	}

	public void updateNode( Bounds2D mapBnds, Bounds2D winBnds )
	{
		ArrayList<LinkRelaxData> links = new ArrayList<LinkRelaxData>( m_links.length );

		for( LinkRelaxData link : m_links)
		{
			if ( link.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))
			{
				links.add( link );
			}
		}

		m_links	= links.toArray( new LinkRelaxData[links.size()]);
		m_node.setPos( mapBnds.project( m_pos, winBnds ));
		m_node.setSize( mapBnds.project( m_size, winBnds ));
	}

	public void paint( Graphics g, int id, EZFlags flags, Bounds2D mapBnds, Bounds2D winBnds, boolean isFilled, RelaxerNG relaxer )
	{
		Vertex  p       = mapBnds.project( m_pos, winBnds );
		float   size    = mapBnds.project( m_size, winBnds ),
				maxSize = mapBnds.project( m_maxSize, winBnds ),
				sepSize = mapBnds.project( m_sepSize, winBnds ),
				scale	= mapBnds.project( 1.f, winBnds ),
				norm;
		int     s       = (int)size,
				w       = (int)( 2.f * size ),
				x       = (int)p.x,
				y       = (int)p.y;

		if ( flags.isEnabled( GfxTester.NODE_BIT ))
		{
			g.setColor( m_isLocked ? TesterUI.LOCK_COL : TesterUI.NODE_COL );
			if ( isFilled )	g.fillOval( x - s, y - s, w, w );
			else			g.drawOval( x - s, y - s, w, w );
		}

		if ( flags.isEnabled( GfxTester.SHIELD_BIT ))
		{
			s = (int)( maxSize );
			w = (int)( 2.f * maxSize );
			g.setColor( TesterUI.SHIELD_COL );
			g.drawOval( x - s, y - s, w, w );
			s = (int)( sepSize );
			w = (int)( 2.f * sepSize );
			g.setColor( TesterUI.SEPSHIELD_COL );
			g.drawOval( x - s, y - s, w, w );
		}

		if ( m_repBak != null )
		{
			if ( flags.isEnabled( GfxTester.NREP_BIT ))
			{
				norm	= scale * relaxer.m_nodeRep;
				g.setColor( TesterUI.NREP_COL );
				g.drawLine( x, y, x +(int)( norm * m_repBak.x ), y +(int)( norm * m_repBak.y ));
			}

			if ( flags.isEnabled( GfxTester.TENS_BIT ))
			{
				norm	= scale * relaxer.m_linkTens;
				g.setColor( TesterUI.TENS_COL );
				g.drawLine( x, y, x +(int)( norm * m_tensBak.x ), y +(int)( norm * m_tensBak.y ));
			}

			if ( flags.isEnabled( GfxTester.ROT_BIT ))
			{
				norm	= scale * relaxer.m_linkRot;
				g.setColor( TesterUI.ROT_COL );
				g.drawLine( x, y, x +(int)( norm * m_rotBak.x ), y +(int)( norm * m_rotBak.y ));
			}

			if ( flags.isEnabled( GfxTester.CROSS_BIT ))
			{
				norm	= scale * relaxer.m_unCross;
				g.setColor( TesterUI.CROSS_COL );
				g.drawLine( x, y, x +(int)( norm * m_crossRepBak.x ), y +(int)( norm * m_crossRepBak.y ));
			}

			if ( flags.isEnabled( GfxTester.LREP_BIT ))
			{
				norm	= scale * relaxer.m_linkRep;
				g.setColor( TesterUI.LREP_COL );
				g.drawLine( x, y, x +(int)( norm * m_linkRepBak.x ), y +(int)( norm * m_linkRepBak.y ));
			}
		}
		boolean hasNodeId   = flags.isEnabled( GfxTester.NODEID_BIT ),
				hasExt      = flags.isEnabled( GfxTester.NODEEXT_BIT );

		if ( hasNodeId || hasExt )
		{
			Color   color   = null;
			String  value   = "";

			if ( hasNodeId )
			{
				color   = TesterUI.NODEID_COL;
				value   = String.valueOf( id );
			}
			if ( hasExt )
			{
				m_label = m_pos.toString();
//				m_label = m_pos.toString() + ";
				color   = TesterUI.NODEEXT_COL;
				value  += hasNodeId ?
							' ' + m_label:
							m_label + " t=" + m_flags.isEnabled( TERMINATOR_BIT );//isTerminator();
			}

			g.setColor( color );
			g.drawString( value, x+10, y+20 );
		}
	}

	public String toString()
	{
	//	return "p=" + m_pos + ", r=" + m_repBak + ", t=" + m_tensBak + ", l=" + m_rotBak + ", c=" + m_crossBak + ", cr=" + m_cRepBak;
		return "p=" + m_pos + ", r=" + m_rep + ", t=" + m_tens + ", l=" + m_rot + ", c=" + m_linkRep + ", cr=" + m_crossRep;
	}

//	protected PopupMenu getMenu()
//	{
//		PopupMenu   menu    = new PopupMenu();
//		Field[]     fields  = getClass().getDeclaredFields();
//		Field       field;
//		int         i, n    = fields.length;
//
//		try
//		{
//			for ( i = 0; i < n; i ++ )
//			{
//				field   = fields[i];
//				menu.add( field.getName()+ " = " + field.get( this ));
//			}
//		}
//		catch ( Exception e )
//		{
//			System.out.println( "pb" );
//		}
//
//		return menu;
//	}
}

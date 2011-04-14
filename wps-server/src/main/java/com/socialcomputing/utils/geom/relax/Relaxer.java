package com.socialcomputing.utils.geom.relax;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.Bounds2D;
import com.socialcomputing.utils.math.EZMath;

public class Relaxer
{
	public static final	int		NODE_BND		= 0;
	public static final	int		LINK_BND		= 1;

	public static final	int		FIRST_VTX		= 0;
	public static final	int		CENTER_VTX		= 1;
	public static final	int		VEC1_VTX		= 2;
	public static final	int		VEC2_VTX		= 3;
	public static final	int		MIN1_VTX		= 4;
	public static final	int		MAX1_VTX		= 5;
	public static final	int		LAST_VTX		= 7;

	protected	final float INT_MAX_INV = 1.f / Integer.MAX_VALUE;

	protected   float           m_scale     	= 0.5f;
	protected   float           m_nodeRep   	= 0.2f;
	protected 	float     		m_linkTens 		= 0.2f;
	protected   float           m_nodeRepVar	= 1.f;
	protected   float           m_linkTensVar	= 1.f;
	protected   Bounds2D        m_bounds    = new Bounds2D();
	protected   NodeRelaxData   m_base      = null;
	protected   NodeRelaxData[] m_nodes;
	protected   LinkRelaxData[] m_links;
	protected	Vertex[]		m_vertexBuf		= new Vertex[8];
	private   float           	m_lenSum;
	private   int             	m_lenCnt;

	public Relaxer()
	{
		init();
	}

	public Relaxer( RelaxParams params, NodeRelaxData base, RelaxableNode[] nodes, RelaxableLink[] links )
	{
		init();
		setParams( params, base, nodes, links );
	}

	public void setParams( RelaxParams params, NodeRelaxData base )
	{
		if ( params != null )
		{
			m_scale     	= params.getScale();
			m_nodeRep    	= params.getNodeRep();
			m_linkTens   	= params.getLinkTen();
			m_nodeRepVar	= params.getRepMix();
			m_linkTensVar	= params.getTensMix();
			m_base      	= base;
		}
	}

	private void init()
	{
		int	i, n	= m_vertexBuf.length;

		for ( i = 0; i < n; i ++ )
		{
			m_vertexBuf[i]	= new Vertex();
		}
	}

	public void setParams( RelaxParams params, NodeRelaxData base, RelaxableNode[] nodes, RelaxableLink[] links )
	{
		setParams( params, base );
		initData( nodes, links );
	}

	public NodeRelaxData[] getDataNodes()
	{
		return m_nodes;
	}

	public LinkRelaxData[] getDataLinks()
	{
		return m_links;
	}

	public void setDataNodes( NodeRelaxData[] nodes )
	{
		m_nodes = nodes;
	}

	public void setDataLinks( LinkRelaxData[] links )
	{
		m_links = links;
	}

	public void relaxeLink( LinkRelaxData linkDat )
	{
		NodeRelaxData   fromDat     = linkDat.m_from;
		NodeRelaxData   toDat       = linkDat.m_to;
		float           fromInertia = fromDat.m_inertia;
		float           toInertia   = toDat.m_inertia;
		float		    toFac       = fromInertia != 1.f || toInertia != 1.f ?
										( 1.f - toInertia )/( 2.f - fromInertia - toInertia ):
										.5f;
		float           len, scl, tens   = .75f,// A virer (mettre � 1) et � retuner en fonction...
						fromDirX, fromDirY,
						toDirX, toDirY,
						dirX        = toDat.m_pos.x - fromDat.m_pos.x,
						dirY        = toDat.m_pos.y - fromDat.m_pos.y;

		if ( dirX == 0 && dirY == 0 )  // repulse nodes in a random direction
		{
			int         xInt    = EZMath.s_rand.nextInt();
			boolean     b1      = ( xInt & 0x1 )== 0,
						b2      = ( xInt & 0x2 )== 0;

			len = linkDat.m_length;
			dirY = INT_MAX_INV * xInt;

			if ( b1 )
			{
				dirY *= len;
				dirX = b2 ? len : -len;
			}
			else
			{
				dirX = dirY * len;
				dirY = b2 ? len : -len;
			}
		}
		else
		{
			len = dirX * dirX + dirY * dirY;

			float   dl	= ( linkDat.m_length * linkDat.m_length )- len;

			dl		*= linkDat.m_stiffness + m_linkTensVar *( 1.f - linkDat.m_stiffness );
			scl		= dl / ( linkDat.m_length + len );
			dirX	*= scl; dirY *= scl;

			m_lenSum    += linkDat.getLengthVar( len );
			m_lenCnt    ++;
		}

		float   sTens   = tens * toFac;

		scl = ( 1.f - fromInertia )*( sTens - tens );
		fromDirX = dirX * scl; fromDirY = dirY * scl;   // change direction at once
		fromDat.m_tens.x += fromDirX; fromDat.m_tens.y += fromDirY;

		scl = ( 1.f - toInertia )* sTens;
		toDirX = dirX * scl; toDirY = dirY * scl;
		toDat.m_tens.x += toDirX; toDat.m_tens.y += toDirY;
	}

	public void relaxeNode( NodeRelaxData nodeDat, int beg, final int end )
	{
		int             i;
		NodeRelaxData   to;
		LinkRelaxData   linkDat;
		Vertex          fromPos     = nodeDat.m_pos;
		Vertex          toPos;
		float           fromSize    = nodeDat.m_size,
						fromMaxSize = nodeDat.m_maxSize,
						fromSepSize = nodeDat.m_sepSize,
						fromInertia = nodeDat.m_inertia,
						toInertia, toFac,
						len, scl, siz, distMin, distMax, distMax2,
						dirX, dirY,
						fromDirX, fromDirY,
						toDirX, toDirY,
						q;
		Vertex          repVar;

		for ( i = beg; i <= end; i ++ )
		{
			to  = m_nodes[i];

			if ( to == nodeDat )   continue;

			toInertia   = to.m_inertia;

			if ( to.isRepulsive() &&( fromInertia != 1.f || toInertia != 1.f ))
			{
				distMin     = fromSize + to.m_size;

				toPos   = to.m_pos;
				dirX = toPos.x - fromPos.x; dirY = toPos.y - fromPos.y;

				if ( dirX == 0 && dirY == 0 )
				{
					int         xInt    = EZMath.s_rand.nextInt();
					boolean     b1      = ( xInt & 0x1 )== 0,
								b2      = ( xInt & 0x2 )== 0;

					dirY = INT_MAX_INV * xInt;

					if ( b1 )
					{
						dirY *= distMin;
						dirX = b2 ? distMin : -distMin;
					}
					else
					{
						dirX = dirY * distMin;
						dirY = b2 ? distMin : -distMin;
					}
				}
				else
				{
					linkDat     = nodeDat.getLinkDataTo( to );
					q			= 2.f * m_nodeRepVar;

					if ( linkDat != null && linkDat.m_flags.isDisabled( LinkRelaxData.FILTER_BIT )) // linked
					{
						if ( m_nodeRepVar < .5f )
						{
							distMin	= fromSize + to.m_size;
							distMax	= fromMaxSize + to.m_maxSize;
						}
						else
						{
							q		-= 1.f;
							distMin	= fromMaxSize + to.m_maxSize;
							distMax	= fromSepSize + to.m_sepSize;
						}
					}
					else	// separated
					{
						if ( m_nodeRepVar < .5f )
						{
							distMin	= fromMaxSize + to.m_maxSize;
							distMax	= fromSepSize + to.m_sepSize;
						}
						else
						{
							q		-= 1.f;
							distMin	= fromSepSize + to.m_sepSize;
							distMax	= distMin;
						}
					}

					distMax		= EZMath.interLin( q , distMin, distMax );
					distMax2    = distMax * distMax;
					len         = dirX * dirX + dirY * dirY;

					if ( len >= distMax2 )   continue;   // Too far away

					siz         = len;
					len         = distMax *( 1.f - len / distMax2 );
					siz         = len /(float)Math.sqrt( siz );
					dirX        *= siz; dirY *= siz;
				}

				toFac   = fromInertia != 1.f || toInertia != 1.f ?
						( 1.f - toInertia )/( 2.f - fromInertia - toInertia ):
						.5f;

				if ( fromInertia != 1.f )
				{
					scl = ( 1.f - fromInertia )*( toFac - 1.f );
					fromDirX = dirX * scl; fromDirY = dirY * scl;   // change direction at once
					repVar = nodeDat.m_rep;
					repVar.x += fromDirX; repVar.y += fromDirY;
				}

				if ( toInertia != 1.f )
				{
					scl = ( 1.f - toInertia )* toFac;
					toDirX = dirX * scl; toDirY = dirY * scl;
					repVar = to.m_rep;
					repVar.x += toDirX; repVar.y += toDirY;
				}
			}
		}
	}

	public void balance( NodeRelaxData nodeDat )
	{
		Vertex  rep     = nodeDat.m_rep,
				tens    = nodeDat.m_tens;
		float	dx, dy;

		if ( nodeDat.m_inertia < 1.f )
		{
			dx = m_scale *( m_linkTens * tens.x + m_nodeRep * rep.x );
			dy = m_scale *( m_linkTens * tens.y + m_nodeRep * rep.y );
			nodeDat.m_pos.x += dx;
			nodeDat.m_pos.y += dy;
		}

		tens.reset();//tens.x = tens.y = 0.f;
		rep.reset();//rep.x = rep.y = 0.f;
	}

	public float relaxe( final int beg, final int end, final int iter )
	{
		final int   lCnt    = m_links.length;

		NodeRelaxData   nodeDat;
		LinkRelaxData   linkDat;
		int             i, j;

		for ( j = 0; j < iter; j ++ )
		{
			m_lenSum    = 0.f;
			m_lenCnt    = 0;

			// Relaxe links
			for ( i = 0; i < lCnt; i ++ )
			{
				linkDat = m_links[i];

				if ( linkDat.isRelaxable())
				{
					relaxeLink( linkDat );
				}
			}

			// Relaxe nodes
			if ( m_base != null && m_base.isRepulsive())
			{
				relaxeNode( m_base, 0, end );
			}

			for ( i = beg; i <= end; i ++ )
			{
				nodeDat = m_nodes[i];

				if ( nodeDat.isRepulsive())
				{
					relaxeNode( nodeDat, j, end );
				}
			}

			// Relaxe extensions
			relaxeEx( beg, end );

			// balance relaxations
			for ( i = beg; i <= end; i ++ )
			{
				nodeDat = m_nodes[i];

				if ( nodeDat.isRelaxable())
				{
					balance( nodeDat );
				}
			}
		}

		return m_lenCnt > 0 ? m_lenSum / m_lenCnt : 0;
	}

	public void relaxeEx( final int beg, final int end )
	{
	}

	// Post Relaxe (intersection...)
//	public void postRelaxe( final int beg, final int end, EZFlags flags )
//	{
//	}

	public void setNodesLock( int beg, int end, boolean isLocked, boolean isRepulsive )
	{
		for ( int i = beg; i < end; i ++ )
		{
			m_nodes[i].m_isLocked       = isLocked;
			m_nodes[i].m_isRepulsive    = isRepulsive;
		}
	}

	public void setLinksFlags( int beg, int end, int flags )
	{
		for ( int i = beg; i < end; i ++ )
		{
			m_links[i].m_flags.enable( flags );
		}
	}

	private void initData( RelaxableNode[] nodes, RelaxableLink[] links )
	{
		// init Node & link Data & nodeRep..., precalc de order....
		int             i, n    = nodes.length;
		RelaxableNode   node;
		RelaxableLink   link;
		NodeRelaxData   nodeDat;
		LinkRelaxData   linkDat;

		m_nodes = new NodeRelaxData[n];

		for ( i = 0; i < n; i ++ )
		{
			node        = nodes[i];
			nodeDat     = new NodeRelaxData( node );
			m_nodes[i]  = nodeDat;
			node.setRelaxData( nodeDat );
		}

		n   = links.length;

		m_links = new LinkRelaxData[n];

		for ( i = 0; i < n; i ++ )
		{
			link        = links[i];
			linkDat     = new LinkRelaxData( link );
			m_links[i]  = linkDat;
			link.setRelaxData( linkDat );
		}

		n   = nodes.length;

		for ( i = 0; i < n; i ++ )
		{
			m_nodes[i].init();
		}
	}

	public void updateData( Bounds2D winBnds )
	{
		int		i, n	= m_nodes.length;
//		already done in alignData;
//		evalBBox( 0, m_nodes.length, winBnds, false );

//		m_bounds.updateAspect( winBnds );

		for ( i = 0; i < n; i ++ )
		{
			m_nodes[i].updateNode( m_bounds, winBnds );
		}

		n	= m_links.length;

		LinkRelaxData	link;
		ArrayList		links	= new ArrayList();

		for ( i = 0; i < n; i ++ )
		{
			link	= m_links[i];

			if ( link.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))
			{
				link.updateLink( m_bounds, winBnds );
				links.add( link );
			}
		}

		m_links	= (LinkRelaxData[])links.toArray( new LinkRelaxData[links.size()]);
	}

	public void alignData( Bounds2D winBnds, NodeRelaxData base )
	{
		int		i, n	= m_nodes.length;

		m_base	= base;

		evalBBox( 0, m_nodes.length, false );

		if ( m_bounds.isWidthMin() != winBnds.isWidthMin())	// We should rotate the map by 90�
		{
			for ( i = 0; i < n; i ++ )
			{
				m_nodes[i].m_pos.turn();
			}

			m_base.m_pos.turn();
			m_bounds.turn();
		}

		Vertex	V		= m_bounds.getCenter().subThis( m_base.m_pos );
		boolean	xSwap	= V.x < 0,
				ySwap	= V.y < 0;

		for ( i = 0; i < n; i ++ )
		{
			m_nodes[i].m_pos.swapThis( xSwap, ySwap );
		}

		m_base.m_pos.swapThis( xSwap, ySwap );
		m_bounds.swapThis( xSwap, ySwap );

/*		LinkRelaxData	linkDat;
		float			alpha	= EZMath.PI / 8;

		n	= m_links.length;

		for ( i = 0; i < n; i ++ )
		{
			linkDat	= m_links[i];

			if ( !linkDat.hasSpace( alpha ))
			{
				tessLinks[m++]	= linkDat;
				link.setRelaxData( linkDat );
				widthBnd.check( attLink.m_size );
				lenBnd.check( attLink.m_length );
			}

			linkDat.m_flags.enable( LinkRelaxData.VISITED_BIT );
		}*/
	}

	public void dump()
	{
		int	i, n	= m_nodes.length;

		for ( i = 0; i < n; i ++ )
		{
			if ( m_nodes[i].isReady())
			{
				System.out.println( "n[" + i + "]=" + m_nodes[i]);
			}
		}

		n	= m_links.length;

		for ( i = 0; i < n; i ++ )
		{
			if ( m_links[i].isReady())
			{
				System.out.println( "l[" + i + "]=" + m_links[i]);
			}
		}
	}

	public void evalBBox( int beg, int end, boolean withBase )
	{
		NodeRelaxData   nodeDat;
		int             i;

		m_bounds.reset();

		// bbox
		for ( i = beg; i < end; i ++ )
		{
			nodeDat = m_nodes[i];

			if ( nodeDat.isReady())
			{
				m_bounds.sizedCheck( nodeDat.m_pos, nodeDat.m_size );
			}
		}

		if ( withBase && m_base != null )
		{
			m_bounds.sizedCheck( m_base.m_pos, m_base.m_size );
		}

//		m_bounds.updateAspect( winBnds );
	}

	public void updateBoundingBase( int beg, int end )
	{
		int				i;
		NodeRelaxData	nodeDat	= m_nodes[beg];
		Vertex			center	= m_vertexBuf[CENTER_VTX].setLocation( nodeDat.m_pos ),
						pos,
						CP		= m_vertexBuf[VEC1_VTX];
		float			rad		= nodeDat.m_size,
						rad2	= rad * rad,
						len, size;

		for ( i = beg + 1; i < end; i ++ )
		{
			nodeDat	= m_nodes[i];
			pos		= nodeDat.m_pos;
			size	= nodeDat.m_size;

			if ( Vertex.sqrLength( pos, center )> rad2 )
			{
				CP.set( center, pos );
				len		= CP.length();
				rad2	= .5f *( len + rad + size );
				CP.scaleThis(( rad2 - rad )/ len );
				center.addThis( CP );
				rad		= rad2;
				rad2	*= rad2;
			}
		}

		m_base.m_pos		= center;
		m_base.m_size		= rad;
		m_base.m_sepSize	= rad * 1.05f;
		m_base.m_maxSize	= rad * 1.05f;
	}

	public Bounds2D getBounds()
	{
		return m_bounds;
	}

//	public Vertex project( Vertex v, Bounds2D winBnds )
//	{
//		return m_bounds.project( v, winBnds );
//	}

	public synchronized Vertex unproject( Vertex v, Bounds2D winBnds )
	{
		return winBnds.project( v, m_bounds );
	}

//	protected boolean isInfinite( float x, String msg )
//	{
//		if ( Float.isInfinite( x ))
//		{
//			System.out.println( msg + " isInfinite" );
//			return true;
//		}
//
//		return false;
//	}
//
//	protected boolean isNaN( float x, String msg )
//	{
//		if ( Float.isNaN( x ))
//		{
//			System.out.println( msg + " isNaN" );
//			return true;
//		}
//
//		return false;
//	}
//
//	protected void checkNaN( String msg )
//	{
//		int				i, n	= m_nodes.length;
//		NodeRelaxData	nodeDat;
//
//		for ( i = 0; i < n; i ++ )
//		{
//			nodeDat = m_nodes[i];
//
//			if ( nodeDat.isReady())
//			{
//				if ( Float.isNaN( nodeDat.m_pos.x ) || Float.isNaN( nodeDat.m_pos.y ))
//				{
//					System.out.println( msg + " : node[" + i + "] isNaN -> " + nodeDat );
//				}
//				if ( Float.isInfinite( nodeDat.m_pos.x ) || Float.isInfinite( nodeDat.m_pos.y ))
//				{
//					System.out.println( msg + " : node[" + i + "] isInfinite -> " + nodeDat );
//				}
//			}
//		}
//	}

	protected HashMap getEditableFields()
	{
		HashMap	fieldMap	= new HashMap();

		fieldMap.put( "global forces scale", "com.socialcomputing.utils.geom.relax.Relaxer.m_scale" );
		fieldMap.put( "node repulsion", "com.socialcomputing.utils.geom.relax.Relaxer.m_nodeRep" );
		fieldMap.put( "link tension", "com.socialcomputing.utils.geom.relax.Relaxer.m_linkTens" );
		fieldMap.put( "node rep Sep/Ext ratio", "com.socialcomputing.utils.geom.relax.Relaxer.m_nodeRepVar" );
		fieldMap.put( "link tens Soft/Hard ratio", "com.socialcomputing.utils.geom.relax.Relaxer.m_linkTensVar" );

		return fieldMap;
	}

	protected synchronized void paint( Graphics g, boolean isCentered, Bounds2D winBnds, EZFlags flags )
	{
		NodeRelaxData   nodeDat;
		LinkRelaxData   linkDat;
		int             i, n;

//		m_nodes[0].m_pos.x	= 0.f / 0.f;
		if ( isCentered )
		{
			evalBBox( 0, m_nodes.length, true );
			m_bounds.updateAspect( winBnds );
		}

		n   = m_links.length;

		if ( m_base != null )
		{
			m_base.paint( g, -1, flags, m_bounds, winBnds, false, (RelaxerNG)this );
		}

		for ( i = 0; i < n; i ++ )
		{
			linkDat = m_links[i];

			if ( linkDat.isReady())
			{
				linkDat.paint( g, i, flags, m_bounds, winBnds, true );
			}
		}

		n   = m_nodes.length;

		for ( i = 0; i < n; i ++ )
		{
			nodeDat = m_nodes[i];

			if ( nodeDat.isReady())
			{
				nodeDat.paint( g, i, flags, m_bounds, winBnds, true, (RelaxerNG)this );
			}
		}
	}
}
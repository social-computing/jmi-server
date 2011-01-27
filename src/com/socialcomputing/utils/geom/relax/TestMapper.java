package com.socialcomputing.utils.geom.relax;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;

import com.socialcomputing.utils.geom.triangle.Delaunay;
import com.socialcomputing.utils.geom.triangle.QuadEdge;
import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.Bounds2D;
import com.socialcomputing.utils.math.EZMath;

public class TestMapper implements RelaxListener
{
	private static final float  s_trsh      = .0005f;
	protected float             m_err;
	private   int               m_nodeCnt   = 8;//11;//8;//20;
	private   int               m_linkCnt   = 13;//16;//13;//30;
	private   int               m_iterCnt   = 50;

	private     RelaxableNode[] m_nodes;
	private     RelaxableLink[] m_links;
	private     NodeRelaxData   m_base;
	protected 	LinkRelaxData[] m_tessLinks;
	private     RelaxerNG       m_relaxer;
	private     Delaunay        m_delaunay;
	private		CrossMgr		m_crossMgr;

	protected	int				m_tessCnt;
	private     Vertex          m_center;
	private     RelaxParams     m_params;

	private     int             m_stage;
	private     int             m_step;
	private     int             m_iter;
	private     int             m_stepCnt;

	public TestMapper( GfxTester tester )
	{
		m_base      = new NodeRelaxData( new Vertex( 400, 300 ), 10, 10, 10, 1, true, true, "base" );
//		m_params    = new RelaxParams( .5f, .2f, .2f, .2f, .2f, .2f, false, m_base );
		m_crossMgr	= new CrossMgr();
		m_relaxer   = new RelaxerNG( m_crossMgr );
	}

	public void iterate( EZFlags flags )
	{
		boolean	needFilter	= flags.isEnabled( CrossMgr.FILTER_BIT ),
				needSwap	= flags.isEnabled( CrossMgr.SWAP_BIT );

		switch ( m_stage )
		{
			case 0 :
				m_err   = m_relaxer.relaxe( 0, m_step, 1 );
				m_relaxer.postProcess( 0, m_step, needFilter, needSwap );
				break;
			case 1 :
				m_err   = m_relaxer.relaxe( 0, m_nodeCnt - 1, 1 );
				m_relaxer.postProcess( 0, m_nodeCnt - 1, needFilter, needSwap );
				break;

			case 2 :
				tesselate();
				break;

			case 3 :
				m_err   = m_relaxer.relaxe( 0, m_nodeCnt - 1, 1 );
				m_relaxer.postProcess( 0, m_nodeCnt - 1, needFilter, needSwap );
		}
	}

//	private void tesselate()
//	{
//		((RelaxerNG)m_relaxer).tesselate( m_delaunay, m_links.length );
//	}

	public HashMap getEditableFields()
	{
		HashMap	fieldMap	= new HashMap();

		fieldMap.put( "Nodes count", "com.socialcomputing.utils.geom.relax.TestMapper.m_nodeCnt" );
		fieldMap.put( "Links count", "com.socialcomputing.utils.geom.relax.TestMapper.m_linkCnt" );
		fieldMap.put( "relax treshold", "com.socialcomputing.utils.geom.relax.TestMapper.s_trsh" );
		fieldMap.put( "max iter count", "com.socialcomputing.utils.geom.relax.TestMapper.m_iterCnt" );

		return fieldMap;
	}

	public void tesselate()
	{
		ArrayList       edges   	= m_delaunay.process().edges();
		CrossMgr		crossMgr	= ((RelaxerNG)m_relaxer).m_crossMgr;
		QuadEdge        edge;
		NodeRelaxData   fromDat, toDat;
		LinkRelaxData   linkDat;
		LinkRelaxData[] tessLinks	= new LinkRelaxData[3*m_nodes.length],
						links		= m_relaxer.m_links;
		int             i, n    	= edges.size(),
						m			= 0,
						linkCnt		= m_links.length;

		m_crossMgr.evalInters( m_relaxer.getDataNodes(), m_relaxer.getDataLinks(), true );
		m_crossMgr.filter( true );

		for ( i = m = 0; i < n; i ++ )
		{
			edge    = (QuadEdge)edges.get( i );
			fromDat = (NodeRelaxData)edge.getFrom();
			toDat   = (NodeRelaxData)edge.getTo();
			linkDat = fromDat.getLinkDataTo( toDat );

			if (( linkDat == null || linkDat.m_flags.isEnabled( LinkRelaxData.FILTER_BIT ))  // this link doesn't already exists or has been removed
				&& !crossMgr.isInter( fromDat, toDat, links ))
			{
				tessLinks[m++]    = new LinkRelaxData( fromDat, toDat );
			}
		}

		links	= new LinkRelaxData[linkCnt+m];

		System.arraycopy( m_relaxer.m_links, 0, links, 0, linkCnt );
		System.arraycopy( tessLinks, 0, links, linkCnt, m );
		m_relaxer.m_links	= links;
	}

	public void keyPressed( KeyEvent e, Frame frame, Graphics g )
	{
		switch ( e.getKeyCode())
		{
			case KeyEvent.VK_D :
				m_relaxer.dump();
				break;
		}
	}
//	public void dump()
//	{
//		m_relaxer.dump();
//	}

	public boolean initStage()
	{
		m_stage ++;

		switch ( m_stage )
		{
			case 0 :
				m_step      = -1;
				m_stepCnt   = m_nodeCnt;
				m_params    = new RelaxParams( .25f, 0.f, 1.f, 0.f, 1.f, 1.f, 1.f, 0.f, 0.f );
//				m_params    = new RelaxParams( .25f, .2f, 1.f, .2f, 1.f, .2f, .1f, .1f );
				m_relaxer.setParams( m_params, null );
				return true;

			case 1 :
				m_step      = -1;
				m_stepCnt   = 1;
				m_params    = new RelaxParams( .5f, .5f, 1.f, 0.f, 0.f, .5f, 1.f, 0.f, 0.f );
				m_relaxer.setParams( m_params, null );
//				m_relaxer.setLinksFlags( 0, m_linkCnt, LinkRelaxData.LOCKED_BIT );
				return true;

			case 2 :
				m_step      = -1;
				m_stepCnt   = 1;
				m_delaunay  = new Delaunay( m_relaxer.m_nodes );
				return true;

	//		default: return false;
		}

		m_stage --;

		return false;
	}

	public boolean initStep()
	{
		m_step ++;

		if ( m_step < m_stepCnt )
		{
			switch ( m_stage )
			{
				case 0 :
					m_iter      = -1;
					m_iterCnt   = 50;
					m_nodes[m_step].initPos( m_step < 2 ? m_center : m_relaxer.m_bounds.getCenter());
					return true;

				case 1 :
					m_iter      = -1;
					m_iterCnt   = 50;
					return true;

				case 2 :
					m_iter      = -1;
					m_iterCnt   = 1;
					return true;

//				default: return false;
			}
		}
		m_step --;

		return false;
	}

	public boolean initIter()
	{
		m_iter ++;

		return m_iter < m_iterCnt &&( m_iter < 5 || m_err > s_trsh );
	}

	public String getStage()
	{
		switch ( m_stage )
		{
			case 0 : return "Relaxe";

			case 1 : return "Filter";

			case 2 : return "Tess";

			case 3 : return "Finished";

			default: return "";
		}
	}

	public String getStep()
	{
		return String.valueOf( m_step );
	}

	public String getIter()
	{
		return String.valueOf( m_iter );
	}

	public RelaxableNode[] getNodes()
	{
		return m_nodes;
	}

	public NodeRelaxData getBase()
	{
		return m_base;
	}

	public RelaxableLink[] getLinks()
	{
		return m_links;
	}

	public Relaxer getRelaxer()
	{
		return m_relaxer;
	}

	public void updateRelaxParams( Field field )
	{
		try
		{
			System.out.println( "set " + field.getName() + " to " + field.get( m_relaxer ));
		}
		catch ( Exception e ){}
	}

	private void randomizeLinks()
	{
		int         i;
		Node        from, to;
		int         fromId, toId;
		Integer     key;
		Hashtable   linkTable   = new Hashtable( m_linkCnt );
		Link        link;

		for ( i = 0; i < m_linkCnt; )
		{
			fromId  = (int)( EZMath.random()* m_nodeCnt );
			toId    = (int)( EZMath.random()* m_nodeCnt );
			key     = fromId > toId ? new Integer(( fromId << 16 )| toId ): new Integer(( toId << 16 )| fromId );

			if (( fromId != toId )&&( !linkTable.containsKey( key )))
			{
				from        = (Node)m_nodes[fromId];
				to          = (Node)m_nodes[toId];

				if ( from.m_tmpLinks.size() < 5 && to.m_tmpLinks.size() < 5 )
				{
					link        = createRandomLink( from, to );
					linkTable.put( key, link );
					m_links[i++]  = link;
				}
			}
		}
	}

	private void randomizeNodes()
	{
		int         i;
		float       inertia, size;

		// Nodes randomization
		for ( i = 0; i < m_nodeCnt; i ++ )
		{
			inertia     = 0.f;
			size        = EZMath.interLin( EZMath.random(), 12.f, 20.f );
			m_nodes[i]  = new Node( size, inertia, true, true );
		}
	}

	private static Link createRandomLink( RelaxableNode from, RelaxableNode to )
	{
		float	len			= EZMath.random( 50, 100 ),
				stiffness	= EZMath.random( .01f, .1f ),
				width		= EZMath.interLin( stiffness, 3, 9 );

		return new Link((Node)from, (Node)to, len, stiffness, width, true );
	}

	public void initData( Bounds2D winBnds )
	{
		int     i, n    = ( m_nodeCnt *( m_nodeCnt - 1 ))/ 2; // !nodeCnt = Max linkCnt
		int[]   links   = null;

		EZMath.setSeed( 0 );

		if ( m_nodeCnt < 3 )
		{
			m_nodeCnt = 2;
			m_linkCnt = 1;
		}
		else if ( m_linkCnt > n )
		{
			m_linkCnt = m_nodeCnt;
		}

		m_nodes = new Node[m_nodeCnt];
		m_links = new Link[m_linkCnt];
		m_tessLinks = new LinkRelaxData[n];
		m_tessCnt   = 0;

		// Nodes randomization
		randomizeNodes();

		// Basic Link configurations
		switch ( m_nodeCnt )
		{
			case 2 :
				if ( m_linkCnt == 1 )  links   = new int[]{ 0, 1 };
				break;
			case 3 :
				if ( m_linkCnt == 2 )
					links   = new int[]{ 0, 1,  1, 2 };
				else if ( m_linkCnt == 3 )
					links   = new int[]{ 0, 1,  1, 2,  2, 0 };
				break;
			case 4 :
				if ( m_linkCnt == 3 )
					links   = new int[]{ 0, 1,  0, 2,  0, 3 };
				else if ( m_linkCnt == 4 )
					links   = new int[]{ 0, 1,  1, 2,  2, 3,  3, 0 };
				else if ( m_linkCnt == 5 )
					links   = new int[]{ 0, 1,  1, 2,  2, 3,  3, 0,  0, 2 };
				break;
			case 5 :
				if ( m_linkCnt == 4 )
					links   = new int[]{ 0, 1,  0, 2,  0, 3,  0, 4 };
				else if ( m_linkCnt == 5 )
					links   = new int[]{ 0, 1,  0, 4,  1, 2,  4, 3,  2, 3 };
//					links   = new int[]{ 0, 1,  1, 2,  2, 3,  3, 4,  4, 0 };
				break;
			case 6 :
				if ( m_linkCnt == 5 )
					links   = new int[]{ 0, 1,  0, 2,  0, 3,  0, 4,  0, 5 };
				break;
			case 41 :
				if ( m_linkCnt == 40 )
					links   = new int[]{ 0, 1,  1, 2,  2, 3,  2, 4,  1, 5,  5, 6,  5, 7,  1, 8,  8, 9,  8, 10,
										 0, 11,  11, 12,  12, 13,  12, 14,  11, 15,  15, 16,  15, 17,  11, 18,  18, 19,  18, 20,
										 0, 21,  21, 22,  22, 23,  22, 24,  21, 25,  25, 26,  25, 27,  21, 28,  28, 29,  28, 30,
										 0, 31,  31, 32,  32, 33,  32, 34,  31, 35,  35, 36,  35, 37,  31, 38,  38, 39,  38, 40 };
				break;
		}
		if ( links != null )
		{
			int i2;

			n    = links.length >> 1;

			for ( i = 0; i < n; i ++ )
			{
				i2          = i << 1;
				m_links[i]  = createRandomLink( m_nodes[links[i2]], m_nodes[links[i2+1]] );
			}

		}
		else
		{
			// Links randomization
			randomizeLinks();
		}

		n   = m_nodes.length;

		// Nodes initialisation
		for ( i = 0; i < n; i ++ )
		{
			((Node)m_nodes[i]).initLinks();
		}

		// Nodes initialisation
		for ( i = 0; i < n; i ++ )
		{
			((Node)m_nodes[i]).init();//Links();
		}

		// Nodes ordering
		Comparator  comp    = new Comparator()
		{
			public int compare( Object o1, Object o2 )
			{
				Node    n1  = (Node)o1,
						n2  = (Node)o2;
				float   o   = n2.getWeight()- n1.getWeight();

				return o > 0 ? 1 :( o < 0 ? -1 : 0 );
			}
		};

		Arrays.sort( m_nodes, comp );

		m_relaxer.setParams( m_params, null, m_nodes, m_links );

		m_delaunay  = null;
		m_center    = winBnds.getCenter();
		m_stage     = -1;
		m_step      = -1;
		m_iter      = -1;
	}
}

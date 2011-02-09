package com.socialcomputing.utils.geom.triangle;

import java.util.ArrayList;
import java.util.HashMap;

import com.socialcomputing.utils.geom.Localisable;
import com.socialcomputing.utils.geom.Vertex;

public final class QuadEdge
{
	protected   int         m_r;
	protected   NodeTriData m_data;
	protected   QuadEdge    m_next;
//		public final QuadEdge e[] = new QuadEdge[4];
	protected   QuadEdge[]  m_group;
	protected   Localisable m_rightClu  = null;
	protected   Localisable m_leftClu   = null;

	private QuadEdge( int i, NodeTriData data, QuadEdge[] quad )
	{
		m_r     = i;
		m_data  = data;
		m_group = quad;
	}

	public QuadEdge( NodeTriData data, NodeTriData data1 )
	{
		m_r       = 0;
		m_data    = data;
		m_group   = new QuadEdge[4];
		m_next    = this;

		QuadEdge    quadedge1 = new QuadEdge( 1, null, m_group ),
					quadedge2 = new QuadEdge( 2, data1, m_group ),
					quadedge3 = new QuadEdge( 3, null, m_group);

		m_group[0] = this;
		m_group[1] = quadedge1;
		m_group[2] = quadedge2;
		m_group[3] = quadedge3;

		quadedge1.m_next = quadedge3;
		quadedge2.m_next = quadedge2;
		quadedge3.m_next = quadedge1;
	}

	public Localisable getFrom()
	{
		return m_data.m_node;
	}

	public Localisable getTo()
	{
		return Sym().m_data.m_node;
	}

	public Vertex getFromPos()
	{
		return m_data.m_pos;
	}

	public Vertex getToPos()
	{
		return Sym().m_data.m_pos;
	}

	public QuadEdge Rot()
	{
//		return m_group.e[(m_r + 1) % 4];
		return m_group[( m_r + 1 )& 3];
	}

	public QuadEdge Sym()
	{
//		return Rot().Rot();
		QuadEdge    edgeRot = m_group[( m_r + 1 )& 3];

		return edgeRot.m_group[( edgeRot.m_r + 1 )& 3];
	}

	public QuadEdge RotInv()
	{
		return Rot().Rot().Rot();
	}

	public QuadEdge Oprev()
	{
//		return Rot().Onext().Rot();
		QuadEdge    edgeRotNext = m_group[( m_r + 1 )& 3].m_next;

		return edgeRotNext.m_group[( edgeRotNext.m_r + 1 )& 3];
	}

	public QuadEdge Lnext()
	{
		return RotInv().m_next.Rot();
	}

	public QuadEdge Rnext()
	{
		return Rot().m_next.RotInv();
	}

	public QuadEdge Dnext()
	{
//		return Sym().Onext().Sym();
		QuadEdge    edgeRot = m_group[( m_r + 1 )& 3];

		return edgeRot.m_group[( edgeRot.m_r + 1 )& 3].m_next.Sym();
	}

	public QuadEdge Lprev()
	{
//		return Onext().Sym();
		QuadEdge    edgeRot = m_next.m_group[( m_next.m_r + 1 )& 3];

		return edgeRot.m_group[( edgeRot.m_r + 1 )& 3];
//		return m_next.Sym();
	}

	public void splice(QuadEdge quadedge)
	{
//		if(isPrimal() && !quadedge.isPrimal() || !isPrimal() && quadedge.isPrimal())
/*		if ( isPrimal() == !quadedge.isPrimal())
		{
			System.out.println("Splice error: must be both primal or both dual.");
			System.exit(1);
		}*/
		QuadEdge quadedge1 = m_next.Rot();
		QuadEdge quadedge2 = quadedge.m_next.Rot();
		QuadEdge quadedge3 = m_next;
		m_next = quadedge.m_next;
		quadedge.m_next = quadedge3;
		quadedge3 = quadedge1.m_next;
		quadedge1.m_next = quadedge2.m_next;
		quadedge2.m_next = quadedge3;
	}

	public boolean isRightOf( Vertex vect )
	{
		return Vertex.isCCW( vect, Dest(), Org());
	}

	public boolean isLeftOf( Vertex vect )
	{
		return Vertex.isCCW( vect, Org(), Dest());
	}

	public Vertex Org()
	{
		return m_data.m_pos;
	}

	public Vertex Dest()
	{
		return Sym().m_data.m_pos;
	}

	public EdgeList edges()
	{
		return new EdgeList( this );
	}

	public Vertex getRightClusterPos()
	{
		return m_rightClu.getPos();
	}

	public Vertex getLeftClusterPos()
	{
		return m_leftClu.getPos();
	}

	public Vertex getCenter()
	{
		Vertex  from = m_data.m_pos;
		Vertex  to = Dest();

		return Vertex.center( from, to );
	}

	protected static final class EdgeList extends ArrayList
	{
/**
		 * 
		 */
		private static final long serialVersionUID = 2036889757915194457L;
		
		//		private ListIterator    m_itr;
		private HashMap         m_table;
//		private ArrayList       m_edges;
		private QuadEdge        m_edgeBuf;

		public EdgeList( QuadEdge quadedge )
		{
			super();
			m_table = new HashMap();
//			m_edges = new ArrayList();
			walkEdges( quadedge.m_group[0]);
//			m_itr   = m_edges.listIterator();
		}

		private void walkEdges( QuadEdge quadedge )
		{
			m_table.put( quadedge, null );
			add( quadedge );

			m_edgeBuf   = quadedge.m_next.m_group[0];

			if ( !m_table.containsKey( m_edgeBuf ))
				walkEdges( m_edgeBuf );

			m_edgeBuf   = quadedge.Sym().m_next.m_group[0];

			if ( !m_table.containsKey( m_edgeBuf ))
				walkEdges( m_edgeBuf );
		}

//		public boolean hasMoreElements()
//		{
//			return m_itr.hasNext();
//		}
//
//		public Object nextElement()
//		{
//			return m_itr.next();
//		}
	}

/*	public QuadEdge Rprev()
	{
		return Sym().m_next;
	}*/

/*	public QuadEdge Dprev()
	{
		return RotInv().m_next.RotInv();
	}*/

/*	public boolean isPrimal()
	{
		return ( m_r & 1 )== 0;
//		return m_r % 2 == 0;
	}*/

//	public boolean isCanonical()
//	{
//		return m_r == 0;
//	}

//	public QuadEdge canonical()
//	{
//		return m_group[0];
//	}

//	public QuadEdge Onext()
//	{
//		return m_next;
//	}
}

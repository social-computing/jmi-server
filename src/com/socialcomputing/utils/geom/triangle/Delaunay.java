package com.socialcomputing.utils.geom.triangle;

import java.util.Arrays;

import com.socialcomputing.utils.geom.Localisable;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.Bounds2D;
import com.socialcomputing.utils.math.EZMath;

public class Delaunay
{
	private NodeTriData[]   m_nodes;

	public Delaunay( Localisable[] nodes, Bounds2D bnds, int fakeCnt )
	{
		Vertex	    center  = bnds.getCenter(),
					pos;
		int         i, n    = nodes.length;
		float       a, da   = EZMath.PI2 / fakeCnt,
					r       = 200.f,//bnds.isWidthMin() ? bnds.getHeight() : bnds.getWidth(),
					err     = .05f * r;

		m_nodes = new NodeTriData[n+fakeCnt];

		for ( i = 0, a = 0; i < fakeCnt; i ++, a += da )
		{
			pos         = Vertex.createPolar( r - EZMath.random( err ), a );
			pos.addThis( center );
			m_nodes[i]  = new NodeTriData( pos );
		}

		for ( i = 0; i < n; i ++ )
		{
			m_nodes[i+fakeCnt]  = new NodeTriData( nodes[i]);
		}
	}

	public Delaunay( Localisable[] nodes )
	{
		int i, n    = nodes.length;

		m_nodes = new NodeTriData[n];

		for ( i = 0; i < n; i ++ )
		{
			m_nodes[i]  = new NodeTriData( nodes[i]);
		}
	}

	private QuadEdge Connect( QuadEdge quadedge, QuadEdge quadedge1 )
	{
		QuadEdge quadedge2 = new QuadEdge( quadedge.Sym().m_data, quadedge1.m_data );
		quadedge2.splice( quadedge.Lnext() );
		quadedge2.Sym().splice( quadedge1 );
		return quadedge2;
	}

	private void DeleteEdge(QuadEdge quadedge)
	{
		quadedge.splice(quadedge.Oprev());
		quadedge.Sym().splice(quadedge.Sym().Oprev());
	}

	/**
	 * Return the Delaunay tesselation of the nodes
	 * @return	The list of edges
	 */
	public QuadEdge process()
	{
		Arrays.sort( m_nodes, NodeTriData.s_comp );

		return compute( m_nodes )[0];//left
	}

	private QuadEdge[] compute( NodeTriData[] nodes )
	{
		int size    = nodes.length;

		if ( size == 2 )
		{
			QuadEdge quadedge = new QuadEdge( nodes[0], nodes[1] );

			return new QuadEdge[]{ quadedge, quadedge.Sym()};
		}
		else if( size == 3 )
		{
			Vertex      v0 = nodes[0].m_pos,
						v1 = nodes[1].m_pos,
						v2 = nodes[2].m_pos;
			QuadEdge    quadedge1 = new QuadEdge( nodes[0], nodes[1] ),
						quadedge2 = new QuadEdge( nodes[1], nodes[2] );

			quadedge1.Sym().splice( quadedge2 );

			if ( Vertex.isCCW( v0, v1, v2 ))
			{
				Connect( quadedge2, quadedge1 );

				return new QuadEdge[]{ quadedge1, quadedge2.Sym()};
			}
			else if ( Vertex.isCCW( v0, v2, v1 ))
			{
				QuadEdge    quadedge4 = Connect( quadedge2, quadedge1 );

				return new QuadEdge[]{ quadedge4.Sym(), quadedge4 };
			}
			else
			{
				return new QuadEdge[]{ quadedge1, quadedge2.Sym()};
			}
		}

		QuadEdge[]  edgepair    = compute( subArray( nodes, 0, size >> 1 )),
					edgepair1   = compute( subArray( nodes, size >> 1, size ));
		QuadEdge    quadedge5   = edgepair[0],//left;
					quadedge6   = edgepair[1],//right;
					quadedge7   = edgepair1[0],//left;
					quadedge8   = edgepair1[1];//right;

		for ( ; ; )
		{
			for ( ; quadedge6.isLeftOf( quadedge7.Org()); quadedge6 = quadedge6.Lnext());

			if ( !quadedge7.isRightOf( quadedge6.Org())) break;

			quadedge7 = quadedge7.Sym().m_next;
//			quadedge7 = quadedge7.Rprev();
		}

		QuadEdge    quadedge9 = Connect( quadedge7.Sym(), quadedge6 );

		if( quadedge6.Org().equals( quadedge5.Org()))
		{
			quadedge5 = quadedge9.Sym();
		}

		if( quadedge7.Org().equals( quadedge8.Org()))
		{
			quadedge8 = quadedge9;
		}

		Vertex  dst, org;

		for ( ; ; )
		{
			QuadEdge    quadedge10 = quadedge9.Sym().m_next;

			dst = quadedge9.Dest();
			org = quadedge9.Org();

			if ( quadedge9.isRightOf( quadedge10.Dest()))
			{
				QuadEdge    quadedge11;

				for ( ; Vertex.isInCircle( dst, org, quadedge10.Dest(), quadedge10.m_next.Dest()); quadedge10 = quadedge11 )
				{
					quadedge11 = quadedge10.m_next;
					DeleteEdge( quadedge10 );
				}

			}

			QuadEdge    quadedge12 = quadedge9.Oprev();

			if ( quadedge9.isRightOf( quadedge12.Dest()))
			{
				QuadEdge    quadedge13;

				for( ; Vertex.isInCircle( dst, org, quadedge12.Dest(), quadedge12.Oprev().Dest()); quadedge12 = quadedge13 )
				{
					quadedge13 = quadedge12.Oprev();
					DeleteEdge( quadedge12 );
				}
			}
			Vertex  v10Dst = quadedge10.Dest();
			Vertex  v12Dst = quadedge12.Dest();
			boolean is9RightOf10    = quadedge9.isRightOf( v10Dst ),
					is9RightOf12    = quadedge9.isRightOf( v12Dst );

			if ( is9RightOf10 || is9RightOf12 )
			{
				if ( !is9RightOf10 || is9RightOf12 && Vertex.isInCircle( v10Dst, quadedge10.Org(), quadedge12.Org(), v12Dst))
				{
					quadedge9 = Connect( quadedge12, quadedge9.Sym());
				}
				else
				{
					quadedge9 = Connect( quadedge9.Sym(), quadedge10.Sym());
				}
			}
			else
			{
				return new QuadEdge[]{ quadedge5, quadedge8 };
			}
		}
	}

	private NodeTriData[] subArray( NodeTriData[] nodes, int from, int to )
	{
		NodeTriData[]   subNodes    = new NodeTriData[to-from];

		System.arraycopy( nodes, from, subNodes, 0, to - from );

		return subNodes;
	}

//	private class EdgePair
//	{
//
//		public QuadEdge left;
//		public QuadEdge right;
//
//		public EdgePair(QuadEdge quadedge, QuadEdge quadedge1)
//		{
//			left = quadedge;
//			right = quadedge1;
//		}
//	}
}

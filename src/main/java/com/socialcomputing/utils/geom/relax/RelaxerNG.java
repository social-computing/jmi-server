package com.socialcomputing.utils.geom.relax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import com.socialcomputing.utils.geom.triangle.Delaunay;
import com.socialcomputing.utils.geom.triangle.QuadEdge;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.EZMath;

public final class RelaxerNG extends Relaxer
{
	protected float     m_linkRot 		= 0.2f;
	protected float     m_linkRep 		= 0.2f;
	protected float     m_unCross 		= 0.2f;
	protected float		m_linkRepVar	= 1.f;
	protected CrossMgr  m_crossMgr;

	private static final    Comparator  s_linkCmp   = new Comparator()
	{
		public int compare( Object o1, Object o2 )
		{
			LinkRelaxData   l1  = (LinkRelaxData)o1,
							l2  = (LinkRelaxData)o2;
			boolean         b1  = l1.isVisible(),
							b2  = l2.isVisible();
//			boolean         b1  = l1.isReady() && !l1.isDead(),
//							b2  = l2.isReady() && !l2.isDead();

			if ( b1 && b2 )
			{
				NodeRelaxData   nodeDat = l1.getLinkedNodeData( l2 );
				float           d1      = l1.getLinkDir( nodeDat ),
								d2      = l2.getLinkDir( nodeDat ),
								d       = d1 - d2;

				return d > 0 ? 1 :( d < 0 ? -1 : 0 );
			}
			else
			{
				return b1 ? -1 :( b2 ? 1 : 0 );
			}
		}
	};

	public RelaxerNG( CrossMgr crossMgr )
	{
		super();

		m_crossMgr  = crossMgr;
	}

	public void setParams( RelaxParams params, NodeRelaxData base )
	{
		super.setParams( params, base );

		if ( params != null )
		{
			m_linkRot	= params.getLinkRot();
			m_linkRep	= params.getLinkRep();
			m_unCross	= params.getCrossRep();
		}
	}

	public void updateParams( RelaxParams params )
	{
		params.setParams( m_scale, m_nodeRep, m_nodeRepVar, m_linkTens, m_linkTensVar, m_linkRep, m_linkRepVar, m_linkRot, m_unCross );
	}

	public CrossMgr getCrossMgr()
	{
		return m_crossMgr;
	}

	public void evalLinkRot( NodeRelaxData nodeDat )
	{
		NodeRelaxData   toDat;
		LinkRelaxData[] links   = nodeDat.m_links;
		int             i, n    = links.length;
		float           a; 
						//sclMin  = Float.MAX_VALUE;
		Vertex          pos     = nodeDat.m_pos,
						toPos, rotVec	= m_vertexBuf[VEC2_VTX];

		Arrays.sort( links, s_linkCmp );

		for ( i = n - 1; i >= 0; i -- )
		{
			if ( links[i].isVisible())	break;
//			if ( links[i].isReady() && !links[i].isDead())    break;
		}

		n   = i + 1;

		if ( n > 2 )
		{
			float   aPrv    = links[n-1].getLinkDir( nodeDat ),
					aCur    = links[0].getLinkDir( nodeDat ),
					aNxt;

			for ( i = 0; i < n; i ++ )
			{
				toDat   = links[i].getOtherNodeData( nodeDat );
				toPos   = toDat.m_pos;
				aNxt    = links[(i+1)%n].getLinkDir( nodeDat );

				// middle between previous and next node
				a = .5f *( aPrv + aNxt );
				if ( aPrv > aNxt )	a += EZMath.PI;

				rotVec.setPolar( links[i].m_length, a );//  = Vertex.createPolar( links[i].m_length, a );
				rotVec.addThis( pos );
				rotVec.subThis( toPos );

				toDat.m_rot.addThis( rotVec );

				aPrv    = aCur;
				aCur    = aNxt;
			}
		}
		else if ( n == 2 )
		{
			float   a0  = links[0].getLinkDir( nodeDat ),
					a1  = links[1].getLinkDir( nodeDat ),
					da  = .5f *( EZMath.PI + a0 - a1 );

			toDat   = links[0].getOtherNodeData( nodeDat );
			toPos   = toDat.m_pos;
			rotVec.setPolar( links[0].m_length, a0 - da );//Vertex.createPolar( links[0].m_length, a0 - da );
			rotVec.addThis( pos );
			rotVec.subThis( toPos );
			rotVec.scaleThis( .1f );

			toDat.m_rot.addThis( rotVec );

			toDat   = links[1].getOtherNodeData( nodeDat );
			toPos   = toDat.m_pos;
			rotVec.setPolar( links[1].m_length, a1 + da );//   = Vertex.createPolar( links[1].m_length, a1 + da );
			rotVec.addThis( pos );
			rotVec.subThis( toPos );
			rotVec.scaleThis( .1f );

			toDat.m_rot.addThis( rotVec );
		}
	}

	/**
	 * Repulsion of this node by the surrounding links.
	 * @param nodeDat the node to be repulsed by the links
	 */
	public void evalLinkRep( NodeRelaxData nodeDat )
	{
		LinkRelaxData   linkDat, fromLink, toLink;
		NodeRelaxData   fromDat, toDat;
		int             i,  n   = m_links.length;
		float           l, len, s,
						size    = nodeDat.m_size,
						maxSize	= nodeDat.m_maxSize,
						sepSize	= nodeDat.m_sepSize;
						//size2   = nodeDat.m_sepSize;
		Vertex          pos     = nodeDat.m_pos,
						cross   = nodeDat.m_linkRep,
						fromPos, toPos,
						U		= m_vertexBuf[VEC1_VTX],
						V		= m_vertexBuf[VEC2_VTX],
						linkMin = m_vertexBuf[MIN1_VTX],
						linkMax = m_vertexBuf[MAX1_VTX];

		for ( i = 0; i < n; i ++ )
		{
			linkDat = m_links[i];

			if ( linkDat.isVisible())//isReady() && linkDat.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))
			{
				fromDat = linkDat.m_from;
				toDat   = linkDat.m_to;

				if ( nodeDat != fromDat && nodeDat != toDat )
				{
					fromPos = fromDat.m_pos;
					toPos	= toDat.m_pos;

					if ( fromPos.x < toPos.x )
					{
						linkMin.x	= fromPos.x - linkDat.m_width;
						linkMax.x	= toPos.x + linkDat.m_width;
					}
					else
					{
						linkMin.x	= toPos.x - linkDat.m_width;
						linkMax.x	= fromPos.x + linkDat.m_width;
					}

					if ( fromPos.y < toPos.y )
					{
						linkMin.y	= fromPos.y - linkDat.m_width;
						linkMax.y	= toPos.y + linkDat.m_width;
					}
					else
					{
						linkMin.y	= toPos.y - linkDat.m_width;
						linkMax.y	= fromPos.y + linkDat.m_width;
					}

					if ( pos.x + sepSize > linkMin.x && pos.x - sepSize < linkMax.x &&
						pos.y + sepSize > linkMin.y && pos.y - sepSize < linkMax.y )
					{

					U.set( fromPos, toDat.m_pos );

					if ( U.x != 0 || U.y != 0 )
					{
						V.set( fromPos, pos );
						l	= ( U.x * V.x + U.y * V.y )/( U.x * U.x + U.y * U.y	);

						if ( l > 0 && l < 1 )       // node projection is in link
						{
							U.scaleThis( l );       // 'from' to 'intersection' vector
							U.addThis( fromPos );   // intersection pos
							U.subThis( pos );       // 'node' to 'intersection' vector
							len	= U.sqrLength();

							if ( len == 0 )			// 'node' is on 'link'
							{
								U	= V.turn();
//								len	= U.sqrLength();
							}

							fromLink	= nodeDat.getLinkDataTo( linkDat.m_from );
							toLink		= nodeDat.getLinkDataTo( linkDat.m_to );

							boolean	isWeak		= m_linkRepVar < .5f,
									isLinked	= ( fromLink != null && fromLink.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))||( toLink != null && toLink.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ));
							float	q			=  isWeak ? 2.f * m_linkRepVar : 2.f * m_linkRepVar - 1.f,
									distMin		= linkDat.m_width + maxSize,
									distMax		= linkDat.m_width + sepSize,
									distMax2;

							if ( isLinked == isWeak )
							{
								if ( isWeak )	// linked && m_linkRepVar < .5
								{
									distMax	= distMin;// + maxSize;
									distMin	= linkDat.m_width + size;
								}
								else			// separated && m_linkRepVar >= .5
								{
									distMin	= distMax;//linkDat.m_width + sepSize;
								}
							}

							if ( distMin != distMax )	distMax	= distMin + q *( distMax - distMin );

							distMax2    = distMax * distMax;

							if ( len < distMax2 )	// Not too far away
							{
								if ( len == 0 )
								{
									U.resize( -.66f * distMax );
								}
								else
								{
									s	= l > .5f ? 1.f - l : l;
									len	= s * ( distMax2 - len )/( distMax *(float)Math.sqrt( len ));

									U.scaleThis( -.66f * len );
								}
								cross.addThis( U );
								U.scaleThis( -.5f );
								fromDat.m_linkRep.addThis( U );
								toDat.m_linkRep.addThis( U );
							}
						}
					}
					}
				}
			}
		}
	}

	public void balance( NodeRelaxData nodeDat )
	{
		Vertex  		pos     = nodeDat.m_pos,
						tens    = nodeDat.m_tens,
						rep     = nodeDat.m_rep,
						rot     = nodeDat.m_rot,
						cross   = nodeDat.m_linkRep,
						cRep    = nodeDat.m_crossRep;
		float           mix     = m_scale *( 1.f - nodeDat.m_inertia ),
						dx, dy;

		if ( nodeDat.m_inertia < 1.f && !nodeDat.m_isLocked )
		{
			dx = mix *( m_linkTens * tens.x + m_nodeRep * rep.x + m_linkRot * rot.x + m_linkRep * cross.x + m_unCross * cRep.x );
			dy = mix *( m_linkTens * tens.y + m_nodeRep * rep.y + m_linkRot * rot.y + m_linkRep * cross.y + m_unCross * cRep.y );
			pos.x += dx;
			pos.y += dy;
			if ( Float.isNaN( pos.x ) || Float.isNaN( pos.y ))
			{
				throw ( new RuntimeException( "balance tens=" + tens + " rep=" + rep + " rot=" + rot + " cross=" + cross + " cRep=" + cRep ));
			}
		}

		nodeDat.m_repBak.setLocation( rep );//    = new Vertex( rep );
		nodeDat.m_tensBak.setLocation( tens );//   = new Vertex( tens );
		nodeDat.m_rotBak.setLocation( rot );//    = new Vertex( rot );
		nodeDat.m_linkRepBak.setLocation( cross );//  = new Vertex( cross );
		nodeDat.m_crossRepBak.setLocation( cRep );//   = new Vertex( cRep );

		rep.reset();
		tens.reset();
		rot.reset();
		cross.reset();
		cRep.reset();
//		rep.x   = rep.y     = 0.f;
//		tens.x  = tens.y    = 0.f;
//		rot.x   = rot.y     = 0.f;
//		cross.x = cross.y   = 0.f;
//		cRep.x  = cRep.y    = 0.f;
	}

	public void oldSwap( final int beg, final int end )
	{
		int                 i, j, n;
		LinkRelaxData[]     nLnks;
		NodeRelaxData       nodeDat, A, B;
		Vertex              U, V;
		float               dot;

		for ( j = beg; j <= end; j ++ )
		{
			nodeDat = m_nodes[j];

			if ( nodeDat.isRelaxable())
			{
				nLnks   = nodeDat.m_links;
				n       = nLnks.length;
				A       = null;
				B       = null;

				for ( i = 0; i < n; i ++ )
				{
					if ( nLnks[i].isRelaxable())
					{
						if ( B != null )    break;
						else
						{
							if ( A == null )    A = nLnks[i].getOtherNodeData( nodeDat );
							else                B = nLnks[i].getOtherNodeData( nodeDat );
						}
					}
				}

				if ( i == n && B != null && A.getLinkDataTo( B )!= null )
				{
					U       = Vertex.create( A.m_pos, B.m_pos );
					V       = Vertex.create( A.m_pos, nodeDat.m_pos );
					dot     = Vertex.crossProduct( U, V );

					if ( isSwapable( A, B, nodeDat, U, dot )&& isSwapable( B, A, nodeDat, U, dot ))
					{
						U.subThis( V );
						U.addThis( A.m_pos );
						nodeDat.m_pos.setLocation( U );
					}
				}
			}
		}
	}

	public void postProcess( final int beg, final int end, boolean needFilter, boolean needSwap )
	{
		int                 i, n;
		//LinkRelaxData[]     nLnks;
		//NodeRelaxData       nodeDat, A, B;
		//Vertex              U, V;
		//float               dot;

		m_crossMgr.clearInters();

		if ( needFilter || m_unCross > 0 )
		{
			m_crossMgr.evalInters( m_nodes, m_links, false );
		}

		// Old rough methode!
		if ( needSwap )
		{
			oldSwap( beg, end );
		}

		n	= m_nodes.length;

		for ( i = 0; i < n; i ++ )
		{
			m_nodes[i].updateFlags();
		}

		if ( needFilter )
		{
			m_crossMgr.filter( false );
		}
	}

	protected HashMap getEditableFields()
	{
		HashMap	fieldMap	= super.getEditableFields();

		fieldMap.put( "link uncrossing", "com.socialcomputing.utils.geom.relax.RelaxerNG.m_unCross" );
		fieldMap.put( "link repulsion", "com.socialcomputing.utils.geom.relax.RelaxerNG.m_linkRep" );
		fieldMap.put( "link rep Sep/Ext ratio", "com.socialcomputing.utils.geom.relax.RelaxerNG.m_linkRepVar" );
		fieldMap.put( "link rotation", "com.socialcomputing.utils.geom.relax.RelaxerNG.m_linkRot" );

		return fieldMap;
	}

	private boolean isSwapable( NodeRelaxData A, NodeRelaxData B, NodeRelaxData nodeDat, Vertex U, float dot )
	{
		Vertex          V, APos = A.m_pos;
		LinkRelaxData[] links   = A.m_links;
		NodeRelaxData   curDat;
		int             i, j, n = links.length;

		for ( i = j = 0; i < n; i ++ )
		{
			if ( links[i].isRelaxable())
			{
				curDat  = links[i].getOtherNodeData( A );
				j ++;

				if ( curDat == B || curDat == nodeDat ) continue;

				V   = Vertex.create( APos, curDat.m_pos );

				if ( Vertex.crossProduct( U, V )* dot < 0 ) return false;
			}
		}

		return j > 2;
	}

	public void relaxeEx( final int beg, final int end )
	{
		int             i, n = m_links.length;
		Vertex          fromPos, toPos;
		float           dir;
		LinkRelaxData   linkDat;
		NodeRelaxData   nodeDat;

		// Eval links dir
		for ( i = 0; i < n; i ++ )
		{
			linkDat = m_links[i];

			if ( linkDat.isRelaxable())
			{
				fromPos = linkDat.m_from.m_pos;
				toPos   = linkDat.m_to.m_pos;
				dir     = (float)Math.atan2( toPos.y - fromPos.y, toPos.x - fromPos.x );
				if ( dir < 0 )  dir = EZMath.PI2 + dir;

				linkDat.m_dir   = dir;
			}
		}

		if ( m_linkRot != 0 )
		{
			for ( i = beg; i <= end; i ++ )
			{
				nodeDat = m_nodes[i];

				if ( nodeDat.isRelaxable())
				{
					evalLinkRot( nodeDat );
				}
			}
		}

		if ( m_linkRep != 0 )
		{
			for ( i = beg; i <= end; i ++ )
			{
				nodeDat = m_nodes[i];

				if ( nodeDat.isRelaxable())
				{
					evalLinkRep( nodeDat );
				}
			}
		}

		// try decrossing by swapping nodes.
		m_crossMgr.updateCrossRep();
	}

	public void tesselate( Delaunay delaunay, int linkCnt )
	{
		ArrayList       edges   	= delaunay.process().edges();
		QuadEdge        edge;
		NodeRelaxData   fromDat, toDat;
		LinkRelaxData   linkDat;
		LinkRelaxData[] tessLinks	= new LinkRelaxData[3*m_nodes.length];

		int             i, n    = edges.size(),
						m		= 0;

		for ( i = m = 0; i < n; i ++ )
		{
			edge    = (QuadEdge)edges.get( i );
			fromDat = (NodeRelaxData)edge.getFrom();
			toDat   = (NodeRelaxData)edge.getTo();
			linkDat = fromDat.getLinkDataTo( toDat );

			if (( linkDat == null || linkDat.m_flags.isEnabled( LinkRelaxData.FILTER_BIT ))  // this link doesn't already exists or has been removed
				&& !m_crossMgr.isInter( fromDat, toDat, m_links ))
			{
				tessLinks[m++]    = new LinkRelaxData( fromDat, toDat );
			}
		}

		LinkRelaxData[] links	= new LinkRelaxData[linkCnt+m];

		System.arraycopy( m_links, 0, links, 0, linkCnt );
		System.arraycopy( tessLinks, 0, links, linkCnt, m );
		m_links	= links;
	}
}
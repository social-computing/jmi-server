package com.socialcomputing.utils.geom.relax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.geom.Vertex;

public final class CrossMgr
{
	public	static final    int     SWAP_BIT    = 0x00001000;
	public	static final    int     FILTER_BIT  = 0x00002000;

	private HashMap   	m_leftCrs;
	private HashMap   	m_rightCrs;
	private ArrayList   m_crossLst;

	private static final Comparator	s_crossLockCmp	= new Comparator()
	{
		public int compare( Object o1, Object o2 )
		{
			Map.Entry   	e1  = (Map.Entry)o1,
							e2  = (Map.Entry)o2;
			LinkRelaxData	link1	= (LinkRelaxData)e1.getKey(),
							link2	= (LinkRelaxData)e2.getKey();
			int     		comp;

			if (( comp = compareHardTerms( link1.m_flags.isEnabled( LinkRelaxData.HARDTERM_BIT ), link2.m_flags.isEnabled( LinkRelaxData.HARDTERM_BIT )))!= 0 )
				return comp;

			if (( comp = compareLocks( link1.m_flags.isEnabled( LinkRelaxData.LOCKED_BIT ), link2.m_flags.isEnabled( LinkRelaxData.LOCKED_BIT )))!= 0 )
				return comp;

			if (( comp = compareCrosses(((Integer)e1.getValue()).intValue(), ((Integer)e2.getValue()).intValue()))!= 0 )
				return comp;

			return compareWidths( link1.m_width, link2.m_width );
		}
	};

	private static final Comparator	s_crossCmp	= new Comparator()
	{
		public int compare( Object o1, Object o2 )
		{
			Map.Entry   	e1  = (Map.Entry)o1,
							e2  = (Map.Entry)o2;
			LinkRelaxData	link1	= (LinkRelaxData)e1.getKey(),
							link2	= (LinkRelaxData)e2.getKey();
			int     		comp;

			if (( comp = compareTerms( link1.isTerminator(), link2.isTerminator()))!= 0 )
				return comp;

			if (( comp = compareCrosses(((Integer)e1.getValue()).intValue(), ((Integer)e2.getValue()).intValue()))!= 0 )
				return comp;

			return compareWidths( link1.m_width, link2.m_width );
		}
	};

	private static final Comparator	s_strictCrossLockCmp	= new Comparator()
	{
		public int compare( Object o1, Object o2 )
		{
			Map.Entry   	e1  = (Map.Entry)o1,
							e2  = (Map.Entry)o2;
			LinkRelaxData	link1	= (LinkRelaxData)e1.getKey(),
							link2	= (LinkRelaxData)e2.getKey();
			int     		comp;
			float			fCmp;

			if (( comp = compareLocks( link1.m_flags.isEnabled( LinkRelaxData.LOCKED_BIT ), link2.m_flags.isEnabled( LinkRelaxData.LOCKED_BIT )))!= 0 )
				return comp;

			if (( comp = compareCrosses(((Integer)e1.getValue()).intValue(), ((Integer)e2.getValue()).intValue()))!= 0 )
				return comp;

			if (( comp = compareWidths( link1.m_width, link2.m_width ))!= 0 )
				return comp;

			fCmp	=	link1.m_from.m_pos.x - link2.m_from.m_pos.x;
			if (( comp = fCmp > 0 ? 1 :( fCmp < 0 ? -1 : 0 ))!= 0 )
				return comp;

			fCmp	=	link1.m_from.m_pos.y - link2.m_from.m_pos.y;
			if (( comp = fCmp > 0 ? 1 :( fCmp < 0 ? -1 : 0 ))!= 0 )
				return comp;

			fCmp	=	link1.m_to.m_pos.x - link2.m_to.m_pos.x;
			if (( comp = fCmp > 0 ? 1 :( fCmp < 0 ? -1 : 0 ))!= 0 )
				return comp;

			fCmp	=	link1.m_to.m_pos.y - link2.m_to.m_pos.y;
			return fCmp > 0 ? 1 :( fCmp < 0 ? -1 : 0 );
		}
	};

	private static final Comparator	s_strictCrossCmp	= new Comparator()
	{
		public int compare( Object o1, Object o2 )
		{
			Map.Entry   	e1  = (Map.Entry)o1,
							e2  = (Map.Entry)o2;
			LinkRelaxData	link1	= (LinkRelaxData)e1.getKey(),
							link2	= (LinkRelaxData)e2.getKey();
			int     		comp;
			float			fCmp;

			if (( comp = compareTerms( link1.isTerminator(), link2.isTerminator()))!= 0 )
				return comp;

			if (( comp = compareCrosses(((Integer)e1.getValue()).intValue(), ((Integer)e2.getValue()).intValue()))!= 0 )
				return comp;

			if (( comp = compareWidths( link1.m_width, link2.m_width ))!= 0 )
				return comp;

			fCmp	=	link1.m_from.m_pos.x - link2.m_from.m_pos.x;
			if (( comp = fCmp > 0 ? 1 :( fCmp < 0 ? -1 : 0 ))!= 0 )
				return comp;

			fCmp	=	link1.m_from.m_pos.y - link2.m_from.m_pos.y;
			if (( comp = fCmp > 0 ? 1 :( fCmp < 0 ? -1 : 0 ))!= 0 )
				return comp;

			fCmp	=	link1.m_to.m_pos.x - link2.m_to.m_pos.x;
			if (( comp = fCmp > 0 ? 1 :( fCmp < 0 ? -1 : 0 ))!= 0 )
				return comp;

			fCmp	=	link1.m_to.m_pos.y - link2.m_to.m_pos.y;
			return fCmp > 0 ? 1 :( fCmp < 0 ? -1 : 0 );
			}
	};

	public CrossMgr()
	{
		m_leftCrs   = new HashMap();
		m_rightCrs  = new HashMap();
		m_crossLst  = new ArrayList();
	}

	private boolean intersect( Vertex A, Vertex B, Vertex C, Vertex D )
	{
		float   d1  = (B.x - A.x)*(C.y - A.y) - (C.x - A.x)*(B.y - A.y),// = det(P1, P2, P3)
				d2  = (B.x - A.x)*(D.y - A.y) - (D.x - A.x)*(B.y - A.y),// = det(P1, P2, P4)
				d3  = (C.x - A.x)*(D.y - A.y) - (D.x - A.x)*(C.y - A.y),// = det(P3, P4, P1)
				d4  = d1 - d2 + d3;         // det(P1, P2, P3) - det(P1, P2, P4) + det(P3, P4, P1) = det(P3, P4, P2)

		return ( d1 * d2 < 0 && d3 * d4 < 0 )||( d1 == 0 && d2 == 0 && d3 == 0 && d4 == 0 );  // det(P1, P2, P3) * det(P1, P2, P4) < 0 && det(P3, P4, P1) * det(P3, P4, P2) < 0
	}

	public void clearInters()
	{
		int             i, n	= m_crossLst.size(),
						disBits	= LinkRelaxData.INTER_BIT | LinkRelaxData.HARDTERM_BIT | LinkRelaxData.SHADOW_BIT;
		Cross           cross;
		EZFlags			flagsA, flagsB;

		m_leftCrs.clear();
		m_rightCrs.clear();

		for ( i = 0; i < n; i ++ )
		{
			cross   = (Cross)m_crossLst.get( i );
			flagsA	= cross.m_linkA.m_flags;
			flagsB	= cross.m_linkB.m_flags;

			if ( flagsA.isEnabled( LinkRelaxData.INTER_BIT ))
			{
				flagsA.disable( disBits );

//				if ( cross.m_linkA.m_label.equals( "75" ))	System.out.println( "Before " + cross.m_linkA );

				if ( flagsA.isEnabled( LinkRelaxData.FILTER_BIT )&& flagsA.isDisabled( LinkRelaxData.LOCKED_BIT ))
				{
					flagsA.disable( LinkRelaxData.FILTER_BIT );
					flagsA.enable( LinkRelaxData.SHADOW_BIT );
				}

//				if ( cross.m_linkA.m_label.equals( "75" ))	System.out.println( "After " + cross.m_linkA );
			}

			if ( flagsB.isEnabled( LinkRelaxData.INTER_BIT ))
			{
				flagsB.disable( disBits );

//				if ( cross.m_linkB.m_label.equals( "75" ))	System.out.println( "Before " + cross.m_linkB );

				if ( flagsB.isEnabled( LinkRelaxData.FILTER_BIT )&& flagsB.isDisabled( LinkRelaxData.LOCKED_BIT ))
				{
					flagsB.disable( LinkRelaxData.FILTER_BIT );
					flagsB.enable( LinkRelaxData.SHADOW_BIT );
				}

//				if ( cross.m_linkB.m_label.equals( "75" ))	System.out.println( "After " + cross.m_linkB );
			}
		}

		m_crossLst.clear();
	}

	public void evalInters( NodeRelaxData[] nodes, LinkRelaxData[] links, boolean withLocks )
	{
		int             i, j, n     = m_crossLst.size();
		LinkRelaxData   linkDat, link2Dat;
		Vertex          fromPos, toPos;
		//ArrayList       list;
		//Cross           cross;
		boolean			fromLock, toLock,
						//fromTerm, toTerm,
						fromLT, toLT;

		n   = links.length;

		// Eval links intersections
		for ( i = 0; i < n; i ++ )
		{
			linkDat = links[i];

			if ( linkDat.isRelaxable())
			{
				fromLock	= linkDat.m_flags.isEnabled( LinkRelaxData.LOCKED_BIT );

				if ( !fromLock || withLocks )
				{
					fromLT		= !fromLock && linkDat.isTerminator();
					fromPos		= linkDat.m_from.m_pos;
					toPos   	= linkDat.m_to.m_pos;

					for ( j = i + 1; j < n; j ++ )
					{
						link2Dat    = links[j];

						if ( link2Dat.isRelaxable())
						{
							toLock	= link2Dat.m_flags.isEnabled( LinkRelaxData.LOCKED_BIT );

							if ( !toLock || withLocks )
							{
								if (!( toLock && fromLock )&& intersect( fromPos, toPos, link2Dat.m_from.m_pos, link2Dat.m_to.m_pos ))
								{
									toLT	= !toLock && link2Dat.isTerminator();
									if ( fromLT && !toLT ) linkDat.m_flags.enable( LinkRelaxData.HARDTERM_BIT );
									if ( !fromLT && toLT ) link2Dat.m_flags.enable( LinkRelaxData.HARDTERM_BIT );
									linkDat.m_flags.enable( LinkRelaxData.INTER_BIT );
									link2Dat.m_flags.enable( LinkRelaxData.INTER_BIT );
									insert( linkDat, link2Dat );
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean isInter( NodeRelaxData fromDat, NodeRelaxData toDat, LinkRelaxData[] links )
	{
		LinkRelaxData   linkDat;
		Vertex 			fromPos	= fromDat.m_pos,
						toPos	= toDat.m_pos;
		int             i, n    = links.length;

		for ( i = 0; i < n; i ++ )
		{
			linkDat    = links[i];

			if ( linkDat.isRelaxable()&& linkDat.m_flags.isDisabled( LinkRelaxData.FILTER_BIT )
				&& intersect( fromPos, toPos, linkDat.m_from.m_pos, linkDat.m_to.m_pos ))
			{
				return true;
			}
		}

		return false;
	}

	public boolean hasOneInterMax( NodeRelaxData fromDat, NodeRelaxData toDat, LinkRelaxData[] links, int[] inters )
	{
		LinkRelaxData   linkDat;
		Vertex 			fromPos		= fromDat.m_pos,
						toPos		= toDat.m_pos;
		int             i, n    	= links.length;
		boolean			hasInter	= false;

		for ( i = 0; i < n; i ++ )
		{
			linkDat    = links[i];

			if ( linkDat.isRelaxable()&& linkDat.m_flags.isDisabled( LinkRelaxData.FILTER_BIT )
				&& intersect( fromPos, toPos, linkDat.m_from.m_pos, linkDat.m_to.m_pos ))
			{
				if ( hasInter )	return false;
				else
				{
					inters[0]	= i;
					hasInter	= true;
				}
			}
		}

		return true;
	}

	public void untangle()
	{
		int             i, n;
		LinkRelaxData   linkDat, link2Dat;
		ArrayList       list;
		Iterator		keyItr	= m_leftCrs.keySet().iterator();
		NodeRelaxData   A, B, C, D;
		Cross           cross;

		while ( keyItr.hasNext())
		{
			linkDat = (LinkRelaxData)keyItr.next();

			if ( linkDat.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))
			{
				list    = (ArrayList)m_leftCrs.get( linkDat );
				A       = (NodeRelaxData)linkDat.m_from;
				B       = (NodeRelaxData)linkDat.m_to;
				n       = list.size();

				for ( i = 0; i < n; i ++ )
				{
					cross   = (Cross)list.get( i );

					if ( !cross.m_isSwap )
					{
						link2Dat    = cross.getOtherLink( linkDat );

						if ( link2Dat.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))
						{
							C           = (NodeRelaxData)link2Dat.m_from;
							D           = (NodeRelaxData)link2Dat.m_to;

							if ( isSwapable( A, B, C, D ))      unswap( A, B, C, D, cross );
							else if ( isSwapable( B, A, C, D )) unswap( B, A, C, D, cross );
							else if ( isSwapable( C, D, A, B )) unswap( C, D, A, B, cross );
							else if ( isSwapable( D, C, A, B )) unswap( D, C, A, B, cross );
						}
					}
				}
			}
		}
	}

	public void updateCrossRep()
	{
		int             i, n    = m_crossLst.size();
		LinkRelaxData   linkDat, link2Dat;
		ArrayList       list;
		Iterator		keyItr	= m_leftCrs.keySet().iterator();
		NodeRelaxData   A, B, C, D;
		Cross           cross;

		for ( i = 0; i < n; i ++ )
		{
			((Cross)m_crossLst.get( i )).m_isSwap    = false;
		}

		while ( keyItr.hasNext())
		{
			linkDat = (LinkRelaxData)keyItr.next();

			if ( linkDat.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))
			{
				list    = (ArrayList)m_leftCrs.get( linkDat );
				A       = (NodeRelaxData)linkDat.m_from;
				B       = (NodeRelaxData)linkDat.m_to;
				n       = list.size();

				for ( i = 0; i < n; i ++ )
				{
					cross   = (Cross)list.get( i );

					if ( !cross.m_isSwap && !cross.m_isDel )
					{
						link2Dat    = cross.getOtherLink( linkDat );

						if ( link2Dat.m_flags.isDisabled( LinkRelaxData.FILTER_BIT ))
						{
							C           = (NodeRelaxData)link2Dat.m_from;
							D           = (NodeRelaxData)link2Dat.m_to;

							if ( intersect( A.m_pos, B.m_pos, C.m_pos, D.m_pos ))
							{
								if ( isSwapable( A, B, C, D ))      unswapRep( A, B, C, D, cross );
								else if ( isSwapable( B, A, C, D )) unswapRep( B, A, C, D, cross );
								else if ( isSwapable( C, D, A, B )) unswapRep( C, D, A, B, cross );
								else if ( isSwapable( D, C, A, B )) unswapRep( D, C, A, B, cross );
							}
							else
							{
								if ( !cross.m_isDel )
								{
									cross.m_isDel   = true;
									A.m_crossRep.reset();//    = new Vertex();
									B.m_crossRep.reset();//    = new Vertex();
									C.m_crossRep.reset();//    = new Vertex();
									D.m_crossRep.reset();//    = new Vertex();
								}
							}
						}
					}
				}
			}
		}
	}

	private void unswapRep( NodeRelaxData A, NodeRelaxData B, NodeRelaxData C, NodeRelaxData D, Cross cross )
	{
		Vertex  I       = Vertex.center( C.m_pos, D.m_pos ),
				repVec  = Vertex.center( I, B.m_pos );

		repVec.subThis( A.m_pos );
		A.m_crossRep.addThis( repVec );
		A.m_rep.reset();// = new Vertex();
		A.m_rot.reset();// = new Vertex();
		A.m_linkRep.reset();// = new Vertex();
		C.m_linkRep.reset();// = new Vertex();
		D.m_linkRep.reset();// = new Vertex();

		cross.m_isSwap   = true;
	}

	private void unswap( NodeRelaxData A, NodeRelaxData B, NodeRelaxData C, NodeRelaxData D, Cross cross )
	{
		A.m_pos.setLocation( Vertex.center( A.m_pos, B.m_pos ));

		cross.m_isSwap   = true;
	}

	private boolean isSwapable( NodeRelaxData A, NodeRelaxData B, NodeRelaxData C, NodeRelaxData D )
	{
		LinkRelaxData[]     links   = A.m_links;
		NodeRelaxData       nodeDat;
		Vertex              CPos    = C.m_pos,
							U       = Vertex.create( CPos, D.m_pos ),
							V       = Vertex.create( CPos, B.m_pos );
		float               dot     = Vertex.crossProduct( U, V );
		int                 i, n    = links.length;

		for ( i = 0; i < n; i ++ )
		{
			if ( links[i].isRelaxable())
			{
				nodeDat = links[i].getOtherNodeData( A );

				if ( nodeDat == B || nodeDat == C || nodeDat == D ) continue;
				else
				{
					V   = Vertex.create( CPos, nodeDat.m_pos );

					if ( Vertex.crossProduct( U, V )* dot < 0 ) break;
				}
			}
		}

		return i == n;
	}

	/**
	 * /!\ evalInters should be previously called to ensure intersections are evaluated
	 *      and crosses are properly initialized (m_isSwap=false) /!\
	 */
	public void filter( boolean withLocks )
	{
		int             i, n;
		LinkRelaxData   linkDat;
		//ArrayList       list;
		//NodeRelaxData   A, B, C, D;
		Cross           cross;
		HashMap         linkMap     = new HashMap();
		//CrossLink       crossLink;
		Integer         cCnt;

		n   = m_crossLst.size();

		if ( n > 0 )    // There are at least 2 crossing links
		{
			for ( i = 0; i < n; i ++ )
			{
				cross       = (Cross)m_crossLst.get( i );

				linkDat     = cross.m_linkA;
				cCnt        = (Integer)linkMap.get( linkDat );
				linkMap.put( linkDat, new Integer( cCnt == null ? 1 : cCnt.intValue()+ 1 ));

				linkDat     = cross.m_linkB;
				cCnt        = (Integer)linkMap.get( linkDat );
				linkMap.put( linkDat, new Integer( cCnt == null ? 1 : cCnt.intValue()+ 1 ));
			}

			n   = linkMap.size();

			Map.Entry[] crossLinks  = (Map.Entry[])linkMap.entrySet().toArray( new Map.Entry[n]);

//			if ( withLocks )	System.out.println("CrossLinks");
//			if ( withLocks )	dumpCrossLinks( crossLinks );

			Comparator	strictCmp	= withLocks ? s_strictCrossLockCmp : s_strictCrossCmp,
						comp		= withLocks ? s_crossLockCmp : s_crossCmp;
			Arrays.sort( crossLinks, strictCmp );

//			dumpCrossLinks( crossLinks );
//			if ( withLocks )	dumpCrossLinks( crossLinks );

			while (((Integer)crossLinks[0].getValue()).intValue()> 0 )
			{
				linkDat = (LinkRelaxData)crossLinks[0].getKey();

				if ( !withLocks &&( linkDat.isTerminator()|| linkDat.m_flags.isEnabled( LinkRelaxData.LOCKED_BIT )))	break;

				linkDat.filter();
				removeFromTable( linkDat, linkMap, m_leftCrs );
				removeFromTable( linkDat, linkMap, m_rightCrs );

				Arrays.sort( crossLinks, comp );

//				if ( withLocks )	dumpCrossLinks( crossLinks );
			}
		}
	}

	private static final int compareHardTerms( boolean isAHTerm, boolean isBHTerm )
	{
		if ( isAHTerm || isBHTerm )
		{
			if ( !isBHTerm )		return 1;
			else if ( isAHTerm )	return 0;
			else					return -1;
		}
		else						return 0;
	}

	private static final int compareLocks( boolean isALocked, boolean isBLocked )
	{
		if ( isALocked || isBLocked )
		{
			if ( !isBLocked )		return 1;
			else if ( isALocked )	return 0;
			else					return -1;
		}
		else						return 0;
	}

	private static final int compareTerms( boolean isATerm, boolean isBTerm )
	{
		if ( isATerm || isBTerm )
		{
			if ( !isBTerm )		return 1;
			else if ( isATerm )	return 0;
			else				return -1;
		}
		else					return 0;
	}

	private static final int compareCrosses( int crossA, int crossB )
	{
		return crossB - crossA;
	}

	private static final int compareWidths( float widthA, float widthB )
	{
		float	dw	= widthB - widthA;

		return dw > 0 ? -1 :( dw < 0 ? 1 : 0 );
	}

	private void removeFromTable( LinkRelaxData linkDat, HashMap linkMap, HashMap crossTbl )
	{
		ArrayList       list    = (ArrayList)crossTbl.get( linkDat );
		int             i, l, n = list != null ? list.size(): 0 ;
		//Integer         cCnt;
		Cross           cross;
		LinkRelaxData   linkA, linkB;

		for ( i = 0; i < n; i ++ )
		{
			cross   = (Cross)list.get( i );

			if ( cross.m_isSwap )    continue;
			else
			{
				linkA   = cross.m_linkA;
				linkB   = cross.m_linkB;

				l       = ((Integer)linkMap.get( linkA )).intValue();
				linkMap.put( linkA, new Integer( l - 1 ));
//				System.out.println( "dec " + linkA + " " + l + " - 1" );

				l       = ((Integer)linkMap.get( linkB )).intValue();
				linkMap.put( linkB, new Integer( l - 1 ));
//				System.out.println( "dec " + linkB + " " + l + " - 1" );
				cross.m_isSwap   = true;
			}
		}
	}

	protected boolean contains( LinkRelaxData link )
	{
		return m_leftCrs.containsKey( link ) || m_rightCrs.containsKey( link );
	}

	protected void insert( LinkRelaxData  A, LinkRelaxData B )
	{
		Cross       cross   = new Cross( A, B );

		m_crossLst.add( cross );
		insertTable( cross, m_leftCrs );
		insertTable( cross, m_rightCrs );
	}

	private void insertTable( Cross cross, HashMap<LinkRelaxData, ArrayList<Cross>> crossTbl )
	{
		LinkRelaxData   	linkDat     = crossTbl == m_leftCrs ? cross.m_linkA : cross.m_linkB;
		ArrayList<Cross>    list        = crossTbl.get( linkDat );

		if ( list == null )
		{
			list    = new ArrayList<Cross>();
			crossTbl.put( linkDat, list );
		}

		list.add( cross );
	}

	public static void main( String[] args )
	{
		LinkRelaxData   linkDat;
		HashMap         linkMap     = new HashMap();
		int[][]			linkVals	= new int[][]
		{// Lock Cross Width
			{0,1,2},
			{1,1,2},
			{0,1,8},
			{1,1,8},
			{0,2,3},
			{1,2,3},
			{0,2,1},
			{1,2,1},
			{0,3,10},
			{1,3,10},
			{0,3,5},
			{1,3,5},
		};
		int             i, n		= linkVals.length;

		for ( i = 0; i < n; i ++ )
		{
			linkDat	= new LinkRelaxData( 10.f, 1.f, linkVals[i][2], true, linkVals[i][0] << 2 );
			linkMap.put( linkDat, new Integer( linkVals[i][1] ));
		}

		Map.Entry[] crossLinks  = (Map.Entry[])linkMap.entrySet().toArray( new Map.Entry[n]);
		dumpCrossLinks( crossLinks );
		Arrays.sort( crossLinks, s_crossCmp );
		dumpCrossLinks( crossLinks );
	}

	private static void dumpCrossLinks( Map.Entry[] crossLinks )
	{
		LinkRelaxData	linkDat;
		int             i, n	= crossLinks.length,
						cCnt;

		for ( i = 0; i < n; i ++ )
		{
			linkDat	= (LinkRelaxData)crossLinks[i].getKey();
			cCnt	= ((Integer)crossLinks[i].getValue()).intValue();

			System.out.println( "[" + i + "] " + linkDat + ", c=" + cCnt + ", w=" + linkDat.m_width );
		}
	}

	class Cross
	{
		protected LinkRelaxData m_linkA;
		protected LinkRelaxData m_linkB;
		protected boolean       m_isSwap;
		protected boolean       m_isDel;

		public Cross( LinkRelaxData A, LinkRelaxData B )
		{
			m_linkA = A;
			m_linkB = B;
			m_isSwap = false;
			m_isDel = false;
		}

		public LinkRelaxData getOtherLink( LinkRelaxData linkDat )
		{
			return linkDat == m_linkA ? m_linkB : m_linkA;
		}
	}

	class CrossLink
	{
		protected LinkRelaxData m_link;
		protected int           m_crossCnt;

		public CrossLink( LinkRelaxData linkDat )
		{
			m_crossCnt  = 0;
		}
	}
}
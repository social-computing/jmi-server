package com.socialcomputing.utils.geom.relax;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.geom.Localisable;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.Bounds2D;
import com.socialcomputing.utils.math.EZMath;

public final class LinkRelaxData extends RelaxData
{
	public	static final	int     INTER_BIT   	= 0x0001;
	public	static final    int     FILTER_BIT  	= 0x0002;
	public	static final    int     LOCKED_BIT  	= 0x0004;
	public	static final    int     TESS_BIT    	= 0x0008;
	public	static final    int		VISITED_BIT		= 0x0010;
	public	static final	int     TERMINATOR_BIT	= 0x0020;
	public	static final	int     HARDTERM_BIT	= 0x0040;
	public	static final	int     SHADOW_BIT		= 0x0080;

	/**
	 * This factor is a blender between original position and th�orical position.
	 * So the value is between [0,1].
	 * 0 means no effect.
	 * 1 means ensure RestLen between this nodes but should cause bouncing.
	 */
//	protected   static final    float   s_tens      = 0.75f;

	protected   RelaxableLink           m_link;
	protected   NodeRelaxData           m_from;
	protected   NodeRelaxData           m_to;
	protected   float                   m_length;
	protected   float                   m_stiffness;
	protected   float                   m_width;
	protected   float                   m_oldLen;
	protected   float                   m_dir;
	protected   String                  m_label;
	protected   boolean                 m_isRelaxable;
	protected   EZFlags                 m_flags = new EZFlags();

	/**
	 * Debug only!
	 */
	public LinkRelaxData( float length, float stiffness, float width, boolean isRelaxable, int flags )
	{
		super( "fake" );

		m_length		= length;
		m_stiffness		= stiffness;
		m_width			= width;
		m_isRelaxable   = isRelaxable;
		m_flags			= new EZFlags( flags );
	}

	public LinkRelaxData( RelaxableLink link )
	{
		super( link.getLabel());

		m_link          = link;
		m_from          = link.getFromNode().getRelaxData();
		m_to            = link.getToNode().getRelaxData();
		m_length		= link.getLength();
		m_stiffness		= link.getStiffness();
		m_width			= link.getWidth();
		m_oldLen        = m_length;
		m_label         = link.getLabel();
		m_isRelaxable   = link.isRelaxable();
	}

	public LinkRelaxData( NodeRelaxData from, NodeRelaxData to )
	{
		super( "fake" );

		m_from          = from;
		m_to            = to;
		m_width			= 1;
		m_flags.enable( TESS_BIT );
	}

	public Localisable getFrom()
	{
		return m_from;
	}

	public Localisable getTo()
	{
		return m_to;
	}

	public RelaxableLink getSource()
	{
		return m_link;
	}

	protected void init()
	{
		m_from  = m_link.getFromNode().getRelaxData();
		m_to    = m_link.getToNode().getRelaxData();
	}

	protected NodeRelaxData getOtherNodeData( NodeRelaxData nodeDat )
	{
		return m_from == nodeDat ? m_to : m_from;
	}

	protected boolean isRelaxable()
	{
		return  m_isRelaxable && m_from.isRelaxable() && m_to.isRelaxable() &&( m_from.m_inertia < 1.f || m_to.m_inertia < 1.f )&& m_flags.isDisabled( FILTER_BIT );
	}

	protected boolean isReady()
	{
		return m_from.m_pos != null && m_to.m_pos != null;
	}

	protected boolean isVisible()
	{
		return m_from.m_pos != null && m_to.m_pos != null && m_flags.isDisabled( FILTER_BIT );
	}

	protected void updateFlags()
	{
//		m_flags.setEnabled( TERMINATOR_BIT, m_from.isTerminator() || m_to.isTerminator());
	}

	protected boolean isTerminator()
	{
		return ( m_from.m_flags.isEnabled( NodeRelaxData.TERMINATOR_BIT ) || m_to.m_flags.isEnabled( NodeRelaxData.TERMINATOR_BIT ))&& m_flags.isDisabled( SHADOW_BIT )&& m_flags.isDisabled( FILTER_BIT );
//		return m_flags.isEnabled( TERMINATOR_BIT );
//		return m_from.isTerminator() || m_to.isTerminator();
	}

	protected void filter()
	{
		m_flags.enable( FILTER_BIT );
		m_from.updateFlags();
		m_to.updateFlags();
	}

	public NodeRelaxData getLinkedNodeData( LinkRelaxData data )
	{
		NodeRelaxData   from    = data.m_from,
						to      = data.m_to;

		if ( m_from == from || m_from == to )   return m_from;
		else if ( from == m_to || from == to )  return from;
		else return null;
	}

	public float getLinkDir( NodeRelaxData nodeDat )
	{
		float   dir = m_dir;

		if ( nodeDat != m_from ) dir  = dir > EZMath.PI ? dir - EZMath.PI : dir + EZMath.PI;

		return dir;
	}

	public void updateLink( Bounds2D mapBnds, Bounds2D winBnds )
	{
		m_link.setWidth( mapBnds.project( m_width, winBnds ));
	}

	public void setWidth( float width )
	{
		m_width		= width;
	}

	public String toString()
	{
		return "(" + m_label + ") l=" + m_flags.isEnabled( LOCKED_BIT ) + ", t=" + isTerminator() + ", f=" + m_flags.isEnabled( FILTER_BIT ) + ", h=" + m_flags.isEnabled( SHADOW_BIT ) + " f=" + m_from.m_pos + " t=" + m_to.m_pos;
	}

//	public boolean isDead()
//	{
//		return m_flags.isEnabled( FILTER_BIT );
//	}

	protected boolean contains( Vertex pos )
	{
		Vertex  fromPos = m_from.m_pos,
				toPos   = m_to.m_pos,
				dir		= m_from.m_pos.sub( m_to.m_pos );
		float   //len     = dir.length(),
				width   = m_width;

		dir.turn().resize( width );

		Polygon		poly	= new Polygon();

		poly.addPoint((int)( fromPos.x + dir.x ), (int)( fromPos.y + dir.y ));
		poly.addPoint((int)( fromPos.x - dir.x ), (int)( fromPos.y - dir.y ));
		poly.addPoint((int)( toPos.x - dir.x ), (int)( toPos.y - dir.y ));
		poly.addPoint((int)( toPos.x + dir.x ), (int)( toPos.y + dir.y ));

		return poly.contains( pos.x, pos.y );
	}

	public void paint( Graphics g, int id, EZFlags flags, Bounds2D mapBnds, Bounds2D winBnds, boolean isFilled )
	{
		Vertex  fromPos = mapBnds.project( m_from.m_pos, winBnds ),
				toPos   = mapBnds.project( m_to.m_pos, winBnds ),
				dir		= m_from.m_pos.sub( m_to.m_pos );
		float   len     = dir.length(),
				width   = mapBnds.project( m_width, winBnds );

		if ( flags.isEnabled( GfxTester.LINK_BIT ) && !(/* flags.isEnabled( CrossMgr.FILTER_BIT )&&*/ m_flags.isEnabled( FILTER_BIT )))
		{
			float   dLen    = Math.abs( len - m_length )/ m_length;

			if ( flags.isEnabled( GfxTester.INTER_BIT )&& m_flags.isEnabled( INTER_BIT ))
			{
				g.setColor( TesterUI.INTER_COL );
			}
			else if ( m_flags.isEnabled( LOCKED_BIT ))
			{
				g.setColor( TesterUI.LOCK_COL );
			}
			else if ( m_flags.isEnabled( TESS_BIT ))
			{
				g.setColor( TesterUI.TESS_COL );
			}
			else
			{
				if ( dLen < .2f )
				{
					g.setColor( Color.yellow );
				}
				else if ( len < m_length )
				{
					g.setColor( new Color( 1.f - len / m_length, 1.f, 1.f - len / m_length ));
				}
				else
				{
					g.setColor( new Color( 0.f, m_length / len, 0.f ));
				}
//				int	red	= (int)( 1000.f * m_lenDif);
//
//				g.setColor( new Color( red > 255 ? 255 : 0, 0, 0 ));
			}

			dir.turn().resize( width );

			Polygon		poly	= new Polygon();

			poly.addPoint((int)( fromPos.x + dir.x ), (int)( fromPos.y + dir.y ));
			poly.addPoint((int)( fromPos.x - dir.x ), (int)( fromPos.y - dir.y ));
			poly.addPoint((int)( toPos.x - dir.x ), (int)( toPos.y - dir.y ));
			poly.addPoint((int)( toPos.x + dir.x ), (int)( toPos.y + dir.y ));

			if ( isFilled )	g.fillPolygon( poly );
			else			g.drawPolygon( poly );
		}

		boolean hasLinkId   = flags.isEnabled( GfxTester.LINKID_BIT ),
				hasExt      = flags.isEnabled( GfxTester.LINKEXT_BIT );

		if ( hasLinkId || hasExt )
		{
			Vertex  center  = Vertex.center( fromPos, toPos );
			Color   color   = null;
			String  value   = "";

			if ( hasLinkId )
			{
				color   = TesterUI.LINKID_COL;
				value   = String.valueOf( id );
			}

			if ( hasExt )
			{
				color   = TesterUI.LINKEXT_COL;
				value  += hasLinkId ?
							' ' + m_label:
							m_label + " t=" + isTerminator();
			}

			g.setColor( color );
			g.drawString( value, (int)center.x, (int)center.y );
		}
	}

	private boolean hasSpace( NodeRelaxData	ANode, Vertex AB, float cosMax )
	{
		LinkRelaxData[]	links   	= ANode.m_links;
		LinkRelaxData	link;
		int				i, n    	= links.length;
		Vertex			C, A		= ANode.m_pos;
		float			acx, acy,
						nac, nab	= AB.sqrLength(),
						dp, cos;

		for ( i = 0; i < n; i ++ )
		{
			link    = links[i];

			if ( link.m_flags.isDisabled( FILTER_BIT )&& link != this )
			{
				C   = link.getOtherNodeData( ANode ).m_pos;
				acx = C.x - A.x;
				acy = C.y - A.y;
				dp  = AB.x * acx + AB.y * acy;

				if ( dp > 0 )
				{
					dp	*= dp;						// dp	= (AB x AC)2
					nac = acx * acx + acy * acy;	// nac	= |AC|2
					cos = dp /( nab * nac );		// cos	= cos(AB,AC)2

					if ( cos > cosMax )
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	// Works only for angles < 90�
	public boolean hasSpace( float angle )
	{
		float			cosMax	= (float)Math.cos( angle );
		Vertex      	AB      = m_to.m_pos.sub( m_from.m_pos );

		cosMax	*= cosMax;

		return hasSpace( m_from, AB, cosMax ) && hasSpace( m_to, AB.flip(), cosMax );
	}

	private		int						m_lenCnt		= 8;
	private		int						m_lenPos		= 0;
	//private		float					m_lenPrv		= 0;
	private		float					m_lenAvg		= 0;
	//private		float					m_lenDif		= 0;
	private		float					m_lenSum		= 0;
	private		float[]					m_lenTab		= new float[m_lenCnt];

	protected float getLengthVar( float len )
	{
		float	var	= Math.abs( len - m_oldLen );

		m_lenSum	+= var - m_lenTab[m_lenPos];
		m_lenTab[m_lenPos]	= var;
		m_lenPos	= ( m_lenPos + 1 )% m_lenCnt;
		m_oldLen	= len;

		float	avg	= m_lenSum / m_lenCnt,
				dif	= Math.abs( avg - m_lenAvg );

		m_lenAvg	= avg;
		//m_lenDif	= dif;

		return dif;//m_lenSum / m_lenCnt;
	}

	public static void main( String[] args )
	{
		NodeRelaxData	A		= new NodeRelaxData( new Vertex ( 0, 0 ), 0, 0, 0, 0, false, false, null ),
						B		= new NodeRelaxData( new Vertex ( 10, 0 ), 0, 0, 0, 0, false, false, null ),
						C		= new NodeRelaxData( null, 0, 0, 0, 0, false, false, null );
		LinkRelaxData	AB	= new LinkRelaxData( A, B ),
//						AC	= new LinkRelaxData( A, C ),
						BC	= new LinkRelaxData( B, C );
		float			a, da	= 360 / 100,
						angle	= 45;

//		A.m_links	= new LinkRelaxData[]{ AB, AC };
		B.m_links	= new LinkRelaxData[]{ AB, BC };

		for ( a = 0; a < 360; a += da )
		{
//			C.m_pos	= Vertex.createPolar( 5, a * EZMath.PI / 180 );
			C.m_pos	= Vertex.createPolar( 5, a * EZMath.PI / 180 );
			C.m_pos.addThis( B.m_pos );
			System.out.println( "hasSpace( " + a + " ) = " + AB.hasSpace( angle * EZMath.PI / 180 ));
		}
	}
}
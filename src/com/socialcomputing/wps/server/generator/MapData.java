package com.socialcomputing.wps.server.generator;

import java.io.*;
import java.awt.*;
import java.util.*;

import org.jdom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.geom.relax.*;
import com.socialcomputing.utils.*;
import com.socialcomputing.utils.math.*;
//import com.socialcomputing.utils.geom.*;
import com.socialcomputing.utils.geom.relax.RelaxParams;

//import com.socialcomputing.wps.server.plandictionary.*;

/**
 * <p>Title: MapData</p>
 * <p>Description: This holds the Map relaxation parameters.<br>
 * It also converts values from the analysis (ProtoAttribute&AttributeLink) to the generator (NodeMapData&LinkMapData).
 * Parameters can be read or written using XML.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class MapData implements Serializable
{
	static final long serialVersionUID  = 7086160279928299040L;

	public static final Logger log = LoggerFactory.getLogger(MapData.class);
	
	/**
	 * Index of Nodes size [min,max] in the bounds array.
	 */
	protected static final int		N_SIZE_BND			= 0;

	/**
	 * Index of Link width [min,max] in the bounds array.
	 */
	protected static final int		L_WIDTH_BND			= 1;

	/**
	 * Index of external Link stiffness [min,max] in the bounds array.
	 */
	protected static final int		L_STIFF_BND			= 2;

	/**
	 * Index of mixed Link stiffness [min,max] in the bounds array.
	 */
	protected static final int		LM_STIFF_BND		= 3;

	/**
	 * Index of base Link stiffness [min,max] in the bounds array.
	 */
	protected static final int		LB_STIFF_BND		= 4;

	/**
	 * Index of external Link length [min,max] in the bounds array.
	 */
	protected static final int		L_LENGTH_BND		= 5;

	/**
	 * Index of mixed Link length [min,max] in the bounds array.
	 */
	protected static final int		LM_LENGTH_BND		= 6;

	/**
	 * Index of base Link length [min,max] in the bounds array.
	 */
	protected static final int		LB_LENGTH_BND		= 7;

	/**
	 * A map table to associate stage names with their table index.
	 */
	private static final HashMap	s_stageNames;

	static
	{
		s_stageNames	= new HashMap( 4 );
		s_stageNames.put( "baseRelax", new Integer( 0 ));
		s_stageNames.put( "baseFilter", new Integer( 1 ));
		s_stageNames.put( "extRelax", new Integer( 2 ));
		s_stageNames.put( "extFilter", new Integer( 3 ));
		s_stageNames.put( "extLast", new Integer( 4 ));
	}

	/**
	 * Name of each bounds. This can be used for debug purpose while displaying histograms.
	 */
	protected static final String[]	s_bndNames			= new String[]
	{
		"Node size",
		"Link width",
		"Link stiffness",
		"Mixed Link stiffness",
		"Base Link stiffness",
		"Link length",
		"Mixed Link length",
		"Base Link length",
	};

	/**
	 * An array of relax parameters. For each stage a RelaxParams is associated.
	 */
	protected	RelaxParams[]       m_relaxParams;

	/**
	 * The window margin around the map.
	 */
	protected	int 				m_margin;

	/**
	 * Minimum number of base links per base attribute.
	 */
	protected	float 				m_baseLinkMin;

	/**
	 * Minimum number of mixed links per external attribute.
	 */
	protected	float 				m_mixedLinkMin;

	/**
	 * Minimum number of ext links per external attribute.
	 */
	protected	float 				m_extLinkMin;

	/**
	 * Maximum number of base links per base attribute.
	 */
	protected	float 				m_baseLinkMax;

	/**
	 * Maximum number of mixed links per external attribute.
	 */
	protected	float 				m_mixedLinkMax;

	/**
	 * Maximum number of ext links per external attribute.
	 */
	protected	float 				m_extLinkMax;

	/**
	 * Proportion of best base links to keep.
	 * Number of Links to keep = m_baseLinkKeep * base Attributes count
	 */
	protected	float 				m_baseLinkKeep;

	/**
	 * Proportion of best mixed links to keep.
	 * Number of Links to keep = m_mixedLinkKeep * ext Attributes count
	 */
	protected	float 				m_mixedLinkKeep;

	/**
	 * Proportion of best ext links to keep.
	 * Number of Links to keep = m_extLinkKeep * ext Attributes count
	 */
	protected	float 				m_extLinkKeep;

	/**
	 * Window bounds in pixels after having retrieved the margin around.
	 * This is used to transform the boundless Map coordinates to the Applet window coordinates.
	 */
	public		Bounds2D  			m_winBnds;

	/**
	 * An array containing the [min,max] values of the analysis parameters.
	 */
	protected	Bounds[]			m_bounds;

	/**
	 * A reference to the Mapper that use this to create a Map.
	 */
	protected	Mapper    			m_mapper;

	/**
	 * Creates a new MapData and initialize it with its default values.
	 * @param planType	An id from the Analysis to adjust the default values to the type of Plan.
	 * This is not used anymore as one default value works (i.e don't crash) for all maps.
	 */
	public MapData( int planType )
	{
		reset( planType );
	}

	/**
	 * Reset the default values of this.
	 * @param planType	An id from the Analysis to adjust the default values to the type of Plan.
	 * This is not used anymore as one default value works (i.e don't crash) for all maps.
	 */
	public void reset( int planType )
	{
		m_baseLinkMin	= 1.f;
		m_mixedLinkMin	= 1.f;
		m_extLinkMin	= 1.f;
		m_baseLinkMax	= 3.f;
		m_mixedLinkMax	= 3.f;
		m_extLinkMax	= 3.f;
		m_baseLinkKeep	= 1.f;
		m_mixedLinkKeep	= 1.f;
		m_extLinkKeep	= 1.f;

		m_bounds	= new Bounds[]
		{
			new Bounds( 2.5f, 3.5f ),				// nodeSize
			new Bounds( .5f, 1.5f ),				// linkWidth
			new Bounds( .5f, 1.f ),					// linkExtStiff
			new Bounds( .5f, 1.f ),					// linkMixedStiff
			new Bounds( .5f, 1.f ),					// linkBaseStiff
			new Bounds( 8.f, 10.f ),				// linkExtLen
			new Bounds( 8.f, 10.f ),				// linkMixedLen
			new Bounds( 8.f, 10.f ),				// linkBaseLen
		};

		m_relaxParams	= new RelaxParams[]
		{	//				 scl	nRep	rMix	lTen	tMix	lRep	lRMix	lRot	cRep	iters	trsh	lastIt	lastTrsh
			// baseRelax
			new RelaxParams( .25f,	.25f,	.75f,	.25f,	.25f,	.55f,	.75f,	0.f,	0.f,	30, 	.002f,	50,		.002f ),
			// baseFilter
			new RelaxParams( .1f,	.5f,	1.f,	.25f,	.75f,	1.f,	1.f,	0.f,	0.f,	100, 	.001f ),
			// extRelax
			new RelaxParams( .25f,	.25f,	.5f,	.1f,	.1f,	0.f,	.5f,	0.f,	0.f,	25,		.01f,	50,	.002f ),
			// extFilter
			new RelaxParams( .2f,	.25f,	.75f,	.25f, 	.5f,	.5f,	.75f,	0.f,	0.f,	200,	.002f ),
			// extLast
			new RelaxParams( .1f,	.5f,	1.f,	.1f, 	.75f,	1.f,	1.f,	0.f,	0.f,	100,	.001f ),
		};
	}

	/**
	 * Initialize the Mapper and Window bounds fields.
	 * @param mapper A reference to the Mapper that use this to create a Map.
	 */
	public void init( Mapper mapper )
	{
		Point   dim = mapper.m_protoPlan.m_env.m_transfo.getCart();

		m_winBnds   = new Bounds2D( 0, dim.x, 0, dim.y );
		m_mapper	= mapper;
	}

	/**
	 * Gets the graphical length of a link using its analysis length.
	 * The length is normalized using the LENGTH bounds specified in the dictionnary.
	 * @param link	A graphical Link holding it's analysis �quivalent.
	 * @return	The normalized graphical length of this link.
	 */
	protected float getLinkLen( LinkMapData link )
	{
		int	offset	= getLinkOffset( link );

		return project( link.m_link.m_length, ProtoPlan.L_LENGTH_BND + offset, L_LENGTH_BND + offset );
	}

	/**
	 * Gets the normalized length of a link using its analysis length.
	 * @param link	A graphical Link holding it's analysis �quivalent.
	 * @return	The length of this link normalized to [0,1[ .
	 */
	protected float getLinkNormLen( LinkMapData link )
	{
		int	offset	= getLinkOffset( link );

		return m_mapper.m_protoPlan.m_bounds[ProtoPlan.L_LENGTH_BND + offset].normalize( link.m_link.m_length );
	}

	/**
	 * Gets the graphical mass of a link using its analysis width.
	 * The mass is normalized using the WIDTH bounds specified in the dictionnary.
	 * @param link	A graphical Link holding it's analysis �quivalent.
	 * @return	The Mass of this link normalized to [.5,1[ .
	 */
	protected float getLinkMass( LinkMapData link )
	{
		int		offset	= getLinkOffset( link );
		float	wNrm	= map( link.m_link.m_size, ProtoPlan.L_WIDTH_BND + offset, .5f, 1.f );

		return wNrm;
	}

	/**
	 * Gets the graphical mass of a node using its analysis weight.
	 * The mass is normalized using the WEIGHT bounds specified in the dictionnary.
	 * @param node	A graphical node holding it's analysis �quivalent.
	 * @return	The Mass of this node normalized to [.5,2[ .
	 */
	protected float getNodeMass( NodeMapData node )
	{
		int	offset	= getNodeOffset( node );

		return map( node.m_att.m_weight, ProtoPlan.A_WEIGHT_BND + offset, .5f, 2.f );
	}

	/**
	 * Gets the graphical width of a link using its analysis width.
	 * The mass is normalized using the WIDTH bounds specified in the dictionnary.
	 * @param link	A graphical Link holding it's analysis �quivalent.
	 * @return	The normalized graphical width of this link.
	 */
	protected float getLinkWidth( LinkMapData link )
	{
		int	offset	= getLinkOffset( link );

		return project( link.m_link.m_size, ProtoPlan.L_WIDTH_BND + offset, L_WIDTH_BND );
	}

	/**
	 * Gets the graphical stiffness of a link using its analysis width.
	 * The stiffness is normalized using the WIDTH bounds specified in the dictionnary.
	 * @param link	A graphical Link holding it's analysis �quivalent.
	 * @return	The normalized graphical stiffness of this link.
	 */
	protected float getLinkStiffness( LinkMapData link )
	{
		int	offset	= getLinkOffset( link );

		return project( link.m_link.m_size, ProtoPlan.L_WIDTH_BND + offset, L_STIFF_BND + offset );
	}

	/**
	 * Gets the graphical size of a node using its analysis size.
	 * The size is normalized using the SIZE bounds specified in the dictionnary.
	 * @param node	A graphical node holding it's analysis �quivalent.
	 * @return	The normalized graphical size of this node.
	 */
	protected float getNodeSize( NodeMapData node )
	{
		return project( node.m_att.m_size, ProtoPlan.A_ALLSIZE_BND, N_SIZE_BND );
	}

	/**
	 * Reads a MapData using data from an XML element.
	 * Default values are used when values are missing in the XML file.
	 * @param elem	An XML JDOM element.
	 * @param type	Type of Plan to set default values. This is not used anymore.
	 * @return		A new MapData initialized with values from elem.
	 * @throws JDOMException
	 */
	public static MapData readObject( Element elem, int type )
	throws JDOMException
	{
		MapData	mapDat = new MapData( type );	// init to default values

		mapDat.readObject( elem );

		return mapDat;
	}

	/**
	 * Reads a this using data from an XML element.
	 * Default values are used when values are missing in the XML file.
	 * @param elem	An XML JDOM element.
	 * @return		A new MapData initialized with values from elem.
	 * @throws JDOMException
	 */
	public void readObject( Element elem )
	throws JDOMException
	{
		if ( elem != null )
		{
			//Bounds[]	bounds	= m_bounds;
			//String		valueStr;

			try
			{
				m_bounds[LB_LENGTH_BND]	= EZDom.readDomBounds( elem, "linkBaseLen" );
				m_bounds[LM_LENGTH_BND]	= EZDom.readDomBounds( elem, "linkMixedLen" );
				m_bounds[L_LENGTH_BND]	= EZDom.readDomBounds( elem, "linkExtLen" );
				m_bounds[N_SIZE_BND]	= EZDom.readDomBounds( elem, "nodeSize" );
				m_bounds[L_WIDTH_BND]	= EZDom.readDomBounds( elem, "linkWidth" );
				m_bounds[LB_STIFF_BND]	= EZDom.readDomBounds( elem, "linkBaseStiff" );
				m_bounds[LM_STIFF_BND]	= EZDom.readDomBounds( elem, "linkMixedStiff" );
				m_bounds[L_STIFF_BND]	= EZDom.readDomBounds( elem, "linkExtStiff" );

				m_margin 		= EZDom.readInt( elem, "margin" );

				m_baseLinkMin	= EZDom.readFloat( elem, "baseLinkMin" );
				m_mixedLinkMin	= EZDom.readFloat( elem, "mixedLinkMin" );
				m_extLinkMin	= EZDom.readFloat( elem, "extLinkMin" );

				m_baseLinkMax	= EZDom.readFloat( elem, "baseLinkMax" );
				m_mixedLinkMax	= EZDom.readFloat( elem, "mixedLinkMax" );
				m_extLinkMax	= EZDom.readFloat( elem, "extLinkMax" );

				m_baseLinkKeep	= EZDom.readFloat( elem, "baseLinkKeep" );
				m_mixedLinkKeep	= EZDom.readFloat( elem, "mixedLinkKeep" );
				m_extLinkKeep	= EZDom.readFloat( elem, "extLinkKeep" );
			}
			catch ( NumberFormatException e )
			{
				log.info("error parsing Number in MapData.readObject(): {}", elem );
			}

			java.util.List  stageLst    = elem.getChildren( "stage" );
			ListIterator    it          = stageLst.listIterator();
			RelaxParams		relaxPrm;

			while ( it.hasNext())
			{
				relaxPrm	= RelaxParams.readObject((Element)it.next());

				if ( relaxPrm != null )	m_relaxParams[getStageId( relaxPrm )]	= relaxPrm;
			}
		}
	}

	/**
	 * Writes this to an XML Element.
	 * @return	A new XML JDOM Element describing this.
	 */
	public Element writeObject()
	{
		Element	root	= new Element( "relax" );

		EZDom.writeDomBounds( root, "linkBaseLen", m_bounds[LB_LENGTH_BND] );
		EZDom.writeDomBounds( root, "linkMixedLen", m_bounds[LM_LENGTH_BND] );
		EZDom.writeDomBounds( root, "linkExtLen", m_bounds[L_LENGTH_BND] );
		EZDom.writeDomBounds( root, "nodeSize", m_bounds[N_SIZE_BND] );
		EZDom.writeDomBounds( root, "linkWidth", m_bounds[L_WIDTH_BND] );
		EZDom.writeDomBounds( root, "linkBaseStiff", m_bounds[LB_STIFF_BND] );
		EZDom.writeDomBounds( root, "linkMixedStiff", m_bounds[LM_STIFF_BND] );
		EZDom.writeDomBounds( root, "linkExtStiff", m_bounds[L_STIFF_BND] );

		root.setAttribute( "margin",  String.valueOf( m_margin ));

		root.setAttribute( "baseLinkMin",  String.valueOf( m_baseLinkMin ));
		root.setAttribute( "mixedLinkMin",  String.valueOf( m_mixedLinkMin ));
		root.setAttribute( "extLinkMin",  String.valueOf( m_extLinkMin ));

		root.setAttribute( "baseLinkMax",  String.valueOf( m_baseLinkMax ));
		root.setAttribute( "mixedLinkMax",  String.valueOf( m_mixedLinkMax ));
		root.setAttribute( "extLinkMax",  String.valueOf( m_extLinkMax ));

		root.setAttribute( "baseLinkKeep",  String.valueOf( m_baseLinkKeep ));
		root.setAttribute( "mixedLinkKeep",  String.valueOf( m_mixedLinkKeep ));
		root.setAttribute( "extLinkKeep",  String.valueOf( m_extLinkKeep ));

		//java.util.List  stageLst    = root.getChildren( "stage" );
		//ListIterator    it          = stageLst.listIterator();
		//RelaxParams		relaxPrm;

		int	i, n	= m_relaxParams.length;

		for ( i = 0; i < n; i ++ )
		{
			m_relaxParams[i].writeObject( root );
		}

		return root;
	}

	/**
	 * Gets the stage id of a RelaxParams.
	 * This is used when a RelaxParams is read to know where to put it in the RelaxParams table.
	 * @param relaxPrm	A newly read RelaxParams.
	 * @return	The index matching relaxPrm type.
	 */
	private static int getStageId( RelaxParams relaxPrm )
	{
		return ((Integer)s_stageNames.get( relaxPrm.m_name )).intValue();
	}

	/**
	 * Gets the offset of a link type in the link bounds table.
	 * In the Bounds table bounds have the order : base, mixed, ext.
	 * So knowing the base index, the mixed is base + 1, the ext is base + 2.
	 * @param link	The link to know the offset.
	 * @return	An offset in [0,2] matching link type (0:base, 1:mixed, 2:ext).
	 */
	private int getLinkOffset( LinkMapData link )
	{
		return link.m_isBase ? 2 :( link.m_isMixed ? 1 : 0 );
	}

	/**
	 * Gets the offset of a node type in the node bounds table.
	 * In the Bounds table bounds have the order : base, ext.
	 * So knowing the base index, the ext is base + 1.
	 * @param node	The node to know the offset.
	 * @return	An offset in [0,1] matching node type (0:base, 1:ext).
	 */
	private int getNodeOffset( NodeMapData node )
	{
		return node.m_isBase ? 1 : 0;
	}

	/**
	 * Maps a value using source and destination bounds.
	 * Knowing the bounds of a value, its normalized value can be evaluated.
	 * Then this normalized value is mapped to another interval.
	 * @param x		Value to transform.
	 * @param from	Index of the Bounds of this value. So from.min <= x <= from.max
	 * @param to	Index of the Bounds to map the value to. So to.min <= project( x ) <= to.max
	 * @return		The value of x mapped to the 'to' interval.
	 */
	protected float project( float x, int from, int to )
	{
		return m_mapper.m_protoPlan.m_bounds[from].project( x, m_bounds[to] );
	}

	/**
	 * Normalize a value using its bounds.
	 * Knowing the bounds of a value, its normalized value can be evaluated.
	 * @param x		Value to transform.
	 * @param from	Index of the Bounds of this value. So from.min <= x <= from.max
	 * @return		The value of x normalized in [0,1[.
	 */
	protected float normalize( float x, int from )
	{
		return m_mapper.m_protoPlan.m_bounds[from].normalize( x );
	}

	/**
	 * Maps a value using source and destination bounds.
	 * Knowing the bounds of a value, its normalized value can be evaluated.
	 * Then this normalized value is mapped to another interval defined by its begin and end value.
	 * @param x		Value to transform.
	 * @param from	Index of the Bounds of this value. So from.min <= x <= from.max
	 * @param beg	Begin value if x = from.min. So beg <= map( x )
	 * @param end	End value if x = to.min. So map( x ) < end
	 * @return		The value of x mapped to the [beg,end[ interval.
	 */
	protected float map( float x, int from, float beg, float end )
	{
		return EZMath.interLin( m_mapper.m_protoPlan.m_bounds[from].normalize( x ), beg, end );
	}

	/**
	 * Lineary interpolates a value using an interval from the bounds table.
	 * x' = (1-x) * to.beg + x * to.end
	 * @param x		Interpolator in [0,1]
	 * @param to	Index of the Bounds to interpolate in.
	 * @return		A value inside the 'to' interval corresponding to x in the [0,1] interval.
	 */
	protected float interlin( float x, int to )
	{
		return EZMath.interLin( x, m_bounds[to].m_min, m_bounds[to].m_max );
	}

	public String toString()
	{
		String	msg		= "";
		int		i, n	= s_bndNames.length;

		for ( i = 0; i < n; i ++ )
		{
			msg += s_bndNames[i] + " = " + m_bounds[i] + '\n';
		}

		msg += "m_baseLinkMin = " + m_baseLinkMin + '\n';
		msg += "m_mixedLinkMin = " + m_mixedLinkMin + '\n';
		msg += "m_extLinkMin = " + m_extLinkMin + '\n';
		msg += "m_baseLinkKeep = " + m_baseLinkKeep + '\n';
		msg += "m_mixedLinkKeep = " + m_mixedLinkKeep + '\n';
		msg += "m_extLinkKeep = " + m_extLinkKeep + '\n';
		msg += "m_winBnds = " + m_winBnds + '\n';
		msg += "m_margin = " + m_margin + '\n';

		for ( i = 0; i < 4; i ++ )
		{
			msg += "\n[" + i + "] " + m_relaxParams[i] + '\n';
		}

		return msg;
	}
}

package com.socialcomputing.wps.server.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.utils.math.EZMath;

/**
 * <p>Title: ProtoAttribute</p>
 * <p>Description: An Attribute(place) linked to others by Links(streets).<br>
 * Links are stored in an array.
 * It has a weight, a size and an ID given by the analysis.
 * Some attributes are clusterized so they have a parent Attribute.
 * The other are clusters so they have children Attributes.
 * There is 2 kind of Attributes :
 * <ul>
 * <li>base Attribute : It is in the base of the Map.</li>
 * <li>ext Attribute : It is outside of the base.</li>
 * </ul></p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class ProtoAttribute implements MapableAtt
{
	/**
	 * Identifier of this in DB.
	 */
	public String       	m_strId;

	/**
	 * Attribute size (diameter) as defined by the analysis.
	 */
	public int              m_size;

	/**
	 * Attribute weight (importance) as defined by the analysis.
	 */
	public int              m_weight;

	/**
	 * Links between this attribute and others.
	 */
	public ArrayList        m_links;

	/**
	 * An array holding this links sorted from the 'best' to the 'worst'.
	 * This order is defined by the s_linkCmp comparator.
	 */
	protected	AttributeLink[]	m_sortLinks;

	/**
	 * clusterized children of this or null if this is not a cluster.
	 */
	public ArrayList        m_children;

	/**
	 * Parent Attribute of this is it is clusterized or null if this is cluster.
	 */
	public ProtoAttribute   m_parent;

	/**
	 * A reference to a map graphical data.
	 * Used to speed up lookup between analysis data and generator data.
	 */
	private		NodeMapData	m_mapData;

	/**
	 * True if this is a reference Attribute.
	 */
	protected	boolean		m_isRef;

	/**
	 * True if this is in the base.
	 */
	protected	boolean		m_isBase;

	/**
	 * Number of base/ext links in this.
	 * Attributs can't have base AND ext links at the same time.
	 * So this holds one this the 2 possible link type count.
	 */
	protected	int			m_linkCnt			= 0;

	/**
	 * Minimum number of base/ext links allowed.
	 * Attributs can't have base AND ext links at the same time.
	 * So this holds one this the 2 possible link type minimum count.
	 */
	protected	int			m_minLinkCnt		= 1;

	/**
	 * Maximum number of base/ext links allowed.
	 * Attributs can't have base AND ext links at the same time.
	 * So this holds one this the 2 possible link type maximum count.
	 */
	protected	int			m_maxLinkCnt		= 4;

	/**
	 * Minimum number of mixed links allowed.
	 */
	protected	int			m_minMixedLinkCnt	= 1;

	/**
	 * Maximum number of mixed links allowed.
	 */
	protected	int			m_maxMixedLinkCnt	= 4;

	/**
	 * Current number of non-filtered base/ext links.
	 * Attributs can't have base AND ext links at the same time.
	 */
	protected	int			m_activeLinkCnt		= 0;

	/**
	 * Current number of non-filtered mixed links.
	 */
	protected	int			m_activeMixedCnt	= 0;

	/**
	 * See espinat@mapstan.com
	 */
	public int          	m_num       = -1;

	/**
	 * See espinat@mapstan.com
	 */
	public int              m_filteredLinkCnt   = 0;

	/**
	 * See espinat@mapstan.com
	 */
	public RecommendationGroup[]  m_recomGroups = null;

	/**
	 * See espinat@mapstan.com
	 */
	public ProtoAttribute( String strId, boolean isRef, boolean isBase, int size, int weight, int num )
	{
		m_strId     = strId;
		m_isRef     = isRef;
		m_isBase    = isBase;
		m_size      = size;
		m_weight    = weight;
		m_links     = new ArrayList();
		m_num       = num;
	}

	/**
	 * Sets Graphical Map data for this.
	 * @param data	A NodeMapData corresponding to this.
	 */
	public void setMapData( NodeMapData data )
	{
		m_mapData    = data;
	}

	/**
	 * Gets the associated Map graphical data.
	 * @return	The matching NodeMapData
	 */
	public NodeMapData getMapData()
	{
		return m_mapData;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public void addLink( AttributeLink link )
	{
		m_links.add( link );
	}

	/**
	 * See espinat@mapstan.com
	 */
	public void removeLink( AttributeLink link )
	{
		int i, lCnt = m_links.size();

		for ( i = 0; i < lCnt && m_links.get( i )!= link; i ++ );

		if ( i < lCnt ) m_links.remove( i );
	}

	/**
	 * See espinat@mapstan.com
	 */
	public void removeAllLinks( )
	{
		m_links.clear();
	}

	/**
	 * See espinat@mapstan.com
	 */
	public ListIterator getLinkIterrator()
	{
		return m_links.listIterator();
	}

	/**
	 * See espinat@mapstan.com
	 */
	public int getLinkCount()
	{
		return m_links.size() - m_filteredLinkCnt;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public int getBaseLinkCount()
	{
		AttributeLink   link;
		ListIterator    it  = m_links.listIterator();
		int             cnt = 0;


		while ( it.hasNext())
		{
			link = (AttributeLink)it.next();

			if ( link.isLinkBetweenBase() && !link.isFiltered())    cnt ++;
		}

		return cnt;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public int getRelatedBaseLinkCount()
	{
		AttributeLink   link;
		ListIterator    it  = m_links.listIterator();
		int             cnt = 0;


		while ( it.hasNext())
		{
			link = (AttributeLink)it.next();

			if ( link.isLinkRelatedToBase() && !link.isFiltered())    cnt ++;
		}

		return cnt;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public Collection getRecommendations(int recomType)
	{
		Collection ret=null;

		if (m_recomGroups!=null && ((m_recomGroups[recomType])!=null) )
		   ret=m_recomGroups[recomType].m_recommendations;

		return ret;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public TreeSet getAllRecommendations(int recomType)
	{
		TreeSet set=new TreeSet();
		Collection coll;
		ProtoAttribute child=null;

		if ((coll=getRecommendations(recomType))!=null)
		   set.addAll(coll);

		if (m_children!=null)
		   {
				Iterator it=m_children.iterator();

				while (it.hasNext())
					  {
					  child=(ProtoAttribute)it.next();
					  if ((coll=child.getRecommendations(recomType))!=null)
						 set.addAll(coll);
					  }
		   }
		return set;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public int getLinkCountNotInBase()
	{
		AttributeLink   link;
		ListIterator    it  = m_links.listIterator();
		int             cnt = 0;


		while ( it.hasNext())
		{
			link = (AttributeLink)it.next();

			if ( !link.isLinkRelatedToBase() && !link.isFiltered())    cnt ++;
		}

		return cnt;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public int getChildrenCount()
	{
		return m_children == null ? 0 : m_children.size();
	}

	/**
	 * Gets this parent if this is clusterized.
	 * @return	The parent Attribute or null if this is a cluster.
	 */
	public MapableAtt getParent()
	{
		return m_parent;
	}

/*	public void setParent( ProtoAttribute parent )
	{
	setParent(parent,PlanRequest.ATTR_FREQUENCY_SIZE);
	}*/

	/**
	 * See espinat@mapstan.com
	 */
	public void setParent( ProtoAttribute parent, int attributeSizeType )
	{
		if ( parent.m_parent!= null ) // there is an other parent !
		{
			setParent( parent.m_parent , attributeSizeType);
		}
		else // the real parent
		{
			m_parent = parent;

			// Add child to real parennt
			m_parent.addChild( this, attributeSizeType );

			// Transfer children
			if ( m_children != null )
			{
			   m_parent.addChildren( m_children );
			   m_children = null;
			}
		}
	}

	/**
	 * See espinat@mapstan.com
	 */
	public void addChild( ProtoAttribute child, int attributeSizeType )
	{
		if ( m_children == null )
		{
			m_children = new ArrayList();
		}

		child.m_parent = this;

		if ( !m_children.contains( child ))
		{
			AttributeLink    aL = child.findLink( this );

//			m_weight = (int)(( m_size * m_weight + child.m_weight * child.m_size )/(float)( m_size + child.m_size ));
			m_weight = ( m_size * m_weight + child.m_weight * child.m_size )/( m_size + child.m_size );

			if (attributeSizeType==AnalysisProfile.ATTR_FREQUENCY_SIZE)
				{
					m_size  += child.m_size;
					if ( aL != null )
						m_size -= aL.m_size;
				}
			else
				{
				m_size  = Math.max(m_size, child.m_size);
				}

			addChildLinks( child );
			m_children.add( child );
	   }
	}

	/**
	 * See espinat@mapstan.com
	 */
	public void transferChild( ProtoAttribute child )
	{
		int i, cCnt;

		if ( m_children == null )
		{
			m_children = new ArrayList();
		}

		child.m_parent  = this;
		cCnt            = m_children.size();

		for ( i = 0; i < cCnt && m_children.get( i )!= child; i ++ );

		if ( i == cCnt )
		{
			m_children.add( child );
		}
	}

	/**
	 * Compute new parent Links values with child link values
	 * See espinat@mapstan.com
	 */
	public void addChildLinks( ProtoAttribute child )
	{
		AttributeLink   aL                      = child.findLink( this );   //child link
		AttributeLink   aL2                     = null;                     //parent link
		//int             childParentRadiation    = 0;

		if ( aL != null ||( aL = findLink( child ))!= null )
		{
			//childParentRadiation = aL.m_size;
			aL.remove();
		}
		else return; // cas o� on est dans d�j� dans un fils qui n'a plus de lien qu'on attache � un nouveau p�re

		ListIterator it = child.getLinkIterrator();

	//	if (child==this)
	//	   System.out.print("ERRORCP:");

		while ( it.hasNext())
		{
			aL  = (AttributeLink)it.next(); //child link
			aL2 = null;                     //parent link

			if ( aL.isValid())
			{
				aL2 = aL.m_from == child ? findLink( aL.m_to ) : findLink( aL.m_from );

				if ( aL2 != null ) // on modifie le lien existant
				{
					//int minLinkSize = Math.min( aL.m_size, aL2.m_size );

					// On prend la taille de lien la plus grande, mais ne peut �tre sup�rieur � la fr�quence de l'attribut
					aL2.m_size      = Math.min(m_size,Math.max( aL.m_size, aL2.m_size ));

					// Math.max(aL2.m_size-(m_size-childParentRadiation),0) +  Math.max(aL.m_size-(child.m_size-childParentRadiation),0);
					//(aL2.m_size+aL.m_size)- (int)Math.min(minLinkSize,(int)((float)childParentRadiation*(float)childParentRadiation/(float)Math.min(m_size, child.m_size))); // aproximation
					aL2.m_length    = (int)((( m_size *(long)aL2.m_length )+( child.m_size *(long)aL.m_length ))/( m_size + child.m_size ));
					// marquer lien non valide aL
					aL.m_isInvalid	= true;
//					aL.m_type      |= AttributeLink.INVALID_LINK;

					if ( aL.m_from == child )   ((ProtoAttribute)aL.m_to ).removeLink( aL );
					else                        ((ProtoAttribute)aL.m_from ).removeLink( aL );

					it.remove();
				}
				else // le lien n'existe pas on l'ajoute
				{
					if ( aL.m_from == child )
					{
						aL.m_from   = this;
					}
					else
					{
						aL.m_to     = this;
					}
					addLink( aL );
				}
			}
		}
		it = null;
		child.removeAllLinks();
	}

	/**
	 * find an attribute Link which is a related to an attribute identified by attrNum
	 * See espinat@mapstan.com
	 */
	public AttributeLink findLink( ProtoAttribute attr )
	{
		AttributeLink   aL      = null;
		int             i, lCnt = m_links.size();

		for ( i = 0; i < lCnt; i ++ )
		{
			aL = (AttributeLink)m_links.get( i );

			if (( aL.m_from == attr )||(aL.m_to == attr ))  return aL;
		}

		return null;
	}

	/**
	 * add all children from child to parent
	 * See espinat@mapstan.com
	 */
	public void addChildren(List child)
	{
		if ( m_children == null )
		{
			m_children= new ArrayList();
		}

		Iterator it = child.iterator();

		while ( it.hasNext())
		{
			transferChild((ProtoAttribute)it.next());
		}
	}

	/**
	 * Return wether this comes from the reference.
	 * @return	True if this is a reference Attribute.
	 */
	public final boolean isRef()
	{
		return m_isRef;
	}

	/**
	 * Return wether this comes from the base.
	 * @return	True if this is a base Attribute.
	 */
	public final boolean isBase()
	{
		return m_isBase;
	}

	/**
	 * Returns wether this can reach another Attribute using existing links.
	 * Filtered links are not considered.
	 * @param att	The attribute to reach.
	 * @return	True if a link path exists between this and att.
	 */
	protected boolean isReachable( ProtoAttribute att )
	{
		AttributeLink	link;
		ProtoAttribute	to;
		int				i, n	= m_links.size();

		for ( i = 0; i < n; i ++ )
		{
			link	= (AttributeLink)m_links.get( i );

			if ( !link.m_isFiltered && !link.m_isMarked )
			{
				link.m_isMarked	= true;
				to	= link.m_to == this ? link.m_from : link.m_to;

				if ( to.m_isBase &&( to == att || to.isReachable ( att )))
					return true;
			}
		}

		return false;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean hasRecos( )
	{
		return m_recomGroups != null;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean hasReco( int type )
	{
		return m_recomGroups[type] != null;
	}

	/**
	 * Parent Attribute of this is it is clusterized or null if this is cluster.
	 */

	/**
	 * Gets the links between this attribute and others.
	 * @return	a list of AttributeLink.
	 */
	public ArrayList getLinks()
	{
		return m_links;
	}

	/**
	 * Gets the clusterized children of this or null if this is not a cluster.
	 * @return	a list of ProtoAttribute or null if this has none.
	 */
	public ArrayList getChildren()
	{
		return m_children;
	}

	/**
	 * Gets this size (diameter) as defined by the analysis.
	 * @return	This integer size converted to float.
	 */
	public float getSize()
	{
		return m_size;
	}

	/**
	 * Gets this weight (importance) as defined by the analysis.
	 * @return	This integer weight converted to float.
	 */
	public float getWeight()
	{
		return m_weight;
	}

	/**
	 * Gets the identifier of this in DB.
	 * @return	A String identifier.
	 */
	public String getStrId()
	{
		return m_strId;
	}

	/**
	 * Convert this to a string describing main members.
	 * The format is :<br>
	 * i=[m_strId], n=[m_num], b=[m_isBase], r=[m_isRef], c=[is a Cluster], s=[children count], l=[links count]
	 * @return	A String representation of this.
	 */
	public String toString()
	{
		boolean isClus  = m_parent != null;
		int     subs    = m_children != null ? m_children.size() : 0,
				links   = m_links != null ? m_links.size() : 0;

		return "i=" + m_strId + ", n=" + m_num + ", b=" + m_isBase + ", r=" + m_isRef + ", c=" + isClus + ", s=" + subs + ", l=" + links;
	}

	/**
	 * Initialize the different links count in this affter sorting them.
	 * So m_sortLinks is initialized and all min/max link count are evaluated using dictionary settings.
	 * @param mapDat	The MapData associated with the Plan holding this Attribute.
	 */
	protected void setLinksCnts( MapData mapDat )
	{
		int		i, n	= m_links.size();

		m_sortLinks	= new AttributeLink[n];
		m_links.toArray( m_sortLinks );
		Arrays.sort( m_sortLinks, 0, n, ProtoPlan.s_linkCmp );


		if ( isBase())
		{
			for ( i = 0; i < n && m_sortLinks[i].isBase(); i ++ );
			m_linkCnt		= i;
			m_minLinkCnt	= EZMath.getStatInt( mapDat.m_baseLinkMin );
			m_maxLinkCnt	= EZMath.getStatInt( mapDat.m_baseLinkMax );
		}
		else
		{
			for ( i = 0; i < n && m_sortLinks[i].isMixed(); i ++ );
			m_linkCnt		= n - i;
			m_minLinkCnt	= EZMath.getStatInt( mapDat.m_extLinkMin );
			m_maxLinkCnt	= EZMath.getStatInt( mapDat.m_extLinkMax );
		}

		m_minMixedLinkCnt	= EZMath.getStatInt( mapDat.m_mixedLinkMin );
		m_maxMixedLinkCnt	= EZMath.getStatInt( mapDat.m_mixedLinkMax );
	}

	/**
	 * Unfilter the best min links of each kind in this.
	 * Depending on the type of this, base, mixed or ext links are unfiltered.
	 */
	protected void setMinLinks()
	{
		AttributeLink   link;
		int             i, n	= m_links.size(),
						mixCnt	= n - m_linkCnt;
		boolean			isBase	= isBase();

		if ( isBase )
		{
			m_activeLinkCnt	= m_minLinkCnt < m_linkCnt ? m_minLinkCnt : m_linkCnt;
			n	= m_activeLinkCnt;

			for ( i = 0; i < n; i ++ )
			{
				link	= m_sortLinks[i];
				link.m_isFiltered	= false;
			}
		}
		else
		{
			m_activeMixedCnt	= m_minMixedLinkCnt < mixCnt ? m_minMixedLinkCnt : mixCnt;
			n	= m_activeMixedCnt;

			for ( i = 0; i < n; i ++ )
			{
				link	= m_sortLinks[i];
				link.m_isFiltered	= false;
			}

			m_activeLinkCnt	= m_minLinkCnt < m_linkCnt ? m_minLinkCnt : m_linkCnt;
			n	= m_activeLinkCnt + mixCnt;

			for ( i = mixCnt; i < n; i ++ )
			{
				link	= m_sortLinks[i];
				link.m_isFiltered	= false;
			}
		}
	}
}

package com.socialcomputing.wps.server.generator;

import java.util.Collection;
import java.util.TreeSet;

import com.socialcomputing.wps.server.utils.TreeSetExt;

/**
 * <p>Title: AttributeLink</p>
 * <p>Description: A Link(street) between two Attributs(places).<br>
 * It has a length and a width given by the analysis.
 * There is 3 kind of Links :
 * <ul>
 * <li>base Link : It has 2 base Attributes.</li>
 * <li>mixed Link : It has a base Attribute and an ext Attribute.</li>
 * <li>ext Link : It has 2 ext Attributes.</li>
 * </ul></p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class AttributeLink implements Comparable, MapableLink
{
	/**
	 * Link length as defined by the analysis.
	 */
	public int          			m_length;

	/**
	 * One attribute of the link.
	 * m_from is different from m_to.
	 */
	public ProtoAttribute  			m_from;

	/**
	 * The other attribute of the link.
	 * m_to is different from m_from.
	 */
	public ProtoAttribute  			m_to;

	/**
	 * Width of the link as defined by the analysis.
	 */
	public int   					m_size = 0;

	/**
	 * This link Array of recommendation Groups.
	 */
	public RecommendationGroup[]	m_recomGroups;


	/**
	 * A reference to a map graphical data.
	 * Used to speed up lookup between analysis data and generator data.
	 */
	private LinkMapData             m_mapData;

	/**
	 * True if this is invalid and should be deleted.
	 * This is no more used by generator.
	 */
	protected boolean               m_isInvalid		= false;

	/**
	 * True if this mustn't be relaxed but can appears after.
	 * Because only 'best' links should be relaxed, the others (weak) must be marked.
	 */
	protected boolean               m_isWeak    	= false;

	/**
	 * True if this will not be relaxed.
	 * This is set by generator after FILTERED_LINK has been applied.
	 */
	protected boolean               m_isFiltered    = true;

	/**
	 * True if this is marked.
	 * Used to remember the links visited while traversing links graph.
	 */
	protected boolean               m_isMarked    	= false;

	/**
	 * Sets the map graphical data for this.
	 * @param data	A LinkMapData corresponding to this.
	 */
	public void setMapData( LinkMapData data )
	{
		m_mapData    = data;
	}

	/**
	 * Gets the associated Map graphical data.
	 * @return	The matching LinkMapData
	 */
	public LinkMapData getMapData()
	{
		return m_mapData;
	}

	/**
	 * Gets one of the attributes of this link.
	 * @return	The 'from' MapableAtt.
	 */
	public MapableAtt getFromAtt()
	{
		return m_from;
	}

	/**
	 * Gets one of the attributes of this link.
	 * @return	The 'to' MapableAtt.
	 */
	public MapableAtt getToAtt()
	{
		return m_to;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public void remove( )
	{
		((ProtoAttribute)m_to ).removeLink( this );
		((ProtoAttribute)m_from ).removeLink( this );
//		m_type  |= INVALID_LINK;
		m_isInvalid	= true;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public final int compareTo( Object o )
	{
		AttributeLink link2=(AttributeLink)o;

		if ( link2.m_from.m_isBase && link2.m_to.m_isBase && m_from.m_isBase && m_to.m_isBase )
			return compareLink(this, link2);

		if ( link2.m_from.m_isBase && link2.m_to.m_isBase )
			return 1;

		if ( m_from.m_isBase && m_to.m_isBase )
			return -1;

		return compareLink(this, link2);
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean isLinkBetweenBase()
	{
		return m_from.m_isBase && m_to.m_isBase;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean isLinkRelatedToBase()
	{
		return m_from.m_isBase || m_to.m_isBase;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean isLastLinkRelatedToBase()
	{
		int cnt1=((ProtoAttribute)m_from).getRelatedBaseLinkCount();
		int cnt2=((ProtoAttribute)m_to).getRelatedBaseLinkCount();

		return isLinkRelatedToBase() ? (( cnt1 == 1 )||( cnt2 == 1 ))&& isLinkRelatedToBase() : false;
	}


	/**
	 * See espinat@mapstan.com
	 */
	public boolean isLastLink()
	{
		final int   lastCnt = 1;
		int         cnt1    = ((ProtoAttribute)m_from).getLinkCount();
		int         cnt2    = ((ProtoAttribute)m_to).getLinkCount();

		return ( cnt1 <= lastCnt )||( cnt2 <= lastCnt );
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean isLastLinksNotInBase()
	{
		final int   lastCnt = 2;
		int         cnt1    = ((ProtoAttribute)m_from).getLinkCountNotInBase();
		int         cnt2    = ((ProtoAttribute)m_to).getLinkCountNotInBase();

		return isLinkRelatedToBase() ? false : ( cnt1 <= lastCnt )||( cnt2<=lastCnt );
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean isRef()
	{
		return m_from.isRef() && m_to.isRef();
	}

	/**
	 * See espinat@mapstan.com
	 */
	public void setFiltered()
	{
		if (!isFiltered())
		{
			m_isWeak	= true;
			((ProtoAttribute)m_to).m_filteredLinkCnt ++;
			((ProtoAttribute)m_from).m_filteredLinkCnt ++;
		}
	}

	/**
	 * See espinat@mapstan.com
	 * It's no more used in generator.
	 */
	public boolean isValid()
	{
		return !m_isInvalid;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean isDisplay()
	{
		return isValid() && !isFiltered();
	}

	/**
	 * See espinat@mapstan.com
	 */
	final private int compareLink(AttributeLink link1, AttributeLink link2)
	{
		if ( link2.m_length < link1.m_length )
			return 1;
		else if (link2.m_length == link1.m_length )
		{
			int val1=((ProtoAttribute)link1.m_to).m_weight +((ProtoAttribute)link1.m_from).m_weight;
			int val2=((ProtoAttribute)link2.m_to).m_weight +((ProtoAttribute)link2.m_from).m_weight;

			return val2 - val1;
		}
		else return -1;
	}

	/** Compute the intersection of 'from' and 'to' sets
	 * 
	 */
	public Collection getRecommendations( int recomType )
	{
		TreeSet set1=((ProtoAttribute)m_from ).getAllRecommendations( recomType );
		TreeSet set2=((ProtoAttribute)m_to ).getAllRecommendations( recomType );

		return TreeSetExt.getIntersection( set1, set2 );
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean hasRecos( )
	{
		if ( m_from == null || m_to == null )
			System.out.println( "PB!" );
		return ((ProtoAttribute)m_from ).m_recomGroups != null && ((ProtoAttribute)m_to ).m_recomGroups != null;
	}

	/**
	 * See espinat@mapstan.com
	 */
	public boolean hasReco( int type )
	{
		return ((ProtoAttribute)m_from ).m_recomGroups[type] != null && ((ProtoAttribute)m_to ).m_recomGroups[type] != null;
	}

	/**
	 * Gets the identifier of this link.
	 * As links have no identifier, nothing is returned.
	 * @return	null.
	 */
	public String getStrId( )
	{
		return null;
	}

	/**
	 * Returns wether the analysis thinks this is relaxable.
	 * @return	True if this is a non relaxable weak link.
	 */
	public boolean isFiltered()
	{
		return m_isWeak;
	}

	/**
	 * Returns wether its two attributes comes from base.
	 * @return	True if m_from and m_to are in base.
	 */
	public boolean isBase( )
	{
		return m_from.isBase()&& m_to.isBase();
	}

	/**
	 * Returns wether its two attributes are of different kind.
	 * @return True if m_from type (base or ext) is different from m_to.
	 */
	public boolean isMixed( )
	{
		return m_from.isBase()!= m_to.isBase();
	}

	/**
	 * Returns wether its two attributes are external (outside of the base).
	 * @return	True if m_from and m_to are external.
	 */
	public boolean isExt( )
	{
		return !m_from.isBase() && !m_to.isBase();
	}

	/**
	 * Convert this to a string describing main members.
	 * The format is :<br>
	 * f=[m_isWeak], i=[m_isInvalid], between [m_from] and [m_to]
	 * @return	A String representation of this.
	 */
	public String toString()
	{
		return "f=" + m_isWeak + ", i=" + m_isInvalid + " between " + m_from + " and " + m_to;
	}

	/**
	 * Check and correct supernumary links.
	 * If one of its attributs have too much links then try to remove this one if possible.
	 * If it can't be removed then find another one to remove if possible.
	 */
	protected void updateSupernumerary()
	{
		//AttributeLink	link;
		boolean	isFromSNum, isToSNum;
		//int		i, n;

		isFromSNum	= m_from.m_activeLinkCnt > m_from.m_maxLinkCnt;
		isToSNum	= m_to.m_activeLinkCnt > m_to.m_maxLinkCnt;

		if ( isFromSNum && isToSNum )	// remove this link!
		{
			m_isFiltered	= true;
			m_from.m_activeLinkCnt --;
			m_to.m_activeLinkCnt --;
		}
		else
		{
			if ( isFromSNum )		removeSNum( m_from, m_to );
			else if ( isToSNum )	removeSNum( m_to, m_from );
			// else no Supernumerary!
		}
	}

	/**
	 * Unfilter this if its attributes have still not reach their maximum link count.
	 * This is used to "unfilter" the best links of the map.
	 */
	protected void activateIfPossible()
	{
		if ( isMixed())
		{
			if ( m_from.m_activeMixedCnt < m_from.m_maxMixedLinkCnt && m_to.m_activeMixedCnt < m_to.m_maxMixedLinkCnt )
			{
				m_from.m_activeMixedCnt ++;
				m_to.m_activeMixedCnt ++;
				m_isFiltered	= false;
			}
		}
		else
		{
			if ( m_from.m_activeLinkCnt < m_from.m_maxLinkCnt && m_to.m_activeLinkCnt < m_to.m_maxLinkCnt )
			{
				m_from.m_activeLinkCnt ++;
				m_to.m_activeLinkCnt ++;
				m_isFiltered	= false;
			}
		}
	}

	/**
	 * Filter this because it's supernumary.
	 * If it's not possible then try to remove another weak link from the 'to' attribute.
	 * @param from	Attribute in this that has no supernumary links.
	 * @param to	Attribute in this that has supernumary links including this.
	 */
	private void removeSNum( ProtoAttribute from, ProtoAttribute to )
	{
		if ( to.m_activeLinkCnt > to.m_minLinkCnt )	// we can remove this link
		{
			m_isFiltered	= true;
			from.m_activeLinkCnt --;
			to.m_activeLinkCnt --;
		}
		else	// we should find another link if possible to replace the one to remove
		{
			AttributeLink	link;
			int				i, n	= to.m_linkCnt;

			// find this in the link table
			for ( i = 0; i < n && to.m_sortLinks[i] != this; i ++ );

			// then find the first weaker link that can be "unfiltered"
			for ( ; i < n; i ++ )
			{
				link	= to.m_sortLinks[i];

				if ( link.m_isFiltered )
				{
					link.m_isFiltered	= false;
					link.m_from.m_activeLinkCnt ++;
					link.m_to.m_activeLinkCnt ++;

					m_isFiltered		= true;
					from.m_activeLinkCnt --;
					to.m_activeLinkCnt --;
					break;
				}
			}
		}
	}

	/**
	 * Checks wether this can be filtered without breaking the base connexity.
	 * @param plan	ProtoPlan holding this link.
	 * @return		True if this link can be safely removed.
	 */
	protected boolean isLinkFiltrable( ProtoPlan plan )
	{
		plan.setLinksMarker( false );
		m_isMarked	= true;

		return m_from.isReachable( m_to );
	}

}

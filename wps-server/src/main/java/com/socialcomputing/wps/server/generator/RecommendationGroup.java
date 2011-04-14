package com.socialcomputing.wps.server.generator;

import java.util.Collection;

/**
 * <p>Title: RecommendationGroup</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class RecommendationGroup extends ProtoObject
{
	public static final int     SATTRIBUTES_RECOM    = 0;
	public static final int     ENTITIES_RECOM        = 1;
	public static final int     ATTRIBUTES_RECOM     = 2;
	public static final int     RECOM_TYPE_CNT      = 3;


	/**
	 * Ids of sub attributes which are recommended
	 */
	public Collection<String> m_recommendations = null;

	public RecommendationGroup( String strId )
	{
		super( strId, SATTRIBUTES_RECOM  );
	}

	public RecommendationGroup( String strId, int type )
	{
		super( strId, type );
	}

	public RecommendationGroup( String strId, int type, int num )
	{
		super( strId, type, num );
	}

	public void setRecommendations(Collection<String> recommendations) {
		m_recommendations=recommendations;
	}

}

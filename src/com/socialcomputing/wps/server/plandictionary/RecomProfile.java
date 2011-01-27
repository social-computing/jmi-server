package com.socialcomputing.wps.server.plandictionary;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      MapStan
 * @author Franck Valetas
 * @version 1.0
 */

public class RecomProfile implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3244029320222883158L;
	public int m_RecommendationThreshold = 20; // % between 0 and 100 (Attention, c'est l'inverse d'une distance)
	public int m_RecommendationScale= WPSDictionary.APPLY_TO_ALL;
	public int m_RecommendationMaxNb = 10;

	private ClassifierMapper m_PropertiesMapper = null;

	public RecomProfile()
	{
	}

	static public RecomProfile readObject( org.jdom.Element element) throws org.jdom.JDOMException
	{
		RecomProfile profile = new RecomProfile();
		profile.m_RecommendationThreshold = Integer.parseInt( element.getAttributeValue( "threshold"));
		String p = element.getAttributeValue( "apply");
		if( p.equalsIgnoreCase( "base"))
			profile.m_RecommendationScale = WPSDictionary.APPLY_TO_BASE;
		else if( p.equalsIgnoreCase( "notbase"))
			profile.m_RecommendationScale = WPSDictionary.APPLY_TO_NOT_BASE;
		else
			profile.m_RecommendationScale = WPSDictionary.APPLY_TO_ALL;
		profile.m_RecommendationMaxNb = Integer.parseInt( element.getAttributeValue( "max"));

		profile.m_PropertiesMapper = ClassifierMapper.readSimpleMapping( element);
		return profile;
	}

	public String getSwatchProperty( String swatchPropId)
	{
		String value = m_PropertiesMapper.getAssociatedName( swatchPropId);
		return (value == m_PropertiesMapper.getDefault()) ? null : value;
	}
}
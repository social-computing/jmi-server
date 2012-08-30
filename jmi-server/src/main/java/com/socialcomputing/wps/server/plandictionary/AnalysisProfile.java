package com.socialcomputing.wps.server.plandictionary;

import com.socialcomputing.wps.server.generator.MapData;
import com.socialcomputing.wps.server.generator.RecommendationGroup;
import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;

/**
 * Title:        AnalysisProfile
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      MApStan
 * @author Franck Valetas
 * @version 1.0
 */

public class AnalysisProfile implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2809485930297348565L;
	public String m_Name = null;
	public String m_Description = null;

	public static final int DEFAULT_DATA_CLUSTER_THRESHOLD=20;

	// Type de plan
	public static final int PERSONAL_PLAN= 0x01;
	public static final int GLOBAL_PLAN= 0x02;
	public static final int DISCOVERY_PLAN= 0x03;

	// Type d'algorithme de choix du parent pour la clusterisation
	public static final int SIZE_CLUSTERING=0;
	public static final int PERSONAL_POND_CLUSTERING=1;

	// Type d'algorithme pour determiner la taille d'un attribut
	public static final int ATTR_FREQUENCY_SIZE=0;
	public static final int ATTR_PONDERATION_SIZE=1;
	public static final int ATTR_SAME_SIZE=2;
	public static final int ATTR_MAX_PONDERATION_SIZE=3;

	// Flag spécifique pour ne pas ajouter l'attribut de decouverte
	public static final int NO_DISCOVERY_ADD_IN_PROFILE=1000;

	public String m_AttributesRef = null;

	public int m_planType=PERSONAL_PLAN;

	// Moteur Analyse
	public int m_DataClusterThreshold = DEFAULT_DATA_CLUSTER_THRESHOLD; // between 0 and 1
	public int m_BalancedEffect = 0;
	public int m_EntitiesMaxNb = 100;
	public int m_AttributesMaxNb = 60;  // attributes Cnt
	public int m_AttributesBaseMaxNb = 30 ; // max attributes cnt in base
	public int  m_AttrLinkThreshold = 10; // % between 0 and 100
	public int  m_DataClusterLevel = 50 ; // % between 0 and 100
	public int m_MaxAttributesPerCluster=7;
	public int  m_LinksPerAttributeThreshold = 3; // cnt >=1
	public boolean m_DoClustering = true;
	public String m_SelfClusteringProperty = null;
	public String m_ForceClusteringProperty = null;
	public int m_parentClustering=SIZE_CLUSTERING;
	public int m_attributeSizeType= ATTR_FREQUENCY_SIZE;
	public MapData       m_mapDat;


	// Matching Swatch Attribute / DB Attribute
	public ClassifierMapper m_EntityPropertiesMapper = new ClassifierMapper();
	// Matching Swatch Attribute / DB Attribute
	public ClassifierMapper m_AttributePropertiesMapper = new ClassifierMapper();
	// Matching Swatch Attribute / DB Attribute
	public ClassifierMapper m_SubAttributePropertiesMapper = new ClassifierMapper();

	// Recommandations
	public RecomProfile [] m_RecomProfiles = new RecomProfile[ RecommendationGroup.RECOM_TYPE_CNT];

	static public AnalysisProfile readObject( org.jdom.Element element) throws org.jdom.JDOMException
	{
		AnalysisProfile profile = new AnalysisProfile( element.getAttributeValue( "name"));
		profile.m_Description = element.getChildText( "comment");
		profile.m_AttributesRef = element.getAttributeValue( "attributes-ref");

		String p = element.getAttributeValue( "type");
		if( p.equalsIgnoreCase( "global"))
			profile.m_planType = AnalysisProfile.GLOBAL_PLAN;
		else if( p.equalsIgnoreCase( "discovery"))
			profile.m_planType = AnalysisProfile.DISCOVERY_PLAN;
		else
			profile.m_planType = AnalysisProfile.PERSONAL_PLAN;

		profile.m_DoClustering = element.getAttributeValue( "cluster").equalsIgnoreCase( "yes");
		profile.m_SelfClusteringProperty = element.getAttributeValue( "self-clustering-property");
		profile.m_ForceClusteringProperty = element.getAttributeValue( "force-clustering-property");
		p = element.getAttributeValue( "cluster-type");
		if( p.equalsIgnoreCase( "size"))
			profile.m_parentClustering = AnalysisProfile.SIZE_CLUSTERING;
		else
			profile.m_parentClustering = AnalysisProfile.PERSONAL_POND_CLUSTERING;

		p = element.getAttributeValue( "attribute-size");
		if( p.equalsIgnoreCase( "frequency"))
			profile.m_attributeSizeType = AnalysisProfile.ATTR_FREQUENCY_SIZE;
		else if( p.equalsIgnoreCase( "same"))
			profile.m_attributeSizeType = AnalysisProfile.ATTR_SAME_SIZE;
		else if( p.equalsIgnoreCase( "max-ponderation"))
			profile.m_attributeSizeType = AnalysisProfile.ATTR_MAX_PONDERATION_SIZE;
		else if( p.equalsIgnoreCase( "ponderation"))
			profile.m_attributeSizeType = AnalysisProfile.ATTR_PONDERATION_SIZE;

		profile.m_BalancedEffect = Integer.parseInt( element.getAttributeValue( "balanced-effect"));
		profile.m_EntitiesMaxNb = Integer.parseInt( element.getAttributeValue( "entities-max"));
		profile.m_AttributesMaxNb = Integer.parseInt( element.getAttributeValue( "attributes-max"));

		profile.m_AttributesBaseMaxNb = Integer.parseInt( element.getAttributeValue( "attributes-base-max") );
		if (profile.m_AttributesBaseMaxNb!=NO_DISCOVERY_ADD_IN_PROFILE)
			profile.m_AttributesBaseMaxNb = Math.min(profile.m_AttributesMaxNb, profile.m_AttributesBaseMaxNb);

		profile.m_AttrLinkThreshold = Integer.parseInt( element.getAttributeValue( "attr-link-threshold"));
		profile.m_DataClusterLevel = Integer.parseInt( element.getAttributeValue( "cluster-level"));
		profile.m_MaxAttributesPerCluster = Integer.parseInt( element.getAttributeValue( "attributes-cluster-max"));
		profile.m_LinksPerAttributeThreshold = Integer.parseInt( element.getAttributeValue( "links-attribute-threshold"));
		profile.m_DataClusterThreshold = Integer.parseInt( element.getAttributeValue( "cluster-threshold"));

		// Properties Mapping
		org.jdom.Element props = element.getChild( "props-mapping");
		if( props != null)
		{
			org.jdom.Element pps = props.getChild( "entity-props-mapping");
			if( pps != null)
				profile.m_EntityPropertiesMapper = ClassifierMapper.readSimpleMapping( pps);
			pps = props.getChild( "attribute-props-mapping");
			if( pps != null)
				profile.m_AttributePropertiesMapper = ClassifierMapper.readSimpleMapping( pps);
			pps = props.getChild( "subattribute-props-mapping");
			if( pps != null)
				profile.m_SubAttributePropertiesMapper = ClassifierMapper.readSimpleMapping( pps);
		}
		else {
            profile.m_EntityPropertiesMapper = ClassifierMapper.GetSimpleMapping();
            profile.m_AttributePropertiesMapper = ClassifierMapper.GetSimpleMapping();
            profile.m_SubAttributePropertiesMapper = ClassifierMapper.GetSimpleMapping();
		}

		// Recommandation profiles
		org.jdom.Element rec = element.getChild( "entities-recommendation");
		if( rec != null)
			profile.m_RecomProfiles[ RecommendationGroup.ENTITIES_RECOM] = RecomProfile.readObject( rec);
		rec = element.getChild( "attributes-recommendation");
		if( rec != null)
			profile.m_RecomProfiles[ RecommendationGroup.ATTRIBUTES_RECOM] = RecomProfile.readObject( rec);
		rec = element.getChild( "subattributes-recommendation");
		if( rec != null)
			profile.m_RecomProfiles[ RecommendationGroup.SATTRIBUTES_RECOM] = RecomProfile.readObject( rec);

		// If this element does not exists, returns default values
		profile.m_mapDat   = MapData.readObject( element.getChild( "relax" ), profile.m_planType );

		return profile;
	}

	public AnalysisProfile( String name)
	{
		m_Name = name;
	}

	public void checkIntegrity( String m, iEntityConnector entities) throws org.jdom.JDOMException, JMIException
	{
		if( entities.getProfile( m_AttributesRef) == null)
			throw new org.jdom.JDOMException( m + ", Analysis Profile '" + m_Name + "', Unknown Attributes '" + m_AttributesRef + "'");
	}

	public iProfileConnector getConnector( WPSDictionary dico) throws JMIException
	{
		return dico.m_EntitiesConnector.getProfile( m_AttributesRef);
	}

	public String getEntitySwatchProperty( String swatchPropId)
	{
		String value = m_EntityPropertiesMapper.getAssociatedName( swatchPropId);
		return (value == m_EntityPropertiesMapper.getDefault()) ? null : value;
	}

	public String getAttributeSwatchProperty( String swatchPropId)
	{
		String value = m_AttributePropertiesMapper.getAssociatedName( swatchPropId);
		return (value == m_AttributePropertiesMapper.getDefault()) ? null : value;
	}

	public String getSubAttributeSwatchProperty( String swatchPropId)
	{
		String value = m_SubAttributePropertiesMapper.getAssociatedName( swatchPropId);
		return (value == m_SubAttributePropertiesMapper.getDefault()) ? null : value;
	}

}
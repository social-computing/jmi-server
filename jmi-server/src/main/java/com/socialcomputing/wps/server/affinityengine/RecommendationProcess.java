package com.socialcomputing.wps.server.affinityengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.socialcomputing.wps.server.generator.RecommendationGroup;
import com.socialcomputing.wps.server.planDictionnary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.connectors.SubAttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.utils.AttributesPonderationMap;
import com.socialcomputing.wps.server.utils.MathLogBuffer;
import com.socialcomputing.wps.server.utils.NumAndFloat;
import com.socialcomputing.wps.server.utils.ObjectToNumConverter;
import com.socialcomputing.wps.server.utils.StringAndFloat;
import com.socialcomputing.wps.server.utils.StringToNumConverter;
import com.socialcomputing.wps.server.webservices.PlanRequest;

/**
 * recommendation for attributes of reference entity (create only if
 * recommendation is requested)
 **/
public class RecommendationProcess {
	public class SAttrStat {
		public int attribute;
		public float sum;
		public int inter;

		public SAttrStat(int attrNum) {
			attribute = attrNum;
		}
	}

	private PlanRequest m_PlanRequest = null;
	// AnalysisProfile used for current request
	private AnalysisProfile m_Profile = null;

	/* key= id (String), value AffinityCoef (float) */
	private HashMap<String, Float> m_Entities = null;

	// Data set by AnalysisEngine for better speed
	private HashMap m_Profiles = null;
	private StringToNumConverter m_AttrConverter = null;

	/**
	 * key = Attribute Id (String) object = SubAttributes Recom (String)
	 */
	private HashMap<String, Collection<StringAndFloat>> m_Recommendations = new HashMap<String, Collection<StringAndFloat>>();
	private String m_RefEntity = null;
	private String[] m_RefAttributes = null;
	private String[] m_RefSAttributes = null;
	private ObjectToNumConverter m_SAttrConverter = new ObjectToNumConverter();
	private iProfileConnector m_ProfileCon = null;
	private float m_MaxPond = 100; // A FIXER DATABASE !

	public RecommendationProcess(PlanRequest planRequest,
			StringAndFloat[] entities) throws JMIException {
		m_PlanRequest = planRequest;
		m_Profile = m_PlanRequest.getAnalysisProfile();

		m_SAttrConverter.setBlocksize(Math.max(
				planRequest.getAffinityReaderProfile().m_AffinityMaxNb * 50,
				1000));
		m_Entities = new HashMap<String, Float>();
		for (int i = 0; i < entities.length; ++i) {
			m_Entities.put(entities[i].m_Id, entities[i].m_value);
		}

		m_RefEntity = m_PlanRequest.m_entityId;
		m_ProfileCon = m_PlanRequest.getAnalysisProfile().getConnector(
				m_PlanRequest.m_Dictionary);

		Collection<String> saColl = new ArrayList<String>();
		Collection<String> aColl = new ArrayList<String>();
		if (m_PlanRequest.getAnalysisProfile().m_planType != AnalysisProfile.DISCOVERY_PLAN) { 
			// Read attributes of m_RefEntity and store in m_RefSAttributes and sort
			iEnumerator<AttributeEnumeratorItem> aEnum = m_ProfileCon.getEnumerator( m_RefEntity);
			for (AttributeEnumeratorItem aItem : aEnum) {
				aColl.add(aItem.m_Id);
				iSubAttributeConnector saCon = m_ProfileCon.getSubAttribute();
				if (saCon != null) {
					iEnumerator<SubAttributeEnumeratorItem> saEnum = saCon.getEnumerator(
							m_RefEntity, aItem.m_Id);
					if (saEnum != null) {
						for( SubAttributeEnumeratorItem saItem : saEnum) {
							saColl.add(saItem.m_Id);
						}
					}
				}
			}
		}
		m_RefAttributes = (String[]) aColl.toArray(new String[0]);
		// Arrays.sort(m_RefAttributes);
		m_RefSAttributes = (String[]) saColl.toArray(new String[0]);
		Arrays.sort(m_RefSAttributes);
	}

	/**
	 * precompute recommendation for attributes of reference entity
	 */
	public void precompute() throws JMIException {
		compute(m_RefAttributes);
	}

	/**
	 * recommendation for the reference entity and a given attributes collection
	 **/
	public void compute(String attributes[]) throws JMIException {
		double ln2 = MathLogBuffer.getLog(2);
		double lnAffinityCard = MathLogBuffer.getLog(m_Entities.size() + 1);

		String eId = null;
		iSubAttributeConnector saCon = null;
		int aNum, saNum;
		SAttrStat stat = null;

		Arrays.sort(attributes);

		Iterator it = m_Entities.entrySet().iterator();

		// For All entities of the segment
		while (it.hasNext()) {
			eId = (String) ((Map.Entry) it.next()).getKey();

			for (AttributeEnumeratorItem aItem : m_ProfileCon.getEnumerator(eId)) {
				aNum = 0;
				// For all specified attributes
				if ((aNum = Arrays.binarySearch(attributes, aItem.m_Id)) >= 0) {
					saCon = m_ProfileCon.getSubAttribute();
					if (saCon != null) {
						for( SubAttributeEnumeratorItem saItem : saCon.getEnumerator(eId, aItem.m_Id)) {
							// If not in sub-attributes of reference entity
							if (Arrays.binarySearch(m_RefSAttributes,
									saItem.m_Id) < 0) {
								saNum = addSubAttribute(saItem.m_Id, aNum); // not�
																			// l'attribut
																			// associe
																			// +
																			// pour
																			// le
																			// stockage
																			// des
																			// valeurs
																			// ?
								stat = (SAttrStat) m_SAttrConverter
										.getObject(saNum);
								stat.sum += AttributesPonderationMap
										.ponderationFunction(
												saItem.m_Ponderation, m_MaxPond)
										* Math.log(2.0 - (double) getAffinityCoef(eId))
										/ ln2; // pb taille variable traiter
								stat.inter++;
							}
						}
					}
				}
			}
		}

		// a TreeSet for each attribute to sort SAttribute recommandation
		TreeSet tree[] = new TreeSet[attributes.length];
		for (int i = 0; i < attributes.length; ++i)
			tree[i] = new TreeSet();

		float value;
		// the recommendations by value foe each attribute
		for (int i = 0; i < m_SAttrConverter.size(); ++i) {
			stat = (SAttrStat) m_SAttrConverter.getObject(i);
			value = stat.sum
					/ (float) stat.inter
					* (float) (MathLogBuffer.getLog(stat.inter + 1) + lnAffinityCard)
					/ (float) lnAffinityCard;
			if (value >= ((float) m_Profile.m_RecomProfiles[RecommendationGroup.SATTRIBUTES_RECOM].m_RecommendationThreshold / (float) 100.0))
				tree[stat.attribute].add(new StringAndFloat(m_SAttrConverter
						.getString(i), value));
		}

		// recommendations in hashmap for each attribut
		Collection entRet = null;
		Object[] array = null;
		for (int i = 0; i < attributes.length; ++i) {
			if (tree[i].size() != 0) {
				array = tree[i].toArray();
				if (m_Profile.m_RecomProfiles[RecommendationGroup.SATTRIBUTES_RECOM].m_RecommendationMaxNb < array.length)
					entRet = tree[i]
							.headSet(array[m_Profile.m_RecomProfiles[RecommendationGroup.SATTRIBUTES_RECOM].m_RecommendationMaxNb]);
				else
					entRet = tree[i];
				m_Recommendations.put(attributes[i], entRet /*
															 * convertir en
															 * String [] ?
															 */);
			}
		}
	}

	/**
	 * give recommendations for a given attribute read in m_Recommdations
	 */
	public Collection<StringAndFloat> getRecommendations(String attribute) {
		if (m_Recommendations == null)
			return new ArrayList<StringAndFloat>();
		else
			return m_Recommendations.get(attribute);
	}

	/*
	 * Get Affinity Coef with reference entity for a given entity id
	 */
	private float getAffinityCoef(String id) {
		return ((Float) m_Entities.get(id)).floatValue();
	}

	/**
	 * Test if the SubAttribute is present in the converter and add it if
	 * necessary
	 */
	private int addSubAttribute(String subAttributeId, int attributeNum) {
		if (m_SAttrConverter.contains(subAttributeId))
			return (m_SAttrConverter.getNum(subAttributeId));
		else {
			return m_SAttrConverter.add(subAttributeId, new SAttrStat(
					attributeNum));
		}
	}

	public void setProfilesBuffer(HashMap profiles,
			StringToNumConverter attrConverter) {
		m_Profiles = profiles;
		m_AttrConverter = attrConverter;
	}

	/**
	 * recommendation for the reference entity and a given attributes collection
	 * called by analyzer with a table of attributes (Numerical)
	 **/
	public void compute(int attributes[]) throws JMIException {
		double ln2 = MathLogBuffer.getLog(2);
		double lnAffinityCard = MathLogBuffer.getLog(m_Entities.size() + 1);
		Iterator it = m_Profiles.entrySet().iterator();
		// Map.Entry item=null;
		String eId = null, aId = null;
		iSubAttributeConnector saCon = null;

		SAttrStat stat = null;
		int saNum, aNum;
		NumAndFloat[] profile;
		Map.Entry entry = null;

		while (it.hasNext()) {
			entry = (Map.Entry) it.next();
			eId = (String) entry.getKey();

			if ((m_RefEntity != null) && (eId.compareTo(m_RefEntity) == 0))
				continue;

			profile = (NumAndFloat[]) entry.getValue();
			saCon = m_ProfileCon.getSubAttribute();

			for (int i = 0; i < profile.length; ++i) {
				// For all specified attributes
				if ((aNum = Arrays.binarySearch(attributes, profile[i].m_num)) >= 0) {

					if (saCon != null) {
						aId = m_AttrConverter.getString(profile[i].m_num);

						for( SubAttributeEnumeratorItem saItem : saCon.getEnumerator(eId, aId)) {

							// If not in sub-attributes of reference entity
							if (Arrays.binarySearch(m_RefSAttributes,
									saItem.m_Id) < 0) {
								saNum = addSubAttribute(saItem.m_Id, aNum); // not�
																			// l'attribut
																			// associ�
																			// +
																			// pour
																			// le
																			// stockage
																			// des
																			// valeurs
																			// ?
								stat = (SAttrStat) m_SAttrConverter
										.getObject(saNum);
								// stat.sum+=AttributesPonderationMap.ponderationFunction(saItem.m_Ponderation,
								// m_MaxPond)/getAffinityCoef( eId); // pb
								// taille variable traiter
								stat.sum += AttributesPonderationMap
										.ponderationFunction(
												saItem.m_Ponderation, m_MaxPond)
										* Math.log(2.0 - (double) getAffinityCoef(eId))
										/ ln2; // pb taille variable traiter
								stat.inter++;
							}
						}
					}
				}
			}
		}

		// a TreeSet for each attribute to sort SAttribute recommandation
		TreeSet tree[] = new TreeSet[attributes.length];
		for (int i = 0; i < attributes.length; ++i)
			tree[i] = new TreeSet();

		// System.out.println("SAttrCnt"+m_SAttrConverter.size());

		// the recommendations by value foe each attribute
		float value;
		for (int i = 0; i < m_SAttrConverter.size(); ++i) {
			stat = (SAttrStat) m_SAttrConverter.getObject(i);
			value = stat.sum
					/ (float) stat.inter
					* (float) (MathLogBuffer.getLog(stat.inter + 1) + lnAffinityCard)
					/ (float) lnAffinityCard;
			if (value >= ((float) m_Profile.m_RecomProfiles[RecommendationGroup.SATTRIBUTES_RECOM].m_RecommendationThreshold / (float) 100.0))
				tree[stat.attribute].add(new StringAndFloat(m_SAttrConverter
						.getString(i), value));
		}

		// recommendations in hashmap for each attribut
		Collection entRet = null;
		Object[] array = null;
		String attrId = null;
		for (int i = 0; i < attributes.length; ++i) {
			if (tree[i].size() != 0) {
				array = tree[i].toArray();
				attrId = m_AttrConverter.getString(attributes[i]);

				if (m_Profile.m_RecomProfiles[RecommendationGroup.SATTRIBUTES_RECOM].m_RecommendationMaxNb < array.length)
					entRet = tree[i]
							.headSet(array[m_Profile.m_RecomProfiles[RecommendationGroup.SATTRIBUTES_RECOM].m_RecommendationMaxNb]);
				else
					entRet = tree[i];

				m_Recommendations
						.put(attrId, entRet /* convertir en String [] ? */);
			}
		}
	}
}

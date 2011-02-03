/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.socialcomputing.wps.server.analysisengine;

//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.affinityengine.RecommendationInterface;
import com.socialcomputing.wps.server.affinityengine.RecommendationProcess;
import com.socialcomputing.wps.server.generator.AttributeLink;
import com.socialcomputing.wps.server.generator.EntityLink;
import com.socialcomputing.wps.server.generator.ProtoAttribute;
import com.socialcomputing.wps.server.generator.ProtoEntity;
import com.socialcomputing.wps.server.generator.ProtoPlan;
import com.socialcomputing.wps.server.generator.RecommendationGroup;
import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.utils.FSymmetricalMatrix;
import com.socialcomputing.wps.server.utils.MathLogBuffer;
import com.socialcomputing.wps.server.utils.NumAndFloat;
import com.socialcomputing.wps.server.utils.ObjectNumStat;
import com.socialcomputing.wps.server.utils.ObjectStringStat;
import com.socialcomputing.wps.server.utils.SymmetricalMatrix;
import com.socialcomputing.wps.server.webservices.PlanRequest;
import com.socialcomputing.utils.EZTimer;
import com.socialcomputing.utils.math.Bounds;


/**
 * The analysis engine compute the entities and attributes, and all the statistical informations which are necessary to generate plan.
 **********************************************************
 Java
 * Class Name : AnalysisEngine
 * ---------------------------------------------------------
 * Filetype: (SOURCE)
 * Filepath: C:\Dvpt\com\voyezvous\wps\server\analysisengine\AnalysisEngine.java
 *
 *
 * ---------------------------------------------------
 * Author         : Emmanuel SPINAT
 * Creation Date  : Thur - Dec 28, 2000
 *
 * Change Log     :
 *
 * ********************************************************** */
public class AnalysisProcess {

	private static final Logger log = LoggerFactory.getLogger(AnalysisProcess.class);
	
	// All statistics about radiation of base attributes
	// which are necessary to compute attributes to display on map
	class BaseRadiationStat {
		int m_MaxFrequency = 0;
		// Radiation vector of base attributes
		int[] m_BaseRadiation = null;
		// Number of attributes in radiation of base attributes
		int m_AttributesCntInRadiation = 0;
		// Statistics for all attributes in base radiation
		ArrayList m_AttributesStat = null;
	}


	private PlanRequest m_PlanRequest = null;
	private AnalysisProfile m_Profile = null;
	private ProtoPlan m_Plan = null;

	// All entities to compute
	private Collection<String> m_Entities = null;
	private RadiationData m_RadData = null;
	// All statistics about radiation of base attributes
	private BaseRadiationStat m_BaseRadiationStat = new BaseRadiationStat();
	// Link to recommendation engine to send it bufferized profiles
	// and launch recommendation if necessary
	RecommendationInterface m_RecomInterface = null;

	// Statistics about clustering
	int m_clusterCnt=0;
	int m_clusterCntInBase=0;

	/**
	 * entities is the affinity group
	 **/
	public AnalysisProcess (PlanRequest planRequest, Collection<String> entities)  throws WPSConnectorException
	{
		m_PlanRequest = planRequest;
		m_Entities = entities;
		m_RadData = new RadiationData(planRequest, entities);
		m_Profile = m_PlanRequest.getAnalysisProfile();
	}

	/**
	 * entities is the affinity group
	 **/
	public AnalysisProcess (PlanRequest planRequest, Collection<String> entities, RecommendationInterface recomInterface)  throws WPSConnectorException
	{
		this(planRequest, entities);
		m_RecomInterface = recomInterface;
	}

	// Compute initial statistics about radiation of base attributes
	private void computeBaseRadiationStat (Collection<Integer> base) {
		Integer[] baseArray = base.toArray( new Integer[0]);
		m_BaseRadiationStat.m_BaseRadiation = m_RadData.getRadiationArray(baseArray);
		m_BaseRadiationStat.m_AttributesCntInRadiation = RadiationData.positiveValuesCnt(m_BaseRadiationStat.m_BaseRadiation);
		m_BaseRadiationStat.m_MaxFrequency = -1;
		for (int i = 0, max; i < baseArray.length; ++i) {
			if (m_BaseRadiationStat.m_MaxFrequency < (max = m_RadData.getFrequency(baseArray[i].intValue())))
				m_BaseRadiationStat.m_MaxFrequency = max;
		}
	}

	// Compute initial statistics about all attributes
	private ArrayList computeInitialAttributesStat (Collection base) {
		ArrayList vArray = new ArrayList();
		int size = m_RadData.getAttributesCnt();
		List sortedBase = new ArrayList(base);
		Collections.sort(sortedBase);
		for (int i = 0; i < size; ++i) {
			if (Collections.binarySearch(sortedBase, new Integer(i)) < 0) {
				float temp[] = new float[3];
				// temp[0]=m_RadData.getExclusiveRadiation(i, m_BaseRadiationStat);
				temp[1] = m_RadData.getBalancedRadiationPower(i);               // A optimiser
				temp[2] = (float)m_RadData.getFrequency(i);
				vArray.add(new ObjectNumStat(i, temp));
			}
		}
		return  vArray;
	}

	// compute statistics about attributes in base radiation
	// This statistics are necessary to computeAttributes method
	private ArrayList computeAttributesInRadiationStat (Collection base, ArrayList allAttributes) {
		ArrayList vArray = new ArrayList();
		int size = m_RadData.getAttributesCnt();
		if (allAttributes == null) {
			List sortedBase = new ArrayList(base);
			Collections.sort(sortedBase);
			for (int i = 0; i < size; ++i) {
				if ((Collections.binarySearch(sortedBase, new Integer(i)) < 0)
						&& (m_BaseRadiationStat.m_BaseRadiation[i] != 0)) {
					float temp[] = new float[1];
					temp[0] = m_RadData.getExclusiveRadiation(i, m_BaseRadiationStat);
					vArray.add(new ObjectNumStat(i, temp));
				}
			}
		}
		else {
			Iterator it = allAttributes.iterator();
			while (it.hasNext()) {
				ObjectNumStat stat = ((ObjectNumStat)it.next());
				if (m_BaseRadiationStat.m_BaseRadiation[stat.m_Num] != 0) {
					float temp[] = new float[1];
					temp[0] = m_RadData.getExclusiveRadiation(stat.m_Num, m_BaseRadiationStat);
					vArray.add(new ObjectNumStat(stat.m_Num, temp));
				}
			}
		}
		return  vArray;
	}

	// Update initial statistics about radiation of base attributes
	// When you add a new attribute in base
	private void updateBaseRadiationStat (int attributeToAdd) {
		m_BaseRadiationStat.m_MaxFrequency = Math.max(m_BaseRadiationStat.m_MaxFrequency,
				m_RadData.getFrequency(attributeToAdd));
		int value;
		for (int i = 0; i < m_RadData.getAttributesCnt(); ++i) {
			value = m_RadData.getRadiation(i, attributeToAdd);
			if (value != 0) {
				if (m_BaseRadiationStat.m_BaseRadiation[i] == 0)
					++m_BaseRadiationStat.m_AttributesCntInRadiation;
				m_BaseRadiationStat.m_BaseRadiation[i] += value;
			}
		}
	}

	/** The public function to compute and obtain ProtoPlan
	 */
	public ProtoPlan getProtoPlan ()  throws WPSConnectorException
	{

		Collection base, attributes, entities;

		EZTimer timer = new EZTimer();

		if ((m_RadData.getAttributesCnt()!=0) & (m_Entities.size()!=0))
			{
			base = getBase();

			attributes = computePlanAttributes(base);

			if (m_RecomInterface != null)
				computeRecommendations(base, attributes);

				if (m_PlanRequest.getModel().m_DisplayEntities)
					entities = computePlanEntities(base, attributes);
				else entities=computePlanRefEntity(base, attributes);

				}
		else
			{
				base=new ArrayList();
				attributes=	new ArrayList();
				entities =	new ArrayList();
			}

		createProtoPlan(base, attributes, entities);

		log.info("Attributes:{}",m_Plan.m_attributes.length);
		log.info("Attribute Links:{}",m_Plan.m_attLinks.length);
		log.info("ClusterCnt:{}",m_clusterCnt);

		timer.showElapsedTime( "ANALYSIS" );

//		if (EZDebug.s_level!=EZDebug.DEBUG_NONE)
//			dumpPlan(m_Plan);

		return  m_Plan;
	}

	/**
	 *  * Compute the attributes of the base
	 */
	private Collection getBase ()  throws WPSConnectorException
	{
		Collection base;
		//int firstAttributeNum = -1;
		//int size = m_RadData.getAttributesCnt();
		int maxBaseSize = m_PlanRequest.getAnalysisProfile().m_AttributesBaseMaxNb;

		if ((m_PlanRequest.m_entityId != null) && (m_Profile.m_planType==AnalysisProfile.PERSONAL_PLAN))
			base = m_RadData.getNumProfileCollection(m_PlanRequest.m_entityId);
		else base = new ArrayList();             // Add the attribute which has the max Prp power

		if (base.size()==0)
			{
			if ((m_Profile.m_planType==AnalysisProfile.DISCOVERY_PLAN) && (m_PlanRequest.m_discoveryAttributeId!=null) && (m_PlanRequest.m_discoveryAttributeId.compareTo("")!=0) )
				base.add(new Integer(m_RadData.getNum(m_PlanRequest.m_discoveryAttributeId)));
			else {
				 if (m_Profile.m_attributeSizeType==AnalysisProfile.ATTR_MAX_PONDERATION_SIZE)
					{
					//base=m_RadData.getMaxPondAttributesArray(m_Profile.m_AttributesMaxNb);
					 base.add(new Integer(m_RadData.getAttributeMaxPondAttribute()));
					}
				 else base.add(new Integer(m_RadData.getMaxBalancedRadiationPowerAttribute()));
				 }
			}

		if (base.size()>maxBaseSize)
		   base= (new ArrayList(base)).subList(0,maxBaseSize);

		// Create Statitics Array
		computeBaseRadiationStat(base);
		// Ne pas faire systematiquement - le faire sur le sous-ensemble pertinent dans la radiation
		// Si pas besoin de calculer la base
		ArrayList vArray = null;
		Iterator it = null;
		while ((base.size()<m_Profile.m_AttributesMaxNb) && (m_BaseRadiationStat.m_AttributesCntInRadiation < Math.min(2*m_Profile.m_AttributesMaxNb,
				m_RadData.getAttributesCnt()))) {
			if (vArray == null)
				vArray = computeInitialAttributesStat(base);
			it = vArray.iterator();
			while (it.hasNext()) {
				ObjectNumStat stat = ((ObjectNumStat)it.next());
//	La connexit� de la base n'est pas n�cessaire car il y a filtrage au niveau de la repr�sentation
// if (m_RadData.getRadiation(stat.m_Num, m_BaseRadiationStat)>0) // la base doit �tre connexe
					{
					   stat.m_values[0] = m_RadData.getExclusiveRadiation(stat.m_Num, m_BaseRadiationStat);
					}
			}

			if (vArray.size()!=0)
				{
					Collections.sort(vArray);
					base.add(new Integer(((ObjectNumStat)vArray.get(0)).m_Num));
					updateBaseRadiationStat(((ObjectNumStat)vArray.get(0)).m_Num);
					vArray.remove(0);
				}
			else break;
		}
		// Calculer ExclusiveRadiation uniquement pour les attributs n�cessaire (Radiation)
		m_BaseRadiationStat.m_AttributesStat = computeAttributesInRadiationStat(base,
				vArray);
		return  base;
	}

	/**
	 * * Compute the attributes to display on map
	 */
	private Collection computePlanAttributes (Collection base)  throws WPSConnectorException
	{
		TreeSet tree = new TreeSet();
		Collection attributes = new ArrayList();
		//Integer[] baseArray = (Integer[])base.toArray(new Integer[0]);
		Iterator it = m_BaseRadiationStat.m_AttributesStat.iterator();
		TreeSet exclusionAttributes=null;
		int num;

		if (m_Profile.m_AttributesMaxNb<=base.size())
			return attributes;

			iIdEnumerator enumvar = m_PlanRequest.getAnalysisProfile().getConnector(m_PlanRequest.m_Dictionary).getExclusionEnumerator(m_PlanRequest.m_entityId);

			// Construction de la la liste des attributs exclus
			if (enumvar.hasNext())
			   {
				exclusionAttributes= new TreeSet();
				for( String item : enumvar)
					{
						if (m_RadData.m_AttrConverter.contains(item)==true)
						   exclusionAttributes.add( new Integer(m_RadData.getNum(item)));
					}
			   }

		float temp[];
		int i;
		while (it.hasNext()) {
			ObjectNumStat stat = (ObjectNumStat)it.next();
			if ((exclusionAttributes==null) || (exclusionAttributes.contains(new Integer(stat.m_Num))==false))
			   {
				num = stat.m_Num; i=0;
				temp= new float[6];

				// On favorise le crit�re de pond�ration totale
				if (m_Profile.m_attributeSizeType==AnalysisProfile.ATTR_PONDERATION_SIZE)
				   temp[i++] = m_RadData.getAttributeSumPond(num);
				else if (m_Profile.m_attributeSizeType==AnalysisProfile.ATTR_MAX_PONDERATION_SIZE)
				   temp[i++] = m_RadData.getAttributeMaxPond(num);


				if (m_RadData.m_discoverRestrictAttrFlag)
				   {
				   temp[i++] = m_RadData.getRadiation(num,  m_BaseRadiationStat);
				   temp[i++] = m_RadData.getFrequency(num);
				   }
				else
					{
					temp[i++] = m_RadData.getBalancedRadiationSum(num, (Integer [])base.toArray(new Integer[0]));
					temp[i++] = m_RadData.getRadiation(num,  m_BaseRadiationStat);
					}
				temp[i++] = m_RadData.getBalancedRadiation(num, m_BaseRadiationStat);
				temp[i++] = (-stat.m_values[0]);

				if ((m_Profile.m_attributeSizeType!=AnalysisProfile.ATTR_PONDERATION_SIZE)&&(m_Profile.m_attributeSizeType!=AnalysisProfile.ATTR_MAX_PONDERATION_SIZE))
				   temp[i++] = m_RadData.getFrequency(num);

				temp[i++] = m_RadData.getBalancedRadiationPower(num);

				tree.add(new ObjectNumStat(num, temp));
			   }
		}

		if (tree.size() == 0)
			return  new ArrayList();

/* Si on a choisi certain �l�ment de la base
		if (Arrays.binarySearch(m_RadData.m_refEntityProfile, new NumAndFloat(num, 0)) >= 0)
				   base.add(new Integer());*/

		if (tree.size()<=(m_Profile.m_AttributesMaxNb-base.size()))
			return tree;
		else
			{
			Object[] array = tree.toArray();
			Collection attRet = tree.headSet(array[m_Profile.m_AttributesMaxNb-base.size()]);
			return  attRet;
			}
	}



	/** Compute the entities to display on map
	 *  called if AnalysisProcess.m_DisplayEntities = false (ie <displau-profile display-entities='no' )
	 */
	private Collection<ObjectStringStat> computePlanRefEntity(Collection<Integer> base, Collection<ObjectNumStat> attributes) {
		Collection<ObjectStringStat> ret = new ArrayList<ObjectStringStat>();
		if ((m_PlanRequest.m_entityId != null) && (m_Profile.m_planType != AnalysisProfile.DISCOVERY_PLAN))
			ret.add(new ObjectStringStat(m_PlanRequest.m_entityId, new float[0]));
		return ret;
	}


	/** Compute the entities to display on map
	 *  Called if m_DisplayEntities=true (ie <displayprofile display-entities='yes' > )
	 */
	private Collection<ObjectStringStat> computePlanEntities (Collection<Integer> base, Collection<ObjectNumStat> attributes)  throws WPSConnectorException
	{
		TreeSet<ObjectStringStat> tree = new TreeSet<ObjectStringStat>();
		// convert attibutes collection in array
		int aArray[] = new int[attributes.size()+base.size()];

		int i = 0; 
		for( Integer val : base)
			aArray[i++] = val;

		for( ObjectNumStat val2 : attributes)
			aArray[i++] = val2.m_Num;

		// Duplicate matrix
		SymmetricalMatrix radMat = new SymmetricalMatrix(m_RadData.m_RadiationMatrix, aArray);
		FSymmetricalMatrix balRadMat = new FSymmetricalMatrix(m_RadData.m_BalRadiationMatrix, aArray);

		// create StringAndFloat Collection to sort entities
		for( String name : m_Entities)
		{
			float temp[] = new float[2];
			temp[0] = m_RadData.getAverageRadiation( name, radMat, aArray);
			if (temp[0] != 0) {
				temp[1] = m_RadData.getAverageBalancedRadiation( name, balRadMat, aArray);
				tree.add(new ObjectStringStat(name, temp));
			}
		}
		if (tree.size() == 0)
			return new ArrayList<ObjectStringStat>();
		ObjectStringStat[] array = ( ObjectStringStat[])tree.toArray();

		Collection<ObjectStringStat> entRet = null;
		if (m_Profile.m_EntitiesMaxNb<array.length)
		   entRet = tree.headSet( array[m_Profile.m_EntitiesMaxNb]);
		else 
			entRet = tree;
		//  voir subset par rapport au TreeSet ?
		return  entRet;
	}

	/** Called if ProtoPlan.m_RecomInterface != null
	 * 
	 * @param base
	 * @param attributes
	 * @throws WPSConnectorException
	 */
	private void computeRecommendations (Collection<Integer> base, Collection<ObjectNumStat> attributes) throws WPSConnectorException
	{
		if( m_Profile.m_RecomProfiles[ RecommendationGroup.SATTRIBUTES_RECOM] == null)
			return; // No recommendation

		//Iterator it = m_Entities.iterator();
		int i=0; 
		// convert attibutes collection in array
		int aArray[] = new int[attributes.size()+base.size()];

		if (m_Profile.m_RecomProfiles[ RecommendationGroup.SATTRIBUTES_RECOM].m_RecommendationScale!=WPSDictionary.APPLY_TO_NOT_BASE)
	    {
			for( Integer val : base)
				aArray[i] = val;
	    }

		if (m_Profile.m_RecomProfiles[ RecommendationGroup.SATTRIBUTES_RECOM].m_RecommendationScale!=WPSDictionary.APPLY_TO_BASE)
		   {
			for( ObjectNumStat val2 : attributes)
				aArray[i++] = val2.m_Num;
		   }

		Arrays.sort(aArray);

		RecommendationProcess proc = m_RecomInterface.getRecommendationProcess();
		proc.setProfilesBuffer(m_RadData.m_Entities, m_RadData.m_AttrConverter);
		proc.compute(aArray);

	}


	private void createProtoPlan (Collection base, Collection attributes, Collection entities)  throws WPSConnectorException
	{
		//AnalysisProfile profile = m_PlanRequest.getAnalysisProfile();
		m_Plan = new ProtoPlan( m_PlanRequest );


		ProtoAttribute[] aArray = new ProtoAttribute[base.size() + attributes.size()];
		ProtoEntity[] eArray = new ProtoEntity[entities.size()];
		ArrayList eLinkArray = new ArrayList();
		ArrayList aLinkArray = new ArrayList();


		Iterator bIt = base.iterator();
		Iterator aIt = attributes.iterator();
		Iterator eIt = entities.iterator();
		int attributesNumToOffset[]= new int[m_RadData.getAttributesCnt()];
		Arrays.fill(attributesNumToOffset, Integer.MIN_VALUE);


		ProtoAttribute  a;
		String          aId;
		Collection      recommendations;
		int             i, j, k, aNum, jNum, kNum;
		int             size, weight, baseAttrMinSize=Integer.MAX_VALUE;
		boolean         isRef=(m_Profile.m_planType==AnalysisProfile.GLOBAL_PLAN)?false:true, isBase = true;


		// Base Attributes creation
		for (i = 0; bIt.hasNext(); ++i) {
			aNum        = ((Integer)bIt.next()).intValue();
			aId         = m_RadData.getString(aNum);

			if (m_Profile.m_planType==AnalysisProfile.PERSONAL_PLAN)
				isRef       = ((Arrays.binarySearch(m_RadData.m_refEntityProfile, new NumAndFloat(aNum, 0)) >= 0) ? true : false);
			else if (m_Profile.m_planType==AnalysisProfile.DISCOVERY_PLAN)
				{
				if ((i==0) && (m_PlanRequest.m_discoveryAttributeId!=null) && (aId.compareTo(m_PlanRequest.m_discoveryAttributeId)==0))
					isRef=true;
				else isRef=false;
				}

			if ((m_Profile.m_attributeSizeType!=AnalysisProfile.ATTR_PONDERATION_SIZE) && (m_Profile.m_attributeSizeType!=AnalysisProfile.ATTR_MAX_PONDERATION_SIZE))
			   {
				size        = m_RadData.getFrequency(aNum);
				weight      = m_RadData.getRadiationPower(aNum);      // � voir inertie puissance de rayonnement
			   }
			else  {
					size = (int)(m_RadData.getRefEntityPond(aNum)/m_RadData.m_MaxAttributePond*(float)m_RadData.m_maxFrequency)+1;
					if (size<baseAttrMinSize)
					   baseAttrMinSize=size;
					weight      = (int)(Math.pow(m_RadData.getRefEntityPond(aNum),2.0)/m_RadData.m_MaxAttributePond);
				  }

			a           = new ProtoAttribute(aId, isRef, isBase, size, weight, i);
			aArray[i]   = a;
			attributesNumToOffset[aNum]=i;

			// On fixe les recommandations pour chaque attributs
			if (m_RecomInterface!=null)
			   {
			   a.m_recomGroups= new RecommendationGroup[RecommendationGroup.RECOM_TYPE_CNT];
			   if ((recommendations=m_RecomInterface.getRecommendations(aId))!=null)
				  {
						   a.m_recomGroups[RecommendationGroup.SATTRIBUTES_RECOM]= new RecommendationGroup(aId, RecommendationGroup.SATTRIBUTES_RECOM);
						   a.m_recomGroups[RecommendationGroup.SATTRIBUTES_RECOM].setRecommendations(recommendations);
				  }
				a.m_recomGroups[RecommendationGroup.ENTITIES_RECOM]= new RecommendationGroup(aId, RecommendationGroup.ENTITIES_RECOM);
				a.m_recomGroups[RecommendationGroup.ENTITIES_RECOM].setRecommendations(new ArrayList());
			   }

			// On met � jour les bornes inf�rieures et sup�rieures de size et length
			m_Plan.m_bounds[ProtoPlan.A_ALLSIZE_BND].check( size );
		}



		// Compute link between base attributes
		float           value; int minBaseLinkLength=Integer.MAX_VALUE;
		AttributeLink   aLink;
		float baseLog=(float)MathLogBuffer.getLog(base.size()+1);

		for (j = 0; j < base.size(); ++j) {
			jNum = m_RadData.getNum(aArray[j].m_strId);
			for (k = j + 1; k < base.size(); ++k) {
				kNum            = m_RadData.getNum(aArray[k].m_strId);
//				value           = m_RadData.getBalancedRadiation(jNum, kNum);
//				if (value>((float)model.m_AttrLinkThreshold/100))
					size            = m_RadData.getRadiation(jNum, kNum);

					if (size!=0)
					   {
							value           = (float)(1.0-m_RadData.getBalancedRadiation(jNum, kNum))*(float) Math.log(m_RadData.getExclusiveRadiation(jNum, kNum)+base.size())/baseLog;
							weight          = (int)(value*(float)Integer.MAX_VALUE);  // distance
							aLink           = new AttributeLink();
							aLinkArray.add(aLink);
							aLink.m_length  = weight;
							aLink.m_size    = size;
							aLink.m_from    = aArray[j];
							aLink.m_to      = aArray[k];
							aArray[j].addLink(aLink);
							aArray[k].addLink(aLink);
							if (minBaseLinkLength>weight)
							   minBaseLinkLength=weight;
					   }
			}
		}

		// Verify always links in base
		for (j = 0; j < base.size(); ++j)
			{
			if (aArray[j].getLinkCount()==0)
			   for (k = 0; k < base.size(); ++k)
					  if (j!=k)
							{
							aLink           = new AttributeLink();
							aLinkArray.add(aLink);
							aLink.m_length  = minBaseLinkLength;
							aLink.m_from    = aArray[j];
							aLink.m_to      = aArray[k];
							aArray[j].addLink(aLink);
							aArray[k].addLink(aLink);
							}
				}




		// Compute attributes not in base
		ObjectNumStat aStat;
		isBase = false;
		isRef=false;
		for (
		/* keep older value of i */
		; aIt.hasNext(); ++i) {
			aStat       = (ObjectNumStat)aIt.next();
			aNum        = aStat.m_Num;
			aId         = m_RadData.getString(aNum);

			if (m_Profile.m_planType==AnalysisProfile.PERSONAL_PLAN)
				isRef       = ((Arrays.binarySearch(m_RadData.m_refEntityProfile, new NumAndFloat(aNum, 0)) >= 0) ? true : false);

			if ((m_Profile.m_attributeSizeType!=AnalysisProfile.ATTR_PONDERATION_SIZE)&&(m_Profile.m_attributeSizeType!=AnalysisProfile.ATTR_MAX_PONDERATION_SIZE))
			   {
				size        = m_RadData.getFrequency(aNum);
//			    weight      = m_RadData.getRadiationPower(aNum);      // � voir inertie puissance de rayonnement
// 			    weight      = (int)(aStat.m_values[0]);               //  � voir inertie rayonnement par rapport � la base
			   }
			else
				{
					if (isRef)
					   {
						size = (int)(m_RadData.getRefEntityPond(aNum)/m_RadData.m_MaxAttributePond*(float)m_RadData.m_maxFrequency)+1;
						if (size<baseAttrMinSize)
						  baseAttrMinSize=size;
					   }
					else size = Math.max(m_RadData.getFrequency(aNum), baseAttrMinSize);
				}
			weight = size;


			a           = new ProtoAttribute( aId, isRef, isBase, size, weight, i );
			aArray[i]   = a;
			attributesNumToOffset[aNum]=i;

			// On fixe les recommandations pour chaque attributs
			if (m_RecomInterface!=null)
			   {
			   a.m_recomGroups= new RecommendationGroup[RecommendationGroup.RECOM_TYPE_CNT];
			   if ((recommendations=m_RecomInterface.getRecommendations(aId))!=null)
				  {
						   a.m_recomGroups[RecommendationGroup.SATTRIBUTES_RECOM]= new RecommendationGroup(aId, RecommendationGroup.SATTRIBUTES_RECOM);
						   a.m_recomGroups[RecommendationGroup.SATTRIBUTES_RECOM].setRecommendations(recommendations);
				  }
				a.m_recomGroups[RecommendationGroup.ENTITIES_RECOM]= new RecommendationGroup(aId, RecommendationGroup.ENTITIES_RECOM);
				a.m_recomGroups[RecommendationGroup.ENTITIES_RECOM].setRecommendations(new ArrayList());
			   }

			m_Plan.m_bounds[ProtoPlan.A_ALLSIZE_BND].check( size );
		}


		// Compute link between  attributes
		for (k = 0; k < aArray.length; ++k) {
			kNum = m_RadData.getNum(aArray[k].m_strId);
			for (j = Math.max(base.size(), k + 1); j < aArray.length; ++j) {
				jNum            = m_RadData.getNum(aArray[j].m_strId);
				value           = ((float)1.0-(float)m_RadData.getBalancedRadiation(jNum, kNum));

				size            = m_RadData.getRadiation(jNum, kNum);
				if ((size!=0) /*&& (value<((float)model.m_AttrLinkThreshold/100))*/  ) { // pas de filtrage � ce niveau
				weight          = (int)((value)*(float)Integer.MAX_VALUE); //distance

				aLink           = new AttributeLink();
				aLinkArray.add(aLink);
				aLink.m_length  = weight;
				aLink.m_size    = size;
				aLink.m_from    = aArray[j];
				aLink.m_to      = aArray[k];
				aArray[j].addLink(aLink);
				aArray[k].addLink(aLink);
//				System.out.println(m_RadData.getString(jNum)+"/"+m_RadData.getString(kNum)+":"+weight);
				}
			}
		}

		// Compute entities
		ObjectStringStat eStat = null;
		for (i = 0; eIt.hasNext(); ++i) {
			eStat = (ObjectStringStat)eIt.next();
			String eId = eStat.m_Id;
			ProtoEntity e = new ProtoEntity( eId, ProtoEntity.NORMAL_ENTITY, i );
			eArray[i]=e;

			NumAndFloat[] prof = m_RadData.getNumericalProfile(eId);
			e.m_links = new EntityLink[prof.length];
			for (j = 0; j < prof.length; ++j) {
				if ((aNum=attributesNumToOffset[prof[j].m_num])!=Integer.MIN_VALUE)
				   {
						EntityLink eaLink = new EntityLink();
						eaLink.m_from = eArray[i] ; // entity offset in array
						eaLink.m_to = 	aArray[aNum]; //attribut offset in array
						eaLink.m_length = (int)(prof[j].m_value/(m_RadData.m_MaxAttributePond
								+ 1.0)*(float)Integer.MAX_VALUE);
						e.m_links[j] = eaLink;
						eLinkArray.add(eaLink);
				   }
			}
		}

		// Compute entities recommendations by attributes
		if( m_Profile.m_RecomProfiles[ RecommendationGroup.ENTITIES_RECOM] != null)
			{
				boolean applyNotToBase=(m_Profile.m_RecomProfiles[ RecommendationGroup.ENTITIES_RECOM].m_RecommendationScale==WPSDictionary.APPLY_TO_NOT_BASE);
				boolean applyToBaseOnly=(m_Profile.m_RecomProfiles[ RecommendationGroup.ENTITIES_RECOM].m_RecommendationScale==WPSDictionary.APPLY_TO_BASE);

				eIt = m_Entities.iterator();
				for (i = 0; eIt.hasNext(); ++i) {
					String eId = (String)eIt.next();
					NumAndFloat[] prof = m_RadData.getNumericalProfile(eId);
					for (j = 0; j < prof.length; ++j) {
						if ((aNum=attributesNumToOffset[prof[j].m_num])!=Integer.MIN_VALUE)
						   {
							isBase=aArray[aNum].isBase();
							if ((isBase && !applyNotToBase) || (!isBase && !applyToBaseOnly))
							  aArray[aNum].m_recomGroups[RecommendationGroup.ENTITIES_RECOM].m_recommendations.add(eId); //attribut offset in array
						   }
					}
				}
			}

		attributesClustering(aLinkArray, aArray, base);

		attributeLinksFiltering(aLinkArray, aArray, base);

		attributesClustering(aLinkArray, aArray, base);

		// Clean All invalid link in attributes and compute max min
		cleanInvalidLinksInAttributes(aArray, true);

		// Clean Invalid links and suppress Zero Links
		cleanInvalidLinks(aLinkArray, true);

		// Set ProtoPlan
		m_Plan.m_attributes = aArray;
		m_Plan.m_entities = eArray;
		m_Plan.m_attLinks = (AttributeLink [])aLinkArray.toArray(new AttributeLink[0]);
		m_Plan.m_entLinks = (EntityLink [])eLinkArray.toArray(new EntityLink[0]);


	}


private boolean doClustering(ProtoAttribute child, ProtoAttribute parent)  throws WPSConnectorException
{
	int MaxAttributesPerCluster= m_Profile.m_MaxAttributesPerCluster;

	if ((parent == null) || (parent.isRef()!=child.isRef()) || ((parent.getChildrenCount()+child.getChildrenCount())>=(MaxAttributesPerCluster-1)))
		return false;

	// Traitement du cas particulier des informations n'appartenant qu'� notre user
	if ((m_Profile.m_SelfClusteringProperty != null)&& (m_PlanRequest.m_entityId!=null))
		if (((child.m_size==1) && (parent.m_size==1)) && (parent.isBase())) // Si il appartient � un seul utilisateur & dans la base
			{
			iProfileConnector	iProfile=m_PlanRequest.getAnalysisProfile().getConnector( m_PlanRequest.m_Dictionary);

			Hashtable childProps =iProfile.getAnalysisProperties(child.m_strId, m_PlanRequest.m_entityId);
			Hashtable parentProps =iProfile.getAnalysisProperties(parent.m_strId, m_PlanRequest.m_entityId);

			Object prop1= childProps.get( m_Profile.m_SelfClusteringProperty);
			Object prop2= parentProps.get( m_Profile.m_SelfClusteringProperty);

			if ((prop1!=null) && (prop2!=null) && (!prop1.equals(prop2)))
				{
				return (false); // Si la propri�t� n'est pas �gale alors on ne clusterise pas
				}
			}


		child.setParent(parent, m_Profile.m_attributeSizeType);

		++m_clusterCnt;

		if (parent.isBase())
			   ++m_clusterCntInBase;

		m_Plan.m_bounds[ProtoPlan.A_ALLSIZE_BND].check( (parent.m_parent==null)?parent.m_size:parent.m_parent.m_size );
		return  true;
}

private ProtoAttribute chooseParent(ProtoAttribute attr1, ProtoAttribute attr2)
{
if( attr1.m_parent!=null && attr1.m_parent == attr2)
	return null;
if( attr2.m_parent!=null && attr2.m_parent == attr1)
	return null;
float size1=Float.MIN_VALUE, size2=Float.MIN_VALUE;

if ((m_Profile.m_parentClustering==AnalysisProfile.PERSONAL_POND_CLUSTERING) && (attr1.isRef()) && (attr2.isRef()) )
	{
		size1=m_RadData.getRefEntityPond(attr1.m_strId);
		size2=m_RadData.getRefEntityPond(attr2.m_strId);
	}

if ((size1==Float.MIN_VALUE) || (size2==Float.MIN_VALUE))
	{
		size1=(float)attr1.m_size;
		size2=(float)attr2.m_size;
	}

if (size1>size2)
	return attr2.m_parent!=null?null:attr1;
else if (size1==size2)
	{
		if (attr1.m_weight>attr1.m_weight)
			return attr2.m_parent!=null?null:attr1;
		else if (attr1.m_weight==attr1.m_weight)
			{
				if (attr2.m_parent==null)
					return attr1;
				else if (attr1.m_parent==null)
					return attr2;
				else return null;
				//if (attr1.m_num<attr2.m_num)
					//return attr1;
			}
	}
return attr1.m_parent!=null?null:attr2;
}


private void attributesClustering(ArrayList aLinkArray, ProtoAttribute [] aArray, Collection base)  throws WPSConnectorException
{
	attributesClustering(aLinkArray, aArray, base,(float)(m_Profile.m_DataClusterThreshold/100.0), false);
}


private void attributesClustering(ArrayList aLinkArray, ProtoAttribute [] aArray, Collection base, float threshold, boolean baseOnly)  throws WPSConnectorException
{
	if( !m_Profile.m_DoClustering) return; // FRV : No Clustering

	float baseThreshold, endThreshold;
	//int clusterCntInBase=0;
	int attributesMaxNb=m_Profile.m_AttributesMaxNb;
	double clusterCoef=(double)m_Profile.m_DataClusterLevel/100.0; /* 2 attributes for 1 node */

//	System.out.println("BASICCLUSTER");
	while (basicAttributesClustering(aLinkArray,    baseOnly));

	// Si la base est trop grande et pas assez clusteris�
//	System.out.println("BASECLUSTER");
	baseThreshold=0;
	while ( ((base.size()-m_clusterCntInBase-4)>(int)((float)base.size()*clusterCoef*0.7)) &&  ((baseThreshold+=(threshold/10.0))<(threshold)))
			  {
				globalAttributesClustering( aArray,  baseThreshold, true);
			  }

	// Introduire une clusterisation en fonction du nombre d'objet visibles ?
//	System.out.println("nonBASECLUSTER");
	baseThreshold=0;

	// Pour les discovery plan on augmente le seuil pour autoriser une clusterisation plus forte
	if (m_Profile.m_planType==AnalysisProfile.DISCOVERY_PLAN)
		endThreshold=(threshold+(float)0.2);
	else endThreshold= threshold;

	while (((aArray.length-m_clusterCnt)>(int)((float)attributesMaxNb*clusterCoef)) &&  ((baseThreshold+=(endThreshold/10.0))<endThreshold))
		while (globalAttributesClustering( aArray,   baseThreshold,  baseOnly));


	}

private boolean basicAttributesClustering(ArrayList aLinkArray,   boolean baseOnly)  throws WPSConnectorException
{
	//int MaxAttributesPerCluster= m_Profile.m_MaxAttributesPerCluster;
	int clusterCnt=m_clusterCnt;
	ProtoAttribute parent, attr1, attr2;
	AttributeLink link;

	for( ListIterator it=aLinkArray.listIterator(); it.hasNext(); )
	{
		link=(AttributeLink)it.next();

		attr1= (ProtoAttribute)link.m_from;
		attr2= (ProtoAttribute)link.m_to;
		parent = chooseParent(attr1, attr2);
		if (parent == null) continue;

		if( m_Profile.m_ForceClusteringProperty != null)
		{   // FRV : Force clutering
			iProfileConnector	iProfile=m_PlanRequest.getAnalysisProfile().getConnector( m_PlanRequest.m_Dictionary);

			Hashtable childProps = iProfile.getAnalysisProperties(attr1.m_strId, m_PlanRequest.m_entityId);
			Hashtable parentProps = iProfile.getAnalysisProperties(attr2.m_strId, m_PlanRequest.m_entityId);

			Object prop1= childProps.get( m_Profile.m_ForceClusteringProperty);
			Object prop2= parentProps.get( m_Profile.m_ForceClusteringProperty);

			if((prop1!=null) && (prop2!=null) && prop1.equals(prop2))
				doClustering( (parent == attr1) ? attr2 : attr1, parent);
		}
		else
		{   // Standard case : clusterisation si lien complet
			if(link.isDisplay() && link.m_length==0)
			{
//			   System.out.println("L:"+link.m_to.m_strId+"/"+link.m_from.m_strId+":"+link.m_length);
			   if (link.isLinkBetweenBase() || ((!link.isLinkRelatedToBase()) && !baseOnly))
				  doClustering( (parent == attr1) ? attr2 : attr1, parent);
			}
		  }
	}
	return (clusterCnt != m_clusterCnt) ? true : false;
	}


private boolean globalAttributesClustering(ProtoAttribute [] aArray, float threshold, boolean baseOnly)  throws WPSConnectorException
	{
	int MaxAttributesPerCluster= m_Profile.m_MaxAttributesPerCluster;
	int clusterCnt=m_clusterCnt;
	boolean isBetweenBase;
	int minLength, currentWeight;
	Collection parentList= new ArrayList();
	AttributeLink link;
	ProtoAttribute parent, attr, bestParent;

	ListIterator it;

		// Clustering of attributes
		for (int i=aArray.length-1; i>=0; --i)
		   {
			parent=bestParent=null;
			attr=aArray[i];
			parentList.clear();
			minLength=Integer.MAX_VALUE;
			currentWeight=Integer.MIN_VALUE;

			if (attr.m_parent!=null) // deja clusteris�
			   continue;

			it=attr.getLinkIterrator();

			while (it.hasNext())
				  {
					link=(AttributeLink)it.next();
					if (!link.isDisplay())
					   continue;
//			           System.out.println(attr.m_strId+":L:"+link.m_to.m_strId+"/"+link.m_from.m_strId+":"+link.m_length+":"+link.m_size+":"+((ProtoAttribute)link.m_to).m_weight+":"+((ProtoAttribute)link.m_to).m_size+":"+((ProtoAttribute)link.m_from).m_weight+":"+((ProtoAttribute)link.m_from).m_size);

					isBetweenBase=link.isLinkBetweenBase();
					if (baseOnly && !(isBetweenBase))
					   break;

					// Si lien en dessous du seuil, peut �tre p�re
					if (link.m_length< (int)((float)Integer.MAX_VALUE*threshold))
					   {
						// if two attributes in base or two attributes not in base
						if ((isBetweenBase &&  baseOnly) || ((!link.isLinkRelatedToBase()) && !baseOnly))
						   {
							parent=chooseParent((ProtoAttribute)link.m_from, (ProtoAttribute)link.m_to);

							if (parent==attr)  // On cherche � clusteriser attr, pas l'inverse
								parent=null;

							if ((parent!=null) && ((parent.getChildrenCount()+attr.getChildrenCount())<(MaxAttributesPerCluster-1)))
								   {
								   if ((link.m_length<minLength) || ((link.m_length==minLength) && (parent.m_weight>currentWeight)) )
									  {
										minLength=link.m_length;
										currentWeight=parent.m_weight;
										bestParent=parent;
									  }
									parentList.add(parent);
								   }
							}
						}
					}

					it=null;
					if (parentList.size()!=0)
					   if (checkParentClustering(parentList, threshold))
						  doClustering( attr, bestParent);
		   }

	return (clusterCnt!=m_clusterCnt)?true:false;
	}


	// Check if parent
	private boolean checkParentClustering( Collection list, float threshold)
	{

		if (list.size()==1)
		   return true;

		AttributeLink link;
		ProtoAttribute attr1, attr2;


		Iterator it1 =list.iterator();
		Iterator it2 =list.iterator();

		while (it1.hasNext())
			  {
				attr1=(ProtoAttribute)it1.next();

				while (it2.hasNext())
					  {
						attr2=(ProtoAttribute)it2.next();

						if (attr1==attr2)
						   break;

						if ((link=attr1.findLink(attr2))!=null)
						   if (link.m_length>= (int)((float)Integer.MAX_VALUE*threshold))
							  return false;
					  }
			  }

		return true;
	}




	// Filtering attribute links
	private void attributeLinksFiltering(ArrayList aLinkArray, ProtoAttribute [] aArray, Collection base)
	{
		int filteredLinkCnt=0;
		ProtoAttribute attr;
		ListIterator it;
		AttributeLink link;


		// filtering by attribute
		// limit the number of links by attribute
		int cntToDelete;
		for (int i=0; i<aArray.length; ++i)
		{
			attr=aArray[i];

			if (aArray[i].isBase())
			   cntToDelete = attr.getLinkCount()-(int)(((float)aArray.length/(float)base.size()+5.0)*(float)m_Profile.m_LinksPerAttributeThreshold);
		   else
			   cntToDelete = attr.getLinkCount()-(5*m_Profile.m_LinksPerAttributeThreshold);

		   if (cntToDelete>0)
		   {
			   Collections.sort(attr.m_links);
			   it = attr.m_links.listIterator(attr.m_links.size());
			   while (it.hasPrevious() && (cntToDelete>0))
			   {
					link=(AttributeLink)it.previous();
					if (!link.isDisplay())
					   continue;

					if (link.isLinkBetweenBase())
					   break;

					if ((!link.isLastLinkRelatedToBase()) && (!link.isLastLinksNotInBase()) )
					{
						if ((((ProtoAttribute)link.m_from).m_links.size()>m_Profile.m_LinksPerAttributeThreshold) && (((ProtoAttribute)link.m_to).m_links.size()>m_Profile.m_LinksPerAttributeThreshold))
						{
							link.setFiltered();
							++filteredLinkCnt;
							// System.out.println("CLATTR:"+link.m_from.m_num+"/"+link.m_to.m_num);
						}
					}
					--cntToDelete;
				}
			}
		}
		cleanInvalidLinks(aLinkArray, false);

		// global filtering
		// limit the global number of link
		Collections.sort(aLinkArray);
		int threshold= m_Profile.m_LinksPerAttributeThreshold;
		int baseAttributeLinksCnt=getBaseAttributeLinksCnt(aLinkArray);
		int offset=(int)((float)(m_Profile.m_LinksPerAttributeThreshold-1)*(float)(base.size()-m_clusterCntInBase)/2.0);
		int cnt1, cnt2;

		if (offset<aLinkArray.size())
		{
			it=aLinkArray.listIterator(offset);

			// filtering of base links
			while(it.nextIndex()<(baseAttributeLinksCnt-offset))
			{
				link=(AttributeLink)it.next();
				if (!link.isDisplay())
				   continue;

				// Filtering attribute links of base
				if (link.isLinkBetweenBase())
				{
					cnt1=((ProtoAttribute)link.m_from).getBaseLinkCount();
					cnt2=((ProtoAttribute)link.m_to).getBaseLinkCount();
					if ((cnt1> threshold) && (cnt2>threshold))
					{
						link.setFiltered();
						++filteredLinkCnt;
						//	System.out.println("CLBASE:"+link.m_from.m_num+"/"+link.m_to.m_num);
						continue;
					}
				}
				else break;
			}
		}

		// Filtering of attributes not in base
		it=aLinkArray.listIterator(aLinkArray.size());
		while(it.hasPrevious())
		{
			link=(AttributeLink)it.previous();
			if (!link.isDisplay())
			   continue;
			if (link.isLinkBetweenBase())
			   break;
			if ((((ProtoAttribute)link.m_from).getLinkCount()>m_Profile.m_LinksPerAttributeThreshold) && (((ProtoAttribute)link.m_to).getLinkCount()>m_Profile.m_LinksPerAttributeThreshold))
			   if ((!link.isLastLink()) && (!link.isLastLinkRelatedToBase()) && (!link.isLastLinksNotInBase()) )
			   {
				   link.setFiltered();
				   ++filteredLinkCnt;
			   }

			if ((filteredLinkCnt)>(aLinkArray.size()-aArray.length*m_Profile.m_LinksPerAttributeThreshold))
			   break;
		}
	}

private int getBaseAttributeLinksCnt(ArrayList aLinkArray)
		{
		int cnt=0;
		AttributeLink link;

		ListIterator it=aLinkArray.listIterator();
		while(it.hasNext())
			{
			link=(AttributeLink)it.next();
			if (link.isLinkBetweenBase())
				++cnt;
			else break;
			}
		return cnt;
		}



private void cleanInvalidLinksInAttributes(ProtoAttribute [] aArray, boolean computeMaxMin)
		{
		AttributeLink link;
		ProtoAttribute attr;
		//ProtoAttribute parent;
		ListIterator it;


		// Clean All invalid link in attributes
		for (int i=0; i<aArray.length; ++i)
		   {
			attr=aArray[i];
			//parent=null;

			if (m_Profile.m_attributeSizeType==AnalysisProfile.ATTR_SAME_SIZE)
			   attr.m_size=(int)m_Plan.m_bounds[ProtoPlan.A_ALLSIZE_BND].m_max;

			it=attr.getLinkIterrator();
			while (it.hasNext())
				  {
				  link=(AttributeLink)it.next();
				  if (!link.isValid())
					 it.remove();
				  else if (computeMaxMin)
					   {
						m_Plan.m_bounds[ProtoPlan.L_ALLLENGTH_BND].check( link.m_length );
					   }
				  }

		   }
		}

// Clean all invalid links from link collection and modify zero links
private void cleanInvalidLinks(ArrayList aLinkArray, boolean cleanZeroLinks)
  {
		AttributeLink link;
		ListIterator it=aLinkArray.listIterator();
		boolean zeroExist=false;
		Bounds	linkLenBnd	= m_Plan.m_bounds[ProtoPlan.L_ALLLENGTH_BND];

		while(it.hasNext())
			{
			link=(AttributeLink)it.next();
			if (!link.isValid())
				it.remove();

			if ((cleanZeroLinks) && (link.m_length==0))
				{
				link.m_length=(linkLenBnd.m_min<=1)?1:((int)linkLenBnd.m_min-1);
				if (!zeroExist)
					{
					linkLenBnd.check( link.m_length );
					zeroExist=true;
					}
				}
			}
	}


// Rescale attribute size for cluster (ponderationSize)
/*private void rescalePonderAttributeSize(ProtoAttribute [] aArray)
		{
		ProtoAttribute attr;
		int childrenCnt;

		// Clean All invalid link in attributes
		for (int i=0; i<aArray.length; ++i)
		   {
			attr=aArray[i];

			if ((childrenCnt=attr.getChildrenCount())!=0)
				{
				attr.m_size	= (int)((float)attr.m_size/(float)(childrenCnt+1));
				}

		   }
		}*/


/*private void dumpPlan(ProtoPlan plan)
{
ProtoAttribute [] aArray = plan.m_attributes;
ProtoAttribute attr;
AttributeLink link;
Iterator it;
int i;
FileOutputStream file;

try{
	file =new FileOutputStream("c:/temp/dump"+System.currentTimeMillis()+".txt");


PrintStream prStr=new PrintStream(file);


		// Dump attributes Array
		prStr.println("/////ATTRIBUTES");
		int noLinkCnt=0;
		for (i=0; i< aArray.length; ++i)
			{
			prStr.print((aArray[i].isBase())?"Base:":"");
			prStr.println(aArray[i].m_strId);

			if (aArray[i].m_parent!=null)
			   {
			   prStr.println("Parent="+aArray[i].m_parent.m_strId);

				if (aArray[i].m_links.size()!=0)
				  prStr.println("INVALID CHILD:");

				if ((aArray[i].m_parent.m_children==null) || (aArray[i].m_parent.m_children.size()==0))
				   prStr.println("INVALID PARENT:");
			   }

			if (aArray[i].m_children!=null)
				{
				prStr.print("Children=");
				it=aArray[i].m_children.iterator();
				while(it.hasNext())
					{
					attr=(ProtoAttribute)it.next();
					prStr.print(":"+attr.m_strId);
					}
				prStr.println(":");
				}

			if ((aArray[i].m_links!=null)&&(aArray[i].m_links.size()!=0))
				{
				prStr.print("All Links=");
				it=aArray[i].m_links.iterator();
				while(it.hasNext())
					{
					link=(AttributeLink)it.next();
					prStr.print(":"+(((ProtoAttribute)link.m_to).isBase()?"B":"")+link.m_to.m_strId+"/"+(((ProtoAttribute)link.m_from).isBase()?"B":"")+link.m_from.m_strId);
					}
				prStr.println(":");
				}

			if ((aArray[i].m_links!=null)&&(aArray[i].m_links.size()!=0))
				{
				prStr.print("Non Filtered Links=");
				it=aArray[i].m_links.iterator();
				while(it.hasNext())
					{
					link=(AttributeLink)it.next();
					if (!link.isFiltered())
					prStr.print(":"+(((ProtoAttribute)link.m_to).isBase()?"B":"")+link.m_to.m_strId+"/"+(((ProtoAttribute)link.m_from).isBase()?"B":"")+link.m_from.m_strId);
					}
				prStr.println(":");
				}

			if ((aArray[i].getLinkCount()==0) && (aArray[i].m_parent==null))
			   ++noLinkCnt;


			}

		int ilCnt=0;
		int fCnt=0;

		// Dump Links array
		prStr.println("/////ATTRIBUTES LINKS");
		for (i=0; i< plan.m_attLinks.length; ++i)
			{
			if (m_Plan.m_attLinks[i].isFiltered())
				   {
					prStr.print("Filtered Link:");
					fCnt++;
				   }
		prStr.println(m_Plan.m_attLinks[i].m_to.m_strId+"/"+m_Plan.m_attLinks[i].m_from.m_strId+":"+m_Plan.m_attLinks[i].m_length);


		if ((((ProtoAttribute)(plan.m_attLinks[i].m_to)).m_parent!=null) || (((ProtoAttribute)(plan.m_attLinks[i].m_from)).m_parent!=null))
		   prStr.println("INVALID CHILD:");

			if (!m_Plan.m_attLinks[i].isValid())
			   ++ilCnt;
//			else if ((((ProtoAttribute)m_Plan.m_attLinks[i].m_to).m_parent!=null) || (((ProtoAttribute)m_Plan.m_attLinks[i].m_from).m_parent!=null))
//				   prStr.println("Parent Link:"+m_Plan.m_attLinks[i].m_to.m_strId+"/"+m_Plan.m_attLinks[i].m_from.m_strId);
			}
		prStr.println("Filtered Links Cnt:"+fCnt+"/"+m_Plan.m_attLinks.length);

		if (ilCnt!=0)
		  prStr.println("Invalid Links:"+ilCnt);


		if (noLinkCnt!=0)
		  prStr.println("Attribute with no link:"+noLinkCnt);

		  }
		catch( FileNotFoundException e) {}
	}*/



	// Test method
	public static void main (String[] args) {
		/*WPSDictionary dic = WPSDictionary.CreateTestInstance( "Toto");
		 dic.openConnections();*/
		try {
			WPSDictionary dic = new WPSDictionary("TEST");
			PlanRequest pr = new PlanRequest(null, dic, new Hashtable());
			Collection coll = new ArrayList();
			pr.m_entityId = "1";
			//pr.m_Dictionary.m_AttributesMaxNb=400;
			//pr.m_Dictionary.m_EntitiesMaxNb=70;
			int size = 2000;
			for (int i = 0; i < size; ++i)
				coll.add((new Integer(i)).toString());
			long t1 = System.currentTimeMillis();
			//AnalysisProcess data = new AnalysisProcess(pr, coll);
			//ProtoPlan plan = data.getProtoPlan();
			long t2 = System.currentTimeMillis();
			//data = new AnalysisProcess(pr, coll);
			//plan = data.getProtoPlan();
			long t3 = System.currentTimeMillis();
			//data = new AnalysisProcess(pr, coll);
			//plan = data.getProtoPlan();
			long t4 = System.currentTimeMillis();
			System.out.println("Time:" + (t2 - t1));
			System.out.println("Time:" + (t3 - t2));
			System.out.println("Time:" + (t4 - t3));
			//System.out.println("ACnt"+m_RadData.getAttributesCnt());
			//System.out.println("Value"+m_RadData.getRadiation(1,46));
		}
		catch( Exception  e)
		{
			e.printStackTrace();
		}
	}
}
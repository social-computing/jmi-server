package com.socialcomputing.wps.server.analysisengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.utils.FSymmetricalMatrix;
import com.socialcomputing.wps.server.utils.MathLogBuffer;
import com.socialcomputing.wps.server.utils.NumAndFloat;
import com.socialcomputing.wps.server.utils.ObjectNumStat;
import com.socialcomputing.wps.server.utils.StringAndFloat;
import com.socialcomputing.wps.server.utils.StringIdSymMatrix;
import com.socialcomputing.wps.server.utils.StringToNumConverter;
import com.socialcomputing.wps.server.utils.SymmetricalMatrix;
import com.socialcomputing.wps.server.webservices.PlanRequest;

/**
 * All the data which are necessary for the analysis engine */
public class RadiationData
{

	/**
	 * Entities which are used to creation analysis data
	 * key=string;
	 * object=profile (NumAndFloat []) */
	public HashMap m_Entities=null;

	private PlanRequest m_PlanRequest = null;
	private boolean m_discoverAddFlag = false;
	public boolean m_discoverRestrictAttrFlag = false;

	TreeSet m_discoAttrSet= new TreeSet();

// Matrix containing statistics which are computed at initialization
	public StringIdSymMatrix m_RadiationMatrix=null;
	public FSymmetricalMatrix m_BalRadiationMatrix=null;
	public FSymmetricalMatrix m_ExRadiationMatrix=null;

// Vectors containing statistics which are computed at initialization
	float[] m_BalancedRadiationPower=null;
	float[] m_ExclusiveRadiationPower=null;
	float[] m_AttributesPondSum=null;
	float[] m_AttributesPondMax=null;
	int[] m_Frequency=null;

	int m_attributesCnt=0;
	int m_maxFrequency=(-1);
	int m_maxAttributesCnt=(-1); // max attributes cnt in a profile
	NumAndFloat [] m_refEntityProfile= null;

// Convert Table between String and Numerical ids of attibutes
	public StringToNumConverter m_AttrConverter=new StringToNumConverter();

// Max ponderation computed at initialization
	public float m_MaxAttributePond=0;


// Test method
	public static void main(String [] args)
	{
		try {
			WPSDictionary dic=new WPSDictionary("TEST");
			PlanRequest pr=new PlanRequest(null,  dic, new Hashtable());
			Collection coll= new ArrayList();
			pr.m_entityId = "1";
			int size=100;

			for (int i=0; i<size; ++i)
			{
				coll.add((new Integer(i)).toString());
			}
			long t1= System.currentTimeMillis();

			RadiationData data= new RadiationData(pr, coll);

			long t2= System.currentTimeMillis();

			System.out.println("Time:"+(t2-t1));
			System.out.println("ACnt"+data.getAttributesCnt());
			System.out.println("Value"+data.m_RadiationMatrix.getAt(1,46));
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
	}




	/**
	 */
	public  RadiationData( PlanRequest planRequest, Collection entities )  throws WPSConnectorException
	{
		int lastNumOfRequestingEntity=0;

		m_Entities=new HashMap();
		m_PlanRequest=planRequest;
		NumAndFloat[] profile=null;

		// Read All The profiles and store it in Hashtable
		m_Frequency= new int[1000];

		// Initialisation de l'ajout de l'attribut de decouverte
		m_discoverAddFlag=((m_PlanRequest.getAnalysisProfile().m_planType==AnalysisProfile.DISCOVERY_PLAN)&&
						   (m_PlanRequest.m_discoveryAttributeId.compareTo("")!=0) &&
						   m_PlanRequest.getAnalysisProfile().m_AttributesBaseMaxNb!=AnalysisProfile.NO_DISCOVERY_ADD_IN_PROFILE)?true:false;

		m_discoverRestrictAttrFlag=((m_PlanRequest.getAnalysisProfile().m_planType==AnalysisProfile.DISCOVERY_PLAN)&&
									(m_PlanRequest.m_discoveryAttributeId.compareTo("")!=0) &&
									m_PlanRequest.getAnalysisProfile().m_AttributesBaseMaxNb==AnalysisProfile.NO_DISCOVERY_ADD_IN_PROFILE)?true:false;

		if ((m_PlanRequest.m_entityId!=null))
		{
			m_refEntityProfile=getNumericalProfile(m_PlanRequest.m_entityId);
			// memorisation pour filtrage
			lastNumOfRequestingEntity=(m_attributesCnt-1);
		}

		Iterator it = entities.iterator();
		while (it.hasNext())
		{
			getNumericalProfile((String)it.next());
		}

		// Filtering of attributes
		filteringAttributes( lastNumOfRequestingEntity+1);
		m_Frequency = null;

		// On r�cup�re le profil de r�ference apr�s filtrage
		if (m_refEntityProfile!=null)
		{
			m_refEntityProfile=(NumAndFloat[])getNumericalProfile(m_PlanRequest.m_entityId).clone();
			Arrays.sort(m_refEntityProfile);
		}


		// Creation of matrix
		m_RadiationMatrix= new StringIdSymMatrix(m_AttrConverter, true);
		m_AttributesPondSum= new float[getAttributesCnt()];
		m_AttributesPondMax= new float[getAttributesCnt()];

		// Initialization of radiation matrix
		int i, j,num; float value;
		it = entities.iterator();
		while (it.hasNext())
		{
			profile=getNumericalProfile((String)it.next());
			for (i=0; i< profile.length; ++i)
			{
				num=profile[i].m_num;
				value=profile[i].m_value;
				m_AttributesPondSum[num]+=value;

				if(value>m_AttributesPondMax[num])
					m_AttributesPondMax[num]=value;

				for (j=i; j< profile.length; ++j)
					m_RadiationMatrix.incrAt(num,profile[j].m_num);
			}
		}

		// initialization of frequency vector
		int [] iVector= new int[getAttributesCnt()];
		for (i=0; i < iVector.length; ++i)
		{
			if ((iVector[i]=getFrequency(i)) > m_maxFrequency)
			   m_maxFrequency=iVector[i];
		}
		m_Frequency= iVector;


		// initialization of matrix of balanced radiation
		FSymmetricalMatrix matrix = new FSymmetricalMatrix(getAttributesCnt(), false);
		for (i=0; i<matrix.size(); ++i)
			for (j=i/*+1*/; j<matrix.size(); ++j)
				matrix.setAt(i,j,getBalancedRadiation(i,j));
		m_BalRadiationMatrix= matrix;

		// Allocation of matrix of exclusive radiation
		// Data are stored in matrix at the first call to GetExclusiveRadiation
		matrix = new FSymmetricalMatrix(getAttributesCnt(), true);
		m_ExRadiationMatrix= matrix;

		// initialization of vector of balanced radiation power
		float [] vector= new float[getAttributesCnt()];
		for (i=0; i<vector.length; ++i)
		{
			vector[i]=getBalancedRadiationPower(i);
		}
		m_BalancedRadiationPower= vector;
	}


	/**
	 *  */
	public  final int addAttribute( String id )
	{
		int num=m_AttrConverter.add(id);
		int newSize=m_AttrConverter.size();

		if (m_attributesCnt<newSize)
			m_attributesCnt=newSize;

		if (num<m_Frequency.length)
		   ++m_Frequency[num];
		else
		{
			int newArray []=new int[m_Frequency.length+1000];
			System.arraycopy(m_Frequency/*src*/, 0,  newArray/*dest*/, 0, m_Frequency.length);
			m_Frequency=newArray;
		}
		return num;
	}


	private final void filteringAttributes(int numToStartFiltering) throws WPSConnectorException
	{
		int target=Math.min(15*m_PlanRequest.getAnalysisProfile().m_AttributesMaxNb, 1000);

		if (m_attributesCnt>target)
		{
			int toDelete= m_attributesCnt-target;
			StringToNumConverter newAttrConverter=new StringToNumConverter();
			int[] convertNumTable= new int[m_attributesCnt];
			int num, i;  String id;

			// Construction du nouveau converter
			for (i=m_attributesCnt-1; i>=0; --i)
			{
				if ((toDelete==0) || (m_Frequency[i]>1) || (i<numToStartFiltering))
				{
					num=newAttrConverter.add(m_AttrConverter.getString(i));
					convertNumTable[i]=num;
				}
				else {
					--toDelete;
					convertNumTable[i]=Integer.MIN_VALUE;
				}
			}

			// Cr�ation des nouveaux profils renum�rot�s
			Iterator it=m_Entities.entrySet().iterator();
			Collection numAttributes; Map.Entry entry;
			HashMap newEntities=new HashMap();
			NumAndFloat [] profile;

			while (it.hasNext())
			{
				numAttributes= new ArrayList();
				entry=(Map.Entry)it.next();
				id=(String)entry.getKey();
				profile=(NumAndFloat [])entry.getValue();

				for (i=0; i< profile.length; ++i)
					if ((num=convertNumTable[profile[i].m_num])!=Integer.MIN_VALUE)
					   numAttributes.add(new NumAndFloat(num, profile[i].m_value));

				it.remove();
				newEntities.put(id, (NumAndFloat [])(numAttributes.toArray(new NumAndFloat[0])));
			}

			m_Entities=newEntities;
			m_AttrConverter = newAttrConverter;
			m_attributesCnt=m_AttrConverter.size();
		}
	}





	/**
	 *  */
	public  final int getAttributesCnt(  )
	{
		return m_attributesCnt;
	}
	/**
	 * */
	public  final float getBalancedRadiation( int i,int j )
	{
		if (m_BalRadiationMatrix!=null)
		   return m_BalRadiationMatrix.getAt(i,j);
		else
		{
			 int freqI=getFrequency( i), freqJ=getFrequency( j);
			 if ((freqI==0) || (freqJ==0))
				return 0.0F;
			int minFreq=Math.min(  freqJ,  freqI);
			int maxFreq=Math.max(  freqJ,  freqI);

			// Fr�quence maximale ?? sur tous les �l�ments
			float value =  (float)getRadiation( i, j) / (float)minFreq   *(float)MathLogBuffer.getLog(minFreq+m_maxFrequency)/(float)MathLogBuffer.getLog(maxFreq+m_maxFrequency);
			// On donne un avantage � ceux qui ont des fr�quences respectives proches
			return  value;
			//		return  (float)getRadiation( i, j) / (float)minFreq;
		}
	}

	/**
	 */
	public  final float getBalancedRadiation( int i, Integer [] j )
	{
		int frequency=0, radiation=0;

		for (int ix=0; ix<j.length; ix++)
		{
			radiation+=getRadiation(i,j[ix].intValue());
			frequency=Math.max( getFrequency(j[ix].intValue()), frequency);
		}

		return  (float)radiation/ (float)Math.min(  getFrequency( i), frequency);
	}

	public  final float getBalancedRadiationSum( int i, Integer [] j )
	{
		float radiation=(float)0.0;

		for (int ix=0; ix<j.length; ix++)
		{
			radiation+=getBalancedRadiation(i,j[ix].intValue());
		}

		return  radiation;
	}

	/**
	 */
	public  final float getBalancedRadiation( int i, AnalysisProcess.BaseRadiationStat stat )
	{
		return  (float)stat.m_BaseRadiation[i]/ (float)Math.min(  getFrequency( i), stat.m_MaxFrequency);
	}


	/**
	 * */
	public final int getFrequency( int i )
	{
		if (m_Frequency!=null)
		   return m_Frequency[i];
		else
		   return m_RadiationMatrix.getAt( i, i);
	}
	/**
	 *  */
	public final  int getNum( String id )
	{
		return m_AttrConverter.getNum(id);
	}
	/**
	 *  */
	public final int getRadiation( int i, int j )
	{
		return m_RadiationMatrix.getAt( i, j);
	}
	/**
	 */
	public final int getRadiationPower( int i )
	{
		int sum=0;

		for(int  j = 0; j< m_attributesCnt ; j++)
			sum += getRadiation( i, j);
		return sum;
	}

	public  final float getExclusiveRadiationPower( int i )
	{
		if (m_ExclusiveRadiationPower!=null)
		   return m_ExclusiveRadiationPower[i];
		else
		{
			float sum=0;
			for(int  j = 0; j< m_attributesCnt; j++)
				sum += getExclusiveRadiation( i, j);
			return sum;
		}
	}
	/**
	 *  */
	public final String getString( int num )
	{
		return m_AttrConverter.getString(num);
	}
	/**
	 */
	public  final int [] getRadiationArray( int i )
	{
		int [] array= new int[m_attributesCnt];

		for( int j = 0;  j< m_attributesCnt; ++j)
			 array[j] += getRadiation( i, j);
		return array;
	}
	/**
	 */
	public final  int [] getRadiationArray( Integer [] i )
	{
		int [] array= new int[m_attributesCnt];

		for( int j = 0, ix;  j< m_attributesCnt; ++j)
			 for( ix = 0;  ix< i.length; ++ix)
				  array[j] += getRadiation( i[ix].intValue(), j);

		return array;
	}
	/**
	 */
	public  final float getExclusiveRadiation( int i, Integer [] j )
	{
		float rpi=0, rpj=0;
		float inter=0, diff1=0, diff2=0;

		for (int k=0;k< m_attributesCnt; ++k)
		{
			rpi=getBalancedRadiation(i,k) ;
			rpj=getBalancedRadiation(k, j);

			if ((rpi!=0.0) && (rpj!=0.0))
			   inter+=Math.abs(rpi - rpj);
			else
				if (rpi==0.0)
					diff1+=rpj;
				else
					diff2+=rpi;
		}
		return ((Math.min(diff1,diff2)+(float)1.0));
	}

	/**
	 */
	public  final float getExclusiveRadiation( int i, AnalysisProcess.BaseRadiationStat stat)
	{
		float rpi=0;
		float diff1=0;

		for (int k=0;k< m_attributesCnt; ++k)
		{
			if (stat.m_BaseRadiation[k]==0 && ((rpi=getBalancedRadiation(i,k))!=0.0))
			{
				diff1+=rpi;
			}
		}

		return (diff1);
	}
	/**
	 */
	public final  float getExclusiveRadiation( int i, int j )
	{
		float rpi=0, rpj=0;
		float inter=0, diff1=0, diff2=0;

		float value=m_ExRadiationMatrix.getAt(i, j);
		if (value!=Float.MIN_VALUE)
			return value;

		  for (int k=0;k< m_attributesCnt; ++k)
		  {
			  rpi=getBalancedRadiation(i,k) ;
			  rpj=getBalancedRadiation(j,k);

			  if ((rpi!=0.0) && (rpj!=0.0))
		//			      inter+=Math.abs(rpi - rpj);
			   inter+=Math.min(rpi, rpj);
		   else if (rpi==0.0)
			   diff1+=rpj;
		   else
			   diff2+=rpi;
		  }

		float minDiff=Math.min(diff1,diff2);

		value=/*inter**/(minDiff+1)/(float)m_attributesCnt*(float)2.0*(float)(diff1+diff2+1)/(float)(inter+diff1+diff2+1.0);

		m_ExRadiationMatrix.setAt(i, j, value);
		return (value); // normalis�e entre 0 et 1
		// on donne un petit avantage � une diff�rence sym�trique qd la somme diff�rence sym�trique plus forte l'avantage
	}
	/**
	 */
	public final float getBalancedRadiationPower( int i )
	{
		if (m_BalancedRadiationPower!=null)
		   return m_BalancedRadiationPower[i];
		else
		{
			 float sum=0;
			 for( int j = 0;  j< m_attributesCnt; ++j)
				  sum += getBalancedRadiation( i, j);
			return sum;
		}
	}

	/**
	 */
	public final int getMaxBalancedRadiationPowerAttribute()
	{

		int firstAttributeNum=(-1);
		float max=-1, value;
		for (int i=0; i<m_attributesCnt; ++i) {
			if ((value=getBalancedRadiationPower(i))>max) {
				max=value;
				firstAttributeNum=i;
			}
		}
		return firstAttributeNum;
	}

	public final NumAndFloat [] getNumericalProfile( String entity )  throws WPSConnectorException
	{
		return getNumericalProfile(  entity , true);
	}

	/**
	 * * Get an array of numerical which describe the attributes of entity
	 */
	public final NumAndFloat [] getNumericalProfile( String entity , boolean buffering)  throws WPSConnectorException
	{
		NumAndFloat [] array;
		boolean discoverAddFlag = m_discoverAddFlag;
		boolean discoverRestrictFlag = m_discoverRestrictAttrFlag;
		boolean isDiscoverList = false;

		if ((array=(NumAndFloat [])m_Entities.get(entity))==null)
		{
			Collection numAttributes = new ArrayList();
			AnalysisProfile profile = m_PlanRequest.getAnalysisProfile();

			ArrayList<StringAndFloat> stockAttributes = new ArrayList<StringAndFloat>();
			for( AttributeEnumeratorItem item : profile.getConnector( m_PlanRequest.m_Dictionary).getEnumerator(entity))
			{
				if( discoverRestrictFlag && !isDiscoverList && m_PlanRequest.m_discoveryAttributeId.compareTo( item.m_Id)==0)
					isDiscoverList = true; // La liste contient-elle le discoveryAttr
				stockAttributes.add( new StringAndFloat( item.m_Id, item.m_Ponderation));
			}

			int num = 0, attributesCnt = 0;
			for( int i = 0; i < stockAttributes.size(); ++i)
			{
				StringAndFloat aItem = ( StringAndFloat) stockAttributes.get( i);
				if (aItem.m_value>m_MaxAttributePond)
				   m_MaxAttributePond=aItem.m_value;

				++attributesCnt;

				// On teste la presence de l'attribut de decouverte
				if( discoverAddFlag && (m_PlanRequest.m_discoveryAttributeId.compareTo(aItem.m_Id)==0))
					discoverAddFlag = false;
				if( isDiscoverList)
					m_discoAttrSet.add(aItem.m_Id);
				if( !discoverRestrictFlag || isDiscoverList || m_discoAttrSet.contains( aItem.m_Id))
				{
					 num = addAttribute(aItem.m_Id);
					 numAttributes.add( new NumAndFloat( num, aItem.m_value));
				}
			}

			// Si l'attribut de d�couverte n'est pas l� on l'ajoute avec une pond�ration minimale
			if (discoverAddFlag)
			{
				num = addAttribute( m_PlanRequest.m_discoveryAttributeId);
				numAttributes.add( new NumAndFloat(num, 1));
			}

			if (attributesCnt > m_maxAttributesCnt)
			   m_maxAttributesCnt = attributesCnt;

			array = (NumAndFloat [])(numAttributes.toArray(new NumAndFloat[0]));

		   if (buffering)
			   m_Entities.put(entity, array);
		}
		return array;
	}


	public final Collection getNumProfileCollection( String entity )  throws WPSConnectorException
	{
		NumAndFloat [] array;
		Collection ret= new ArrayList();
		array=getNumericalProfile(entity);
		for (int i=0; i< array.length; ++i)
			ret.add(new Integer(array[i].m_num));
		return ret;
	}
	/**
	 */
	public final int getRadiation( int i, Integer [] j )
	{
		int sum=0;
		for( int jx = 0;  jx< j.length; ++jx)
			 sum += getRadiation( i, j[jx].intValue());
		return sum;
	}


	public final int getRadiationCnt( int i, Integer [] j )
	{
		int sum=0;
		for( int jx = 0;  jx< j.length; ++jx)
			 if (getRadiation( i, j[jx].intValue())>0)
				++sum;
		return sum;
	}

	public  final int getRadiation( int i, AnalysisProcess.BaseRadiationStat stat )
	{
		return  stat.m_BaseRadiation[i];
	}

	/**
	 */
	public final float getAverageRadiation( String entity )  throws WPSConnectorException
	{
		float sum=0;
		NumAndFloat [] profile=getNumericalProfile(entity);

		if (profile.length==0)
		   return 0;

		for (int i=0; i<profile.length; ++i)
			sum+=getRadiationPower(profile[i].m_num);
		return sum/(float)profile.length;
	}
	/**
	 */
	public  final float getAverageBalancedRadiation( String entity )  throws WPSConnectorException
	{
		float sum=0;
		NumAndFloat [] profile=getNumericalProfile(entity);

		if (profile.length==0)
		   return 0;

		for (int i=0; i<profile.length; ++i)
			sum+=getBalancedRadiationPower(profile[i].m_num);
		return sum/(float)profile.length;
	}
	/**
	 * attributes must be a sorted array
	 */
	public  final float getAverageRadiation( String entity , int[] attributes)  throws WPSConnectorException
	{
		float sum=0;
		NumAndFloat [] profile=getNumericalProfile(entity);

		if (profile.length==0)
		   return 0;

		for (int i=0; i<profile.length; ++i)
			if (Arrays.binarySearch(attributes,profile[i].m_num )>=0)
			   sum+=getRadiationPower(profile[i].m_num);

		return sum/(float)profile.length;
	}

	public  final float getAverageRadiation( String entity , SymmetricalMatrix radMatrix, int [] attributes)  throws WPSConnectorException
	{
		int sum=0;
		int value;
		NumAndFloat [] profile=getNumericalProfile(entity);

		if (profile.length==0)
		   return 0;

	  for (int i=0, j, aNum; i<profile.length; ++i)
	  {
		  aNum=	profile[i].m_num;
		  if(radMatrix.getAt(aNum,aNum) >0)
		   for (j=0; j<attributes.length; ++j)
			if ((value=radMatrix.getAt(aNum,attributes[j]))!=0)
		  sum+=value;
	  }
	  return (float)sum/(float)profile.length;
	}

	/**
	 * attributes must be a sorted array
	 */
	public  final float getAverageBalancedRadiation( String entity , int[] attributes)  throws WPSConnectorException
	{
		float sum=0;
		NumAndFloat [] profile=getNumericalProfile(entity);

		if (profile.length==0)
	  return 0;

  for (int i=0; i<profile.length; ++i)
	  if (Arrays.binarySearch(attributes,profile[i].m_num )>=0)
	   sum+=getBalancedRadiationPower(profile[i].m_num);
  return sum/(float)profile.length;
	}

	public  final float getAverageBalancedRadiation( String entity , FSymmetricalMatrix radMatrix, int[] attributes)  throws WPSConnectorException
	{
		float sum=0;
		float value=0;
		NumAndFloat [] profile=getNumericalProfile(entity);

		if (profile.length==0)
	  return 0;

  for (int i=0, aNum, j; i<profile.length; ++i)
  {
	  aNum=profile[i].m_num;
	  if(radMatrix.getAt(aNum,aNum) >0)
	   for (j=0; j<attributes.length; ++j)
		if ((value=radMatrix.getAt(aNum,attributes[j]))!=0.0)
	  sum+=value;
  }
  return sum/(float)profile.length;
	}



	static int positiveValuesCnt(int [] array)
	{
		int cnt=0;
		for (int i=0; i<array.length; ++i)
	  if (array[i]>0)
	   ++cnt;
  return cnt;
	}

	float getRefEntityPond(int num)
	{
		if (m_refEntityProfile==null)
	  return Float.MIN_VALUE;

  int found=Arrays.binarySearch(m_refEntityProfile, new NumAndFloat(num, 0));
  if (found>=0)
	  return m_refEntityProfile[found].m_value;
  else return Float.MIN_VALUE;
	}

	float getRefEntityPond(String id)
	{
		return getRefEntityPond(getNum(id));
	}

	float getAttributeSumPond(String id)
	{
		return m_AttributesPondSum[getNum(id)];
	}

	float getAttributeSumPond(int num)
	{
		return m_AttributesPondSum[num];
	}
	float getAttributeMaxPond(String id)
	{
		return m_AttributesPondMax[getNum(id)];
	}

	float getAttributeMaxPond(int num)
	{
		return m_AttributesPondMax[num];
	}

	public final int getAttributeMaxPondAttribute()
	{

		int firstAttributeNum=(-1);
		float max=-1, value;
		for (int i=0; i<m_attributesCnt; ++i) {
			if ((value=m_AttributesPondMax[i])>max) {
				max=value;
				firstAttributeNum=i;
			}
		}
		return firstAttributeNum;
	}

	public ArrayList getMaxPondAttributesArray(int count)
	{
		TreeSet tree = new TreeSet();
		//Collection attributes = new ArrayList();

		float temp[];
		int i;
		for (int num=0; num<m_attributesCnt; ++num) {
			temp= new float[3]; i=0;

// On favorise le crit�re de pond�ration totale
			temp[i++] = getAttributeMaxPond(num);
			temp[i++] = getFrequency(num);
			temp[i++] = getBalancedRadiationPower(num);

			tree.add(new ObjectNumStat(num, temp));
		}

		if (tree.size() == 0)
	  return  new ArrayList();

  Iterator it= tree.iterator();
  ArrayList attRet=new ArrayList();


  while (it.hasNext() && (attRet.size()<count))
	  attRet.add(new Integer(((ObjectNumStat)it.next()).m_Num));

  return  attRet;
	}


}
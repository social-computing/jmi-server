package com.socialcomputing.wps.server.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.socialcomputing.wps.server.planDictionnary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
/**
  *  describes a Map which permits to store (attributeId/ponderation) and to compute set fonctions
  * Key is attribute identifier (Numerical)
  * Value is attribute ponderation (>0) */

public class AttributesPonderationMap extends TreeMap<Integer, Float>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9036576082626446960L;

	private StringToNumConverter m_Converter = null;

	private static float m_PondMax=-1;

	public   AttributesPonderationMap( iEnumerator<AttributeEnumeratorItem> enumerator, StringToNumConverter converter ) throws WPSConnectorException
	{
		m_Converter = converter;
		for( AttributeEnumeratorItem item : enumerator)
		{
			put( new Integer( m_Converter.add(item.m_Id)), new Float(item.m_Ponderation));
			if (m_PondMax<item.m_Ponderation)
			{
				m_PondMax=item.m_Ponderation;
			}
		}
	}

	public   AttributesPonderationMap( iEnumerator<AttributeEnumeratorItem> enumerator, ObjectToNumConverter<ArrayList<String>> converter, String objectId ) throws WPSConnectorException
	{
		m_Converter = converter;
		for( AttributeEnumeratorItem item : enumerator)
		{
			int numAttr = converter.add( item.m_Id, null);
			
			put( numAttr, new Float( item.m_Ponderation));

			// On met � jour la table des attributs
			ArrayList<String> array = converter.getObject(numAttr);
			if (array == null)
				converter.setObject( numAttr, array = new ArrayList<String>());
			array.add( objectId);

			if (m_PondMax<item.m_Ponderation)
			   {
				m_PondMax=item.m_Ponderation;
			   }
		}
	}


	public  float getIntersectionPonderation( AttributesPonderationMap attPondMap2 )
	{
		float retVal = 0;
		Iterator it1 = this.entrySet().iterator();
		Iterator it2 = attPondMap2.entrySet().iterator();

		Map.Entry entry1=null, entry2=null;
		boolean b1 = it1.hasNext();
		boolean b2 = it2.hasNext();
		boolean ok = b1 && b2;
		while( ok)
		{
			if( b1)
				entry1 = (Map.Entry)it1.next();
			if( b2)
				entry2 = (Map.Entry)it2.next();

			int comp = ((Comparable)(entry1.getKey())).compareTo(entry2.getKey());
			if( comp == 0)
			{
				retVal+=ponderationFunction(Math.abs( ( float)((Float)entry1.getValue()).floatValue()-((Float)entry2.getValue()).floatValue()), m_PondMax );
				//System.out.println( entry1.getValue() + "*" + entry2.getValue() + " : " + retVal);
				b1 = it1.hasNext();
				b2 = it2.hasNext();
				ok = b1 && b2;
			}
			else if( comp < 0)
			{
				b1 = it1.hasNext();
				ok = b1;
				b2 = false;
			}
			else //if( comp > 0)
			{
				b2 = it2.hasNext();
				ok = b2;
				b1 = false;
			}
		}
		return retVal;
	}


	public  int getSymmetricalDifferenceCardinality( AttributesPonderationMap attPondMap2 )
	{
		int retVal = 0;
		Iterator it1 = this.entrySet().iterator();
		Iterator it2 = attPondMap2.entrySet().iterator();

		Map.Entry entry1=null, entry2=null;
		boolean b1 = it1.hasNext();
		boolean b2 = it2.hasNext();
		boolean ok = b1 && b2;
		while( ok)
		{
			if( b1)
				entry1 = (Map.Entry)it1.next();
			if( b2)
				entry2 = (Map.Entry)it2.next();

			int comp = ((Comparable)(entry1.getKey())).compareTo(entry2.getKey());
			if( comp == 0)
			{
				b1 = it1.hasNext();
				b2 = it2.hasNext();
				ok = b1 && b2;
			}
			else if( comp < 0)
			{
			   ++retVal;
				b1 = it1.hasNext();
				ok = b1;
				b2 = false;
			}
			else //if( comp > 0)
			{
			   ++retVal;
				b2 = it2.hasNext();
				ok = b2;
				b1 = false;
			}
		}

		while (it1.hasNext())
			{
			it1.next();
			++retVal;
			}

		while (it2.hasNext())
			{
			it2.next();
			++retVal;
			}

		return retVal;
	}

/**
  *  return the cardinalty of intersection between current Map and attPondMap2
  *
  */
	public  int getIntersectionCardinality( AttributesPonderationMap attPondMap2 )
	{
		int retVal = 0;
		Iterator it1 = this.entrySet().iterator();
		Iterator it2 = attPondMap2.entrySet().iterator();

		Map.Entry entry1=null, entry2=null;
		boolean b1 = it1.hasNext();
		boolean b2 = it2.hasNext();
		boolean ok = b1 && b2;
		while( ok)
		{
			if( b1)
				entry1 = (Map.Entry)it1.next();
			if( b2)
				entry2 = (Map.Entry)it2.next();

			int comp = ((Comparable)(entry1.getKey())).compareTo(entry2.getKey());
			if( comp == 0)
			{
				++retVal;
				b1 = it1.hasNext();
				b2 = it2.hasNext();
				ok = b1 && b2;
			}
			else if( comp < 0)
			{
				b1 = it1.hasNext();
				ok = b1;
				b2 = false;
			}
			else //if( comp > 0)
			{
				b2 = it2.hasNext();
				ok = b2;
				b1 = false;
			}
		}
		return retVal;
	}

	public static void setPondMax( float max)
	{
		m_PondMax=max;
	}

	public static float getPondMax( float max)
	{
		return m_PondMax;
	}

   public static float ponderationFunction(float pond, float maxPond)
   {
	return (float)(Math.log(pond+2)/Math.log(maxPond+2));
   }

}

package com.socialcomputing.wps.server.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
/**
  *  describes a Map which permits to store (attributeId/ponderation) and to compute set fonctions
  * Key is attribute identifier (Numerical)
  * Value is attribute ponderation (>0) */

public class TreeSetExt
{

	static public  Collection getIntersection( TreeSet set1, TreeSet set2 )
	{
		ArrayList ret=new ArrayList();
		Iterator it1 = set1.iterator();
		Iterator it2 = set2.iterator();

		Object entry1=null, entry2=null;
		boolean b1 = it1.hasNext();
		boolean b2 = it2.hasNext();
		boolean ok = b1 && b2;
		while( ok)
		{
			if( b1)
				entry1 = it1.next();
			if( b2)
				entry2 = it2.next();

			int comp = ((Comparable)(entry1)).compareTo(entry2);
			if( comp == 0)
			{
				ret.add(entry1);
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
		return ret;
	}
}
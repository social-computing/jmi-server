package com.socialcomputing.wps.server.utils;

import java.util.TreeSet;

/**
  * An object described with float values
  * It could be use in a TreeMap because it implements interface "comparable" */

public class ObjectNumStat extends ObjectStat
{

   public int m_Num;

// Test method
public static void main(String [] args) {
TreeSet set= new TreeSet();

int size=100;
//String [] temp= new String[size];

long t1= System.currentTimeMillis();
 for (int i=0; i<size; ++i)
   {
	float [] values= new float[2];
	values[0]=Math.abs(i-50);
	values[1]=Math.abs(i-70);
	ObjectNumStat stat=new ObjectNumStat(i,values);
	set.add(stat);
   }
System.out.println(set.toString());

long t2= System.currentTimeMillis();

System.out.println("Time:"+(t2-t1));
System.out.println("Size:"+set.size());
}


/**
 */
   public  int compareTo( Object o )
   {
	int value=super.compareTo(o);
	if (value==0)
		{
		if (m_Num>((ObjectNumStat)o).m_Num)
			return 1;
		else return -1;
		}
	else return value;
   }




// Get/Set Methods For Member Variables


/**
  * */
   public  ObjectNumStat( int num, float[] values )
   {
	super(values);
   m_Num=num;
   }

   public  String toString(  )
   {
   String temp= new String((new Integer(m_Num)).toString()+ super.toString());
  return temp;
   }
}

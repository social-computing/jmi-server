package com.socialcomputing.wps.server.utils;
import java.util.TreeSet;

/**
  * An object described with float values
  * It could be use in a TreeMap because it implements interface "comparable" */

public class ObjectStringStat extends ObjectStat
{

   public String m_Id = null;


/**
 */
   public  int compareTo( Object o )
   {
	int value=super.compareTo(o);
	if (value==0)
		{
		return(m_Id.compareTo(((ObjectStringStat)o).m_Id));
		}
	else return value;
   }




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
	ObjectStringStat stat=new ObjectStringStat((new Integer(i)).toString(),values);
	set.add(stat);
   }
System.out.println(set.toString());

long t2= System.currentTimeMillis();

System.out.println("Time:"+(t2-t1));
System.out.println("Size:"+set.size());
}


// Get/Set Methods For Member Variables


/**
   */
   public  ObjectStringStat( String id, float[] values )
   {
	super(values);
   m_Id=id;
   }
   public  String toString(  )
   {
   String temp= new String(m_Id+ super.toString());
   return temp;
   }
}

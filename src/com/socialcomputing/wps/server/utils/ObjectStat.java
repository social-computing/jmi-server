package com.socialcomputing.wps.server.utils;

/**
  * An object described with float values
  * It could be use in a TreeMap because it implements interface "comparable" */

public class ObjectStat implements Comparable
{

   public float m_values[] = null;


// Get/Set Methods For Member Variables


/**
 */
   public  int compareTo( Object o )
   {
    for (int i=0; i<m_values.length; ++i)
   {
   float val2=((ObjectStat)o).m_values[i];
   if (val2<m_values[i])
   return -1;
   else
   if (val2>m_values[i])
   return 1;
   }
   return 0;
   }
/**
  *  */
   public  ObjectStat(  float[] values )
   {
   m_values=values;

   }

   public  String toString(  )
   {
   String temp= new String("");
   for (int i=0; i<m_values.length; ++i)
       temp+=(":"+m_values[i]);

    return temp;
   }
}

package com.socialcomputing.wps.server.utils;

/**
  *  describes a converter between an Id (String) and a numerical (int) */

public class MathLogBuffer
{
   static private double[] m_log = new double[1000];
   static private int m_size;
   static private int m_block_size = 500;

static {
	m_size=m_log.length;
	for (int i=1; i<=m_size; ++i)
		m_log[i-1]=Math.log(i);
}

// Test method
public static void main(String [] args) {
 System.out.println(MathLogBuffer.getLog(10));
 System.out.println(MathLogBuffer.getLog(100));

}



// Get/Set Methods For Member Variables

   public static final double getLog( int num )
   {
	if (num>=m_size)
		{
		resize(num);
		}
	return m_log[num-1];
   }


   private static final  int resize(  int num)
   {
   int new_size=num+m_block_size+1;
   double newArray []=new double[new_size];
   System.arraycopy(m_log/*src*/, 0,  newArray/*dest*/, 0, m_size);
	for (int i=m_size+1; i<=new_size; ++i)
		newArray[i-1]=Math.log(i);
   m_log=newArray;
   m_size=m_log.length;
   return m_size;
  }

}

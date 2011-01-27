package com.socialcomputing.wps.server.utils;

import java.util.Collection;
/**
  * A matrix which is symetric and can be adressed with string */

public class StringIdSymMatrix extends SymmetricalMatrix
{

   public StringToNumConverter m_StringToNumConverter;


// Test method
public static void main(String [] args) {
int size=5000;
StringToNumConverter conv=new  StringToNumConverter();
String [] temp= new String[size];

 for (int i=0; i<size; ++i)
    temp[i]=(new Integer(i+100)).toString();

 for (int i=0; i<size; ++i)
    conv.add(temp[i]);

StringIdSymMatrix mat=new  StringIdSymMatrix(conv, true);

long t1= System.currentTimeMillis();

 for (int i=0; i<size; ++i)
  for (int j=i; j<size; ++j)
    mat.setAt(temp[i], temp[j], i+j);

long t2= System.currentTimeMillis();

 for (int i=0; i<size; ++i)
  for (int j=i; j<size; ++j)      {
      mat.getAt(temp[i],temp[j]);
      }

long t3= System.currentTimeMillis();

 for (int i=0; i<size; ++i)
  for (int j=i; j<size; ++j)      {
      mat.incrAt(temp[i],temp[j]);
      }

long t4= System.currentTimeMillis();

System.out.println("Time:"+(t2-t1)+":"+(t3-t2)+":"+(t4-t3));
System.out.println("Size:"+mat.size());
System.out.println("Value:"+"10,24:"+mat.getAt(temp[10],temp[24]));
System.out.println("Value:"+"24,10:"+mat.getAt(temp[24],temp[10]));
System.out.println("Value:"+"11,24:"+mat.getAt(temp[24],temp[11]));

}

// Get/Set Methods For Member Variables

/**
  * */
   public StringIdSymMatrix( Collection stringList, boolean m_WithDiag )
   {
    super(stringList.size(), m_WithDiag);
    m_StringToNumConverter = new StringToNumConverter(stringList);
   }


/**
  *  */
   public  void setAt( String i,String j,int value )
   {
   setAt(m_StringToNumConverter.getNum(i), m_StringToNumConverter.getNum(j), value);
   }
/**
  *  */
   public  int getAt( String i,String j )
   {
   return getAt(m_StringToNumConverter.getNum(i), m_StringToNumConverter.getNum(j));
   }
/**
  *  */
   public  void incrAt( String i,String j )
   {
   incrAt(m_StringToNumConverter.getNum(i), m_StringToNumConverter.getNum(j));
   }
/**
  *  */
   public  int getNum( String id )
   {
return m_StringToNumConverter.getNum(id);
   }
/**
  *  */
   public  String getString( int num )
   {
return m_StringToNumConverter.getString(num);
   }
/**
  * */
   public  StringIdSymMatrix( StringToNumConverter converter, boolean m_WithDiag )
   {
   super(converter.size(), m_WithDiag);
   m_StringToNumConverter = converter;
   }

}

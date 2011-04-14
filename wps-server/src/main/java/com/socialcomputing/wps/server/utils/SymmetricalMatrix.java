package com.socialcomputing.wps.server.utils;

/**
  * describes a symmetrical Matrix (with or without diag) */

public class SymmetricalMatrix
{

   private int m_Size = 0;
   private int[][] m_Array = null;


// Test method
public static void main(String [] args) {
int size=5000;
SymmetricalMatrix mat=new  SymmetricalMatrix(size, true);

long t1= System.currentTimeMillis();

//String [] temp= new String[size];

 for (int i=0; i<size; ++i)
  for (int j=i; j<size; ++j)
    mat.setAt(i, j, i+j);

long t2= System.currentTimeMillis();

 for (int i=0; i<size; ++i)
  for (int j=i; j<size; ++j)      {
      mat.getAt(i,j);
      }

long t3= System.currentTimeMillis();

 for (int i=0; i<size; ++i)
  for (int j=i; j<size; ++j)      {
      mat.incrAt(i,j);
      }

long t4= System.currentTimeMillis();

System.out.println("Time:"+(t2-t1)+":"+(t3-t2)+":"+(t4-t3));
System.out.println("Size:"+mat.size());
System.out.println("Value:"+"10,24:"+mat.getAt(10,24));
System.out.println("Value:"+"24,10:"+mat.getAt(24,10));
System.out.println("Value:"+"11,24:"+mat.getAt(24,11));

}


/**
  * */
   public  SymmetricalMatrix( int size, boolean withDiag )
   {
   m_Size = size;
   m_Array = new int [size][];
   for (int i=0; i< size; ++i)
       m_Array[i]= new int[i+1];
   }


   public  SymmetricalMatrix( SymmetricalMatrix matrix , int [] valuesToCopy )
   {
   m_Size = matrix.size();
   m_Array = new int [m_Size][];
   for (int i=0; i< m_Size; ++i)
	   {
       m_Array[i]= new int[i+1];
	   }

	   int ix, jx;
  for (int i=0,j; i< valuesToCopy.length; ++i)
      for (j=i; j< valuesToCopy.length; ++j)
  	   {
	   ix=valuesToCopy[i];
	   jx=valuesToCopy[j];
	   if (jx>ix)
          m_Array[jx][ix]=matrix.m_Array[jx][ix];
		else m_Array[ix][jx]=matrix.m_Array[ix][jx];
	   }

   }

/**
   */
   public final int getAt( int i,int j )
   {
	if (j>i)
	  return(m_Array[j][i]);
	else return(m_Array[i][j]);
	}

   /**
   */
   public  final int size(  )
   {
     return m_Size;
   }
/**
  * */
   public final void setAt( int i,int j,int value )
   {
	if (j>i)
	  m_Array[j][i]=value;
	else m_Array[i][j]=value;
   }
/**
  *  */
   public  final void  incrAt( int i,int j )
   {
	if (j>i)
      ++m_Array[j][i];
  else  ++m_Array[i][j];
  }

}

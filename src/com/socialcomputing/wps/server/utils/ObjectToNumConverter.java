package com.socialcomputing.wps.server.utils;

/**
  *  describes a converter between an Id (String) and a numerical (int)
  *  An Object could be associated with each id
  *
  * **********************************************************
  * Java Class Name : ObjectToNumConverter
  * ---------------------------------------------------------
  * Filetype: (SOURCE)é
  * Filepath: C:\Dvpt\src\com\voyezvous\wps\server\\utils\ObjectToNumConverter.java
  *
  *
  * GDPro Properties
  * ---------------------------------------------------
  * - GD Symbol Type    : CLD_Class
  * - GD Method         : UML ( 5.0 )
  * - GD System Name    : WPS
  * - GD Diagram Type   : Class Diagram
  * - GD Diagram Name   : AffinityEngine
  * ---------------------------------------------------
  * Author         : espinat
  * Creation Date  : Wed - Jan 24, 2001
  *
  * Change Log     :
  *
  * ********************************************************** */

public class ObjectToNumConverter extends com.socialcomputing.wps.server.utils.StringToNumConverter
{

   private Object[] m_NumToObject = null;


// Get/Set Methods For Member Variables


/**
 */
   public  int add( String id, Object obj )
   {
  if (m_StringToNum.containsKey(id))
	return (((Integer)m_StringToNum.get(id)).intValue());
  else  return  _add(id, obj);
   }
/**
  *  */
   public  Object getObject( int num )
   {
   return m_NumToObject[num];
   }

   public  void setObject( int num, Object obj )
   {
   m_NumToObject[num]=obj;
   }


   /**
  * */
   public  ObjectToNumConverter(  )
   {
	super();
   m_NumToObject=new Object[m_block_size];
   }
/**
 */
   private  int _add( String id, Object obj )
   {
   int num =_add( id);


   if (num>=m_NumToObject.length)
	{
	Object newArray [] =new Object[m_NumToObject.length+m_block_size];
	System.arraycopy(m_NumToObject/*src*/, 0,  newArray/*dest*/, 0, m_NumToObject.length);
	m_NumToObject=newArray;
	}

   m_NumToObject[num]=obj;

   return num;
  }

}

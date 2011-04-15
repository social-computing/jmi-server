package com.socialcomputing.wps.server.utils;

import java.util.ArrayList;

/**
 * describes a converter between an Id (String) and a numerical (int) An Object
 * could be associated with each id
 * 
 * ********************************************************** Java Class Name :
 * ObjectToNumConverter
 * --------------------------------------------------------- Filetype: (SOURCE)�
 * Filepath:
 * C:\Dvpt\src\com\voyezvous\wps\server\\utils\ObjectToNumConverter.java
 * 
 * 
 * GDPro Properties --------------------------------------------------- - GD
 * Symbol Type : CLD_Class - GD Method : UML ( 5.0 ) - GD System Name : WPS - GD
 * Diagram Type : Class Diagram - GD Diagram Name : AffinityEngine
 * --------------------------------------------------- Author : espinat Creation
 * Date : Wed - Jan 24, 2001
 * 
 * Change Log :
 * 
 * **********************************************************
 */

public class ObjectToNumConverter<E> extends StringToNumConverter {

	private ArrayList<E> m_NumToObject = null;

	// Get/Set Methods For Member Variables

	/**
 */
	public int add(String id, E obj) {
		if (m_StringToNum.containsKey(id))
			return m_StringToNum.get(id);
		else
			return _add(id, obj);
	}

	/**
  *  */
	public E getObject(int num) {
		return m_NumToObject.get( num);
	}

	public void setObject(int num, E obj) {
		m_NumToObject.set( num, obj);
	}

	/**
  * */
	public ObjectToNumConverter() {
		super();
		m_NumToObject = new ArrayList<E>(m_block_size);
	}

	/**
 */
	private int _add(String id, E obj) {
		int num = _add(id);

//		if (num >= m_NumToObject.length) {
//			E[] newArray[] = new E[m_NumToObject.length + m_block_size];
//			System.arraycopy(m_NumToObject/* src */, 0, newArray/* dest */, 0,
//					m_NumToObject.length);
//			m_NumToObject = newArray;
//		}

		m_NumToObject.set( num, obj);

		return num;
	}

}

package com.socialcomputing.wps.server.utils;

import java.util.Collection;
import java.util.HashMap;

/**
 * describes a converter between an Id (String) and a numerical (int)
 */

public class StringToNumConverter {

	protected String[] m_NumToString = null;
	protected HashMap<String, Integer> m_StringToNum = null;
	protected int m_block_size = 500;

	// Get/Set Methods For Member Variables

	/**
  *  */
	public final int getNum(String id) {
		return m_StringToNum.get(id);
	}

	/**
  *  */
	public final String getString(int num) {
		return m_NumToString[num];
	}

	/**
  * */
	public StringToNumConverter( Collection<String> stringList) {
		m_NumToString = new String[stringList.size()];
		m_StringToNum = new HashMap<String, Integer>();
		for( String id : stringList) {
			_add(  id);
		}
	}

	/**
   */
	public final int add(String id) {
		if (m_StringToNum.containsKey(id))
			return m_StringToNum.get(id);
		else
			return _add(id);
	}

	/**
 */
	public final int size() {
		return m_StringToNum.size();
	}

	/**
 */
	public StringToNumConverter() {
		m_NumToString = new String[m_block_size];
		m_StringToNum = new HashMap<String, Integer>();
	}

	/**

  *  */
	protected final int _add(String id) {
		int size = m_StringToNum.size();
		m_StringToNum.put(id, new Integer(size)); /* key, value */

		if (size >= m_NumToString.length) {
			String newArray[] = new String[m_NumToString.length + m_block_size];
			System.arraycopy(m_NumToString/* src */, 0, newArray/* dest */, 0,
					m_NumToString.length);
			m_NumToString = newArray;
		}

		m_NumToString[size] = id;
		return size;
	}

	public boolean contains(String id) {
		return m_StringToNum.containsKey(id);
	}

	public void setBlocksize(int size) {
		m_block_size = size;
	}
	
	// Test method
	public static void main(String[] args) {
		StringToNumConverter conv = new StringToNumConverter();

		long t1 = System.currentTimeMillis();
		int size = 100000;
		String[] temp = new String[size];

		for (int i = 0; i < size; ++i)
			temp[i] = (new Integer(i + 100)).toString();

		for (int i = 0; i < size; ++i)
			conv.add(temp[i]);

		long t2 = System.currentTimeMillis();

		for (int i = 0; i < size; ++i) {
			// System.out.println(i+":"+conv.getNum((new
			// Integer(i)).toString()));
			conv.getNum(temp[i]);
		}

		long t3 = System.currentTimeMillis();

		for (int i = 0; i < size; ++i) {
			// System.out.println(i+":"+conv.getString(i));
			conv.getString(i);
		}

		long t4 = System.currentTimeMillis();

		System.out.println("Time:" + (t2 - t1) + ":" + (t3 - t2) + ":"
				+ (t4 - t3));
		System.out.println("Size:" + conv.size());
		System.out.println("Where:" + temp[100] + ":" + conv.getNum(temp[100]));
		System.out.println("Contains:" + temp[200] + ":"
				+ conv.contains(temp[200]));
		System.out.println("Contains:" + "NotInMap:"
				+ conv.contains("NotInMap"));
	}

	

}

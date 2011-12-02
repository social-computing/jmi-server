package com.socialcomputing.wps.server.webservices;

import java.util.Hashtable;

/**
  * The internet user  */

public class RequestingClassifyId
{
	public String m_Id = null;
	public String m_Name = null;

	public RequestingClassifyId( String userId)
	{
		m_Id = userId;
		m_ClassifiersResults = new Hashtable<String, String>();
	}

	/**
	* Store previous classification results for the requesting entity (classifier name, classifier result) */
	public Hashtable<String, String> m_ClassifiersResults = null;


}

package com.socialcomputing.wps.server.plandictionary;

import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;

public class Entities implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 767055829875007506L;
	// 0 : JDBC Connector
	public int m_ConnectorType;
	public iEntityConnector m_EntitiesConnector = null;

	public ClassifierMapper m_FilteringMapper = new ClassifierMapper();

	public ClassifierMapper m_AnalysisMapper = new ClassifierMapper();

	public ClassifierMapper m_AdvertisingMapper = null;

	public Entities()
	{
		m_ConnectorType = -1;
	}

	public Entities( int type, iEntityConnector connector)
	{
		m_ConnectorType = type;
		m_EntitiesConnector = connector;
	}
}

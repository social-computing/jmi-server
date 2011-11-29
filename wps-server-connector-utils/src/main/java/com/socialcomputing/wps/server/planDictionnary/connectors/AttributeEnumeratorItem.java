package com.socialcomputing.wps.server.planDictionnary.connectors;

/**
 * Title:        WPS Connectors
 * Description:
 * Copyright:    Copyright (c) 2010
 * Company:      Social Computing
 * @author Franck Valetas
 * @version 1.0
 */

public class AttributeEnumeratorItem implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1716440422035255420L;
	
    public String  m_Id = null;
	public float   m_Ponderation = 0;

	public AttributeEnumeratorItem()
	{
	}

	public AttributeEnumeratorItem( String id, float pond)
	{
		m_Id = id;
		m_Ponderation = pond;
	}

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof AttributeEnumeratorItem) {
            return this.m_Id.equalsIgnoreCase( ((AttributeEnumeratorItem) obj).m_Id);
        }
        return super.equals(obj);
    }

}
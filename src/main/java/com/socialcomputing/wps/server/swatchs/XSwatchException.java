package com.socialcomputing.wps.server.swatchs;

import org.jdom.*;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class XSwatchException extends JDOMException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7180873733391114213L;
	protected Element   m_elem;

	public XSwatchException( Element elem )
	{
		m_elem      = elem;
	}

	public String toString()
	{
		return "<" + m_elem.getName() + "id=" + m_elem.getAttributeValue( "id" ) + ">";
	}
}

class MissingAttException extends XSwatchException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5997691463923353812L;
	protected String    m_attNam;

	public MissingAttException( Element elem, String attNam )
	{
		super( elem );

		m_elem      = elem;
		m_attNam    = attNam;
	}

	public String toString()
	{
		return super.toString() + "must contains a '" + m_attNam + "' attribute";
	}
}

class ExclusivAttException extends XSwatchException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2277088351565543084L;
	protected String    m_att1Nam;
	protected String    m_att2Nam;

	public ExclusivAttException( Element elem, String att1Nam, String att2Nam )
	{
		super( elem );

		m_elem      = elem;
		m_att1Nam    = att1Nam;
		m_att2Nam    = att2Nam;
	}

	public String toString()
	{
		return super.toString() + "must contains only one of '" + m_att1Nam + "' or '" + m_att2Nam + " attribute";
	}
}
package com.socialcomputing.wps.server.swatchs;

import java.io.Serializable;
import java.util.Hashtable;

import com.socialcomputing.wps.client.applet.VContainer;

 /**
  * <p>Title: ConstValue</p>
  * <p>Description: A simple value container that hold a static Object.</p>
  * <p>Copyright: Copyright (c) 2001-2003</p>
  * <p>Company: MapStan (Voyez Vous)</p>
  * @author flugue@mapstan.com
  * @version 1.0
  */
public class ConstValue extends ValueContainer implements Serializable
{
	static final long serialVersionUID = 8981377598486770654L;

	/**
	 * Object to encapsulate.
	 */
	protected   Object  m_val;

	/**
	 * Creates a new ConstValue using a value.
	 * @param val	The value to encapsulate.
	 */
	public  ConstValue( Object val )
	{
		m_val = val;
	}

	/**
	 * Convert this to a WPSApplet VContainer.
	 * @param refs	The swatch table of reference. Not used.
	 * @return		a new constant VContainer holding m_val.
	 */
	public VContainer toClientCont( Hashtable refs )
	{
		return new VContainer( m_val, false );
	}

	/**
	 * Gets the inner raw Object value.
	 * @param refs	The swatch table of reference. Not used.
	 * @return		m_val.
	 */
	public Object getRawValue( Hashtable refs )
	{
		return m_val;
	}
}

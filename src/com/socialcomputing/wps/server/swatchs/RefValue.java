package com.socialcomputing.wps.server.swatchs;

import java.io.Serializable;
import java.util.Hashtable;

import com.socialcomputing.wps.client.applet.VContainer;

/**
 * <p>Title: RefValue</p>
 * <p>Description: A ValueContainer that holds a reference to another ValueContainer</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class RefValue extends ConstValue implements Serializable
{
	static final long serialVersionUID = "RefValue".hashCode();

	/**
	 * Creates a new RefValue using a reference.
	 * @param ref	The reference to put into this.
	 */
	public  RefValue( Object ref )
	{
		super( ref );
	}

	/**
	 * Converts this to a WPSApplet VContainer.
	 * WPSApplet Objects have no fields but an array of VContainers.
	 * So this method convert Server fields to Client fields.<br>
	 * As reference should be allocated only once, a reference buffer is filled with the converted references.
	 * The converted Object is stored in a ClientValue Container so it can easily be checked.
	 * Then next time the same reference should be converted, the ref table is first checked to see if it already exists encapsulated in a ClientValue.
	 * If so, this value is simply returned. So reference Server-side stay references on Client-side.<br>
	 * The reference can be Clientable or not. If it is, its toClientCont method is called, else its simply put in a const VContainer.
	 * @param refs	Table of reference coming from the XSwatch using this.
	 * @return		A new VContainer if this wasn't bufferized or an existing one if this was previously called.
	 */
	public VContainer toClientCont( Hashtable refs )
	{
		Object      ref = refs.get( m_val );
		VContainer  clientVal;

		if ( ref instanceof ClientValue )  // impossible if this has not been previously bufferized!
		{
			clientVal = (VContainer)(((ClientValue)ref ).m_val );
		}
		else    // create the client and bufferized it
		{
			if ( ref instanceof Clientable )
			{
				clientVal = ((Clientable)ref ).toClientCont( refs );
			}
			else
			{
				clientVal = new VContainer( ref, false );
			}

			refs.put( m_val, new ClientValue( clientVal ));
		}

		return clientVal;
	}

	/**
	 * Converts this to a value for the Client-side.
	 * Some Objects should not be encapsuled in the a VContainer because they are directly referenced as is.
	 * So this method convert Refs to their WPSApplet equivalent without encapsulating them.
	 * The reference can be Clientable or not. If it is, its toClient method is called, else its simply returned as is.
	 * @param refs	Table of reference coming from the XSwatch using this.
	 * @return		An Object to be used by the Client-side.
	 * @see			toClientCont
	 */
	public Object toClient( Hashtable refs )
	{
		Object      ref = refs.get( m_val );
		Object      clientVal;

		if ( ref instanceof ClientValue )  // impossible if this has not been previously bufferized!
		{
			clientVal = ((ClientValue)ref ).m_val;
		}
		else    // create the client and bufferized it
		{
			if ( ref instanceof Clientable )
			{
				clientVal = ((Clientable)ref ).toClient( refs );
			}
			else
			{
				clientVal = ref;
			}

			refs.put( m_val, new ClientValue( clientVal ));
		}

		return clientVal;
	}

	/**
	 * Gets the value referenced by this.
	 * @param refs	Table of reference coming from the XSwatch using this.
	 * @return		The referenced value whose key is m_val.
	 */
	public Object getRawValue( Hashtable refs )
	{
		return refs.get( m_val );
	}
}

/**
 * <p>Title: ClientValue</p>
 * <p>Description: A RefValue already converted to a WPSApplet Object.<br>
 * The converted Object is simply stored in this m_val member.
 * This class is used to differenciate normal RefValues from already converted RefValues.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
class ClientValue extends ConstValue
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1388329603346469348L;

	public ClientValue( Object val )
	{
		super( val );
	}

	public Object toClient( Hashtable refs )
	{
		return m_val;
	}
}

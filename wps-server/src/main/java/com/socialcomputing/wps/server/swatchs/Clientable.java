package com.socialcomputing.wps.server.swatchs;

import java.util.Hashtable;

import com.socialcomputing.wps.client.applet.VContainer;

/**
 * <p>Title: Clientable</p>
 * <p>Description: Implements this interface so the class can give its properties </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public interface Clientable
{
	public Object toClient( Hashtable refs );
	public VContainer toClientCont( Hashtable refs );
}
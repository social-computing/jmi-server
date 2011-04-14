package com.socialcomputing.wps.server.swatchs;

import java.util.ArrayList;

/**
 * <p>Title: Propable</p>
 * <p>Description: To allow the Swatch to retrieve values from its containers (XMenu,XSat, X...) they should implements this interface.<br>
 * The Value to add to the list should be a ValueContainer(ConstValue, InterValue, RefValue, TextValue)</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public interface Propable
{
	/**
	 * Adds all the Properties (ValueContainer) inside this to a previously allocated list.
	 * @param list	A list of ValueContainers.
	 */
	public void addPropsToList( ArrayList list );
}
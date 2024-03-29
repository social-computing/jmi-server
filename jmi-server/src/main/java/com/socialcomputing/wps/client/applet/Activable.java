package com.socialcomputing.wps.client.applet;

import java.awt.Graphics;

/**
 * <p>Title: Activable</p>
 * <p>Description: An interface for zone that can be painted differently when they are hovered.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public interface Activable
{
	/**
	 * Methode called when this zone must be painted with its current swatch.
	 * @param applet    WPSApplet owning this zone.
	 * @param g         A Graphics on which this must be painted.
	 */
	public void paintCur( WPSApplet applet, Graphics g );
}

package com.socialcomputing.wps.client.applet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p>Title: Env</p>
 * <p>Description: A place to store the "environment" datas and a media loader.
 * The global "cosmetics" of the applet are stored here.<br>
 * It also manage media related stuff.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class Env implements Serializable, Runnable
{
	/**
	 * JDK 1.1 serialVersionUID
	 */
	static final long serialVersionUID = -478378518827669742L;

	/**
	 * True if this Plan is sound enabled.
	 * This is not used, it should be deprecated...
	 */
//	public static final int AUDIO_BIT       = 0x01;

	/**
	 * True if this Plan displays Entities (Groups and Clusters)
	 * This is not used, it should be deprecated...
	 */
	public static final int GROUP_BIT       = 0x02;

	/**
	 * True if this Plan can be accessed using JavaScript.
	 * This is not used, it should be deprecated...
	 */
//	public static final int JSCRIPT_BIT     = 0x04;

	/**
	 * Contains XXX_BIT(s).
	 * This is not used, it should be deprecated, but first removed from Model.java
	 */
	public		int         m_flags;

	/**
	 * Plan background Color.
	 */
	public  	ColorX      m_inCol;

	/**
	 * Plan border Color.
	 * This is not used, it should be deprecated, but first removed from Model.java
	 */
	public  	ColorX      m_outCol;

	/**
	 * Plan filter color.
	 * This color will apeared dimmed under the current zone and over the background, showing the actual BBox.
	 */
	public  	ColorX      m_filterCol;

	/**
	 * Applet size & scale.
	 * This is used by the Server to retrieve the Applet size and then give a well sized plan to the client.
	 */
	public      Transfo     m_transfo;

	/**
	 * Global properties.
	 * Properties that don't appear in Zones because they are global to the plan.<br>
	 * ex : paths of icons, name of user...
	 * Those props are copied in Zones table during init.
	 */
	public      Hashtable   m_props;

	/**
	 * Table of the 32 selection name.
	 * This is used by swatchs because they only know the selection name, not the ID.
	 * It should be used by Javascript...but it isn't.
	 */
	public      Hashtable   m_selections;

	/**
	 * Table containing icons and sounds.
	 * This buffer stores all media object using a unique key to load them asynchronously during init.
	 */
	protected   transient Hashtable     m_medias  = null;

	/**
	 * A simple reference to the Applet.
	 * This is necessary because the Thread must know the Applet.
	 * But it is launch by run() that don't have any arguments.
	 */
	private   transient WPSApplet     m_applet  = null;

	/**
	 * If a selection has this flag set, it can be displayed by an external UI
	 */
//	public static final int EXTERN_SEL      = 0x20; // 32

	/**
	 * Radius in pixels of the cycleMenu zone.
	 */
//	public static final int ZONE_RAD        = 50;

	public Env() {
        m_props     = new Hashtable();
	}
	
	/**
	 * Initialize transient fields and set Applet background color.
	 * @param applet
	 * @param needPrint
	 */
	void init( WPSApplet applet, boolean needPrint )
	{
		Color	bkCol	= needPrint ? Color.white : m_inCol.getColor();
		applet.setBackground( bkCol );

		m_applet        = applet;
		m_medias        = new Hashtable();
	}

	/**
	 * Returns this object as a xml format
	 * ON
	 * @return
	 */
	/*public String getXML()
	{
		String result = new String();
		
		result = "<env>";
		result += "</env>";
		
		return result;
	}*/
	
	/**
	 * This try to load asynchronously the known medias (icons) during the Applet initialisation.
	 * As java 1.1 API is a bit simple, they didn't thought we want to load media without displaying them.
	 * And MS JVM forgot to totaly implement MediaTracker class so we can't know when a media is fully loaded!
	 * That's why this is a big horrible patch:<br>
	 * First check all images so they should load (but they don't).
	 * Then test their flags to know if they are really loaded.
	 * Then initialize the plan so it tries to display the images.
	 * Restart checking the images, and so on until all images are loaded.<br>
	 * It is important to displayed images because else they will never be loaded!
	 */
	public synchronized void run()
	{
		MediaTracker    tracker = new MediaTracker( m_applet );
		Enumeration     enumvar;
		Image           image;
		Object          media;
		int             i, flags;
		boolean         needLoading = true,
						isFirst = true,
						isAlive = true;

		while ( needLoading && isAlive )
		{
			enumvar        = m_medias.elements();
			needLoading = false;
			i           = 0;

			while ( enumvar.hasMoreElements())
			{
				media = enumvar.nextElement();

				if ( media instanceof Image )
				{
					image   = (Image)media;
					flags   = m_applet.checkImage( image, m_applet );
					tracker.addImage((Image)media, i );
					tracker.checkID( i, true );
					i ++;

					needLoading = needLoading ||(( flags & ImageObserver.ALLBITS )== 0 &&( flags & ImageObserver.ERROR )== 0 );
				}
			}

			try
			{
				tracker.waitForAll( 5000 );
				isAlive = m_applet.m_plan != null;

				if ( isFirst && isAlive ) m_applet.m_plan.init();
			}
			catch ( InterruptedException e ){}
		}

		if ( isAlive )
		{
			m_applet.m_plan.init();
			m_applet.repaint();
			m_applet.m_isMediaReady	= true;
		}
	}

	/**
	 * Flush all medias and clear remove them from the table.
	 * This is needed to reload images when the plan is resized.
	 */
	protected void clearMedias()
	{
		Enumeration enumvar    = m_medias.elements();
		Object      media;

		while ( enumvar.hasMoreElements())
		{
			media   = enumvar.nextElement();

			if ( media instanceof Image )
			{
				((Image)media ).flush();
			}
		}

		m_medias.clear();
	}

	/**
	 * Apply a half transparent color over an image.
	 * This is achieved by drawing 45� lines every 2 pixels.
	 * @param image		The image to cover.
	 * @param dim		size of the image.
	 */
	protected void filterImage( Image image, Dimension dim )
	{
		if ( m_filterCol != null )
		{
			Color       col = m_filterCol.getColor();
			Graphics    g   = image.getGraphics();

			g.setColor( col );

			int w = dim.width - 1,
				h = dim.height - 1,
				min = Math.min( w, h ),
				i, j, n = min + 2;

			for ( i = 1, j =( w + h + 1 )% 2; i < n; i += 2, j += 2 )
			{
				g.drawLine( 0, i, i, 0 );
				g.drawLine( w - j, h, w, h - j );
			}

			if ( w > h )
			{
				n = w - min;

				for ( i = 1 +( h % 2 ); i < n; i += 2 )
				{
					g.drawLine( i, h, min + i, 0 );
				}
			}
			else
			{
				n = h - min;

				for ( i = 1 +( w % 2 ); i < n; i += 2 )
				{
					g.drawLine( w, i, 0, min + i );
				}
			}
		}
	}
}

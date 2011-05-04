package com.socialcomputing.wps.script  {
    import flash.display.Graphics;
    import flash.display.Loader;
    import flash.geom.ColorTransform;
    import flash.geom.Rectangle;
    
    import mx.controls.Image;
	
	import com.socialcomputing.wps.components.PlanComponent;

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
public class Env
{
	/**
	 * True if this Plan is sound enabled.
	 * This is not used, it should be deprecated...
	 */
//	public static final int AUDIO_BIT       = 0x01;

	/**
	 * True if this Plan displays Entities (Groups and Clusters)
	 * This is not used, it should be deprecated...
	 */
	public static const GROUP_BIT:int= 0x02;

	/**
	 * True if this Plan can be accessed using JavaScript.
	 * This is not used, it should be deprecated...
	 */
//	public static final int JSCRIPT_BIT     = 0x04;

	/**
	 * Contains XXX_BIT(s).
	 * This is not used, it should be deprecated, but first removed from Model.java
	 */
	public		var m_flags:int;

	/**
	 * Plan background Color.
	 */
	public  	var m_inCol:ColorX;

	/**
	 * Plan border Color.
	 * This is not used, it should be deprecated, but first removed from Model.java
	 */
	public  	var m_outCol:ColorX;

	/**
	 * Plan filter color.
	 * This color will apeared dimmed under the current zone and over the background, showing the actual BBox.
	 */
	public  	var m_filterCol:ColorX;

	/**
	 * Applet size & scale.
	 * This is used by the Server to retrieve the Applet size and then give a well sized plan to the client.
	 */
	public      var m_transfo:Transfo;

	/**
	 * Global properties.
	 * Properties that don't appear in Zones because they are global to the plan.<br>
	 * ex : paths of icons, name of user...
	 * Those props are copied in Zones table during init.
	 */
	public      var m_props:Array;

	/**
	 * Table of the 32 selection name.
	 * This is used by swatchs because they only know the selection name, not the ID.
	 * It should be used by Javascript...but it isn't.
	 */
	public      var m_selections:Array;

	/**
	 * Table containing icons and sounds.
	 * This buffer stores all media object using a unique key to load them asynchronously during init.
	 */
    [transient]
	public var m_medias:Array = null;

	/**
	 * A simple reference to the Applet.
	 * This is necessary because the Thread must know the Applet.
	 * But it is launch by run() that don't have any arguments.
	 */
    /*[transient]
	private var m_applet:WPSApplet = null;*/

	/**
	 * If a selection has this flag set, it can be displayed by an external UI
	 */
//	public static final int EXTERN_SEL      = 0x20; // 32

	/**
	 * Radius in pixels of the cycleMenu zone.
	 */
//	public static final int ZONE_RAD        = 50;

	/**
	 * Initialize transient fields and set Applet background color.
	 * @param applet
	 * @param needPrint
	 */
	public function init(component:PlanComponent, needPrint:Boolean):void {
        var bkWhite:ColorTransform = new ColorTransform();
        bkWhite.color = 0xFFFFFF;
		var bkCol:ColorTransform= needPrint ? bkWhite : m_inCol.getColor();
		trace( "Enc init à terminer");
		//component.setBackground( bkCol );
		// ??? component.setStyle( "backgroundColor", 0);
		//m_applet        = applet;
		m_medias        = new Array();
	}

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
	public function run():void {
		/*var tracker:MediaTracker= new MediaTracker( m_applet );
		var enumvar:Enumeration;
		var image:Image;
		var media:Object;
		var i:int, flags;
		var needLoading:Boolean= true,
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

				if ( media is Image )
				{
					image   = Image(media);
					flags   = m_applet.checkImage( image, m_applet );
					tracker.addImage(Image(media), i );
					tracker.checkID( i, true );
					i ++;

					needLoading = needLoading ||(( flags & ImageObserver.ALLBITS )== 0&&( flags & ImageObserver.ERROR )== 0);
				}
			}

			try
			{
				tracker.waitForAll( 5000);
				isAlive = m_applet.m_plan != null;

				if ( isFirst && isAlive ) m_applet.m_plan.init();
			}
			catch ( e:InterruptedException){}
		}

		if ( isAlive )
		{
			m_applet.m_plan.init();
			m_applet.repaint();
			m_applet.m_isMediaReady	= true;
		}*/
	}

	/**
	 * Flush all medias and clear remove them from the table.
	 * This is needed to reload images when the plan is resized.
	 */
	protected function clearMedias():void {
		for(var media:Object in m_medias){
			if ( media is Loader )
			{
				(media as Loader).unload();
			}
		}
		m_medias.length = 0;
	}

	/**
	 * Apply a half transparent color over an image.
	 * This is achieved by drawing 45� lines every 2 pixels.
	 * @param image		The image to cover.
	 * @param dim		size of the image.
	 */
	protected function filterImage( image:Image, dim:Rectangle):void {
		if ( m_filterCol != null )
		{
			var col:ColorTransform= m_filterCol.getColor();
            image.alpha = 0.5;
			/*var g:Graphics= image.getGraphics();

			g.setColor( col );

			var w:int= dim.width - 1,
				h = dim.height - 1,
				min = Math.min( w, h ),
				i, j, n = min + 2;

			for ( i = 1, j =( w + h + 1)% 2; i < n; i += 2, j += 2)
			{
				g.drawLine( 0, i, i, 0);
				g.drawLine( w - j, h, w, h - j );
			}

			if ( w > h )
			{
				n = w - min;

				for ( i = 1+( h % 2); i < n; i += 2)
				{
					g.drawLine( i, h, min + i, 0);
				}
			}
			else
			{
				n = h - min;

				for ( i = 1+( w % 2); i < n; i += 2)
				{
					g.drawLine( w, i, 0, min + i );
				}
			}*/
		}
	}
}
}
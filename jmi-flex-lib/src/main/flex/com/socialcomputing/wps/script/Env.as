package com.socialcomputing.wps.script  {
    import com.socialcomputing.wps.components.Map;
    import com.socialcomputing.wps.util.LoaderEx;
    
	import flash.utils.Dictionary;
    import flash.geom.ColorTransform;

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
	public var m_medias:Object = null, m_badMedias = null;;

	/**
	 * Table containing media loders.
	 */
	public var m_loaders:Object = null;

	/**
	 * A simple reference to the Applet.
	 * This is necessary because the Thread must know the Applet.
	 * But it is launch by run() that don't have any arguments.
	 */
	private var m_applet:Map = null;

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
	public function init(applet:Map, needPrint:Boolean):void {
        var bkWhite:ColorTransform = new ColorTransform();
        bkWhite.color = 0xFFFFFF;
		var bkCol:ColorTransform= needPrint ? bkWhite : m_inCol.getColor();
		this.m_applet        = applet;
		this.m_medias        = new Object();
		this.m_loaders       = new Array();
		this.m_badMedias     = new Object();
	}

	public function getMedia(name:String):Object {
		return this.m_medias[name];
	}
	
	public function putMedia(name:String, media:Object):void {
		this.m_medias[name] = media;
	}
	
	public function getBadMedia(name:String):Boolean {
		return this.m_badMedias[name] != null;
	}
	
	public function putBadMedia(name:String):void {
		this.m_badMedias[name] = false;
	}
	
	public function addLoader(name:String, loader:LoaderEx):void {
		this.m_loaders[name] = loader;
	}
	
	public function getLoader(name:String):LoaderEx {
		return this.m_loaders[name];
	}
	
	public function removeLoader(name:String):void {
		if( this.m_loaders[name])
			delete this.m_loaders[name];
	}
	
	public function close():void {
		for( var name:String in m_loaders) {
			var loader:LoaderEx = this.m_loaders[name] as LoaderEx;
			try {
				loader.close();
			} catch( err:Error) {
				trace( err);
			}
			delete this.m_loaders[name];
		}
	}
 }
}

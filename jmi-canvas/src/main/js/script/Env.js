JMI.namespace("script.Env");

/*
 * <p>Title: Env</p>
 * <p>Description: A place to store the "environment" datas and a media loader.
 * The global "cosmetics" of the applet are stored here.<br>
 * It also manage media related stuff.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.Env = (function() {
	
/*
 * Contains XXX_BIT(s).
 * This is not used, it should be deprecated, but first removed from Model.java
 */
	var m_flags,
	// Plan background Color.
	 m_inCol = JMI.script.ColorX,
	/*
	 * Plan border Color.
	 * This is not used, it should be deprecated, but first removed from Model.java
	 */
	m_outCol = JMI.script.ColorX,
	/*
	 * Plan filter color.
	 * This color will apeared dimmed under the current zone and over the background, showing the actual BBox.
	 */
	m_filterCol = JMI.script.ColorX,
	/*
	 * Applet size & scale.
	 * This is used by the Server to retrieve the Applet size and then give a well sized plan to the client.
	 */
	m_transfo = JMI.script.Transfo,
	/*
	 * Global properties.
	 * Properties that don't appear in Zones because they are global to the plan.<br>
	 * ex : paths of icons, name of user...
	 * Those props are copied in Zones table during init.
	 */
	m_props,
	/*
	 * Table of the 32 selection name.
	 * This is used by swatchs because they only know the selection name, not the ID.
	 * It should be used by Javascript...but it isn't.
	 */
	m_selections,
	/*
	 * Table containing icons and sounds.
	 * This buffer stores all media object using a unique key to load them asynchronously during init.
	 */
	m_medias,
 	//Table containing media loders.
	m_loaders,
	
	/**
	 * A simple reference to the Applet.
	 * This is necessary because the Thread must know the Applet.
	 * But it is launch by run() that don't have any arguments.
	 */
	m_applet = JMI.components.Map;

	var Env = function() {
	};
	
    Env.prototype = {
        contructor: JMI.script.Env,
		
	/*
	 * Initialize transient fields and set Applet background color.
	 * @param applet
	 * @param needPrint
	 */
	init: function(applet, needPrint) {
	    var bkWhite = new ColorTransform();
	    bkWhite.color = 0xFFFFFF;
		var bkCol = needPrint ? bkWhite : m_inCol.getColor();
		m_applet   = applet;
		m_medias   = new Object();
		m_loaders  = new Object();
	},
	
	getMedia: function(name) {
		return this.m_medias[name];
	},
	
	putMedia: function (name, media) {
		this.m_medias[name] = media;
	},
	
	addLoader: function (name, loader) {
		this.m_loaders[name] = loader;
	},
	
	getLoader: function (name) {
		return this.m_loaders[name];
	},
	
	removeLoader: function(name) {
		if( this.m_loaders[name])
			delete this.m_loaders[name];
	},
	
	close: function() {
		// TODO
	/*	for( var name:String in m_loaders) {
			var loader:LoaderEx = this.m_loaders[name] as LoaderEx;
			try {
				loader.close();
			} catch( err:Error) {
				trace( err);
			}
			delete this.m_loaders[name];
		}*/
	}
	};
	
	return Env;
}());

	/**
	 * True if this Plan is sound enabled.
	 * This is not used, it should be deprecated...
	 */
//	public static final int AUDIO_BIT       = 0x01;

	/**
	 * True if this Plan displays Entities (Groups and Clusters)
	 * This is not used, it should be deprecated...
	 */
//	public static const GROUP_BIT:int= 0x02; TODO portage

	/**
	 * True if this Plan can be accessed using JavaScript.
	 * This is not used, it should be deprecated...
	 */
//	public static final int JSCRIPT_BIT     = 0x04;

	/**
	 * If a selection has this flag set, it can be displayed by an external UI
	 */
//	public static final int EXTERN_SEL      = 0x20; // 32

	/**
	 * Radius in pixels of the cycleMenu zone.
	 */
//	public static final int ZONE_RAD        = 50;


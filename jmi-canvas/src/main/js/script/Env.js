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
	var Env = function() {
	    /*
         * Contains XXX_BIT(s).
         * This is not used, it should be deprecated, but first removed from Model.java
         */
        this.flags = null;
        
        // Plan background Color.
        // :JMI.script.ColorX
        this.inCol = null; 
        
        /*
         * Plan border Color.
         * This is not used, it should be deprecated, but first removed from Model.java
         */
        // :JMI.script.ColorX
        this.outCol = null;
        
        /*
         * Plan filter color.
         * This color will apeared dimmed under the current zone and over the background, showing the actual BBox.
         * :JMI.script.ColorX
         */
        this.filterCol = null;
        
        /*
         * Applet size & scale.
         * This is used by the Server to retrieve the Applet size and then give a well sized plan to the client.
         * :JMI.script.Transfo
         */
        this.transfo = null;
        
        /*
         * Global properties.
         * Properties that don't appear in Zones because they are global to the plan.<br>
         * ex : paths of icons, name of user...
         * Those props are copied in Zones table during init.
         */
        this.props = null;
        
        /*
         * Table of the 32 selection name.
         * This is used by swatchs because they only know the selection name, not the ID.
         * It should be used by Javascript...but it isn't.
         */
        this.selections = null;
        
        /*
         * Table containing icons and sounds.
         * This buffer stores all media object using a unique key to load them asynchronously during init.
         */
        this.medias = null;
        
        //Table containing media loders.
        this.loaders = null;
        
        /**
         * A simple reference to the Applet.
         * This is necessary because the Thread must know the Applet.
         * But it is launch by run() that don't have any arguments.
         */
        // TODO : portage, applet à remplacer
        this.applet = null;
	};
	
    Env.prototype = {
        constructor: JMI.script.Env,
		

        /*
         * Initialize transient fields and set Applet background color.
         * 
         * @param applet
         * @param needPrint
         */ 
        init: function(applet, needPrint) {
            var bkWhite = "0xFFFFFF";
            var bkCol = needPrint ? bkWhite : inCol.getColor();
            this.applet = applet;
            medias = {};
            loaders = {};
        },
        
        getMedia: function(name) {
            return this.medias[name];
        },
        
        putMedia: function (name, media) {
            this.medias[name] = media;
        },

        addLoader: function (name, loader) {
            this.loaders[name] = loader;
        },	

        getLoader: function (name) {
            return this.loaders[name];
        },	
        
        removeLoader: function(name) {
            if(this.loaders[name]) {
                delete this.loaders[name];
            }
        },

        // TODO portage : gestion des loaders
        close: function() {
            /*	for( var name:String in loaders) {
             var loader:LoaderEx = this.loaders[name] as LoaderEx;
             try {
             loader.close();
             } catch( err:Error) {
             trace( err);
             }
             delete this.loaders[name];
             }*/
        }
	};
	
	return Env;
}());

// Constants
// TODO portage : à ajouter au fur et à mesure
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
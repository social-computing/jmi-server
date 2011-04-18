package  {
    import java.applet.Applet;
    import java.awt.AlphaComposite;
    import java.awt.Color;
    import java.awt.Cursor;
    import java.awt.Dimension;
    import java.awt.Font;
    import java.awt.FontMetrics;
    import java.awt.Graphics;
    //import java.awt.Graphics2D;
    import java.awt.Image;
    import java.awt.Point;
    import java.awt.Rectangle;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.MouseEvent;
    import java.awt.event.MouseListener;
    import java.awt.event.MouseMotionListener;
    import java.io.DataInputStream;
    import java.io.FilterInputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.ObjectInputStream;
    import java.io.PrintWriter;
    import java.io.StringWriter;
    import java.io.UnsupportedEncodingException;
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.net.URLConnection;
    import java.net.URLEncoder;
    import java.util.Hashtable;
    import java.util.StringTokenizer;
    import java.util.zip.GZIPInputStream;
    import netscape.javascript.*;
    
    /**
     * <p>Title: WPSApplet</p>
     * <p>Description: An Applet to display and interact with WPS Plan.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class WPSApplet extends Applet implements Runnable, ActionListener, MouseMotionListener, MouseListener, WaitListener/*, KeyListener, ComponentListener*/
    {
        /**
         * True if the browser using this Applet is a buggy IE with inconsistant drawing APIs.
         */
        protected   static var s_hasGfxInc:Boolean;
        
        /**
         * Environment data (Plan color, dimension,...) of the Plan.
         * It is retrieved from the server using serialization.
         */
        protected   var m_env:Env;
        
        /**
         * Plan to display in this Applet.
         * It is retrieved from the server using serialization.
         */
        protected   var m_plan:Plan;
        
        /**
         * Image used as a background on which the current zone is drawn.
         * It includes the background, and the zones rendered with their 'ghosted' satellites form the rest swatch.
         * The resulting image is then filtered with a transparency color.
         */
        protected   var m_backImgUrl:Image;
        protected   var m_backImg:Image;
        
        /**
         * Image used to quickly restore the aspect of a zone that is no longer current.
         * It includes the background + links + Satellites of each place at rest.
         */
        protected   var m_restImg:Image;
        
        /**
         * True if this is ready to be accessed by JavaScript.
         * Should be checked using isReady() method.
         */
        private		var m_isReady:Boolean;
        
        /**
         * True if all media data is fully downloaded.
         */
        protected	var m_isMediaReady:Boolean;
        
        /**
         * Member used to temporary store the current cursor position.
         */
        protected   var m_curPos:Point= new Point();
        
        /**
         * Current stage during the init process.
         * This is used for debug purpose and will be send by email in case of an exception.
         */
        protected	var m_debugStage:String= "Applet init";
        
        /**
         * Precise Error message if an exception occures.
         * This is used for debug purpose and will be send by email in case of an exception.
         */
        protected	var m_error:String= "none";
        
        /**
         * Size of serialized plan (returned by the server).
         *
         */
        protected	var m_WPSSize:int= 0;
        
        /**
         * System information.
         * This is used for debug purpose and will be send by email in case of an exception.
         */
        protected	var m_system:StringBuffer= new StringBuffer(); 
        /**
         * LiveConnect bridge to Javascript.
         * If it can't be initialized, an alternate page is used to simulate the bridge.
         */
        private     var m_planWindow:JSObject= null;
        
        /**
         * Thread used to detect double clicks.
         */
        private     var m_clickWaiter:Waiter= null;
        
        /**
         * Thread used to dynamicaly refresh the Plan during a resize.
         */
        private     var m_resizeWaiter:Waiter= null;
        
        /**
         * Thread that call the server at a fixed delay to keep the connection alive.
         */
        private     var m_serverWaiter:Waiter= null;
        
        /**
         * Delay (in ms) between 2 refresh during initialization
         */
        private     var m_delay:Number;
        
        /**
         * The bounding box of the initialisation jauge.
         */
        private     var m_loaderBnds:Rectangle= new Rectangle();
        
        /**
         * First static part of the message displayed on top of the jauge before the dynamic part.
         */
        private     var m_preMsg:String;
        
        /**
         * Second static part of the message displayed on top of the jauge after the dynamic part.
         */
        private     var m_postMsg:String;
        
        /**
         * Dynamic part of the message displayed on top of the jauge. Usually a percentage.
         */
        private     var m_refPos:int;
        
        /**
         * Header of the HTTP connections.
         * This is used for debug purpose and will be send by email in case of an exception.
         */
        private     var m_httpHdr:String;
        
        /**
         * Color of the messages displayed while Applet is loading.
         */
        private     var m_msgCol:Color;
        
        /**
         * Theorical size of the Applet.
         * As IE JVM is not reliable when asking the size of the Applet,
         * an alternate one must be guessed to avoid allocation problems.
         */
        protected	var m_size:Dimension;
        
        protected var m_composite:AlphaComposite;
        
        /**
         * Launch the initialisation thread.
         * The main Thread manage the GUI (Repaint, mouse, jauge...)
         **/
        public function init( ):void {
            var build:String= null;
            addJSonParam( m_system, "os", System.getProperty( "os.name" ), true);
            addJSonParam( m_system, "osVers", System.getProperty( "os.version" ), false);
            addJSonParam( m_system, "arch", System.getProperty( "os.arch" ), false);
            addJSonParam( m_system, "java", System.getProperty( "java.class.version" ), false);
            
            // MS JVM build
            // ON try { build = com.ms.util.SystemVersionManager.getVMVersion().getProperty( "BuildIncrement" ); } catch ( Throwable t ){}
            // Mac MRJ version
            if ( build == null ) try { build = System.getProperty( "mrj.version" ); } catch ( t:Throwable){}
            if ( build != null )
                addJSonParam( m_system, "VM", build, false);
            //      LiveConnect!
            try  { m_planWindow = JSObject.getWindow( this );} catch( t:Throwable) { m_planWindow = null; }
            
            // Start a new thread that will retrieve the Plan and initialize it.
            ( new Thread( this, "WPSStarter" )).start();
        }
        
        /**
         * Downloading and initialize the Plan.
         * Also perform Applet initialization and if an exception happens, notify the support by sending an email.
         * This is a very sensitive piece of code, beware of the order of each steps!
         */
        public synchronized function run():void {
            var inStm:InputStream= null;
            var colorStr:String= getParameter( "InitColor" );
            var n:int= 0;
            
            m_debugStage    = "Parameters retrieval";
            setBackground( new Color( colorStr == null ? 0: Integer.parseInt( colorStr, 16)));
            colorStr		= getParameter( "MsgColor" );
            m_msgCol		= new Color( colorStr == null ? 0x: Integer.parseInt( colorStr, 16));
            
            // If the browser is IE / Windows, drawing APIs are not consistant, this handle the case.
            s_hasGfxInc     = System.getProperty( "java.vendor" ).toLowerCase().indexOf( "microsoft" )!= -1&& System.getProperty( "os.name" ).toLowerCase().indexOf( "windows" )!= -1;
            
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
            m_size	= super.getSize();
            
            try
            {
                m_debugStage    = "Dimension waiting";
                // Wait until the real applet size is known, thanks IE for this BUG!
                while (( m_size.width <= 0|| m_size.height <= 0|| m_size.width > 2048|| m_size.height > 2048)&& n < 20)
                {
                    wait( 100);
                    m_size = super.getSize();
                    n ++;
                }
                
                if ( n == 20)	m_size = null;
                m_size			= getSize();
                m_loaderBnds    = getBounds();
                
                var servletParams:String= "width=" +( m_size.width < 100? 100: m_size.width )+ "&height=" +( m_size.height < 100? 100: m_size.height )+ "&" + getParameter( "WPSParameters" ),
                    //planFilename    = getParameter( "PlanFile" ),
                    computeMsg      = getParameter( "ComputeMsg" ),     // "Connecting to WPS Server"
                    downloadMsg		= getParameter( "DownloadMsg" ),    // "Downloading your Plan"
                    //					initMsg			= getParameter( "initMsg" );        // "Initializing your plan"
                    jsCallback      = getParameter( "OnMapReadyFunc" ),
                    bgImageUrl		= getParameter( "bgImageUrl" ),
                    needPrint      	= getParameter( "NeedPrint" );
                
                var g:Graphics= getGraphics();
                
                if ((bgImageUrl!=null) && (bgImageUrl.length()!=0))
                    try 
                {
                    var toolkit:java.awt.Toolkit= java.awt.Toolkit.getDefaultToolkit();
                    var url:java.net.URL= new URL(bgImageUrl);
                    m_backImgUrl	= toolkit.getImage(url);
                    if (m_backImgUrl!=null) // draw background picture
                        g.drawImage( m_backImgUrl, 0, 0, null );
                }
                catch( e:Exception)
                {
                    m_error	= "bgImageUrl error";
                    throw ( new RuntimeException( e.getMessage()));
                }
                
                m_debugStage    = "First refresh";
                setRefresh( computeMsg, null, 0);
                
                m_debugStage    = "Servlet stream retrieval";
                /*if ( planFilename != null ) // stream from a file, should disapear in release
                {
                inStm = new FileInputStream( getCodeBase().getFile() + planFilename );
                }
                else*/                        // stream from a Servlet
                //{
                inStm = getBinaryStream( this, addCGIParam( getParameter( "ServletURL" ), servletParams, true ), false);
                //}
                
                m_debugStage    = "Plan size reading";
                var iDataStream:DataInputStream= new DataInputStream( inStm );
                m_WPSSize = iDataStream.readInt();
                
                if ( m_WPSSize > 0) // A Plan exists
                {
                    m_debugStage    = "zipped Object stream creation";
                    var is1:ObjectInputStream= new ObjectInputStream( new GZIPInputStream ( new JaugeInputStream( inStm, m_WPSSize, downloadMsg ), 1024));
                    
                    // deserialize and init the Env object
                    m_debugStage    = "Env reading";
                    m_env   = Env(is1.readObject());
                    
                    m_debugStage    = "Env init";
                    m_env.init( this, needPrint != null && needPrint.equalsIgnoreCase( "true" ));
                    
                    // deserialize and init the Plan object
                    m_debugStage    = "Plan reading";
                    m_plan  = Plan(is1.readObject());
                    
                    //Graphics    g   = getGraphics();
                    
                    m_debugStage    = "Offscreen images";
                    try
                    {
                        
                        m_backImg	= createImage( m_size.width, m_size.height );
                        m_restImg	= createImage( m_size.width, m_size.height );
                    }
                    catch ( e:Exception)
                    {
                        m_error	= "offscreenInit";
                        throw ( new RuntimeException( e.getMessage()));
                    }
                    
                    m_plan.m_applet     = this;
                    m_plan.m_curSel     = -1;
                    m_plan.m_waiters    = new Hashtable();
                    
                    m_debugStage    = "Zones init";
                    m_plan.initZones( g, m_plan.m_links, true );
                    m_plan.initZones( g, m_plan.m_nodes, true );
                    
                    m_debugStage    = "Plan resize";
                    m_plan.resize( m_size );
                    
                    m_debugStage    = "Plan init";
                    m_plan.init();
                    
                    m_debugStage    = "Plan resize";
                    m_plan.resize( m_size );
                    
                    // init listeners of the UI
                    addMouseListener( this );
                    addMouseMotionListener( this );
                    //				addKeyListener( this );
                    showStatus( "" );
                    
                    m_isReady	= true;
                    
                    try
                    {
                        m_debugStage    = "JSObject call";
                        if( jsCallback != null )
                        {
                            var t:Hashtable= new Hashtable();
                            t.put( "ENV", m_env.m_props);
                            var s:String= Base.parseString( jsCallback, t, false);
                            performAction( s);
                        }
                    }
                    catch ( e:Exception)
                    {
                        System.out.println( "JSObject not ready" );
                    }
                    
                    m_debugStage    = "First paint";
                    repaint();
                    
                    // launch a thread to load icons asynchronously
                    m_debugStage    = "Icon loader creation";
                    ( new Thread( m_env, "WPSMedia" )).start();
                    
                    // launch a thread to wakeup the Server
                    var wakeUpStr:String= getParameter( "WakeUpURL" ),
                        delayStr	= getParameter( "WakeUpDelay" );
                    
                    if ( wakeUpStr != null )
                    {
                        m_debugStage    = "Server waiter creation";
                        var delay:int= delayStr != null ? Integer.parseInt( delayStr ) : 300000;
                        
                        m_serverWaiter = new Waiter( this, new Object[]{ convertURL( wakeUpStr )}, 0, delay );
                        m_serverWaiter.start();
                    }
                    
                    
                    
                    m_debugStage = "Running";
                }
                else    // The Plan is void or can't be delivered, let's show a page to explain why!
                {
                    m_debugStage    = "Void map";
                    if( m_WPSSize == 0)
                    {
                        try
                        {
                            var callback:String= getParameter( "OnVoidMapFunc" ); // Empty Plan
                            if( callback != null )
                            {
                                var t:Hashtable= new Hashtable();
                                var props:Hashtable= m_env == null ? new Hashtable() : m_env.m_props;
                                props.put("$err-context", m_system.toString());
                                t.put( "ENV", props);
                                var s:String= Base.parseString( callback, t, false);
                                performAction( s);
                            }
                        }
                        catch ( e:Exception)
                        {
                            System.out.println( "JSObject not ready" );
                        }
                    }
                    else
                    {
                        var serverErr:String= null;
                        try {
                            serverErr = iDataStream.readUTF();
                        }
                        catch ( e:IOException){}
                        handleError( false, (serverErr == null ? "WPS server error" : serverErr));
                    }
                }
            }
            catch( e:Exception)
            {
                handleError( e );
            }
            finally
            {
                if ( inStm != null )
                {  // Vidange du flux HTTP !!! Obligatoire
                    try {
                        while ( inStm.read() != -1) ;
                        inStm.close();
                    }
                    catch ( e:IOException){}
                }
            }
            setCursor( Cursor.getDefaultCursor());
        }
        
        /**
         * Send an email to the support if any exception occures during the initialization process.
         * The mail contains many parameters in CGI format:
         * <ul>
         * <li>stack : jvm callstack at the exception moment.</li>
         * <li>header : HTTP header of the last connection.</li>
         * <li>jsObj : LiveConnect status.</li>
         * <li>size : Applet theorical size.</li>
         * <li>java : Java Class version.(1.1=45.3, 1.2=46, 1.3=47, 1.4=48)</li>
         * <li>version : WPSApplet version.</li>
         * <li>stage : Name of the stage where the exception happens.</li>
         * <li>pb : Details about the error. This is set only in important part.</li>
         * </ul>
         * @param e	The exception that cause this methode to be called.
         */
        private function handleError( e:Exception):void {
            var writer:StringWriter= new StringWriter();
            e.printStackTrace();
            e.printStackTrace( new PrintWriter( writer ));
            handleError( true, writer.toString());
        }
        
        private function handleError( client:Boolean, stack:String):void {
            try
            {
                addJSonParam( m_system, "source", (client ? "client" : "server"), false);
                addJSonParam( m_system, "stack", stack, false);
                addJSonParam( m_system, "header", m_httpHdr == null ? "null" : m_httpHdr, false);
                addJSonParam( m_system, "jsObj", m_planWindow == null ? "null" : "OK", false);
                addJSonParam( m_system, "size", m_size.toString(), false);
                addJSonParam( m_system, "stage", m_debugStage, false);
                addJSonParam( m_system, "pb", m_error, false);
                addJSonParam( m_system, "wpssize", String.valueOf( m_WPSSize), false);
                addJSonParam( m_system, "version", AppletVersion.APPLET_VERSION, false);
                try
                {
                    var callback:String= getParameter( "OnErrorMapFunc" ); 
                    if( callback != null )
                    {
                        var t:Hashtable= new Hashtable();
                        var props:Hashtable= m_env == null ? new Hashtable() : m_env.m_props;
                        props.put("$err-context", m_system.toString());
                        t.put( "ENV", props);
                        var s:String= Base.parseString( callback, t, false);
                        performAction( s);
                    }
                }
                catch ( e:Exception)
                {
                    System.out.println( "JSObject not ready" );
                }
            }
            catch( ex:Exception) {
                ex.printStackTrace();
            }
        }
        
        /**
         * A simple wrapper to enable double buffering.
         * This simply calls update( g ).
         * This method is called by the GUI, don't use it directly!
         * @param g	The Graphics of the Applet.
         */
        public function paint( g:Graphics):void {
            update( g );
        }
        
        /**
         * A wrapper to handle double buffering and initial jauge refresh.
         * If the Applet is Ready, draws the Plan. In fact the background is simply blited
         * and only the current Zone is drawn. This avoid CPU overhead and flickering.
         * Else refresh the init jauge.
         * @param g
         */
        public function update( g:Graphics):void {
            if ( g != null && isReady())
            {
                g.drawImage( m_restImg, 0, 0, null );
                m_plan.paintCurZone( g );  // A new Zone is hovered, let's paint it!
            }
            else
            {
                refresh( -1, false );
            }
        }
        
        /**
         * Wrapper to force image loading because MediaTracker class is buggy in IE implementation.
         * The code is handled in Env.init(). This is only called by the Awt framework.
         * @return true if all the images are loaded.
         */
        public function imageUpdate( img:Image, infoflags:int, x:int, y:int, width:int, height:int):Boolean {
            return m_isMediaReady;
        }
        
        /**
         * True is the Applet is loaded and the Plan initialized.
         * this is used by Javascript to know when to start calling the Applet through LiveConnect
         */
        public function isReady():Boolean {
            return m_plan != null && m_isReady;
        }
        
        /**
         * Sets the jauge refresh message and display it.
         * @param preMsg	Static text before the counter. If null, nothing is displayed.
         * @param postMsg	Static text after the counter. If null, the counter is not displayed.
         * @param pos		Counter position.
         */
        protected function setRefresh( preMsg:String, postMsg:String, pos:int):void {
            m_preMsg        = preMsg;
            m_postMsg       = postMsg;
            m_refPos        = pos;
            m_delay         = System.currentTimeMillis();    // init refresh timer!
            
            refresh( pos, true );
        }
        
        /**
         * Refresh a jauge during the initialization process.
         * The refresh is asynchronous, it wait at least 50ms between 2 redraws.
         * This avoid CPU overhead if the call frequency is too important.
         * SetRefresh should be called before changing the message to display (not during the counter progression).
         * @param pos		The counter position.
         * @param isInit	True force immediate refresh and evaluate the bounding box of the text.
         */
        function refresh( pos:int, isInit:Boolean):void {
            var font:Font= new Font( "SansSerif", Font.BOLD, 24);
            
            try // magnifique rustine qui aurra probablement des effets de bord...
            {
                if ( pos >= 0&& pos <= 100)    m_refPos    = pos;
                
                if (( System.currentTimeMillis()- m_delay > 50|| isInit || pos == -1)&& m_preMsg != null )
                {
                    var g:Graphics= getGraphics();
                    
                    if ( g != null )
                    {
                        var dim:Dimension= getSize();
                        var x:int= dim.width >> 1,
                            y   = dim.height >> 1;
                        
                        if (m_backImgUrl!=null) // draw background picture
                            g.drawImage( m_backImgUrl, 0, 0, null );
                        else // fill rect with background color
                            g.clearRect( m_loaderBnds.x, m_loaderBnds.y - m_loaderBnds.height, m_loaderBnds.width, m_loaderBnds.height + 19);
                        
                        g.setFont( font );
                        
                        if ( isInit )
                        {
                            var fm:FontMetrics= g.getFontMetrics();
                            var w:int= fm.stringWidth( m_preMsg + "99" + m_postMsg ),
                                h   = fm.getHeight();
                            
                            m_loaderBnds = new Rectangle( x -( w >> 1), y -( h >> 1), w, h );
                        }
                        
                        var msg:String= m_preMsg;
                        
                        if ( m_postMsg != null )
                        {
                            g.setColor( Color.black );
                            g.fillRect( m_loaderBnds.x + 10, m_loaderBnds.y + 8, m_loaderBnds.width - 30, 10);
                            g.setColor( Color.white );
                            g.fillRect( m_loaderBnds.x + 11, m_loaderBnds.y + 9, ( pos *( m_loaderBnds.width - 30))/ 100, 8);
                            msg += ' ' + String.valueOf( m_refPos ) + m_postMsg;
                        }
                        g.setColor( m_msgCol );
                        g.drawString( msg, m_loaderBnds.x, m_loaderBnds.y );
                        g.dispose();
                        
                        getToolkit().sync();
                        showStatus( msg );
                    }
                    m_delay = System.currentTimeMillis();
                }
            }
            catch ( e:Exception){}
        }
        
        /**
         * Wrapper for a menu item that call performAction with the ActionCommand String as argument.
         **/
        public function actionPerformed( e:ActionEvent):void {
            try {
                performAction( e.getActionCommand());
            } catch (e1:UnsupportedEncodingException) {
                e1.printStackTrace();
            }
        }
        
        /**
         * Perform an URL action.
         * The action depends on the string passed:
         * <ul>
         * <li>URL : Opens the URL in a new window.</li>
         * <li>_target:URL : Opens the URL in the frame called target if it exists or else in a new window whose name is set to target.</li>
         * <li>javascript:function(args) : If LiveConnect is enabled, call the Javascript function with args (arg1,..,argn).
         * Else, if an alternate page is defined (NoScriptUrl Applet parameter), this page is opened with the function(args) passed using the CGI syntax.</li>
         * <li>javascript:_target:function(args) : See javascript:function(args) and _target cases.</li>
         * </ul>
         * @param actionStr		An URL like string describing what action to do.
         * @throws UnsupportedEncodingException 
         */
        public function performAction( actionStr:String):void {
            var jsStr:String= "javascript";
            
            try
            {
                var target:String= "_blank";
                var sep:int= actionStr.indexOf( ':' );
                
                if ( sep != -1)
                {
                    target  = actionStr.substring( 0, sep );
                    
                    if( target.equalsIgnoreCase( jsStr ))   // Call javascript function
                    {
                        actionStr   = actionStr.substring( jsStr.length()+ 1);
                        if( actionStr.charAt( 0)== '_' )
                        {	// javascript:_target:function()
                            var pos:int= actionStr.indexOf( ':' );
                            if( pos <= 0) return;
                            target      = actionStr.substring( 1, pos );
                            actionStr   = actionStr.substring( pos + 1);
                        }
                        
                        // LiveConnect!
                        if( m_planWindow != null)
                        {
                            var pos:int= actionStr.indexOf( '(' );
                            
                            if( pos > 0)
                            {
                                var func:String= actionStr.substring( 0, pos ),
                                    paramStr    = actionStr.substring( pos + 1, actionStr.length()- 1);
                                var params:Array= Base.getTextParts( paramStr, "," );
                                
                                m_planWindow.call( func, params );
                            }
                            return;
                        }
                        else    // Javascript not supported try to emulate it, if possible
                        {
                            var noScriptUrl:String= getParameter( "NoScriptUrl" );
                            
                            if( target.equalsIgnoreCase( "null" )|| noScriptUrl == null )	return;
                            
                            actionStr = addCGIParam( noScriptUrl, "func=" + URLEncoder.encode( actionStr , "UTF-8" ), true );
                        }
                    }
                    else if( target.charAt( 0)== '_' )   // open a frame window
                    {
                        target      = actionStr.substring( 1, sep );
                        actionStr   = actionStr.substring( sep + 1);
                    }
                    else
                    {
                        target  = "_blank";
                    }
                }
                //System.out.println( actionStr + " in " + target);
                getAppletContext().showDocument( convertURL( actionStr ), target );
            }
            catch ( ex:MalformedURLException){}
        }
        
        /**
         * Gets the number of attributes in this map including clusterized ones.
         * Called by JavaScript.
         * @return  the attribute count.
         */
        public function getAttCount():int {
            return m_plan.m_nodes.length;
        }
        
        /**
         * Gets the number of links in this map.
         * Called by JavaScript.
         * @return  the links count.
         */
        public function getLinkCount():int {
            return m_plan.m_linksCnt;
        }
        
        /**
         * Gets an attribute property knowing its index.
         * If the property is an array, returns a string representation of the array
         * where objects are separated by '\n'.
         * Called by JavaScript.
         * @param   index       The index in the attribute in the table.
         * @param   propName    The name of the property to retrieve.
         * @return  the attribute property as specified in the Dictionary.
         */
        public function getAttProp( index:int, propName:String):Object {
            return getProp( m_plan.m_nodes[index], propName );
        }
        
        /**
         * Gets a link property knowing its index.
         * If the property is an array, returns a string representation of the array
         * where objects are separated by '\n'.
         * Called by JavaScript.
         * @param   index       The index in the link in the table.
         * @param   propName    The name of the property to retrieve.
         * @return  the link property as specified in the Dictionary.
         */
        public function getLinkProp( index:int, propName:String):Object {
            return getProp( m_plan.m_links[index], propName );
        }
        
        /**
         * Gets an environment property knowing its index.
         * If the property is an array, returns a string representation of the array
         * where objects are separated by '\n'.
         * Called by JavaScript.
         * @param   propName    The name of the property to retreive.
         * @return  the attribute prop as specified by its swatch.
         */
        public function getEnvProp( propName:String):Object {
            return getProp( m_env.m_props, propName );
        }
        
        /**
         * Select a list of attributes.
         * The display must be refreshed to reflect the new selection.
         * Called by JavaScript.
         * @param   attIds  A list of attributes index separated by ','.
         * @param selNam	A selection name as defined in the Dictionary.
         */
        public function setAttSelection( attIds:String, selNam:String):void {
            setZoneSelection( attIds, selNam, m_plan.m_nodes, m_plan.m_nodes.length );
        }
        
        /**
         * Select a list of links.
         * The display must be refreshed to reflect the new selection.
         * Called by JavaScript.
         * @param linkIds	A list of links index separated by ','.
         * @param selNam	A selection name as defined in the Dictionary.
         */
        public function setLinkSelection( linkIds:String, selNam:String):void {
            setZoneSelection( linkIds, selNam, m_plan.m_links, m_plan.m_linksCnt );
        }
        
        /**
         * Remove attributes from a selection.
         * The display must be refresh to reflect the new selection.
         * Called by JavaScript.
         * @param selNam	A selection name as defined in the Dictionary.
         */
        public function clearAttSelection( selNam:String):void {
            clearZoneSelection( selNam, m_plan.m_nodes, m_plan.m_nodes.length );
        }
        
        /**
         * Remove links from a selection.
         * The display must be refresh to reflect the new selection.
         * Called by JavaScript.
         * @param selNam	A selection name as defined in the Dictionary.
         */
        public function clearLinkSelection( selNam:String):void {
            clearZoneSelection( selNam, m_plan.m_links, m_plan.m_linksCnt );
        }
        
        /**
         * Sets the currently displayed selection.
         * Called by JavaScript.
         * @param selNam	A selection name as defined in the Dictionary.
         */
        public function setSelection( selNam:String):void {
            var selId:int= getSelId( selNam );
            
            m_plan.m_curSel = selId;
            m_plan.init();
        }
        
        /**
         * Gets the id of a selection, knowing its name.
         * @param selNam	A selection name as defined in the Dictionary.
         * @return			An ID in [0,31] or -1 if the selection name is unknown.
         */
        private function getSelId( selNam:String):int {
            var selId:int= Integer(m_env.m_selections.get( selNam ));
            
            return  selId != null ? selId.intValue() : -1;
        }
        
        /**
         * Create an URL using a relative or absolute string representation.
         * @param urlStr	An absolute (http://) or relative URL.
         * @return			The URL corresponding to urlStr
         * @throws MalformedURLException
         */
        protected function convertURL( urlStr:String):URL {
            return //urlStr.startsWith( "http") ?
            //new URL( urlStr ) :              
            new URL( getCodeBase(), urlStr );  
        }
        
        /**
         * Gets a stream to read using an URL.
         * @param applet			Applet holding the debug information.
         * @param url				URL to open for reading.
         * @param needHeaderCheck	True if the HTTP header must be checked to avoid redirs.
         * @return					A stream to the URL or null if the header must be checked and is != 200.
         * @throws Exception
         */
        protected static function getBinaryStream( applet:WPSApplet, url:String, needHeaderCheck:Boolean):InputStream {
            applet.m_debugStage    = "Open Connection";
            var connect:URLConnection= applet.convertURL( url ).openConnection();
            
            applet.m_debugStage    = "Connection setting";
            connect.setDoInput( true );
            connect.setDoOutput( false );
            connect.setUseCaches( false );
            connect.setAllowUserInteraction( true );
            connect.setRequestProperty( "Content-Type", "application/octet-stream" );
            
            // Deprecated since 1.8.9
            // Contournement bug Mozilla 0.9.1
            if( connect.getRequestProperty( "COOKIE" )== null)
                connect.setRequestProperty( "COOKIE", "JSESSIONID=" + applet.getParameter( "SessionId" ));
            var basicAuthB64:String= applet.getParameter( "BasicAuthBase64");
            if( basicAuthB64 != null)
                connect.setRequestProperty( "Authorization", "Basic " + basicAuthB64);
            // End deprecated
            
            // deprecated replaced with :
            // HTTP headers
            for( var i:int= 0; i < 10; ++i)
            {
                var hname:String= applet.getParameter( "HTTPHeaderName"+i);
                var setValue:String= applet.getParameter( "HTTPHeaderSetValue"+i);
                var setIfEmptyValue:String= applet.getParameter( "HTTPHeaderSetIfEmptyValue"+i);
                var addValue:String= applet.getParameter( "HTTPHeaderAddValue"+i);
                if( hname != null && (setValue != null || setIfEmptyValue != null || addValue != null))
                {
                    if( setValue != null)
                        connect.setRequestProperty( hname, setValue);
                    else
                        if( setIfEmptyValue != null && connect.getRequestProperty( hname) == null)
                            connect.setRequestProperty( hname, setIfEmptyValue);
                        else
                        {
                            setValue = connect.getRequestProperty( hname);
                            if( setValue != null)
                                addValue += ";" + setValue;
                            connect.setRequestProperty( hname, addValue);
                        }
                }
            }
            connect.connect();
            
            applet.m_debugStage    = "HTTP Header retrieval";
            var header:String= connect.getHeaderField( 0);
            if ( header == null )  header = connect.getHeaderFieldKey( 0);
            
            applet.m_httpHdr = header;
            
            if( needHeaderCheck )
                return ( header != null && header.indexOf( "200" ) != -1)||( header == null )? connect.getInputStream() : null;
            
            return connect.getInputStream();
        }
    }
    
    import java.awt.event.MouseEvent;
    import java.io.InputStream;
    import java.applet.Applet;
    import java.awt.event.MouseListener;
    import java.util.StringTokenizer;
    import java.awt.Dimension;
    import java.net.URLEncoder;
    import java.net.URL;
    import java.awt.event.MouseMotionListener;
    import java.awt.Point;
    import java.io.FilterInputStream;
    import netscape.javascript.*
        import java.util.Hashtable;
    import java.io.UnsupportedEncodingException;
    
    /**
     * <code>JaugeInputStream</code> displays a progress bar as it reads its Stream.
     * <p>
     * A refresh is called only if the progress has incremented by one step.
     * There are potentialy 100 steps to complete the download.
     *
     * @author      Franck Lugu�
     */
    /**
     * <p>Title: JaugeInputStream</p>
     * <p>Description: An input stream that shows the progress of its reading.<br>
     * The size of the data to read must be known.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    private 
    internal class JaugeInputStream extends FilterInputStream
    {
        /**
         * Total size (in bytes) of the stream to download.
         */
        private var m_size:int= 0;
        
        /**
         * Current size (in bytes) of the stream already downloaded.
         */
        private var m_bytesCnt:int= 0;
        
        /**
         * Previous jauge position.
         * This is a percentage in [0,99].
         */
        private var m_oldPos:int= 0;
        
        /**
         * Message to display in on the left side of the percentage.
         */
        private var m_message:String;
        
        /**
         * Creates a new JaugeInputStream giving the size of the data to download and the stream to use.
         * @param is	the stream to read from.
         * @param size	the size in byte of the data to download.
         */
        public function JaugeInputStream( is:InputStream, size:int, message:String)
        {
            super( is );
            
            m_size      = size;
            m_message   = message;
            
            setRefresh( m_message, "%", 0);
        }
        
        /**
         * Wrapper to refresh the jauge while some bytes are read.
         * Only the Java.io framework should call this.
         * The refresh method is called only if the percentage has changed.
         */
        public function read( b:Array, off:int, len:int):int {
            var n:int= super.read( b, off, len );
            
            m_bytesCnt += n;
            
            if ( m_oldPos < 100)
            {
                var pos:int= ( 100* m_bytesCnt )/ m_size;
                
                if ( pos > m_oldPos && pos <= 100)
                {
                    m_oldPos = pos;
                    refresh( pos, false );
                }
            }
            return n;
        }
    }
    
    /**
     * Wrapper to clean up media and threads allocated by the Applet before it dies.
     * This is probably useless now...
     */
    public function destroy():void {
        /*int n = Thread.activeCount();
        
        Thread[]    threads = new Thread[n];
        Thread.enumerate( threads );
        
        for ( int i = threads.length - 1; i >= 0; i -- )
        {
        if ( threads[i].getName().startsWith( "WPS" ))
        {
        threads[i].stop();
        }
        }
        
        if ( m_plan != null )
        {
        m_plan = null;
        }
        
        if ( m_env != null )
        {
        m_env.clearMedias();
        m_env = null;
        }
        
        removeMouseListener( this );
        removeMouseMotionListener( this );
        
        System.gc();*/
    }
    
    /**
     * Callback to handle the waiters this Applet listen to.
     * The DoubleClick, Alarm and resize are handled here.
     */
    public function stateChanged( params:Array, state:int):void {
        if ( m_plan != null )
        {
            if ( params != null )
            {
                var obj:Object= params[0];
                
                if ( obj is Point ) // Simple or DoubleClick
                {
                    var pos:Point= Point(params[0]);
                    var event:int= Satellite.CLICK_VAL;
                    
                    switch ( state )
                    {
                        case WaitListener.INIT:
                        {
                            m_plan.updateZoneAt( pos );
                            break;
                        }
                            
                        case WaitListener.INTERRUPTED:
                            event   = Satellite.DBLCLICK_VAL;
                            
                        case WaitListener.END:
                        {
                            if ( m_plan.m_curSat != null )
                            {
                                m_plan.m_curSat.execute( WPSApplet.this, m_plan.m_curZone, pos, event );
                            }
                        }
                    }
                }
                else    // Server Alarm
                {
                    if ( state == WaitListener.END )
                    {
                        try
                        {
                            (URL(params[0])).openConnection().getInputStream().close();
                            m_serverWaiter.m_loop = true;
                        }
                        catch ( e:Exception){}
                    }
                }
            }
            else    // Window resize
            {
                if ( state == WaitListener.END )
                {
                    m_size	= null;
                    m_size	= getSize();
                    m_plan.resize( m_size );
                    m_env.clearMedias();
                    m_plan.init();
                    
                    // launch a thread to load icons asynchronously
                    ( new Thread( m_env, "WPSMedia" )).start();
                }
            }
        }
    }
    
    /**
     * Wrapper to handle the getSize methode in case it returns wrong values.
     * This is a patch for IE who sometimes return width or height <= 0 or >>2048 just to annoye me!
     * @return	A "legal" size (800x600) in case of dumb size.
     */
    public function getSize():Dimension {
        if ( m_size == null )
        {
            m_size	= super.getSize();
            
            if ( m_size.width <= 0|| m_size.width > 2048||
                m_size.height <= 0|| m_size.height > 2048)
            {
                m_size.width	= 800;
                m_size.height	= 600;
            }
        }
        
        return m_size;
    }
    
    /**
     * Redirect clicks to a waiter to detect double clicks.
     * The waiter separate simple and double-clicks by waiting 200 ms.
     */
    public function mousePressed( e:MouseEvent):void {
        if ( m_clickWaiter != null && m_clickWaiter.isAlive())
        {
            m_clickWaiter.m_isInterrupted = true;
        }
        else
        {
            m_clickWaiter = new Waiter( this, new Object[]{ e.getPoint()}, 0, 200);
            m_clickWaiter.start();
        }
    }
    
    /**
     * Update the current Zone or subZone when the cursor moves.
     */
    public function mouseMoved( e:MouseEvent):void {
        m_curPos    = e.getPoint();
        m_plan.updateZoneAt( m_curPos ); // The Zone, SubZone or Satellite can have changed
    }
    
    /**
     * Wrapper to handle the Applet automatic resize.
     * When the Applet size is set to "100%", the resize can be dynamic.
     * This only works with IE Windows and Mozilla (and NS)
     * A waiter filter resize events to guess when the user stop the resize.
     */
    public function reshape( x:int, y:int, w:int, h:int):void {
        super.reshape(x,y,w,h);
        
        if ( isReady())
        {
            if ( m_resizeWaiter != null && m_resizeWaiter.isAlive())
            {
                m_resizeWaiter.m_loop = true;
            }
            else
            {
                m_resizeWaiter = new Waiter( this, null, 0, 100);
                m_resizeWaiter.start();
            }
        }
    }
    
    // Necessary empty wrappers to implement the MouseListener interface.
    // An anonymous MouseAdapter inner class is bigger!
    public function mouseClicked( e:MouseEvent):void {}
    public function mouseReleased( e:MouseEvent):void {}
    public function mouseDragged( e:MouseEvent):void {}
    public function mouseEntered( e:MouseEvent):void {}
    public function mouseExited( e:MouseEvent):void {}
    
    /**
     * Retrieve a propertie from a table so it can be sent to Javascript through LiveConnect.
     * If the property is an array, returns a string representation of the array
     * where objects are separated by '\n'.
     * @param propTab
     * @param propName
     * @return
     */
    private function getProp( propTab:Hashtable, propName:String):Object {
        var propStr:String= null;
        var prop:Object= propTab.get( propName );
        
        if ( prop != null )
        {
            if ( prop is Object[] )
            {
                var props:Array= (Object[])prop;
                var i:int, n    = props.length;
                
                propStr = props[0].toString();
                
                for ( i = 1; i < n; i ++ )
                {
                    propStr = propStr + '\n' + props[i];
                }
            }
            else
            {
                propStr = prop.toString();
            }
        }
        
        return propStr;
    }
    
    /**
     * Select a list of zones.
     * The display must be refreshed to reflect the new selection.
     * @param   ids		A list of zones index separated by ','.
     * @param selNam	A selection name as defined in the Dictionary.
     * @param zones		An array of Zones (Nodes or Links).
     * @param n			Number of zone to select in the array, starting from index 0.
     */
    private function setZoneSelection( ids:String, selNam:String, zones:Array, n:int):void {
        var selId:int= getSelId( selNam );
        
        if ( selId != -1)
        {
            var tokens:StringTokenizer= new StringTokenizer( ids, "," );
            var index:int;
            
            selId = 1<< selId;
            
            while( tokens.hasMoreElements())
            {
                index                       = Float.valueOf( tokens.nextToken()).intValue();
                zones[index].m_selection   |= selId;
            }
        }
    }
    
    /**
     * Remove zones from a selection.
     * The display must be refresh to reflect the new selection.
     * @param selNam	A selection name as defined in the Dictionary.
     * @param zones		An array of Zones (Nodes or Links).
     * @param n			Number of zone to remove from selection in the array, starting from index 0.
     */
    private function clearZoneSelection( selNam:String, zones:Array, n:int):void {
        var selId:int= getSelId( selNam );
        
        if ( selId != -1)
        {
            var i:int, unselBit	= ~( 1<< selId );
            
            for( i = 0; i < n; i ++ )
            {
                zones[i].m_selection &= unselBit;
            }
        }
    }
    
    /**
     * Add a CGI couple (param=value) to a string or a CGI line of parameters to an URL.
     * @param param		An URL or a parameter name, depending on the isURL value.
     * @param value		A CGI line of params or a value corresponding to its parameter, depending on the isURL value.
     * @param isURL		True if param is an URL and value is a CGI line of parameters.
     * @return			A couple "&param=value" or an URL followed by a CGI line "param?value".
     * @throws UnsupportedEncodingException 
     */
    private function addCGIParam( param:String, value:String, isURL:Boolean):String {
        if ( isURL )
        {
            var pos:int= param.indexOf( '?' );
            
            if( pos == -1)	param += '?';
            else if( pos < param.length() - 1)	param += '&';
            
            return param + value;
        } else
            try {
                return '&' + param + '=' + URLEncoder.encode( value , "UTF-8" );
            } catch (e:UnsupportedEncodingException) {
                e.printStackTrace();
                return "";
            }
    }
    private function addJSonParam( buffer:StringBuffer, param:String, value:String, first:Boolean):StringBuffer {
        if( !first)
            buffer.append(',');
        var evalue:String="";
        try {
            evalue = URLEncoder.encode( value , "UTF-8" );
        } catch (e:UnsupportedEncodingException) {
        }
        return buffer.append(param).append( ":\"").append(evalue).append('"');
    }
}
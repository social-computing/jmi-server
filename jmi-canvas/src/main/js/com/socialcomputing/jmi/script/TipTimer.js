
JMI.namespace("com.socialcomputing.wps.script.TipTimer") = (function() {
		
    // :Timer;
    var timer,
    
        //:int
	    start, 
	    
	    //:int
	    duration,
	    
	    //:Boolean
	    _isInterrupted,
	    
	    // :Plan
	    _plan,
	    
	    // :ActiveZone
	   _zone,
	   
	   // :Slice
	   _slice,
	
	   // :Boolean
	   _started,
	
	   // :String
	   _key,
	
	   // :Rectangle
	   _bounds;
	
	/**
	 * @param plan //:Plan
	 * @param zone //:ActiveZone
	 * @param slice //:Slice
	 * @param key //:String
	 * @param start //:Number
	 * @param duration // :int //default value -1
	 */
	this.prototype.init = function(plan, zone, slice, key, start, duration) {
		this._isInterrupted = false;
		
		this._plan = plan;
		this._zone = zone;
		this._slice = slice;
		this._key = key;
		this._bounds = new Rectangle(); 
		this._started = false;
		
		this.start = start;
		this.duration = duration;
		
		// TODO: Portage : trouver un équivalent au timer
		// timer = new Timer( start, 1);
		// timer.addEventListener("timer", startHandler);
		// timer.start();
	};
	
	// :Slice 
	this.prototype.getSlice = function() {
		return _slice;
	};

    // :ActiveZone
	this.prototype.getZone = function() {
		return _zone;
	};

    // :Boolean
	this.prototype.getStarted= function() {
		return _started;
	};
	
	// :void
	this.prototype.interrupt = function() {
		if (timer != null) {
		    timer.stop();
			clean(false);
		}			
	};
	
	/**
	 * @param full //:Boolean 
	 * @return //:void
	 */
	this.prototype.clean = function(full) {
		if (this._started) {
			this._started = false;
			if ( _plan.m_applet.plan != null) {
				 
				if(_bounds.width == 0 && _bounds.height == 0) {
					// Asynchronous URL content loaded (bounds not set)
					slice.setBounds( _plan.m_applet, _plan.m_applet.curDrawingSurface.graphics, zone.getParent(), zone, null, null, null, _bounds );
				}
				_plan.m_applet.renderShape( _plan.m_applet.restDrawingSurface, _bounds.width, _bounds.height, new Point( _bounds.x, _bounds.y));
				if( full) _plan.paintCurZone();
			}
			// TODO : portage : delete keyword in javascript ?
			delete _plan.m_tipTimers[_key];
		}
	};
	
	/**
	 * @param event :TimerEvent
	 * @return //:void
	 */
	this.prototype.startHandler = function(event) {
		if( !this._started) {
			this._started = true;
			// :Point
			var pos = _plan.m_applet.curPos;
			slice.paint( _plan.m_applet, _plan.m_applet.curDrawingSurface, zone.getParent(), zone, null, pos, null );
			slice.setBounds( _plan.m_applet, _plan.m_applet.curDrawingSurface.graphics, zone.getParent(), zone, null, pos, null, _bounds );
			_plan.m_applet.renderShape( _plan.m_applet.curDrawingSurface, _bounds.width, _bounds.height, new Point( _bounds.x, _bounds.y));
		}
		if( duration != -1) {
		    // TODO portage : timer in javascript
		    /*
			timer = new Timer( duration, 1);
			timer.addEventListener("timer", stopHandler);
			timer.start();
			*/
		}
	};
	

    /**
     * @param event :TimerEvent
     * @return //:void
     */
    this.prototype.stopHandler = function(event) {
        clean(true);
    };
    
    // Public API
    return  {
        init: init,
        getSlice: getSlice,
        getZone: getZone,
        getStarted: getStarted,
        interrupt: interrupt,
        clean: clean,
        startHandler: startHandler,
        stopHandler: stopHandler,
    };
}());
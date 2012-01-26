JMI.namespace("script.TipTimer");

JMI.script.TipTimer = (function() {

	/**
	 * @param plan //:Plan
	 * @param zone //:ActiveZone
	 * @param slice //:Slice
	 * @param key //:String
	 * @param start //:Number
	 * @param duration // :int //default value -1
	 */
    var TipTimer = function(plan, zone, slice, key, start, duration) {
		this.isInterrupted = false;
		
		this.plan = plan;
		this.zone = zone;
		this.slice = slice;
		this.key = key;
		this.bounds = new JMI.script.Rectangle();
		this.started = false;
		
		this.start = start;
		this.duration = duration;
		var timer = this;
		this.timer = this.async(
						function () { 
							timer.startHandler(); 
						}, start);
	};
    
	TipTimer.prototype = {
		constructor: JMI.script.TipTimer,
		
		async: function(fn, t) {
		    return setTimeout(fn, t);
		},
		// :void
		interrupt: function() {
			if (this.timer != null) {
			    clearTimeout( this.timer);
				this.clean(false);
			}			
		},
		
		/**
		 * @param full //:Boolean 
		 * @return //:void
		 */
		clean: function(full) {
			if (this.started) {
				this.started = false;
				if (this.plan.applet.planContainer != null) {
					 
					if (this.bounds.width == 0 && this.bounds.height == 0) {
						// Asynchronous URL content loaded (bounds not set)
						this.slice.setBounds( this.plan.applet, this.plan.applet.curDrawingContext, this.zone.getParent(), this.zone, null, null, null, this.bounds);
					}
					this.plan.applet.renderShape(this.plan.applet.restDrawingCanvas, this.bounds.width, this.bounds.height, new JMI.script.Point(this.bounds.x, this.bounds.y));
					if(full) this.plan.paintCurZone();
				}
				delete this.plan.tipTimers[this.key];
			}
		},
		
		/**
		 * @param event :TimerEvent
		 * @return //:void
		 */
		startHandler: function() {
			if (!this.started) {
				this.started = true;
				// :Point
				var pos = this.plan.applet.curPos;
				this.slice.paint(this.plan.applet, this.plan.applet.curDrawingContext, this.zone.getParent(), this.zone, null, pos, null);
				this.slice.setBounds(this.plan.applet, this.plan.applet.curDrawingContext, this.zone.getParent(), this.zone, null, pos, null, this.bounds);
				this.plan.applet.renderShape(this.plan.applet.curDrawingCanvas, this.bounds.width, this.bounds.height, new JMI.script.Point(this.bounds.x, this.bounds.y));
			}
			if (this.duration != -1) {
				var timer = this;
				this.timer = this.async(
								function () { 
									timer.stopHandler(); 
								}, this.duration);
			}
		},
		
	    /**
	     * @param event :TimerEvent
	     * @return //:void
	     */
	    stopHandler: function() {
	        this.clean(true);
	    }
	};
		
	return TipTimer;
}());
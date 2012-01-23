JMI.namespace("script.TipTimer");

JMI.script.TipTimer = (function() {

    var TipTimer = function() {};
    
	TipTimer.prototype = {
		constructor: JMI.script.TipTimer,
		
		/**
		 * @param plan //:Plan
		 * @param zone //:ActiveZone
		 * @param slice //:Slice
		 * @param key //:String
		 * @param start //:Number
		 * @param duration // :int //default value -1
		 */
		init: function(plan, zone, slice, key, start, duration) {
			this.isInterrupted = false;
			
			this.plan = plan;
			this.zone = zone;
			this.slice = slice;
			this.key = key;
			this.bounds = new JMI.script.Rectangle();
			this.started = false;
			
			this.start = start;
			this.duration = duration;
			this.timer = setTimeout( this.startHandler, start);
		},
		
		// :void
		interrupt: function() {
			if (this.timer != null) {
			    this.clearTimeout( this.timer);
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
				if (this.plan.applet.plan != null) {
					 
					if (this.bounds.width == 0 && this.bounds.height == 0) {
						// Asynchronous URL content loaded (bounds not set)
						this.slice.setBounds( this.plan.applet, this.plan.applet.curDrawingContext, zone.getParent(), zone, null, null, null, this.bounds);
					}
					this.plan.applet.renderShape(this.plan.applet.restDrawingContext, this.bounds.width, this.bounds.height, new JMI.script.Point(this.bounds.x, this.bounds.y));
					if(full) this.plan.paintCurZone();
				}
				// TODO : portage : delete keyword in javascript ?
				delete this.plan.tipTimers[this.key];
			}
		},
		
		/**
		 * @param event :TimerEvent
		 * @return //:void
		 */
		startHandler: function(event) {
			if (!this.started) {
				this.started = true;
				// :Point
				var pos = this.plan.applet.curPos;
				this.slice.paint(this.plan.applet, this.plan.applet.curDrawingContext, this.zone.getParent(), this.zone, null, pos, null);
				this.slice.setBounds(this.plan.applet, this.plan.applet.curDrawingContext, this.zone.getParent(), this.zone, null, pos, null, this.bounds);
				this.plan.applet.renderShape(this.plan.applet.curDrawingContext, this.bounds.width, this.bounds.height, new JMI.script.Point(this.bounds.x, this.bounds.y));
			}
			if (duration != -1) {
				this.timer = setTimeout(this.stopHandler, duration);
			}
		},
		
	    /**
	     * @param event :TimerEvent
	     * @return //:void
	     */
	    stopHandler: function(event) {
	        this.clean(true);
	    }
	};
		
	return TipTimer;
}());
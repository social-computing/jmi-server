package com.socialcomputing.wps.script
{
	import flash.events.TimerEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.Timer;
	
	import mx.containers.Tile;
	import mx.rpc.events.HeaderEvent;
	
	import org.mockito.impl.ReturningAnswer;
	
	public class TipTimer
	{
		private var timer:Timer;
		private var start:int, duration:int;
		private var _isInterrupted:Boolean;
		
		private var _plan:Plan;
		private var _zone:ActiveZone;
		private var _slice:Slice;
		private var _started:Boolean;
		private var _key:String;
		private var _bounds:Rectangle;
		
		public function TipTimer( plan:Plan, zone:ActiveZone, slice:Slice, key:String, start:Number, duration:int=-1)
		{
			this._isInterrupted = false;
			
			this._plan = plan;
			this._zone = zone;
			this._slice = slice;
			this._key = key;
			this._bounds = new Rectangle(); 
			this._started = false;
			
			this.start = start;
			this.duration = duration;
			timer = new Timer( start, 1);
			timer.addEventListener("timer", startHandler);
			timer.start();
		}
		
		public function get slice():Slice
		{
			return _slice;
		}

		public function get zone():ActiveZone
		{
			return _zone;
		}

		public function interrupt():void {
			if( timer != null) {
				timer.stop();
				clean( false);
			}			
		}
		
		public function clean( full:Boolean):void {
			if( this._started) {
				this._started = false;
				_plan.m_applet.renderShape( _plan.m_applet.restDrawingSurface, _bounds.width, _bounds.height, new Point( _bounds.x, _bounds.y));
				if( full)
					_plan.paintCurZone();
				delete _plan.m_tipTimers[_key];
			}
		}
		
		public function startHandler(event:TimerEvent):void {
			if( !this._started) {
				this._started = true;
				var pos:Point = _plan.m_applet.curPos;
				slice.paint( _plan.m_applet, _plan.m_applet.curDrawingSurface, zone.getParent(), zone, null, pos, null );
				slice.setBounds( _plan.m_applet, _plan.m_applet.curDrawingSurface.graphics, zone.getParent(), zone, null, pos, null, _bounds );
				_plan.m_applet.renderShape( _plan.m_applet.curDrawingSurface, _bounds.width, _bounds.height, new Point( _bounds.x, _bounds.y));
			}
			if( duration != -1) {
				timer = new Timer( duration, 1);
				timer.addEventListener("timer", stopHandler);
				timer.start();
			}
		}
		
		public function stopHandler(event:TimerEvent):void {
			clean( true);
		}
	}
}
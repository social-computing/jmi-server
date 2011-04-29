package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.plan.PlanContainer;
	import com.socialcomputing.wps.script.Swatch;
	import com.socialcomputing.wps.shapes.Circle;
	
	import mx.controls.Alert;
	
	import spark.components.Group;
	import spark.core.SpriteVisualElement;
	
	public class PlanComponent extends Group
	{
		private var _dataProvider:PlanContainer = null;
		private var _nodes:Array = null;
		private var _drawingSurface: SpriteVisualElement;
		
		public function PlanComponent()
		{
			super();
			this.addEventListener(NodeEvent.NODE_MOUSEDOWN,  onNodeMouseDown, true);
			// TODO : See how to draw links ...
			// Might be on a separate drawing surface ?
			/* 
			_drawingSurface = new SpriteVisualElement();
			this.addElement(_drawingSurface);
			*/
		}
		
		public function set dataProvider(value:Object):void
		{
			// If the given value is null return for now
			// TODO : If the local plancontainer is set, reset objects 
			if(value == null) {
				return;
			}
			
			if(value is PlanContainer) {
				this._dataProvider = value as PlanContainer;
			}
			else {
				// TODO : convert from a json formatted result here
				this._dataProvider = PlanContainer.fromJSON(value);

			}
			
			// Fake elements here
			this._nodes = new Array(new Node(1, 30, 30), new Node(2, 70, 80));
			for each (var n:Node in this._nodes) {
				var nc:NodeComponent = new NodeComponent();
				
				nc.bcolor = 0xFFFFFF;
				nc.color = 0x555555;
				nc.node = n;
				this.addElement(nc);
			}
			
			/*
			 * Don't redraw immediately, because maybe the code that's calling us is
			 * going to change several settings, and we don't want to redraw for each 
			 * setting change. Instead, tell the flex framework that
			 * we need to be redrawn; the framework will ensure that updateDisplayList
			 * is invoked after all scripts have finished executing.
			 */
			this.invalidateDisplayList();
		}
		
		
		protected function onNodeMouseDown(event:NodeEvent):void
		{
			Alert.show("Selected node : " + event.node.id);
		}
		
		/**
		 * @inheritDoc
		 */
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			trace("Update graphic display");
			// graphics.clear();
			// Draw a rectange in the graphic area
			// For test purposes only
			/* 
			
			graphics.beginFill( 0xffffff, 0.0 );
			graphics.drawRect( 0, 0, unscaledWidth, unscaledHeight );
			graphics.endFill();
			*/
			/*
			if(_dataProvider != null){
				drawComponents();
			}
			*/
		}
		
		// The drawing of all plan components here
		/*
		private function drawComponents():void {
			trace("draw components");
			
			graphics.lineStyle(1, 0x000000, 1.0);
			for each (var i:Node in this.node) {
				graphics.drawEllipse(i.x, i.y, i.width, i.width);
			}
		}
		*/
	}
}
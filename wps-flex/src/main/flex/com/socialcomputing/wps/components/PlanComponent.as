package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.plan.PlanContainer;
	import com.socialcomputing.wps.script.Swatch;
	
	import spark.components.BorderContainer;
	
	public class PlanComponent extends BorderContainer
	{
		private var _dataProvider:PlanContainer = null;
		private var node:Array = null;
		
		public function PlanComponent()
		{
			super();
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
				// Fake elements here
				this.node = new Array(new Node(30, 30), new Node(70, 80));
			}
			this.invalidateDisplayList();
		}
		
		//  
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			//super.updateDisplayList(unscaledWidth, unscaledHeight);
			trace("update display list called");
			
			// Draw a rectange in the graphic area
			// For test purposes only
			graphics.lineStyle(1, 0x000000, 1.0); 
			graphics.drawRect(5, 5, 25, 25);
			
			if(_dataProvider != null){
				drawComponents();
			}
		}
		
		// The drawing of all plan components here
		private function drawComponents():void {
			trace("draw components");
			for each (var i:Node in this.node) {
				graphics.drawEllipse(i.x, i.y, i.width, i.width);
			}
		}
	}
}
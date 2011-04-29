package com.socialcomputing.wps.shapes
{
	import mx.core.UIComponent;
	import mx.utils.ColorUtil;
	
	/** 
	 * A UIComponent that is simply a colored circle.
	 * 
	 */
	public class Circle extends UIComponent
	{
		/** Current color setting. */
		private var _color: int;
		private var _bcolor: int;


		/** 
		 * Set the circle forground color 
		 */
		public function set color(i: int): void {
			_color = i;
			invalidateDisplayList(); 
		}
		
		/** 
		 * Set the circle background color 
		 */
		public function set bcolor(i: int): void {
			_bcolor = i;
			invalidateDisplayList(); 
		}
		

		/** 
		 *  Redraws the component, using our current color, height, and width settings.
		 *  This function is called whenever the flex framework decides it's time to redraw the component. 
		 */
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			// Clear the drawing area
			graphics.clear();
			
			var nc:int = ColorUtil.adjustBrightness(_color,-30);	
			graphics.lineStyle(1,nc);
			
			/* drawing foregroud circle */
			graphics.beginFill(_color);
			graphics.drawCircle(unscaledHeight / 2,
				                unscaledHeight / 2, 
								unscaledHeight / 2+8);
			graphics.endFill();
			
			/* drawing background circle */
			graphics.lineStyle(1, nc);
			graphics.beginFill(_bcolor);
			graphics.drawCircle(unscaledHeight / 2,
								unscaledHeight / 2,
								unscaledHeight / 2);
			graphics.endFill();			
		}
	}
}
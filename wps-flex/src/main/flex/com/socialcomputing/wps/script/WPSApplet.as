package com.socialcomputing.wps.script
{
    import flash.geom.Rectangle;
    
    import mx.controls.Image;
    import mx.core.UIComponent;

	// La future classe de composant à renommer et refactorer 
	public class WPSApplet extends UIComponent
	{
		public var m_env:Env;
		public var m_plan:Plan;
		
        public var m_backImg:Image;
		public var m_restImg:Image;
        
		public var m_backImgUrl:String;
		
		public function WPSApplet()
		{
		}
        public function getSize():Dimension {
            return new Dimension( width, height);
        }
		public function createImage(width:int, height:int):Image {
			var image:Image  = new Image();
			image.width = width;
			image.height = height;
			return image;
		}
	}
}
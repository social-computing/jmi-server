package com.socialcomputing.wps.script
{
    import flash.geom.Rectangle;
    
    import mx.controls.Image;

	// La future classe de composant à renommer et refactorer 
	public class WPSApplet
	{
		public var m_env:Env;
		public var m_plan:Plan;
		
        public var m_restImg:Image;
        
		public function WPSApplet()
		{
		}
        // TODO à implémenter
        public function getSize():Dimension {
            return new Dimension();
        }
	}
}
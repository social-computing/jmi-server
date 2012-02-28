package com.socialcomputing.wps.script {
    import mx.controls.menuClasses.MenuItemRenderer;
    
    [Bindable]
    public class CustomMenuItemRenderer extends MenuItemRenderer
    {
        override public function set data(value:Object):void {
            if(value == null) return;
            super.data = value;
            if(this.data){
                if (this.data.bold) 
					this.setStyle("fontWeight", "bold");
				if (this.data.italic) 
					this.setStyle("fontStyle", "italic");
				if( this.data.hasOwnProperty("font"))
                	this.setStyle("fontFamily", this.data.font);
				if( this.data.hasOwnProperty("size"))
					this.setStyle("fontSize", this.data.size);
            }
            
        }
    }
}
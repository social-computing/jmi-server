package com.socialcomputing.wps.script {
    import mx.controls.menuClasses.MenuItemRenderer;
    
    [Bindable]
    public class CustomMenuItemRenderer extends MenuItemRenderer
    {
        
        override public function set data(value:Object):void {
            if(value == null) return;
            super.data = value;
            if(this.data){
                //Si on veut rajouter une tooltip
                //this.label.toolTip = "tooltip";
                
                /*var myTextFormat:TextFormat = new TextFormat();
                myTextFormat.bold = true;
                this.label.setTextFormat(myTextFormat);*/
                if (this.data.bold == "true") this.setStyle("fontWeight", "bold");
                this.setStyle("fontFamily", this.data.font);
            }
            
        }
        
        
    }
}
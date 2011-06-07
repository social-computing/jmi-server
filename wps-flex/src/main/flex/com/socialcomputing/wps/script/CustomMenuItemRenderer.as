package com.socialcomputing.wps.script {
    import flash.text.TextFormat;
    
    import mx.controls.menuClasses.MenuItemRenderer;
    
    [Bindable]
    public class CustomMenuItemRenderer extends MenuItemRenderer
    {
        /*override protected function updateDisplayList(unscaledWidth:Number,
        unscaledHeight:Number):void {
        
        //TODO set font bold on this.data.label 
        if (this.data.bold == "true") {
        //this.data.setStyle("fontWeight", "bold");
        this.data.label.font = "bold";
        }
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        }*/
        
        
        
        override public function set data(value:Object):void {
            if(value == null) return;
            super.data = value;
            if(this.data && this.data.bold == "true"){
                //Si on veut rajouter une tooltip
                //this.label.toolTip = "tooltip";
                
                /*var myTextFormat:TextFormat = new TextFormat();
                myTextFormat.bold = true;
                this.label.setTextFormat(myTextFormat);*/
                this.setStyle("fontWeight", "bold");
            }
        }
        
        
    }
}
package com.socialcomputing.wps.script {
    import flash.text.TextFormat;
    
    import mx.controls.menuClasses.MenuItemRenderer;
    
    [Bindable]
    public class CustomMenuItemRenderer extends MenuItemRenderer
    {
        override protected function updateDisplayList(unscaledWidth:Number,
                                                      unscaledHeight:Number):void {
            
            //TODO set font bold on this.data.label 
            /*if (this.data.bold == "true") {
                this.data.setStyle("FontWeight", "bold");
            }*/
            super.updateDisplayList(unscaledWidth, unscaledHeight);
        }
    }
}
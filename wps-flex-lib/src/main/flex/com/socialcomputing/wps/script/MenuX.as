package com.socialcomputing.wps.script  {
    import flash.text.TextFormat;
    
    import mx.collections.ArrayCollection;
    
    /**
     * <p>Title: MenuX</p>
     * <p>Description: A wrapper for the java.awt.Menu class.<br>
     * Because Serializtion is not compatible between client and server for the original class.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class MenuX extends Base
    {
        /**
         * Index of the bit flag prop in VContainer table
         */
        //	public  static final int    FLAGS_VAL           = 0;
        
        /**
         * Index of the font prop in VContainer table
         */
        public const FONT_VAL:int= 1;
        
        /**
         * Index of the text prop in VContainer table
         */
        public const TEXT_VAL:int= 2;
        
        /**
         * True if this menu is just an item.
         */
        public const ITEM_BIT:int= 0x01;
        
        /**
         * Items or subMenus
         */
        public  var m_items:Vector.<MenuX> = null;
        
        /**
         * Creates a new Menu filled with items.
         * @param items	A MenuX table that holds items or submenus.
         */
        public function MenuX( items:Vector.<MenuX>)
        {
            m_items = items;
        }
        
        /**
         * Returns an AWT PopupMenu matching this abstract representation.
         * Each item of the menu adds zone as an ActionListener.
         */
        /**
         * Parse this MenuX to return an awt PopupMenu.
         * The Menu to be initialized must be created before and passed as en argument.
         * When an item is selected, the listener actionPerformed() methode is called.
         * A MenuX can contains list properties so the parsing can generate more than one Menu!
         * @param dst		A new java.awt.PopupMenu to fill according to this MenuX.
         * @param listener	An Object to call when an item is selected.
         * @param zone		The zone from which this menu belongs. So the props can be decoded.
         * @return			True if this menu is not empty. This is used in the recursive process and is useless for the main call.
         * @throws UnsupportedEncodingException 
         */
        public function parseMenu( dst:ArrayCollection, zone:ActiveZone):Boolean {
            var i:int;
            var j:int;
            var k:int	= -1;
            var n:int		= 1;
            var iCnt:int	= m_items.length;
            var isEmpty:Boolean= true;
            var subMenu:ArrayCollection;
            var menuItm:Object;
            var font:TextFormat= this.getTextFormat( zone.m_props);
            var labels:Vector.<String> = null;
            
            if ( isDefined( TEXT_VAL ))
            {
                labels	= parseString( TEXT_VAL, zone.m_props);
                n		= labels.length;
            }
            
            for ( j = 0; j < n; j ++ )
            {
                if ( n > 1|| (n == 1 && labels != null))
                {
					menuItm	= new Object();
					menuItm.label = labels[j];
                    dst.addItem( menuItm );
                    if( n > 1)
                        k = j;
                }
                else
                {
                    subMenu	= dst;
                }
                
                for ( i = 0; i < iCnt; i ++ )
                {
                    var menu:MenuX = m_items[i];
                    var flags:int = menu.getFlags( zone.m_props);
                    
                    if ( isEnabled( flags, ITEM_BIT ))
                    {
                        if ( menu.parseItem( subMenu, zone, k ))
                        {
                            isEmpty = false;
                        }
                    }
                    else
                    {
                        if ( menu.parseMenu( subMenu, zone ))
                            isEmpty = false;
                        else
                        {
                            subMenu.removeItemAt( subMenu.length - 1);
                            var subitems:Vector.<String> = menu.parseString( TEXT_VAL, zone.m_props);
                            if( subitems != null && subitems.length > 0)
                            {
                                menuItm = new Object;
								menuItm.label = subitems[0];
                                // TODO menuItm.setFont( menu.getFont( zone ));
                                var fontMenu:TextFormat = menu.getFont( FONT_VAL, zone.m_props).getTextFormat(zone.m_props);
                                if ( fontMenu != null) {
                                    if (fontMenu.bold == true)
                                        menuItm.bold = "true";
                                    else
                                        menuItm.bold = "false";
                                    menuItm.font = fontMenu.font;
                                }
                                menuItm.enabled = false;
                                subMenu.addItem( menuItm );
                            }
                        }
                    }
                }
            }
            return !isEmpty;
        }
        
        /**
         * Parses this MenuX when it's an item (ITEM_BIT).
         * An item can contain list properties so when it's parsed it can generate more than one MenuItem!
         * @param dst		The parent menu of this item. It will hold the item or items depending of the type of this.
         * @param listener	An Object to call when this item is selected.
         * @param zone		The zone from which this parent menu belongs. So the props can be decoded.
         * @param j			The parent menu index if the parent is a multi menu (list prop inside).
         * @return			True if this item is not empty. This is used in the recursive process and is useless for the main call.
         * @throws UnsupportedEncodingException 
         */
        private function parseItem( dst:ArrayCollection, zone:ActiveZone, j:int):Boolean {
            var parts:Array 	= getString( TEXT_VAL, zone.m_props ).split( SEP);
            var title:String	= parts[0];
            var url:String      = parts.length > 1? parts[1] : null;
            var redir:String    = parts.length > 2? parts[2] : null;
            //itemStr;
            var font:TextFormat= this.getTextFormat( zone.m_props);
            var items:Vector.<String>	= parseString3( title, zone.m_props);
            var urls:Vector.<String>   	= url != null ? parseString3( url, zone .m_props) : null;
            var redirs:Vector.<String>  = redir != null ? parseString3( redir, zone.m_props) : urls;
            //MenuItem    item;
            var i:int;
            var n:int	= items.length;
            var m:int		= redirs != null ? redirs.length : 0;
            
            if ( j == -1)
            {
                for ( i = 0; i < n; i ++ )
                {
                    addItem( dst, items[i], redirs != null ? redirs[i] : null, font );
                }
            }
            else
            {
                i	= m - 1< j ? m - 1: j;
                j	= n - 1< j ? n - 1: j;
                if( i >= 0 && j >= 0)
                    addItem( dst, items[j], redirs != null ? redirs[i] : null, font );
            }
            
            return n > 0;
        }
        
        /**
         * Creates a new MenuItem, add it to a Menu and store the URL to call inside.
         * @param menu		The parent Menu of this item.
         * @param listener	An Object to call when this item is selected.
         * @param title		Label of this item that will be displayed in the Menu.
         * @param url		Adress to go (including Javascript) when this is selected.
         * @param font		TypeFace of the label.
         */
        private function addItem( menu:ArrayCollection, title:String, url:String, font:TextFormat):void {
            var item:Object = new Object();
            if( url == null && title == "-") {
				item.type = "separator";	
			} else {
				item.label = title;
				if ( url != null )
					item.action = url;
                if ( font != null) {
                    if (font.bold == true)
                        item.bold = "true";
                    else
                        item.bold = "false";
                    item.font = font.font;
                }
			}
			menu.addItem( item );
        }
        
        /**
         * Retrieve a java.awt.Font from this FontX propertie (FONT_VAL container).
         * @param props		A props table holding this FontX prop if it has one.
         * @return			the matching Font or null if the container is empty or the prop is null.
         */
        public function getTextFormat(props:Array):TextFormat {
            var font:FontX= getFont( FONT_VAL, props);
            
            return font != null ? font.getTextFormat( props ): null;
        }
    }
}
JMI.namespace("script.MenuX");

/*
 * <p>Title: MenuX</p>
 * <p>Description: A wrapper for the java.awt.Menu class.<br>
 * Because Serializtion is not compatible between client and server for the original class.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.MenuX = (function() {
    /**
     * Items or subMenus
     */
    var items; //:Vector.<MenuX> = null;
    
	var MenuX = function() {
	};
	
	MenuX.prototype = {
		constructor: JMI.script.MenuX,

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
    parseMenu: function( dst, zone) {
        var i;
        var j;
        var k		= -1;
        var n		= 1;
        var iCnt	= this.items.length;
        var isEmpty = true;
        var subMenu;
        var menuItm;
        var font = this.getTextFormat( zone.props);
        var labels = null;
        
        if ( isDefined( TEXT_VAL ))
        {
            labels	= parseString( TEXT_VAL, zone.props);
            n		= labels.length;
        }
        
        for ( j = 0; j < n; j ++ )
        {
            if ( n > 1|| (n == 1 && labels != null))
            {
				subMenu	= [];
				menuItm = {};
				menuItm.label = labels[j];
				menuItm.children = subMenu;
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
                var menu = m_items[i];
                var flags = menu.getFlags( zone.props);
                
                if ( JMI.script.Base.isEnabled( flags, ITEM_BIT ))
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
                        var subitems = menu.parseString( TEXT_VAL, zone.props);
                        if( subitems != null && subitems.length > 0)
                        {
                            menuItm = new Object;
							menuItm.label = subitems[0];
                            // TODO menuItm.setFont( menu.getFont( zone ));
                            var fontMenu = menu.getFont( FONT_VAL, zone.props).getTextFormat(zone.props);
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
    },
    
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
    parseItem: function( dst, zone, j) {
        var parts 	= this.getString( TEXT_VAL, zone.props ).split( SEP);
        var title	= parts[0];
        var url		= parts.length > 1? parts[1] : null;
        var redir   = parts.length > 2? parts[2] : null;
        //itemStr;
        var font = this.getTextFormat( zone.props);
        var its = parseString3( title, zone.props);
        var urls = url != null ? parseString3( url, zone.props) : null;
        var redirs = redir != null ? parseString3( redir, zone.props) : urls;
        //MenuItem    item;
        var i;
        var n	= its.length;
        var m	= redirs != null ? redirs.length : 0;
        
        if ( j == -1)
        {
            for ( i = 0; i < n; i ++ )
            {
                addItem( dst, its[i], redirs != null ? redirs[i] : null, font );
            }
        }
        else
        {
            i	= m - 1< j ? m - 1: j;
            j	= n - 1< j ? n - 1: j;
            if( i >= 0 && j >= 0)
                addItem( dst, its[j], redirs != null ? redirs[i] : null, font );
        }
        
        return n > 0;
    },
    
    /**
     * Creates a new MenuItem, add it to a Menu and store the URL to call inside.
     * @param menu		The parent Menu of this item.
     * @param listener	An Object to call when this item is selected.
     * @param title		Label of this item that will be displayed in the Menu.
     * @param url		Adress to go (including Javascript) when this is selected.
     * @param font		TypeFace of the label.
     */
    addItem: function( menu, title, url, font) {
        var item = {};
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
    },
    
    /**
     * Retrieve a java.awt.Font from this FontX propertie (FONT_VAL container).
     * @param props		A props table holding this FontX prop if it has one.
     * @return			the matching Font or null if the container is empty or the prop is null.
     */
    getTextFormat: function(props) {
        var font = getFont( FONT_VAL, props);
        
        return font != null ? font.getTextFormat( props ): null;
    }
	};
	
	return MenuX;
}());		

/**
 * Index of the bit flag prop in VContainer table
 */
//	public  static final int    FLAGS_VAL           = 0;

/**
 * Index of the font prop in VContainer table
 */
JMI.script.MenuX.FONT_VAL = 1;

/**
 * Index of the text prop in VContainer table
 */
JMI.script.MenuX.TEXT_VAL = 2;

/**
 * True if this menu is just an item.
 */
JMI.script.MenuX.ITEM_BIT = 0x01;

    

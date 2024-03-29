package com.socialcomputing.wps.client.applet;

import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * <p>Title: MenuX</p>
 * <p>Description: A wrapper for the java.awt.Menu class.<br>
 * Because Serializtion is not compatible between client and server for the original class.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class MenuX extends Base implements Serializable
{
	/**
	 * Index of the bit flag prop in VContainer table
	 */
//	public  static final int    FLAGS_VAL           = 0;

	/**
	 * Index of the font prop in VContainer table
	 */
	public  static final int    FONT_VAL            = 1;

	/**
	 * Index of the text prop in VContainer table
	 */
	public  static final int    TEXT_VAL            = 2;

	/**
	 * True if this menu is just an item.
	 */
	public  static final int    ITEM_BIT            = 0x01;

	/**
	 * JDK 1.1 serialVersionUID
	 */
	static final long serialVersionUID = 3411919927856702861L;

	/**
	 * Items or subMenus
	 */
	public  MenuX[] m_items = null;

	/**
	 * Creates a new Menu filled with items.
	 * @param items	A MenuX table that holds items or submenus.
	 */
	public MenuX( MenuX[] items )
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
	boolean parseMenu( Menu dst, ActionListener listener, ActiveZone zone ) throws UnsupportedEncodingException
	{
		int         i, j, k	= -1,
					n		= 1,
					iCnt	= m_items.length;
		boolean     isEmpty = true;
		//MenuX       item;
		Menu        subMenu;
		MenuItem    menuItm;
		Font        font    = getFont( zone );
		String[]	labels	= null;

		dst.setFont( font );

		if ( isDefined( TEXT_VAL ))
		{
			labels	= parseString( TEXT_VAL, zone );
			n		= labels.length;
			if ( n == 1 )	dst.setLabel( labels[0] );
		}

		for ( j = 0; j < n; j ++ )
		{
			if ( n > 1 || (n == 1 && labels != null))
			{
				subMenu	= new Menu();
				subMenu.setLabel( labels[j] );
				dst.add( subMenu );
				if( n > 1)
					k = j;
			}
			else
			{
				subMenu	= dst;
			}

			for ( i = 0; i < iCnt; i ++ )
			{
				MenuX   menu    = (MenuX)m_items[i];
				int     flags   = menu.getFlags( zone );

				if ( MenuX.isEnabled( flags, ITEM_BIT ))
				{
					if ( menu.parseItem( subMenu, listener, zone, k ))
					{
						isEmpty = false;
					}
				}
				else
				{
					if ( menu.parseMenu( subMenu, listener, zone ))
						isEmpty = false;
					else
					{
						subMenu.remove( subMenu.getItemCount()-1);
						String[] subitems = menu.parseString( TEXT_VAL, zone );
						if( subitems != null && subitems.length > 0)
						{
							menuItm = new MenuItem( subitems[0]);
							menuItm.setFont( menu.getFont( zone ));
							menuItm.setEnabled( false );
							subMenu.add( menuItm );
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
	private boolean parseItem( Menu dst, ActionListener listener, ActiveZone zone, int j ) throws UnsupportedEncodingException
	{
		String[]    parts   = getTextParts( getString( TEXT_VAL, zone ), SEP );
		String      title   = parts[0],
					url     = parts.length > 1 ? parts[1] : null,
					redir   = parts.length > 2 ? parts[2] : null;
					//itemStr;
		Font        font    = getFont( zone );
		String[]    items   = parseString( title, zone ),
					urls    = url != null ? parseString( url, zone ) : null,
					redirs  = redir != null ? parseString( redir, zone ) : urls;
		//MenuItem    item;
		int         i, n	= items.length,
					m		= redirs != null ? redirs.length : 0;

		if ( j == -1 )
		{
			for ( i = 0; i < n; i ++ )
			{
				addItem( dst, listener, items[i], redirs != null ? redirs[i] : null, font );
			}
		}
		else
		{
			i	= m - 1 < j ? m - 1 : j;
			j	= n - 1 < j ? n - 1 : j;
			if( i >= 0 && j >= 0)
				addItem( dst, listener, items[j], redirs != null ? redirs[i] : null, font );
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
	private void addItem( Menu menu, ActionListener listener, String title, String url, Font font )
	{
		MenuItem	item = new MenuItem( title );

		if ( url != null )
		{
			item.setActionCommand( url );
			item.addActionListener( listener );
		}

		item.setFont( font );
		menu.add( item );
	}

	/**
	 * Retrieve a java.awt.Font from this FontX propertie (FONT_VAL container).
	 * @param props		A props table holding this FontX prop if it has one.
	 * @return			the matching Font or null if the container is empty or the prop is null.
	 */
	protected Font getFont( Hashtable props )
	{
		FontX   font = getFont( FONT_VAL, props );

		return font != null ? font.getFont( props ): null;
	}
}
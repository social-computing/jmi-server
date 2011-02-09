package com.socialcomputing.wps.client.applet;

import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * <p>Title: Satellite</p>
 * <p>Description: An elementary part of a swatch that can be stacked.<br>
 * Each Satellite contains many flags to describe how to display it.
 * It also contains many container to manage the positioning, events, selection and link to parent.
 * A Shape and a table of slices are also necessary to know how to draw this.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class Satellite extends Base implements Serializable
{
	/**
	 * Index of the Transfo prop in VContainer table.
	 * If there is no Transfo it is copied from the default one (first Satellite of the swatch).
	 */
	public      static final int    TRANSFO_VAL         = 1;

	/**
	 * Index of the hovered event prop in VContainer table
	 */
	public      static final int    HOVER_VAL           = 2;

	/**
	 * Index of the click event prop in VContainer table
	 */
	public      static final int    CLICK_VAL           = 3;

	/**
	 * Index of the double click event prop in VContainer table.
	 */
	public      static final int    DBLCLICK_VAL        = 4;

	/**
	 * Index of the selection prop in VContainer table if this is a selection Satellite.
	 */
	public      static final int    SELECTION_VAL        = 5;

	/**
	 * Index of the dark link color prop in VContainer table.
	 */
	public      static final int    LINK_DRK_COL_VAL    = 6;

	/**
	 * Index of the link color prop in VContainer table.
	 */
	public      static final int    LINK_NRM_COL_VAL    = 7;

	/**
	 * Index of the bright link color prop in VContainer table.
	 */
	public      static final int    LINK_LIT_COL_VAL    = 8;

	/**
	 * True if this Satellite is visible.
	 * It can be interesting to create fake invisible satellite (even if it has never been tested).
	 */
	public      static final int    VISIBLE_BIT         = 0x001;

	/**
	 * This has a link with its parent.
	 * Useful for creating s�lection tips.
	 */
	public      static final int    LINK_BIT            = 0x002;

	/**
	 * This can only be visible on a SuperZone (a zone that clusterize the others).
	 */
	public      static final int    SUPER_BIT           = 0x004;

	/**
	 * This can only be visible on a SubZone (a clusterized zone).
	 */
	public      static final int    SUB_BIT             = 0x008;

	/**
	 * This can only be visible on a current zone (a hovered zone).
	 */
	public      static final int    CUR_BIT             = 0x010;

	/**
	 * This can only be visible on a rest zone (a not hovered zone).
	 */
	public      static final int    REST_BIT            = 0x020;

	/**
	 * This is visible under the filter of the current zone.
	 */
	public      static final int    BACK_BIT            = 0x040;

	/**
	 * This is a Selection sat.
	 * To know that this sat should always stay on top of the others.
	 */
	public      static final int    SEL_BIT             = 0x080;

	/**
	 * This is a Tip sat.
	 * To know that this sat should not be tested when bounds are evaluated.
	 */
	public      static final int    TIP_BIT             = 0x100;

	/**
	 * This is sat can't be right or left sided.
	 */
	public      static final int    NOSIDED_BIT         = 0x200;

	/**
	 * Draws only Selection sats.
	 */
	protected   static final int    SEL_TYP             = 0;

	/**
	 * Draws only Tip sats.
	 */
	protected   static final int    TIP_TYP             = 1;

	/**
	 * Draws all sats but Selection and tips ones.
	 */
	protected   static final int    BASE_TYP            = 2;

	/**
	 * Draws all sats.
	 */
	protected   static final int    ALL_TYP             = 3;

	/**
	 * JDK 1.1 serialVersionUID
	 */
	protected   static final long   serialVersionUID    = 5646487912398423454L;

	/**
	 * Shape used to draw this.
	 * This can be a simple dot, a disk, a rectangle or a polygon.
	 */
	protected   ShapeX              m_shape;

	/**
	 * The elementary slices that are stacked to draw this satellite.
	 * They describe how to fill the shape.
	 */
	protected   Slice[]             m_slices;

	/**
	 * Creates a Satellite with its shape and slices.
	 * @param shape		A shape that is filled by the slices.
	 * @param slices	A table of slices used to render this.
	 */
	public Satellite( ShapeX shape, Slice[] slices )
	{
		m_shape     = shape;
		m_slices    = slices;
	}

	/**
	 * Returns wether this satellite is visible.
	 * @param zone		Zone from which the satellite belongs.
	 * @param isTip		True if this is a Tip.
	 * @param curSel	Identifier of the current active selection on the Plan.
	 * @param sel		Identifier of this satellite selection or -1 if there is none.
	 * @return			True if this satellite is visible, false otherwise.
	 */
	protected boolean isVisible( ActiveZone zone, boolean isTip, int curSel, int sel )
	{
		boolean hasSel  = curSel >= 0,
				isSel   = isEnabled( zone.m_selection, 1 << curSel );

		return isTip ? !hasSel || !isSel : hasSel && sel == curSel && isSel;
	}

	/**
	 * Draws this satellite on a Graphics.
	 * It's position and size is evaluated by its parent and transfo.
	 * A type filtering can be applied to select a special kind of satellite.
	 * @param applet		The Applet that owns this.
	 * @param g				A graphics to draw this in.
	 * @param zone			The zone that holds the properties used by this satellite.
	 * @param satCtr		This satellite center.
	 * @param supCtr		This parent satellite center.
	 * @param isLinkOnly	True to paint only the link between this and its parent if it exists.
	 * @param satData		This satellite data buffer.
	 * @param showTyp		The type of satellite to display.[ALL_TYP,BASE_TYP,TIP_TYP,SEL_TYP]
	 * @throws UnsupportedEncodingException 
	 */
	protected void paint( WPSApplet applet, Graphics g, ActiveZone zone, Point satCtr, Point supCtr, boolean isLinkOnly, SatData satData, int showTyp ) 
	{
		int         flags       = satData.m_flags;
		boolean     isTip       = isEnabled( flags, Satellite.TIP_BIT ),
					isSel       = isEnabled( flags, Satellite.SEL_BIT ),
					isVisible   = isTip || isSel ? satData.m_isVisible : true;

		ActiveZone  supZone     = zone.getParent();

		if ( isVisible )
		{
			if ( isLinkOnly )               // we must draw this Satellite Link if it exists
			{
				if ( isEnabled( flags, LINK_BIT ))    // This has a Link
				{
					int     x1  = supCtr.x,
							y1  = supCtr.y,
							x2  = satCtr.x,
							y2  = satCtr.y;

					setColor( g, LINK_DRK_COL_VAL, zone );
					g.drawLine( x1, y1 + 1, x2, y2 + 1 );

					if ( setColor( g, LINK_LIT_COL_VAL, zone ))
					{
						g.drawLine( x1 - 1, y1, x2 - 1, y2 );
						g.drawLine( x1, y1, x2, y2 );
						g.drawLine( x1 + 1, y1, x2 + 1, y2 );

						setColor( g, LINK_NRM_COL_VAL, zone );
						g.drawLine( x1, y1 - 1, x2, y2 - 1 );
					}
				}
			}
			else
			{
				boolean isShowable = isSel;

				switch ( showTyp )
				{
					case ALL_TYP    : isShowable = true; break;
					case BASE_TYP   : isShowable = !( isTip || isSel ); break;
					case TIP_TYP    : isShowable = isTip; break;
		//			case SEL_TYP :  return isSel;
				}

				if ( isShowable )
				{
					int     i, n    = m_slices.length;

					for ( i = 0; i < n; i ++ )
					{
						m_slices[i].paint( applet, g, supZone, zone, m_shape, satCtr, supCtr );
					}
				}
			}
		}
	}

	/**
	 * Return wether a point is inside this.
	 * @param applet		The Applet that owns this.
	 * @param g				A graphics to get the FontMetrics used by this.
	 * @param zone			The zone that holds the properties used by this satellite.
	 * @param satCtr		This satellite center.
	 * @param supCtr		This parent satellite center.
	 * @param transfo		The transformation that give the position and scale of this using its parent.
	 * @param pos			A point position to test.
	 * @param isPie			True if this is in the 'pie' part of this satellite.
	 * @param isFake		True if this is the first (main) Satellite.
	 * @return				True if this contains pos.
	 * @throws UnsupportedEncodingException 
	 */
	protected boolean contains( WPSApplet applet, Graphics g, ActiveZone zone, Point satCtr, Point supCtr, Transfo transfo, Point pos, boolean isPie, boolean isFake )
	{
		int         i, n    = m_slices.length;

		if ( supCtr == null )	supCtr = m_shape.getCenter( zone );

		for ( i = 0; i < n && !m_slices[i].contains( applet, g, zone.getParent(), zone, m_shape, satCtr, supCtr, pos ); i ++ );

		if ( i < n )    // point is in one of this slices
		{
			applet.m_plan.m_newZone = zone;

			if ( isPie )
			{
				BagZone         supZone = (BagZone)zone;
				ActiveZone[]    zones   = supZone.m_subZones;
				n = zones.length + 1;

				Point   center  = isFake ? m_shape.getCenter( supZone ) : supCtr;
				float   dir     = supZone.m_dir != 10.f ? supZone.m_dir : transfo.m_dir,
						step    = supZone.m_stp,
						m       = .5f *( Pi2 / step - n ),
						a       = (float)Math.atan2( pos.y - center.y, pos.x - center.x );

				if ( dir < 0 )  dir += Pi2;
				if ( a < 0 )    a += Pi2;
				if ( a < dir )  a += Pi2;

				a = .5f +( a - dir )/ step;
				i = (int)a;

				if ( i > 0 )
				{
					if ( i < n )
					{
						applet.m_plan.m_newZone = zones[i-1];
					}
					else if ( a - n < m )
					{
						applet.m_plan.m_newZone = zones[n-2];
					}
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Sets this bounds by updating an already created Rectangle.
	 * @param applet		The Applet that owns this.
	 * @param g				A graphics to get the FontMetrics used by this.
	 * @param zone			The zone that holds the properties used by this satellite.
	 * @param satCtr		This satellite center.
	 * @param supCtr		This parent satellite center.
	 * @param bounds		A Rectangle to merge with this bounds.
	 * @throws UnsupportedEncodingException 
	 */
	protected void setBounds( WPSApplet applet, Graphics g, ActiveZone zone, Point satCtr, Point supCtr, Rectangle bounds )
	{
		int i, n    = m_slices.length;

		for ( i = 0; i < n; i ++ )
		{
			m_slices[i].setBounds( applet, g, zone.getParent(), zone, m_shape, satCtr, supCtr, bounds );
		}
	}

	/**
	 * Execute one or more actions matching an event.
	 * The actions are stored in this satellite, one list for each of the 3 possible events.
	 * They are executed using there declaration order in the list. They can be one of this:
	 * <ul>
	 * <li>show				Shows a message in the StatusBar.</li>
	 * <li>open				Opens an URL in a new window or a frame.</li>
	 * <li>popup			Popup a menu at the cursor position.</li>
	 * <li>pop				Pops a tooltip near the cursor position.</li>
	 * <li>play				Plays a sound file.</li>
	 * <li>dump				Dumps a text on the Java Console.</li>
	 * </ul>
	 * @param applet		The Applet that owns this.
	 * @param zone			The zone that holds the properties used by this satellite.
	 * @param pos			The current cursor position. Used to popup a menu.
	 * @param actionId		Type of event that triggers the action.[HOVER_VAL,CLICK_VAL,DBLCLICK_VAL].
	 */
	protected synchronized void execute( WPSApplet applet, ActiveZone zone, Point pos, int actionId )
	{
		Satellite   firstSat    = zone.m_curSwh.m_satellites[0];
		boolean     isExe       = isDefined( actionId );

		if ( isExe )
		{
			String  actionStr = getString( actionId, zone );

			if ( actionStr != null )
			{
				String[]    actions = Base.getTextParts( getString( actionId, zone ), "\n" );
				String      action, func, args;
				int         i, j, n = actions.length;

				try
				{
					for ( i = 0; i < n; i ++ )
					{
						action  = actions[i];
						j       = action.indexOf( ' ' );
						func    = action.substring( 0, j );
						args    = parseString( action.substring( j + 1, action.length()), zone )[0];

						if ( func.equals( "show" ))         // Shows a message in the StatusBar
						{
							applet.showStatus( args );
						}
						else if ( func.equals( "open" ))      // Go to a page, opening a new browser window
						{
							j   = args.indexOf( SUBSEP );

							if ( j != -1 )  // tracking
							{
								args    = args.substring( j, args.length());
							}

							applet.actionPerformed( new ActionEvent( this, 0, args ));
						}
						else if ( func.equals( "popup" ))    // Popup a menu
						{
							MenuX   menu = (MenuX)zone.m_curSwh.m_refs.get( args );

							if ( menu != null )
							{
								PopupMenu   popup = new PopupMenu();

								menu.parseMenu( popup, applet, zone );
								applet.add( popup );
								popup.show( applet, pos.x, pos.y );
							}
						}
						else if ( func.equals( "pop" ))    // Pop a tooltip
						{
							Slice   slice   = (Slice)zone.m_curSwh.m_refs.get( args );

							if ( slice != null )
							{
								int     delay   = slice.getInt( Slice.DELAY_VAL, zone ),
										length  = slice.getInt( Slice.LENGTH_VAL, zone );

								applet.m_plan.popSlice( zone, slice, delay, length, args );
							}
						}
						else if ( func.equals( "play" ))    // Plays a sound in .au Sun audio format
						{
							AudioClip   clip = (AudioClip)applet.m_env.m_medias.get( args );

							if ( clip == null )
							{
								clip = applet.getAudioClip( applet.getCodeBase(), args );
								clip.play();
								applet.m_env.m_medias.put( args, clip );
							}
						}
						else if ( func.equals( "dump" ))   // Print a string in the console
						{
							System.out.println( args );
						}
					}
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
		}
		else if ( this != firstSat )
		{
			firstSat.execute( applet, zone, pos, actionId );
		}
	}
}
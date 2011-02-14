package com.socialcomputing.wps.client.applet;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * <p>Title: Swatch</p>
 * <p>Description: A template that describe how to draw a zone and interact with it.<br>
 * Each kind of zones usally shares the same swatchs, one for the rest and one for the current(hovered) state.
 * As the swatchs use properties that differs in each zones, the rendering and events can be differents
 * for each zone even if they share the same swatchs.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class Swatch extends Base implements Serializable
{
	/**
	 * Index of the title prop that can be reteived using JavaScript.
	 * This should be deprecated as Javascript directly access to raw NAME propertie instead.
	 */
	public      static final int    TITLE_VAL               = 1;

	/**
	 * True if this holds one or more satellites linked to their parent (optimisation).
	 */
	public      static final int    LINK_BIT                = 0x02;

	/**
	 * Objects references (MenuX, Slice...) created by the server side.
	 * This is used by the events to find menu or slices to pop.
	 */
	public  Hashtable               m_refs;

	/**
	 * JDK 1.1 serialVersionUID
	 */
	protected static final long serialVersionUID = 5513545413351365448L;

	/**
	 * Layers of satellites that describes this.
	 * Satellites are where the real information of this is.
	 */
	protected   Satellite[]     m_satellites;

	/**
	 * Creates a new Swatch by filling its satellite table.
	 * The first one (index 0) hold default values (tranformation and events).
	 * @param satellites	Satellites describing this Swatch.
	 */
	public Swatch( Satellite[] satellites )
	{
		m_satellites    = satellites;
	}

	
	/**
	 * Draws the satellites of this that have the required flags enabled.
	 * @param applet		Applet holding this.
	 * @param g				Graphics to paint in.
	 * @param zone			Zone to paint.
	 * @param isCur			True if zone is hovered.
	 * @param isFront		True to paint only satellites over the transparent filter. False to only paint those below.
	 * @param showTyp		Flags indicating what type of satellite to draw.(Satellite.XXX_TYP)
	 * @param showLinks		True if links between satelites should be drawn. False for the opposite.
	 * @throws UnsupportedEncodingException 
	 */
	protected void paint( WPSApplet applet, Graphics g, ActiveZone zone, boolean isCur, boolean isFront, int showTyp, boolean showLinks )
	{
		Satellite       sat     = m_satellites[0];
		ShapeX          shape   = sat.m_shape;
		//int             curSel  = applet.m_plan.m_curSel,
		int				flags   = getFlags( zone );
		Transfo         transfo = sat.getTransfo( Satellite.TRANSFO_VAL, zone );


		//try {
			
			// Draws Satellites links first (if they exists)
			// so they can be partly covered by other sats
			if ( isEnabled( flags, LINK_BIT )&& showLinks )
			{
				drawSats( applet, g, zone, shape, transfo, true, isCur, isFront, showTyp );
			}
			
			// Draws Satellites without links
			drawSats( applet, g, zone, shape, transfo, false, isCur, isFront, showTyp );
		/*} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		
	}

	/**
	 * Draws satellites that have the required flags enabled.
	 * Those without transfo use a default transformation.
	 * @param applet		Applet holding this.
	 * @param g				Graphics to paint in.
	 * @param zone			Zone to draw the sats.
	 * @param shape			Default shape coming from the first satellite([0]).
	 * @param transfo		Default transformation coming from the first satellite([0]).
	 * @param isLinkOnly	True to draw only links between satelites.
	 * @param isCur			True if zone is hovered.
	 * @param isFront		True to paint only satellites over the transparent filter. False to only paint those below.
	 * @param showTyp		Flags indicating what type of satellite to draw.(Satellite.XXX_TYP)
	 * @throws UnsupportedEncodingException 
	 */
	protected void drawSats( WPSApplet applet, Graphics g, ActiveZone zone, ShapeX shape, Transfo transfo, boolean isLinkOnly, boolean isCur, boolean isFront, int showTyp )// throws UnsupportedEncodingException
	{
		boolean         isBag       = zone instanceof BagZone;
		BagZone         supZone     = isBag ? (BagZone)zone : null;
		ActiveZone[]    zones       = isBag ? supZone.m_subZones : null;
		ActiveZone      curZone     = applet.m_plan.m_curZone,
						subZone;
		Satellite       sat         = m_satellites[0];
		SatData         satData     = isCur ? zone.m_curData[0] : zone.m_restData[0];
		Transfo         satRelTrf, satTrf;
		int             i, n        = m_satellites.length,
						flags;
		boolean         hasRestBit, hasCurBit, isCurSub;
		Point           satCtr,
						supCtr      = shape.getCenter( zone );

		if ( !isLinkOnly )
		{
			// Draws the place itself using the first Satellite
			sat.paint( applet, g, zone, null, null, false, satData, showTyp);
		}

		for ( i = 1; i < n; i ++ )
		{
			sat     = m_satellites[i];
			satData = isCur ? zone.m_curData[i] : zone.m_restData[i];
			flags   = satData.m_flags;

			if ((( isLinkOnly && isEnabled( flags, Satellite.LINK_BIT ))|| !isLinkOnly )&&
					isEnabled( flags, Satellite.VISIBLE_BIT )&&
					( isFront != isEnabled( flags, Satellite.BACK_BIT ))) // This Sat is visible
			{
				if ( isBag )
				{
					hasRestBit  = isEnabled( flags, Satellite.REST_BIT );
					hasCurBit   = isEnabled( flags, Satellite.CUR_BIT );
					satRelTrf   = sat.getTransfo( Satellite.TRANSFO_VAL, zone );
					satTrf      = transfo != null ? transfo.transform( satRelTrf, true ) : null;

					if( supZone.m_dir != 10.f)
					{
						if( !isEnabled( flags, Satellite.NOSIDED_BIT))
							satTrf.m_dir = supZone.m_dir;
						else 
						{
							if( isEnabled( supZone.m_flags, ActiveZone.LEFT_BIT))
								satTrf.m_dir += (Base.Pi2 / 2);
						}

					}

					float   dir = satTrf.m_dir;

					if ( zones != null && Base.isEnabled( flags, Satellite.SUB_BIT ))   // draws SubZones
					{
						int j, m    = zones.length;

						for ( j = 0; j < m; j ++ )
						{
							subZone         = zones[j];
							satTrf.m_dir   += supZone.m_stp;
							isCurSub        = subZone == curZone;
							satData         = isCur ? subZone.m_curData[i] : subZone.m_restData[i];

							if (( !isCur ||(( hasRestBit && !isCurSub )||( hasCurBit && isCurSub ))))
							{
								satCtr  = shape.transformOut( zone, satTrf );
								sat.paint( applet, g, subZone, satCtr, supCtr, isLinkOnly, satData, showTyp  );
							}
						}
					}

					if ( isEnabled( flags, Satellite.SUPER_BIT )) // draws SuperZone
					{
						isCurSub    = supZone == curZone;
						satData = isCur ? zone.m_curData[i] : zone.m_restData[i];

						if ( zones != null )    satTrf.m_dir = dir;

						if (( !isCur ||(( hasRestBit && !isCurSub )||( hasCurBit && isCurSub ))))
						{
							satCtr  = shape.transformOut( zone, satTrf );
							sat.paint( applet, g, supZone, satCtr, supCtr, isLinkOnly, satData, showTyp  );
						}
					}
				}
				else	// links
				{
					sat.paint( applet, g, zone, null, null, false, satData, showTyp );
				}
			}
		}
	}

	/**
	 * Gets this bounds by merging the satellites bounds.
	 * @param applet		The Applet that owns this.
	 * @param g				A graphics to get the FontMetrics used by this.
	 * @param zone			The zone that holds the properties used by this swatch.
	 * @param isCurZone		True if zone is hovered.
	 * @return				This swatch bounding box for zone.
	 * @throws UnsupportedEncodingException 
	 */
	Rectangle getBounds( WPSApplet applet, Graphics g, ActiveZone zone, boolean isCurZone )
	{
		Rectangle       bounds      = new Rectangle();
		Satellite       sat         = m_satellites[0];
		ShapeX          shape       = sat.m_shape;
		boolean         isBag       = zone instanceof BagZone;
		BagZone         supZone     = isBag ? (BagZone)zone : null;
		ActiveZone[]    zones       = isBag ? supZone.m_subZones : null;
		ActiveZone      subZone;
		Transfo         satRelTrf, satTrf,
						transfo     = sat.getTransfo( Satellite.TRANSFO_VAL, zone );
		int             i, n        = m_satellites.length,
						flags;
		//boolean         hasRestBit, hasCurBit, hasLinkBit, isCur;
		SatData         satData;
		Point           satCtr,
						supCtr      = shape.getCenter( zone );

		// Gets the bounds of the place itself using the first Satellite
		sat.setBounds( applet, g, zone, null, null, bounds );

		for ( i = 1; i < n; i ++ )
		{
			sat         = m_satellites[i];

			satData     = isCurZone ? zone.m_curData[i] : zone.m_restData[i];
			flags       = satData.m_flags;

			if ( Base.isEnabled( flags, Satellite.VISIBLE_BIT ))        // This Sat is visible
			{
				if ( isBag )
				{
					//hasRestBit  = Base.isEnabled( flags, Satellite.REST_BIT );
					//hasCurBit   = Base.isEnabled( flags, Satellite.CUR_BIT );
					satRelTrf   = sat.getTransfo( Satellite.TRANSFO_VAL, zone );
					satTrf      = transfo.transform( satRelTrf, true );

					if ( supZone.m_dir != 10.f )  satTrf.m_dir = supZone.m_dir;

					if (( !isEnabled( flags, Satellite.SEL_BIT )|| satData.m_isVisible )
						&& Base.isEnabled( flags, Satellite.SUPER_BIT ))    // Gets SuperZone bounds
					{
						satCtr  = shape.transformOut( zone, satTrf );
						sat.setBounds( applet, g, zone, satCtr, supCtr, bounds );
					}

					if ( zones != null && Base.isEnabled( flags, Satellite.SUB_BIT ))   // gets SubZones bounds
					{
						int j, m    = zones.length;

						for ( j = 0; j < m; j ++ )
						{
							subZone         = zones[j];

							satTrf.m_dir   += supZone.m_stp;
							satData         = isCurZone ? subZone.m_curData[i] : subZone.m_restData[i];
							flags           = satData.m_flags;

							if ( !isEnabled( flags, Satellite.SEL_BIT )|| satData.m_isVisible )
							{
								satCtr  = shape.transformOut( zone, satTrf );
								sat.setBounds( applet, g, subZone, satCtr, supCtr, bounds );
							}
						}
					}
				}
				else	// links
				{
					sat.setBounds( applet, g, zone, null, null, bounds );
				}
			}
		}

		return bounds;
	}

	/**
	 * Gets the satellite under the cursor if it is in this zone swatch or null if it isn't.
	 * @param applet		The Applet that owns this.
	 * @param g				A graphics to get the FontMetrics used by this.
	 * @param zone			Zone to check the satellites.
	 * @param pos			Location of the cursor.
	 * @param isCurZone		True if zone is the current one.
	 * @return				The sat of this swatch that is hovered or null if there isn't.
	 * @throws UnsupportedEncodingException 
	 */
	Satellite getSatAt( WPSApplet applet, Graphics g, ActiveZone zone, Point pos, boolean isCurZone )
	{
		if ( zone.getParent().m_bounds.contains( pos ))      // pos is in the Bounding Box
		{
			Satellite       sat         = m_satellites[0];
			ShapeX          shape       = sat.m_shape;
			boolean         isBag       = zone instanceof BagZone;
			BagZone         supZone        = isBag ? (BagZone)zone : null;
			ActiveZone[]    zones       = isBag ? supZone.m_subZones : null;
			ActiveZone      curZone     = applet.m_plan.m_curZone,
							subZone;
			Transfo         satRelTrf, satTrf,
							transfo     = sat.getTransfo( Satellite.TRANSFO_VAL, zone );
			int             i, n        = m_satellites.length,
							flags;
			boolean         hasRestBit, hasCurBit, hasSubBit,  isCur, isVisible;
			SatData         satData;
			Point           supCtr      = shape.getCenter( zone );

			for ( i = n - 1; i > 0; i -- )
			{
				sat     = m_satellites[i];
				satData = isCurZone ? zone.m_curData[i] : zone.m_restData[i];
				flags   = satData.m_flags;

				if ( isEnabled( flags, Satellite.VISIBLE_BIT )&&( isCurZone || !isEnabled( flags, Satellite.TIP_BIT )))    // This Sat is visible and it's not a tip (avoid anoying place popup!)
				{
					isVisible   = !isEnabled( flags, Satellite.SEL_BIT )|| satData.m_isVisible;

					if ( isBag )
					{
						hasCurBit   = isEnabled( flags, Satellite.CUR_BIT );
						hasSubBit   = isEnabled( flags, Satellite.SUB_BIT );
						satRelTrf   = sat.getTransfo( Satellite.TRANSFO_VAL, zone );

						if ( zones != null && hasSubBit && hasCurBit && satRelTrf != null && satRelTrf.m_pos == 0.f )
						{
							if ( isVisible && sat.contains( applet, g, zone, null, null, transfo, pos, true, true ))
							{
								return sat;
							}
							else
							{
								continue;
							}
						}


						hasRestBit  = isEnabled( flags, Satellite.REST_BIT );
						satTrf      = transfo.transform( satRelTrf, true );

						if ( isBag && supZone.m_dir != 10.f )  satTrf.m_dir = supZone.m_dir;

						if ( Base.isEnabled( flags, Satellite.SUPER_BIT ))  // Test if SuperZone contains pos
						{
							isCur   = supZone == curZone;

							if ( isVisible &&(( hasRestBit && !isCur )||( hasCurBit && isCur ))&& sat.contains( applet, g, zone, shape.transformOut( zone, satTrf ), supCtr, satTrf, pos, false, false ))
							{
								return sat;
							}
						}

						if ( zones != null && hasSubBit )   // Test if SubZones contains pos
						{
							int j, m    = zones.length;

							satTrf.m_dir +=( zones.length + 1 )* supZone.m_stp;

							for ( j = m - 1; j >= 0; j -- )
							{
								subZone         = zones[j];
								satTrf.m_dir   -= supZone.m_stp;
								isCur           = subZone == curZone;

								satData     = isCurZone ? subZone.m_curData[i] : subZone.m_restData[i];
								flags       = satData.m_flags;
								isVisible   = !isEnabled( flags, Satellite.SEL_BIT )|| satData.m_isVisible;

								if ( isVisible &&(( hasRestBit && !isCur )||( hasCurBit && isCur ))&& sat.contains( applet, g, subZone, shape.transformOut( zone, satTrf ), supCtr, satTrf, pos, false, false ))
								{
									return sat;
								}
							}
						}
					}
					else // links
					{
						if ( isVisible && sat.contains( applet, g, zone, null, null, transfo, pos, false, true ))
						{
							return sat;
						}
					}
				}
			}
			// Tests if the place itself contains pos
			sat = m_satellites[0];

			if ( sat.contains( applet, g, zone, null, null, transfo, pos, zones != null, true )||
				( isCurZone && !( zone instanceof LinkZone )))
			{
				return sat;
			}
		}
		return null;
	}

	/**
	 * Evaluate this swatch satellites data buffers for a zone.
	 * @param applet		The Applet that owns this.
	 * @param zone			Zone holding satellites.
	 * @param isSuper		True if zone is a BagZone.
	 * @return				An array of satellite data.
	 * @throws UnsupportedEncodingException 
	 */
	protected SatData[] evalSatData( WPSApplet applet, ActiveZone zone, boolean isSuper )
	{
		int         i, n        = m_satellites.length;
		SatData[]   satDatas    = new SatData[n];
		SatData     satData;
		Satellite   sat;
		int         flags;
		boolean     isTip, isSel;


		for ( i = 0; i < n; i ++ )
		{
			sat     = m_satellites[i];
			satData = new SatData();
			flags   = sat.getFlags( zone );
			satData.m_flags     = flags;

			isTip   = isEnabled( flags, Satellite.TIP_BIT );
			isSel   = isEnabled( flags, Satellite.SEL_BIT );

			if ( isTip || isSel )
			{
				String[]    sels    = sat.parseString( Satellite.SELECTION_VAL, zone );
				int         sel     = -1;

				if ( sels != null )
				{
					Integer     selId   = (Integer)applet.m_env.m_selections.get( sels[0] );

					sel = selId != null ? selId.intValue() : -1;
				}

				satData.m_isVisible = sat.isVisible( zone,  isTip, applet.m_plan.m_curSel, sel );
			}
			else
			{
				satData.m_isVisible = true;
			}

			satDatas[i] = satData;
		}

		return satDatas;
	}
}

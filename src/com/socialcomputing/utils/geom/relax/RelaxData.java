package com.socialcomputing.utils.geom.relax;

import java.awt.PopupMenu;
import java.lang.reflect.Field;

public class RelaxData
{
	protected   String          m_label;

	public RelaxData( String label )
	{
		m_label	= label;
	}

	protected PopupMenu getMenu()
	{
		PopupMenu   menu    = new PopupMenu();
		Field[]     fields  = getClass().getDeclaredFields();
		Field       field;
		int         i, n    = fields.length;

		try
		{
			for ( i = 0; i < n; i ++ )
			{
				field   = fields[i];
				menu.add( field.getName()+ " = " + field.get( this ));
			}
		}
		catch ( Exception e )
		{
			System.out.println( "pb" );
		}

		return menu;
	}
}

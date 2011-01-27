package com.socialcomputing.utils.geom.relax;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.math.Bounds2D;

public interface RelaxListener
{
	public  Relaxer         getRelaxer                  ( );
	public  RelaxableNode[] getNodes                    ( );
	public  RelaxableLink[] getLinks                    ( );
	public  NodeRelaxData   getBase                     ( );
	public  String          getStage                    ( );
	public  String          getStep                     ( );
	public  String          getIter                     ( );
	public  void            initData                    ( Bounds2D winBnds );
	public  boolean         initStage                   ( );
	public  boolean         initStep                    ( );
	public  boolean         initIter                    ( );
	public  void            iterate                     ( EZFlags flags );
	public  void            keyPressed                 	( KeyEvent e, Frame frame, Graphics g );
	public	HashMap			getEditableFields			( );
	public	void			updateRelaxParams			( Field field );
}

package com.socialcomputing.wps.script
{
	/**
	 * An <code>Insets</code> object is a representation of the borders 
	 * of a container. It specifies the space that a container must leave 
	 * at each of its edges. The space can be a border, a blank space, or 
	 * a title. 
	 *
	 * @version 	1.30, 12/19/03
	 * @author 	Arthur van Hoff
	 * @author 	Sami Shaio
	 * @see         java.awt.LayoutManager
	 * @see         java.awt.Container
	 * @since       JDK1.0
	 */
	public class Insets
	{
		/**
		 * The inset from the top.
		 * This value is added to the Top of the rectangle
		 * to yield a new location for the Top.
		 *
		 * @serial
		 * @see #clone()
		 */
		public var top:int;
		
		/**
		 * The inset from the left.
		 * This value is added to the Left of the rectangle
		 * to yield a new location for the Left edge.
		 *
		 * @serial
		 * @see #clone()
		 */
		public var left:int;
		
		/**
		 * The inset from the bottom.
		 * This value is subtracted from the Bottom of the rectangle
		 * to yield a new location for the Bottom.
		 *
		 * @serial
		 * @see #clone()
		 */
		public var bottom:int;
		
		/**
		 * The inset from the right.
		 * This value is subtracted from the Right of the rectangle
		 * to yield a new location for the Right edge.
		 *
		 * @serial
		 * @see #clone()
		 */
		public var right:int;
				
		public function Insets()
		{
		}
		
		/**
		 * Creates and initializes a new <code>Insets</code> object with the 
		 * specified top, left, bottom, and right insets. 
		 * @param       top   the inset from the top.
		 * @param       left   the inset from the left.
		 * @param       bottom   the inset from the bottom.
		 * @param       right   the inset from the right.
		 */
		public function init(top:int, left:int, bottom:int, right:int):void {
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}
		
		/**
		 * Set top, left, bottom, and right to the specified values
		 *
		 * @param       top   the inset from the top.
		 * @param       left   the inset from the left.
		 * @param       bottom   the inset from the bottom.
		 * @param       right   the inset from the right.
		 * @since 1.5
		 */
		public function set(top:int, left:int, bottom:int, right:int):void {
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}
				
	}
}
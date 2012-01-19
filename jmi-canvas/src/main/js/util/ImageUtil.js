JMI.namespace("util.ImageUtil");

JMI.util.ImageUtil = (function() {
	
    var ImageUtil = function(x, y) {
    };
    
	ImageUtil.prototype = {
		constructor: JMI.util.ImageUtil,
	};
	
	return ImageUtil;
}());

/*
 * Image utility class.
 * 
 * Contains utility methods to manipulate Bitmaps, BitmapsData and Graphics. 
 */

/*
 * Clear the given sprite which means : 
 *   - clear the sprite graphics 
 *   - remove all the sprite children
 * 
 * @param sprite the sprite to clear
 */
JMI.util.ImageUtil.clear= function( canvas, context) {
	context.clearRect( 0, 0, canvas.width, canvas.height);
}

JMI.util.ImageUtil.roundRect = function(context,sx,sy,ex,ey,r) {
    var r2d = Math.PI/180;
    //if( ( ex - sx ) - ( 2 * r ) < 0 ) { r = ( ( ex - sx ) / 2 ); } //ensure that the radius isn't too large for x
    //if( ( ey - sy ) - ( 2 * r ) < 0 ) { r = ( ( ey - sy ) / 2 ); } //ensure that the radius isn't too large for y
    context.beginPath();
    context.moveTo(sx+r,sy);
    context.lineTo(ex-r,sy);
    context.arc(ex-r,sy+r,r,r2d*270,r2d*360,false);
    context.lineTo(ex,ey-r);
    context.arc(ex-r,ey-r,r,r2d*0,r2d*90,false);
    context.lineTo(sx+r,ey);
    context.arc(sx+r,ey-r,r,r2d*90,r2d*180,false);
    context.lineTo(sx,sy+r);
    context.arc(sx+r,sy+r,r,r2d*180,r2d*270,false);
    context.closePath();
}

/**
 * Copy the all the elements drawn in a sprite graphics to another sprite.
 * 
 * @param src the source sprite to copy the graphics content from
 * @param dst the destination sprite in which to paste the src graphics
 */
/*public static function copy(src:Sprite, dst:Sprite):void {
	dst.graphics.copyFrom( src.graphics);
}*/

/**
 * Draw the given bitmap in the specified graphics object.
 * The scala and the position are taken frop the bitmap attributes.
 * 
 * @param image    the bitmap image to draw
 * @param graphics the graphics object where the bitmap will be rendered
 */ 
/*public static function drawBitmap(image:Bitmap, graphics:Graphics):void {
	// Initialize a matrix with the scale and position of the image
	var matrix: Matrix = new Matrix();
	matrix.scale(image.scaleX, image.scaleY);
	matrix.translate(image.x, image.y);

	// Reset line style
	graphics.lineStyle();
	
	// Fill the graphics with the image bitmap data  
	graphics.beginBitmapFill(image.bitmapData, matrix);
	graphics.drawRect(image.x, image.y, image.width, image.height);
	graphics.endFill();	
}

public static function drawTextField(text:TextField, graphics:Graphics):void {
	var bitmapData:BitmapData = new BitmapData(text.width, text.height, true, 0x000000);
	bitmapData.draw(text);
	
	// Initialize a matrix with the scale and position of the image
	var matrix: Matrix = new Matrix();
	matrix.scale(1, 1);
	matrix.translate(text.x, text.y);
	
	// Fill the graphics with the image bitmap data  
	graphics.beginBitmapFill(bitmapData, matrix);
	graphics.lineStyle();
	graphics.drawRect(text.x, text.y, text.width, text.height);
}*/

/**
 * Apply a half transparent color over an image.
 * This is achieved by drawing 45° lines every 2 pixels.
 * 
 * @param image The image to cover.
 * @param dim   size of the image.
 */
/*JMI.util.ImageUtil.filterImage = function( sprite:Sprite, dim:Dimension, color:uint):void {
	var g:Graphics = sprite.graphics;
	g.lineStyle(1, color, 1);
	
	var w:int= dim.width - 1,
		h:int = dim.height - 1,
		min:int = Math.min( w, h ),
		i:int, j:int, n:int = min + 2;
	
	for ( i = 1, j =( w + h + 1)% 2; i < n; i += 2, j += 2)
	{
		//g.drawLine( 0, i, i, 0);
		g.moveTo(0, i);
		g.lineTo(i, 0);
		//g.drawLine( w - j, h, w, h - j );
		g.moveTo(w - j, h);
		g.lineTo(w, h-j);
	}
	if ( w > h )
	{
		n = w - min;
		
		for ( i = 1+( h % 2); i < n; i += 2)
		{
			//g.drawLine( i, h, min + i, 0);
			g.moveTo(i, h);
			g.lineTo(min+i, 0);
		}
	}
	else
	{
		n = h - min;
		
		for ( i = 1+( w % 2); i < n; i += 2)
		{
			//g.drawLine( w, i, 0, min + i );
			g.moveTo(w, i);
			g.lineTo(0, min + i);
		}
	}
}*/

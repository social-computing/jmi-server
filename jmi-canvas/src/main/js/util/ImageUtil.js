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
 * Clear a canevas drawing context 
 */
JMI.util.ImageUtil.clear = function(canvas, context) {
	context.clearRect(0, 0, canvas.width, canvas.height);
};

JMI.util.ImageUtil.roundRect = function(context, x, y, width, height, radius) {
	  context.beginPath();
	  context.moveTo(x + radius, y);
	  context.lineTo(x + width - radius, y);
	  context.quadraticCurveTo(x + width, y, x + width, y + radius);
	  context.lineTo(x + width, y + height - radius);
	  context.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
	  context.lineTo(x + radius, y + height);
	  context.quadraticCurveTo(x, y + height, x, y + height - radius);
	  context.lineTo(x, y + radius);
	  context.quadraticCurveTo(x, y, x + radius, y);
	  context.closePath();
};

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
 * Apply a half transparent color over an image.
 * This is achieved by drawing 45° lines every 2 pixels.
 * 
 * @param image The image to cover.
 * @param dim   size of the image.
 */
JMI.util.ImageUtil.filterImage = function( gDrawingContext, dim, color) {
	var w = dim.width - 1,
		h = dim.height - 1,
		min = Math.min( w, h ),
		i , j , n = min + 2;
	
    gDrawingContext.beginPath();
	for ( i = 1, j =( w + h + 1)% 2; i < n; i += 2, j += 2)
	{
		gDrawingContext.moveTo(0, i);
		gDrawingContext.lineTo(i, 0);

		gDrawingContext.moveTo(w - j, h);
		gDrawingContext.lineTo(w, h-j);
	}
	if ( w > h )
	{
		n = w - min;
		
		for ( i = 1+( h % 2); i < n; i += 2)
		{
			gDrawingContext.moveTo(i, h);
			gDrawingContext.lineTo(min+i, 0);
		}
	}
	else
	{
		n = h - min;
		
		for ( i = 1+( w % 2); i < n; i += 2)
		{
			gDrawingContext.moveTo(w, i);
			gDrawingContext.lineTo(0, min + i);
		}
	}
    gDrawingContext.closePath();
    gDrawingContext.lineWidth = 1;
    gDrawingContext.fillStyle = color;
    gDrawingContext.fill();
}

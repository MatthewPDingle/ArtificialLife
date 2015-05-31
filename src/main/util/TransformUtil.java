package main.util;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class TransformUtil
{
	/**
	 * Translates a point given in screen coordinates to a point
	 * in world coordinates.  Two transforms are involved.  The first
	 * is a scale transform that scales from the screen size to the
	 * currentWorldView size.  The second is a translation transform
	 * that moves from the upper left (base) of the world to the 
	 * currentWorldView location.
	 * 
	 * @param point
	 * @param screenD The dimensions of the component you're displaying
	 * @param currentWorldView A window displaying a piece of the world
	 * @return
	 */
	public static final Point2D screenToWorld (Point2D point, 
			Dimension screenD, Rectangle2D currentWorldView) 
	{
		double xScale = currentWorldView.getWidth() / screenD.getWidth();
		double yScale = currentWorldView.getHeight() / screenD.getHeight();
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(xScale, yScale);
		
		Point2D worldLocation = scaleTransform.transform(point, null);
		
		AffineTransform translation = new AffineTransform();
		translation.translate(currentWorldView.getX(),
				currentWorldView.getY());
		worldLocation = translation.transform(worldLocation, null);
		
		return worldLocation;
	}
	
	/**
	 * Scales a point in screen coordinates to a point in world 
	 * 
	 * 
	 * @param point
	 * @param screenD
	 * @param currentWorldView
	 * @return
	 */
	public static final Point2D screenToWorldScaleOnly (Point2D point,
			Dimension screenD, Rectangle2D currentWorldView) 
	{
		double xScale = currentWorldView.getWidth() / screenD.getWidth();
		double yScale = currentWorldView.getHeight() / screenD.getHeight();
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(xScale, yScale);
		
		return scaleTransform.transform(point, null);
	}
	
	/**
	 * Translates a point given in world coordinates to a point
	 * in screen coordinates.  Two transforms are involved.  The first
	 * is a translation transform that moves from the location of the
	 * currentWorldView to the upper left (base) of the world.  The
	 * second is a scale transform that scales from the currentWorldView
	 * size to the screen size.
	 * 
	 * @param point
	 * @param screenD The dimensions of the component you're displaying
	 * @param currentWorldView A window displaying a piece of the world
	 * @return
	 */
	public static final Point2D worldToScreen (Point2D point, 
			Dimension screenD, Rectangle2D currentWorldView) 
	{
		AffineTransform translation = new AffineTransform();
		translation.translate(-currentWorldView.getX(),
				-currentWorldView.getY());
		point = translation.transform(point, null);
		
		double xScale = screenD.getWidth() / currentWorldView.getWidth();
		double yScale = screenD.getHeight() / currentWorldView.getHeight();
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(xScale, yScale);
		
		return scaleTransform.transform(point, null);
	}

	/**
	 * Scales a vector in world coordinates to a vector on screen
	 * 
	 * 
	 * @param point
	 * @param screenD
	 * @param currentWorldView
	 * @return
	 */
	public static final Point2D worldToScreenScaleOnly (Point2D point,
			Dimension screenD, Rectangle2D currentWorldView) 
	{
		double xScale = screenD.getWidth() / currentWorldView.getWidth();
		double yScale = screenD.getHeight() / currentWorldView.getHeight();
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(xScale, yScale);
		
		return scaleTransform.transform(point, null);
	}
	
	/**
	 * Translates a point in 2D by deltaX and deltaY.  They can be 
	 * positive or negative.
	 * 
	 * @param point
	 * @param deltaX
	 * @param deltaY
	 * @return
	 */
	public static final Point2D translate (Point2D point, double deltaX, double deltaY) 
	{
		AffineTransform translation = new AffineTransform();
		translation.translate(deltaX, deltaY);
		return translation.transform(point, null);
	}
}

package main.util;

import java.awt.geom.Point2D;

public class GeometryUtil {
	
	public static double calculateDistance(Point2D p1, Point2D p2) {
		try {
			double x = (p2.getX() - p1.getX()) * (p2.getX() - p1.getX());
			double y = (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()); 
			double sum = x + y;
			double sqrt = Math.sqrt(sum);
			if (Double.isNaN(sqrt)) {
				throw new Exception ("Tried taking Math.sqrt(" + sum + ")");
			}
			return Math.sqrt(sum);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static Point2D getUnitVector(Point2D p1, Point2D p2) {
		try {
			Point2D vector = new Point2D.Double();
			vector.setLocation(p2.getX() - p1.getX(), p2.getY() - p1.getY());
			double distance = Math.sqrt((vector.getX() * vector.getX()) + (vector.getY() * vector.getY()));
			if (Double.isNaN(distance)) {
				throw new Exception("Tried making Math.sqrt(" + (vector.getX() * vector.getX()) + (vector.getY() * vector.getY()) + ")");
			}
			Point2D unitVector = new Point2D.Double();
			unitVector.setLocation(vector.getX() / distance, vector.getY() / distance);
			return unitVector;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Point2D getVector(Point2D p1, Point2D p2) {
		Point2D vector = new Point2D.Double();
		vector.setLocation(p2.getX() - p1.getX(), p2.getY() - p1.getY());
		return vector;
	}
	
	public static double getDotProduct(Point2D p1, Point2D p2) {
		try {
			return (p1.getX() * p2.getX()) + (p1.getY() * p2.getY());
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static float getHypotenuse(Point2D p) {
		try {
			float h = (float)Math.sqrt((p.getX() * p.getX()) + (p.getY() * p.getY()));
			if (Float.isNaN(h)) {
				throw new Exception("Tried to do a bad Math.sqrt(" + (p.getX() * p.getX()) + (p.getY() * p.getY()) + ")");
			}
			return h;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static Point2D getAverageVector(Point2D p1, Point2D p2) {
		double x = (p1.getX() + p2.getX()) / 2d;
		double y = (p1.getY() + p2.getY()) / 2d;
		return new Point2D.Double(x, y);
	}
	
	public static float getAngleInDegrees(Point2D p) {
		return (float)Math.toDegrees(Math.atan2(-p.getY(), p.getX()));
	}
	
	public static Point2D getVectorFromAngleInDegrees(float angle, float hypotenuse) {
		float x = (float)Math.cos(Math.toRadians(angle)) * hypotenuse;
		float y = (float)Math.sin(Math.toRadians(angle)) * -hypotenuse;
		return new Point2D.Double(x, y);
	}
}
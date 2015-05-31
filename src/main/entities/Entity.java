package main.entities;

import java.awt.Color;
import java.awt.geom.Point2D;

public abstract class Entity {

	protected int id;
	protected String name = "";
	protected float amount;
	protected float maxAmount;
	protected float size;
	protected float age;
	protected Color color;
	protected Point2D center;
	
	public Point2D getCenter() {
		return center;
	}

	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}
	
	public float getSize() {
		return size;
	}

	public float getAmount() {
		return amount;
	}
	
	public float getAge() {
		return age;
	}
	
	public Color getColor() {
		return color;
	}
	
	public float getPercentage() {
		return amount / maxAmount;
	}
}
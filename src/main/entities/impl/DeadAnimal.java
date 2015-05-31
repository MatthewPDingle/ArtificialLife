package main.entities.impl;

import java.awt.Color;
import java.awt.geom.Point2D;

import main.life.managers.EntitySingleton;

public class DeadAnimal extends Food {

	public DeadAnimal(int id, Color color, float size, float amount, float maxAmount, Point2D center) {
		super();
		this.id = id;
		this.color = color;
		this.size = size;
		this.amount = amount;
		this.maxAmount = maxAmount;
		this.center = center;
	}
	
	public float consume(float amount) {
		if (this.amount < amount) {
			amount = this.amount;
		}
		if (this.amount >= amount) {
			this.amount -= amount;
		}
		if (this.amount <= 0) {
			EntitySingleton.getInstance().getEntities().remove(this);
		}
		return amount;
	}
}
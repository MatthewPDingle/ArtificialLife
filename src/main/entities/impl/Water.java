package main.entities.impl;

import java.awt.Color;
import java.awt.geom.Point2D;

import main.entities.Entity;
import main.life.managers.EntitySingleton;

public class Water extends Entity {

	public Water(int id, Color color, float amount, float maxAmount, Point2D center) {
		super();
		this.id = id;
		this.color = color;
		this.amount = amount;
		this.maxAmount = maxAmount;
		this.size = 50;
		this.center = center;
	}
	
	public void updateStats() {
		amount += -.00001f;
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
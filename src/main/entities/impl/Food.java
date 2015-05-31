package main.entities.impl;

import java.awt.Color;
import java.awt.geom.Point2D;

import main.entities.Entity;
import main.life.managers.EntitySingleton;

public class Food extends Entity {

	public Food() {
		super();
	}
	
	public Food(int id, Color color, String name, float amount, float maxAmount, Point2D center) {
		super();
		this.id = id;
		this.color = color;
		this.name = name;
		this.amount = amount;
		this.maxAmount = maxAmount;
		this.age = 0;
		this.size = 10;
		this.center = center;
	}
	
	public void updateStats() {
		age += .0001f;
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
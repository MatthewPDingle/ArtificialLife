package main.life.ai.impl;

import java.awt.geom.Point2D;

import main.life.ai.AI;

public class NaivePredator extends AI {

	public void think() {
		float maxSpeed = myself.getMaxSpeed();
		myself.setMovementVector(new Point2D.Double(maxSpeed, 0));
	}

}
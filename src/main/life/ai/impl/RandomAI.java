package main.life.ai.impl;

import java.awt.geom.Point2D;

import main.entities.impl.Animal;
import main.entities.impl.Food;
import main.entities.impl.Water;
import main.life.ai.AI;
import main.util.GeometryUtil;
import constants.Comparator;

public class RandomAI extends AI {
	
	public void think() {
		// If eating
		if (myself.isEating()) {
			myself.eat();
		}
		if (myself.getFood() >= myself.getEatUntil()) {
			myself.setEating(false);
		}
		
		// If drinking
		if (myself.isDrinking()) {
			myself.drink();
		}
		if (myself.getWater() >= myself.getDrinkUntil()) {
			myself.setDrinking(false);
		}
		
		// If thirsty, seek out water
		if (myself.getWater() < myself.getThirstyLevel() && !myself.isDrinking() && !myself.isEating()) {
			// Find nearest water
			Water nearestWater = findNearestWater();
			
			// If I see water nearby
			if (nearestWater != null) {
				Point2D vectorToWater = GeometryUtil.getVector(myself.getCenter(), nearestWater.getCenter());
				float vectorLength = GeometryUtil.getHypotenuse(vectorToWater);
				
				// If the water is close enough to drink, drink it.
				if (vectorLength <= nearestWater.getSize() / 2f) {
					myself.drink(nearestWater);
				}
				// Not close enough to drink.  Move over there.
				else {
					float maxSpeed = myself.getMaxSpeed();
					float factor = vectorLength / maxSpeed;
					
					Point2D vectorAtMaxSpeed = new Point2D.Double(vectorToWater.getX() / factor, vectorToWater.getY() / factor);
					
					Point2D newVector = GeometryUtil.getAverageVector(myself.getMovementVector(), vectorAtMaxSpeed);
					
					myself.setMovementVector(newVector);
				}
			}
			else{ 
				moveToSearch();
			}
		}
		// If hungry, seek out food
		if (myself.getFood() < myself.getHungryLevel() && !myself.isDrinking() && !myself.isEating()) {
			// Find nearest food
			Food nearestFood = findNearestFood();
			
			// If I detect food nearby
			if (nearestFood != null) {
				Point2D vectorToFood = GeometryUtil.getVector(myself.getCenter(), nearestFood.getCenter());
				float vectorLength = GeometryUtil.getHypotenuse(vectorToFood);
				
				// If the food is close enough to eat, eat it.
				if (vectorLength <= myself.getSize()) {
					myself.eat(nearestFood);
				}
				// Not close enough to eat.  Move over there.
				else {
					float maxSpeed = myself.getMaxSpeed();
					float factor = vectorLength / maxSpeed;
					
					Point2D vectorAtMaxSpeed = new Point2D.Double(vectorToFood.getX() / factor, vectorToFood.getY() / factor);
					
					Point2D newVector = GeometryUtil.getAverageVector(myself.getMovementVector(), vectorAtMaxSpeed);
					
					myself.setMovementVector(newVector);
				}
			}
			// No food nearby
			else {
				moveToSearch();
			}
		}
		// Else have sex or move at random
		else {
			if (myself.isVerile()) {
				Animal nearestAnimal = findNearestAnimal(Comparator.SAME, Comparator.OPPOSITE);
				if (nearestAnimal != null) {
					Point2D vectorToAnimal = GeometryUtil.getVector(myself.getCenter(), nearestAnimal.getCenter());
					float vectorLength = GeometryUtil.getHypotenuse(vectorToAnimal);
					
					// If I'm right by another animal, fuck it.  
					if (vectorLength <= myself.getSize()) {
						myself.fuck(nearestAnimal);
					}
					// Else move over to it.
					else {
						float maxSpeed = myself.getMaxSpeed();
						float factor = vectorLength / maxSpeed;
						
						Point2D vectorAtMaxSpeed = new Point2D.Double(vectorToAnimal.getX() / factor, vectorToAnimal.getY() / factor);
						
						Point2D newVector = GeometryUtil.getAverageVector(myself.getMovementVector(), vectorAtMaxSpeed);
						
						myself.setMovementVector(newVector);
					}
				}
				else {
					moveToSearch();
				}
			}
			else {
				moveToSearch();
			}
		}
	}
	
	private void moveAtRandom() {
		Point2D movementVector = myself.getMovementVector();
		float deltaX = (float)(Math.random() * 2f - 1f) / 20f; // -.05 to .05
		float deltaY = (float)(Math.random() * 2f - 1f) / 20f;
		float newDeltaX = (float)movementVector.getX() + deltaX;
		float newDeltaY = (float)movementVector.getY() + deltaY;

		myself.setMovementVector(new Point2D.Double(newDeltaX, newDeltaY));
	}
	
	private void moveToSearch() {
		Point2D movementVector = myself.getMovementVector();
		if (movementVector.getX() == 0 && movementVector.getY() == 0) {
			float deltaX = (float)(Math.random() * 2f - 1f) / 20f; // -.05 to .05
			float deltaY = (float)(Math.random() * 2f - 1f) / 20f;
			float newDeltaX = (float)movementVector.getX() + deltaX;
			float newDeltaY = (float)movementVector.getY() + deltaY;
			movementVector = new Point2D.Double(newDeltaX, newDeltaY);
		}
		
		float angleD = GeometryUtil.getAngleInDegrees(movementVector);
		angleD += ((Math.random() * 10f) - 5f);
		float hypotenuse = GeometryUtil.getHypotenuse(movementVector);
		Point2D newMovementVector = GeometryUtil.getVectorFromAngleInDegrees(angleD, hypotenuse);
		
		float maxSpeed = myself.getMaxSpeed();
		
		float factor = hypotenuse / maxSpeed;
		if (factor == 0) {
			factor = 1;
		}
		Point2D vectorAtMaxSpeed = new Point2D.Double(newMovementVector.getX() / factor, newMovementVector.getY() / factor);
		
		myself.setMovementVector(vectorAtMaxSpeed);
	}
}
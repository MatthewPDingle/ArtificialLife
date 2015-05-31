package main.life.ai;

import main.entities.Entity;
import main.entities.impl.Animal;
import main.entities.impl.Food;
import main.entities.impl.Water;
import main.life.managers.AnimalSingleton;
import main.life.managers.EntitySingleton;
import main.util.GeometryUtil;
import constants.Comparator;

public abstract class AI {

	protected Animal myself = null;
	
	protected EntitySingleton entitySingleton = EntitySingleton.getInstance();
	protected AnimalSingleton animalSingleton = AnimalSingleton.getInstance();
	
	/**
	 * Modify the movement vector to change movement.
	 * Eating will automatically cause the animal to stop.
	 */
	public abstract void think();
	
	public void knowMyself(Animal animal) {
		this.myself = animal;
	}
	
	protected Food findNearestFood() {
		Food nearestFood = null;
		float nearestDistance = 1000000f;
		for (Entity entity : entitySingleton.getEntities()) {
			if (entity instanceof Food) {
				Food food = (Food)entity;
				float distance = (float)GeometryUtil.calculateDistance(myself.getCenter(), food.getCenter());
				boolean canSee = myself.canSee(food.getCenter());
				boolean canSmell = myself.canSmell(distance);
				if (canSmell || canSee) {
					if (distance < nearestDistance) {
						nearestDistance = distance;
						nearestFood = food;
					}
				}
			}
		}
		return nearestFood;
	}
	
	protected Water findNearestWater() {
		Water nearestWater = null;
		float nearestDistance = 1000000f;
		for (Entity entity : entitySingleton.getEntities()) {
			if (entity instanceof Water) {
				Water water = (Water)entity;
				float distance = (float)GeometryUtil.calculateDistance(myself.getCenter(), water.getCenter());
				boolean canSee = myself.canSee(water.getCenter());
				if (canSee) {
					if (distance < nearestDistance) {
						nearestDistance = distance;
						nearestWater = water;
					}
				}
			}
		}
		return nearestWater;
	}
	
	/**
	 * 
	 * @param species If you want the SAME, OPPOSITE, or EITHER species
	 * @param gender If you want the SAME, OPPOSITE, or EITHER gender
	 * @return
	 */
	protected Animal findNearestAnimal(Comparator species, Comparator gender) {
		Animal nearestAnimal = null;
		float nearestDistance = 1000000f;
		for (Animal animal : animalSingleton.getAnimals()) {
			boolean valid = true;
			if (species == Comparator.SAME) {
				if (!sameSpecies(myself, animal)) {
					valid = false;
				}
			}
			else if (species == Comparator.OPPOSITE) {
				if (sameSpecies(myself, animal)) {
					valid = false;
				}
			}
			if (gender == Comparator.SAME) {
				if (!sameGender(myself, animal)) {
					valid = false;
				}
			}
			else if (gender == Comparator.OPPOSITE) {
				if (sameGender(myself, animal)) {
					valid = false;
				}
			}
				
			if (valid) {
				float distance = (float)GeometryUtil.calculateDistance(myself.getCenter(), animal.getCenter());
				boolean canSee = myself.canSee(animal.getCenter());
				boolean canSmell = myself.canSmell(distance);
				if (canSmell || canSee) {
					if (distance < nearestDistance) {
						nearestDistance = distance;
						nearestAnimal = animal;
					}
				}
			}
		}
		return nearestAnimal;
	}
	
	private boolean sameSpecies(Animal a1, Animal a2) {
		if (a1.getSpecies().equals(a2.getSpecies())) {
			return true;
		}
		return false;
	}
	
	private boolean sameGender(Animal a1, Animal a2) {
		if (a1.getGender().equals(a2.getGender())) {
			return true;
		}
		return false;
	}
}
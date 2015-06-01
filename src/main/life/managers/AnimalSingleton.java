package main.life.managers;

import java.util.ArrayList;

import main.entities.impl.Animal;

public class AnimalSingleton {

	private ArrayList<Animal> animals = new ArrayList<Animal>();
	private int detailsDisplayId = 0;
	private int counter = 0;
	
	private static AnimalSingleton instance = null;
	
	protected AnimalSingleton() {
	}
	
	public static AnimalSingleton getInstance() {
		if (instance == null) {
			instance = new AnimalSingleton();
		}
		return instance;
	}
	
	public ArrayList<Animal> getAnimals() {
		return animals;
	}
	
	public void addAnimal(Animal a) {
		animals.add(a);
	}

	public Animal getNext() {
		if (animals.size() == 0) {
			return null;
		}
		
		int smallestPositiveDiff = 1000000;
		int newDetailsDisplayId = detailsDisplayId;
		
		for (Animal animal : animals) {
			int diff = animal.getID() - detailsDisplayId;
			if (diff > 0 && diff < smallestPositiveDiff) {
				smallestPositiveDiff = diff;
				newDetailsDisplayId = animal.getID();
			}
		}
		detailsDisplayId = newDetailsDisplayId;
		if (smallestPositiveDiff == 1000000) {
			int smallestId = 1000000;
			for (Animal animal : animals) {
				if (animal.getID() < smallestId) {
					smallestId = animal.getID();
				}
			}
			detailsDisplayId = smallestId;
		}
		
		for (Animal animal : animals) {
			if (animal.getID() == detailsDisplayId) {
				return animal;
			}
		}
		return null;
	}
	
	public Animal getPrevious() {
		if (animals.size() == 0) {
			return null;
		}
		
		int smallestPositiveDiff = 1000000;
		int newDetailsDisplayId = detailsDisplayId;
		
		for (Animal animal : animals) {
			int diff = detailsDisplayId - animal.getID();
			if (diff > 0 && diff < smallestPositiveDiff) {
				smallestPositiveDiff = diff;
				newDetailsDisplayId = animal.getID();
			}
		}
		detailsDisplayId = newDetailsDisplayId;
		if (smallestPositiveDiff == 1000000) {
			int biggestId = 0;
			for (Animal animal : animals) {
				if (animal.getID() > biggestId) {
					biggestId = animal.getID();
				}
			}
			detailsDisplayId = biggestId;
		}
		
		for (Animal animal : animals) {
			if (animal.getID() == detailsDisplayId) {
				return animal;
			}
		}
		return null;
	}
	
	public Animal getCurrent() {
		if (animals.size() == 0) {
			return null;
		}

		for (Animal animal : animals) {
			if (detailsDisplayId == animal.getID()) {
				return animal;
			}
		}
		return getPrevious();
	}

	public int getNextCounter() {
		return counter++;
	}
}
package main.life.managers;

import java.util.ArrayList;

import main.entities.impl.Animal;

public class AnimalSingleton {

	private ArrayList<Animal> animals = new ArrayList<Animal>();
	private int detailsDisplayIndex = 0;
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
		detailsDisplayIndex++;
		if (detailsDisplayIndex >= animals.size()) {
			detailsDisplayIndex = 0;
		}
		Animal a = animals.get(detailsDisplayIndex);
		return a;
	}
	
	public Animal getPrevious() {
		if (animals.size() == 0) {
			return null;
		}
		detailsDisplayIndex--;
		if (detailsDisplayIndex < 0) {
			detailsDisplayIndex = animals.size() - 1;
		}
		Animal a = animals.get(detailsDisplayIndex);
		return a;
	}
	
	public Animal getCurrent() {
		if (animals.size() == 0) {
			return null;
		}
		if (detailsDisplayIndex >= animals.size()) {
			detailsDisplayIndex = 0;
		}
		Animal a = animals.get(detailsDisplayIndex);
		return a;
	}

	public int getNextCounter() {
		return counter++;
	}
}
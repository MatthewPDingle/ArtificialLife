package main.life.managers;

import java.util.ArrayList;

import main.entities.Entity;

public class EntitySingleton {

	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	private static EntitySingleton instance = null;
	
	protected EntitySingleton() {
	}
	
	public static EntitySingleton getInstance() {
		if (instance == null) {
			instance = new EntitySingleton();
		}
		return instance;
	}
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
	
	public void addEntity(Entity a) {
		entities.add(a);
	}
}
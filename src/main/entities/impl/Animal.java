package main.entities.impl;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

import main.entities.Entity;
import main.gui.MainUI;
import main.life.ai.AI;
import main.life.ai.impl.RandomAI;
import main.life.managers.AnimalSingleton;
import main.life.managers.EntitySingleton;
import main.util.GeometryUtil;
import main.util.MathUtil;

import org.jfree.data.xy.XYSeries;

public class Animal extends Entity {

	protected int generation;
	protected String species;
	protected float health; 	// 0 - 1
	protected float food;		// 0 - 1
	protected float water; 		// 0 - 1
	protected float energy;		// 0 - 1
	protected float fitness;	// 0 - 1
	protected float mass;
	protected float age;
	protected String gender;	// male or female
	protected float smellingDistance;	// Same world units as size
	protected float visionDistance;		// Same world units as size
	protected int visionFOV;			// Field of view (in degrees)
	protected float timeSinceLastBaby;
	
	private boolean eating = false;
	private Food eatingFood = null;
	private boolean drinking = false;
	private Water drinkingWater = null;
	
	protected Point2D movementVector;
	protected LinkedList<Point2D> last10movementVectors = new LinkedList<Point2D>();
	
	protected LinkedList<Float> foodHistory = new LinkedList<Float>();
	protected LinkedList<Float> waterHistory = new LinkedList<Float>();
	protected LinkedList<Float> healthHistory = new LinkedList<Float>();
	protected LinkedList<Float> energyHistory = new LinkedList<Float>();
	protected LinkedList<Float> fitnessHistory = new LinkedList<Float>();
	protected LinkedList<Float> speedHistory = new LinkedList<Float>();
	
	protected AI ai = null;
	
	public Animal(int generation, String species, String gender, Color color, float size, float mass, 
			float smellingDistance, float visionDistance, int visionFOV, Point2D center, AI ai) {
		super();
		this.id = AnimalSingleton.getInstance().getNextCounter();
		this.generation = generation;
		this.species = species;
		this.gender = gender;
		this.color = color;
		this.health = .8f;
		this.food = .8f;
		this.water = .9f;
		this.energy = .8f;
		this.fitness = .25f;
		this.size = size;
		this.mass = mass;
		this.smellingDistance = smellingDistance;
		this.visionDistance = visionDistance;
		this.visionFOV = visionFOV;
		this.age = 0;
		this.timeSinceLastBaby = 0;
		this.movementVector = new Point2D.Double(0, 0);
		
		this.center = center;
		
		if (ai == null) {
			this.ai = new RandomAI();
		}
		else {
			this.ai = ai;
		}
		this.ai.knowMyself(this);
	}
	
	public String toString() {
		String output = "";
		output += "ID: " + id + "\n";
		output += "Generation: " + generation + "\n";
		output += "Species: " + species + "\n";
		output += "Gender: " + gender + "\n";
		output += "Color: RGB(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")" + "\n";
		output += "Health: " + String.format("%.2f", health) + "\n";
		output += "Food: " + String.format("%.2f", food) + "\n";
		output += "Water: " + String.format("%.2f", water) + "\n";
		output += "Energy: " + String.format("%.2f", energy) + "\n";
		output += "Fitness: " + String.format("%.2f", fitness) + "\n";
		output += "Smell: " + String.format("%.2f", smellingDistance) + "\n";
		output += "Vision: " + String.format("%.2f", visionDistance) + "\n";
		output += "FOV: " + visionFOV + "\n";
		output += "Size: " + size + "\n";
		output += "Age: " + String.format("%.2f", age) + "\n";
		return output;
	}

	public void move() {
		// Update position
		float deltaX = (float)movementVector.getX();
		float deltaY = (float)movementVector.getY();
		Point2D potentialNewCenter = new Point2D.Double(center.getX() + deltaX, center.getY() + deltaY);
	
		// Out of bounds checking
		if (potentialNewCenter.getX() < (size / 2f)) {
			deltaX = (float)(-center.getX() + (size / 2f));
			movementVector.setLocation(0, deltaY);
		}
		if (potentialNewCenter.getX() > MainUI.WORLD_WIDTH - (size / 2f)) {
			deltaX = (float)(-center.getX() + MainUI.WORLD_WIDTH - (size / 2f));
			movementVector.setLocation(0, deltaY);
		}
		if (potentialNewCenter.getY() < (size / 2f)) {
			deltaY = (float)(-center.getY() + (size / 2f));
			movementVector.setLocation(deltaX, 0);
		}
		if (potentialNewCenter.getY() > MainUI.WORLD_HEIGHT - (size / 2f)) {
			deltaY = (float)(-center.getY() + MainUI.WORLD_HEIGHT - (size / 2f));
			movementVector.setLocation(deltaX, 0);
		}

		potentialNewCenter = new Point2D.Double(center.getX() + deltaX, center.getY() + deltaY);
		this.center = potentialNewCenter;

		updateStats();
	}
	
	private void updateStats() {
		float deltaX = (float)movementVector.getX();
		float deltaY = (float)movementVector.getY();
		float speed = (float)Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
		if (Float.isNaN(speed)) {
			speed = 0;
		}
		
		// Update fitness based on speed
		float fitnessDecay = -.00003f - (age * .2f * age / 80000f);
		float fitnessIncrease = speed * .0001f;
		fitness = fitness + fitnessIncrease + fitnessDecay;
		
		// Update energy based on speed
		energy = energy - (speed * speed * .001f * (1.1f - fitness));
		energy += .0001;
		
		// Update energy based on food & water
		float foodImpact = -(.2f - food) * .002f;
		energy = energy + foodImpact;
		float waterImpact = -(.2f - water) * .0002f;
		energy = energy + waterImpact;
		
		// Update food and water
		float speedHit = speed * -.0004f;
		float timeHit = -.00001f;
		food = food + speedHit + timeHit;
		water = water + (speedHit / 2f) + timeHit;
		
		// Update health
		if (health >= .5) {
			health += (food * .0001f);
		}
		if (health < .1) {
			health -= .0001f;
		}
		if (food <= 0) {
			health -= .00005f;
		}
		if (water <= 0) {
			health -= .0002f;
		}
		if (health <= 0) {
			die();
		}
		
		// Update age
		age += .0001f;
		
		// Maybe die of old age
		if (age > 8) {
			float increasedRisk = age * .00001f;
			if (Math.random() < increasedRisk) {
				die();
			}
		}
		
		// Update time since last baby
		timeSinceLastBaby += .0001f;
		
		// Make sure the stats fall within their min/max range
		statRangeCheck();
		
		// Update speed and movementVector based on energy & fitness
//		float energyHit = MathUtil.log(energy + .05f, 2f);
//		float newSpeed = speed * (1 + (energyHit / 100f));
		float energyHit = MathUtil.log(energy + .1f, 1.5f);
		float newSpeed = speed * (1 + (energyHit / 100f));
		if (newSpeed == 0) newSpeed = .1f;
		float speedChangePercent = newSpeed / speed;
		if (Float.isNaN(speedChangePercent)) {
			speedChangePercent = 1f;
		}
		if (Float.isInfinite(speedChangePercent)) {
			speedChangePercent = 1f;
		}
		movementVector.setLocation(deltaX * speedChangePercent, deltaY * speedChangePercent);

		// Update histories
		if (foodHistory.size() > 1000) foodHistory.removeLast();
		foodHistory.addFirst(food);
		
		if (waterHistory.size() > 1000) waterHistory.removeLast();
		waterHistory.addFirst(water);
		
		if (energyHistory.size() > 1000) energyHistory.removeLast();
		energyHistory.addFirst(energy);
		
		if (fitnessHistory.size() > 1000) fitnessHistory.removeLast();
		fitnessHistory.addFirst(fitness);
		
		if (healthHistory.size() > 1000) healthHistory.removeLast();
		healthHistory.addFirst(health);
		
		if (speedHistory.size() > 1000) speedHistory.removeLast();
		speedHistory.addFirst(speed);
	}
	
	private void statRangeCheck() {
		if (food > 1) food = 1;
		if (food < 0) food = 0;
		if (water > 1) water = 1;
		if (water < 0) water = 0;
		if (health > 1) health = 1;
		if (health < 0) health = 0;
		if (energy > 1) energy = 1;
		if (energy < 0) energy = 0;
		if (fitness > 1) fitness = 1;
		if (fitness < 0) fitness = 0;
	}
	
	public void tradeFoodForEnergy(float foodAmount) {
		// Can only use as much food as I have
		if (food < foodAmount) {
			foodAmount = food;
		}
		
		if (energy + foodAmount > 1) {
			// Don't need the full amount
			float neededAmount = 1 - (energy + foodAmount);
			energy = energy + neededAmount;
			food = food - neededAmount;
		}
		else {
			// Use as much food as I can
			energy = energy + foodAmount;
			food = food - foodAmount;
		}
	}
	
	public boolean canSmell(float distance) {
		if (distance <= smellingDistance) {
			return true;
		}
		return false;
	}
	
	public boolean canSee(Point2D p) {
		float distance = (float)GeometryUtil.calculateDistance(this.center, p);
		if (distance > visionDistance) {
			return false;
		}
		Point2D pointVector = GeometryUtil.getVector(this.center, p);
		float pointAngle = GeometryUtil.getAngleInDegrees(pointVector);
		float movementAngle = GeometryUtil.getAngleInDegrees(movementVector);
		
		if (Math.abs(pointAngle - movementAngle) <= (visionFOV / 2))
			return true;
		else
			return false;
	}
	
	public float getMaxSpeed() {
		float minSpeed = .25f;
		if (eating || drinking) {
			minSpeed = 0;
		}
//		float speed = (((1 + energy) * (1 + energy)) * fitness) + minSpeed;
//		float speed = (((1 + energy) * (1 + energy)) + (fitness / 2f)) + minSpeed;
		float speed = ((energy + (fitness / 2f)) * 2f) + minSpeed;
		return speed;
	}
	
	public void eat(Food food) {
		this.eating = true;
		this.eatingFood = food;
		eat();
	}
	
	public void eat() {
		this.movementVector.setLocation(0, 0);
		float amountAte = eatingFood.consume(.002f);
		this.food += amountAte;
		if (!(eatingFood instanceof DeadAnimal)) { // Plants give a little water
			this.water += (amountAte / 10f);
		}
		
		// If I finished this food
		if (!EntitySingleton.getInstance().getEntities().contains(eatingFood)) {
			eating = false;
			eatingFood = null;
		}
	}
	
	public void drink(Water water) {
		this.drinking = true;
		this.drinkingWater = water;
		drink();
	}
	
	public void drink() {
		this.movementVector.setLocation(0, 0);
		float amountDrank = drinkingWater.consume(.002f);
		this.water += amountDrank;
		// If I finished this water
		if (!EntitySingleton.getInstance().getEntities().contains(drinkingWater)) {
			drinking = false;
			drinkingWater = null;
		}
	}
	
	private void die() {
		System.out.println("Animal dieing");
		EntitySingleton.getInstance().addEntity(new DeadAnimal(this.id, Color.RED, this.size, 2.0f, 2.0f, this.center));
		AnimalSingleton.getInstance().getAnimals().remove(this);
	}
	
	public void fuck(Animal a) {
		// Only do male so both partners don't create offspring
		if (gender.equals("male")) {
			System.out.println("there's some fuckery afoot");
			timeSinceLastBaby = 0;
			a.setTimeSinceLastBaby(0);
			
			// Location
			float offset = size;
			Random r = new Random();
			if (r.nextFloat() < .5f) {
				offset = -offset;
			}
			Point2D offspringCenter = new Point2D.Double();
			if (r.nextFloat() < .5f) {
				offspringCenter = new Point2D.Double(center.getX() + offset, center.getY() + Math.random() * size);
			}
			else {
				offspringCenter = new Point2D.Double(center.getX() + Math.random() * size, center.getY() + offset);
			}
			if (offspringCenter.getX() < 0) {
				offspringCenter.setLocation(0, offspringCenter.getY());
			}
			if (offspringCenter.getX() > MainUI.WORLD_WIDTH) {
				offspringCenter.setLocation(MainUI.WORLD_WIDTH, offspringCenter.getY());
			}
			if (offspringCenter.getY() < 0) {
				offspringCenter.setLocation(offspringCenter.getX(), 0);
			}
			if (offspringCenter.getY() > MainUI.WORLD_HEIGHT) {
				offspringCenter.setLocation(offspringCenter.getX(), MainUI.WORLD_HEIGHT);
			}

			// Gender 50/50
			String childGender = "male";
			if (Math.random() < .5f) childGender = "female";
			
			// Color 25% father's, 25% mother's, 50% combination
			float red = ((color.getRed() / 255f) + (a.getColor().getRed() / 255f)) / 2f;
			float green = ((color.getGreen() / 255f) + (a.getColor().getGreen() / 255f)) / 2f;
			float blue = ((color.getBlue() / 255f) + (a.getColor().getBlue() / 255f)) / 2f;
			Color childColor = new Color(red, green, blue);
			if (Math.random() < .25f) { // father's color
				childColor = this.color;
			}
			else if (Math.random() < .5f) { // mother's color
				childColor = a.getColor();
			}
			
			// Size
			float childSize = MathUtil.getRandomNumWithinXPercentOfValues(a.getSize(), size, .1f);
			
			// Mass
			float childMass = MathUtil.getRandomNumWithinXPercentOfValues(a.getMass(), mass, .1f);
			
			// Smelling Distance
			float childSmellingDistance = MathUtil.getRandomNumWithinXPercentOfValues(a.getSmellingDistance(), smellingDistance, .1f);
			
			// Vision Distance
			float childVisionDistance = MathUtil.getRandomNumWithinXPercentOfValues(a.getVisionDistance(), visionDistance, .1f);
			
			// Vision FOV
			int childVisionFOV = Math.round(MathUtil.getRandomNumWithinXPercentOfValues(a.getVisionFOV(), visionFOV, .01f));
			
			// Generation 
			int childGeneration = generation + 1;
			if (a.getGeneration() > generation) {
				childGeneration = a.getGeneration() + 1;
			}
			
			Animal p = new Animal(childGeneration, species, childGender, childColor, childSize, childMass, childSmellingDistance, childVisionDistance, childVisionFOV, offspringCenter, null);
			AnimalSingleton.getInstance().addAnimal(p);
		}
	}
	
	public boolean isVerile() {
		if (age >= 1 && age < 5 && health >= .5 && timeSinceLastBaby >= .5) {
			return true;
		}
		return false;
	}

	public Point2D getMovementVector() {
		return movementVector;
	}
	
	public Point2D getAverageMovementVector() {
		try {
			float sumX = 0;
			float sumY = 0;
			for (Point2D p : last10movementVectors) {
				sumX += p.getX();
				sumY += p.getY();
			}
			float averageX = sumX / (float)last10movementVectors.size();
			float averageY = sumY / (float)last10movementVectors.size();
			if (Float.isNaN(averageX)) {
				averageX = 0;
			}
			if (Float.isNaN(averageY)) {
				averageY = 0;
			}
			return new Point2D.Double(averageX, averageY);
		}
		catch (Exception e) {
			return movementVector;
		}
	}

	public void setMovementVector(Point2D movementVector) {
		if (isEating() || isDrinking()) {
			movementVector.setLocation(0, 0);
		}
		
		this.movementVector = movementVector;
		
		if (last10movementVectors.size() >= 10) {
			last10movementVectors.removeLast();
		}
		last10movementVectors.addFirst(movementVector);
	}

	public float getMass() {
		return mass;
	}
	
	public AI getAI() {
		return ai;
	}

	public float getSmellingDistance() {
		return smellingDistance;
	}

	public float getVisionDistance() {
		return visionDistance;
	}

	public int getVisionFOV() {
		return visionFOV;
	}

	public String getSpecies() {
		return species;
	}

	public String getGender() {
		return gender;
	}

	public void setTimeSinceLastBaby(float timeSinceLastBaby) {
		this.timeSinceLastBaby = timeSinceLastBaby;
	}

	public int getGeneration() {
		return generation;
	}

	public float getWater() {
		return water;
	}

	public float getFood() {
		return food;
	}
	
	public boolean isEating() {
		return eating;
	}

	public void setEating(boolean eating) {
		this.eating = eating;
		this.eatingFood = null;
	}

	public boolean isDrinking() {
		return drinking;
	}

	public void setDrinking(boolean drinking) {
		this.drinking = drinking;
		this.drinkingWater = null;
	}

	public XYSeries getFoodSeries() {
		XYSeries s = new XYSeries("Food");
		for (int a = 0; a < foodHistory.size(); a++) {
			s.add(a, foodHistory.get(a));
		}
		return s;
	}

	public XYSeries getWaterSeries() {
		XYSeries s = new XYSeries("Water");
		for (int a = 0; a < waterHistory.size(); a++) {
			s.add(a, waterHistory.get(a));
		}
		return s;
	}

	public XYSeries getHealthSeries() {
		XYSeries s = new XYSeries("Health");
		for (int a = 0; a < healthHistory.size(); a++) {
			s.add(a, healthHistory.get(a));
		}
		return s;
	}

	public XYSeries getEnergySeries() {
		XYSeries s = new XYSeries("Energy");
		for (int a = 0; a < energyHistory.size(); a++) {
			s.add(a, energyHistory.get(a));
		}
		return s;
	}

	public XYSeries getFitnessSeries() {
		XYSeries s = new XYSeries("Fitness");
		for (int a = 0; a < fitnessHistory.size(); a++) {
			s.add(a, fitnessHistory.get(a));
		}
		return s;
	}
	
	public XYSeries getSpeedSeries() {
		XYSeries s = new XYSeries("Speed");
		for (int a = 0; a < speedHistory.size(); a++) {
			s.add(a, speedHistory.get(a));
		}
		return s;
	}
}
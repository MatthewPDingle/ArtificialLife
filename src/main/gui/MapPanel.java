package main.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import main.entities.Entity;
import main.entities.impl.Animal;
import main.entities.impl.DeadAnimal;
import main.entities.impl.Food;
import main.entities.impl.Water;
import main.life.ai.impl.NaivePredator;
import main.life.managers.AnimalSingleton;
import main.life.managers.EntitySingleton;
import main.util.GeometryUtil;
import main.util.TransformUtil;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

public class MapPanel extends JPanel implements ComponentListener, Runnable {
	private AnimalSingleton animalSingleton = AnimalSingleton.getInstance();
	private EntitySingleton entitySingleton = EntitySingleton.getInstance();
	
	private Background background = null;
	private MapPanel thisMapPanel = null;
	private Graphics2D doubleBufferG = null;
	private Image doubleBufferImage = null;
	
	private Dimension screenDimensions = null;
	private Rectangle2D currentWorldView = null;
	private Rectangle2D defaultWorldView = null;
	private Dimension worldDimensions = null;

	private Point currentWorldViewMousePressedPoint = new Point();
	private FPSModule fpsModule = null;
	
	private boolean showVision = false;
	private boolean smoothVision = false;
	private boolean showOlfaction = false;
	private boolean showMovementVector = false;
	private boolean followSelectedAnimal = false;
	
	private boolean running = true;
	private boolean paused = true;
	private long desiredFPS = 120;
    private long desiredDeltaLoop = (1000000000) / desiredFPS;
	
	public MapPanel(FPSModule fpsModule, Background background, int worldWidth, int worldHeight) {
		super();
		this.fpsModule = fpsModule;
		this.background = background;
		this.worldDimensions = new Dimension(worldWidth, worldHeight);
		this.addComponentListener(this);
		this.setFocusable(true);
		addMouseListeners();
		addKeyboardListeners();
		currentWorldView = new Rectangle2D.Double();
		defaultWorldView = new Rectangle2D.Double();
		thisMapPanel = this;
	}

    public void setDefaultWorldView() {
    	screenDimensions = this.getSize();
		int leftBorder = (worldDimensions.width / 2) - (screenDimensions.width / 2);
		int topBorder = (worldDimensions.height / 2) - (screenDimensions.height / 2);
		defaultWorldView.setFrame(leftBorder, topBorder, screenDimensions.getWidth(), screenDimensions.getHeight());
		resetBuffer();
    }
	    
    private void setCurrentWorldView() {
    	screenDimensions = this.getSize();
		int leftBorder = (worldDimensions.width / 2) - (screenDimensions.width / 2);
		int topBorder = (worldDimensions.height / 2) - (screenDimensions.height / 2);
		currentWorldView.setFrame(leftBorder, topBorder, screenDimensions.getWidth(), screenDimensions.getHeight());
    }
    
	public void resetWorldView() {
		currentWorldView = (Rectangle2D)defaultWorldView.clone();
		repaint();
	}
	
    public void addKeyboardListeners() {
    	this.addKeyListener(new KeyListener() {
    		public void keyPressed(KeyEvent ke) {
    			int keyCode = ke.getKeyCode();
    			if (keyCode == KeyEvent.VK_P) { // Pause , Unpause 
    				paused = !paused;
    				System.out.println(paused);
    				JLayeredPane jlp = (JLayeredPane)thisMapPanel.getParent();
    				Component component = jlp.getComponentAt(new Point(0, 0)).getComponentAt(new Point(4, 4));
					JLabel label = (JLabel)component;
					label.setVisible(paused);
    	        } 
    			if (keyCode == KeyEvent.VK_A) { // Previous animal
    				if (animalSingleton.getAnimals().size() > 0) {
	    				JLayeredPane jlp = (JLayeredPane)thisMapPanel.getParent();
	    				Component component = jlp.getComponentAt(new Point(0, 0)).getComponentAt(new Point(4, 64));
	    				JTextArea txtAnimalDetails = (JTextArea)component;
						txtAnimalDetails.setText(animalSingleton.getPrevious().toString());
    				}
    			}
    			if (keyCode == KeyEvent.VK_S) { // Next animal
    				if (animalSingleton.getAnimals().size() > 0) {
	    				JLayeredPane jlp = (JLayeredPane)thisMapPanel.getParent();
	    				Component component = jlp.getComponentAt(new Point(0, 0)).getComponentAt(new Point(4, 64));
	    				JTextArea txtAnimalDetails = (JTextArea)component;
						txtAnimalDetails.setText(animalSingleton.getNext().toString());
    				}
    			}
    		}
    		
    		public void keyReleased(KeyEvent ke) {
    		}
    		
    		public void keyTyped(KeyEvent ke) {	
    		}
    	});
    }

	public void addMouseListeners () {
		this.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved (MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				if (notches > 0) {
					for (int a = 0; a < 10; a++) {
						zoom(2);
						if (a % 2 == 1)
							update(thisMapPanel.getGraphics());
					}
				}
				else {
					for (int a = 0; a < 10; a++) {
						zoom(-2);
						if (a % 2 == 1)
							update(thisMapPanel.getGraphics());
					}
				}
				repaint();
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent me) {
			}
			
			public void mouseDragged(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me)) {
					double deltaX = currentWorldViewMousePressedPoint.getX() - me.getX();
					double deltaY = currentWorldViewMousePressedPoint.getY() - me.getY();
					
					// Convert the screen delta vector to a world delta vector
					Point2D scaleVector = TransformUtil.screenToWorldScaleOnly(new Point2D.Double(deltaX, deltaY), screenDimensions, currentWorldView);
					deltaX = scaleVector.getX();
					deltaY = scaleVector.getY();
	
					currentWorldView.setFrame(currentWorldView.getX() + deltaX, currentWorldView.getY() + deltaY, currentWorldView.getWidth(), currentWorldView.getHeight());
					
					update(thisMapPanel.getGraphics());
					currentWorldViewMousePressedPoint = me.getPoint();
					thisMapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					repaint(); // Needed to get labels to update
				}
			}
		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				String gender = "male";
				if (Math.random() < .5f) gender = "female";
				
				float eatUntil = .6f + (float)Math.random() * .4f; // .6 - 1
				float drinkUntil = .6f + (float)Math.random() * .4f;
				
				float hungryLevel = .05f + (float)Math.random() * .35f; // .05 - .4
				float thirstyLevel = .05f + (float)Math.random() * .35f;
				
				float size = 5f + (float)Math.random() * 5f; // 5 - 10
				float mass = size * 1.5f;
				float smellingDistance = 60f + (float)Math.random() * 30f; // 60-90
				float visionDistance = 150f + (float)Math.random() * 100f; // 150-250
				float visionFOV = 100 + (float)Math.random() * 80f; // 100-180
				
				if (me.getButton() == MouseEvent.BUTTON1) {
					// Make color
					float r = .5f + ((float)Math.random() / 2f);
					float g = .5f + ((float)Math.random() / 2f);
					float b = .5f + ((float)Math.random() / 2f);
					int random = (int)Math.floor(Math.random() * 6d);
					if (random == 0) {
						r = 0;
					}
					if (random == 1) {
						g = 0;
					}
					if (random == 2) {
						b = 0;
					}
					if (random == 3) {
						r = 0;
						g = 0;
					}
					if (random == 4) {
						r = 0;
						b = 0;
					}
					if (random == 5) {
						g = 0;
						b = 0;
					}
			
					Color c = new Color(r, g, b);

					Point2D wCenter = TransformUtil.screenToWorld(me.getPoint(), thisMapPanel.getSize(), currentWorldView);
					Animal p = new Animal(1, "Gazelle", gender, c, size, mass, smellingDistance, visionDistance, (int)visionFOV, eatUntil, drinkUntil, hungryLevel, thirstyLevel, wCenter, null);
					animalSingleton.addAnimal(p);
				}
				if (me.getButton() == MouseEvent.BUTTON2) {
					Point2D wCenter = TransformUtil.screenToWorld(me.getPoint(), thisMapPanel.getSize(), currentWorldView);
					Animal p = new Animal(1, "Cheetah", "male", Color.ORANGE, size, mass, smellingDistance, visionDistance, (int)visionFOV, eatUntil, drinkUntil, hungryLevel, thirstyLevel, wCenter, new NaivePredator());
					animalSingleton.addAnimal(p);
				}
				else if (me.getButton() == MouseEvent.BUTTON3) {
					currentWorldViewMousePressedPoint = me.getPoint();
				}
			}
			public void mouseReleased(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON3) {
					thisMapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
	}

	public void zoom(double amount) {
		// Update the currentWorldView
		amount = 1 + (amount / 100);
		Point2D currentWorldViewSize = new Point2D.Double(currentWorldView.getWidth(), currentWorldView.getHeight());
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(amount, amount);
		Point2D newWorldViewSize = scaleTransform.transform(currentWorldViewSize, null);
		double xOffset = (currentWorldView.getCenterX()) - (worldDimensions.width / 2);
		double yOffset = (currentWorldView.getCenterY()) - (worldDimensions.height / 2);
		double leftBorder = (worldDimensions.width / 2) + xOffset - (newWorldViewSize.getX() / 2);
		double topBorder = (worldDimensions.height / 2) + yOffset - (newWorldViewSize.getY() / 2);
		currentWorldView.setFrame(leftBorder, topBorder, newWorldViewSize.getX(), newWorldViewSize.getY());
	}
	
    private void resetBuffer() {
    	if (doubleBufferG != null) {
    		doubleBufferG.dispose();
    		doubleBufferG = null;
    	}
    	if (doubleBufferImage != null) {
    		doubleBufferImage.flush();
    		doubleBufferImage = null;
    	}
    	System.gc();
    	doubleBufferImage = new BufferedImage(screenDimensions.width, screenDimensions.height, BufferedImage.TYPE_INT_ARGB);
    	doubleBufferG = (Graphics2D)doubleBufferImage.getGraphics();
    	
    	// Set Background
    	Composite currComposite = doubleBufferG.getComposite();
		Color currColor = doubleBufferG.getColor();
		doubleBufferG.fillRect(0, 0, worldDimensions.width, worldDimensions.height);
		
		// Set composite back to what it was originally
		doubleBufferG.setComposite(currComposite);
					
		// Set color back to what it was originally
		doubleBufferG.setColor(currColor);
    }
	
	public void paintBuffer(Graphics2D g) {
		// Update FPS
		fpsModule.incrementFrameCounter();
		
		// Turn on Antialiasing
		g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		
		// Draw Background
		Composite currComposite = g.getComposite();
		Color currColor = g.getColor();
		
		// Set composite back to what it was originally
		g.setComposite(currComposite);
		// Set color back to what it was originally
		g.setColor(currColor);

		// Draw Background
		Point2D centerLocation = new Point2D.Double((MainUI.WORLD_WIDTH / 2), (MainUI.WORLD_WIDTH / 2));
		Point2D backgroundLocation = TransformUtil.worldToScreen(centerLocation, this.getSize(), currentWorldView);
		Point2D backgroundSize = TransformUtil.worldToScreenScaleOnly(new Point2D.Double(MainUI.WORLD_WIDTH, MainUI.WORLD_HEIGHT), this.getSize(), currentWorldView);
		Point2D backgroundUpperLeft = TransformUtil.worldToScreen(new Point2D.Double(0,0), this.getSize(), currentWorldView);
		g.drawImage(background.getImage(),
				(int)(backgroundLocation.getX() - (backgroundSize.getX() / 2)),
				(int)(backgroundLocation.getY() - (backgroundSize.getY() / 2)),
				(int)(backgroundLocation.getX() - backgroundUpperLeft.getX()) * 2,
				(int)(backgroundLocation.getY() - backgroundUpperLeft.getY()) * 2,
				this);
		
		// Draw Entities (Food, Water, Dead Animals)
		ArrayList<Entity> existingEntities = new ArrayList<Entity>();
		existingEntities.addAll(entitySingleton.getEntities());
		for (Entity entity:existingEntities) {
			Point2D screenLocation = TransformUtil.worldToScreen(entity.getCenter(), this.getSize(), currentWorldView);
			Point2D dimensions = TransformUtil.worldToScreenScaleOnly(new Point2D.Double(entity.getSize(), entity.getSize()), this.getSize(), currentWorldView);
			
			Color color = entity.getColor();
			Color innerColor = new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, entity.getPercentage());
			
			g.setColor(innerColor);
			g.fillRect((int)(screenLocation.getX() - (dimensions.getX() / 2)),
					(int)(screenLocation.getY() - (dimensions.getY() / 2)),
					(int)(dimensions.getX()), 
					(int)(dimensions.getY()));
			g.setColor(color);
			g.drawRect((int)(screenLocation.getX() - (dimensions.getX() / 2)),
					(int)(screenLocation.getY() - (dimensions.getY() / 2)),
					(int)(dimensions.getX()), 
					(int)(dimensions.getY()));
		}

		// Draw Animals
		ArrayList<Animal> existingAnimals = new ArrayList<Animal>();
		existingAnimals.addAll(animalSingleton.getAnimals());
		for (Animal animal:existingAnimals) {
			Point2D screenLocation = TransformUtil.worldToScreen(animal.getCenter(), this.getSize(), currentWorldView);
			Point2D dimensions = TransformUtil.worldToScreenScaleOnly(new Point2D.Double(animal.getSize(), animal.getSize()), this.getSize(), currentWorldView);
			Point2D vector = TransformUtil.worldToScreenScaleOnly(animal.getAverageMovementVector(), this.getSize(), currentWorldView);
	
			// Draw the animal's sense of smell
			if (showOlfaction) {
				Color smellColor = new Color(.8f, .8f, 1f, .2f);
				g.setColor(smellColor);
				Point2D smellDimensions = TransformUtil.worldToScreenScaleOnly(new Point2D.Double(animal.getSmellingDistance(), animal.getSmellingDistance()), this.getSize(), currentWorldView);
				g.fillOval((int)(screenLocation.getX() - (smellDimensions.getX())),
						(int)(screenLocation.getY() - (smellDimensions.getY())),
						(int)(smellDimensions.getX() * 2), 
						(int)(smellDimensions.getY() * 2));
			}
			
			// Draw the animal's FOV
			if (showVision) {
				if (smoothVision) {
					for (int a = 0; a <= 20; a++) {
						Color fovColor = new Color(1f, 1f, 1f, .01f);
						g.setColor(fovColor);
						Point2D fovDimensions = TransformUtil.worldToScreenScaleOnly(new Point2D.Double(animal.getVisionDistance(), animal.getVisionDistance()), this.getSize(), currentWorldView);
						float degreeHeading = GeometryUtil.getAngleInDegrees(vector);
						int fov = animal.getVisionFOV();
						int degreeHeadingLeft = (int)degreeHeading - (fov / 2) + a;
						g.fillArc((int)(screenLocation.getX() - (fovDimensions.getX())),
								(int)(screenLocation.getY() - (fovDimensions.getY())),
								(int)(fovDimensions.getX() * 2), 
								(int)(fovDimensions.getY() * 2),
								(int)degreeHeadingLeft, fov - (2 * a));
					}
				}
				else {
					Color fovColor = new Color(1f, 1f, 1f, .2f);
					g.setColor(fovColor);
					Point2D fovDimensions = TransformUtil.worldToScreenScaleOnly(new Point2D.Double(animal.getVisionDistance(), animal.getVisionDistance()), this.getSize(), currentWorldView);
					float degreeHeading = GeometryUtil.getAngleInDegrees(vector);
					int fov = animal.getVisionFOV();
					int degreeHeadingLeft = (int)degreeHeading - (fov / 2);
					g.fillArc((int)(screenLocation.getX() - (fovDimensions.getX())),
							(int)(screenLocation.getY() - (fovDimensions.getY())),
							(int)(fovDimensions.getX() * 2), 
							(int)(fovDimensions.getY() * 2),
							(int)degreeHeadingLeft, fov);
				}
			}
			
			// Draw focus ring if applicable
			if (animal.getID() == animalSingleton.getCurrent().getID()) {
				g.setColor(Color.YELLOW);
				g.fillOval((int)(screenLocation.getX() - (dimensions.getX() / 2) - 2),
						(int)(screenLocation.getY() - (dimensions.getY() / 2) - 2),
						(int)(dimensions.getX() + 4), 
						(int)(dimensions.getY() + 4));
			}
			
			// Draw Animal
			g.setColor(animal.getColor());
			g.fillOval((int)(screenLocation.getX() - (dimensions.getX() / 2)),
					(int)(screenLocation.getY() - (dimensions.getY() / 2)),
					(int)(dimensions.getX()), 
					(int)(dimensions.getY()));
			
			// Draw movement vector
			if (showMovementVector) {
				g.setColor(Color.WHITE);
				g.drawLine((int)screenLocation.getX(), 
						(int)screenLocation.getY(), 
						(int)screenLocation.getX() + (int)vector.getX() * 5, 
						(int)screenLocation.getY() + (int)vector.getY() * 5);
			}
		}
	}
	
	public void paint(Graphics g) {
		// Reset buffer if needed
		if (doubleBufferImage == null || doubleBufferG == null) {
			resetBuffer();
		}

		// Clear the buffer
		doubleBufferG.clearRect(0, 0, screenDimensions.width, screenDimensions.height);
		
		// Paint to the buffer
		paintBuffer(doubleBufferG);

		// Draw the buffered image on the MapPanel
		g.drawImage(doubleBufferImage, 0, 0, this);
	}
	
	public void update(Graphics g) {
		paint(g);
	}

	public void run() {
		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;
		
		while (running) {
			beginLoopTime = System.nanoTime();

			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			updateScene((int)((currentUpdateTime - lastUpdateTime) / (1000000)));
			
			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;
	        
	        if (deltaLoop > desiredDeltaLoop) {
	            // Do nothing. We are already late.
	        }
	        else {
	            try {
	            	double sleep = (double)((desiredDeltaLoop - deltaLoop) / (1000000d));
	                Thread.sleep((long)sleep);
	            }
	            catch(InterruptedException e) {}
	        }
		}
	}
	
	protected void updateScene(int deltaTime) {
		try {
			if (!paused) {
				// Have the animals think about what to do and then move
				synchronized(animalSingleton.getAnimals()) {
					ArrayList<Animal> existingAnimals = new ArrayList<Animal>();
					existingAnimals.addAll(animalSingleton.getAnimals());
					for (Animal animal : existingAnimals) {
						animal.getAI().think();
						animal.move();
					}
				}
				
				// Make new plants (food)
				Random r = new Random();
				if (r.nextFloat() < .00013) {
					System.out.println("adding reproduced food");
					synchronized(entitySingleton.getEntities()) {
						ArrayList<Food> newFood = new ArrayList<Food>();
						for (Entity entity : entitySingleton.getEntities()) {
							if (entity instanceof Food && !(entity instanceof DeadAnimal)) {
								Food food = (Food)entity;
								if (r.nextFloat() < .1f) { // 10% chance this food reproduces
									float offset = food.getSize();
									if (r.nextFloat() < .5f) {
										offset = -offset;
									}
									Point2D center;
									if (r.nextFloat() < .5f) {
										center = new Point2D.Double(food.getCenter().getX() + offset, food.getCenter().getY() + Math.random() * food.getSize());
									}
									else {
										center = new Point2D.Double(food.getCenter().getX() + Math.random() * food.getSize(), food.getCenter().getY() + offset);
									}
									double x = center.getX();
									double y = center.getY();
									if (x + 5 > MainUI.WORLD_WIDTH) {
										center.setLocation(x - 5, y);
									}
									if (x - 5 < 0) {
										center.setLocation(x + 5, y);
									}
									if (y + 5 > MainUI.WORLD_HEIGHT) {
										center.setLocation(x, y - 5);
									}
									if (y - 5 < 0) {
										center.setLocation(x, y + 5);
									}
									
									Food f = new Food(1, Color.GREEN, "bush", 1f, 1f, center);
									newFood.add(f);
								}
							}
						}
						for (Food food : newFood) {
							entitySingleton.addEntity(food);
						}
					}
				}
				if (r.nextFloat() < .0015) {
					System.out.println("adding random food");
					double x = 5 + (Math.random() * ((double)MainUI.WORLD_WIDTH - 10));
					double y = 5 + (Math.random() * ((double)MainUI.WORLD_HEIGHT - 10));
					Point2D center = new Point2D.Double(x, y);
					Food f = new Food(1, Color.GREEN, "bush", 1f, 1f, center);
					entitySingleton.addEntity(f);
				}
				
				// Add new water
				if (r.nextFloat() < .0001) {
					System.out.println("adding water");
					double x = 25 + (Math.random() * ((double)MainUI.WORLD_WIDTH - 50));
					double y = 25 + (Math.random() * ((double)MainUI.WORLD_HEIGHT - 50));
					Point2D center = new Point2D.Double(x, y);
					Water w = new Water(0, Color.BLUE, 10f, 10f, center);
					entitySingleton.addEntity(w);
				}
				
				// Update animal details text area
				if (animalSingleton.getAnimals().size() > 0) {
					JLayeredPane jlp = (JLayeredPane)thisMapPanel.getParent();
					Component component = jlp.getComponentAt(new Point(0, 0)).getComponentAt(new Point(4, 64));
					final JTextArea txtAnimalDetails = (JTextArea)component;
					txtAnimalDetails.setText(animalSingleton.getCurrent().toString());
					
					int numAnimals = animalSingleton.getAnimals().size();
					
					Component component2 = jlp.getComponentAt(new Point(0, 0)).getComponentAt(new Point(4, 44));
					JLabel lblAnimalDetails = (JLabel)component2;
					lblAnimalDetails.setText("Animal Details (" + numAnimals + " total) [press s]");
				}
				
				// Update animal details chart
				if (animalSingleton.getAnimals().size() > 0) {
					JLayeredPane jlp = (JLayeredPane)thisMapPanel.getParent();
					Component component = jlp.getComponentAt(new Point(0, 0)).getComponentAt(new Point(4, 475));
					ChartPanel chartPanel = (ChartPanel)component;
	
					XYSeriesCollection dataset = new XYSeriesCollection();
					dataset.addSeries(animalSingleton.getCurrent().getFoodSeries());
					dataset.addSeries(animalSingleton.getCurrent().getWaterSeries());
					dataset.addSeries(animalSingleton.getCurrent().getHealthSeries());
					dataset.addSeries(animalSingleton.getCurrent().getEnergySeries());
					dataset.addSeries(animalSingleton.getCurrent().getFitnessSeries());
					dataset.addSeries(animalSingleton.getCurrent().getSpeedSeries());
					
					JFreeChart chart = chartPanel.getChart();
					XYPlot plot = (XYPlot)chart.getPlot();
					plot.setDataset(dataset);
					
					chartPanel.revalidate();
					chartPanel.repaint();
				}
				
				// Follow the selected animal, if selected
				if (followSelectedAnimal) {
					Animal selectedAnimal = animalSingleton.getCurrent();
					if (selectedAnimal != null) {
						Point2D center = selectedAnimal.getCenter();
						currentWorldView.setFrame(center.getX() - (currentWorldView.getWidth() / 2), center.getY() - (currentWorldView.getHeight() / 2), currentWorldView.getWidth(), currentWorldView.getHeight());
					}
				}
				
				// Refresh map panel
				this.repaint();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void componentResized(ComponentEvent e) {
    	setCurrentWorldView();
		resetBuffer();
		repaint();
    }
    
    public void componentHidden(ComponentEvent e) {}

    public void componentMoved(ComponentEvent e) {}
   
    public void componentShown(ComponentEvent e) {}

	public void setShowVision(boolean showVision) {
		this.showVision = showVision;
	}

	public void setSmoothVision(boolean smoothVision) {
		this.smoothVision = smoothVision;
	}

	public void setShowOlfaction(boolean showOlfaction) {
		this.showOlfaction = showOlfaction;
	}

	public void setShowMovementVector(boolean showMovementVector) {
		this.showMovementVector = showMovementVector;
	}
	
	public void setFollowSelectedAnimal(boolean followSelectedAnimal) {
		this.followSelectedAnimal = followSelectedAnimal;
	}
}
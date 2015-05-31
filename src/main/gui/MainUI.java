package main.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import main.entities.impl.Food;
import main.entities.impl.Water;
import main.life.managers.EntitySingleton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import constants.Constants;

public class MainUI {

	public static final int WINDOW_WIDTH = 1420;
	public static final int WINDOW_HEIGHT = 1220;
	public static final int SETTINGS_WIDTH = 200;
	public static final int SETTINGS_HEIGHT = WINDOW_HEIGHT;
	public static final int MAP_WIDTH = WINDOW_WIDTH - SETTINGS_WIDTH;
	public static final int MAP_HEIGHT = WINDOW_HEIGHT;
	public static final int WORLD_WIDTH = 1200;
	public static final int WORLD_HEIGHT = 1200;
	
	private JFrame jFrame = null;
	
	private EntitySingleton entitySingleton = EntitySingleton.getInstance();

	public static void main(String[] args) {
		new MainUI();
	}
	
	public MainUI() {
		try {
			Background background = new Background("800x800bg.png");		
			jFrame = new JFrame();
			
			// Add FPS label
			JLabel lblFPS = new JLabel();
			lblFPS.setName("lblFPS");
			lblFPS.setDoubleBuffered(true);
			lblFPS.setText("FPS");
			lblFPS.setSize(60, 15);
			lblFPS.setLocation(4, 24);
			
			// Add the FPS Module
			FPSModule fpsModule = new FPSModule();
			fpsModule.setFPSLabel(lblFPS);
			fpsModule.start();
			
			// Add the MapPanel
			final MapPanel mapPanel = new MapPanel(fpsModule, background, WORLD_WIDTH, WORLD_HEIGHT);
			mapPanel.setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
			mapPanel.setSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
			mapPanel.setBounds(new Rectangle(SETTINGS_WIDTH, 0, MAP_WIDTH, MAP_HEIGHT));
			mapPanel.setBackground(Color.WHITE);
			mapPanel.setDoubleBuffered(true);
			mapPanel.setLayout(null);
			mapPanel.setVisible(true);
			mapPanel.setDefaultWorldView();
			
			// Add all the extra status labels
			final JLayeredPane jlp = new JLayeredPane();
			jlp.setBorder(null);
			jlp.setDoubleBuffered(true);
			jlp.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
			
			// Add pause label
			JLabel lblPause = new JLabel();
			lblPause.setName("lblPause");
			lblPause.setDoubleBuffered(true);
			lblPause.setText("PAUSED");
			lblPause.setSize(50, 15);
			lblPause.setLocation(4, 4);
		
			// Add FPS label
			JLabel lblAnimalDetails = new JLabel();
			lblAnimalDetails.setName("lblAnimalDetails");
			lblAnimalDetails.setDoubleBuffered(true);
			lblAnimalDetails.setText("Animals [a/s keys to switch]");
			lblAnimalDetails.setSize(192, 15);
			lblAnimalDetails.setLocation(4, 44);
			
			// Add Animal Details Text Area
			JTextArea txtAnimalDetails = new JTextArea();
			txtAnimalDetails.setName("txtAnimalDetails");
			txtAnimalDetails.setDoubleBuffered(false);
			txtAnimalDetails.setSize(192, 404);
			txtAnimalDetails.setLocation(4, 64);
			txtAnimalDetails.setEditable(false);
			txtAnimalDetails.setFocusable(false);
			Border border = BorderFactory.createLineBorder(Color.GRAY);
			txtAnimalDetails.setBorder(border);

			// Add Animal Details Chart
			XYDataset dataset = new XYSeriesCollection();
			JFreeChart chart = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, false, false, false);
			chart.setBackgroundPaint(Color.WHITE);
			XYPlot plot = (XYPlot)chart.getPlot();
			plot.getDomainAxis().setInverted(true);
			plot.getDomainAxis().setRange(0, 1000);
			plot.getDomainAxis().setVisible(false);
			plot.getRangeAxis().setRange(-.01, 1.01);
			plot.setRangeGridlinePaint(new Color(230,230,230));
			plot.setRangeGridlineStroke(new BasicStroke(1f));
			plot.setBackgroundPaint(Color.WHITE);
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			renderer.setStroke(new BasicStroke(1f));
			renderer.setSeriesPaint(0, Constants.GREEN);
			renderer.setSeriesPaint(1, Constants.BLUE);
			renderer.setSeriesPaint(2, Constants.PURPLE);
			renderer.setSeriesPaint(3, Constants.ORANGE);
			renderer.setSeriesPaint(4, Constants.RED);
			renderer.setSeriesPaint(5, Constants.BROWN);
			renderer.setSeriesShapesVisible(0, false);
			renderer.setSeriesShapesVisible(1, false);
			renderer.setSeriesShapesVisible(2, false);
			renderer.setSeriesShapesVisible(3, false);
			renderer.setSeriesShapesVisible(4, false);
			renderer.setSeriesShapesVisible(5, false);
			plot.setRenderer(renderer);
			ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setBorder(border);
			chartPanel.setSize(192, 200);
			chartPanel.setLocation(4, 475);
			
			// Add Animals Details Legend
			JLabel lblHealth = new JLabel("Health");
			lblHealth.setSize(40, 15);
			lblHealth.setLocation(4, 680);
			lblHealth.setForeground(Constants.PURPLE);
			
			JLabel lblFood = new JLabel("Food");
			lblFood.setSize(40, 15);
			lblFood.setLocation(44, 680);
			lblFood.setForeground(Constants.GREEN);
			
			JLabel lblWater = new JLabel("Water");
			lblWater.setSize(40, 15);
			lblWater.setLocation(75, 680);
			lblWater.setForeground(Constants.BLUE);
			
			JLabel lblEnergy = new JLabel("Energy");
			lblEnergy.setSize(40, 15);
			lblEnergy.setLocation(114, 680);
			lblEnergy.setForeground(Constants.ORANGE);
			
			JLabel lblFitness = new JLabel("Fitness");
			lblFitness.setSize(44, 15);
			lblFitness.setLocation(156, 680);
			lblFitness.setForeground(Constants.RED);
			
			final JCheckBox chkOlfaction = new JCheckBox("Show Olfaction");
			chkOlfaction.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mapPanel.setShowOlfaction(chkOlfaction.isSelected());
				}
			});
			chkOlfaction.setSize(160, 15);
			chkOlfaction.setLocation(4, 700);
			chkOlfaction.setFocusable(false);
			
			final JCheckBox chkVision = new JCheckBox("Show Vision");
			chkVision.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mapPanel.setShowVision(chkVision.isSelected());
				}
			});
			chkVision.setSize(160, 15);
			chkVision.setLocation(4, 720);
			chkVision.setFocusable(false);
			
			final JCheckBox chkSmoothVision = new JCheckBox("Smooth Vision");
			chkSmoothVision.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mapPanel.setSmoothVision(chkSmoothVision.isSelected());
				}
			});
			chkSmoothVision.setSize(160, 15);
			chkSmoothVision.setLocation(4, 740);
			chkSmoothVision.setFocusable(false);
			
			final JCheckBox chkShowMovementVector = new JCheckBox("Show Movement Vector");
			chkShowMovementVector.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mapPanel.setShowMovementVector(chkShowMovementVector.isSelected());
				}
			});
			chkShowMovementVector.setSize(160, 15);
			chkShowMovementVector.setLocation(4, 760);
			chkShowMovementVector.setFocusable(false);
			
			final JCheckBox chkFollowSelectedAnimal = new JCheckBox("Follow Selected Animal");
			chkFollowSelectedAnimal.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mapPanel.setFollowSelectedAnimal(chkFollowSelectedAnimal.isSelected());
				}
			});
			chkFollowSelectedAnimal.setSize(160, 15);
			chkFollowSelectedAnimal.setLocation(4, 780);
			chkFollowSelectedAnimal.setFocusable(false);
			
			// Add the SettingsPanel
			JPanel settingsPanel = new JPanel();
			settingsPanel.setLayout(null);
			settingsPanel.setBackground(SystemColor.control);
			settingsPanel.setPreferredSize(new Dimension(SETTINGS_WIDTH, SETTINGS_HEIGHT));
			settingsPanel.setBounds(new Rectangle(0, 0, SETTINGS_WIDTH, SETTINGS_HEIGHT));
			settingsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			settingsPanel.add(lblPause);
			settingsPanel.add(lblFPS);
			settingsPanel.add(lblAnimalDetails);
			settingsPanel.add(txtAnimalDetails);
			settingsPanel.add(chartPanel);
			settingsPanel.add(lblHealth);
			settingsPanel.add(lblFood);
			settingsPanel.add(lblWater);
			settingsPanel.add(lblEnergy);
			settingsPanel.add(lblFitness);
			settingsPanel.add(chkOlfaction);
			settingsPanel.add(chkVision);
			settingsPanel.add(chkSmoothVision);
			settingsPanel.add(chkShowMovementVector);
			settingsPanel.add(chkFollowSelectedAnimal);
			
			// Add some food to the map to start
			for (int a = 0; a < 50; a++) {
				double x = 5 + (Math.random() * ((double)WORLD_WIDTH - 10));
				double y = 5 + (Math.random() * ((double)WORLD_HEIGHT - 10));
				Point2D center = new Point2D.Double(x, y);
				Food f = new Food(a, Color.GREEN, "bush", 1f, 1f, center);
				entitySingleton.addEntity(f);
			}
			
			// Add some water to the map to start
			for (int a = 0; a < 8; a++) {
				double x = 25 + (Math.random() * ((double)WORLD_WIDTH - 50));
				double y = 25 + (Math.random() * ((double)WORLD_HEIGHT - 50));
				Point2D center = new Point2D.Double(x, y);
				Water w = new Water(a, Color.BLUE, 10f, 10f, center);
				entitySingleton.addEntity(w);
			}
	
			// Add crap to the JLayeredPane
			jlp.add(settingsPanel);
			jlp.add(mapPanel);
			
			jlp.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					Component[] components = jlp.getComponents();
					for (int a = 0; a < components.length; a++) {
						if (components[a] instanceof MapPanel) {
							MapPanel mp = (MapPanel)components[a];
							mp.setSize(new Dimension((int)(jlp.getSize().getWidth() - SETTINGS_WIDTH), (int)(jlp.getSize().getHeight())));
							break;
						}
					}
				}
			});
			
			// Show it
			jFrame.add(jlp);
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.pack();
			jFrame.setResizable(true);
			jFrame.setVisible(true);
			
			// Start it
			new Thread(mapPanel).start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
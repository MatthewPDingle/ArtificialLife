package main.gui;

import java.util.LinkedList;
import javax.swing.JLabel;

public class FPSModule extends Thread {

	private static final int REFRESH_RATE = 100;
	private static final int RESET_RATE = 1000;
	private LinkedList<Integer> frameCounterHistory = new LinkedList<Integer>();
	private int frameCounter = 0;
	private JLabel fpsLabel = null;
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(REFRESH_RATE);
				int totalFramesInHistory = 0;
				for (Integer frameCounter:frameCounterHistory) {
					totalFramesInHistory += frameCounter;
				}
				fpsLabel.setText(new Double(totalFramesInHistory * 1000d / (double)RESET_RATE).toString() + " FPS");
				
				frameCounterHistory.addFirst(frameCounter);
				if (frameCounterHistory.size() > 10) {
					frameCounterHistory.removeLast();
				}
				frameCounter = 0;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
	
	public void incrementFrameCounter() {
		frameCounter++;
	}
	
	public void setFPSLabel(JLabel fpsLabel) {
		this.fpsLabel = fpsLabel;
	}
}
package main.gui;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Background {

	BufferedImage image = null;
	
	public Background(String name) {
		try {
			image = ImageIO.read(new File("resources/backgrounds/" + name));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setActiveImage(BufferedImage image) {
		this.image = image;
	}

}
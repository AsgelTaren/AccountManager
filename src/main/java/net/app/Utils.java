package net.app;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Utils {

	public static BufferedImage loadIMG(InputStream stream) {
		try {
			return ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage loadIMGInternal(String path) {
		return loadIMG(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
	}

	public static BufferedImage loadIMG(File f) {
		try {
			return ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void write(String data, File f) {
		try {
			BufferedWriter wr = new BufferedWriter(new FileWriter(f));
			wr.write(data);
			wr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage resize(BufferedImage img, int width, int height) {
		BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		res.getGraphics().drawImage(img, 0, 0, width, height, null);
		return res;
	}
}

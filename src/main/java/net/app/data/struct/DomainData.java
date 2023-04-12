package net.app.data.struct;

import java.io.InputStream;

import javax.swing.ImageIcon;

import net.app.Utils;

public enum DomainData {

	NAME("name", "Nom"), URL("url", "URL"), LOGIN_URL("login", "URL de login");

	private ImageIcon icon;
	private String name, locname;

	private DomainData(String locname, String name) {
		this.locname = locname;
		this.name = name;
	}

	public static void loadIcons() {
		for (DomainData data : values()) {

			try {
				InputStream stream =	DomainData.class
						.getResourceAsStream("res/domains/" +data.locname + ".png");
				if (stream == null) {
					System.out.println("Unable to load icon for " + data);
					continue;
				}
				data.icon = new ImageIcon(Utils.resize(Utils.loadIMG(stream), 32, 32));
			} catch (Exception e) {
				System.out.println("Unable to load icon for " + data);
			}
		}

	}

	public ImageIcon getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

}

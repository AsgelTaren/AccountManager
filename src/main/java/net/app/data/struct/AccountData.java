package net.app.data.struct;

import java.io.InputStream;

import javax.swing.ImageIcon;

import net.app.Utils;

public enum AccountData {

	NAME("name", "Nom"), USERNAME("username", "Nom d'utilisateur"), EMAIL("email", "E-Mail"),
	PASSWORD("password", "Mot de Passe"), BIRTH("birth", "Date de Naissance"), PHONE("phone", "Téléphone"),
	EXTRA("extra", "Extra");

	private ImageIcon icon;
	private String name, locname;

	private AccountData(String locname, String name) {
		this.locname = locname;
		this.name = name;
	}

	public static void loadIcons() {
		for (AccountData data : values()) {

			try {
				InputStream stream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("res/accounts/" + data.locname + ".png");
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

	@Override
	public String toString() {
		return name;
	}

}

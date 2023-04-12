package net.app;

import com.formdev.flatlaf.FlatDarkLaf;

public class Main {

	public static void main(String[] args) throws Exception {
		FlatDarkLaf.setup();
		App app = new App();
		app.init();
	}

}
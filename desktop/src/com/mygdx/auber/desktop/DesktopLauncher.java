package com.mygdx.auber.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import auber.com.mygdx.Auber;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 144;
		config.width = 1920;
		config.height = 1080;
		new LwjglApplication(new Auber(), config);
	}
}

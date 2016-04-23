/**
 * URL for executable: https://drive.google.com/file/d/0B4to7QAfaHDIeEE0TG4tR0dyRk0/view?usp=sharing
 */

package com.superduckinvaders.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.superduckinvaders.game.DuckGame;

/**
 * Desktop launcher for Super Duck Invaders.
 */
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.resizable = true;
		config.title = "The Empire Strikes Quack";
		new LwjglApplication(new DuckGame(), config);
	}
}
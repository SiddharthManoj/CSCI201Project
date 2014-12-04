/*
 * Game.java
 * James Zhang
 * December 10 2011
 * Starts the game. Also contains static variables and methods that are related
 * to the game.
 */

package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import framework.GameWindow;

public final class Game {
	// size variables
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	// title of game
	public static String title = "The Finance Detective";

	// fonts and their metrics that are used by the game. a font metric takes
	// some time to create, so they should be initialized once and then reused
	public static Font font12;
	public static Font font16;
	public static Font font24;
	public static Font font36;
	public static Font font48;
	public static FontMetrics metrics12;
	public static FontMetrics metrics16;
	public static FontMetrics metrics24;
	public static FontMetrics metrics36;

	// main method that starts the game
	public static void main(String[] args) {
		// load everything
		load();

		// create the window
		GameWindow game = new GameWindow(title, 60);
		game.setBackground(new Color(128, 128, 128));

		// icon
		URL url = Game.class.getResource("/resources/icon.png");
		Image icon = new ImageIcon(url).getImage();
		game.setIconImage(icon);

		// add the menu screen
		MenuScreen menu = new MenuScreen(game);
		game.addScreen(menu);

		// start the game
		game.start(32, 32, 800, 600);

		// load the other screens
		menu.load();
	}

	// loads the static variables in this class
	private static void load() {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();

		// load the fonts and their metrics
		font12 = new Font("Consolas", Font.PLAIN, 12);
		metrics12 = g.getFontMetrics(font12);

		font16 = new Font("Consolas", Font.PLAIN, 16);
		metrics16 = g.getFontMetrics(font16);

		font24 = new Font("Consolas", Font.PLAIN, 24);
		metrics24 = g.getFontMetrics(font24);

		font36 = new Font("Consolas", Font.PLAIN, 36);
		metrics36 = g.getFontMetrics(font36);

		font48 = new Font("Consolas", Font.PLAIN, 48);

		// load music
		MusicPlayer.load();
	}

	// helper methods for other classes
	// paints a box like the menu box (will paint a border around)
	public static void paintBox(Graphics g, int x, int y, int w, int h) {
		int border = 5;
		
		// calculate the actual top, left, width, and height (with border)
		int left = x - border, top = y - border;
		int width = w + 2 * border, height = h + 2 * border;

		// background
		g.setColor(new Color(128, 128, 144));
		g.fillRect(x + 1, y + 1, w - 1, h - 1);

		// fill (top bottom left right)
		g.setColor(new Color(112, 112, 160));
		g.fillRect(left, top, width, border);
		g.fillRect(left, y + h, width, border);
		g.fillRect(left, y, border, h);
		g.fillRect(x + w, y, border, h);

		// outline
		g.setColor(new Color(64, 64, 80));
		g.drawRect(x, y, w, h);
		g.drawRect(left, top, width, height);

		// glow
		g.setColor(new Color(255, 255, 255, 48));
		g.drawRect(x - 1, y - 1, w + 2, h + 2);
		g.drawRect(left + 1, top + 1, width - 2, height - 2);
	}

	// loads an image from the resources. if it is not found, a 32x32 bitmap
	// is returned with the name painted on it
	public static BufferedImage loadImage(String name) {
		BufferedImage result;

		try {
			result = ImageIO.read(Game.class.getResource("/resources/" + name));
		} catch (Exception e) {
			result = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			
			Graphics g = result.getGraphics();
			g.setColor(Color.black);
			g.setFont(Game.font12);
			g.drawString(name, 4, 8);
		}

		return result;
	}
	
	// opens a stream to a resource
	public static InputStream loadStream(String name) {
		return Game.class.getResourceAsStream("/resources/" + name);
	}
}

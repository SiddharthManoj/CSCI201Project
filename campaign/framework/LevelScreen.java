/*
 * LevelScreen.java
 * James Zhang
 * Represents the template for a level screen, which shows a menu whenever
 * the escape key is pressed.
 */

package campaign.framework;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import framework.GameScreen;
import framework.GameWindow;

public abstract class LevelScreen extends GameScreen {
	private static final long serialVersionUID = 1L;

	// displays instructions or plot
	public GameText gameText;

	// image pop-up variables
	private BufferedImage popup;
	private int fadeRate = 5;
	private int alpha = 0;

	// constructor
	public LevelScreen(GameWindow parent, String name) {
		super(parent, name);
	}
	
	// a method that may be overridden
	public void init() {}

	// helper method
	public static BufferedImage makeTranslucent(BufferedImage image,
			float transparency) {
		BufferedImage result = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TRANSLUCENT);
		Graphics2D g = result.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				transparency));
		g.drawImage(image, null, 0, 0);
		g.dispose();
		return result;
	}

	// the escape key must bring up the menu
	@Override
	public void processKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			PlayMenuScreen.setBackgroundImage(this.getImage());
			PlayMenuScreen.pausedScreen = this.getName();
			setScreen("playMenuScreen");
			e.consume();
		}
	}

	// pop-up properties
	public void addPopup(BufferedImage image) {
		this.popup = image;
	}

	public void setAlpha(int value) {
		if (value < 0)
			value = 0;
		
		alpha = value;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setFadeRate(int value) {
		this.fadeRate = Math.abs(value);
	}

	// updates the transparency of the pop-up
	public void updatePopup() {
		alpha -= fadeRate;

		if (alpha < 0)
			alpha = 0;
	}

	// draws the pop-up image with transparency
	public void drawPopup(Graphics g) {
		float f = (float) Math.min(100, alpha) / 100;
		if (popup != null && alpha != 0)
			g.drawImage(makeTranslucent(popup, f), 0, 0, null);
	}

	// unused updating method to be overridden by subclass
	@Override
	public void update(long gameTime) {}
	
	// unused repainting method to be overridden by subclass
	@Override
	public void repaint(Graphics g) {}
}

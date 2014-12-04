/*
 * WinScreen.java
 * James Zhang
 * Shows the win screen.
 */

package campaign.levels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import campaign.framework.LevelScreen;
import framework.GameWindow;
import game.Game;
import game.MusicPlayer;

public class WinScreen extends LevelScreen {
	private static final long serialVersionUID = 1L;

	// the background image to display
	BufferedImage background;

	// constructor
	public WinScreen(GameWindow parent) {
		super(parent, "winScreen");

		// load the background image
		background = Game.loadImage("win.png");
	}

	// paints the screen
	@Override
	public void repaint(Graphics g) {
		// draw background image
		g.drawImage(background, 0, 0, null);

		// congratulate player
		g.setColor(new Color(255, 255, 0));
		g.setFont(Game.font48);
		g.drawString("Congratulations!", 20, 60);
		g.drawString("You beat the game!", 20, 113);

		// draw instructions
		g.setColor(new Color(255, 255, 0));
		g.setFont(Game.font24);
		g.drawString("Press any key to continue", 20, 150);
	}
	// called when this screen is displayed
	@Override
	public void processTransition() {
		// play music
		MusicPlayer.playLoop("win");
	}

	// go back to the menu screen when any key is pressed
	@Override
	public void processKeyPressed(KeyEvent e) {
		transitionScreen("menuScreen", 500);
	}
}

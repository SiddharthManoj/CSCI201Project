/*
 * PlayMenuScreen.java
 * James Zhang
 * January 3 2011
 * The menu screen that pops up when the user is playing the game and presses
 * the escape button. This class has static variables for ease of access.
 */

package campaign.framework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import framework.GameScreen;
import framework.GameWindow;
import game.Game;
import game.MenuButton;

public class PlayMenuScreen extends GameScreen implements ActionListener {
	private static final long serialVersionUID = 1L;

	// the name of the level screen that is paused
	public static String pausedScreen;

	// the background image for the paused level screen
	private static BufferedImage background;

	// values for the menu box
	int left = 250, top = 228;
	
	// dimensions for the buttons
	int width = 300, height = 48;

	// creates a screen
	public PlayMenuScreen(GameWindow parent) {
		super(parent, "playMenuScreen");

		// add a menu box (buttons)
		String[] names = { "Resume", "Menu Screen", "Quit Game" };
		int[] indices = { 0, 1, 2 };
		MenuButton[] buttons = new MenuButton[names.length];

		// add the buttons
		for (int i = 0; i < names.length; i++) {
			buttons[i] = new MenuButton();

			buttons[i].setFont(Game.font24);
			buttons[i].setSize(width, height);
			buttons[i].setText(Game.metrics24, names[i]);
			buttons[i].setActionCommand(names[i]);
			buttons[i].setLocation(left, top + i * height);
			buttons[i].setRound(indices[i]);

			buttons[i].addActionListener(this);
			add(buttons[i]);
		}
	}

	// sets the background of the image. this background image is processed
	// now so it doesn't have to be processed repeatedly during repainting
	public static void setBackgroundImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		// copy the image
		background = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = background.getGraphics();
		g.drawImage(image, 0, 0, null);

		// draw the darken effect onto the image
		g.setColor(new Color(128, 128, 128, 224));
		g.fillRect(0, 0, width, height);
	}

	// pressing escape here will exit the menu
	@Override
	public void processKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			setScreen(pausedScreen);
		}
	}

	// perform the appropriate action
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals("Resume")) {
			setScreen(pausedScreen);
		} else if (command.equals("Menu Screen")) {
			transitionScreen("menuScreen", 500);
			((LevelScreen) getScreen(pausedScreen)).init();
		} else if (command.equals("Quit Game")) {
			System.exit(0);
		}
	}

	@Override
	public void update(long gameTime) {}

	// repaints the screen
	@Override
	public void repaint(Graphics g) {
		// draw the background
		g.drawImage(background, 0, 0, null);

		// draw the menu box
		Game.paintBox(g, left - 1, top - 1, width + 1, 3 * height + 1);
	}
}

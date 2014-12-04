/*
 * MenuScreen.java
 * James Zhang
 * December 10 2011
 * Shows the menu.
 */

package game;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import campaign.framework.*;
import campaign.levels.LevelScreen1;
import campaign.levels.LevelScreen2;
import campaign.levels.LevelScreen3;
import campaign.levels.LevelScreen4;
import campaign.levels.LevelScreen5;
import campaign.levels.WinScreen;

import framework.*;

import practice.PracticeScreen;

public class MenuScreen extends LevelScreen implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;

	// this value slowly increases to the target loading percentage
	private double loadPercentage;

	// the set loading percentage. the value is changed by the loading objects
	public double targetLoadPercentage;

	// the play, options, and about buttons
	private MenuButton[] buttons;

	// the background image
	private BufferedImage background;

	// location and size of the menu box
	int left = 250, top = 300;
	int width = 300, height = 48;

	// arrays indicating button properties
	private static final String[] names = { "Play Campaign", "Practice",
			"Instructions", "About", "Quit" };
	private static final String[] actions = { "levelScreen1", "practiceScreen",
			"instructionsScreen", "aboutScreen", "exit" };
	private static final int[] indices = { 0, 1, 1, 1, 2 };

	// creates a new menu screen and loads the resources
	public MenuScreen(GameWindow parent) {
		super(parent, "menuScreen");

		// create the buttons
		buttons = new MenuButton[names.length];
		for (int i = 0; i < names.length; i++) {
			buttons[i] = new MenuButton();

			buttons[i].setFont(Game.font24);
			buttons[i].setSize(width, height);
			buttons[i].setText(Game.metrics24, names[i]);
			buttons[i].setActionCommand(actions[i]);
			buttons[i].setLocation(left, top + i * height);
			buttons[i].setRound(indices[i]);
			buttons[i].setEnabled(false);

			buttons[i].addActionListener(this);
			add(buttons[i]);
		}

		// load background image
		background = Game.loadImage("menu.png");
	}

	// starts the thread that loads the resources
	public void load() {
		Thread thread = new Thread(this);
		thread.run();
	}
	
	// loads resources in a separate thread
	@Override
	public void run() {
		targetLoadPercentage = 0;

		// load questions first
		Question.load();

		// create and load the screens
		InstructionsScreen instructions = new InstructionsScreen(parent(), this);
		AboutScreen about = new AboutScreen(parent(), this);
		PlayMenuScreen menu = new PlayMenuScreen(parent());
		WinScreen win = new WinScreen(parent());
		PracticeScreen practice = new PracticeScreen(parent());
		QuestionScreen questions = new QuestionScreen(parent());
		
		// load the bullet
		Bullet.load();
		
		// add the levels
		parent().addScreen(new LevelScreen1(parent()));
		parent().addScreen(new LevelScreen2(parent()));
		parent().addScreen(new LevelScreen3(parent()));
		parent().addScreen(new LevelScreen4(parent()));
		parent().addScreen(new LevelScreen5(parent()));

		// add other screens
		parent().addScreen(practice);
		parent().addScreen(instructions);
		parent().addScreen(about);
		parent().addScreen(win);

		// add escape menu and question screen
		parent().addScreen(menu);
		parent().addScreen(questions);

		// everything is now loaded; play the menu music
		targetLoadPercentage = 1;
		MusicPlayer.playLoop("main");
	}

	// updates the screen
	@Override
	public void update(long gameTime) {
		// skip updating after the loading is done
		if (loadPercentage == 1)
			return;
		
		// update the loading percentage, limiting how much it can change
		double difference = targetLoadPercentage - loadPercentage;
		double change = Math.min(0.02, difference / 2);
		if (difference < 0.01)
			change = difference;
		loadPercentage += change;

		// enable the buttons when done loading
		if (loadPercentage == 1)
			for (MenuButton button : buttons)
				button.setEnabled(true);
	}

	// called when the menu screen is displayed
	@Override
	public void processTransition() {
		// play main menu music
		MusicPlayer.playLoop("main");
	}

	// draws the progress fill
	private void drawProgressFill(Graphics g) {
		// border widths
		int b5 = 5;
		int b4 = b5 - 1;

		// values
		int len = buttons.length;
		int hwidth = width / 2, toth = len * height;
		int tot = 2 * (width + toth) + 4 * b5; // total pixels of progress bar

		// find total length and set progress fill color
		int length = (int) (tot * loadPercentage);
		g.setColor(new Color(112, 112, 160));

		// draw top left: 100 + 5 = 105
		if (length >= hwidth + b5)
			g.fillRect(left + hwidth, top - b5, hwidth + b5, b4);
		else {
			g.fillRect(left + hwidth, top - b5, length, b4);
			return;
		}
		length -= (hwidth + b5);

		// draw right: 180 + 5 = 185
		if (length >= toth + b5)
			g.fillRect(left + width + 1, top - 1, b4, toth + b5 + 1);
		else {
			g.fillRect(left + width + 1, top - 1, b4, length + 1);
			return;
		}
		length -= (len * height + b5);

		// draw bottom: 200 + 5 = 205
		if (length >= width + b5)
			g.fillRect(left - b5, top + toth + 1, width + b5 + 1, b4);
		else {
			g.fillRect(left + width - length, top + toth + 1, length + 1, b4);
			return;
		}
		length -= (width + b5);

		// draw left: 180 + 5 = 185
		if (length >= toth + b5)
			g.fillRect(left - b5, top + -b5, b4, toth + b5 + 1);
		else {
			g.fillRect(left - b5, top + toth - length, b4, length + 1);
			return;
		}
		length -= (toth + b5);

		// draw top right: 100
		if (length >= hwidth)
			g.fillRect(left - 1, top - b5, hwidth + 1, b4);
		else {
			g.fillRect(left - 1, top - b5, length + 1, b4);
			return;
		}
		length -= hwidth;
	}

	// repaints the screen
	@Override
	public void repaint(Graphics g) {
		// draw background
		g.drawImage(background, 0, 0, 800, 600, null);
		
		// border widths
		int b5 = 5;
		int b4 = b5 - 1;
		int b10 = 2 * b5;

		// other values
		int len = buttons.length;
		int toth = len * height;

		// fill progress background
		g.setColor(new Color(192, 192, 192));
		g.fillRect(left - b5, top - b5, width + b10, toth + b10);

		// draw progress fill
		drawProgressFill(g);

		// draw progress outline
		g.setColor(new Color(64, 64, 64));
		g.drawRect(left - 1, top - 1, width + 1, toth + 1);
		g.drawRect(left - b5 - 1, top - b5 - 1, width + b10 + 1, toth + b10 + 1);

		// draw progress glow
		g.setColor(new Color(255, 255, 255, 48));
		g.drawRect(left - 1 - 1, top - 1 - 1, width + 2 + 1, toth + 2 + 1);
		g.drawRect(left - b4 - 1, top - b4 - 1, width + b10 - 2 + 1, toth + b10
				- 2 + 1);

		// title
		g.setColor(new Color(0, 0, 0));
		g.setFont(Game.font48);
		g.drawString(Game.title, 130, 120);
	}

	// handle button presses
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("exit"))
			System.exit(0);
		else
			transitionScreen(e.getActionCommand(), 500);
	}
}

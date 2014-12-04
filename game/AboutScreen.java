/*
 * AboutScreen.java
 * James Zhang
 * December 10 2011
 * Shows information about the game.
 */

package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import campaign.framework.LevelScreen;
import framework.GameWindow;

public class AboutScreen extends LevelScreen implements ActionListener {
	private static final long serialVersionUID = 1L;

	// location and size of label
	private int labelTop = 40, labelLeft = 40;
	private int labelWidth = 720, labelHeight = 420;

	// creates a new about screen and loads the resources
	// while loading, change loader.targetLoadPercentage from 0.97 - 1
	public AboutScreen(GameWindow parent, MenuScreen loader) {
		super(parent, "aboutScreen");

		// add the back button
		TextButton back = new TextButton();
		back.setSize(80, 30);
		back.setLocation(360, 490);
		back.setFont(Game.font36);
		back.setBackground(new Color(128, 128, 128));
		back.setText(Game.metrics36, "Back");

		back.addActionListener(this);
		add(back);

		// set the about text
		String text = "<html><center>Created By "
				+ "Cheng Zhang, "
				+ "Richard Jiang, and "
				+ "<br>Siddharth Manoj<br>"
				+ "<br>We do not own the music and sprites used in this<br> game. "
				+ "All music is used for non-commercial purposes <br>"
				+ "and not for profit as it is used for educational <br>"
				+ "purposes only.<br>"
				+ "<br>Music used: Nostalgia - The Hero, Wizet - Ellinia,<br>"
				+ "Megaman - Depth, Wizet - Temple of Time,<br>"
				+ "Yu-Gi-Oh! - Treasure Trove, Megaman - Cybeast,<br>"
				+ "Megaman - Marine City, SoundJax - Bullet.wav<br>"
				+ "</center></html>";
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setFont(Game.font24);
		label.setForeground(new Color(16, 32, 64));

		label.setLocation(labelLeft, labelTop);
		label.setSize(labelWidth, labelHeight);

		add(label);
		loader.targetLoadPercentage = 1;
	}

	// unused updating method
	@Override
	public void update(long gameTime) {}

	// repaints the screen
	@Override
	public void repaint(Graphics g) {
		Game.paintBox(g, labelLeft, labelTop, labelWidth, labelHeight);
	}

	// process "back" button clicked
	@Override
	public void actionPerformed(ActionEvent arg0) {
		transitionScreen("menuScreen", 500);
	}
}

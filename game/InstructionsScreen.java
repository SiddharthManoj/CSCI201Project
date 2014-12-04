/*
 * InstructionsScreen.java
 * James Zhang
 * December 25 2011
 * Shows the instructions.
 */

package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import campaign.framework.LevelScreen;
import framework.GameWindow;

public class InstructionsScreen extends LevelScreen implements ActionListener {
	private static final long serialVersionUID = 1L;

	// location and size of label
	private int labelTop = 60, labelLeft = 60;
	private int labelWidth = 680, labelHeight = 380;

	// constructor
	public InstructionsScreen(GameWindow parent, MenuScreen loader) {
		super(parent, "instructionsScreen");

		// create the back button
		TextButton back = new TextButton();

		back.setSize(80, 30);
		back.setLocation(360, 490);
		back.setFont(Game.font36);
		back.setBackground(new Color(128, 128, 128));
		back.setText(Game.metrics36, "Back");

		back.addActionListener(this);
		add(back);

		// create the instructions label
		String text = "<html>Click to answers questions or open shops.<br><br>"
				+ "The basic keys used in the game are:<br>"
				+ "[W][A][S][D] for movement<br>"
				+ "[ARROW KEYS] for movement (alternative)<br>"
				+ "[SPACE] for combat<br>" + "[TAB] for advancing the text<br>"
				+ "[ESC] to pause or quit<br><br>"
				+ "Storyline:<br>The evil intergalactic thief, Sinclair, has " +
				"stolen a large amount of<br> money from an important " +
				"bank, and you, Carnegie the detective, must<br> stop him! " +
				"Fight through hordes of bosses and lesser enemies to get " +
				"to<br> him! Detailed instructions will be given at the " +
				"beginning of each <br> level.</html>";
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setFont(Game.font16);
		label.setForeground(new Color(16, 32, 64));

		label.setSize(labelWidth, labelHeight);
		label.setLocation(labelTop, labelLeft);

		add(label);

		loader.targetLoadPercentage = 0.93;
	}

	// unused updating method
	@Override
	public void update(long gameTime) {}

	// repaints the screen
	@Override
	public void repaint(Graphics g) {
		Game.paintBox(g, labelTop, labelLeft, labelWidth, labelHeight);
	}

	// process "back" button clicked
	@Override
	public void actionPerformed(ActionEvent arg0) {
		transitionScreen("menuScreen", 500);
	}
}

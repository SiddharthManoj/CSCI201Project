/*
 * PracticeScreen.java
 * James Zhang
 * Continuously asks the player questions.
 */

package practice;

import java.awt.Graphics;

import campaign.framework.QuizScreen;
import framework.GameWindow;
import game.Game;

public class PracticeScreen extends QuizScreen {
	private static final long serialVersionUID = 1L;

	// constructor
	public PracticeScreen(GameWindow parent) {
		super(parent, "practiceScreen");

		// load asteroid image
		asteroidImage = Game.loadImage("quizAsteroid.png");
	}

	// called when this screen is displayed
	@Override
	public void processTransition() {
		super.processTransition();

		// show instructions
		gameText.setText("Welcome to practice mode! Try to answer questions "
				+ "quickly and accurately as the asteroid falls. The "
				+ "question does not change until you answer it correctly, "
				+ "so take your time. Press esc to pause or return to the "
				+ "menu screen.");
	}

	// paints over the quiz screen
	@Override
	public void paintScreen(Graphics g) {}

	// called when a question is answered
	@Override
	public void questionAnswered() {}
}

/*
 * LevelScreen5.java
 * James Zhang
 * Level 5.
 */

package campaign.levels;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import campaign.framework.QuizScreen;
import framework.GameWindow;
import game.Game;
import game.MusicPlayer;

public class LevelScreen5 extends QuizScreen implements ActionListener {
	private static final long serialVersionUID = 1L;

	// pop-ups
	private BufferedImage levelPopup;
	private BufferedImage instructionsPopup;
	
	// number of questions left to answer in order to pass this level
	int questionsLeft = 10;

	// constructor
	public LevelScreen5(GameWindow parent) {
		super(parent, "levelScreen5");
		
		// load pop-ups
		levelPopup = Game.loadImage("level5.png");
		instructionsPopup = Game.loadImage("quiz.png");
		
		asteroidImage = Game.loadImage("quizPiece.png");
	}

	// called when this screen is displayed
	@Override
	public void processTransition() {
		super.processTransition();
		
		// play level 5 music
		MusicPlayer.playLoop("level5");
		
		// reset variables
		questionsLeft = 10;

		// show level title
		addPopup(levelPopup);
		setAlpha(10 * 60 * 3 + 100);
		setFadeRate(10);
		
		// show introduction text
		gameText.setText("Carnegie: Yes!  My missile hit Sinclair’s ship "
				+ "perfectly!  Now, all I have to do is catch up to him in "
				+ "my own ship.  However, debris from his ship is falling "
				+ "toward me... I must be careful!");
		gameText.addFinishedListener(this);
	}

	// this method is called repeatedly
	@Override
	public void update(long gameTime) {
		super.update(gameTime);
		super.updatePopup();

		if (questionsLeft <= 0 && gameText.alpha == 0)
			transitionScreen("winScreen", 1000);
	}

	// paints over the quiz screen
	@Override
	public void paintScreen(Graphics g) {
		if (questionsLeft == 1)
			g.drawString("1 more question left!", 10, 20);
		else if (questionsLeft != 0)
			g.drawString(questionsLeft + " more questions left", 10, 20);
		
		super.drawPopup(g);
	}

	// called when a question is answered
	@Override
	public void questionAnswered() {
		--questionsLeft;
		if (questionsLeft < 0)
			questionsLeft = 0;

		if (questionsLeft <= 0) {
			setShow(false);
			gameText.setText("Carnegie: Sinclair, I have caught you!\n"
					+ "Sinclair: You may have defeated me, but the truth is, "
					+ "I am only a small part of a huge web of intergalactic "
					+ "evil!  There will always be more crimes to solve, "
					+ "despite your small victory today! Hahahahaha!"
					+ "\nCarnegie: What Sinclair says may be true, but that "
					+ "does not detract from my victory today at all.  "
					+ "Indeed, what he says is only a reminder to me of the "
					+ "need to work even harder and with even more "
					+ "determination to stop crime and to solve mysteries.  "
					+ "The realization of my ideals may elude me today, and "
					+ "it may escape me tomorrow, but that does not matter.  "
					+ "Onward!");
			gameText.update(getGameTime());
		}
	}

	// called when the game text finishes its introduction
	@Override
	public void actionPerformed(ActionEvent e) {
		// show level instructions
		addPopup(instructionsPopup);
		setAlpha(1 * 60 * 4);
		setFadeRate(1);
	}
}

/*
 * QuizScreen.java
 * Siddharth Manoj, James Zhang
 * Continuously asks the player questions.
 */

package campaign.framework;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import campaign.framework.GameText;
import campaign.framework.LevelScreen;
import framework.GameWindow;
import game.Game;
import game.Question;

public abstract class QuizScreen extends LevelScreen {
	private static final long serialVersionUID = 1L;
	
	// whether to show the question
	private boolean show = true;

	// asteroid and background images
	protected Image asteroidImage;
	private Image background;

	// location of the falling asteroid
	private int asteroidLocation;

	// current question, questions, answer choices, and answer
	Question currentQuestion;

	// buttons for the player to answer
	ButtonGroup colorGroup;
	JRadioButton[] answerButtons = new JRadioButton[4];

	// constructor
	public QuizScreen(GameWindow parent, String name) {
		super(parent, name);
		init();
	}

	// loads the buttons
	@Override
	public void init() {
		gameText = new GameText(new Rectangle(20, 220, 760, 200));

		// create the buttons
		colorGroup = new ButtonGroup();
		for (int i = 0; i < 4; i++) {
			answerButtons[i] = new JRadioButton("");
			answerButtons[i].setLocation(20, 465 + 30 * i);
			answerButtons[i].setSize(760, 30);
			answerButtons[i].setForeground(Color.white);
			answerButtons[i].setOpaque(false);
			answerButtons[i].setSelected(false);

			add(answerButtons[i]);
			colorGroup.add(answerButtons[i]);
		}

		// load the background
		background = Game.loadImage("quiz.jpg");
		
		// initialize variables
		asteroidLocation = 0;
		currentQuestion = Question.next();
	}

	// clears the selection of all answer buttons
	private void deselectAnswerButtons() {
		colorGroup.clearSelection();
	}

	// called when the screen is shown
	@Override
	public void processTransition() {
		currentQuestion = Question.next();
		setShow(true);
	}

	// updates the answer buttons to the correct question
	private void updateQuestion() {
		for (int i = 0; i < 4; i++)
			answerButtons[i].setText(currentQuestion.getAnswerChoice(i));
	}
	
	// paints other things on the screen
	public abstract void paintScreen(Graphics g);
	
	// called whenever a question is answered by the player
	public abstract void questionAnswered();
	
	// sets whether to show the answer choices
	public void setShow(boolean value) {
		show = value;
		for (int i = 0; i < 4; i++)
			answerButtons[i].setVisible(show);
	}

	// updates the quiz screen
	@Override
	public void update(long gameTime) {
		// don't do anything while the game text is showing
		gameText.update(gameTime);
		if (gameText.alpha != 0)
			return;

		// update asteroid location
		++asteroidLocation;
		if (asteroidLocation >= 600)
			asteroidLocation = 0;

		// check if the user answered the question correctly
		int answer = currentQuestion.getCorrectAnswerIndex();
		for (int i = 0; i < 4; i++) {
			if (answerButtons[i].isSelected() && answer == i) {
				asteroidLocation = 0;

				// get new question
				currentQuestion = Question.next();

				deselectAnswerButtons();
				questionAnswered();
				
				break;
			}
		}

		updateQuestion();
	}
	
	// paints the quiz screen
	@Override
	public void repaint(Graphics g) {
		// draw background and asteroid
		g.drawImage(background, 0, 0, 800, 600, this);
		
		// draw asteroid and question text?
		if (show) {
			g.drawImage(asteroidImage, 325, asteroidLocation, 50, 50, null);
			
			Font font = new Font("SansSerif", Font.BOLD, 12);
			g.setFont(font);
			g.setColor(Color.white);
			g.drawString(currentQuestion.getQuestion(), 10, 450);
		}
		
		paintScreen(g);

		gameText.paint(g);
	}

	// called when a key is pressed
	@Override
	public void processKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_TAB)
			gameText.keyPressed();

		super.processKeyPressed(e);
	}
}

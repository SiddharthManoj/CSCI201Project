/*
 * QuestionScreen.java
 * James Zhang
 * Represents a screen that asks the user questions.
 */

package campaign.framework;

import framework.GameWindow;
import game.Game;
import game.MenuButton;
import game.Question;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

public class QuestionScreen extends LevelScreen implements ActionListener {
	private static final long serialVersionUID = 1L;

	// static controls
	static JLabel label;
	static int[] indices = { 0, 1, 1, 2 };
	static MenuButton[] buttons = new MenuButton[4];

	// label box dimensions for the question
	static final int labelX = 20, labelY = 20;
	static final int labelWidth = 760, labelHeight = 260;

	// the background image for the paused level screen
	private static BufferedImage background;

	// the question to be asked and its 4 answer choices
	public static String question = "";
	public static String[] answers = new String[4];

	// the correct answer index and the response index
	public static int answer = 0;
	public static int firstResponse = -1;

	// value is set to to 0 after done
	public static int flag = 0;

	// the screen to return to after the question is answered
	public static String prev = "";

	// the time when the screen goes back
	private static long fadeTime = -1;
	
	// question count
	private static int questionCount = 1;
	private static boolean multipleQuestions = false;

	// values for the menu box
	int left = 50, top = 324;
	int width = 700, height = 48;

	// constructor
	public QuestionScreen(GameWindow parent) {
		super(parent, "questionScreen");

		// create the box
		for (int i = 0; i < 4; i++) {
			buttons[i] = new MenuButton();

			buttons[i].setFont(Game.font12);
			buttons[i].setSize(width, height);
			buttons[i].setText(Game.metrics12, "");
			buttons[i].setActionCommand("" + i);
			buttons[i].useOriginalPainting();
			buttons[i].setLocation(left, top + i * height);
			buttons[i].setRound(indices[i]);

			buttons[i].addActionListener(this);
			add(buttons[i]);
		}

		// label
		label = new JLabel("", JLabel.CENTER);
		label.setFont(Game.font24);
		label.setForeground(new Color(16, 32, 64));

		label.setLocation(labelX, labelY);
		label.setSize(labelWidth, labelHeight);

		add(label);
	}

	// the initializer
	// sets the background of the image. this background image must be
	// processed so the image does not have to be processed during painting
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
	
	// assigns the question screen to ask more than one question
	public static void assignQuestionCount(int questions) {
		questionCount = questions;
		multipleQuestions = true;
	}
	
	// assigns the screen to display a question
	public static void importQuestion(Question q) {
		question = q.getQuestion();

		for (int i = 0; i < 4; i++)
			answers[i] = q.getAnswerChoice(i);

		answer = q.getCorrectAnswerIndex();

		flag = 1;
		fadeTime = -1;
		updateQuestion();
	}

	// updates the label and buttons to display the question and answers
	public static void updateQuestion() {
		// label
		label.setText("<html><center>" + question + "</center></html>");

		// user has not responded yet
		firstResponse = -1;

		// update answers
		for (int i = 0; i < 4; i++) {
			buttons[i].useOriginalPainting();
			buttons[i].setEnabled(true);

			// set text to the answer
			buttons[i].setForeground(new Color(0, 0, 0));
			buttons[i].setText("<html><center>" + answers[i]
					+ "</center></html>");

			// disable a answer button if its text is empty
			buttons[i].setVisible(answers[i].length() != 0);
		}
	}

	// updates the question screen
	@Override
	public void update(long gameTime) {
		if (fadeTime != -1 && System.currentTimeMillis() >= fadeTime)
			setScreen(prev);
	}

	// draw the background and box border
	@Override
	public void repaint(Graphics g) {
		g.drawImage(background, 0, 0, null);
		Game.paintBox(g, left - 1, top - 1, width + 1, height * 4 + 1);
		
		// draw questions left
		if (multipleQuestions && questionCount >= 1) {
			g.setFont(Game.font24);
			g.setColor(Color.yellow);
			
			if (questionCount == 1)
				g.drawString("1 correct question left!", 20, 28);
			else
				g.drawString(questionCount + " correct questions left", 20, 28);
		}
	}

	// called when a button is pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		// get answer index
		int idx = e.getActionCommand().charAt(0) - '0';

		// only set the response once
		if (firstResponse == -1) {
			firstResponse = idx;
		}

		buttons[idx].setEnabled(false);
		
		// if the answer is correct, wait 1 second for the user to see so
		// and highlight the correct answer
		if (idx == answer) {
			flag = 0;

			// display the answer
			buttons[idx].setForeground(new Color(0, 128, 32));
			
			// hide the other answers
			for (int i = 0; i < 4; i++)
				if (i != idx) {
					buttons[i].setText("");
					buttons[i].setEnabled(false);
				}
			
			if (firstResponse == answer)
				--questionCount;
			
			// check if more questions have to be answered
			if (multipleQuestions && questionCount >= 1) {
				importQuestion(Question.next());
				updateQuestion();
			} else if (multipleQuestions) {
				fadeTime = System.currentTimeMillis();
				questionCount = 1;
				multipleQuestions = false;
			} else {
				// set the time this screen disappears
				fadeTime = System.currentTimeMillis() + 1000;
				questionCount = 1;
			}
		}
	}
}

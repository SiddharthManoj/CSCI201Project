/*
 * Question.java
 * James Zhang
 * Manages the questions that are used in the game.
 */

package game;

import java.util.ArrayList;
import java.util.Scanner;

public class Question {
	// list of all questions
	private static ArrayList<Question> questions;

	// list of questions that were not asked yet, which will be refilled when
	// this list becomes empty
	private static ArrayList<Question> questionsToAsk;

	// the question asked
	private String question;

	// the four answer choices to the question
	private String[] answerChoices = new String[4];

	// the index of the correct answer
	private int answer;

	// constructor
	public Question(String question, String[] answerChoices, int answer) {
		this.question = question;

		for (int i = 0; i < 4; i++)
			this.answerChoices[i] = answerChoices[i];

		this.answer = answer;
	}
	
	// getter methods
	public String getQuestion() {
		return question;
	}
	
	public String getAnswerChoice(int index) {
		return answerChoices[index];
	}
	
	public int getCorrectAnswerIndex() {
		return answer;
	}

	// loads all of the questions from a text file
	public static void load() {
		// initialize lists
		questions = new ArrayList<Question>();
		questionsToAsk = new ArrayList<Question>();

		// open the text file
		Scanner in = new Scanner(Game.loadStream("questions.txt"));

		// read every line, which is either blank, a question, or an answer
		while (in.hasNextLine()) {
			// the first line should contain question...
			String question = in.nextLine();

			// ...unless it's a blank line
			if (question.length() <= 2)
				continue;
			
			// store the answer
			String[] answerChoices = new String[4];
			int answer = 0;
			
			// make sure there is actually a marked answer to this question
			boolean hasAnswer = false;

			// read in the possible answer choices
			for (int i = 0; i < answerChoices.length; i++) {
				answerChoices[i] = in.nextLine();

				// the answer starts with an asterisk
				if (answerChoices[i].startsWith("*")) {
					answerChoices[i] = answerChoices[i].substring(1);
					answer = i;
					hasAnswer = true;
				}
			}

			// only add the question if there is an actual answer
			if (hasAnswer)
				questions.add(new Question(question, answerChoices, answer));
		}

		// clean up
		in.close();
	}

	// clears the questions to ask, add in all the questions, then shuffle it
	public static void clear() {
		questionsToAsk.clear();
		questionsToAsk.addAll(questions);

		// shuffle deck
		for (int i = 0; i < 3; i++)
			shuffle();
	}

	// randomizes the questions to ask
	private static void shuffle() {
		for (int i = 0; i < questions.size(); i++) {
			// choose an element to swap with
			int rand = (int) (questions.size() * Math.random());

			// swap the current one and the chosen element
			Question temp = questionsToAsk.get(i);
			questionsToAsk.set(i, questionsToAsk.get(rand));
			questionsToAsk.set(rand, temp);
		}
	}

	// gets the next question, resetting the questions to ask if needed
	public static Question next() {
		if (questionsToAsk.size() == 0)
			clear();

		return questionsToAsk.remove(0);
	}
}

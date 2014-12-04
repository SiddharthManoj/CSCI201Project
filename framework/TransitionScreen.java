/*
 * TransitionScreen.java
 * James Zhang
 * December 9 2011
 * The TransitionScreen displays a fade out from the current GameScreen and a
 * fade into the next GameScreen. A snapshot of both GameScreen classes are
 * taken and used for the transition, so user input will not generate any
 * responses.
 */

package framework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TransitionScreen extends GameScreen {
	private static final long serialVersionUID = 1L;

	// whether the screen is performing a transition
	private boolean isTransitioning;

	// the image of the screen that is fading out
	private BufferedImage fadeOutImage;

	// the image of the screen that is fading in
	private BufferedImage fadeInImage;

	// the next screen that is to be transitioned to
	private GameScreen nextScreen;

	// the game time when the transition started
	private long startTime;

	// the total length of the transition in milliseconds
	private int transitionLength;

	// the image to draw, which points to one of the images
	private BufferedImage image;

	// the blackness alpha of the fade
	private int alpha;

	// constructor
	public TransitionScreen(GameWindow parent) {
		super(parent, "transitionScreen");

		isTransitioning = false;
	}

	// transitions from one screen to another
	public void transition(GameScreen sender, GameScreen screen, int length) {
		fadeOutImage = sender.getImage();
		nextScreen = screen;
		fadeInImage = screen.getImage();
		startTime = getGameTime();
		transitionLength = length;
		image = fadeOutImage;
		alpha = 0;
		isTransitioning = true;
		setScreen(this);
	}

	// paint the appropriate screen image and the fade
	@Override
	public void repaint(Graphics g) {
		if (!isTransitioning)
			return;

		// draw screen image
		g.drawImage(image, 0, 0, null);

		// draw overlay
		g.setColor(new Color(0, 0, 0, alpha));
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	// updates the transition screen to the next frame
	@Override
	public void update(long time) {
		if (!isTransitioning)
			return;

		// calculate progress
		long elapsedTime = time - startTime;
		double progress = (double) elapsedTime / transitionLength;

		// switch screen image if necessary
		if (progress >= 0.5 && image != fadeInImage)
			image = fadeInImage;

		// set screen if finished or calculate alpha
		if (progress >= 1) {
			setScreen(nextScreen);
			nextScreen.processTransition();
		} else
			alpha = (int) (255 * (1 - 2 * Math.abs(0.5 - progress)));
	}
}

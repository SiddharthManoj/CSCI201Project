/*
 * GameWindow.java
 * James Zhang
 * November 26 2011
 * The GameWindow manages multiple GameScreen classes and implements transitions
 * between screens with the TransitionScreen class.
 */

package framework;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.JFrame;

public class GameWindow extends JFrame implements KeyListener, Runnable,
		WindowFocusListener, KeyEventDispatcher {
	private static final long serialVersionUID = 1L;

	// the container for all of the screens
	private Container content;

	// the list of screens used in the game
	private ArrayList<GameScreen> screens;

	// the screen that is currently shown
	private GameScreen currentScreen;

	// the milliseconds that have passed while the has been focused
	private long gameTime;

	// the target number of milliseconds between game updates
	private int interval;

	// the list of codes that represent keys that are pressed
	private ArrayList<Integer> pressedKeys;

	// the screen that transitions between two screens
	private TransitionScreen transitionScreen;

	// whether the game is running
	private boolean isRunning;

	// constructor
	public GameWindow(String title, int fps) {
		// game loop variables
		interval = 1000 / fps;
		gameTime = 0;
		isRunning = false;

		// screen variables
		screens = new ArrayList<GameScreen>();
		content = getContentPane();
		content.setLayout(new CardLayout());

		// transition screen
		transitionScreen = new TransitionScreen(this);
		addScreen(transitionScreen);

		// keyboard variables and related listeners. use a keyboard focus
		// manager to capture key events even when another control is focused
		pressedKeys = new ArrayList<Integer>();
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);
		addWindowFocusListener(this);

		// window settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle(title);
	}

	// shows the game window and starts the game loop
	public void start(int x, int y, int width, int height) {
		// set the specified location
		setLocation(x, y);

		// the window must be visible before size-related methods work properly
		setVisible(true);

		// set the specified window client size
		content.setPreferredSize(new Dimension(width, height));
		pack();

		// refresh the current screen's size
		setScreen(currentScreen);

		// start the game loop
		Thread thread = new Thread(this);
		thread.start();
	}

	// returns whether the game window is focused (which means it's running)
	public boolean isRunning() {
		return isRunning;
	}

	// gets the milliseconds the game window has been focused
	public long getGameTime() {
		return gameTime;
	}

	// the game loop, which repeatedly updates the current screen
	@Override
	public void run() {
		long updateTime = System.currentTimeMillis();

		while (true) {
			// get the current time
			long curTime = System.currentTimeMillis();

			// calculate the time needed to sleep before the next update
			// the sleep time must be greater than or equal to 0
			long sleepTime = Math.max(0, updateTime - curTime);
			sleep(sleepTime);

			// update the current time
			curTime = System.currentTimeMillis();

			if (isRunning) {
				// update game time
				gameTime += interval;

				// update and repaint the game
				update();
			}

			// increment the update time, but make sure it's more than or equal
			// to the current time
			updateTime = Math.max(curTime, updateTime + interval);
		}
	}

	// sleeps the currently executing thread
	public static void sleep(long millis) {
		// there is no effort made to ensure the specified sleep time is met
		// because the interrupt method should never be called
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.yield();
		}
	}

	// updates the current screen
	private void update() {
		// avoid a null-pointer exception
		if (currentScreen != null)
			currentScreen.update(gameTime);

		repaint();
	}

	// screen methods
	public GameScreen getCurrentScreen() {
		return currentScreen;
	}

	public GameScreen getScreen(String name) {
		for (GameScreen screen : screens) {
			if (screen.getName().equals(name))
				return screen;
		}

		return null;
	}
	
	public void addScreen(GameScreen screen) {
		// add the screen to both the array and layout
		screens.add(screen);
		content.add(screen, screen.getName());

		// set the screen's size to the content's size
		screen.setSize(content.getWidth(), content.getHeight());

		// a transition screen is added by default, so the first screen that
		// is added will be set as the current screen
		if (screens.size() == 2)
			setScreen(screen);
	}

	public void setScreen(String name) {
		GameScreen screen = getScreen(name);

		if (screen != null)
			setScreen(screen);
	}

	public void setScreen(GameScreen screen) {
		// update the reference to the current screen
		currentScreen = screen;

		// show the screen in the layout
		CardLayout layout = (CardLayout) content.getLayout();
		layout.show(content, screen.getName());
	}

	public void transitionScreen(String name, int length) {
		if (length < 0)
			throw new RuntimeException("transition length must be positive");

		// get the next screen by its name
		GameScreen screen = getScreen(name);

		if (screen != null)
			transitionScreen.transition(currentScreen, screen, length);
	}

	// methods that manage which keys are pressed
	public boolean isKeyPressed(int keyCode) {
		return pressedKeys.contains(keyCode);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		addPressedKey(e.getKeyCode());

		currentScreen.processKeyPressed(e);

		// when the alt button is typed and another key is pressed, windows
		// plays an annoying beep, so just consume the alt pressed event.
		// this is a temporary solution
		if (e.getKeyCode() == KeyEvent.VK_ALT)
			e.consume();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		removePressedKey(e.getKeyCode());

		currentScreen.processKeyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		addPressedKey(e.getKeyCode());

		currentScreen.processKeyTyped(e);
	}

	private void addPressedKey(int keyCode) {
		if (!pressedKeys.contains(keyCode))
			pressedKeys.add(keyCode);
	}

	private void removePressedKey(int keyCode) {
		int i = pressedKeys.indexOf(keyCode);

		if (i != -1)
			pressedKeys.remove(i);
	}

	// window focus listeners for running and pausing the game
	@Override
	public void windowLostFocus(WindowEvent e) {
		pressedKeys.clear();

		isRunning = false;
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		isRunning = true;
	}

	// key events must be raised even when another control is focused, so use
	// the keyboard focus manager
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		switch (e.getID()) {
			case KeyEvent.KEY_PRESSED:
				keyPressed(e);
				break;
			case KeyEvent.KEY_RELEASED:
				keyReleased(e);
				break;
			case KeyEvent.KEY_TYPED:
				keyTyped(e);
				break;
		}

		return false;
	}
}

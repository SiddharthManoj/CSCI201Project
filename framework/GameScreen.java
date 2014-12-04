/*
 * GameScreen.java
 * James Zhang
 * November 26 2011
 * The GameScreen manages user input, threading, and painting for one specific
 * part of the game. A GameWindow class manages multiple GameScreen classes.
 */

package framework;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public abstract class GameScreen extends JPanel implements MouseListener,
		MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;

	// the game window that contains the screen
	private GameWindow parent;

	// constructor
	public GameScreen(GameWindow parent, String name) {
		setName(name);
		this.parent = parent;

		// add event listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		// by default use no layout
		setLayout(null);
	}

	// these two methods must be implemented. the update method is called
	// first every interval, then repaint is called
	public abstract void update(long gameTime);

	public abstract void repaint(Graphics g);

	// getter methods put here for easy access
	public GameWindow parent() {
		return parent;
	}

	public long getGameTime() {
		return parent.getGameTime();
	}

	// screen-related methods
	public void setScreen(String name) {
		parent.setScreen(name);
	}

	public void setScreen(GameScreen screen) {
		parent.setScreen(screen);
	}

	public GameScreen getScreen(String name) {
		return parent.getScreen(name);
	}

	public GameScreen getCurrentScreen() {
		return parent.getCurrentScreen();
	}

	// transitions to another screen (transition length is in milliseconds)
	public void transitionScreen(String name, int transitionLength) {
		parent.transitionScreen(name, transitionLength);
	}

	// override this method if something must be done when the screen is
	// transitioned into
	public void processTransition() {}

	// returns whether a key is currently pressed
	public boolean isKeyPressed(int keyCode) {
		return parent.isKeyPressed(keyCode);
	}

	// returns whether mouse events should be relayed to the child class
	private boolean relayMouseEvents() {
		return parent.isRunning();
	}

	// empty override-able methods: these methods are called when the
	// appropriately named event occurs
	public void processKeyPressed(KeyEvent e) {}

	public void processKeyReleased(KeyEvent e) {}

	public void processKeyTyped(KeyEvent e) {}

	public void processMouseClicked(MouseEvent e) {}

	public void processMousePressed(MouseEvent e) {}

	public void processMouseReleased(MouseEvent e) {}

	public void processMouseEntered(MouseEvent e) {}

	public void processMouseExited(MouseEvent e) {}

	public void processMouseDragged(MouseEvent e) {}

	public void processMouseMoved(MouseEvent e) {}

	public void processMouseWheelMoved(MouseWheelEvent e) {}

	// the following are listener methods that will call the empty methods
	// above if necessary
	@Override
	public void mouseClicked(MouseEvent e) {
		if (relayMouseEvents())
			processMouseClicked(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (relayMouseEvents())
			processMousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (relayMouseEvents())
			processMouseReleased(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (relayMouseEvents())
			processMouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (relayMouseEvents()) {
			processMouseExited(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (relayMouseEvents()) {
			processMouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (relayMouseEvents()) {
			processMouseMoved(e);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (relayMouseEvents())
			processMouseWheelMoved(e);
	}

	// paints the screen
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		repaint(g);
	}

	// returns the image of this screen
	public BufferedImage getImage() {
		int w = getWidth();
		int h = getHeight();

		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();

		paintComponent(g);
		paintChildren(g);

		return bi;
	}

	// two game screens are equal if they have the same name
	@Override
	public boolean equals(Object other) {
		if (other instanceof GameScreen) {
			GameScreen gs = (GameScreen) other;
			return getName().equals(gs.getName());
		}

		return false;
	}
}

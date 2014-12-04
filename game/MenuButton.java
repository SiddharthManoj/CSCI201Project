/*
 * MenuButton.java
 * James Zhang
 * December 10 2011
 * A menu button that is part of a menu box.
 */

package game;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import framework.GameButton;

public class MenuButton extends GameButton {
	private static final long serialVersionUID = 1L;

	// the x-coordinate to draw the text at
	private int x;

	// the y-coordinate to draw the text at
	private int y;

	// how the button is rounded (0 for top, 1 for middle, 2 for bottom)
	private int round;

	// whether to use original button painting
	boolean useOriginalPainting = false;

	// constructor
	public MenuButton() {}

	// public methods
	public void setText(FontMetrics metrics, String text) {
		super.setText(text);
		x = centerTextHorizontally(metrics, text, getWidth());
		y = centerTextVertically(metrics, getHeight());
	}

	public void setRound(int index) {
		round = index;
	}

	// sets the button to use the original painting
	public void useOriginalPainting() {
		useOriginalPainting = true;
	}

	// painting methods
	// paints the background color
	private void paintBackground(Graphics g, Color color) {
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	// paints the bottom border if necessary
	private void paintBottom(Graphics g) {
		if (round != 2) {
			g.setColor(new Color(64, 64, 80));
			g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
		}
	}
	
	// paints the button text
	private void paintText(Graphics g) {
		g.setFont(getFont());
		g.setColor(new Color(16, 32, 64));
		g.drawString(getText(), x, y);
	}

	// overridden painting methods for specific button states
	@Override
	public void paintDisabledState(Graphics g) {
		if (useOriginalPainting)
			super.paintOriginalComponent(g);
		else {
			paintBackground(g, new Color(192, 192, 208));
			paintBottom(g);

			// draw text
			g.setFont(getFont());
			g.setColor(new Color(128, 128, 144));
			g.drawString(getText(), x, y);
		}
	}

	@Override
	public void paintArmedState(Graphics g) {
		if (useOriginalPainting)
			super.paintOriginalComponent(g);
		else {
			paintBackground(g, new Color(96, 96, 112));
			paintBottom(g);
			paintText(g);
		}
	}

	@Override
	public void paintRolloverState(Graphics g) {
		if (useOriginalPainting)
			super.paintOriginalComponent(g);
		else {
			paintBackground(g, new Color(160, 160, 176));
			paintBottom(g);
			paintText(g);
		}
	}

	@Override
	public void paintDefaultState(Graphics g) {
		if (useOriginalPainting)
			super.paintOriginalComponent(g);
		else {
			paintBackground(g, new Color(176, 176, 176));
			paintBottom(g);
			paintText(g);
		}
	}
}

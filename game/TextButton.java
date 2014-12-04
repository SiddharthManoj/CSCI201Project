/*
 * TextButton.java
 * James Zhang
 * December 21 2011
 * A button that is only text.
 */

package game;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import framework.GameButton;

public class TextButton extends GameButton {
	private static final long serialVersionUID = 1L;

	// the x-coordinate to draw the text at
	private int x;

	// the y-coordinate to draw the text at
	private int y;

	// constructor
	public TextButton() {}

	// public methods
	public void setText(FontMetrics metrics, String text) {
		// assign text
		super.setText(text);
		
		// center the text
		x = centerTextHorizontally(metrics, text, getWidth());
		y = centerTextVertically(metrics, getHeight());
	}

	// painting methods
	protected void paintBackgroundAndText(Graphics g, Color fontColor) {
		// fill background
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		// draw text
		g.setColor(fontColor);
		g.setFont(getFont());
		g.drawString(getText(), x, y);
	}

	// overridden painting methods for specific button states, each one using
	// a specified color for the text
	@Override
	public void paintDisabledState(Graphics g) {
		paintBackgroundAndText(g, new Color(128, 128, 144));
	}

	@Override
	public void paintArmedState(Graphics g) {
		paintBackgroundAndText(g, new Color(16, 32, 64));
	}

	@Override
	public void paintRolloverState(Graphics g) {
		paintBackgroundAndText(g, new Color(48, 96, 192));
	}

	@Override
	public void paintDefaultState(Graphics g) {
		paintBackgroundAndText(g, new Color(32, 64, 128));
	}
}

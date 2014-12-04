/*
 * GameButton.java
 * James Zhang
 * November 11 2011
 * A GameButton provides methods that make creating a custom button easier.
 */

package framework;

import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;

public abstract class GameButton extends JButton {
	private static final long serialVersionUID = 1L;

	// constructor
	public GameButton() {
		setBorder(BorderFactory.createEmptyBorder());
	}

	// these methods that draw the button must be implemented by the subclass
	public abstract void paintDisabledState(Graphics g);

	public abstract void paintArmedState(Graphics g);

	public abstract void paintRolloverState(Graphics g);

	public abstract void paintDefaultState(Graphics g);

	// paints the button according to its model
	@Override
	public void paintComponent(Graphics g) {
		ButtonModel model = getModel();

		if (!model.isEnabled())
			paintDisabledState(g);
		else if (model.isArmed())
			paintArmedState(g);
		else if (model.isRollover())
			paintRolloverState(g);
		else
			paintDefaultState(g);
	}

	// paints the button with its default painting
	public void paintOriginalComponent(Graphics g) {
		super.paintComponent(g);
	}

	// font centering methods
	public static int centerTextHorizontally(FontMetrics fm, String s, int w) {
		int stringWidth = fm.stringWidth(s);

		return (w - stringWidth) / 2;
	}
	
	public static int centerTextVertically(FontMetrics fm, int h) {
		int ascent = fm.getMaxAscent();
		int descent = fm.getMaxDescent();

		return (h - descent + ascent) / 2;
	}
}

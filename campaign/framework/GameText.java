/*
 * GameText.java
 * James Zhang
 * January 3 2012
 * Draws game text that scrolls in character by character, idea for displaying
 * game dialogue and instructions.
 */

package campaign.framework;

import game.Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameText {
	// takes 15 frames to fade in or fade out (15 * 17 = 255)
	// assuming the frames per second is 60, it takes 250 milliseconds to fade
	private static final int fadeRate = 17;

	// the maximum lines in one block
	private static final int maxLines = 5;

	// the list of blocks
	private ArrayList<String[]> blocks;

	// the current block index to show
	private int blockIndex;

	// new char shown every maxCounterFrame frames
	// assuming 60 frames per second, this means 30 characters per second
	private static final int maxTextCounter = 2;

	// the current text counter; when incremented to maxTextCounter, it will
	// increment curLen
	private int textCounter;

	// the current length of the current block
	private int curLen;

	// the maximum length of the current block, which is the sum of the
	// lengths of the lines in the current block
	private int maxLen;

	// a value from 0-255, where 0 is invisible and 255 is totally visible
	// any value in between means the game text is fading
	public int alpha;

	// how the alpha value is changing each update
	private int alphaChange;

	// the width of the text box
	private Rectangle box;

	// drawing constants
	private static final int sideBorder = 16;
	private static final int top = 16;
	private static Color white = new Color(255, 255, 255);
	private static Color black = new Color(0, 0, 0);

	// for clearer bottom right triangle / circle drawing
	private long offset;
	
	// call a method after finished
	ActionListener listener;

	// constructor
	public GameText(Rectangle box) {
		this.box = box;
		blocks = new ArrayList<String[]>();
		alpha = 0;
		alphaChange = 0;
		textCounter = 0;
		maxLen = 0;
		curLen = 0;
	}
	
	// adds a listener for when the game text is finished displaying text
	public void addFinishedListener(ActionListener listener) {
		this.listener = listener;
	}

	// sets the block index
	private void setBlock(int i) {
		blockIndex = i;
		maxLen = 0;
		curLen = 0;
		for (String line : blocks.get(i)) {
			maxLen += line.length();
		}
	}

	// calculates the length of the next string fragment
	private int calculate(String text) {
		int fixed = Game.metrics24.charWidth(' ');
		int maxLineWidth = (box.width - 2 * sideBorder - 16) / fixed;
		int len = 0;

		while (true) {
			// don't need to do anything if the entire string fits
			if (text.length() < maxLineWidth) {
				len = text.length();
				break;
			}

			// rough split with spaces
			int temp = text.indexOf(" ", len + 1);
			if (temp != -1 && temp < maxLineWidth)
				len = temp;
			else
				break;
		}

		return len;
	}

	// sets the text of a line of the current block
	private void setLine(int line, String text) {
		blocks.get(blocks.size() - 1)[line] = text;
	}

	// adds a block and initializes the strings in it. returns 0
	private int addBlock() {
		String[] block = new String[maxLines];

		for (int i = 0; i < block.length; i++)
			block[i] = new String("");

		blocks.add(block);
		return 0;
	}

	// splits a line of the text into one or more blocks
	private void split(String text) {
		// new block
		int i = addBlock(); // addBlock() returns 0

		while (text.length() != 0) {
			if (i == maxLines) // new block
				i = addBlock(); // addBlock() returns 0

			int len = calculate(text);

			String s = text.substring(0, len);
			s = s.trim();
			setLine(i, s);

			text = text.substring(len);
			++i;
		}
	}

	// animation methods
	public void setText(String text) {
		textCounter = 0;
		curLen = 0;
		blocks.clear();

		// separate the text into blocks (groups of 2-4 lines each)
		String[] lines = text.split("\n"); // \n splits a block

		for (String line : lines) {
			split(line);
		}

		setBlock(0);

		// fade in if necessary
		if (alpha == 0)
			alphaChange = fadeRate;
	}

	// call this method when a key is pressed
	public void keyPressed() {
		if (alpha == 255) {
			if (curLen != maxLen) { // fast forward
				curLen = maxLen;
			} else if (blockIndex >= blocks.size() - 1) { // end of blocks
				alphaChange = -fadeRate;
			} else { // next block
				setBlock(blockIndex + 1);
			}
		}
	}

	// this method should be called every update in the screen
	public void update(long gameTime) {
		// update the current length of the block
		++textCounter;
		if (textCounter == maxTextCounter) {
			textCounter = 0;
			if (curLen != maxLen) {
				++curLen;

				if (curLen == maxLen)
					offset = System.currentTimeMillis() % 1000;
			}
		}

		// update alpha if fading
		alpha += alphaChange;
		if (alpha > 255)
			alpha = 255;
		if (alpha < 0)
			alpha = 0;
		
		// call listener
		if (alphaChange < 0 && alpha == 0 && listener != null) {
			listener.actionPerformed(new ActionEvent(this, 0, "finished"));
			listener = null;
		}
	}

	// draws depending on mode
	public void paint(Graphics g) {
		g.translate(box.x, box.y);

		// draw box background
		g.setColor(new Color(black.getRed(), black.getGreen(), black.getBlue(),
				alpha));
		g.fillRect(0, 0, box.width, box.height);

		// draw box outline
		g.setColor(new Color(white.getRed(), white.getGreen(), white.getBlue(),
				alpha));
		g.drawRect(0, 0, box.width, box.height);

		if (alpha == 0) {
			// box already drawn above
			// but it is invisible
		} else if (alpha == 255) { // draw the text
			// box already drawn above
			drawText(g);
			drawSign(g);
		} else if (alphaChange > 0) { // draw fade in (no text)
			// box already drawn above
		} else if (alphaChange < 0) { // draw fade out (no text)
			// box already drawn above
			drawText(g);
		}

		g.translate(-box.x, -box.y);
	}

	// draws box with text
	private void drawText(Graphics g) {
		Font f = Game.font24;
		FontMetrics fm = Game.metrics24;
		g.setFont(f);

		// the lines to draw
		ArrayList<String> draw = new ArrayList<String>();

		// calculate lines to draw
		int len = curLen;
		String[] lines = blocks.get(blockIndex);
		for (int i = 0; i < lines.length; i++) {
			if (len >= lines[i].length()) {
				len -= lines[i].length();
				draw.add(lines[i]);
			} else if (len > 0) {
				draw.add(lines[i].substring(0, len));
				len = 0;
			}
		}

		// draw text
		int y0 = top - fm.getDescent() + fm.getAscent();
		int dy = fm.getHeight() + 8;
		for (int i = 0; i < draw.size(); i++) {
			String s = draw.get(i);
			int y = y0 + i * dy;

			g.drawString(s, sideBorder, y);
		}
	}

	// draws a tab sign if the text is all shown, telling the user to
	// press it to go to the next one
	private void drawSign(Graphics g) {
		if (maxLen == curLen) {
			// if i < 500, then don't draw anything
			// if i >= 500, then draw something
			long i = (System.currentTimeMillis() - offset) % 1000;
			int w = box.width, h = box.height;
			g.setColor(white);

			if (i >= 500) {
				g.fillRect(w - 64, h - 32, 56, 24);
				
				g.setColor(Color.black);
				g.setFont(new Font("Consolas", Font.BOLD, 24));
				g.drawString("TAB", w - 56, h - 13);
			}
		}
	}
}

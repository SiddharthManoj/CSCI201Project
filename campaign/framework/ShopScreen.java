/*
 * ShopScreen.java
 * James Zhang
 * January 21 2012
 * The menu screen that pops up when the user is playing the game and presses
 * the escape button. This class has static variables for ease of access.
 */

package campaign.framework;

import framework.GameWindow;
import game.Game;
import game.MenuButton;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import campaign.framework.LevelScreen;

public class ShopScreen extends LevelScreen implements ActionListener {
	private static final long serialVersionUID = 1L;

	// controls
	private MenuButton[] buttons = new MenuButton[5];

	// the background image for the paused level screen
	private BufferedImage background;

	// the previous screen
	private String previousScreen = "";
	private LevelScreen level;

	// the shop class
	private Shop shop;

	// values for the menu box
	int left = 250, top = 228;
	int w = 300, h = 48;

	// constructor
	public ShopScreen(GameWindow parent, String name, Shop shop,
			LevelScreen level, String previousScreen) {
		super(parent, name);
		
		// set variables
		this.previousScreen = previousScreen;
		this.level = level;
		this.shop = shop;

		// add buttons
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new MenuButton();

			buttons[i].setFont(Game.font16);
			buttons[i].setSize(w, h);
			buttons[i].setActionCommand(Integer.toString(i));
			buttons[i].setLocation(left, top + i * h);

			if (i == 0)
				buttons[i].setRound(0);
			else if (i == buttons.length - 1)
				buttons[i].setRound(2);
			else
				buttons[i].setRound(1);

			buttons[i].addActionListener(this);
			add(buttons[i]);
		}

		// the last button is to leave the shop
		buttons[buttons.length - 1].setText(Game.metrics16, "leave shop");
	}

	// sets the background of the image. this background image must be
	// processed so the image does not have to be processed during painting
	public void setBackgroundImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		// copy the image
		background = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = background.getGraphics();
		g.drawImage(image, 0, 0, null);

		// draw the darken effect onto the image
		g.setColor(new Color(128, 128, 128, 128));
		g.fillRect(0, 0, width, height);
	}

	// refreshes the buttons
	public void refresh() {
		for (int i = 0; i < 4; i++) {
			buttons[i].setText(Game.metrics16, shop.getItem(i));
			buttons[i].setEnabled(shop.canBuy(i));
		}
	}

	// unused updating method
	@Override
	public void update(long gameTime) {}

	// repaints the shop screen
	@Override
	public void repaint(Graphics g) {
		g.drawImage(background, 0, 0, null);
		Game.paintBox(g, left - 1, top - 1, w + 1, h * buttons.length + 1);
	}

	// called when a button is pressed
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String c = arg0.getActionCommand();
		int index = c.charAt(0) - '0';
		
		if (index == buttons.length - 1) {
			setScreen(previousScreen);
			return;
		}

		if (shop.canBuy(index))
			shop.buy(index);

		refresh();
		setBackgroundImage(level.getImage());
	}
}

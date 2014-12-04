/*
 * Bullet.java
 * James Zhang
 * Represents a bullet in the game.
 */

package campaign.framework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class Bullet {
	// bullet position variables
	public int x, y;
	
	// bullet velocity variables
	private int vy, vx;
	
	// the source of the bullet
	public String source;
	
	// how much damage the bullet does
	public int damage;
	
	// the color of the bullet (0 or 1)
	public int color;

	// the image of the bullet
	private static BufferedImage[] image;

	// loads the bullet images
	public static void load() {
		image = new BufferedImage[2];

		// create red bullet
		image[0] = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image[0].getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.red);
		g.fillOval(0, 0, 8, 8);
		
		// create blue bullet
		image[1] = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) image[1].getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(48, 96, 192));
		g.fillOval(0, 0, 8, 8);
	}

	// constructor
	public Bullet(int x, int y, int vx, int vy) {
		color = 0;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
	}

	// updates the position of the bullet
	public void update() {
		x += vx;
		y += vy;
	}

	// returns whether the bullet will hit a 32x32 in the next frame
	public boolean hitTest(int x, int y) {
		return hitTest(x, y, 32, 32);
	}

	// returns whether the bullet will hit a rectangle in the next frame
	public boolean hitTest(int x, int y, int w, int h) {
		Line2D.Double line = new Line2D.Double(this.x, this.y, this.x + vx,
				this.y + vy);
		Rectangle r = new Rectangle(x - 5, y - 5, w + 9, h + 9);
		return r.intersectsLine(line);
	}

	// returns whether the bullet is off the map (so it can be removed)
	public boolean offMap(int w, int h) {
		return (x < 0 || y < 0 || x >= w || y >= h);
	}

	// paints the bullet
	public void paint(Graphics g) {
		g.drawImage(image[color], x, y, null);
	}
}

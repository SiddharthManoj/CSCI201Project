/*
 * SideScrollerEntity.java
 * James Zhang
 * April 27 2012
 * Represents an entity in an side-scrolling game.
 */

package campaign.framework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class SideScrollerEntity {
	// health variables
	private int currentHealth, totalHealth;

	// position variables
	private int x, y;
	private int vx, vy;

	// turning variables
	private int heading;

	// movement variables
	private int animationFrame;

	// shooting variables
	private long lastShootTime;
	private long shootInterval;

	// painting variables
	// should be 4 by 16
	private BufferedImage[][] images;
	private int width, height;

	// constructors
	// default constructor
	public SideScrollerEntity(int x, int y, int hp, int shootInterval, BufferedImage[][] images, int width, int height) {
		this.x = x;
		this.y = y;
		currentHealth = totalHealth = hp;
		
		this.shootInterval = shootInterval;
		this.images = images;
		this.width = width;
		this.height = height;
		
		vx = vy = 0;
		lastShootTime = 0;
	}
	
	// copy constructor
	public SideScrollerEntity(SideScrollerEntity clone) {
		// assign values
		this.totalHealth = currentHealth = clone.totalHealth;
		this.x = clone.x;
		this.y = clone.y;
		this.shootInterval = clone.shootInterval;
		this.images = clone.images;
		this.width = clone.width;
		this.height = clone.height;
		
		// default values
		vx = vy = 0;
		lastShootTime = 0;
	}
	
	// helper method for importing images
	// flips an image from right to left
	public static BufferedImage flip(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage r = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = r.createGraphics();
		g2d.drawImage (img, w, 0, 0, h, 0, 0, w, h, null);
		return r;
	}
	
	// resizes an image
	public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {  
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage result = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, newWidth, newHeight, 0, 0, w, h, null);
        g.dispose();
        return result;
    }

	// health properties and methods
	public boolean subtractHealth(int damage) {
		currentHealth -= damage;
		if (currentHealth < 0)
			currentHealth = 0;
		return (currentHealth == 0);
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int value) {
		currentHealth = value;
	}

	public int getTotalHealth() {
		return totalHealth;
	}

	public void setTotalHealth(int value) {
		totalHealth = value;
	}
	
	// location properties
	public int getRow() {
		return y / 32;
	}

	public int getCol() {
		return x / 32;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void setX(int value) {
		x = value;
	}

	public void setY(int value) {
		y = value;
	}
	
	// velocity properties
	public int getVx() {
		return vx;
	}

	public int getVy() {
		return vy;
	}
	public void setVx(int value) {
		vx = value;
	}
	
	public void setVy(int value) {
		vy = value;
	}
	
	// size properties
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	// heading properties
	public int getHeading() {
		return heading;
	}
	
	public void setHeading(int value) {
		heading = value;
	}
	
	// no movement methods; let the map process movement
	
	// shooting methods
	public boolean canShoot() {
		return (System.currentTimeMillis() >= lastShootTime + shootInterval);
	}
	
	// returns a bullet that is shot from the entity
	public Bullet shoot(long time, int velocity, int offset) {
		lastShootTime = time;
		
		int x = getX();
		int y = getY() + height / 2 + offset - 4;
		int vx = -velocity;
		int vy = 0;
		
		if (heading == 0) {
			x += width;
			vx = velocity;
		}
		
		return new Bullet(x, y, vx, vy);
	}
	
	// update the animation frame
	public void update(int pixelsUnder) {
		if (pixelsUnder != 0)
			animationFrame = 0;
		else if (vx == 0)
			animationFrame = 0;
		else {
			++animationFrame;
			animationFrame %= 16;
		}
	}
	
	// painting methods
	// draws the default entity image with health bar
	public void paint(Graphics g, int offset) {
		g.drawImage(images[heading][0], x, y, null);
		paintHealthBar(g, offset);
	}
	
	// draws an entity with animations and health bar
	public void paint(Graphics g, int pixelsUnder, int offset) {
		// get the correct image to draw
		BufferedImage image = images[heading][animationFrame / 4];
		
		// use jumping image if jumping
		if (pixelsUnder != 0)
			image = images[2 + heading][0];
		
		// draw image and health bar
		g.drawImage(image, x, y, null);
		paintHealthBar(g, offset);
	}
	
	// draws a health bar above the entity
	private void paintHealthBar(Graphics g, int offset) {
		g.translate(0, -4 + offset);

		int width = 32;
		
		// background
		g.setColor(Color.black);
		g.fillRect(x, y, width, 8);
		
		// health fill
		g.setColor(Color.red);
		int fill = (int) ((double) currentHealth / totalHealth * width);
		g.fillRect(x + 1, y + 1, fill - 2, 8 - 2);

		g.translate(0, 4 - offset);
	}
}

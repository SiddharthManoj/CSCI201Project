/*
 * James Zhang
 * April 27 2012
 * Represents an entity in an RPG-style game.
 */

package campaign.framework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class RpgEntity {
	// constants
	private int MOVEMENT_FRAME_COUNT = 16;
	private int MOVEMENT_INCREMENT = 32 / MOVEMENT_FRAME_COUNT;
	private static final long TURN_WAIT_TIME = 96;

	// health variables
	private int currentHealth, totalHealth;

	// position variables
	private int x, y;
	private int vx, vy;
	private int previousX, previousY;

	// turning variables
	private long lastTurnTime;
	private int heading;

	// movement variables
	private int movementFrame;
	private long lastMoveTime;
	private int animationFrame;

	// shooting variables
	private long lastShootTime;
	private long shootInterval;

	// painting variables
	// should be 4 by 16
	private BufferedImage[][] images;
	private int width, height;

	// constructors
	public RpgEntity(int x, int y, int totalHealth, long shootInterval,
			BufferedImage[][] images, int width, int height) {
		// assign values
		this.totalHealth = currentHealth = totalHealth;
		this.x = previousX = x;
		this.y = previousY = y;
		this.shootInterval = shootInterval;
		this.images = images;
		
		this.width = width;
		this.height = height;

		// default values
		vx = vy = 0;
		lastTurnTime = System.currentTimeMillis();
		lastMoveTime = 0;
		lastShootTime = 0;
	}
	
	public RpgEntity(RpgEntity clone) {
		// assign values
		this.totalHealth = currentHealth = clone.totalHealth;
		this.x = previousX = clone.x;
		this.y = previousY = clone.y;
		this.shootInterval = clone.shootInterval;
		this.images = clone.images;
		this.width = clone.width;
		this.height = clone.height;
		
		// default values
		vx = vy = 0;
		lastTurnTime = System.currentTimeMillis();
		lastMoveTime = 0;
		lastShootTime = 0;
	}

	// helper methods
	// rotates an image a multiple of 90 degrees clockwise
	public static BufferedImage rotateClockwise(BufferedImage img, int rotations) {
		int w = img.getWidth();
		int h = img.getHeight();

		BufferedImage r = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = r.createGraphics();

		g2d.rotate(Math.PI / 2 * rotations, (double) w / 2, (double) h / 2);
		g2d.drawImage(img, 0, 0, null);
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
	
	// flips an image from left to right
	public static BufferedImage flipHorizontally(BufferedImage image) {  
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage result = new BufferedImage(w, h, image.getType());
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();
        return result;
    }

	// health methods
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

	// getter and setter methods
	public int getRow() {
		return previousY;
	}

	public int getCol() {
		return previousX;
	}

	public int getX() {
		int xOffset = vx * movementFrame * MOVEMENT_INCREMENT;
		return (previousX * 32 + xOffset);
	}

	public int getY() {
		int yOffset = vy * movementFrame * MOVEMENT_INCREMENT;
		return (previousY * 32 + yOffset);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getHeading() {
		return heading;
	}
	
	public void setHeading(int value) {
		heading = value;
	}

	// turning methods
	public void turn(int dx, int dy) {
		long cur = System.currentTimeMillis();

		if (dy == -1 && heading != Heading.UP)
			heading = Heading.UP;
		else if (dx == 1 && heading != Heading.RIGHT)
			heading = Heading.RIGHT;
		else if (dy == 1 && heading != Heading.DOWN)
			heading = Heading.DOWN;
		else if (dx == -1 && heading != Heading.LEFT)
			heading = Heading.LEFT;
		else
			return;

		// wait for a while before moving after a turn
		lastTurnTime = cur;
	}

	// movement methods
	public boolean isMoving() {
		return (vx != 0 || vy != 0);
	}

	public void move(int direction) {
		if (!isMoving()) {
			// reset velocity
			vx = vy = 0;

			// assign new velocity
			switch (direction) {
				case Heading.UP:
					vy = -1;
					break;
				case Heading.RIGHT:
					vx = 1;
					break;
				case Heading.DOWN:
					vy = 1;
					break;
				case Heading.LEFT:
					vx = -1;
					break;
			}

			long cur = System.currentTimeMillis();

			// wait before moving after a turn, unless already moving
			if (cur >= lastTurnTime + TURN_WAIT_TIME
					|| cur < lastMoveTime + TURN_WAIT_TIME){
				x += vx;
				y += vy;
				movementFrame = 0;
			} else {
				// toggle the comments if a turn is needed
				// vx = vy = 0;
				x += vx;
				y += vy;
				movementFrame = 0;
			}
			
		}
	}

	// shooting methods
	public boolean canShoot(long gameTime) {
		return (gameTime >= lastShootTime + shootInterval);
	}

	public Bullet shoot(long gameTime, int velocity) {
		// calculate the position of the bullet
		int x = getX(), y = getY();
		int centerX = x + width / 2 - 4, centerY = y + height / 2 - 4;
		
		int bulletX = 0, bulletY = 0;
		int bulletVx = 0, bulletVy = 0;

		switch (heading) {
			case 0:
				bulletX = centerX;
				bulletY = y;
				bulletVy = -velocity;
				break;
			case 1:
				bulletX = x + width;
				bulletY = centerY;
				bulletVx = velocity;
				break;
			case 2:
				bulletX = centerX;
				bulletY = y + height;
				bulletVy = velocity;
				break;
			case 3:
				bulletX = x;
				bulletY = centerY;
				bulletVx = -velocity;
				break;
		}
		
		lastShootTime = gameTime;
		return new Bullet(bulletX, bulletY, bulletVx, bulletVy);
	}

	// painting method
	public void paint(Graphics g) {
		BufferedImage image;
		if (isMoving() && movementFrame < images[0].length)
			image = images[heading][animationFrame / 2];
		else
			image = images[heading][0];
		
		// draw the image
		g.drawImage(image, getX(), getY(), null);
		
		g.translate(0, -4);

		// draw the health bar
		g.setColor(Color.black);
		g.fillRect(getX(), getY(), width, 8);
		
		// draw the fill
		g.setColor(Color.red);
		int length = (int) ((double) currentHealth / totalHealth * width);
		g.fillRect(getX() + 1, getY() + 1, length - 2, 8 - 2);

		g.translate(0, 4);
	}

	// updating method
	public void update() {
		if (isMoving()) {
			++movementFrame;
			++animationFrame;
			animationFrame %= 32;

			// finished moving
			if (movementFrame == MOVEMENT_FRAME_COUNT) {
				previousX = x;
				previousY = y;
				vx = vy = 0;
				movementFrame = 0;
				lastMoveTime = System.currentTimeMillis();
			}
		}
	}
}

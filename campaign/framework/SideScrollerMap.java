/*
 * SideScrollerMap.java
 * James Zhang
 * April 30 2012
 * Represents a map used in a side scrolling game.
 */

package campaign.framework;

import game.Game;
import game.MusicPlayer;
import game.Question;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SideScrollerMap {
	// resources
	private BufferedImage[] mapTiles;
	private BufferedImage shopTile;

	// references
	private LevelScreen parent;
	private LevelListener listener;

	// map variables
	private int[][] map = new int[0][0];
	int acceleration = 1;
	int pixelsUnder = 0;

	// camera variables
	private int x, y;

	// entity and bullet variables
	private SideScrollerEntity hero;
	private SideScrollerEntity boss;
	private ArrayList<SideScrollerEntity> enemies;
	private ArrayList<Bullet> bullets;

	// shop variables
	private int shopX, shopY;
	private int shopWidth, shopHeight;
	private int heroBulletSpeed = 0;
	private int enemyBulletSpeed = 0;
	private int gold;
	private static int bossBulletSpeed = 8;

	// other variables
	// used for a red overlay when the player is hit
	private long red = 0;
	private boolean questionAsked = false;

	// triggers
	private boolean nextLevel;
	private boolean restartLevel;
	private boolean bossSighted;
	private boolean bossKilled;
	private boolean askedFinalQuestions;
	private Rectangle bossSighting;

	// "static" constructor
	public SideScrollerMap(LevelScreen parent, LevelListener listener,
			SideScrollerEntity hero, SideScrollerEntity boss,
			ArrayList<SideScrollerEntity> enemies, int x, int y,
			Rectangle sighting) {
		softReset(parent, listener, boss, boss, enemies, x, y, sighting);
	}

	// "class" constructor
	public void softReset(LevelScreen parent, LevelListener listener,
			SideScrollerEntity hero, SideScrollerEntity boss,
			ArrayList<SideScrollerEntity> enemies, int x, int y,
			Rectangle sighting) {
		// references
		this.parent = parent;
		this.listener = listener;

		// entity and bullet variables
		this.hero = hero;
		this.boss = boss;
		this.enemies = enemies;
		bullets = new ArrayList<Bullet>();

		// camera variables
		this.x = x;
		this.y = y;

		// reset triggers
		nextLevel = false;
		restartLevel = false;
		bossSighted = false;
		bossKilled = false;
		askedFinalQuestions = false;
		this.bossSighting = sighting;

		if (map != null && map.length != 0)
			updateCamera();
	}

	// loading methods
	public void loadResources(BufferedImage[] tiles, int[][] map,
			BufferedImage shopTile, int shopX, int shopY) {
		this.mapTiles = tiles;
		this.map = map;

		this.shopTile = shopTile;
		this.shopX = shopX;
		this.shopY = shopY;
		this.shopWidth = 96;
		this.shopHeight = 96;
	}

	// assigns hero and enemy bullet speeds
	public void setBulletSpeeds(int heroBulletSpeed, int enemyBulletSpeed) {
		this.heroBulletSpeed = heroBulletSpeed;
		this.enemyBulletSpeed = enemyBulletSpeed;
	}

	// helper methods
	// darkens an image for night
	public static void darken(BufferedImage image) {
		Graphics g = image.getGraphics();
		int w = image.getWidth(), h = image.getHeight();
		g.setColor(new Color(0, 0, 128, 128));
		g.fillRect(0, 0, w, h);
	}

	// returns the hero
	public SideScrollerEntity getHero() {
		return hero;
	}

	// bullet speed properties
	public int getHeroBulletSpeed() {
		return heroBulletSpeed;
	}

	public void setHeroBulletSpeed(int value) {
		heroBulletSpeed = value;
	}

	public int getEnemyBulletSpeed() {
		return enemyBulletSpeed;
	}

	public void setEnemyBulletSpeed(int value) {
		enemyBulletSpeed = value;
	}

	// gold methods
	public int getGold() {
		return gold;
	}

	public void subtractGold(int value) {
		gold -= value;
	}

	// hero movement methods
	private boolean unwalkable(int x, int y) {
		return (map[y][x] == 1);
	}

	// checks whether a tile can be moved onto
	private boolean checkTile(int x, int y, boolean value) {
		if (x < 0 || y < 0 || x >= map[0].length || y >= map.length)
			value = false;
		else if (unwalkable(x, y))
			value = false;

		return value;
	}

	// calculates the pixels in one direction the hero may move
	private int calcPx(int dx, int dy) {
		int x = hero.getX(), y = hero.getY();
		int result = 0;
		int xLoc = x / 32, yLoc = y / 32;
		int modX = x % 32, modY = y % 32;
		int cX = (dx == 1 ? 2 : dx);
		int cY = (dy == 1 ? 2 : dy);
		boolean next = true;

		// add remainder
		result += (-dx * modX + -dy * modY);
		if (dx + dy == 1)
			result = (32 + result) % 32;

		// lava check
		if (modY != 0) {
			if (map[yLoc + 2][xLoc] == 2)
				heroDamaged(hero.getCurrentHealth());
		}

		// check next block
		// check additional block if the movement covers 2 blocks
		if (dx != 0) {
			next = checkTile(xLoc + dx, yLoc, next);
			next = checkTile(xLoc + dx, yLoc + 1, next);
			if (modX != 0) {
				next = checkTile(xLoc + cX, yLoc, next);
				next = checkTile(xLoc + cX, yLoc + 1, next);
			}
			if (modY != 0) {
				next = checkTile(xLoc + dx, yLoc + 2, next);
			}
		}

		if (dy != 0) {
			next = checkTile(xLoc, yLoc + dy, next);
			next = checkTile(xLoc, yLoc + cY, next);
			if (modY != 0)
				next = checkTile(xLoc, yLoc + cY + 1, next);
			if (modX != 0) {
				next = checkTile(xLoc + 1, yLoc + dy, next);
				next = checkTile(xLoc + 1, yLoc + cY, next);
				if (modY != 0 && dy == 1) {
					next = checkTile(xLoc + 1, yLoc + 3, next);
				}
			}
		}

		if (next)
			result += 32;

		return result;
	}

	// moves the hero horizontally
	private void moveX() {
		int hVx = hero.getVx();

		if (hVx == 0)
			return;

		int move = hVx;
		int maxPx = calcPx(hVx / Math.abs(hVx), 0);
		if (Math.abs(move) > maxPx)
			move = maxPx * (move / Math.abs(move));

		hero.setX(hero.getX() + move);
	}

	// moves the hero vertically
	private void moveY() {
		int hVy = hero.getVy();

		if (hVy == 0)
			return;

		int move = hVy;
		int maxPx = calcPx(0, hVy / Math.abs(hVy));
		if (Math.abs(move) > maxPx)
			move = maxPx * (move / Math.abs(move));

		hero.setY(hero.getY() + move);
		if (move == 0)
			hero.setVy(0);
	}

	// accelerates the hero due to gravity
	private void accelerate() {
		int hVy = hero.getVy();

		if (hVy < 0 || (pixelsUnder != 0))
			hVy += acceleration;

		if (hVy > 16)
			hVy = 16;

		hero.setVy(hVy);
	}

	// whether an entity can see the hero currently or if it turns
	public boolean seesHero(SideScrollerEntity e) {
		int hX = hero.getCol(), hY = hero.getRow();
		int eX = e.getCol(), eY = e.getRow();

		return (Math.abs(hY - eY) <= 1 && Math.abs(hX - eX) < 10);
	}

	// entity damaged events
	private void heroDamaged(int damage) {
		if (hero.getCurrentHealth() == 0)
			return;

		if (hero.subtractHealth(damage)) {
			listener.heroDied();
			parent.gameText.update(parent.getGameTime());
			restartLevel = true;
		}

		red = System.currentTimeMillis() + 100;
	}

	private void enemyDamaged(SideScrollerEntity enemy, int damage) {
		if (enemy.getCurrentHealth() == 0)
			return;

		if (enemy.subtractHealth(damage)) {
			QuestionScreen.importQuestion(Question.next());
			QuestionScreen.prev = parent.getName();
			QuestionScreen.setBackgroundImage(parent.getImage());
			parent.setScreen("questionScreen");
			questionAsked = true;
		}
	}

	private void bossDamaged(int damage) {
		if (bossKilled)
			return;

		if (boss.subtractHealth(damage)) {
			listener.bossKilled();
			parent.gameText.update(parent.getGameTime());
			bossKilled = true;
		}
	}

	// entity shooting methods
	private void heroShoot() {
		long cur = System.currentTimeMillis();
		if (hero.canShoot()) {
			Bullet b = hero.shoot(cur, heroBulletSpeed, 16);
			b.damage = 1;
			b.source = "player";
			bullets.add(b);
			MusicPlayer.playEffect("bullet");
		}
	}

	private void enemyShoot(SideScrollerEntity e) {
		long cur = System.currentTimeMillis();
		if (seesHero(e)) {
			if (hero.getX() < e.getX())
				e.setHeading(1);
			else
				e.setHeading(0);

			if (e.canShoot()) {
				Bullet b = e.shoot(cur, enemyBulletSpeed, 0);
				b.damage = 1;
				b.source = "enemy";
				bullets.add(b);
			}
		}
	}

	private void bossShoot() {
		long cur = System.currentTimeMillis();
		if (seesHero(boss)) {
			if (hero.getX() < boss.getX())
				boss.setHeading(1);
			else
				boss.setHeading(0);

			if (boss.canShoot()) {
				// boss shoots 3 bullets
				for (int i = 0; i < 3; i++) {
					Bullet b = boss.shoot(cur, bossBulletSpeed, 0);
					b.damage = 2;
					b.source = "enemy";
					b.color = 1;

					// space the bullets out
					for (int j = 0; j < i; j++) {
						b.update();
						b.update();
					}
					bullets.add(b);
				}
			}
		}
	}

	// shop method
	public void mouseClicked(int x, int y) {
		if (parent.gameText.alpha != 0)
			return;

		x = x - (this.x + shopX * 32);
		y = y - (this.y + shopY * 32);
		if (x >= 0 && x < shopWidth && y >= 0 && y < shopHeight) {
			listener.shopClicked();
		}
	}

	// updating method
	public void update() {
		if (parent.gameText.alpha != 0)
			return;

		processTriggers();
		updateHero();
		updateEnemies();
		updateBullets();
		updateCamera();
	}

	// processes triggers for the level, which are mostly text
	private void processTriggers() {
		if (restartLevel) {
			listener.restartLevel();
			return;
		}

		// process enemy death questions
		if (questionAsked && QuestionScreen.flag == 0) {
			questionAsked = false;

			if (QuestionScreen.answer == QuestionScreen.firstResponse)
				gold += 10;
			else
				gold += 5;

			if (gold > 99)
				gold = 99;
		}

		if (!bossSighted && bossSighting.contains(hero.getX(), hero.getY())) {
			bossSighted = true;
			listener.bossSighted();
			parent.gameText.update(parent.getGameTime());
		}

		if (bossKilled && !askedFinalQuestions) {
			askedFinalQuestions = true;
			QuestionScreen.assignQuestionCount(3);
			QuestionScreen.importQuestion(Question.next());
			QuestionScreen.prev = parent.getName();
			QuestionScreen.setBackgroundImage(parent.getImage());
			parent.setScreen("questionScreen");
			return;
		}

		if (askedFinalQuestions && !nextLevel) {
			nextLevel = true;
			listener.nextLevelText();
			parent.gameText.update(parent.getGameTime());
			return;
		}

		if (nextLevel)
			listener.nextLevel();
	}

	// moves the hero and makes the hero shoot if needed
	private void updateHero() {
		// updates hero location and velocity
		pixelsUnder = calcPx(0, 1);
		moveX();
		moveY();
		accelerate();

		int sidewaysSpeed = 2, jumpVelocity = 13;
		hero.setVx(0);

		// process keys
		if (parent.isKeyPressed(KeyEvent.VK_W)
				|| parent.isKeyPressed(KeyEvent.VK_UP)) {
			if (hero.getVy() == 0 && pixelsUnder == 0)
				hero.setVy(-jumpVelocity);
		}

		if (parent.isKeyPressed(KeyEvent.VK_A)
				|| parent.isKeyPressed(KeyEvent.VK_LEFT)) {
			hero.setVx(-sidewaysSpeed);
			hero.setHeading(1);
		} else if (parent.isKeyPressed(KeyEvent.VK_D)
				|| parent.isKeyPressed(KeyEvent.VK_RIGHT)) {
			hero.setVx(sidewaysSpeed);
			hero.setHeading(0);
		}

		// shoot
		if (parent.isKeyPressed(KeyEvent.VK_SPACE))
			heroShoot();

		hero.update(pixelsUnder);
	}

	// update the enemies and boss
	private void updateEnemies() {
		// random enemy movement?
		for (SideScrollerEntity e : enemies)
			if (e.getCurrentHealth() != 0)
				enemyShoot(e);

		if (boss.getCurrentHealth() != 0)
			bossShoot();
	}

	// move the bullets and check for collisions
	private void updateBullets() {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			b.update();
			boolean rem = false;
			if (b.offMap(map[0].length * 32, map.length * 32))
				rem = true;
			else if (b.source.equals("enemy")) {
				if (b.hitTest(hero.getX(), hero.getY(), 32, 64)) {
					heroDamaged(b.damage);
					rem = true;
				}
			} else if (b.source.equals("player")) {
				for (SideScrollerEntity e : enemies) {
					if (e.getCurrentHealth() == 0)
						continue;
					if (b.hitTest(e.getX(), e.getY(), 32, 32)) {
						enemyDamaged(e, b.damage);
						rem = true;
					}
				}
				if (!rem) {
					if (boss.getCurrentHealth() != 0)
						if (b.hitTest(boss.getX(), boss.getY(), 32, 32)) {
							bossDamaged(b.damage);
							rem = true;
						}
				}
			}

			if (rem == false) {
				int x = b.x / 32, y = b.y / 32;
				for (int j = 0; j <= 1; j++)
					for (int k = 0; k <= 1; k++) {
						if (map[y + j][x + k] == 1
								&& b.hitTest((x + k) * 32, (y + j) * 32, 32, 32)) {
							rem = true;
						}
					}
			}

			if (rem) {
				bullets.remove(i);
				i--;
			}
		}
	}

	// moves the camera
	private void updateCamera() {
		// move camera along with hero
		int rx = x + hero.getX(), ry = y + hero.getY();
		int borderX = 350, borderY = 250;

		if (rx < borderX)
			x = borderX - hero.getX();
		else if (rx > 800 - borderX)
			x -= (rx - (800 - borderX));

		if (ry < borderY)
			y = borderY - hero.getY();
		else if (ry > 600 - borderY)
			y -= (ry - (600 - borderY));

		// bound the camera so it doesn't show blank space
		if (x > 0)
			x = 0;
		if (y > 0)
			y = 0;
		if (x < -32 * map[0].length + 800)
			x = -32 * map[0].length + 800;
		if (y < -32 * map.length + 600)
			y = -32 * map.length + 600;
	}

	// draws the map
	public void paint(Graphics g) {
		// map
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map[row].length; col++) {
				// process where the map is currently and where to paint
				int mapx = 32 * col, mapy = 32 * row;
				int px = mapx + x, py = mapy + y;

				// make sure it's worth painting it
				if (px > -32 && px < Game.WIDTH && py > -32 && py < Game.HEIGHT) {
					int id = map[row][col];
					g.drawImage(mapTiles[id], px, py, null);
				}
			}
		}

		g.translate(x, y);

		// draw shop
		g.drawImage(shopTile, 32 * shopX, 32 * shopY, null);

		// hero
		if (hero.getCurrentHealth() > 0)
			hero.paint(g, 12);

		// entities
		for (SideScrollerEntity e : enemies)
			if (e.getCurrentHealth() != 0)
				e.paint(g, 0);
		for (Bullet b : bullets)
			b.paint(g);
		if (boss.getCurrentHealth() != 0)
			boss.paint(g, 0);

		g.translate(-x, -y);

		// draw the amount of gold the player has
		g.setFont(Game.font24);
		g.setColor(Color.yellow);
		g.drawString("Gold: " + gold, 800 - 120, 24);

		// paint a read screen if hit
		if (System.currentTimeMillis() < red) {
			g.setColor(new Color(255, 0, 0, 64));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		}
	}
}

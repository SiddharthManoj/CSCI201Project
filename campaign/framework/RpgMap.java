/*
 * RpgMap.java
 * James Zhang
 * April 30 2012
 * Represents a map for a role playing game.
 */

package campaign.framework;

import game.Game;
import game.MusicPlayer;
import game.Question;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RpgMap {
	// resources
	private BufferedImage[] mapTiles;
	private BufferedImage shopTile;

	// references
	private LevelScreen parent;
	private LevelListener listener;

	// map variables
	private int[][] map = new int[0][0];
	private boolean[] unwalkableTiles = new boolean[0];
	private boolean[] wallTiles = new boolean[0];

	// camera variables
	private int x, y;

	// entity and bullet variables
	private RpgEntity hero;
	private RpgEntity boss;
	private ArrayList<RpgEntity> enemies;
	private ArrayList<Bullet> bullets;

	// shop variables
	private int shopX, shopY;
	private int shopWidth, shopHeight;
	private int heroBulletSpeed = 0;
	private int enemyBulletSpeed = 0;
	private int gold;

	// other variables
	private long red = 0; // used for a red overlay when the player is hit
	private boolean questionAsked = false;

	// triggers
	private boolean nextLevel;
	private boolean restartLevel;
	private boolean bossSighted;
	private boolean bossKilled;
	private boolean askedFinalQuestions;

	// constructor
	public RpgMap(LevelScreen parent, LevelListener listener, RpgEntity hero,
			RpgEntity boss, ArrayList<RpgEntity> enemies, int x, int y) {
		softReset(parent, listener, boss, boss, enemies, x, y);
	}

	// resets the map
	public void softReset(LevelScreen parent, LevelListener listener,
			RpgEntity hero, RpgEntity boss, ArrayList<RpgEntity> enemies,
			int x, int y) {
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

		// reset variables
		gold = 0;

		// reset triggers
		nextLevel = false;
		restartLevel = false;
		bossSighted = false;
		bossKilled = false;
		askedFinalQuestions = false;
	}

	// loaders
	public void loadResources(BufferedImage[] tiles, BufferedImage shopTile,
			int shopX, int shopY, int[][] map, int[] unwalkableTiles,
			int[] wallTiles) {
		this.mapTiles = tiles;
		this.map = map;
		
		// load the tile properties
		this.unwalkableTiles = new boolean[4];
		for (int i = 0; i < unwalkableTiles.length; i++)
			this.unwalkableTiles[unwalkableTiles[i]] = true;

		this.wallTiles = new boolean[4];
		for (int i = 0; i < wallTiles.length; i++)
			this.wallTiles[wallTiles[i]] = true;

		// shop
		this.shopTile = shopTile;
		this.shopX = shopX;
		this.shopY = shopY;
		this.shopWidth = shopTile.getWidth();
		this.shopHeight = shopTile.getHeight();
	}

	// sets the hero and enemy bullet speeds
	public void setBulletSpeeds(int heroBulletSpeed, int enemyBulletSpeed) {
		this.heroBulletSpeed = heroBulletSpeed;
		this.enemyBulletSpeed = enemyBulletSpeed;
	}

	// helper methods
	// darkens the image for night
	public static void darken(BufferedImage image) {
		Graphics g = image.getGraphics();
		int w = image.getWidth(), h = image.getHeight();
		g.setColor(new Color(0, 0, 128, 128));
		g.fillRect(0, 0, w, h);
	}

	// returns the hero
	public RpgEntity getHero() {
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

	// map methods
	private boolean isWall(int id) {
		return wallTiles[id];
	}

	private boolean isUnwalkable(int id) {
		return unwalkableTiles[id];
	}

	// hero action methods
	// try to make the hero move
	private void tryMove(int dx, int dy) {
		if (!hero.isMoving()) {
			// get new location and the tile there
			int newX = hero.getCol() + dx, newY = hero.getRow() + dy;
			int block = map[newY][newX];
			hero.turn(dx, dy);

			// make sure the hero can move
			boolean canMove = true;

			if (isUnwalkable(block))
				canMove = false;

			// and don't move into enemies either
			for (RpgEntity enemy : enemies) {
				if (enemy.getCurrentHealth() == 0)
					continue;

				if (enemy.getCol() == newX && enemy.getRow() == newY) {
					canMove = false;
					break;
				}
			}

			// don't move into the boss (size 2x2 tiles)
			if ((boss.getCol() == newX || boss.getCol() + 1 == newX)
					&& (boss.getRow() == newY || boss.getRow() + 1 == newY)) {
				canMove = false;
			}

			// move if the hero is allowed to
			if (canMove)
				hero.move(hero.getHeading());
		}
	}

	// try to make the hero shoot
	private void tryShoot() {
		long time = System.currentTimeMillis();

		if (hero.canShoot(time)) {
			Bullet bullet = hero.shoot(time, heroBulletSpeed);
			bullet.damage = 1;
			bullet.source = "hero";
			bullets.add(bullet);
			MusicPlayer.playEffect("bullet");
		}
	}

	// called when the hero dies
	private void heroDie() {
		listener.heroDied();
		parent.gameText.update(parent.getGameTime());
		restartLevel = true;
	}

	// enemy artificial intelligence methods
	// whether an enemy can see the hero
	private boolean seesHero(int x, int y) {
		// check columns
		if (x == hero.getCol()) {
			int sign = hero.getRow() - y;
			sign = Math.abs(sign) / sign;

			// enemy cannot see over walls
			for (int i = y + sign; i != hero.getRow(); i += sign)
				if (isWall(map[i][x]))
					return false;
			return true;
		}

		// check rows
		if (y == hero.getRow()) {
			int sign = hero.getCol() - x;
			sign = Math.abs(sign) / sign;

			// enemy cannot see over walls
			for (int i = x + sign; i != hero.getCol(); i += sign)
				if (isWall(map[y][i]))
					return false;
			return true;
		}

		return false;
	}

	// whether an entity sees the hero
	private boolean seesHero(RpgEntity enemy) {
		return seesHero(enemy.getCol(), enemy.getRow());
	}

	// whether the boss sees the hero
	private boolean bossSeesHero() {
		return seesHero(boss.getCol(), boss.getRow())
				|| seesHero(boss.getCol() + 1, boss.getRow() + 1);
	}

	// gets the direction an enemy must turn to face the hero
	public int getHeadingTowardsHero(RpgEntity enemy) {
		if (hero.getCol() < enemy.getCol())
			return Heading.LEFT;
		else if (hero.getRow() > enemy.getRow())
			return Heading.DOWN;
		else if (hero.getCol() > enemy.getCol())
			return Heading.RIGHT;
		else if (hero.getRow() < enemy.getRow())
			return Heading.UP;

		return Heading.UP;
	}

	// enemy and boss shooting methods
	public void enemiesShoot() {
		long time = System.currentTimeMillis();
		
		for (RpgEntity enemy : enemies) {
			if (enemy.getCurrentHealth() == 0)
				continue;

			if (seesHero(enemy)) {
				int heading = getHeadingTowardsHero(enemy);

				// take one step to turn to enemy
				if (enemy.getHeading() != heading) {
					enemy.setHeading(heading);
					enemy.shoot(System.currentTimeMillis(), 0);
				} else if (enemy.canShoot(System.currentTimeMillis())) {
					Bullet b = enemy.shoot(time, enemyBulletSpeed);
					b.damage = 1;
					b.source = "enemy";
					bullets.add(b);
				}
			}
		}
	}

	public void bossShoot() {
		long time = System.currentTimeMillis();

		if (boss.canShoot(time)) {
			// shoot 3 bullets
			for (int i = 0; i < 3; i++) {
				// create bullet
				Bullet b = boss.shoot(time, 4);
				b.damage = 2;
				b.color = 1;
				b.source = "enemy";
				
				// space out the bullets
				for (int j = 0; j < i; j++)
					b.update();
				
				bullets.add(b);
			}
		}
	}

	// enemy and boss on-death methods
	private void bossDie() {
		if (bossKilled)
			return;

		listener.bossKilled();
		parent.gameText.update(parent.getGameTime());
		bossKilled = true;
	}
	
	private void enemyDie(int index) {
		QuestionScreen.importQuestion(Question.next());
		QuestionScreen.prev = parent.getName();
		QuestionScreen.setBackgroundImage(parent.getImage());
		parent.setScreen("questionScreen");
		questionAsked = true;
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

	// update methods
	public void update() {
		if (parent.gameText.alpha != 0)
			return;

		processTriggers();

		processHero();
		processBoss();
		processEnemies();
		processBullets();

		processCamera();
	}

	// processes triggers for the level, mostly text
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

	// processes hero movement and shooting
	private void processHero() {
		if (parent.isKeyPressed(KeyEvent.VK_W)
				|| parent.isKeyPressed(KeyEvent.VK_UP)) {
			tryMove(0, -1);
		} else if (parent.isKeyPressed(KeyEvent.VK_A)
				|| parent.isKeyPressed(KeyEvent.VK_LEFT)) {
			tryMove(-1, 0);
		} else if (parent.isKeyPressed(KeyEvent.VK_S)
				|| parent.isKeyPressed(KeyEvent.VK_DOWN)) {
			tryMove(0, 1);
		} else if (parent.isKeyPressed(KeyEvent.VK_D)
				|| parent.isKeyPressed(KeyEvent.VK_RIGHT)) {
			tryMove(1, 0);
		}

		if (parent.isKeyPressed(KeyEvent.VK_SPACE)) {
			tryShoot();
		}

		hero.update();
	}

	// processes boss movement and shooting
	private void processBoss() {
		if (bossSeesHero()) {
			int heading = 0;
			int hr = hero.getRow(), hc = hero.getCol();
			int br = boss.getRow(), bc = boss.getCol();
			
			if (hr < br)
				heading = 0;
			else if (hc > bc + 1)
				heading = 1;
			else if (hr > br + 1)
				heading = 2;
			else if (hc < bc)
				heading = 3;
			
			if (heading != boss.getHeading()) {
				boss.setHeading(heading);
				boss.shoot(System.currentTimeMillis(), 0);
			} else
				bossShoot();
		}

		boss.update();

		if (bossSighted)
			return;

		// check if the hero sees the boss
		int diffX = Math.abs(boss.getRow() - hero.getRow());
		int diffY = Math.abs(boss.getCol() - hero.getCol());
		if (diffX < 7 && diffY < 7) {
			listener.bossSighted();
			parent.gameText.update(parent.getGameTime());
			bossSighted = true;
		}
	}

	// processes enemy movement and shooting
	private void processEnemies() {
		for (RpgEntity enemy : enemies)
			if (enemy.getCurrentHealth() != 0)
				enemy.update();

		enemiesShoot();
	}

	// processes bullet movement and collisions
	private void processBullets() {
		// update the enemies and boss
		for (Bullet b : bullets)
			b.update();

		// check for bullet collisions
		for (int i = bullets.size() - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);

			// check if bullet hit a wall (the 9 surrounding walls)
			boolean hitWall = false;
			int x = bullet.x / 32, y = bullet.y / 32;
			for (int j = -1; j <= 1; j++)
				for (int k = -1; k <= 1; k++) {
					int r = y + j, c = x + k;
					if (r >= 0 && r < map.length && c >= 0 && c < map[0].length)
						if (isWall(map[r][c])
								&& bullet.hitTest(32 * c, 32 * r, 32, 32))
							hitWall = true;
				}

			// ... or if bullet is off the map
			if (hitWall || bullet.offMap(map[0].length * 32, map.length * 32)) {
				bullets.remove(i);
				continue;
			}

			// check for hero collision
			if (bullet.source.equals("enemy")) {
				if (bullet.hitTest(hero.getX(), hero.getY())) {
					bullets.remove(i);

					red = System.currentTimeMillis() + 100;
					if (hero.subtractHealth(bullet.damage))
						heroDie();
				}
			}

			if (bullet.source.equals("hero")) {
				// check for enemy hit
				for (int j = 0; j < enemies.size(); j++) {
					RpgEntity enemy = enemies.get(j);
					if (enemy.getCurrentHealth() == 0)
						continue;

					if (bullet.hitTest(enemy.getX(), enemy.getY())) {
						bullets.remove(i);

						if (enemy.subtractHealth(bullet.damage))
							enemyDie(j);

						break;
					}
				}

				// check for boss hit
				if (bullet.hitTest(boss.getX(), boss.getY(), boss.getWidth(),
						boss.getHeight())) {
					bullets.remove(i);

					if (boss.subtractHealth(bullet.damage))
						bossDie();
				}
			}
		}
	}

	// updates the camera
	private void processCamera() {
		// move x and y if the hero has moved
		int rx = x + hero.getX(), ry = y + hero.getY();
		int borderX = 350, borderY = 250;

		if (rx < borderX) {
			x = borderX - hero.getX();
		} else if (rx > 800 - borderX) {
			x -= (rx - (800 - borderX));
		}

		if (ry < borderY) {
			y = borderY - hero.getY();
		} else if (ry > 600 - borderY) {
			y -= (ry - (600 - borderY));
		}
	}

	// repainting method
	public void paint(Graphics g) {
		// draw the map
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map[row].length; col++) {
				drawTile(g, row, col, map[row][col]);
			}
		}

		// draw other things
		drawEntities(g);
		drawOther(g);
	}

	// draws the map tiles
	private void drawTile(Graphics g, int row, int col, int id) {
		// process where the map is currently and where to paint
		int mapx = 32 * col, mapy = 32 * row;
		int px = mapx + x, py = mapy + y;

		// make sure the tile will actually show up on the screen
		if (px > -32 && px < Game.WIDTH && py > -32 && py < Game.HEIGHT) {
			// draw the tile
			g.drawImage(mapTiles[id], px, py, null);
			
			// draw the border
			g.setColor(Color.black);
			
			// draw a whole border for non-water, and make up the lost thickness
			if (id != 3)
				g.drawRect(px, py, 31, 31);
			else {
				// top
				if (row != 0 && map[row - 1][col] != 3)
					g.drawLine(px - 1, py, px + 34, py);
				
				// right
				if (col != map[0].length - 1 && map[row][col + 1] != 3)
					g.drawLine(px + 31, py, px + 31, py + 32);
				
				// bottom
				if (row != map.length - 1 && map[row + 1][col] != 3)
					g.drawLine(px - 1, py + 31, px + 34, py + 31);
				
				// left
				if (col != 0 && map[row][col - 1] != 3)
					g.drawLine(px, py, px, py + 32);
			}
		}
	}

	// draws the entities, bullets, and shop
	private void drawEntities(Graphics g) {
		// translate to the camera view
		g.translate(x, y);

		// draw the bullets
		for (Bullet b : bullets)
			b.paint(g);

		// draw shop
		g.drawImage(shopTile, 32 * shopX, 32 * shopY, null);

		// draw the entities
		for (RpgEntity e : enemies)
			if (e.getCurrentHealth() != 0)
				e.paint(g);

		if (hero.getCurrentHealth() != 0)
			hero.paint(g);
		if (boss.getCurrentHealth() != 0)
			boss.paint(g);

		// undo the view
		g.translate(-x, -y);
	}

	// draws the gold and red overlay
	private void drawOther(Graphics g) {
		// draw the amount of gold the player has
		g.setFont(Game.font24);
		g.setColor(Color.yellow);
		g.drawString("Gold: " + gold, 800 - 120, 24);

		// paint red if got hit
		if (System.currentTimeMillis() < red) {
			g.setColor(new Color(255, 0, 0, 64));
			g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		}
	}
}

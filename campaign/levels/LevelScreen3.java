/*
 * LevelScreen3.java
 * James Zhang
 * January 3 2011
 * Level 3.
 */

package campaign.levels;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;

import campaign.framework.GameText;
import campaign.framework.LevelListener;
import campaign.framework.LevelScreen;
import campaign.framework.RpgEntity;
import campaign.framework.RpgMap;
import campaign.framework.Shop;
import campaign.framework.ShopScreen;
import framework.GameWindow;
import game.Game;
import game.MusicPlayer;

public class LevelScreen3 extends LevelScreen implements LevelListener, Shop,
		ActionListener {
	private static final long serialVersionUID = 1L;

	// pop-ups
	private BufferedImage levelPopup;
	private BufferedImage instructionsPopup;

	// large classes
	private RpgMap map;
	private ShopScreen shop;

	// initial settings
	private RpgEntity hero;
	private RpgEntity boss;
	private ArrayList<RpgEntity> enemies;

	// shop variables
	private static final String[] items = { "refill health",
			"increase max health", "slow enemy bullets",
			"increase bullet speed" };
	private static final int[] costs = { 10, 15, 25, 35 };
	private static final int enemyBulletSpeeds[] = { 4, 3 };
	private static final int heroBulletSpeeds[] = { 6, 8 };

	// constructor for static things
	public LevelScreen3(GameWindow parent) {
		super(parent, "levelScreen3");

		// load pop-ups
		levelPopup = Game.loadImage("level3.png");
		instructionsPopup = Game.loadImage("rpg.png");

		// load shop
		shop = new ShopScreen(parent, "shopScreen3", this, this, this.getName());
		parent.addScreen(shop);

		// load entity graphics and map graphics
		BufferedImage[][] heroImages = new BufferedImage[4][16];
		BufferedImage[][] bossImages = new BufferedImage[4][1];
		BufferedImage[][] enemyImages = new BufferedImage[4][1];
		loadEntityGraphics(heroImages, bossImages, enemyImages);

		// load map
		loadMap(heroImages, bossImages, enemyImages);

		// create this class
		init();
	}

	// class constructor
	@Override
	public void init() {
		gameText = new GameText(new Rectangle(20, 380, 760, 200));

		// copy the hero, boss, and enemies
		RpgEntity newHero = new RpgEntity(hero);
		RpgEntity newBoss = new RpgEntity(boss);

		ArrayList<RpgEntity> newEnemies = new ArrayList<RpgEntity>();
		for (RpgEntity enemy : enemies)
			newEnemies.add(new RpgEntity(enemy));

		map.softReset(this, this, newHero, newBoss, newEnemies, 0, -34);
		map.setBulletSpeeds(heroBulletSpeeds[0], enemyBulletSpeeds[0]);
	}

	// load the graphics for each entity
	private void loadEntityGraphics(BufferedImage[][] hero,
			BufferedImage[][] boss, BufferedImage[][] enemy) {
		// load hero, then flip the right to get left
		hero[0] = loadHero(Game.loadImage("rpgHero1.png"));
		hero[1] = loadHero(Game.loadImage("rpgHero2.png"));
		hero[2] = loadHero(Game.loadImage("rpgHero3.png"));
		for (int i = 0; i < 16; i += 2)
			hero[3][i] = RpgEntity.flipHorizontally(hero[1][i]);
		for (int i = 1; i < 16; i += 2)
			hero[3][i] = hero[3][i - 1];

		// load boss
		for (int i = 0; i < 4; i++)
			boss[i][0] = Game.loadImage("rpgBoss2" + (i + 1) + ".png");

		// load enemy
		for (int i = 0; i < 4; i++)
			enemy[i][0] = Game.loadImage("rpgEnemy" + (i + 1) + ".png");
	}

	// loads the graphics for a single hero heading
	private BufferedImage[] loadHero(BufferedImage input) {
		int frames = 6;
		BufferedImage[] inputImages = new BufferedImage[frames];
		BufferedImage[] result = new BufferedImage[16];

		// accept both vertical or horizontal images
		int step = input.getWidth() / frames;
		int height = input.getHeight();
		for (int i = 0; i < frames; i++) {
			BufferedImage temp = input.getSubimage(i * step, 0, step, height);

			int newH = 32;
			int newW = newH * step / height;
			BufferedImage resized = RpgEntity.resize(temp, newW, newH);
			inputImages[i] = new BufferedImage(32, 32,
					BufferedImage.TYPE_INT_ARGB);
			Graphics g = inputImages[i].getGraphics();
			g.drawImage(resized, (32 - newW) / 2, 0, null);
		}

		for (int i = 0; i < 16; i++) {
			result[i] = inputImages[i * 6 / 16];
		}

		return result;
	}

	// loads the map given the loaded graphics
	private void loadMap(BufferedImage[][] heroImages,
			BufferedImage[][] bossImages, BufferedImage[][] enemyImages) {
		// read in map graphics
		BufferedImage[] tiles = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			tiles[i] = Game.loadImage("rpgTile" + (i + 1) + ".png");
			RpgMap.darken(tiles[i]);
		}

		// create scanner
		Scanner in = new Scanner(Game.loadStream("map3.txt"));

		// read in map
		int width = in.nextInt(), height = in.nextInt();
		int[][] grid = new int[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				grid[i][j] = in.nextInt();

		// read in entities
		hero = loadEntity(in, 250, heroImages, 32);
		boss = loadEntity(in, 1000, bossImages, 64);
		enemies = new ArrayList<RpgEntity>();
		int numEnemies = in.nextInt();
		for (int i = 0; i < numEnemies; i++)
			enemies.add(loadEntity(in, 1000, enemyImages, 32));

		// load shop graphics
		BufferedImage shopTile = Game.loadImage("shop1.png");

		// initialize non-walkable and wall tiles
		int[] unwalkable = { 0, 3 };
		int[] walls = { 0 };

		map = new RpgMap(this, this, hero, boss, enemies, 0, -34);
		map.loadResources(tiles, shopTile, 28, 26, grid, unwalkable, walls);
	}

	// creates a single entity given a scanner and resources
	private RpgEntity loadEntity(Scanner in, int shootInterval,
			BufferedImage[][] images, int size) {
		int x = in.nextInt();
		int y = in.nextInt();
		int health = in.nextInt();

		return new RpgEntity(x, y, health, shootInterval, images, size, size);
	}

	// called when the screen is shown
	@Override
	public void processTransition() {
		init();
		MusicPlayer.playLoop("level3");

		// show level title
		addPopup(levelPopup);
		setAlpha(10 * 60 * 4 + 100);
		setFadeRate(10);

		// show introduction text
		gameText.setText("Carnegie: Well, it’s 8 P.M. I should start on "
				+ "my way to the cave so that I will be early and not "
				+ "caught out of position when Sinclair prepares his escape.  "
				+ "Additionally, the forest that I will go through to get to "
				+ "the cave will probably have Sinclair’s goons in it too, "
				+ "so it will take time for me to traverse the forest."
				+ "\nInstructions: Go through the forest using the WASD or "
				+ "arrow keys for movement. Press the space bar to shoot, "
				+ "and avoid being hit by enemies who will attack you on "
				+ "sight. But if you choose to confront these enemies and "
				+ "defeat them, they will ask you a question after they are "
				+ "defeated. Answering this question correctly results in a "
				+ "reward of of 10 gold, while other answers result in a "
				+ "reward of 5 gold. Click on the shop in this level to buy "
				+ "upgrades with the gold you have amassed.");
		gameText.addFinishedListener(this);
	}

	// called when a key is pressed
	@Override
	public void processKeyPressed(KeyEvent e) {
		super.processKeyPressed(e);
		if (e.isConsumed())
			return;

		if (e.getKeyCode() == KeyEvent.VK_TAB)
			gameText.keyPressed();
	}

	// called when a mouse button is clicked
	@Override
	public void processMouseClicked(MouseEvent e) {
		map.mouseClicked(e.getX(), e.getY());
	}

	// threaded methods
	@Override
	public void update(long gameTime) {
		gameText.update(gameTime);
		map.update();
		super.updatePopup();
	}

	@Override
	public void repaint(Graphics g) {
		map.paint(g);
		gameText.paint(g);
		super.drawPopup(g);
	}

	// level listener interface
	@Override
	public void heroDied() {
		gameText.setText("You died! Play again?");
	}

	@Override
	public void bossSighted() {
		gameText.setText("Carnegie: ! That’s Bellamy, one of Sinclair’s "
				+ "top henchmen! He must have been put here to seal off one "
				+ "of the entrances to the cave and to stop possible "
				+ "problems (like me) from ruining the plan.");
	}

	@Override
	public void bossKilled() {
		gameText.setText("Bellamy: I’ll be sure to tell Sinclair about "
				+ "your intrusion when I see him!  You’ll be sorry!"
				+ "\nInstructions: This time, you will have to answer "
				+ "a few questions correctly on your first try before "
				+ "advancing onto the next level.");
	}

	@Override
	public void nextLevelText() {
		gameText.setText("Carnegie: Alright, now that I’ve defeated "
				+ "Bellamy, I will be able to go in to the cave and fight Sinclair.");
	}

	@Override
	public void shopClicked() {
		shop.refresh();
		shop.setBackgroundImage(getImage());
		setScreen(shop.getName());
	}

	@Override
	public void nextLevel() {
		transitionScreen("levelScreen4", 1000);
	}

	@Override
	public void restartLevel() {
		transitionScreen("levelScreen3", 0);
	}

	// shop interface
	@Override
	public String getItem(int index) {
		return items[index] + " (" + costs[index] + ")";
	}

	@Override
	public boolean canBuy(int index) {
		if (map.getGold() < costs[index])
			return false;

		RpgEntity hero = map.getHero();
		switch (index) {
			case 0:
				return (hero.getCurrentHealth() < hero.getTotalHealth());
			case 1:
				return (this.hero.getTotalHealth() == hero.getTotalHealth());
			case 2:
				return (map.getEnemyBulletSpeed() == enemyBulletSpeeds[0]);
			case 3:
				return (map.getHeroBulletSpeed() == heroBulletSpeeds[0]);
		}

		return false;
	}

	@Override
	public void buy(int index) {
		RpgEntity hero = map.getHero();
		switch (index) {
			case 0:
				hero.setCurrentHealth(hero.getTotalHealth());
				break;
			case 1:
				hero.setTotalHealth(this.hero.getTotalHealth() + 10);
				hero.setCurrentHealth(hero.getTotalHealth());
				break;
			case 2:
				map.setEnemyBulletSpeed(enemyBulletSpeeds[1]);
				break;
			case 3:
				map.setHeroBulletSpeed(heroBulletSpeeds[1]);
				break;
			default:
				return;
		}

		map.subtractGold(costs[index]);
		shop.refresh();
	}

	@Override
	public int getGold() {
		return map.getGold();
	}

	// called when the game text finishes its introduction
	@Override
	public void actionPerformed(ActionEvent e) {
		// show level instructions
		addPopup(instructionsPopup);
		setAlpha(1 * 60 * 6);
		setFadeRate(1);
	}
}

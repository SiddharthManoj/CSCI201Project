/*
 * LevelScreen2.java
 * James Zhang
 * January 3 2011
 * Level 2.
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
import campaign.framework.Shop;
import campaign.framework.ShopScreen;
import campaign.framework.SideScrollerEntity;
import campaign.framework.SideScrollerMap;
import framework.GameWindow;
import game.Game;
import game.MusicPlayer;

public class LevelScreen2 extends LevelScreen implements LevelListener, Shop,
		ActionListener {
	private static final long serialVersionUID = 1L;

	// pop-ups
	private BufferedImage levelPopup;
	private BufferedImage instructionsPopup;

	// large variables
	private SideScrollerMap map;
	private ShopScreen shop;

	// initial settings for entities
	private SideScrollerEntity hero;
	private SideScrollerEntity boss;
	private ArrayList<SideScrollerEntity> enemies;

	// shop variables
	private static final String[] items = { "refill health",
			"increase max health", "slow enemy bullets",
			"increase bullet speed" };
	private static final int[] costs = { 10, 15, 25, 35 };
	private static final int enemyBulletSpeeds[] = { 4, 3 };
	private static final int heroBulletSpeeds[] = { 6, 8 };

	// sighting rectangle
	private static Rectangle sighting = new Rectangle(19 * 32, 0, 32, 9 * 32);

	// "static" constructor
	public LevelScreen2(GameWindow parent) {
		super(parent, "levelScreen2");

		// load pop-ups
		levelPopup = Game.loadImage("level2.png");
		instructionsPopup = Game.loadImage("ss1.png");

		// load shop
		shop = new ShopScreen(parent, "shopScreen2", this, this, this.getName());
		parent.addScreen(shop);

		// load entity graphics and map graphics
		BufferedImage[][] heroImages = new BufferedImage[2][1];
		BufferedImage[][] bossImages = new BufferedImage[2][1];
		BufferedImage[][] enemyImages = new BufferedImage[2][1];
		loadEntityGraphics(heroImages, bossImages, enemyImages);

		// load map
		loadMap(heroImages, bossImages, enemyImages);

		// create this class
		init();
	}

	// loads the entity graphics onto their arrays
	private void loadEntityGraphics(BufferedImage[][] hero,
			BufferedImage[][] boss, BufferedImage[][] enemy) {
		// load hero, then flip the right to get left
		BufferedImage image = Game.loadImage("ssHero.png");
		BufferedImage image2 = SideScrollerEntity.resize(image,
				image.getWidth() * 52 / image.getHeight(), 52);
		hero[1][0] = new BufferedImage(32, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics g = hero[1][0].getGraphics();
		g.drawImage(image2, 0, 12, null);
		g.dispose();
		hero[0][0] = SideScrollerEntity.flip(hero[1][0]);

		// load boss
		boss[0][0] = Game.loadImage("ssBoss1.png");
		boss[1][0] = SideScrollerEntity.flip(boss[0][0]);

		// load enemy
		enemy[0][0] = Game.loadImage("ssEnemy.png");
		enemy[1][0] = SideScrollerEntity.flip(enemy[0][0]);
	}

	// loads the map class given the loaded resources
	private void loadMap(BufferedImage[][] heroImages,
			BufferedImage[][] bossImages, BufferedImage[][] enemyImages) {
		// read in map graphics
		BufferedImage[] tiles = new BufferedImage[3];
		for (int i = 0; i < tiles.length; i++)
			tiles[i] = Game.loadImage("ssTile" + (i + 1) + ".png");

		// create scanner
		Scanner in = new Scanner(Game.loadStream("map2.txt"));

		// read in map
		int width = in.nextInt(), height = in.nextInt();
		in.nextLine();
		in.nextLine();
		int[][] grid = new int[height][width];
		for (int i = 0; i < height; i++)
			processMapLine(grid, in.nextLine(), width, i);

		// read in entities
		hero = loadEntity(in, 200, heroImages, 32);
		boss = loadEntity(in, 1000, bossImages, 32);
		enemies = new ArrayList<SideScrollerEntity>();
		int numEnemies = in.nextInt();
		for (int i = 0; i < numEnemies; i++)
			enemies.add(loadEntity(in, 1000, enemyImages, 32));

		// load shop graphics
		BufferedImage shopTile = Game.loadImage("shop2.png");

		map = new SideScrollerMap(this, this, hero, boss, enemies, 0,
				-32 * 32 + 600, sighting);
		map.loadResources(tiles, grid, shopTile, 29, 5);
	}

	// processing for map
	private static void processMapLine(int[][] grid, String line, int width,
			int row) {
		for (int i = 0; i < width; i++) {
			switch (line.charAt(i)) {
				case '1':
					grid[row][i] = 1;
					break;
				case '.':
					grid[row][i] = 2;
					break;
				default:
					grid[row][i] = 0;
					break;
			}
		}
	}

	// loads an entity given a scanner and some resources
	private SideScrollerEntity loadEntity(Scanner in, int shootInterval,
			BufferedImage[][] images, int size) {
		int x = 32 * in.nextInt();
		int y = 32 * in.nextInt();
		int health = in.nextInt();

		return new SideScrollerEntity(x, y, health, shootInterval, images,
				size, size);
	}

	// "class" constructor
	@Override
	public void init() {
		gameText = new GameText(new Rectangle(20, 380, 760, 200));

		// copy the hero, boss, and enemies
		SideScrollerEntity newHero = new SideScrollerEntity(hero);
		SideScrollerEntity newBoss = new SideScrollerEntity(boss);

		ArrayList<SideScrollerEntity> newEnemies = new ArrayList<SideScrollerEntity>();
		for (SideScrollerEntity enemy : enemies)
			newEnemies.add(new SideScrollerEntity(enemy));

		// reset the map
		map.softReset(this, this, newHero, newBoss, newEnemies, 0,
				-32 * 32 + 600, sighting);
		map.setBulletSpeeds(heroBulletSpeeds[0], enemyBulletSpeeds[0]);
	}

	// called when the screen is shown
	@Override
	public void processTransition() {
		init();
		MusicPlayer.playLoop("level2");

		// show level title
		addPopup(levelPopup);
		setAlpha(10 * 60 * 3 + 100);
		setFadeRate(10);

		// show introduction text
		gameText.setText("Carnegie: Hmm... these tunnels were probably what "
				+ "the mayor was talking about.  Maybe searching them will "
				+ "give me a lead as to where Sinclair is going!  However, "
				+ "I have to be careful.  I’m sure that Sinclair has left "
				+ "plenty of enemies and traps in my way, and if what the "
				+ "mayor said was correct, exploring won’t be easy!"
				+ "\nInstructions: Explore the tunnels using the WASD or "
				+ "arrow keys for movement. Be careful to not fall "
				+ "into the lava. Press the space bar to shoot, "
				+ "and avoid being hit by enemies who will attack you on "
				+ "sight. But if you choose to confront these enemies "
				+ "and defeat them, they will ask you a question after "
				+ "they are defeated. Answering this question correctly "
				+ "results in a reward of of 10 gold, while other "
				+ "answers result in a reward of 5 gold. Click on "
				+ "the shop in this level to buy upgrades with the "
				+ "gold you have amassed.");
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

	// level listeners
	@Override
	public void heroDied() {
		gameText.setText("You died! Try again?");
	}

	@Override
	public void bossSighted() {
		gameText.setText("Carnegie: ! I recognize him, too! He is one of "
				+ "Sinclair’s underlings!");
	}

	@Override
	public void bossKilled() {

	}

	@Override
	public void nextLevelText() {
		gameText.setText("Carnegie: Where has Sinclair gone?\nUnderling: You may "
				+ "have beaten me, but Sinclair will prove to be too strong for you!  "
				+ "Just so that I will be able to see the showdown between you two, "
				+ "I’ll tell you what I know.  Sinclair is planning to leave for "
				+ "another planet at the cave a few miles south from here; right "
				+ "now, he is gathering supplies.  If you want to catch him, meet "
				+ "him there when he leaves at 12 A.M., for there is no point in "
				+ "trying to catch him right now!  Haha!");
	}

	@Override
	public void shopClicked() {
		shop.refresh();
		shop.setBackgroundImage(getImage());
		setScreen(shop.getName());
	}

	@Override
	public void nextLevel() {
		transitionScreen("levelScreen3", 1000);
	}

	@Override
	public void restartLevel() {
		transitionScreen("levelScreen2", 0);
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

		SideScrollerEntity hero = map.getHero();
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
		SideScrollerEntity hero = map.getHero();
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
		setAlpha(1 * 60 * 5);
		setFadeRate(1);
	}
}

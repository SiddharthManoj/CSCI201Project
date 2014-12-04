/*
 * LevelListener.java
 * James Zhang
 * April 30 2012
 * An interface that allows a map to communicate the basic game events.
 */

package campaign.framework;

public interface LevelListener {
	void heroDied();
	void bossSighted();
	void bossKilled();
	void nextLevelText();
	
	void shopClicked();

	void nextLevel();
	void restartLevel();
}

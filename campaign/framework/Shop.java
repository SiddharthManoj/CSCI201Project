/*
 * Shop.java
 * James Zhang
 * Represents methods a shop must have.
 */

package campaign.framework;

public interface Shop {
	String getItem(int index);
	boolean canBuy(int index);
	void buy(int index);
	int getGold();
}

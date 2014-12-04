package game2;

import java.io.Serializable;

public class Card implements Serializable {
	private static final long serialVersionUID = 1L;
	
	static final int SUIT_COUNT = 4;
	static final int NUM_COUNT = 13;
	
	public final int suit;
	public final int num;
	
	public Card(int suit, int num) {
		if (suit >= SUIT_COUNT || num >= NUM_COUNT) {
			throw new IllegalArgumentException();
		}
		
		this.suit = suit;
		this.num = num;
	}
}
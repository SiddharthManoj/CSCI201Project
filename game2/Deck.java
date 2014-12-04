package game2;

import java.io.Serializable;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class Deck implements Serializable {
	private static final long serialVersionUID = 1L;
	
	static final int DECK_COUNT = 6;
	static final int CARD_COUNT = DECK_COUNT * Card.SUIT_COUNT * Card.NUM_COUNT;
	static final int SHUFFLE_COUNT = 1024;
	
	Vector<Card> cards;
	Random shuffler;
	
	public Deck() {
		this.cards = new Vector<Card>(CARD_COUNT);
		
		for (int d = 0; d < 6; ++d) {
			for (int s = 0; s < Card.SUIT_COUNT; ++s) {
				for (int n = 0; n < Card.NUM_COUNT; ++n) {
					this.cards.add(new Card(s, n));
				}
			}
		}
		
		this.shuffler = new Random();
		
		for (int i = 0; i < SHUFFLE_COUNT; ++i) {
			Collections.swap(this.cards, shuffler.nextInt(CARD_COUNT), shuffler.nextInt(CARD_COUNT));
		}
	}
	
	public Card getCard() {
		Card card = this.cards.lastElement();
		this.cards.remove(this.cards.size() - 1);
		return card;
	}
}
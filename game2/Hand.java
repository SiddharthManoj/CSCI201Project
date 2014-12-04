package game2;

import java.io.Serializable;
import java.util.Vector;

public class Hand implements Serializable {
	private static final long serialVersionUID = 1L;
	
	int bet;
	Vector<Card> cards;
	
	public Hand(int bet) {
		this.bet = bet;
		this.cards = new Vector<Card>();
	}
	
	public int getBet() {
		return this.bet;
	}
	
	public void setBet(int bet) {
		this.bet = bet;
	}
	
	public void addCard(Card card) {
		this.cards.add(card);
	}
	
	public void removeCard(int idx) {
		this.cards.remove(idx);
	}
	
}
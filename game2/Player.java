package game2;

import java.io.Serializable;
import java.util.Vector;

public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int bet;
	String un;
	Vector<Hand> hands;


	public Player(String un) {
		this.un = un;
		this.hands = new Vector<Hand>(2);
	}
}
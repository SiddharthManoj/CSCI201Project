package game2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

public class Game implements Serializable {
	private static final long serialVersionUID = 1L;
	
	enum State {
		BET,
		DEAL,
		MOVE,
	}
	
	public HashMap<String, Player> playerMap;
	Vector<Player> players;
	Deck deck;
	int currentPlayer;
	State state;
	
	public Game(Vector<Player> players) {
		this.playerMap = new HashMap<String, Player>();
		this.currentPlayer = 0;
		this.players = players;
		this.state = State.BET;
		this.deck = new Deck();
		
		for (Player p : this.players) {
			this.playerMap.put(p.un, p);
		}
	}
	
	public String currentPlayer() {
		return this.players.elementAt(currentPlayer).un;
	}
	
	public void incrementPlayer() {
		++this.currentPlayer;
		this.currentPlayer = this.currentPlayer % this.players.size();
	}
	
	public void deal(Hand hand, int count) {
		for (int i = 0; i < count; ++i) {
			hand.addCard(this.deck.getCard());
		}
	}
}
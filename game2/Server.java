package game2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Server {
	public static final int PORT = 60503;
	// TODO raise player count to 4
	static final int PLAYER_COUNT = 2;
	
	ServerSocket ss;
	Vector<Player> players;
	HashMap<String, Socket> playerMap;
	
	Game game;
	
	public Server() {
		try {
			this.ss = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.players = new Vector<Player>(PLAYER_COUNT);
		this.playerMap = new HashMap<String, Socket>();
		this.getPlayers();
		this.game = new Game(this.players);
		
		for (int i = 0; i < PLAYER_COUNT; ++i) {
			this.sendToAll();
			this.getBet();
			this.game.incrementPlayer();
		}
		
		this.game.state = Game.State.DEAL;
		
		for (Player p : this.game.players) {
			p.hands.add(new Hand(p.bet));
			this.game.deal(p.hands.firstElement(), 2);
		}
		
		this.sendToAll();
	}
	
	void getPlayers() {
		for (int i = 0; i < PLAYER_COUNT; ++i) {
			try {
				Socket s = this.ss.accept();
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				String un = (String) ois.readObject();
				this.players.add(new Player(un));
				this.playerMap.put(un, s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	void sendToAll() {
		for (Map.Entry<String, Socket> e : this.playerMap.entrySet()) {
			System.out.println("Sent game to a client");
			try {
				ObjectOutputStream oos = new ObjectOutputStream(e.getValue().getOutputStream());
				oos.writeObject(this.game);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	void getBet() {
		try {
			Socket s = this.playerMap.get(this.game.currentPlayer());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			this.game = (Game) ois.readObject();
			System.out.println("got updated game from a client");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (Player p : this.game.players) {
			System.out.println(p.bet);
		}
	}
	
	public static void main(String[] args) {
		new Server();
	}
}
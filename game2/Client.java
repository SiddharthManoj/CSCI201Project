package game2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Client {
	Socket s;
	Game game;
	String un;
	
	public Client() {
		// TODO use actual username
		this.un = Integer.toString(new Random().nextInt());
		
		try {
			this.s = new Socket("localhost", Server.PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.checkIn();
		
		// betting round
		for (int i = 0; i < Server.PLAYER_COUNT; ++i) {
			this.getGame();
		}
		
		// dealing round
		for (int i = 0; i < Server.PLAYER_COUNT; ++i) {
			this.getGame();
		}
	}
	
	void checkIn() {
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(this.s.getOutputStream());
			oos.writeObject(this.un);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	void getGame() {
		try {
			ObjectInputStream ois = new ObjectInputStream(this.s.getInputStream());
			this.game = (Game) ois.readObject();
			this.processGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void sendGame() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(this.s.getOutputStream());
			oos.writeObject(this.game);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void processGame() {
		switch (this.game.state) {
		case BET:
			if (this.game.currentPlayer().equals(this.un)) {
				// TODO get actual bet from user
				this.game.playerMap.get(this.un).bet = 69;
				this.sendGame();
			}
			
			break;
		case DEAL:
			// TODO render dealt cards
			break;
		case MOVE:
			break;
		default:
			break;
		}
	}
	
	boolean turn() {
		return this.game.currentPlayer().equals(this.un);
	}
	
	public static void main(String[] args) {
		new Client();
	}
}
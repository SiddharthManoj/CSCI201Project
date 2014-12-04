package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class ChatServer {
	public int numClients = 0;
	private Vector<ChatThread> ctVector = new Vector<ChatThread>();
	public ChatServer(){
	}
	public ChatServer(int port) {
		try {
			ServerSocket listener = new ServerSocket(port);
			while(true) {
				System.out.println("Waiting for connections...");
				Socket s = listener.accept();
				numClients++;
				System.out.println (numClients);
				System.out.println("Connection from " + s.getInetAddress());
				ChatThread ct = new ChatThread(s, this);
				ctVector.add(ct);
				ct.start();
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	public void sendMessage(String message, ChatThread ct) {
		for(ChatThread c : ctVector) {
			if (!c.equals(ct)) {
				c.send(message);
			}
		}
	}
	
	public void removeChatThread(ChatThread ct) {
		ctVector.remove(ct);
	}
	
	public static void main(String [] args) {
		new ChatServer(31418);
	}
	
	public int returnNumConnections(){
		return numClients;
	}
}
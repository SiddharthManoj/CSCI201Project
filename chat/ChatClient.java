package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class ChatClient extends Thread {
	
	private PrintWriter pw;
	private BufferedReader br;
	String name;
	public int id=0;
	//ChatServer server = new ChatServer(31418);
	public ChatClient(String hostname, int port, Scanner scan, String username) {
		Calendar now = Calendar.getInstance();
		try {
			name = username;
			Socket s = new Socket(hostname, port);
			//System.out.println("You are client #: " + server.returnNumConnections());
			this.pw = new PrintWriter(s.getOutputStream());
			this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.start();
			while (true) {
				String line = scan.nextLine();
				// added timestamps to the chat 
				System.out.println(name + "[" + (now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + 
						 ":" + now.get(Calendar.SECOND)) + "]: " + line);
				pw.println(line);
				pw.flush();
			}
			
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient: " + ioe.getMessage());
		}
	}
	public synchronized int nextId() { return id++; }
	public void run() {
		Calendar now = Calendar.getInstance();
		try {
			while(true) {
				String line = br.readLine();
				// timestamps
				System.out.println(name +  "#" + /*server.returnNumConnections()+*/ "[" + (now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + 
						 ":" + now.get(Calendar.SECOND)) + "]: " + line);
			}
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}

	public static void main(String [] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("What is the name/IP of the server? ");
		String hostname = scan.nextLine();
		System.out.print("What is the port? ");
		int port = scan.nextInt();
		new ChatClient(hostname, port, scan, "Sid Manoj");
	}
}
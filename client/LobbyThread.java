package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import server.LobbyServer;

public class LobbyThread extends Thread{
	private Socket s;
	private LobbyServer ls;
	private PrintWriter pwr;
	private String un;
	private BufferedReader br;
	
	public LobbyThread(Socket s, LobbyServer ls) {
		this.s = s;
		this.ls = ls;
		try {
			this.br =  new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.un = br.readLine();
			System.out.println("Checking in: " + this.un);
			this.pwr = new PrintWriter(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void run(){
		try{
			while(true){
				String type = br.readLine();
				if (type.equals("Logout")){
					System.out.println("Logging out " + this.un + "\n");
					ls.logout(this.un);
				}
				else if(type.equals("Populate")){
					System.out.println("Entering Populate");
					System.out.println("Populating friends list.... \n");
					ArrayList <String> tempfriends = new ArrayList <String>();
					ArrayList <Boolean> tempcheckonline= new ArrayList <Boolean>();
					tempfriends = this.ls.findFriends(this.un);
					tempcheckonline = this.ls.checkOnline(tempfriends);
					for(int x = 0; x< tempfriends.size(); x++){
						pwr.println(tempcheckonline.get(x));
						System.out.println(tempcheckonline.get(x) + " \n" );
						pwr.println(tempfriends.get(x));		
						pwr.flush();
					}
					pwr.println("break-list");
					pwr.flush();
		
				}
				else if (type.equals("AddFriend")){
					String friend = br.readLine();
					pwr.println(ls.addFriend(this.un, friend));
					pwr.flush();
				}
				else if (type.equals("Balance")){
					pwr.println(ls.getBalance(this.un));
					pwr.flush();
				}
				else if (type.equals("Chat")){
					
					String recipient = br.readLine();
					String message = br.readLine();

					ls.sendMessage(recipient, message);
					
				}
				else {
					System.out.println("What is " + type + "?");
					System.exit(1);
				}
			}
		} catch(IOException ioe){
			ls.removeLobbyThread(this);
			System.out.println("Client disconnected from " + s.getInetAddress());
		}
	}
	
	public void send(String recipient, String message){
		pwr.println(recipient);
		pwr.println(message);
		pwr.flush();
	}
	
	public String getUsername(){
		return un;
	}

}

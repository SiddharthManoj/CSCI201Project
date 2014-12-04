package server;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LobbyServer extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel main;
	private JTextArea serverView;
	private Connection c;
	public static ArrayList <String> friends = new ArrayList <String>();
	public static ArrayList <Boolean> onlineornot = new ArrayList <Boolean>();
	int counter = 0;
	public LobbyServer() {
		super ("Lobby Server");
		setSize(600,600);
		setupGUI();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try{
			@SuppressWarnings("resource")
			ServerSocket ss = new ServerSocket(3001);
			c = DriverManager.getConnection("jdbc:mysql://localhost/CardShark", "root", "");
		
			while (true) {
				Socket s = ss.accept();
				serverView.append("hi");
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));	
				String type = br.readLine();
				String username = br.readLine();
				
				if (type.equals("Logout")){
					serverView.append("Logging out " + username + "\n");
					logout(username);
				}
				else if(type.equals("populate")){
					counter++;
					serverView.append("Populating friends list.... "  + counter + "\n");
					PrintWriter pwr = new PrintWriter(s.getOutputStream());
					ArrayList <String> tempfriends = new ArrayList <String>();
					ArrayList <Boolean> tempcheckonline= new ArrayList <Boolean>();
					tempfriends = findFriends(username);
					tempcheckonline = checkOnline(tempfriends);
					for(int x = 0; x< tempfriends.size(); x++){
						pwr.println(tempcheckonline.get(x));
						serverView.append(tempcheckonline.get(x) + " \n" );
						pwr.println(tempfriends.get(x));		
						pwr.flush();
					}
					pwr.println("break-list");
					pwr.flush();
		
				}
				else if (type.equals("AddFriend")){
					String friend = br.readLine();
					PrintWriter pwr = new PrintWriter(s.getOutputStream());
					pwr.println(addFriend(username, friend));
					pwr.flush();
					ArrayList <String> tempfriends = new ArrayList <String>();
					ArrayList <Boolean> tempcheckonline= new ArrayList <Boolean>();
					tempfriends = findFriends(username);
					tempcheckonline = checkOnline(tempfriends);
					for(int x = 0; x< tempfriends.size(); x++){
						pwr.println(tempcheckonline.get(x));
						pwr.println(tempfriends.get(x));		
						pwr.flush();
					}
					pwr.println("break-list");
					pwr.flush();
				}
				else if (type.equals("Balance")){
					PrintWriter pwr = new PrintWriter(s.getOutputStream());
					pwr.println(getBalance(username));
					pwr.flush();
				}
				else if (type.equals("Chat")){
					
					String recipient = br.readLine();
					String message = br.readLine();
					PrintWriter pwr = new PrintWriter(s.getOutputStream());
					pwr.println(recipient);
					pwr.println(message);
					pwr.flush();
				}
				else {
					System.out.println("What is " + type + "?");
					System.exit(1);
				}
			}
			
		} catch(IOException ioe){
			System.out.println("IO Error: " + ioe.getMessage());
			System.exit(1);
		} catch (SQLException sqle) {
			System.out.println("SQL Error: " + sqle.getMessage());
			System.exit(1);
		}
	}
	private ArrayList <Boolean> checkOnline(ArrayList <String> names){
		onlineornot.clear();
		for(int x = 0; x< names.size(); x++){
			PreparedStatement select_user_query;
			try {
				select_user_query = c.prepareStatement("SELECT ready_to_play FROM user WHERE username = ?");
				select_user_query.setString(1,  names.get(x));
				ResultSet rs = select_user_query.executeQuery();
				rs.next();
				int ready = rs.getInt("ready_to_play");
				if(ready == 1){
					onlineornot.add(true);
				}
				else
					onlineornot.add(false);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return onlineornot;
	}
	private ArrayList <String> findFriends(String username){
		friends.clear();
		try {
			PreparedStatement select_user_query;
			select_user_query = c.prepareStatement("SELECT uid FROM user WHERE username = ?");
			select_user_query.setString(1,  username);
			ResultSet rs = select_user_query.executeQuery();
			rs.next();
			int uid = rs.getInt("uid");
			PreparedStatement find_all_friends_query = c.prepareStatement("SELECT fid FROM user_friend_relational WHERE uid = ?");
			find_all_friends_query.setInt(1,  uid);
			ResultSet result = find_all_friends_query.executeQuery();
			while (result.next()) {
				int fid2 = result.getInt("fid");
				PreparedStatement find_all_friends_names_query = c.prepareStatement("SELECT username FROM user WHERE uid = ?");
				find_all_friends_names_query.setInt(1,  fid2);
				ResultSet friendnames = find_all_friends_names_query.executeQuery();
				while(friendnames.next()){
					if (!friends.contains(friendnames.getString("username"))){
						friends.add(friendnames.getString("username"));
					}
				}
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return friends;
		
	}
	private void logout(String username){
		
		try {
			PreparedStatement query = c.prepareStatement("UPDATE user SET ready_to_play=false WHERE username = ?");
			query.setString(1, username);
			query.execute();
			friends.clear();
			onlineornot.clear();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			System.exit(1);
		}
		
	}
	
	private double getBalance(String username){
		try{
			PreparedStatement query = c.prepareStatement("SELECT currency FROM user WHERE username = ?");
			query.setString(1, username);
			ResultSet rs = query.executeQuery();
			rs.next();
			serverView.append("Getting Balance\n");
			return rs.getDouble("currency");
		}catch (SQLException sqle) {
			sqle.printStackTrace();
			System.exit(1);
			return -1;
		}
	}
	
	private String addFriend(String un, String friend){
		try{
			PreparedStatement select_user_query = c.prepareStatement("SELECT uid FROM user WHERE username = ?");
			//Get the UID
			select_user_query.setString(1,  un);
			ResultSet rs = select_user_query.executeQuery();
			rs.next();
			int uid = rs.getInt("uid");
			select_user_query.setString(1,  friend);
			rs = select_user_query.executeQuery(); 
			//Checks if the friend exists in the db
			if (!rs.next()) return "This user does not exist.";
			int fid = rs.getInt("uid");
			
			//Check if relationship already exists.
			PreparedStatement check_existence = c.prepareStatement("SELECT  * FROM user_friend_relational WHERE uid = ? AND fid = ?");
			check_existence.setInt(1, uid);
			check_existence.setInt(2, fid);
			rs = check_existence.executeQuery();
			if (rs.next()) return "You already have this friend on your list.";
			
			PreparedStatement add_friendship = c.prepareStatement("INSERT INTO user_friend_relational VALUES (?, ?)");
			add_friendship.setInt(1, uid);
			add_friendship.setInt(2, fid);
			add_friendship.execute();
			add_friendship.setInt(1, fid);
			add_friendship.setInt(2, uid);
			add_friendship.execute();
			
			
			
			return "User Added!";
		} catch (SQLException sqle){
			System.out.println("Problem adding friend: " +sqle.getMessage());
			sqle.printStackTrace();
			System.exit(1);
		}
		return "";
	}
	
	private void setupGUI(){
		main = new JPanel(new BorderLayout());
		serverView = new JTextArea();
		JScrollPane scroll = new JScrollPane (serverView);
		main.add(scroll, BorderLayout.CENTER);
		this.add(main);
	}
	
	
	public static void main(String[] argv){
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException cnfe){
			cnfe.printStackTrace();
			System.exit(1);
		}
		new LobbyServer();
	}

}

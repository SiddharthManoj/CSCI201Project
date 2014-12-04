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
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import client.LobbyThread;

public class LobbyServer extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel main;
	private JTextArea serverView;
	private Connection c; 
	private Vector<LobbyThread> ltVector = new Vector<LobbyThread>();
	
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
			c = DriverManager.getConnection("jdbc:mysql://localhost/CardShark", "root", "3Rdplacespel");
		
			while (true) {
				Socket s = ss.accept();
				System.out.println("Accepting a connection!");
				LobbyThread lt = new LobbyThread(s, this);
				ltVector.add(lt);
				lt.start();
			}
			
		} catch(IOException ioe){
			System.out.println("IO Error: " + ioe.getMessage());
			System.exit(1);
		} catch (SQLException sqle) {
			System.out.println("SQL Error: " + sqle.getMessage());
			System.exit(1);
		}
	}
	
	
	public ArrayList <Boolean> checkOnline(ArrayList <String> names){
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
	public ArrayList <String> findFriends(String username){
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
	
	public void logout(String username){
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
	
	public double getBalance(String username){
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
	
	public void removeLobbyThread(LobbyThread lt){
		ltVector.remove(lt);
	}
	
	public String addFriend(String un, String friend){
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
	
	public void sendMessage(String recipient, String message){
		for (int i = 0; i <ltVector.size(); i++){
			ltVector.get(i).send(recipient, message);
		}
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

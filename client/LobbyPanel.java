package client;
import game.Game;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;


public class LobbyPanel extends JPanel implements ActionListener, ListSelectionListener, Runnable{
	private static final long serialVersionUID = -8069773572372219648L;
	private String un;
	//Messenger recipient.
	private String recipient = "";
	ImageIcon loggedin = new ImageIcon("logged-in.png");
	ImageIcon loggedout = new ImageIcon("logged-out.png");
	JList<Object> friendList = new JList<Object>();
	private JButton join = new JButton("Join Game");
	private JButton send = new JButton("Send");
	private JLabel title; 

	private JPanel east = new JPanel(new BorderLayout()), eastMessagePanel = new JPanel(new BorderLayout());
	private BasicArrowButton bb = new BasicArrowButton(BasicArrowButton.WEST);
	private JTextField messageField = new JTextField();
	private JTextArea log = new JTextArea(10,20);
	
	private JButton logout = new JButton("Logout");
	private JButton addFriend = new JButton("Add Friend");
	private JLabel balanceLabel = new JLabel("");
	private double balance = -1;
	private Socket s;
	private PrintWriter pwr;
	private BufferedReader br;
	public LobbyPanel(String un) {
		try {
			s = new Socket("localhost",3001);
			this.un = un;
			pwr = new PrintWriter(s.getOutputStream());
			pwr.println(this.un);
			pwr.flush();
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			setupGUI();
			System.out.println("Finished GUI");
			addEventHandler();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void setupGUI(){
		this.setLayout(new BorderLayout());
		this.add(join, BorderLayout.WEST);
		this.title = new JLabel("Welcome, " + this.un, JLabel.CENTER);
		this.title.setVerticalAlignment(SwingConstants.TOP);
		this.title.setFont(new Font("Serif", Font.BOLD, 20));
		JPanel north = new JPanel();
		JPanel northContainer = new JPanel();
		northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
		northContainer.add(title);
		getBalance();
		this.balanceLabel.setText("Your current balance: $" + this.balance);
		this.balanceLabel.setFont(new Font("Serif", Font.BOLD, 20));
		northContainer.add(balanceLabel);
		north.add(northContainer);
		this.add(north, BorderLayout.NORTH);
		System.out.println("Finished North");
		//eastMessagePanel is for messages.
		this.eastMessagePanel.add(bb, BorderLayout.NORTH);
		this.log.setEditable(false);
		this.log.setLineWrap(true);
		this.eastMessagePanel.add(new JScrollPane(this.log), BorderLayout.CENTER);
		JPanel southMessagePanel = new JPanel(new BorderLayout());
		southMessagePanel.add(this.messageField, BorderLayout.CENTER);
		southMessagePanel.add(send, BorderLayout.SOUTH);
		this.eastMessagePanel.add(southMessagePanel, BorderLayout.SOUTH);
		this.east.setBorder(new EmptyBorder(10,10,10,10));
		JScrollPane scrollPane = new JScrollPane(friendList);
		System.out.println("Finished South");
		this.east.add(scrollPane, BorderLayout.CENTER);
		this.east.add(logout, BorderLayout.NORTH);
		this.east.add(addFriend, BorderLayout.SOUTH);
		this.add(this.east, BorderLayout.EAST);
		System.out.println("Finished East");
		
		JPanel center = new JPanel();
		ImageIcon cow = new ImageIcon("./data/Cow.png");
		JLabel cowLabel = new JLabel();
		cowLabel.setIcon(cow);
		center.add(cowLabel);
		this.add(center, BorderLayout.CENTER);
		populateFriendList();

		new Thread(this).start();
		/*Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	        	populateFriendList();
	        }
	    }, 3000, 3000);*/
	}
	
	private void addEventHandler(){
		this.logout.setActionCommand("logout");
		this.logout.addActionListener(this);
		this.addFriend.setActionCommand("addFriend");
		this.addFriend.addActionListener(this);
		this.join.setActionCommand("join");
		this.join.addActionListener(this);
		this.friendList.addListSelectionListener(this);
		this.bb.setActionCommand("back");
		this.bb.addActionListener(this);
		this.send.setActionCommand("sendMessage");
		this.send.addActionListener(this);
	}
	
	public void valueChanged(ListSelectionEvent le) {
		if (le.getValueIsAdjusting() && friendList.getSelectedIndex()!=-1){
			String selection = (String)friendList.getSelectedValue();
			this.recipient = selection.split("\\s+")[1];
			this.east.setVisible(false);
			this.eastMessagePanel.setVisible(true);
			this.add(this.eastMessagePanel, BorderLayout.EAST);
		}
		
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("logout")){
			logout();
		}
		else if (ae.getActionCommand().equals("addFriend")){
			String friend = JOptionPane.showInputDialog("Enter a friend's username.");
			if (friend == null) return;
			addFriend(friend);
			populateFriendList();
		}
		else if (ae.getActionCommand().equals("join")){
			joinGame();
		}
		else if (ae.getActionCommand().equals("back")){
			this.eastMessagePanel.setVisible(false);
			this.east.setVisible(true);
			this.add(east, BorderLayout.EAST);
			this.messageField.setText("");
			this.log.setText("");
		}
		else if (ae.getActionCommand().equals("sendMessage")){
			if (!this.messageField.getText().equals("")){
				this.log.append(un + ": " + messageField.getText() + "\n");
				sendMessage(this.messageField.getText());
				this.messageField.setText("");
				
			}
		}
	}
	
	
	private void populateFriendList(){
		try {
			pwr.println("Populate");
			pwr.flush();
			//friendList.setText("");
			ArrayList<String>friendListList = new ArrayList<String>();
			System.out.println("Getting Line");
			String line = br.readLine();
			System.out.println("Got line: " + line);
			while (!line.equals("break-list")){
				System.out.println("not break");
				boolean online = Boolean.parseBoolean(line);
				String name = br.readLine();
				String content = "";
				if (online){
					content += "ON: ";
				}
				else{
					content += "OFF: ";
				}
				content += name;
				friendListList.add(content);
				line = br.readLine();
			}

			friendList.setListData(friendListList.toArray());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	private void logout(){
		pwr.println("Logout");
		pwr.flush();
		//Need some help redirecting to login.
		Container parent = this.getParent();
		parent.remove(this);
		parent.add(new LoginPanel());
		parent.validate();
		parent.repaint();
	}
	
	private void addFriend(String friend){
		try{
			pwr.println("AddFriend");
			pwr.println(friend);
			pwr.flush();
			
			String message = br.readLine();
			JOptionPane.showMessageDialog(this, message);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void sendMessage(String message){
		pwr.println("Chat");
		pwr.println(this.recipient);
		pwr.println(this.un + ": " + message);
		pwr.flush();
		
	}
	
	private void acceptMessage(){
		try{
			String recipient = br.readLine();
			System.out.println(recipient);
			if (recipient.equals(this.un)){
				System.out.println("This");
				log.append(br.readLine() + "\n");
				return;
			}
			else{
				return;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getBalance(){
		try {
			pwr.println("Balance");
			pwr.flush();
			
			double balance = Double.parseDouble(br.readLine());
			this.balance = balance;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
	}
	
	private void joinGame(){
		Container parent = this.getParent();
		parent.remove(this);
		String[] arguments = new String[] {"123"};
	    Game.main(arguments);
		parent.validate();
		parent.repaint();
	}

	public void run() {
		while(true){
			acceptMessage();
			//populateFriendList();
		}
	}


}
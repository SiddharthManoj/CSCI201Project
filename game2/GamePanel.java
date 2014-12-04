package game2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class GamePanel extends JPanel implements ActionListener{
	Socket s;
	Player p;
	ObjectOutputStream oos = null;
	private JButton split = new JButton("Split");
	private JButton hit = new JButton("Hit");
	private JButton stay = new JButton("Stay");
	private JButton dd = new JButton("Double Down");
	private JButton leave = new JButton("Leave");
	private JTextField betAmount = new JTextField(25);
	private String un = "";
	private double balance = 0;
	private ArrayList<Player> opponents = new ArrayList();
	private static final long serialVersionUID = 239847298347L;
	public GamePanel(String un, double balance) {
		// TODO: need to wait/listen for other players to join, then call repaint
		setupGUI();
		this.un = un;
		this.balance = balance;
		try {
			s = new Socket("localhost", 60502);
			oos = new ObjectOutputStream(s.getOutputStream());
			//oos.writeObject(new CheckIn(this.un));
			//p = new Player(s, this.un, balance);
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	private void setupGUI(){
		this.setLayout(new BorderLayout());
		JPanel south = new JPanel();
		south.add(new JLabel("Bet: "));
		south.add(betAmount);
		hit.setActionCommand("hit"); split.setActionCommand("split"); stay.setActionCommand("stay");
		south.add(hit); south.add(stay); south.add(split); south.add(dd);
		this.add(south, BorderLayout.SOUTH);
		

		JPanel east = new JPanel(new BorderLayout());
		east.setBorder(new EmptyBorder(10,10,10,10));
		leave.setActionCommand("leave");
		east.add(leave, BorderLayout.NORTH);
		this.add(east, BorderLayout.EAST);
	
		
		
		repaint();
		
	}
	
	private void addEventHandlers(){
		
	}
	
	private void addOpponent(Player newPlayer){
		opponents.add(newPlayer);
        updateUI();
        revalidate();
	}
	
	public void paintComponent(Graphics page)
	{
	    super.paintComponent(page);
	    Image img = new ImageIcon("./data/cardmat.jpg").getImage();
	    page.drawImage( img, 0, 0, null );
	 
	    // have some incremented integer to keep track of which number player to know where to draw the cards on the screen
	    // something like page.drawImage(img, i * 50 + 50, i * 50 + 50, null)
	    /*for(Player p : opponents){
	    	for(Card c : p.hands.get(0)){
	    		Image cardImage = new ImageIcon(c.imagePath).getImage();
	    		page.drawImage(cardImage, 0, 0, null);
	    	}
	    }*/
	    page.setColor(Color.RED);
	    page.setFont(new Font("Helvetica", Font.BOLD, 20));
		page.drawString("Balance " + balance, 510, 530);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("split")){
			
		}
		else if (ae.getActionCommand().equals("hit")){
			
		}
		else if (ae.getActionCommand().equals("stay")){
			
		}
		else if (ae.getActionCommand().equals("leave")){
			//leave();
		}
		
	}
	
	/*private void leave(){
		try {
			oos.writeObject(new Leave(this.un));
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			System.out.println("Error sending the leave message: " + ioe.getMessage());
		}
		
		
	}*/
}

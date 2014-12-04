package client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 763422554098991814L;
	
	private JTextField username = new JTextField();
	private JPasswordField password = new JPasswordField();
	private JButton submit = new JButton("Submit");
	private JButton register = new JButton("Register");
	private Image backgroundImage;
	
	public LoginPanel() {
		
		this.setBackground(Color.BLACK);
		
		try {
			backgroundImage = ImageIO.read(new File("./resources/win.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel("Username   ", SwingConstants.RIGHT), c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		this.add(this.username, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.0;
		this.add(new JLabel("Password   ", SwingConstants.RIGHT), c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.password, c);
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		this.add(this.submit, c);
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		this.add(this.register, c);
		this.submit.setActionCommand("submit");
		this.submit.addActionListener(this);
		this.register.setActionCommand("register");
		this.register.addActionListener(this);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		// Switching to the panels respectively
		if (e.getActionCommand().equals("submit")) {
			this.submit();
		} else if (e.getActionCommand().equals("register")) {
			this.showRegister();
		}
	}
	
	private void submit() {
		String un = this.username.getText();
		String pw = new String(this.password.getPassword());
		
		if (this.checkLogin(un, pw)) {
			// Redirecting to the LobbyPanel
			Container parent = this.getParent();
			parent.remove(this);
			parent.add(new LobbyPanel(un));
			parent.validate();
			parent.repaint();
		}
		else {
			// Added a pop-up when wrong username-password combination is used
			JOptionPane.showMessageDialog(this, "Username and Password does not match, Please try again", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean checkLogin(String uid, String pw) {
		try {
			Socket s = new Socket("localhost", 60500);
			
			// Sending the username and password to the server
			PrintWriter pwr = new PrintWriter(s.getOutputStream());
			pwr.println("Login");
			pwr.println(uid);
			pwr.println(pw);
			pwr.flush();
			
			// Once the server checks the database for if valid username-password combination -- returns the result
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String resultRaw = br.readLine();
			s.close();
			return Boolean.parseBoolean(resultRaw);
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
			ioe.printStackTrace();
			System.exit(1);
		}
		
		return false;
	}
	private void showRegister() {
		Container parent = this.getParent();
		parent.remove(this);
		parent.add(new RegisterPanel());
		parent.validate();
		parent.repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// int something = this.get
		int width = this.getWidth();
		int height = this.getHeight();
		g.drawImage(backgroundImage, 0, 0, width, height, this);
	}
}

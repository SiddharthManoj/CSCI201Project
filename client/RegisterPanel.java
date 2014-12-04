package client;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class RegisterPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -3831920790532914938L;
	
	private JTextField firstNameForm = new JTextField();
	private JTextField lastNameForm = new JTextField();
	private JTextField username = new JTextField();
	private JPasswordField passwordA = new JPasswordField();
	private JPasswordField passwordB = new JPasswordField();
	private JButton submit = new JButton("Register");
	private JTextField emailForm = new JTextField();

	public RegisterPanel() {
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel("First Name  ", SwingConstants.RIGHT), c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.firstNameForm, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.0;
		this.add(new JLabel("Last name  ", SwingConstants.RIGHT), c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.lastNameForm, c);
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.0;
		this.add(new JLabel("Username  ", SwingConstants.RIGHT), c);
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.username, c);
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0.0;
		this.add(new JLabel("Password  ", SwingConstants.RIGHT), c);
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.passwordA, c);
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0.0;
		this.add(new JLabel("Verify Password  ", SwingConstants.RIGHT), c);
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.passwordB, c);
		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 0.0;
		this.add(new JLabel("e-mail  ", SwingConstants.RIGHT), c);
		c.gridx = 1;
		c.gridy = 5;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.emailForm, c);
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		
		this.add(this.submit, c);
		this.submit.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		String pwA = new String(this.passwordA.getPassword());
		String pwB = new String(this.passwordB.getPassword());
		
		if (!pwA.equals(pwB)) {
			// Add a popup. -- DONE
			JOptionPane.showMessageDialog(this, "Please make sure that the passwords match", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Your passwords do not match!!");
			return;
		}
		
		String firstName = firstNameForm.getText();
		String lastName = lastNameForm.getText();
		String un = this.username.getText();
		String password = pwA;
		String email = emailForm.getText();
		
		// Making sure that the fields are not empty
		if(firstName.isEmpty() || lastName.isEmpty() || un.isEmpty() || password.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "None of these fields can be empty!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else if (addUser(firstName, lastName, un, password, email)){
			//Show the login screen again.  User has been successfully added -- DONE
			JOptionPane.showMessageDialog(this, "Registration Successful");
			System.out.println("User added successfully!");
			Container parent = this.getParent();
			parent.remove(this);
			parent.add(new LoginPanel());
			parent.validate();
			parent.repaint();
		}
		else{
			//Make some popup that says that the username already exists -- DONE.
			JOptionPane.showMessageDialog(this, "Username or e-mail already exists!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	private boolean addUser(String firstName, String lastName, String un, String password, String email){
		try{
			Socket s = new Socket("localhost", 60500);
			PrintWriter pwr = new PrintWriter(s.getOutputStream());
			pwr.println("Register");
			pwr.println(firstName);
			pwr.println(lastName);
			pwr.println(un);
			pwr.println(password);
			pwr.println(email);
			pwr.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String resultRaw = br.readLine();
			s.close();
			return Boolean.parseBoolean(resultRaw);
		} catch(IOException ioe){
			System.out.println("IOException: " + ioe.getMessage());
			ioe.printStackTrace();
			System.exit(1);
		}
		return false;
	}
	
}

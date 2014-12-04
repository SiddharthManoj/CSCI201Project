package client;
import javax.swing.JFrame;


public class Client extends JFrame {
	private static final long serialVersionUID = 6463534731269380864L;
	
	public Client() {
		this.add(new LoginPanel());
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new Client();
	}
}
package messenger.server.window;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Window {
	public Window(){
		JFrame f = new JFrame("Server Messenger");
		f.setSize(500,500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel esecuzione = new JLabel("Server in esecuzione");
		System.out.println("Sto avviando la finestra");
		f.add(esecuzione);
		f.setVisible(true);
	}
}

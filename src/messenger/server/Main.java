package messenger.server;

import java.sql.SQLException;

import javax.swing.SwingUtilities;

import messenger.server.database.Database;
import messenger.server.window.Window;

public class Main {
	public static void main(String args[]) throws InterruptedException{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				new Window();
			}
		});
		try {
			Database.start();
		} catch (ClassNotFoundException e) {
			System.err.println("Classe non trovata");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Errore nell'inserimento della query");
			System.err.println(e.getErrorCode()+" "+e.getMessage());
			e.printStackTrace();
		}
		new Listener();
	}
}

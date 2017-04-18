package messenger.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener {
	private ServerSocket ss;
	private Socket s;

	public Listener() throws InterruptedException{
		ConnectionData d = new ConnectionData();
		new Pool();
		try {
			ss = new ServerSocket(d.getPorta());
			ss.setReuseAddress(true);
			while(true){
				s = ss.accept();
				Thread sh = new Thread(new SocketHandler(s));
				sh.start();
				
			}
			
		} catch (IOException errore) {
			errore.printStackTrace();
		}
		
	}
}

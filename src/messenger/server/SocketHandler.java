package messenger.server;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



import java.net.SocketException;
import java.util.BitSet;






import javax.imageio.ImageIO;

import messenger.server.database.Database;

public class SocketHandler implements Runnable {

	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private boolean active;
	private String user;
	public SocketHandler(Socket s) {
		System.out.println("Un nuovo client si è connesso");
		socket = s;
		ois = null;
		oos = null;
		active = true;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		while(active){
			
				try {
					oos.writeObject("hb");
					ascolta();
					
				} 
				catch(EOFException e){
					e.printStackTrace();
					active = false;
				}
				catch(SocketException e){
					System.err.println(e.getMessage()+ " "+e.getCause());
					e.printStackTrace();
					active = false;
				}
				catch (IOException e) {
					active = false;
					e.printStackTrace();
					
				}
				catch (ClassNotFoundException e) {
					active = false;
					e.printStackTrace();
					
				}
				
				finally{
					//System.err.println("Connessione con "+user+" chiusa");
					//Pool.disconnect(user);
				}

		
		}
		
		Pool.disconnect(user);
	}
	

	private String ascolta() throws EOFException, SocketException, IOException, ClassNotFoundException{
		String request = null;
		String un = null;
		String pw = null;
		String to = null;
		String text = null;
		String answer = null;
		String comando = null;
		String comando_array[] = null;
		if(oos==null || ois==null)
			return null;
			Object o =  ois.readObject();
			if(o!=null){
				if(o instanceof byte[]){
					Database.uploadImage((byte[]) o, this.user);
				}
				else
					request = (String)o;
			}
				

	
		if(request == null)
			return null;
		System.err.println("**"+request);
		comando_array = request.split(" ");
		comando = comando_array[0];
		switch(comando){
			
			case "$$sendMessage":
				to = comando_array[1];
				text = request.substring(comando.length()+to.length()+2);
				if(Pool.isConnected(to))
					Pool.sendMessageTo(to, user, text);
				Database.saveMessage(to, user, text);
				oos.writeObject("$$"+comando+" messageSent");
				break;
			case "$$sendPrivateMessage":
				to = comando_array[1];
				text = request.substring(comando.length()+to.length()+2);
				if(Pool.isConnected(to)){
					Pool.sendPrivateMessageTo(to, user, text);
					oos.writeObject("$$"+comando+" true");
				}
				else
					oos.writeObject("$$"+comando+" false");
				break;
			case "$$destroySession":
				to = comando_array[1];
				boolean p = Pool.isConnected(to);
				if(p)
					Pool.destroy(to, user);
				String d = "$$"+comando+" "+p;
				oos.writeObject(d);
				break;
			case "$$refreshOnline":
				BitSet result = new BitSet();
				try{
				String list[] = comando_array[1].split(",");
				for(int i=0; i<list.length; i++)
					if(Pool.isConnected(list[i]))
						result.set(i);
				}catch(ArrayIndexOutOfBoundsException e){
					System.out.println("e");
				}
				oos.writeObject(result);
				oos.flush();
				break;
				
			case "$$privateSession":
				un = comando_array[1];
				boolean state = true;
				if(!Pool.isConnected(un))
					oos.writeObject("$$"+comando+" false");
				else{
					state = Pool.sendKeyRequestTo(user, un);
					oos.writeObject("$$"+comando+" "+state);
				}
				break;
			case "$$sendKey":
				un = comando_array[1];
				String key = comando_array[2];
				Pool.sendKeyTo(un, user, key);
				oos.writeObject("$$"+comando+" true");
				
				break;
			case "$$sendCriptedKey":
				un = comando_array[1];
				String criptedKey = comando_array[2];
				Pool.sendCriptedKeyTo(un, criptedKey);
				oos.writeObject("$$"+comando+" true");
				break;
			case "$$registra":
				un = comando_array[1];
				pw = comando_array[2];
				//new
				String em = comando_array[3];
				String propic = comando_array[4];
				answer = Database.insertNewUser(un, pw, em, propic);
				oos.writeObject("$$"+comando+" "+answer);
				if(answer.equals("ok")){
					user = un;
					Pool.connect(user, this);
				}
				break;
				
			case "$$login":
				un = comando_array[1];
				pw = comando_array[2];
				answer = Database.loginUser(un, pw);
				oos.writeObject("$$"+comando+" "+answer);
				if(answer.equals("ok")){
					user = un;
					Pool.connect(un, this);
				}
				break;
			case "$$isOnline":
				un = comando_array[1];
				answer = Pool.isConnected(un)?"y":"n";
				oos.writeObject("$$"+comando+" "+answer);
				break;
			case "$$askImage":
				un = comando_array[1];
				BufferedImage image = Database.getImage(un);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] imageByte = null;

		        try {
					ImageIO.write(image, "jpg", baos);
					 baos.flush();
					 imageByte = baos.toByteArray();
				     baos.close();
				       
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        if(imageByte==null)
		        	oos.writeObject("$$"+comando+" false");
		        else{
		        	oos.writeObject("$$"+comando+" true");
		        	oos.flush();
		        	oos.writeObject(imageByte);
		        }
				oos.flush();
				
				break;
			case "$$chatlist":
				un = comando_array[1];
				oos.writeObject(Database.getChatList(un));
				oos.flush();
				System.err.println("??Ho appena scritto un oggetto chatlist");
				break;
			
			default:
				System.err.println("Comando non trovato");
				break;
		}
			
		
		return answer;
	}
	
	public void receiveMessage(String text, String from){
		try {
			oos.writeObject("$$receiveMessage "+from+" "+text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void reiveKeyRequest(String from) {
		try{
			oos.writeObject("$$receiveKeyRequest "+from);
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}

	public void receiveKey(String key, String from) {
		try {
			oos.writeObject("$$receiveKey "+key+" "+from);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void receiveCriptedKey(String criptedKey) {
		try {
			oos.writeObject("$$receiveCriptedKey "+criptedKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void receiveCriptedMessage(String text, String user2) {
		try {
			oos.writeObject("$$receiveCriptedMessage "+user2+" "+text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void destroySession(String from) {
		try {
			oos.writeObject("$$noSession "+from);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

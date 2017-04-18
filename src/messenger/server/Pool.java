package messenger.server;


import java.util.HashMap;

public class Pool {
	private static HashMap<String, SocketHandler> connectedList;
	
	public Pool(){
		connectedList = new HashMap<String, SocketHandler>();
	}
	
	public static void connect(String user, SocketHandler sh){
		connectedList.put(user, sh);
	}
	
	public static void disconnect(String user){
		connectedList.remove(user);
	}
	
	public static boolean isConnected(String user){
		return connectedList.containsKey(user);
	}
	
	public static boolean sendMessageTo(String user, String from, String text){
		if(!isConnected(user)) return false;
		connectedList.get(user).receiveMessage(text, from);
		return true;
	}
	
	public static void printConnected(){
		System.out.println(connectedList);
	}

	public static boolean sendKeyRequestTo(String from, String un) {
		if(!isConnected(un)) return false;
		connectedList.get(un).reiveKeyRequest(from);
		return true;
	}

	public static boolean sendKeyTo(String un, String from, String key) {
		if(!isConnected(un)) return false;
		connectedList.get(un).receiveKey(key, from);
		return true;
		
	}

	public static void sendCriptedKeyTo(String un, String criptedKey) {
		if(!isConnected(un)) return;
		connectedList.get(un).receiveCriptedKey(criptedKey);
		
	}

	public static boolean sendPrivateMessageTo(String to, String user, String text) {
		if(!isConnected(to)) return false;
		connectedList.get(to).receiveCriptedMessage(text, user);
		return true;
		
	}

	public static void destroy(String to, String user) {
		connectedList.get(to).destroySession(user);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	private static LinkedList<String> connectedList;
	
	public Pool(){
		connectedList = new LinkedList<String>();
	}
	
	public static void connect(String user){
		connectedList.add(user);
		JOptionPane.showMessageDialog(null, "nuovo utente connesso: "+user);
	}
	
	public static void disconnect(String user){
		connectedList.remove(user);
		JOptionPane.showMessageDialog(null, "L'utente "+user+" si è disconnesso");
	}
	
	public static boolean isConnected(String user){
		return connectedList.contains(user);
	}
	
	
	public static void printConnected(){
		System.out.println(connectedList);
	}
	*/
	
}

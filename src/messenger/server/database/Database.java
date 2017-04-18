package messenger.server.database;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;



import java.sql.PreparedStatement;

import javax.imageio.ImageIO;


import messenger.server.ImageHandler;



public class Database {

	private static Statement db;
	private static Connection con;

	public static void start() throws SQLException, ClassNotFoundException{
		if(isStarted())
			return;
		final String nomeDB = DatabaseData.NOME_DB;
		final String us = DatabaseData.USERNAME;
		final String pw = DatabaseData.PASSWORD;
		final String ind = DatabaseData.INDIRIZZO;
		final int porta = DatabaseData.PORTA;

		Class.forName("com.mysql.jdbc.Driver");  
		con=DriverManager.getConnection("jdbc:mysql://"+ind+":"+porta+"/"+nomeDB, us , pw);  
		db = con.createStatement();


	}

	public static HashMap<String, String[]> getChatList(String username){
		ResultSet rs = null;
		HashMap<String, String[]> hm = new HashMap<String, String[]>();
		String columns[] = {"testo", "username", "isMittente"};
		String columns_conc = "";
		int n_col = columns.length;
		int rows;
		for(int i=0; i<n_col-1; i++)
			columns_conc+=columns[i]+", ";
		String query = 
				"SELECT "+
						columns_conc+
						" (select username from utenti where utenti.id = mittente)='"+username+"' as "+
						columns[n_col-1] +
						" FROM "+
						"messaggi, "+
						"utenti "+
						"WHERE "+
						"("+
						"mittente=(select id from utenti where username='"+username+"') "+
						"OR "+
						"destinatario=(select id from utenti where username='"+username+"') "+
						") "+
						"AND "+
						"( "+
						"mittente = utenti.id "+
						"OR "+
						"destinatario = utenti.id "+
						") "+

				  "AND "+
				  "username != '"+username+"' ";
		//"GROUP BY username";


		System.out.println(query);
		try {

			rs = db.executeQuery(query);
			rs.last();
			rows = rs.getRow();
			rs.beforeFirst();
			String current;
			int x;
			for(int i=0; i<n_col; i++){ //scorre le colonne
				x=0;
				String[] current_columns = new String[rows];
				while(rs.next()){ // scorre le righe
					current = rs.getString(columns[i]);
					current_columns[x++] = current;
				}
				hm.put(columns[i], current_columns);

				rs.beforeFirst();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.err.println("3: "+columns[i]+" "+current_columns[0]);
		return hm;

	}

	public static void uploadImage(byte[] image, String user){
		//JOptionPane.showMessageDialog(null, "Sto aggiornando l'immagine"+image.length);
		String query = "update utenti set image=? where username='"+user+"';";
		if(!isStarted())
			return;
		/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		InputStream is = new ByteArrayInputStream(image);
		try {
			con.setAutoCommit(false);
			PreparedStatement pre  = con.prepareStatement(query);
			pre.setBinaryStream(1,  is, image.length);
			//JOptionPane.showMessageDialog(null,pre.executeUpdate());
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String insertNewUser(String username, String password, String em, String propic){
		//String query = "insert into utenti(username, password, email) values('"+username+"', '"+password+"', '"+em+"');";
		String answer = "ok";
		if(!isStarted())
			return "no";

		try {

			//File imgf = new File("C:\\Users\\seven\\Desktop\\me.jpg");
			//FileInputStream fin = new FileInputStream();
			con.setAutoCommit(false);
			PreparedStatement pre  = con.prepareStatement("insert into utenti(username, password, email, image) values(?, ?, ?, ?);");

			pre.setString(1, username);
			pre.setString(2, password);
			pre.setString(3, em);
			pre.setBinaryStream(4, null, 0);
			pre.executeUpdate();
			//pre.executeUpdate();
			con.commit();
			//if(fin!=null) fin.close();
			//pre.close();
			//con.close();
			if(db.getUpdateCount()!=1)
				answer="ok";


		} catch (SQLException e) {
			if(e.getErrorCode()==1062)
				answer = "exist";
			//else
				//JOptionPane.showMessageDialog(null, e.getErrorCode());

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
		} 

		return answer;
	}

	public static String loginUser(String username, String password) {
		if(!isStarted())
			return "no";
		ResultSet rs = null;
		int rows = -1;
		String answer = "default";
		System.err.println("Sto provando a fare il login: "+username+" "+password);
		String query = "SELECT count(*) FROM utenti WHERE username='"+username+"' AND password='"+password+"';";

		try {
			rs = db.executeQuery(query);
			rs.first();
			rows = rs.getInt(1);
			System.err.println("Righe: "+rows);

		} catch (SQLException e) {
			answer = "err";
			e.printStackTrace();
		}

		if(rows==1)
			answer = "ok";
		else if(rows == -1)
			answer = "err";
		else if(rows==0)
			answer = "no_l";

		return answer;
	}

	public static boolean isStarted(){
		if(db==null)
			return false;
		return true;
	}




	public boolean close(){
		try {
			db.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public static void saveMessage(String to, String from, String text) {
		if(!isStarted())
			return;

		try{ 
			String query = "INSERT INTO "
					+ "messaggi(mittente, destinatario, testo)\n "
					+ "values(\n"
					+ 	"(SELECT id FROM utenti WHERE username='"+from+"'),\n"
					+	"(SELECT id FROM utenti WHERE username='"+to+"'), \n"
					+ 	" '"+text+"');";

			System.out.println("._._._."+query);
			db.executeUpdate(query);


		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static BufferedImage getImage(String un) {
		String query = "SELECT image FROM utenti WHERE username = '"+un+"';";
		ResultSet rs;
		BufferedImage bi = null;
		boolean noImage=false;
		try {
			rs = db.executeQuery(query);
			if(!rs.next())
				noImage = true;
			InputStream is = rs.getBinaryStream(1);
			if(is!=null)
				bi = ImageIO.read(is);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		System.out.println(noImage);
		
		bi = ImageHandler.resizeImageWithHint(bi, 1, 50, 50);
		return bi;
	}



}

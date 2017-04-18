package messenger.client.cryptography;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crittografia {
	
	
	public static String toSHA1(String text){
		MessageDigest md = null;
		String result = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(text.getBytes());
			result = byteArrayToHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++) 
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		
		return result;
	}
	
	public static String encrypt(String un, String pw){
		
		String hash = null;
		char[] un_c = un.toCharArray();
		hash = toSHA1(pw);
		char[] hash_c = hash.toCharArray();
		StringBuilder result = new StringBuilder();
		int max = hash_c.length>un_c.length?hash_c.length:un_c.length;
		int i = 0;
		int ind_hash = 0;
		int ind_un = 0;
		while(ind_hash<max&&ind_un<max){
			if(i%2==0){
				if(ind_un<un_c.length)
					result.append(un_c[ind_un++]);
			}
			else{
				if(ind_hash<hash_c.length)
					result.append(hash_c[ind_hash++]);
			}
			i++;
		}
		hash = result.toString();
		return toSHA1(hash);
		
	}
	
}

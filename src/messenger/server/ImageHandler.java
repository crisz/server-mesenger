package messenger.server;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageHandler {
	
	   public static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type, int d1, int d2){
		   
			BufferedImage resizedImage = new BufferedImage(d1, d2, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, d1, d2, null);
			g.dispose();	
			g.setComposite(AlphaComposite.Src);
		 
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		 
			return resizedImage;
		    }	
	   	
	   final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	   public static String bytesToHex(byte[] bytes) {
	       char[] hexChars = new char[bytes.length * 2];
	       for ( int j = 0; j < bytes.length; j++ ) {
	           int v = bytes[j] & 0xFF;
	           hexChars[j * 2] = hexArray[v >>> 4];
	           hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	       }
	       return new String(hexChars);
	   }

}	

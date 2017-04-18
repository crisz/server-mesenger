package messenger.client.cryptography;

import java.nio.ByteBuffer;
import java.util.Random;

public class PrimeNumber {
	
	public long getPrimeNumber(){
		return getPrimeNumber(100000000000l);
	}
    public long getPrimeNumber(long limit){
    	long timebefore = System.nanoTime();
    	Random rand = new Random();
    	long n = 0;
    	do{ 
    		n= nextLong(rand, limit);
    	}while(!isPrime(n));
    	System.out.println(n);
    	long timeafter = System.nanoTime();
    	System.out.println("Tempo impiegato: "+((timeafter-timebefore)/1000)+"ms");
    	return n;
	}
    
    public long getCoprimeNumber(long number){
    	long n=0;
    	do{
    		n = getPrimeNumber((number-1)/2);
    		
    	}while(mcd(n, number)!=1);
    	
    	return n;
    }
    
    private long mcd(long x, long y) {

        while ( x != y ) {

            if ( x > y ) {
                x = x - y;
            } else {
                y = y - x;
            }
        }

        return x;
    }
    public static long nextLong(Random rng, long n) {
    	   // error checking and 2^x checking removed for simplicity.
    	   long bits, val;
    	   do {
    	      bits = (rng.nextLong() << 1) >>> 1;
    	      val = bits % n;
    	   } while (bits-val+(n-1) < 0L);
    	   return val;
    	}
    
    public static boolean isPrime(long n) {
        if (n <= 3) {
            return n > 1;
        } else if (n % 2 == 0 || n % 3 == 0) {
            return false;
        } else {
            double sqrtN = Math.floor(Math.sqrt(n));
            for (int i = 5; i <= sqrtN; i += 6) {
                if (n % i == 0 || n % (i + 2) == 0) {
                    return false;
                }
            }
            return true;
        }
    }
	public static long get8BytesNumber() {
		long r;
		do{
			r = nextLong(new Random(), Long.MAX_VALUE);
		}while(ByteBuffer.allocate(Long.SIZE).putLong(r).array().length==8);
		return r;
	}
}

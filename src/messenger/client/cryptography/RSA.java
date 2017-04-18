package messenger.client.cryptography;

import java.math.BigInteger;

public class RSA {
	private long p, q, n, f, e, d;
	public long getMod(){
		return n;
	}
	private void setMod(long n){
		this.n = n;
	}
	
	public long getPublicKey(){
		return e;
	}
	private void setPublicKey(long e){
		this.e = e;
	}
	
	public long getPrivateKey(){
		return d;
	}
	private void setPrivateKey(long d){
		this.d = d;
	}
	
	public RSA(){
		PrimeNumber pn = new PrimeNumber();
		p = pn.getPrimeNumber(1000);
		q = pn.getPrimeNumber(1000);
		n = p*q;
		f = (p-1)*(q-1);
		e = pn.getCoprimeNumber(f);
		d=BigInteger.valueOf(e).modInverse(BigInteger.valueOf(f)).longValue();
		setMod(n);
		setPublicKey(e);
		setPrivateKey(d);
	}
	
}

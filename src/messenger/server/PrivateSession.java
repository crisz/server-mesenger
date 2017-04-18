package messenger.server;

import messenger.client.cryptography.RSA;

public class PrivateSession {
	private long publicKey;
	private long privateKey;
	private long mod;
	public PrivateSession(){
		RSA rsa = new RSA();
		setPublicKey(rsa.getPublicKey());
		setPrivateKey(rsa.getPrivateKey());
		setMod(rsa.getMod());
	}
	public long getPublicKey() {
		return publicKey;
	}
	private void setPublicKey(long publicKey) {
		this.publicKey = publicKey;
	}
	public long getPrivateKey() {
		return privateKey;
	}
	private void setPrivateKey(long privateKey) {
		this.privateKey = privateKey;
	}
	public long getMod() {
		return mod;
	}
	private void setMod(long mod) {
		this.mod = mod;
	}
	
}

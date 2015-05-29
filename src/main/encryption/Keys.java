package main.encryption;

import javax.crypto.SecretKey;

public class Keys {
	public final SecretKey encryption, authentication;

	public Keys(SecretKey encryption, SecretKey authentication) {
		this.encryption = encryption;
		this.authentication = authentication;
	}
}

package main.encryption;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import main.ByteUtils;

public class EC {

	public static void main(String[] args) {

		try {
			/* Generate a EC pair */
			// By default Java 7 use NIST, not good!
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA",
					"SunEC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			keyGen.initialize(256, random);

			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();

			System.out.println("################");
			System.out.println("STORE THIS DATA");
			System.out.println("Public Key: " + pub);
			System.out.println("Private Key: " + priv);
			System.out.println("################");

			FileOutputStream fos = new FileOutputStream("spatcher.pub");
			fos.write("-- PUBLIC HEX BYTES --\n".getBytes());
			fos.write(ByteUtils.toHexString(pair.getPublic().getEncoded())
					.getBytes());
			fos.close();

			// Be careful, private key stored without encription!
			fos = new FileOutputStream("spatcher.key");
			fos.write("-- PRIVATE HEX BYTES --\n".getBytes());
			fos.write(ByteUtils.toHexString(pair.getPrivate().getEncoded())
					.getBytes());
			fos.close();

		} catch (Exception e) {
			System.err.println("Caught exception " + e.toString());
		}
	}
	
	  /**
	   * Encrypt the plain text using public key.
	   * 
	   * @param text
	   *          : original plain text
	   * @param key
	   *          :The public key
	   * @return Encrypted text
	   * @throws java.lang.Exception
	   */
	  public static byte[] encrypt(String text, PublicKey key) {
	    byte[] cipherText = null;
	    try {
	      // get an RSA cipher object and print the provider
	      final Cipher cipher = Cipher.getInstance("DSA");
	      // encrypt the plain text using the public key
	      cipher.init(Cipher.ENCRYPT_MODE, key);
	      cipherText = cipher.doFinal(text.getBytes());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return cipherText;
	  }

	  /**
	   * Decrypt text using private key.
	   * 
	   * @param text
	   *          :encrypted text
	   * @param key
	   *          :The private key
	   * @return plain text
	   * @throws java.lang.Exception
	   */
	  public static String decrypt(byte[] text, PrivateKey key) {
	    byte[] dectyptedText = null;
	    try {
	      // get an RSA cipher object and print the provider
	      final Cipher cipher = Cipher.getInstance("DSA");

	      // decrypt the text using the private key
	      cipher.init(Cipher.DECRYPT_MODE, key);
	      dectyptedText = cipher.doFinal(text);

	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }

	    return new String(dectyptedText);
	  }


}

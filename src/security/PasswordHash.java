package security;

// Standard imports:
import java.security.*;
import java.util.*;

// Project imports:
import exceptions.*;

/**
 * This class contains methods useful in
 * hashed password authentication.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class PasswordHash {
    

    /**
     * The keyLength to use
     */
    private static int keyLength = 16;

    
    /**
     * This method creates a random key.
     *
     * @param length the length of the requested key
     * @return the key which is null if something failed
     */
    public static byte[] createKey () throws AuthorizationException {
	byte[] key = new byte[keyLength];

	// Create a random key using the SHA1PRNG algorithm
	try {
	    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	    random.nextBytes(key);
	} catch(Exception e) {
	    throw new AuthorizationException();
	}
	return key;
    }

    
    /**
     * This method combines the key and the password
     * using xor and then creates a one-way hash string
     * using java.security.MessageDigest based on
     * the SHA algorithm. 
     *
     * @param key the key
     * @param pwd the password
     * @return the hash string which is null if something failed
     */
    public static byte[] hash (byte[] key, String password) throws AuthorizationException {

	// Smear the password to fit the keylength
	if (!password.equals("")) {
	    byte pwd[] = password.getBytes();
	    for (int i = 0; i < key.length; i++)
		key[i] = (byte) (key[i] | pwd[i % pwd.length]);
	}
	
	byte[] hash;

	// Hash the key and password using the SHA algorithm
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA");
	    hash = md.digest(key); 
	} catch(Exception e) {
	    throw new AuthorizationException();
	}
	return hash;
    }
}


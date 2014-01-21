package rmiserver;

// Standard imports:
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

// Project imports:
import exceptions.*;
import security.*;
import util.*;

/**
 * This class takes care of the login process. The client 
 * first send a login request to get a key. The key is then
 * hashed with the password and finally returned to the 
 * login method together with the username.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class RMILogin extends UnicastRemoteObject 
                      implements RMILoginInterface{

    /**
     * Private variable to measure the time that
     * have passed since the login request.
     */
    private Date timestamp;

    /**
     * Private variable to hold the key that was
     * sent in the last login request.
     */
    private byte[] key;

    /**
     * Private variable to hold the hostname of
     * the last client that did the last login
     * request.
     */
    private String client;

    /**
     * Private static variable to hold the
     * time out.
     */
    private static int authTimeout = 20000;
  
    /**
     * Empty constructor.
     */
    public RMILogin() throws RemoteException {
    }

    /**
     * The login request returns a key as a String.
     *
     * @throws RemoteException
     * @throws AuthorizationException 
     *
     * @return the key as a String
     */
    public byte[] loginrequest() 
	throws RemoteException, AuthorizationException {

	timestamp = new Date();
	System.out.print(timestamp + " - Login request from host ");

	try {
		client = this.getClientHost();
	} catch(Exception e) { 
		System.out.println("<unresolvable>");
		throw new AuthorizationException();
	} 
	System.out.println(client);

	key = PasswordHash.createKey();
	return key;
    }


    /**
     * Checks the username and the hashed password.
     *
     * @param username the username
     * @param hashstring the password hashed with the key
     *
     * @throws RemoteException
     * @throws AuthorizationException 
     *
     * @return a RMIServerInterface if login is successfull
     */
    public RMIServerInterface login(String username, byte[] hashstring) 
	throws RemoteException, AuthorizationException {

	String password;
	RMIServer server;
	
	try {
		password = Settings.getInstance().getPassword(username);
		String nclient = this.getClientHost();
		if (nclient != client || client == null) throw new Exception();
		if ((new Date()).getTime() - timestamp.getTime() > authTimeout)
			throw new Exception();
		
		byte[] verification = PasswordHash.hash(key, password);
		if (verification == null) throw new Exception();
		if (!Arrays.equals(hashstring, verification)) throw new Exception();
		server = new RMIServer(username, password);

	} catch(Exception e) { 
		System.out.println(new Date() + " - Failed to authenticate as " + username);
		throw new AuthorizationException();
	} 
	System.out.println(new Date() + " - " + username + " logged in");
	return (RMIServerInterface) server;
   }
}	

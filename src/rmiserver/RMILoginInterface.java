package rmiserver;

// Standard imports:
import java.rmi.*;

// Project imports:
import exceptions.*;

/**
 * This is the interface for the RMILogin class.
 *
 * @see RMILogin
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public interface RMILoginInterface extends Remote {

    public byte[] loginrequest() 
	throws RemoteException, AuthorizationException;
    
    public RMIServerInterface login(String username, byte[] hashstring) 
	throws RemoteException, AuthorizationException;

}	

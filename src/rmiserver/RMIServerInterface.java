package rmiserver;

// Standard imports:
import java.rmi.*;
import java.awt.*;
import java.awt.event.*;

// Project imports:
import exceptions.*;

/**
 * This is a interface for the RMIServer class.
 *
 * @see RMIServer
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public interface RMIServerInterface extends Remote {

    public void reset() throws RemoteException;
  
    public void setArea(int nx, int ny, int nwidth, int nheight) throws RemoteException;

    public Dimension getScreenSize() throws RemoteException;

    public void prepareImage() throws RemoteException, IncompatibleImageException, FatalServerException;	
    public byte[] getImage() throws RemoteException;

    public void restoreImage() throws RemoteException, FatalServerException, IncompatibleImageException ;

    public void setJPEGQuality(float q) throws RemoteException;
    
    public float getJPEGQuality() throws RemoteException;

    public void setEncryptionStatus(boolean b) throws RemoteException;

    public boolean getEncryptionStatus() throws RemoteException;

    public void setDeltaStatus(boolean b) throws RemoteException;

    public boolean getDeltaStatus() throws RemoteException;

    public void mousePressed(MouseEvent e) throws RemoteException;

    public void mouseReleased(MouseEvent e) throws RemoteException;

    public void mouseMoved(MouseEvent e) throws RemoteException;

    public void keyPressed(KeyEvent e) throws RemoteException;

    public void keyReleased(KeyEvent e) throws RemoteException;
    
    public void logout() throws RemoteException;
}	

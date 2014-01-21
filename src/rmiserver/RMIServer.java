package rmiserver;

// Standard imports:
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

// Project imports:
import imageconversion.*;
import exceptions.*;
import util.*;
import security.*;

/**
 * This is the server class that creates images
 * and exectues forwarded events.
 * 
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class RMIServer extends UnicastRemoteObject
    implements RMIServerInterface {
    
    /**
     * The image converter that is used to
     * convert outgoing images
     */
    private ImageConverter converter;

    /**
     * The Robot that is used to execute incoming
     * mouse and key events
     */
    private Robot robot;

    /**
     * A reproduction of the client side image
     * This is used in delta restoration
     */
    private BufferedImage clientImg;

    /**
     * A reference to the last created
     * image data buffer
     */
    private byte[] JPEGArray;

    /**
     * A reference to the encrypted version
     * (if available) of the last created 
     * image data buffer
     */
    private byte[] encryptedJPEGArray;
    
    /**
     * The cipher object used to encrypt outgoing
     * image data buffers
     */
    private IceKey encrypter;
    
    /**
     * The horisontal position of the top left
     * corner of the image area
     */
    private int x;

    /**
     * The vertical position of the top left 
     * corner of the image area
     */
    private int y;

    /**
     * The width of the image area
     */
    private int width;

    /**
     * The height of the image area
     */
    private int height;
   
    /**
     * The username that is bound to this object
     */
    private String username;

    /**
     * The current JPEG image quality value
     */
    private float JPEGQuality;

    /**
     * The current delta status 
     */
    private boolean deltaStatus;

    /**
     * The current encryption status
     */
    private boolean encryptionStatus;


    /**
     * The constructor is called by a RMILogin object
     * that has already authenticated the user
     * 
     * @param user the username
     * @param password the password of the user
     */
    public RMIServer(String user, String password) 
	throws RemoteException, FatalServerException {
	username = user;
	converter = new ImageConverter();
	encrypter = new IceKey(0);
	
	byte[] passwd = password.getBytes();
	byte[] key = new byte[8];
        for (int i = 0; i < 8; i++)
	    key[i] = passwd[i % passwd.length];
	encrypter.set(key);
	
	try {
	    robot = new Robot();
	    JPEGQuality = Settings.getInstance().getJPEGQuality(username);
	    deltaStatus = Settings.getInstance().getDeltaStatus(username);
	    encryptionStatus = Settings.getInstance().getEncryptionStatus(username);	    
	} catch (Exception e) {
	    throw new FatalServerException();
	}	   
    }
    

    /**
     * Resets the image data
     */
    public void reset() throws RemoteException {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    width = (int) dimension.getWidth();
	    height = (int) dimension.getHeight();
	    x = 0;
	    y = 0;
	    clientImg = converter.createBlack(width, height);
    }

    
    /**
     * Executes a mousePressed event using a Robot object
     *
     * @param e the incoming MouseEvent
     * @see java.awt.Robot
     * @see java.awt.event.MouseEvent
     */
    public void mousePressed(MouseEvent e) throws RemoteException {
       	robot.mouseMove(e.getX(), e.getY());
       	robot.mousePress(e.getModifiers());
    }


    /**
     * Executes a mouseReleased event using a Robot object
     *
     * @param e the incoming MouseEvent
     * @see java.awt.Robot
     * @see java.awt.event.MouseEvent
     */
    public void mouseReleased(MouseEvent e) throws RemoteException {
	robot.mouseMove(e.getX(), e.getY());
	robot.mouseRelease(e.getModifiers());
    }


    /**
     * Executes a mouseMoved event using a Robot object
     *
     * @param e the incoming MouseEvent
     * @see java.awt.Robot
     * @see java.awt.event.MouseEvent
     */
    public void mouseMoved(MouseEvent e) throws RemoteException {
	robot.mouseMove(e.getX(), e.getY());
   }


    /**
     * Executes a keyPressed event using a Robot object
     *
     * @param e the incoming KeyEvent
     * @see java.awt.Robot
     * @see java.awt.event.KeyEvent
     */
    public void keyPressed(KeyEvent e) throws RemoteException {
	robot.keyPress(e.getKeyCode());
    }

    
    /**
     * Executes a keyReleased event using a Robot object
     *
     * @param e the incoming KeyEvent
     * @see java.awt.Robot
     * @see java.awt.event.KeyEvent
     */
    public void keyReleased(KeyEvent e) throws RemoteException {
	robot.keyRelease(e.getKeyCode());
    }


    /**
     * Sets the JPEG image quality. This also affects the 
     * users long time settings
     *
     * @param q the JPEG image quality
     */
    public void setJPEGQuality(float q) throws RemoteException {
	JPEGQuality = q;
	try {
		Settings.getInstance().setJPEGQuality(username, q);
	} catch(Exception e) {}
    }
    

    /**
     * Gets the JPEG image quality
     *
     * @return the JPEG image quality
     */
    public float getJPEGQuality() throws RemoteException {
	return JPEGQuality;

    }


    /**
     * Sets the encryption status. This also affects the 
     * users long time settings
     *
     * @param b the new encryption status
     */
    public void setEncryptionStatus(boolean b) throws RemoteException {
	encryptionStatus = b;
	try {
		Settings.getInstance().setEncryptionStatus(username, b);
	} catch(Exception e) {}
    }


    /**
     * Gets the encryption status
     *
     * @return the encryption status
     */
    public boolean getEncryptionStatus() throws RemoteException {
	return encryptionStatus;
    }


    /**
     * Sets the delta status. This also affects the 
     * users long time settings
     *
     * @param b the new delta status
     */
    public void setDeltaStatus(boolean b) throws RemoteException {
	deltaStatus = b;
	try {
		Settings.getInstance().setDeltaStatus(username, b);
		clientImg = converter.createBlack(width, height);
	} catch(Exception e) {}
    }


    /**
     * Gets the delta status
     *
     * @return the delta status
     */
    public boolean getDeltaStatus() throws RemoteException {
	return deltaStatus;
    }


    /**
     * Sets a new image area
     *
     * @param nx the new x value
     * @param ny the new y value
     * @param nwidth the new width
     * @param nheigth the new height
     */
    public void setArea(int nx, int ny, int nwidth, int nheight) throws RemoteException {
	x = nx;
	y = ny;
	width = nwidth;
	height = nheight;
	clientImg = converter.createBlack(width, height);
    }


    /**
     * Gets the screen size in pixels
     * 
     * @return the dimension of the screen 
     */
    public Dimension getScreenSize() throws RemoteException {
	Dimension dimension = null;
	    dimension = Toolkit.getDefaultToolkit().getScreenSize();
	return dimension;
    }

   
    /**
     * Creates a screenshot and prepares it for transmission 
     * using delta precompression and encryption if enabled
     */
    public void prepareImage() throws RemoteException, 
                                      IncompatibleImageException, 
				      FatalServerException {

	// Create screenshot
	BufferedImage img = 
	    robot.createScreenCapture(new Rectangle (x, y, width, height)); 

	// Create delta precompressed image if enabled
	if (deltaStatus) { 
	    BufferedImage tmp = img;
	    img = converter.createDelta(clientImg, img);
	    tmp.flush();
	}
	
	// Encode to JPEG image data
	try {
	    JPEGArray = converter.encodeJPEG(img, JPEGQuality);
	} catch (Exception e) {
	    throw new FatalServerException();
	}
	
	// Encrypt image data if enabled
	if (encryptionStatus) {
	    byte[] plain = new byte[8];
	    byte[] cipher = new byte[8];
	    encryptedJPEGArray = new byte[JPEGArray.length];
	    
	    int i;
	    for (i = 0; i < JPEGArray.length - 8; i = i + 8) {
		System.arraycopy(JPEGArray, i, plain, 0, 8);
		encrypter.encrypt(plain, cipher);
		System.arraycopy(cipher, 0, encryptedJPEGArray, i, 8);
	    }
	    
	    // The last bytes (less than 8) will be sent unencrypted
	    System.arraycopy(JPEGArray, i, encryptedJPEGArray, i, JPEGArray.length - i);
	}
    }

    
    /**
     * Gets the last image data
     */
    public byte[] getImage() throws RemoteException {
	if (encryptionStatus) return encryptedJPEGArray;
	return JPEGArray;
    }


    /**
     * Restores the image from the encoded format. This
     * is used to be able to base next delta precompression
     * on the same image that the client received
     */
    public void restoreImage() throws RemoteException, 
                                      FatalServerException, 
				      IncompatibleImageException {
	
	BufferedImage img = null;
	try {
	    img = converter.decodeJPEG(JPEGArray);
	} catch (IOException e) {
	    throw new FatalServerException();
	}

	if (deltaStatus) {
	    BufferedImage tmp = img;
	    img = converter.restoreDelta(clientImg, img);
	    tmp.flush();
	}				
	
	BufferedImage tmp = clientImg;
	clientImg = img;
	tmp.flush();
    }


    /**
     * Unexports this object and performs a neat printout.
     */
    public void logout() throws RemoteException {
	System.out.println(new Date() + " - " + username + " logged out");
	try {
	    this.unexportObject(this, true);
	} catch (Exception e) {}
    }
}	








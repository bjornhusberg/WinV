
// Standard imports:
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.rmi.*;
import java.applet.*;
import java.util.*;

// Project imports:
import gui.*;
import imageupdate.*;
import security.*;
import rmiserver.RMIServerInterface;
import rmiserver.RMILoginInterface;
import imageconversion.*;

/**
 * This is the main class of the client. 
 * 
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class Client extends JApplet {
    
    /**
     * The gui of the client
     */
    private MainFrame gui;
    
    /**
     * A reference to the login server
     */
    private RMILoginInterface loginserver;

    /** 
     * A reference to the server
     */
    private RMIServerInterface server;
    
    /**
     * The image update thread used to repeatedly ask the server 
     * for new images
     */
    private ImageUpdateThread iu;
    
    /**
     * A handle for the current image
     */
    private BufferedImage img;

    /**
     * The image converter used to convert incoming images
     */
    private ImageConverter converter;
        
    /**
     * The cipher object used to decrypt incoming images
     */
    private IceKey decrypter;
    
    /**
     * A timer for measuring the time between image updates
     */
    private long timer;
    
    /**
     * The current status of delta precompression usage
     * (false = off / true = on)
     */
    private boolean deltaStatus;

    /**
     * The current status of encryption usage
     * (false = off / true = on)
     */
    private boolean encryptionStatus;

    /**
     * The current JPEG image quality value
     * (0.0 = lowest / 1.0 = highest)
     */
    private float JPEGQuality;

    /**
     * The horisontal position of the top left corner
     * of the current image
     */
    private int x;

    /**
     * The vertical position of the top left corner
     * of the current image
     */
    private int y;

    /**
     * The width of the current image
     */
    private int width;

    /**
     * The height of the current image
     */
    private int height;
    

    /**
     * The init-method of this applet connects to the 
     * login object on the server.
     */
    public void init() {
	
 	String rmi = this.getParameter("rmiaddress");
	String http = this.getParameter("httpaddress");
	
	try {
	    loginserver = (RMILoginInterface) Naming.lookup (rmi);
	} catch(Exception e) {
	    System.exit(-1);
	}
	
	converter = new ImageConverter();
	decrypter = new IceKey(0);
	gui = new MainFrame(this, http);
    }	
    

    /**
     * Does an image reset of the server and the client
     */
    public void reset() {
	iu.pause();

	try {
	    server.reset();
	    Dimension d = server.getScreenSize();
	    x = 0;
	    y = 0;
	    width = (int) d.getWidth();
	    height = (int) d.getHeight();
	    img = converter.createBlack(width, height);	
	} catch(Exception e) {}	
	
	iu.restart();	
    }
    

    /**
     * Pauses the image update thread
     */
    public void pause() {
	iu.pause();
    }
    

    /**
     * Restarts the image update thread
     */
    public void restart() {
	iu.restart();
    }
    

    /**
     * Sets the image in the imagePane. 
     * The byte array is first decrypted if nedded
     * and then decoded into a BufferedImage.
     * The image is delta-restored if needed
     * and finally passed to the gui for 
     * display.
     *
     * @param JPEGArray the incoming image data buffer
     */
    public synchronized void setImage(byte[] JPEGArray) {

	try {
	    // Update the statusbar
	    gui.setSize(JPEGArray.length);
	    long newTime = new Date().getTime();
	    gui.setTime( newTime - timer);
	    timer = newTime;	

	    // Decrypt if needed
	    if (encryptionStatus) {
		byte[] plain = new byte[8];
		byte[] cipher = new byte[8];
		for (int i = 0; i < JPEGArray.length - 8; i = i + 8) {
       		    System.arraycopy(JPEGArray, i, cipher, 0, 8);
		    decrypter.decrypt(cipher, plain);
		    System.arraycopy(plain, 0, JPEGArray, i, 8);
		}
	    }
	  
	    // Decode the JPEG image data
	    BufferedImage newimg = converter.decodeJPEG(JPEGArray);
	    
	    // Delta-restore if needed
	    if (deltaStatus) {
		BufferedImage tmp = newimg;
		newimg = converter.restoreDelta(img, newimg);
		tmp.flush();
	    }
	    
	    // Send to gui and flush old image
	    BufferedImage tmp = img;
	    img = newimg;
	    gui.setImage(img);
	    tmp.flush();
		    
	} catch(Exception e){} // Ignore errors!
    }
    
    /**
     * Performs the client-side login ritual.
     * The incoming login request key is hashed
     * with the password and returned to the server.
     * If the login is successfull a RMIServer is
     * received and an ImageUpdateThread is started.
     *
     * @param username the username
     * @param password the password
     * @return true if successfull, false otherwise
     */
    public boolean login(String username, String password) {
	
	// Perform the login
	try {
	    byte[] key = loginserver.loginrequest();
	    byte[] hash = PasswordHash.hash(key, password);
	    server = loginserver.login(username, hash);
        }
	catch(Exception e) {
	    return false; // Return false if login failed
        }

	try {
	    // Get settings from server
	    deltaStatus = server.getDeltaStatus();
	    encryptionStatus = server.getEncryptionStatus();
	    JPEGQuality = server.getJPEGQuality();

	    // Reset server and initialize the image
	    server.reset();
	    Dimension d = server.getScreenSize();
	    x = 0;
	    y = 0;
	    width = (int) d.getWidth();
	    height = (int) d.getHeight();
	    img = converter.createBlack(width, height);	
	} catch (Exception e) {} // Ignore errors!		  
	
	// Initialize the decrypter with the password
	byte[] passwd = password.getBytes();
	byte[] key = new byte[8];
        for (int i = 0; i < 8; i++)
	    key[i] = passwd[i % passwd.length];
	decrypter.set(key);
	
	// Start an ImageUpdateThread
	iu = new ImageUpdateThread(this, server);
	
	// Reset the timer
	timer = new Date().getTime();	
	
	// Login successfull
	return true;
    }
    

    /**
     * Logs out the user
     */
    public void logout() {
	try {
	    // Tell the server we're logging out
	    server.logout();
	} catch(Exception e) {}
	// Remove the reference
	server = null;
    }
    

    /**
     * Set the new locked area to use when requesting
     * images
     *
     * @param nx the new x value
     * @param ny the new y value
     * @param nwidth the new width value
     * @param nheight the new height value
     */
    public void setArea(int nx, int ny, int nwidth, int nheight) {
	try {
	    // Pause the image update thread
	    iu.pause();
	    
	    // Tell the server about the new area
	    server.setArea(nx, ny, nwidth, nheight);
	    
	    // Flush the current image and create a new one
	    img.flush();
 	    img = converter.createBlack(nwidth, nheight);
	    
	    // Set the new image size
	    x = nx;
	    y = ny;
            height = nheight;
	    width = nwidth;
	    
	    // Restart the image update thread
	    iu.restart();
	} catch(Exception e) {} // Ignore errors!		
    }


    /**
     * Sets the status bar status string
     *
     * @param status the string to display
     */
    public void setStatus(String status) {
	gui.setStatus(status);
    }
    

    /**
     * Gets the horisontal position of the top left
     * corner of the current image area
     *
     * @return the x value
     */
    public int getx() {
	return x;
    }

    
    /**
     * Gets the vertical position of the top left
     * corner of the current image area
     *
     * @return the y value
     */
    public int gety() {
	return y;
    }
    

    /**
     * Gets the width of the current image area
     *
     * @return the width
     */
    public int getWidth() {
	return width;
    }
    

    /**
     * Gets the height of the current image area
     *
     * @return the height
     */
    public int getHeight() {
	return height;
    }
    
    
    /**
     * Forwards a mousePressed event to
     * the server
     *
     * @param e the MouseEvent to pass to the server
     */
    public void mousePressed(MouseEvent e) {
	try {
	    server.mousePressed(e);
	} catch (Exception x) {}
    }
    
    /**
     * Forwards a mouseReleased event to
     * the server
     *
     * @param e the MouseEvent to pass to the server
     */
    public void mouseReleased(MouseEvent e) {
	try {
	    server.mouseReleased(e);
	} catch (Exception x) {}
    }
    

    /**
     * Forwards a mouseMoved event to
     * the server
     *
     * @param e the MouseEvent to pass to the server
     */
    public void mouseMoved(MouseEvent e) {
	try {
	    server.mouseMoved(e);
	} catch (Exception x) {}
    }
    

    /**
     * Forwards a keyPressed event to
     * the server
     *
     * @param e the KeyEvent to pass to the server
     */
    public void keyPressed(KeyEvent e) {
	try {
	    server.keyPressed(e);
	} catch (Exception x) {}
    }

    
    /**
     * Forwards a keyReleased event to
     * the server
     *
     * @param e the KeyEvent to pass to the server
     */
    public void keyReleased(KeyEvent e) {
	try {
	    server.keyReleased(e);
	} catch (Exception x) {}
    }
    
    
    /**
     * Sets both server side and client side
     * delta status variables
     *
     * @param b the boolean value of the new delta status
     */
    public void setDeltaStatus(boolean b) {
	deltaStatus = b;
	try {
	    server.setDeltaStatus(b);
	    img = converter.createBlack(width, height);
	} catch (Exception e) {}
    }
   

    /**
     * Gets the current delta status
     *
     * @return the current delta status
     */
    public boolean getDeltaStatus() {
        return deltaStatus;
    }
    

    /**
     * Sets both server side and client side
     * encryption status variables
     *
     * @param b the boolean value of the new encryption status
     */
    public void setEncryptionStatus(boolean b) {
	encryptionStatus = b;
	try {
	    server.setEncryptionStatus(b);
	} catch (Exception e) {}
    }
    

    /**
     * Gets the current encryption status
     *
     * @return the current encryption status
     */
    public boolean getEncryptionStatus() {
	return encryptionStatus;
    }
    

    /**
     * Sets both server side and client side
     * JPEG image quality value
     *
     * @param quality the new value of the JPEG image quality
     */
    public void setJPEGQuality(float quality) {
	JPEGQuality = quality;
	try {
	    server.setJPEGQuality(quality);
	} catch (Exception e) {}
    }
    
    
    /**
     * Gets the current JPEG image quality value
     *
     * @return the current JPEG image quality
     */
    public float getJPEGQuality() {
	return JPEGQuality;
    }
}

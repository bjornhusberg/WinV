package imageupdate;

// Standard imports:
import java.awt.image.*;

// Project imports:
import Client;
import gui.*;
import rmiserver.RMIServerInterface;


/**
 * This thread class loops infinitely asking
 * the server for new images. 
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class ImageUpdateThread extends Thread {

    /**
     * A reference for holding the last 
     * received image data
     */
    private byte[] JPEGArray;

    /**
     * A reference to the server
     */
    private RMIServerInterface server;

    /**
     * The client that executed this thread
     */
    private Client client;

    /**
     * The lock that is shared with the
     * ImageRestoreThread
     */
    private Object lock;

    /**
     * boolean to tell that this object
     * is done
     */
    private boolean done;

    /**
     * boolean to tell that this object
     * is in pause state
     */
    private boolean paused;

    /**
     * booelan to tell that someone has
     * asked this object to pause
     */
    private boolean pausereq;

    /**
     * A reference to the ImageRestoreThread
     */
    private ImageRestoreThread restorer;


    /**
     * The constructor creates and executes an 
     * ImageRestoreThread and then executes this
     * thread
     *
     * @param c the client that started this thread
     * @param s a reference to the server
     */
    public ImageUpdateThread(Client c, RMIServerInterface s) {
	client = c;
	server = s;
	lock = new Object();
	paused = false;
	pausereq = false;
	done = true;
	restorer = new ImageRestoreThread (server, this, lock, client);
	restorer.start();
	this.start();
    }


    /**
     * Sets a pause request and then waits until
     * the thread is paused
     */
    public void pause() {
	pausereq = true;
	while (!paused);
	client.setStatus("paused");
    }


    /**
     * Restarts the thread
     */
    public void restart() {
	pausereq = false;
	paused = false;	
    }

    /**
     * Checks if this thread is done
     *
     * @return true if done / false if busy
     */
    public boolean isDone() {
	return done;
    }

    /**
     * Sets the status to busy
     */
    public void setUndone() {
	done = false;
    }

    /**
     * Loops infinitely alternating with the 
     * ImageRestoreThread while asking the 
     * server for new images
     */
    public void run() {
	while (true) {
	    client.setStatus("running");
	    while (!restorer.isDone()) this.yield();
	    try {  
		synchronized(lock) {
		    JPEGArray = server.getImage();
		}
		client.setImage(JPEGArray);
		if (pausereq) paused = true;
		while (paused) this.yield();
		restorer.setUndone();
		done = true;
	    } catch (Exception e) {}
	}
    }
}


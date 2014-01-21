package imageupdate;

// Standard imports:
import java.awt.image.*;

// Project imports:
import Client;
import gui.*;
import rmiserver.RMIServerInterface;


/**
 * This thread class is used by the ImageUpdateThread
 * to ask the server to prepare and restore a new
 * image while the client is computing the last
 * one
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class ImageRestoreThread extends Thread {

    /**
     * A reference to the server
     */
    private RMIServerInterface server;

    /**
     * The ImageUpdateThread that owns this object
     */
    private ImageUpdateThread updater;

    /**
     * The done-variable is used to tell the
     * ImageUpdateThread that we are finished
     */
    private boolean done;

    /**
     * The lock that is shared with the 
     * ImageUpdateThread object
     */
    private Object lock;

    /**
     * The client that holds settings information
     */
    private Client client;


    /**
     * The constructor
     * 
     * @param s a reference to the server
     * @param t the ImageUpdateThread that created this object
     * @param l the lock that is used to syncronize the events
     * @param c the client that holds settings information
     */
    public ImageRestoreThread (RMIServerInterface s, ImageUpdateThread t, Object l, Client c) {
	client = c;
	server = s;
	updater = t;
	lock = l;
	done = false;
    }


    /**
     * This method is used to check if the object is done
     */
    public boolean isDone() {
	return done;
    }
    

    /**
     * This method is used to tell this object that it
     * needs to take another loop
     */
    public void setUndone() {
	done = false;
    }

    
    /**
     * The thread loops and alternates with
     * the ImageUpdateThread
     */
    public void run() {
	while (true) {
	    while (!updater.isDone()) this.yield();
	    synchronized(lock) {
		try {
		    server.prepareImage();
		    updater.setUndone();
        	    done = true;
		    if (client.getDeltaStatus())
			server.restoreImage();		
		} catch(Exception e) {}
	    }
	}
    }
}


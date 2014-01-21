package gui;

// Standard imports:
import java.awt.event.*;

// Project imports:
import Client;


/**
 * This class is responsible for performing a 
 * logout when the client gui window is closed.
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class MainFrameWindowListener extends WindowAdapter {
    
    /**
     * The client
     */
    private Client client;
    
    /**
     * The constructor
     *
     * @param c the Client
     */
    public MainFrameWindowListener(Client c) {
	super();
	client = c;
    }
    
    /**
     * Asks the client to do a logout
     *
     * @param e the WindowEvent
     */
    public void windowClosing(WindowEvent e) {
	super.windowClosing(e);
	client.logout();	
    }
}


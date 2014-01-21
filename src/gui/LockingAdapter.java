package gui;

// Standard imports:
import javax.swing.event.*;
import java.awt.event.*;

// Project imports:
import Client;


/**
 * This class handles the mouseinputs when the user
 * wants to draw a new image lock area on the screen
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class LockingAdapter extends MouseInputAdapter {

    /**
     * The x value of the mousePress
     */
    private int x1;

    /**
     * The y value of the mousePress
     */
    private int y1;

    /**
     * The x value of the mouseRelease
     */
    private int x2;

    /**
     * The y value of the mouseRelease
     */
    private int y2;
    
    /**
     * The client
     */
    private Client client;

    /**
     * The imagePanel that this object is listening to
     */
    private ImagePanel imagePane;
    

    /**
     * The constructor
     * 
     * @param c the Client
     * @param ip the ImagePanel
     */
    public LockingAdapter (Client c, ImagePanel ip) {
	client = c;
	imagePane = ip;
    }		
    

    /**
     * Activates this adapter and inactivates all
     * others
     */
    public void activate() {
	client.pause();
	imagePane.removeMouseMotionListener(imagePane);
	imagePane.removeMouseListener(imagePane);
	imagePane.addMouseMotionListener(this);
	imagePane.addMouseListener(this);
    }
    
    
    /**
     * Stores the position
     *
     * @param e the incoming mouse event
     */
    public void mousePressed(MouseEvent e) {
	x1 = e.getX();
	y1 = e.getY();
    }
    

    /**
     * Stores the position and sets a temporary outline
     * 
     * @param e the incoming mouse event
     */
    public void mouseDragged(MouseEvent e) {
	
	int x2 = e.getX();
	int y2 = e.getY();
	int x1t = x1;
	int y1t = y1;
	if (x2 < x1) {
	    x1t = x2;
	    x2 = x1;
	}	
	if (y2 < y1) {
	    y1t = y2;
	    y2 = y1;
	}
	imagePane.outline(x1t, y1t, x2 - x1t, y2 - y1t);
    }
    

    /**
     * Stores the position and sets the new area. 
     * Then removes this adapter and restarts the
     * client
     *
     * @param e the incoming mouse event
     */
    public void mouseReleased(MouseEvent e) {
	int x2 = e.getX();
	int y2 = e.getY();
	if (x2 < x1) {
	    int tmp = x2;
	    x2 = x1;
	    x1 = tmp;
	}	
	if (y2 < y1) {
	    int tmp = y2;
	    y2 = y1;
	    y1 = tmp;
	}
	
	client.setArea(x1 + 1, y1 + 1, Math.max(x2 - x1 - 1, 0), 
		       Math.max(y2 - y1 - 1, 0)); 
	
	imagePane.removeMouseMotionListener(this);
	imagePane.removeMouseListener(this);
	imagePane.addMouseMotionListener(imagePane);
	imagePane.addMouseListener(imagePane);
	client.restart();
    }
}





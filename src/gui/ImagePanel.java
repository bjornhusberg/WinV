package gui;

// Standard imports:
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

// Project imports:
import imageconversion.*;
import Client;

/**
 * This part of the gui handles the panel
 * that displays the images. It is extenting
 * JPanel but also acts as MouseInputListener
 * and KeyListener for all input events on
 * the image area
 * 
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class ImagePanel extends JPanel implements  MouseInputListener, 
						   KeyListener {
    
    /**
     * The smallest interval between two
     * forwarded mouse move events
     */
    private static long interval = 100; // 100 ms
    
    /**
     * Static variable for setting the maximum
     * size of the imagepanel
     */
    private static int MAXDIM = 2000;
    
    /**
     * The client that owns the gui
     */
    private Client client;

    /**
     * The main image
     */
    private BufferedImage img;

    /**
     * A representation of the time when
     * the last mouse move event was recoreded
     */
    private long lastmove;

    /**
     * An ImageConverter that is used 
     * to create black images
     */
    private ImageConverter converter;

    /**
     * boolean that indicates weather a 
     * moving outline shall be drawn or not
     */
    private boolean movingoutline;

    /**
     * The horisontal position of the upper left
     * corner of the moving outline
     */
    private int outlinex;

    /**
     * The vertical position of the upper left
     * corner of the moving outline
     */
    private int outliney;

    /**
     * The width of the moving outline
     */
    private int outlinewidth;
    
    /**
     * The height of the moving outline
     */
    private int outlineheight;


    /**
     * The constructor
     *
     * @param c the Client that owns the gui
     */
    public ImagePanel(Client c) {
	super();
	client = c;
	movingoutline = false;
	addMouseMotionListener(this);
	addMouseListener(this);
	addKeyListener(this);
	lastmove = new Date().getTime();
	converter = new ImageConverter();
	img = converter.createBlack(MAXDIM, MAXDIM);
    }
    
    /**
     * Paints the imagePanel by drawing the 
     * image and any outlines
     *
     * @param g the Graphics object to draw to
     */
    public void paint(Graphics g) {
	g.drawImage(img, 0, 0, this);
	g.setColor(Color.red);
	
	if (movingoutline) {
	    g.drawRect(outlinex, outliney, outlinewidth, outlineheight);
	    movingoutline = false;
	} else {
	    g.drawRect(client.getx(), client.gety(), client.getWidth() - 1, 
		       client.getHeight() - 1);
	}
    }
    
    
    /**
     * Sets the area of the moving outline
     *
     * @param x the x value of the outline area
     * @param y the y value of the outline area
     * @param width the width of the outline area
     * @param height the height of the outline area
     */
    public void outline(int x, int y, int width, int height) {
	outlinex = x;
	outliney = y;
	outlinewidth = width;
	outlineheight = height;
	movingoutline = true;
	repaint(new Rectangle(0, 0, MAXDIM, MAXDIM));
    }

    
    /**
     * Draws the image and repaints the imagePanel
     *
     * @param newImg the image to draw
     */
    public void setImage(BufferedImage newImg) {
	Graphics g = img.getGraphics();
	int x = client.getx();
	int y = client.gety();
	g.drawImage(newImg, x, y, this);
	repaint(0, 0, MAXDIM, MAXDIM);
    }


    /**
     * Tells the event-handler that this object
     * can manage focus
     *
     * @return true
     */
    public boolean isManagingFocus() {
	return true;
    }


    /**
     * Called when the user clicks on the image panel.
     * Does nothing
     *
     * @param e the incoming MouseEvent
     */
    public void mouseClicked(MouseEvent e) {
    }
     

    /**
     * Called when the mousepointer enters the image panel.
     * Puts a request focus a la X-Mouse 
     *
     * @param e the incoming MouseEvent
     */
    public void mouseEntered(MouseEvent e) {
	requestFocus();
    }


    /**
     * Called when the mousepointer leaves the image panel.
     * Does nothing
     *
     * @param e the incoming MouseEvent
     */
    public void mouseExited(MouseEvent e) {
    }

            
    /**
     * Called when the user presses a mousebutton on the 
     * image panel. Puts a request focus a la X-Mouse
     * and forwards the event to the server via the client 
     *
     * @param e the incoming MouseEvent
     */
    public void mousePressed(MouseEvent e) {
	requestFocus();
	client.mousePressed(e);
    }

            
    /**
     * Called when the user releases a mousebutton on the 
     * image panel. Forwards the event to the server via 
     * the client 
     *
     * @param e the incoming MouseEvent
     */
    public void mouseReleased(MouseEvent e) {
	client.mouseReleased(e);
    }

    
    /**
     * Called when the user drags the mouse on the 
     * image panel. Redirects to a mouseMoved event
     *
     * @param e the incoming MouseEvent
     */
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
        

    /**
     * Called when the user moves the mouse on the 
     * image panel. Checks if enough time has passed
     * since the last forwarded move before it 
     * sends the move to the server via the client
     *
     * @param e the incoming MouseEvent
     */
    public void mouseMoved(MouseEvent e) {

	long now = new Date().getTime();
	if (now - lastmove > interval) {
	    client.mouseMoved(e);
	    lastmove = now;
	}
    }


    /**
     * Called when the user press a key while
     * the image manages the focus. Forwards
     * the KeyEvent to the server via the client
     *
     * @param e the incoming KeyEvent
     */
    public void keyPressed(KeyEvent e) {
	client.keyPressed(e);
    }


    /**
     * Called when the user release a key while
     * the image manages the focus. Forwards
     * the KeyEvent to the server via the client
     *
     * @param e the incoming KeyEvent
     */
    public void keyReleased(KeyEvent e) {
	client.keyReleased(e);
    }
    

    /**
     * Called when the user types a key while
     * the image manages the focus. Does
     * nothing.
     *
     * @param e the incoming KeyEvent
     */
    public void keyTyped(KeyEvent e) {
    }
}


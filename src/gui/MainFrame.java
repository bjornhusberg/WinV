package gui;

// Standard imports:
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;

// Project imports:
import Client;

/**
 * This is the main class of the gui.
 * 
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class MainFrame extends JFrame implements ActionListener{
    
    /**
     * The toolbar
     */
    private JToolBar toolbar;
    
    /**
     * The exit icon
     */
    private ImageIcon exitIcon;
    
    /**
     * The exit button
     */
    private JButton exitButton;

    /**
     * The reset icon
     */
    private ImageIcon resetIcon;

    /**
     * The reset button
     */
    private JButton resetButton;

    /**
     * The settings icon
     */
    private ImageIcon settingsIcon;

    /**
     * The settings button
     */
    private JButton settingsButton;

    /**
     * The play icon
     */
    private ImageIcon playIcon;

    /**
     * The pause icon
     */
    private ImageIcon pauseIcon;

    /**
     * The pause button
     */
    private JButton pauseButton;

    /**
     * The lock icon
     */
    private ImageIcon lockIcon;	

    /**
     * The lock button
     */
    private JButton lockButton;

    /**
     * The error icon
     */
    private ImageIcon errorIcon;

    /**
     * The settings dialog
     */    
    private SettingsDialog settingsDialog;

    /**
     * The login dialog
     */
    private LoginDialog loginDialog; 

    /**
     * The client
     */
    private Client client;

    /**
     * The image pane
     */
    private ImagePanel imagePane;

    /**
     * The image scrollpane
     */
    private JScrollPane imageScrollPane;

    /**
     * The locking mouse adapter
     */   
    private LockingAdapter lockingAdapter;

    /**
     * The status bar
     */
    private StatusBar statusBar;

    /**
     * Displays an error message dialog
     *
     * @param name the name of the window
     * @param message the error message
     */
    public void errorMessage(String name, String message) {
	JOptionPane.showMessageDialog(this, message, name, JOptionPane.ERROR_MESSAGE, errorIcon);
    }
    
    /**
     * Catches ActionEvents from all buttons in the gui
     * including dialogs other than the MainFrame.
     *
     * @param event the incoming ActionEvent
     */
    public void actionPerformed(ActionEvent event) {
	
	// Login ok button
	if (event.getActionCommand() == "LOGIN_OK") {
	    loginDialog.setVisible(false);
	    if (client.login(loginDialog.getUsername(), loginDialog.getPassword())) {
		enableButtons(true);
		settingsDialog.setDeltaStatus(client.getDeltaStatus());
		settingsDialog.setEncryptionStatus(client.getEncryptionStatus());
		settingsDialog.setJPEGQuality(client.getJPEGQuality());
		imagePane.setPreferredSize(new Dimension(client.getWidth(), client.getHeight()));
		imageScrollPane.update(imageScrollPane.getGraphics());
	    } else {
		errorMessage("Login error", "Login failed.\nYour IP has been logged.");
		loginDialog.reset();
		loginDialog.setVisible(true);
	    }
	}

	// Login cancel button
	if (event.getActionCommand() == "LOGIN_CANCEL") {
	    settingsDialog.setVisible(false);
	    loginDialog.setVisible(false);
	    setVisible(false);
	    settingsDialog.dispose();
	    loginDialog.dispose();
	    dispose();
	}
	
	// Main exit button
	if (event.getActionCommand() == "MAIN_EXIT") {
	    settingsDialog.setVisible(false);
	    enableButtons(false);
	    setTime(0);
	    setSize(0);
	    setStatus("");
	    client.logout();
            loginDialog.reset();
	    loginDialog.setVisible(true);
	}

	// Main reset button
	if (event.getActionCommand() == "MAIN_RESET") {
	    client.reset();
	    imagePane.setPreferredSize(new Dimension(client.getWidth(), client.getHeight()));
	    imageScrollPane.update(imageScrollPane.getGraphics());
	}
	
	// Main settings button
	if (event.getActionCommand() == "MAIN_SETTINGS") {
	    enableButtons(false);
	    settingsDialog.setVisible(true);
	}
	
	// Main pause button
	if (event.getActionCommand() == "MAIN_PAUSE") {
	    enableButtons(false);
	    client.pause();
	    pauseButton.setActionCommand("MAIN_PLAY");
	    pauseButton.setIcon(playIcon);	
	    pauseButton.setEnabled(true);
	}
	
	// Main play button
	if (event.getActionCommand() == "MAIN_PLAY") {
	    pauseButton.setActionCommand("MAIN_PAUSE");
	    pauseButton.setIcon(pauseIcon);	
	    client.restart();
	    enableButtons(true);
	}
	
	// Main lock button
	if (event.getActionCommand() == "MAIN_LOCK") {
	    lockingAdapter.activate();
	}
	
	// Settings ok button
	if (event.getActionCommand() == "SETTINGS_OK") {
	    settingsDialog.setVisible(false);
	    enableButtons(true);
	    client.pause();
            client.setDeltaStatus(settingsDialog.getDeltaStatus());
	    client.setEncryptionStatus(settingsDialog.getEncryptionStatus());
	    client.setJPEGQuality(settingsDialog.getJPEGQuality());
	    client.restart();
	}	

	// Settings cancel button
	if (event.getActionCommand() == "SETTINGS_CANCEL") {
	    settingsDialog.setVisible(false);
	    enableButtons(true);
	    
            settingsDialog.setDeltaStatus(client.getDeltaStatus());
	    settingsDialog.setEncryptionStatus(client.getEncryptionStatus());
	    settingsDialog.setJPEGQuality(client.getJPEGQuality());
	}
    }
    
    
    /**
     * Sets all buttons in the MainFrame to 
     * the enabled status given by the parameter
     *
     * @param b the new status of the buttons
     */
    private void enableButtons (boolean b) {
	exitButton.setEnabled(b);
	settingsButton.setEnabled(b);
	resetButton.setEnabled(b);
	pauseButton.setEnabled(b);
	lockButton.setEnabled(b);
    }
    
    
    /**
     * Forwards a new image to the image pane
     *
     * @param newImage the new image
     */
    public void setImage (BufferedImage newImage) {
	imagePane.setImage(newImage);
	
    }
    
    
    /**
     * Sets the status string in the status bar
     *
     * @param status the status string
     */
    public void setStatus(String status) {
	Dimension d = statusBar.statusLabel.getPreferredSize();
	statusBar.statusLabel.setText(" Status: " + status);
	statusBar.statusLabel.setPreferredSize(d);
    }


    /**
     * Sets the transfer size string in the status bar
     *
     * @param size the number of transfered bytes 
     */
    public void setSize(int size) {
	Dimension d = statusBar.sizeLabel.getPreferredSize();
	statusBar.sizeLabel.setText(" Transfer size: " + String.valueOf(size) + "b");
	statusBar.sizeLabel.setPreferredSize(d);
    }

    /**
     * Sets the update time in the status bar
     * 
     * @param time the update time in milliseconds
     */	
    public void setTime(long time) {
	Dimension d = statusBar.timeLabel.getPreferredSize();
	statusBar.timeLabel.setText(" Update time: " + String.valueOf(time) + "ms");
	statusBar.timeLabel.setPreferredSize(d);
    }

    /**
     * The constructor initializes the entire gui
     *
     * @param c the client
     * @param httpaddress the http address to the images
     */
    public MainFrame(Client c, String httpaddress) {
	
	super("WinV");
	
	client = c;
	
	// The status bar
	statusBar = new StatusBar();
	setTime(0);
	setSize(0);
	setStatus("");
	
	// Load the images	
	try {
/*
	    exitIcon = new ImageIcon(new URL(httpaddress + "exit.gif"));	
	    resetIcon = new ImageIcon(new URL(httpaddress + "reset.gif"));	
	    settingsIcon = new ImageIcon(new URL(httpaddress + "settings.gif"));	
	    playIcon = new ImageIcon(new URL(httpaddress + "play.gif"));	
	    pauseIcon = new ImageIcon(new URL(httpaddress + "pause.gif"));	
	    errorIcon = new ImageIcon(new URL(httpaddress + "stop.gif"));
	    lockIcon = new ImageIcon(new URL(httpaddress + "lock.gif"));
*/
	} catch (Exception e) {}
	
	// The exit button
	exitButton = new JButton(exitIcon);
	exitButton.setToolTipText("Log out");
	exitButton.setActionCommand("MAIN_EXIT");
	exitButton.addActionListener(this);

	// The reset button
	resetButton = new JButton(resetIcon);
	resetButton.setToolTipText("Reset");
	resetButton.setActionCommand("MAIN_RESET");
	resetButton.addActionListener(this);

	// The settings button
	settingsButton = new JButton(settingsIcon);
	settingsButton.setToolTipText("Settings");
	settingsButton.setActionCommand("MAIN_SETTINGS");
	settingsButton.addActionListener(this);

	// The pause button
	pauseButton = new JButton(pauseIcon);
	pauseButton.setToolTipText("Pause/Restart");
	pauseButton.setActionCommand("MAIN_PAUSE");
	pauseButton.addActionListener(this);

	// The lock button
	lockButton = new JButton(lockIcon);
	lockButton.setToolTipText("Lock to area");
	lockButton.setActionCommand("MAIN_LOCK");
	lockButton.addActionListener(this);

	// The toolbar
	toolbar = new JToolBar();
	toolbar.add(exitButton);
	toolbar.add(pauseButton);
	toolbar.add(resetButton);
	toolbar.add(lockButton);
	toolbar.add(settingsButton);

	// The imagepane
        imagePane = new ImagePanel(client);
	imageScrollPane = new JScrollPane(imagePane);  

	// The content pane
	JPanel contentPane = new JPanel(new BorderLayout(), false);
	contentPane.add(toolbar, BorderLayout.NORTH);
	contentPane.add(imageScrollPane, BorderLayout.CENTER);
 	contentPane.add(statusBar, BorderLayout.SOUTH);
	setContentPane(contentPane);

	// The locking mouse adapter
	lockingAdapter = new LockingAdapter(client, imagePane);

	// Round up       
	setSize(500, 400);
	setLocation(100, 100);
	enableButtons(false);
	this.addWindowListener(new MainFrameWindowListener(client));
	this.setVisible(true);
	settingsDialog = new SettingsDialog(this);
	loginDialog = new LoginDialog(this);
	loginDialog.setVisible(true);
    }
}

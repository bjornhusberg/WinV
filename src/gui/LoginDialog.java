package gui;

// Standard imports:
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;


/**
 * This class handles the login dialog. 
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class LoginDialog extends JDialog {
    
    /**
     * The info label
     */
    private JLabel infoLabel;
    
    /**
     * The username label
     */
    private JLabel usernameLabel;
    
    /**
     * The username text field
     */
    private JTextField usernameTextField;

    /**
     * The password label
     */
    private JLabel passwordLabel;

    /**
     * The password field
     */
    private JPasswordField passwordField;
    
    /**
     * The ok button
     */
    private JButton okButton;

    /**
     * The cancel button
     */
    private JButton cancelButton;
   

    /**
     * Gets the contents of the username textfield
     *
     * @return the username
     */
    public String getUsername() {
	return usernameTextField.getText();
    }
    
    /**
     * Gets the contents of the password textfield
     *
     * @return the password
     */
    public String getPassword() {
	return new String(passwordField.getPassword());
    }		
    
    /**
     * Clears the username and password textfields
     */
    public void reset() {
	usernameTextField.setText("");
	passwordField.setText("");
    }
    
    /**
     * The constructor for the dialog
     *
     * @param owner the MainFrame that owns this object
     */
    public LoginDialog(MainFrame owner) {
	
	super(owner, "Login", true);
	
	// Layout properties	
	GridBagLayout gridbagLayout = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();
	JPanel dialogPane = new JPanel(gridbagLayout);
	dialogPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	
	// Info label	
	constraints.anchor = GridBagConstraints.WEST;
	constraints.insets = new Insets(0,0,10,0);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
        infoLabel = new JLabel("Enter your username and password");	
	infoLabel.setForeground(Color.black);
	gridbagLayout.setConstraints(infoLabel, constraints);
	dialogPane.add(infoLabel);
	
	// Username label
	constraints.anchor = GridBagConstraints.EAST;
	constraints.insets = new Insets(0,0,0,5);
	constraints.gridwidth = 1;
        usernameLabel = new JLabel("Username:");
	gridbagLayout.setConstraints(usernameLabel, constraints);
	dialogPane.add(usernameLabel);
	
	// Username text field
	constraints.anchor = GridBagConstraints.CENTER;
	constraints.insets = new Insets(0,0,0,0);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
        usernameTextField = new JTextField();
	usernameTextField.setColumns(15);
	gridbagLayout.setConstraints(usernameTextField, constraints);
	dialogPane.add(usernameTextField);
	
	// Password label
	constraints.anchor = GridBagConstraints.EAST;
	constraints.insets = new Insets(0,0,0,5);
	constraints.gridwidth = 1;
        passwordLabel = new JLabel("Password:");
	gridbagLayout.setConstraints(passwordLabel, constraints);
	dialogPane.add(passwordLabel);
	
	// Password text field
	constraints.anchor = GridBagConstraints.CENTER;
	constraints.insets = new Insets(0,0,0,0);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	passwordField = new JPasswordField();
	passwordField.setColumns(15);
	gridbagLayout.setConstraints(passwordField, constraints);
	dialogPane.add(passwordField);
	
	// Option pane
	JPanel optionPane = 
	    new JPanel(new FlowLayout(FlowLayout.RIGHT), false);
	
	// ok button
        okButton = new JButton("OK");
	okButton.setActionCommand("LOGIN_OK");
	okButton.addActionListener(owner);
	optionPane.add(okButton);

	// cancel button
	cancelButton = new JButton("Cancel");
	cancelButton.setActionCommand("LOGIN_CANCEL");
	cancelButton.addActionListener(owner);
	optionPane.add(cancelButton);
	
	// Content pane
	JPanel contentPane = new JPanel(new BorderLayout(), false);
	contentPane.add(dialogPane, BorderLayout.CENTER);
	contentPane.add(optionPane, BorderLayout.SOUTH);
	
	// Round up	
	setContentPane(contentPane);
	pack();
	setLocationRelativeTo(getOwner());
	getRootPane().setDefaultButton(okButton);
	setResizable(false);
    }
}

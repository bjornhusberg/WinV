package gui;

// Standard imports:
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

// Project imports:
import java.util.Hashtable;


/**
 * This class displays the settings dialog.
 * 
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
public class SettingsDialog extends JDialog {
    
    /**
     * The delta radiobutton
     */
    private JRadioButton deltaRadioButton;
    
    /**
     * The encryption radiobutton
     */
    private JRadioButton encryptionRadioButton; 
    
    /**
     * The high quality label
     */
    private JLabel highLabel;
    
    /**
     * The low quality label
     */
    private JLabel lowLabel;
    
    /**
     * The JPEG quality label
     */
    private JLabel qualityLabel;
    
    /**
     * The quality slider label
     */
    private JSlider qualitySlider;
    
    /**
     * The ok button
     */
    private JButton okButton;
    
    /**
     * The cancel button
     */
    private JButton cancelButton;
    
    /**
     * Sets the status of the delta
     * radiobutton
     *
     * @param b the status
     */
    public void setDeltaStatus(boolean b) {
	deltaRadioButton.setSelected(b);
    }
    
    /**
     * Gets the status of the delta
     * radiobutton
     *
     * @return the status
     */
    public boolean getDeltaStatus() {
	return deltaRadioButton.isSelected();
    }
    
    /**
     * Sets the status of the encryption
     * radiobutton
     *
     * @param b the status
     */
    public void setEncryptionStatus(boolean b) {
	encryptionRadioButton.setSelected(b);
    }
    
    /**
     * Gets the status of the encryption
     * radiobutton
     *
     * @return the status
     */
    public boolean getEncryptionStatus() {
	return encryptionRadioButton.isSelected();
    }
    
    /**
     * Sets the value of the quality slider
     *
     * @param value the value
     */
    public void setJPEGQuality(float value) {
	qualitySlider.setValue((int) (value * 10));
    }
    
    /**
     * Gets the value of the quality slider
     *
     * @return the value of the quality slider
     */
    public float getJPEGQuality() {
	return (float) qualitySlider.getValue() / 10.0f;
    }
    
    /**
     * The constructor creates the settings dialog
     *
     * @param owner the MainFrame that owns this object
     */    
    public SettingsDialog(MainFrame owner) {
	
	super(owner, "Settings", true);
	
	// Layout properties
	GridBagLayout gridbagLayout = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();
	JPanel dialogPane = new JPanel(gridbagLayout);
	dialogPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	constraints.anchor = GridBagConstraints.WEST;
	constraints.insets = new Insets(0,0,10,0);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	
	// The ok button
    	okButton = new JButton("OK");
	okButton.setActionCommand("SETTINGS_OK");
	okButton.addActionListener(owner);
	
	// The cancel button
    	cancelButton = new JButton("Cancel");
	cancelButton.setActionCommand("SETTINGS_CANCEL");
	cancelButton.addActionListener(owner);
	
	// The delta radio button
    	deltaRadioButton = new JRadioButton("Use delta precompression");
	deltaRadioButton.setToolTipText("Delta precompression gives higher compression but slower update");
	gridbagLayout.setConstraints(deltaRadioButton, constraints);
	dialogPane.add(deltaRadioButton);
	
	// The encryption radio button
    	encryptionRadioButton = new JRadioButton("Use encryption");
	encryptionRadioButton.setToolTipText("Encryption is used for security but gives slower update");
	gridbagLayout.setConstraints(encryptionRadioButton, constraints);
	dialogPane.add(encryptionRadioButton);
	
	// New constraints
	constraints.anchor = GridBagConstraints.WEST;
	constraints.insets = new Insets(0,0,20,5);
	constraints.gridwidth = 1;
	
	// The quality label
    	qualityLabel = new JLabel("JPEG image quality");
	qualityLabel.setToolTipText("Higher image quality gives slower update");
	qualityLabel.setForeground(Color.black);
	gridbagLayout.setConstraints(qualityLabel, constraints);
	dialogPane.add(qualityLabel);
	
	// The quality slider
    	qualitySlider = new JSlider(0, 10, 7);
	qualitySlider.setToolTipText("Higher image quality gives slower update");
	qualitySlider.setMajorTickSpacing(5);
	qualitySlider.setMinorTickSpacing(1);
	qualitySlider.setPaintTicks(true);

	// The slider labels	
    	highLabel = new JLabel("High");
	highLabel.setForeground(Color.black);
	highLabel.setFont(new Font(highLabel.getFont().getName(), Font.BOLD, 10));

    	lowLabel = new JLabel("Low");
	lowLabel.setForeground(Color.black);
	lowLabel.setFont(new Font(lowLabel.getFont().getName(), Font.BOLD, 10));

	Hashtable labelTable = new Hashtable();
	labelTable.put(new Integer(0), lowLabel);
	labelTable.put(new Integer(10), highLabel);
	qualitySlider.setLabelTable(labelTable);	
        qualitySlider.setPaintLabels(true);
	qualitySlider.setSnapToTicks(true);

	// New constraints	
	constraints.anchor = GridBagConstraints.WEST;
	constraints.insets = new Insets(0,0,0,5);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	gridbagLayout.setConstraints(qualitySlider, constraints);
	dialogPane.add(qualitySlider);
	
	// The option pane
	JPanel optionPane = 
	    new JPanel(new FlowLayout(FlowLayout.RIGHT), false);
	
	optionPane.add(okButton);
	optionPane.add(cancelButton);

	// The content pane	
	JPanel contentPane = new JPanel(new BorderLayout(), false);
	contentPane.add(dialogPane, BorderLayout.CENTER);
	contentPane.add(optionPane, BorderLayout.SOUTH);
	
	setContentPane(contentPane);
	pack();
	setLocationRelativeTo(getOwner());
	getRootPane().setDefaultButton(okButton);
	setResizable(false);
    }
}

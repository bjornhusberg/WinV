package gui;

// Standard imports:
import javax.swing.*;
import java.awt.*;


/**
 * This class draws a statusbar consisting
 * of three fields: status, size and time
 *
 * @author Bjorn Husberg and Louise Tysk
 * @version 1.0
 */
class StatusBar extends JPanel {
    
    /**
     * The status label
     */
    protected JLabel statusLabel;
    
    /**
     * The transfer size label
     */
    protected JLabel sizeLabel;
    
    /**
     * The update time label
     */
    protected JLabel timeLabel;
    
    
    /**
     * The constructor creates the statusbar
     */
    public StatusBar() {
	
	// Layout properties
	GridBagLayout gridbagLayout = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();
	setLayout(gridbagLayout);
	constraints.insets = new Insets(0,0,0,0);
	constraints.fill = GridBagConstraints.HORIZONTAL; 
	constraints.weightx = 1.0;
	
	// The status label
	statusLabel = new JLabel("dummy");
	statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
	statusLabel.setForeground(Color.black);
	constraints.gridx = 0;
	gridbagLayout.setConstraints(statusLabel, constraints);
	add(statusLabel);
	
	// The size label
	sizeLabel = new JLabel("dummy");
	sizeLabel.setBorder(BorderFactory.createLoweredBevelBorder());
	sizeLabel.setForeground(Color.black);
	constraints.gridx = 1;
	gridbagLayout.setConstraints(sizeLabel, constraints);
	add(sizeLabel);
	
	// The time label
	timeLabel = new JLabel("dummy");
	timeLabel.setBorder(BorderFactory.createLoweredBevelBorder());
	timeLabel.setForeground(Color.black);
	constraints.gridx = 2;
	gridbagLayout.setConstraints(timeLabel, constraints);
	add(timeLabel);
    }
}






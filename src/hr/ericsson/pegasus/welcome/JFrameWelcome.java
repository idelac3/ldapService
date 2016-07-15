package hr.ericsson.pegasus.welcome;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class JFrameWelcome extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static JFrameWelcome jFrameWizard;
	
	public static JPanel jPanelBindSocket;
	
	public static JTextArea jTextAreaLDIF;
	public static JTextArea jTextAreaSchema;
	
	public static JCheckBox jCheckBoxEnableGUI;
	public static JTextField jTextFieldCountLimit;
	public static JCheckBox jCheckBoxEnableDebugging;
	public static JCheckBox jCheckBoxEnableMulticastReplication;
	public static JTextField jTextFieldMulticastInterface;
	public static JTextField jTextFieldMulticastGroup;
	public static JTextField jTextFieldMulticastPort;
	
	public JFrameWelcome() {
		setTitle("Welcome");
		setLocation(120, 120);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(new JTabbedPaneMain());
		pack();
		
		JFrameWelcome.jFrameWizard = this; 
	}

}

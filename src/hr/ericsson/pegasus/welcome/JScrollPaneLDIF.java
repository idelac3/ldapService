package hr.ericsson.pegasus.welcome;

import javax.swing.JScrollPane;

public class JScrollPaneLDIF extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JScrollPaneLDIF() {
		
		JTextAreaLDIFSchema jtextAreaLDIF = new JTextAreaLDIFSchema("ldif");
		
		setViewportView(jtextAreaLDIF);
		
		JFrameWelcome.jTextAreaLDIF = jtextAreaLDIF;
	}
}

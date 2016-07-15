package hr.ericsson.pegasus.welcome;

import javax.swing.JButton;

public class JButtonGeneralQuit extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JButtonGeneralQuit() {
		setText("Quit");
		setToolTipText("Quit this application.");
		addActionListener(new ActionListenerGeneralQuit());
	}
}

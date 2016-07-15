package hr.ericsson.pegasus.welcome;

import javax.swing.JButton;

public class JButtonGeneralStart extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JButtonGeneralStart() {
		setText("Start");
		setToolTipText("Start this application with selected arguments.");
		addActionListener(new ActionListenerGeneralStart());
	}
}

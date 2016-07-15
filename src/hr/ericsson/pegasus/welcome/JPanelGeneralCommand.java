package hr.ericsson.pegasus.welcome;

import javax.swing.JPanel;

public class JPanelGeneralCommand extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JPanelGeneralCommand() {
		add(new JButtonGeneralStart());
		add(new JButtonGeneralQuit());
	}
}

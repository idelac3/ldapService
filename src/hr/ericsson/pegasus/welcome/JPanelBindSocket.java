package hr.ericsson.pegasus.welcome;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class JPanelBindSocket extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JPanelBindSocket() {
		setLayout(new BorderLayout());
		add(new JScrollPaneBindSocket(), BorderLayout.PAGE_START);
		add(new JPanelSocketCommand(), BorderLayout.PAGE_END);
	}
}

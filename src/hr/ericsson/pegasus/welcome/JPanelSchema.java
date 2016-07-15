package hr.ericsson.pegasus.welcome;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class JPanelSchema extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JPanelSchema() {
		setLayout(new BorderLayout());
		add(new JLabel("Enter schema files here:"), BorderLayout.PAGE_START);
		add(new JScrollPaneSchema(), BorderLayout.CENTER);
	}
}

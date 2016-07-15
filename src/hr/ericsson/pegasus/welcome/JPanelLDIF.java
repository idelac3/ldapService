package hr.ericsson.pegasus.welcome;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class JPanelLDIF extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JPanelLDIF() {
		setLayout(new BorderLayout());
		add(new JLabel("Enter LDIF files here:"), BorderLayout.PAGE_START);
		add(new JScrollPaneLDIF(), BorderLayout.CENTER);
	}
}

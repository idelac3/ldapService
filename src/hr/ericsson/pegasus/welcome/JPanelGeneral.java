package hr.ericsson.pegasus.welcome;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class JPanelGeneral extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JPanelGeneral() {
		setLayout(new BorderLayout());
		add(new JScrollPaneGeneral(), BorderLayout.PAGE_START);
		add(new JPanelGeneralCommand(), BorderLayout.PAGE_END);
	}
}

package hr.ericsson.pegasus.welcome;

import javax.swing.JTabbedPane;

public class JTabbedPaneMain extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JTabbedPaneMain() {
		addTab("Socket bind", new JPanelBindSocket());
		addTab("LDIF", new JPanelLDIF());
		addTab("Schema", new JPanelSchema());
		addTab("General", new JPanelGeneral());
	}
}

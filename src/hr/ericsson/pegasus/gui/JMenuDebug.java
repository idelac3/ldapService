package hr.ericsson.pegasus.gui;

import javax.swing.JMenu;

/**
 * <H1>Debug, Menu Item</H1>
 * <HR>
 * @author eigorde
 *
 */

public class JMenuDebug extends JMenu {

	private static final long serialVersionUID = 1L;

	public JMenuDebug() {
		setText("Debug");
		add(new JMenuDebugEnabled());
		addSeparator();
		add(new JMenuDebugReloadMessageHandlers());
	}
}

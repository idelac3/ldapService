package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Debug, Reload MessageHandler instances, Menu Item</H1>
 * <HR>
 * @author eigorde
 *
 */

public class JMenuDebugReloadMessageHandlers extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuDebugReloadMessageHandlers() {
		setText("Reload MessageHandler instance(s)");
		addActionListener(
				new ActionListenerDebugReloadMessageHandlers());
	}
}

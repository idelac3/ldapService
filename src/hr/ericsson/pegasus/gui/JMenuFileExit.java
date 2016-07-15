package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Exit, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerFileExit}.
 * <HR>
 * @author eigorde
 *
 */
public class JMenuFileExit extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuFileExit() {
		setText("Exit");
		
		addActionListener(
				new ActionListenerFileExit());
	}
}

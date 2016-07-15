package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Information, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerFileInformation}.
 * <HR>
 * @author eigorde
 *
 */
public class JMenuFileInformation extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuFileInformation() {
		setText("Information");
		
		addActionListener(
				new ActionListenerFileInformation());
	}
}

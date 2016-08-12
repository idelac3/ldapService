package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Delete all entries, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerLDIFDeleteAllEntries}.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */

public class JMenuLDIFDeleteAllEntries extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuLDIFDeleteAllEntries() {
		setText("Delete all entries");
		
		addActionListener(
				new ActionListenerLDIFDeleteAllEntries());
	}
}

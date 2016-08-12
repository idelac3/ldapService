package hr.ericsson.pegasus.gui;

import javax.swing.JCheckBoxMenuItem;

import hr.ericsson.pegasus.Pegasus;


/**
 * <H1>Debugging enabled, Menu Item</H1>
 * <HR>
 * @author igor.delac@gmail.com
 *
 */

public class JMenuDebugEnabled extends JCheckBoxMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuDebugEnabled() {
		setText("Debugging enabled");
		setSelected(Pegasus.debugEnabled);
		addActionListener(
				new ActionListenerDebugEnabled());
	}
}

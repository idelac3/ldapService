package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import javax.swing.JCheckBoxMenuItem;

/**
 * <H1>Multicast synchronization, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerFileMulticastSync}.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class JMenuFileMulticastSync extends JCheckBoxMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuFileMulticastSync() {
		setText("Multicast synchronization");
		
		setSelected(Pegasus.multicastSync.isJoinPerformed());
		
		addActionListener(
				new ActionListenerFileMulticastSync());
	}
}

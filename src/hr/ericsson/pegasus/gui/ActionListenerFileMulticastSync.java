package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <H1>Multicast synchronization, ActionListener</H1>
 * <HR>
 * This action defined here will trigger join / leave of {@link Pegasus#multicastSync} instance.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class ActionListenerFileMulticastSync implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Pegasus.multicastSync != null) {
			try {
				if (Pegasus.multicastSync.isJoinPerformed()) {
					Pegasus.multicastSync.leave();
				}
				else {
					Pegasus.multicastSync.join();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}

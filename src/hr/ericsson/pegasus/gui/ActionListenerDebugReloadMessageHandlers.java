package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.ClientListener;
import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <H1>Reload MessageHandler instances, ActionListener</H1>
 * <HR>
 * This action defined here will cause on each {@link ClientListner} instance
 * a MessageHandler in channel pipeline to be reloaded.<BR>
 * <B>NOTE:</B> There will be a channel (with handlers) only if at least one client
 * is connected, otherwise this has no effect.
 * <HR>
 * @author eigorde
 *
 */
public class ActionListenerDebugReloadMessageHandlers implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		for (ClientListener listener : Pegasus.clientListenerList) {
			listener.reloadMessageHandlerInstance();
		}
		
	}

}

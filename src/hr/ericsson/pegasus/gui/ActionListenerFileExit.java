package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <H1>Exit, ActionListener</H1>
 * <HR>
 * This action defined here will terminate {@link Pegasus} application.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class ActionListenerFileExit implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);		
	}

}

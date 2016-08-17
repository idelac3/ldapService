package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <H1>Schema, Unload All, ActionListener</H1>
 * <HR>
 * This action defined here will disable schema validation(s).
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class ActionListenerSchemaUnloadAll implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		Pegasus.schema = null;		
		Pegasus.debug("All schema files unloaded, schema validation disabled now.");
	}

}

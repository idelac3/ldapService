package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.ConcurrentBackend;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * <H1>Delete all entries, ActionListener</H1>
 * <HR>
 * This action defined here will erase all entries on backend, see {@link Pegasus#myBackend}.
 * Backend Service will preserve a list of loaded LDIFs, see {@link ConcurrentBackend#reloadLDIFs()}.
 * <HR>
 * @author eigorde
 *
 */
public class ActionListenerLDIFDeleteAllEntries implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		String[] obj = {
				"This operation will erase all data on backend database.",
				"Delete all entries ?"};
		
		int returnCode = JOptionPane.showConfirmDialog(Pegasus.gui, obj, "Delete all entries", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (returnCode == JOptionPane.YES_OPTION) {
			Pegasus.myBackend.eraseEntries();
		}
	}

}

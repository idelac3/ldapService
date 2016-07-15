package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.ConcurrentBackend;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

/**
 * <H1>Reload LDIFs, ActionListener</H1>
 * <HR>
 * This action defined here will try to load all LDIFs on backend, see {@link ConcurrentBackend#reloadLDIFs()}.
 * Backend Service keeps a list of loaded LDIFs each time a new LDIF is loaded.
 * <HR>
 * @author eigorde
 *
 */
public class ActionListenerLDIFReload implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		int items = Pegasus.myBackend.getLoadedLDIFList().size() + 1;
		int counter = 1;
		String[] obj = new String[items];
		obj[0] = "This operation will try to load following LDIF files:";

		for (File file : Pegasus.myBackend.getLoadedLDIFList()) {
			obj[counter] = file.getAbsolutePath();
			counter++;
		}
		
		int returnCode = JOptionPane.showConfirmDialog(Pegasus.gui, obj, "Reload LDIF(s)", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (returnCode == JOptionPane.YES_OPTION) {
			try {
				Pegasus.myBackend.reloadLDIFs();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}

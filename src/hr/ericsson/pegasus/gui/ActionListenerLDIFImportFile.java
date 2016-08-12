package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * <H1>Import LDIF file, ActionListener</H1>
 * <HR>
 * This action defined here will load LDIF file, see {@link Pegasus#myBackend}.
 * Backend Service will have additional entries from newly added LDIF file.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class ActionListenerLDIFImportFile implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String ldifFile;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new LdifFileFilter());
		
		int returnCode = fileChooser.showOpenDialog(Pegasus.gui);
		
		if (returnCode == JFileChooser.APPROVE_OPTION) {
			ldifFile = fileChooser.getSelectedFile().getAbsolutePath();
			try {
				Pegasus.myBackend.ldifRead(new File(ldifFile));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}

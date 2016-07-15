package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import javax.swing.JMenu;

/**
 * <H1>File, Menu Item</H1>
 * <HR>
 * @author eigorde
 *
 */
public class JMenuFile extends JMenu {

	private static final long serialVersionUID = 1L;

	public JMenuFile() {
		setText("File");
		
		add(new JMenuFileTransferBackendData());
		if (Pegasus.multicastSync != null) {
			add(new JMenuFileMulticastSync());
		}
		addSeparator();
		add(new JMenuFileInformation());
		addSeparator();
		add(new JMenuFileExit());
		
	}
}

package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Import LDIF, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerLDIFImportFile}.
 * <HR>
 * @author eigorde
 *
 */

public class JMenuLDIFImportFile extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuLDIFImportFile() {
		setText("Import additional LDIF");
		
		addActionListener(
				new ActionListenerLDIFImportFile());
	}
}

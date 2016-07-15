package hr.ericsson.pegasus.gui;

import javax.swing.JMenu;

/**
 * <H1>LDIF, SubMenu Item</H1>
 * <HR>
 * @author eigorde
 *
 */

public class JMenuLDIF extends JMenu {

	private static final long serialVersionUID = 1L;

	public JMenuLDIF() {
		setText("LDIF");
		add(new JMenuLDIFDeleteAllEntries());
		add(new JMenuLDIFReload());
		add(new JMenuLDIFImportFile());
	}
}

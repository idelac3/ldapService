package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Reload LDIFs, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerLDIFReload}.
 * <HR>
 * @author eigorde
 *
 */

public class JMenuLDIFReload extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuLDIFReload() {
		setText("Reload LDIF(s)");
		
		addActionListener(
				new ActionListenerLDIFReload());
	}
}

package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Schema, Unload All, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerSchemaUnloadAll}.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class JMenuSchemaUnloadAll extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuSchemaUnloadAll() {
		setText("Unload all");
		
		addActionListener(
				new ActionListenerSchemaUnloadAll());
	}
}

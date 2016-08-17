package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Schema, Load schema from file, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerSchemaLoadFile}.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class JMenuSchemaLoadSchemaFile extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuSchemaLoadSchemaFile() {
		setText("Load additional schema file");
		
		addActionListener(
				new ActionListenerSchemaLoadFile());
	}
	
	public JMenuSchemaLoadSchemaFile(String filename) {
		setText("Load " + filename);
		
		addActionListener(
				new ActionListenerSchemaLoadFile(filename));
	}
}

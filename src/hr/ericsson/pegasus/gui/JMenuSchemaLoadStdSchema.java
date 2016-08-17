package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Schema, Load std. schema, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerSchemaLoadStdSchema}.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class JMenuSchemaLoadStdSchema extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuSchemaLoadStdSchema() {
		setText("Load std. schema");
		
		addActionListener(
				new ActionListenerSchemaLoadStdSchema());
	}
}

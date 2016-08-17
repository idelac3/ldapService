package hr.ericsson.pegasus.gui;

import java.io.File;

import javax.swing.JMenu;

/**
 * <H1>Schema, Menu Item</H1>
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class JMenuSchema extends JMenu {

	private static final long serialVersionUID = 1L;

	public JMenuSchema() {
		setText("Schema");
		
		add(new JMenuSchemaUnloadAll());
		addSeparator();
		add(new JMenuSchemaLoadStdSchema());
		add(new JMenuSchemaLoadSchemaFile());
		addSeparator();
		
		/*
		 * Extra look for schema/ subfolder and list
		 * schema files found there too.
		 */
		
		final String schemaDir = "schema/";
		
		File fileList = new File(schemaDir);
		
		if (fileList.isDirectory()) {
			for (String fileItem : fileList.list()) {
				if (fileItem.toLowerCase().endsWith("ldif") ||
						fileItem.toLowerCase().endsWith(".schema")) {
					add(new JMenuSchemaLoadSchemaFile(schemaDir + fileItem));
				}
			}
		}
		
	}
}

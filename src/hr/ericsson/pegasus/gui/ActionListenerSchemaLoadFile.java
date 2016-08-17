package hr.ericsson.pegasus.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFException;

import hr.ericsson.pegasus.Pegasus;

/**
 * <H1>Schema, Load schema from LDIF file, ActionListener</H1>
 * <HR>
 * This action defined here will load additional schema from file.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class ActionListenerSchemaLoadFile implements ActionListener {

	private String filename = null;
	
	/**
	 * This action listener will show user an 'Open'
	 * dialog where user can select schema file to load. 
	 */
	public ActionListenerSchemaLoadFile() {
		this.filename = null;
	}

	/**
	 * This action listener will load schema file from
	 * provided string.
	 * @param filename path to schema file to load
	 */
	public ActionListenerSchemaLoadFile(String filename) {		
		this.filename = filename;		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {

			Schema schema = null;
			
			if (this.filename == null) {
				
				/*
				 * Show a 'Open' dialog and let
				 * user select schema file to load.
				 */
				
				JFileChooser jFileChooser = new JFileChooser();

				int retVal = jFileChooser.showOpenDialog(Pegasus.gui);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					File schemaFile = jFileChooser.getSelectedFile();
					Pegasus.debug("Loading schema file: '" + schemaFile + "'");
					try {					
						schema = Schema.getSchema(schemaFile);
					}
					catch (LDIFException ex1) {
						/*
						 * Try once more with conversion.
						 */
						String convertedSchemaFile = Pegasus.convertSchemaFile(schemaFile.getAbsolutePath());
						Pegasus.debug("Conversion on schema file '" + schemaFile + "' performed.");
						schema = Schema.getSchema(convertedSchemaFile);
					}
				}
				
			}
			else {
				
				/*
				 * Load schema file from filename variable.
				 */
				
				Pegasus.debug("Loading schema file: '" + this.filename + "'");

				try {					
					schema = Schema.getSchema(this.filename);
				}
				catch (LDIFException ex1) {
					/*
					 * Try once more with conversion.
					 */
					String convertedSchemaFile = Pegasus.convertSchemaFile(this.filename);
					Pegasus.debug("Conversion on schema file '" + this.filename + "' performed.");
					schema = Schema.getSchema(convertedSchemaFile);
				}
			}
			
			if (schema != null) {
				if (Pegasus.schema == null) {
					Pegasus.debug("This is first schema loaded.");
					Pegasus.schema = schema;
				}
				else {
					Pegasus.debug("This is additional schema loaded.");
					Pegasus.schema = Schema.mergeSchemas(Pegasus.schema, schema);
				}
			}
			
		} catch (LDIFException e1) {
			Pegasus.debug(e1.getExceptionMessage());
			e1.printStackTrace();
		} catch (IOException e1) {
			Pegasus.debug(e1.getMessage());
			e1.printStackTrace();
		}		
	}

}

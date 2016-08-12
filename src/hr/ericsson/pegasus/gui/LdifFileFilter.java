package hr.ericsson.pegasus.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * <H1>LDIF File Filter</H1>
 * <HR>
 * For Open dialog, file extension filter.<BR>
 * See {@link ActionListenerLDIFImportFile} code.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class LdifFileFilter extends FileFilter{

	@Override
	public boolean accept(File arg0) {
		File file = arg0;
		
		if (file != null) {
			if (file.isDirectory()) {
				return true;
			}
			else {
				if (file.getName().endsWith(".ldif")) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "LDIF files (*.ldif)";
	}

}

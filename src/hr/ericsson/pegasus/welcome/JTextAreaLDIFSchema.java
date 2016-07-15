package hr.ericsson.pegasus.welcome;

import hr.ericsson.pegasus.gui.JFrameGui;

import java.io.File;

import javax.swing.JTextArea;

public class JTextAreaLDIFSchema extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sep = "/";
	
	public JTextAreaLDIFSchema(String ext) {
		
		setFont(JFrameGui.defaultFont);
		
		String currDir = "";

		addFiles(currDir, ext);
		
	}
	
	private void addFiles(String currDir, String ext) {
		
		String appDir = System.getProperty("user.dir");
		
		File f = new File(appDir + sep + currDir);
		for (String fileItem : f.list()) {
			
			if (fileItem.endsWith(ext)) {	
				if (currDir.isEmpty()) {
					append(fileItem + "\n");
				}
				else {
					append(currDir + sep + fileItem + "\n");
				}
			}
			
			if ( new File(fileItem).isDirectory() ) {
				addFiles(fileItem, ext);
			}
			
			if ( new File(currDir + sep +fileItem).isDirectory() ) {
				addFiles(currDir + sep + fileItem, ext);
			}
			
		}
	}
}

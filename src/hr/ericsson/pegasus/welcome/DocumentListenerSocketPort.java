package hr.ericsson.pegasus.welcome;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class DocumentListenerSocketPort implements DocumentListener {

	private JPanelSocketItem parentContainer;
	
	public DocumentListenerSocketPort(JPanelSocketItem parentContainer) {
		this.parentContainer = parentContainer;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		doProcess(arg0);
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		doProcess(arg0);		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		doProcess(arg0);
	}

	private void doProcess(DocumentEvent arg0) {
		try {
			parentContainer.socketPort = arg0.getDocument().getText(0, arg0.getDocument().getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

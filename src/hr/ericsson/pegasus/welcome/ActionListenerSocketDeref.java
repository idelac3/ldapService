package hr.ericsson.pegasus.welcome;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class ActionListenerSocketDeref implements ActionListener {

	private JPanelSocketItem parentContainer;
	
	public ActionListenerSocketDeref(JPanelSocketItem parentContainer) {
		this.parentContainer = parentContainer;	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		parentContainer.deref = ((JCheckBox) e.getSource()).isSelected();
	}

}

package hr.ericsson.pegasus.welcome;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionListenerSocketItemAdd implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		JFrameWelcome.jPanelBindSocket.add(new JPanelSocketItem("0.0.0.0", "1234"));
		JFrameWelcome.jFrameWizard.pack();
	}

}

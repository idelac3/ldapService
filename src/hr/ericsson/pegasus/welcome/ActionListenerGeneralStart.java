package hr.ericsson.pegasus.welcome;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.gui.JFrameGui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.SwingUtilities;

public class ActionListenerGeneralStart implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		String arg = "";
		
		String bind = "";
		String deref = "";
		
		for (Component component : JFrameWelcome.jPanelBindSocket.getComponents()) {
			if (component instanceof JPanelSocketItem) {
				JPanelSocketItem socketItem = (JPanelSocketItem) component;
				bind = bind + socketItem.socketInterface + ":" + socketItem.socketPort + ",";
				if (socketItem.deref) {
					deref = deref + socketItem.socketInterface + ":" + socketItem.socketPort + ",";
				}
			}
		}
		
		if (bind.endsWith(",")) {
			bind = bind.substring(0, bind.length() - 1);
		}
		
		if (deref.endsWith(",")) {
			deref = deref.substring(0, deref.length() - 1);
		}
		
		if (bind.length() > 0) {
			arg = arg + " --bind " + bind;
		}
		
		if (deref.length() > 0) {
			arg = arg + " --deref " + deref;
		}
		
		if (JFrameWelcome.jCheckBoxEnableDebugging.isSelected()) {
			arg = arg + " --debug";
		}
		
		if (JFrameWelcome.jCheckBoxEnableGUI.isSelected()) {
			arg = arg + " --gui";
		}

		if (JFrameWelcome.jCheckBoxEnableMulticastReplication.isSelected()) {
			arg = arg + " --multicastSyncInterface " + JFrameWelcome.jTextFieldMulticastInterface.getText();
			arg = arg + " --multicastSyncGroup " + JFrameWelcome.jTextFieldMulticastGroup.getText();
			arg = arg + " --multicastSyncPort " + JFrameWelcome.jTextFieldMulticastPort.getText();
		}
		
		arg = arg + " --countLimit " + JFrameWelcome.jTextFieldCountLimit.getText();
		
		arg = arg + " --ldifFiles " + JFrameWelcome.jTextAreaLDIF.getText().replaceAll("\n", ",");
		
		arg = arg + " --schemaFiles " + JFrameWelcome.jTextAreaSchema.getText().replaceAll("\n", ",");
		
		final String args = arg;
		
		JFrameWelcome.jFrameWizard.dispose();
		if (JFrameWelcome.jCheckBoxEnableGUI.isSelected()) {
			Runnable doRun = new Runnable() {

				@Override
				public void run() {
					Pegasus.gui = new JFrameGui();
					Pegasus.gui.setVisible(true);
					Pegasus.gui.startMain(args);
				}
			};

			SwingUtilities.invokeLater(doRun);			
		}
		else {
			try {
				Pegasus.main(args.split(" "));
			} catch (IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

}

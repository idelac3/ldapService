package hr.ericsson.pegasus.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.unboundid.util.Debug;

/**
 * <H1>Debug enabled, ActionListener</H1>
 * <HR>
 * This action defined here will toggle {@link Debug} global status.
 * <HR>
 * @author eigorde
 *
 */
public class ActionListenerDebugEnabled implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (Debug.debugEnabled()) {
			Debug.setEnabled(false);
		}
		else {
			Debug.setEnabled(true);
		}
		
	}

}

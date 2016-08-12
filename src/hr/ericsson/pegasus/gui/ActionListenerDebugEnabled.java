package hr.ericsson.pegasus.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.unboundid.util.Debug;

import hr.ericsson.pegasus.Pegasus;

/**
 * <H1>Debug enabled, ActionListener</H1>
 * <HR>
 * This action defined here will toggle {@link Debug} global status.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class ActionListenerDebugEnabled implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		
		Pegasus.debugEnabled = !(Pegasus.debugEnabled);
		
	}

}

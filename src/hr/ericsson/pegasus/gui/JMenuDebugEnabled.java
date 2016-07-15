package hr.ericsson.pegasus.gui;

import javax.swing.JCheckBoxMenuItem;

import com.unboundid.util.Debug;


/**
 * <H1>Debugging enabled, Menu Item</H1>
 * <HR>
 * @author eigorde
 *
 */

public class JMenuDebugEnabled extends JCheckBoxMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuDebugEnabled() {
		setText("Debugging enabled");
		setSelected(Debug.debugEnabled());
		addActionListener(
				new ActionListenerDebugEnabled());
	}
}

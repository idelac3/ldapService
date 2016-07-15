package hr.ericsson.pegasus.gui;

import javax.swing.JMenuBar;

import com.unboundid.util.Debug;

/**
 * <H1>Main Menu Bar</H1>
 * <HR>
 * Menu bar for {@link JMenuFile}, {@link JMenuLDIF}, {@link JMenuDebug}, {@link JMenuStatistic} ... etc.
 * <HR>
 * @author eigorde
 *
 */

public class JMenuBarMain extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public JMenuBarMain() {
		add(new JMenuFile());
		add(new JMenuLDIF());
		add(new JMenuStatistic());
		if (Debug.debugEnabled()) {
			add(new JMenuDebug());
		}
			
	}
}

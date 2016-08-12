package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Clear statistic, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerStatisticClear}.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */

public class JMenuStatisticClear extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuStatisticClear() {
		setText("Clear statistic");
		
		addActionListener(
				new ActionListenerStatisticClear());
	}
}

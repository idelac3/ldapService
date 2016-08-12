package hr.ericsson.pegasus.gui;

import javax.swing.JMenu;

/**
 * <H1>Statistic, SubMenu Item</H1>
 * <HR>
 * @author igor.delac@gmail.com
 *
 */

public class JMenuStatistic extends JMenu {

	private static final long serialVersionUID = 1L;

	public JMenuStatistic() {
		setText("Statistic");
		add(new JMenuStatisticClear());
		add(new JMenuStatisticDelta());
	}
}

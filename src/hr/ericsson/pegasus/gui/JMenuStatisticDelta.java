package hr.ericsson.pegasus.gui;

import javax.swing.JCheckBoxMenuItem;

/**
 * <H1>File, Menu Item Statistic</H1>
 * <HR>
 * Toggle statistic display in total or delta values.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class JMenuStatisticDelta extends JCheckBoxMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuStatisticDelta() {
		setText("Delta values");
		
		JFrameGui.deltaStatistic = this;
	}
}

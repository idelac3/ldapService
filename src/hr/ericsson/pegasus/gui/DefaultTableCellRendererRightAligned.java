package hr.ericsson.pegasus.gui;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * <H1>Right aligned Table Cell Renderer</H1>
 * <HR>
 * This renderer will make {@link TableColumn} data appear on right side.<BR>
 * To use this Renderer:
 * <PRE>
 * JTable myTable = new JTable();
 * ...
 * myTable.getColumnModel().getColumn(0).
 *     setCellRenderer(new DefaultTableCellRendererRightAligned());
 * </PRE>
 * <HR>
 * @author eigorde
 *
 */
public class DefaultTableCellRendererRightAligned extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public DefaultTableCellRendererRightAligned() {
		setHorizontalAlignment(JLabel.RIGHT);
	}
}

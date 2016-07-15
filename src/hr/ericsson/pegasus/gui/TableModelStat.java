package hr.ericsson.pegasus.gui;

import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

/**
 * <H1>Statistic data, Table Model</H1>
 * <HR>
 * This Table Model is used by {@link JTableStat} table
 * to show statistic data.
 * <HR>
 * @author eigorde
 *
 */
public class TableModelStat extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

    private String[] columns = {"NAME", "VALUE"};

    private HashMap<Integer, Long> data;
    
    public TableModelStat() {
    	data = new HashMap<Integer, Long>(32);   	
    	for (int i = 0; i < 13; i++) {
    		data.put(i, 0L);
    	}
    }
    
	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

    @Override
    public String getColumnName(int i) {
        return columns[i];
    }
    
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String key = null;

		switch (rowIndex) {
        case 0:
            key = "Client Connections";
            break;
        case 1:
            key = "Search Requests, Base";
            break;
        case 2:
            key = "Search Requests, One level";
            break;
        case 3:
            key = "Search Requests, Sub levels";
            break;
        case 4:
            key = "Entry Results";
            break;
        case 5:
            key = "Failed Search Requests";
            break;
        case 6:
            key = "Modify Requests";
            break;
        case 7:
            key = "Failed Modify Requests";
            break;
        case 8:
            key = "Add Requests";
            break;
        case 9:
            key = "Failed Add Requests";
            break;
        case 10:
            key = "Delete Requests";
            break;
        case 11:
            key = "Failed Delete Requests";
            break;
        case 12:
            key = "Memory available [%]";
            break;

		}
		
		switch (columnIndex) {
        case 0:
            return key;
        case 1:
        	return data.get(rowIndex);
        default:
            return null;
		}
	}
	
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {

    	String newValue = String.valueOf(value);
    	
    	if (columnIndex == 1) {
    		data.put(rowIndex, Long.valueOf(newValue));
    	}
        fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Long.class;
            default:
                return null;
        }
    }

}

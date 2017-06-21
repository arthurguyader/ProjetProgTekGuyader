package vue;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import model.*;
 
 public class MyTableModel extends AbstractTableModel {	 
	
	private String[] titre={"Nom", "Type", "Valeur", "Nul ?",  "Unique ?", "Clé Primaire ?", "Clé étrangère ?", "Table référée", "Attribut référé"};
	
	private Object[][] data ={{"", Type.INT, 4, false, false, false, false, "", ""}};
	
	private boolean DEBUG = false;
	
	public int getColumnCount() {
		return titre.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return titre[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/
	 * editor for each cell.  If we didn't implement this method,
	 * then the last column would contain text ("true"/"false"),
	 * rather than a check box.
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's
	 * editable.
	 */
	public boolean isCellEditable(int row, int col) {
		
		return true;
	}

	/*
	 * Don't need to implement this method unless your table's
	 * data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		if (DEBUG) {
			System.out.println("Setting value at " + row + "," + col
							   + " to " + value
							   + " (an instance of "
							   + value.getClass() + ")");
		}

		data[row][col] = value;
		fireTableCellUpdated(row, col);

		if (DEBUG) {
			System.out.println("New value of data:");
			printDebugData();
		}
	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
}
package com.zs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Enumeration;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class Util {
	public static void FitTableColumns(JTable myTable) {
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();
		@SuppressWarnings("rawtypes")
		Enumeration columns = myTable.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
			int width = (int) myTable.getTableHeader().getDefaultRenderer()
					.getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable.getCellRenderer(row, col)
						.getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col)
						.getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column); // 此行很重要
			column.setWidth(width + myTable.getIntercellSpacing().width);
		}
	}

	public static void fitTableColumns(JTable table, int[] columnWidths) {
		for (int i = 0; i < columnWidths.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
		}
	}

	public static void showFile(String path, JTextArea textArea) {
		File file = new File(path);
		try {
			InputStream in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte = in.read()) != -1) {
				textArea.append("" + (char) tempbyte);
			}
			in.close();
		} catch (Exception event) {
			event.printStackTrace();
		}
	}

	public static void saveFile(String path, JTextArea textArea) {
		String[] lineString = textArea.getText().split("\r\n");
		int length = lineString.length;
		int i;
		File file = new File(path);
		try {
			FileWriter fWriter = new FileWriter(file.getAbsolutePath());
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			for (i = 0; i < length; i++) {
				bWriter.write(lineString[i] + "\r\n");
			}
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearTextArea(JTextArea textArea) {
		textArea.setText("");
	}

	public static void clearTable(DefaultTableModel tableModel, JTable table) {
		int rows = tableModel.getRowCount();
		for (int i = 0; i < rows; i++) {
			tableModel.removeRow(0);
			table.updateUI();
		}
	}
}

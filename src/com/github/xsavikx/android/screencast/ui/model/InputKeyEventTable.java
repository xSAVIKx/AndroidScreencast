package com.github.xsavikx.android.screencast.ui.model;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class InputKeyEventTable extends JTable {
  private static final long serialVersionUID = 3978642864003531967L;

  private final static int MIN_COLUMN_WIDTH = 20;

  public InputKeyEventTable(InputKeyEventTableModel tableModel) {
    super(tableModel);
    setTableColumnsNames(tableModel.columnNames);
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setTableColumnsPrefferedSize();
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  private void setTableColumnsNames(String[] columnNames) {
    TableColumnModel columnModel = getColumnModel();
    for (int i = 0; i < columnNames.length; i++) {
      TableColumn column = columnModel.getColumn(i);
      column.setHeaderValue(columnNames[i]);
    }
  }

  private void setTableColumnsPrefferedSize() {
    final TableColumnModel columnModel = getColumnModel();
    for (int column = 0; column < getColumnCount(); column++) {
      int width = MIN_COLUMN_WIDTH; // Min width
      for (int row = 0; row < getRowCount(); row++) {
        TableCellRenderer renderer = getCellRenderer(row, column);
        Component comp = prepareRenderer(renderer, row, column);
        width = Math.max(comp.getPreferredSize().width + 5, width);
      }
      columnModel.getColumn(column).setPreferredWidth(width);
    }
  }

}

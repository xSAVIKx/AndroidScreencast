package com.github.xsavikx.androidscreencast.ui.model;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

@Singleton
public class InputKeyEventTable extends JTable {
    private static final long serialVersionUID = 3978642864003531967L;

    private final static int MIN_COLUMN_WIDTH = 20;

    @Inject
    public InputKeyEventTable(InputKeyEventTableModel tableModel) {
        super(tableModel);
        setTableColumnsNames(InputKeyEventTableModel.COLUMN_NAMES);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setTableColumnsPreferredSize();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setTableColumnsNames(String[] columnNames) {
        TableColumnModel columnModel = getColumnModel();
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setHeaderValue(columnNames[i]);
        }
    }

    private void setTableColumnsPreferredSize() {
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

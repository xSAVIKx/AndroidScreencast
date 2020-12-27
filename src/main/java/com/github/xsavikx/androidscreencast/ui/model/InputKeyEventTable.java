/*
 * Copyright 2020 Yurii Serhiichuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.xsavikx.androidscreencast.ui.model;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

@Singleton
public final class InputKeyEventTable extends JTable {
    private static final long serialVersionUID = 3978642864003531967L;

    private final static int MIN_COLUMN_WIDTH = 20;

    @Inject
    InputKeyEventTable(InputKeyEventTableModel tableModel) {
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

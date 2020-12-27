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

import com.github.xsavikx.androidscreencast.api.injector.InputKeyEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@Singleton
public final class InputKeyEventTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1553313932570896541L;
    private final static String INDEX_COLUMN_NAME = "#";
    private final static String TITLE_COLUMN_NAME = "title";
    private final static String DESCRIPTION_COLUMN_NAME = "description";
    static final String[] COLUMN_NAMES = {
            INDEX_COLUMN_NAME, TITLE_COLUMN_NAME, DESCRIPTION_COLUMN_NAME
    };

    private final List<List<Object>> data = new ArrayList<>();
    private int rowCount = 0;

    @Inject
    InputKeyEventTableModel(InputKeyEvent[] initialData) {
        initData(initialData);
    }

    private void initData(InputKeyEvent[] inputKeyEvents) {
        for (InputKeyEvent e : inputKeyEvents) {
            data.add(getDataRow(e));
        }
    }

    private List<Object> getDataRow(InputKeyEvent e) {
        List<Object> row = new ArrayList<>();
        row.add(rowCount++);
        row.add(e.name());
        row.add(e.getDescription());
        return row;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }
}

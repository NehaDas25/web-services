package com.mentor.soapProcess;/*
 * Decompiled with CFR 0.152.
 */
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

class SortFilterModel
extends AbstractTableModel {
    private TableModel model;
    private Row[] rows;
    private int sortColumn;
    private boolean reverse = false;

    public SortFilterModel(TableModel m) {
        this.model = m;
        this.rows = new Row[this.model.getRowCount()];
        int i = 0;
        while (i < this.rows.length) {
            this.rows[i] = new Row();
            this.rows[i].index = i;
            ++i;
        }
    }

    public void updateRows() {
        this.rows = new Row[this.model.getRowCount()];
        int i = 0;
        while (i < this.rows.length) {
            this.rows[i] = new Row();
            this.rows[i].index = i;
            ++i;
        }
    }

    public void sort(int c) {
        this.sortColumn = c;
        if (this.reverse) {
            Arrays.sort(this.rows, Collections.reverseOrder());
            this.reverse = false;
        } else {
            Arrays.sort(this.rows);
            this.reverse = true;
        }
        this.fireTableDataChanged();
    }

    public void addMouseListener(final JTable table) {
        table.getTableHeader().addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() < 2) {
                    return;
                }
                int tableColumn = table.columnAtPoint(event.getPoint());
                int modelColumn = table.convertColumnIndexToModel(tableColumn);
                SortFilterModel.this.sort(modelColumn);
            }
        });
    }

    @Override
    public Object getValueAt(int r, int c) {
        return this.model.getValueAt(this.rows[r].index, c);
    }

    @Override
    public void setValueAt(Object aValue, int r, int c) {
        this.model.setValueAt(aValue, this.rows[r].index, c);
    }

    @Override
    public int getRowCount() {
        return this.model.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return this.model.getColumnCount();
    }

    @Override
    public String getColumnName(int c) {
        return this.model.getColumnName(c);
    }

    public Class getColumnClass(int c) {
        return this.model.getColumnClass(c);
    }

    private class Row
    implements Comparable {
        public int index;

        private Row() {
        }

        public int compareTo(Object other) {
            Row otherRow = (Row)other;
            Object a = SortFilterModel.this.model.getValueAt(this.index, SortFilterModel.this.sortColumn);
            Object b = SortFilterModel.this.model.getValueAt(otherRow.index, SortFilterModel.this.sortColumn);
            if (a instanceof Comparable) {
                return ((Comparable)a).compareTo(b);
            }
            return this.index - otherRow.index;
        }
    }
}


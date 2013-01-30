/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package edu.virginia.patek.nscript;

import java.util.logging.Logger;
import javax.swing.table.*;

/**
 * Implements the AbstractTableModel interfaces to edit the attributes of a
 * simulation object in the SObjectBrowser view.
 */
public class SObjectTableModel extends AbstractTableModel {

    static final long serialVersionUID = 42L;
    /**
     * A reference to the object being edited.
     */
    NSObject o;

    /**
     * Constructor that takes a reference to the object to be edited.
     */
    public SObjectTableModel(NSObject inObject) {
        o = inObject;
    }

    /**
     * Updates the view by changing the object to be edited.
     */
    public void updateView(NSObject inNewObject) {
        o = inNewObject;
    }

    /**
     * Obtains the number of rows in the table. This corresponds to the number
     * of attributes of the currently selected object.
     *
     * @return the number of rows in the attribute table.
     */
    @Override
    public int getRowCount() {
        if (o == null) {
            return 0;
        } else {
            return o.getAttributeCount();
        }
    }

    /**
     * Obtains the numbre of columns in the Attributes table (2).
     *
     * @return the number of columns.
     */
    @Override
    public int getColumnCount() {
        if (o == null) {
            return 0;
        } else {
            return 2;
        }
    }

    /**
     * Obtains the value at a given cell.
     *
     * @param row the row number of the cell whose value is required.
     * @param col the column number of the cell whose value is required.
     * @return a reference to the object stored at the requested position. No
     * validation is done so (row,col) MUST be in the correct ranges.
     */
    @Override
    public Object getValueAt(int row, int col) {
        if (o == null) {
            return "";
        }

        if (col == 0) {
            return o.getSnippet().getAttribute(row).name;
        } else {
            return o.getAttribute(row);
        }
    }

    /**
     * Sets the value at a given position.
     *
     * @param value the new value to be stored.
     * @param row the row position where the new value should be stored.
     * @param column the column position where the new value should be stored.
     * Again, no validation takes place, so (row,column) indexes must be in
     * their correct ranges.
     */
    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column == 1 && o != null) {
            o.setAttribute(row, value.toString());
        }
    }

    /**
     * Obtains the name for a column. In this case "Attribute" or "Value".
     *
     * @param columnIndex the column of interest.
     * @return the name of the column.
     */
    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return Messages.tr("attribute");
        } else {
            return Messages.tr("value");
        }
    }

    /**
     * Verifies if a certain cell is editable. In this case, only the "Value"
     * column is editable.
     *
     * @param rowIndex the row position of the cell of interest.
     * @param columnIndex the column position of the cell of interest.
     * @return true if the cell can be edited, false otherwise.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false;
        } else {
            return true;
        }
    }
    private static final Logger LOG = Logger.getLogger(SObjectTableModel.class.getName());
}

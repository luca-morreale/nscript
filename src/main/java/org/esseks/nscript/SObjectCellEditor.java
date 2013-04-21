/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.awt.Component;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 * Implements the abstract cell behavior by providing a way to edit the cells in
 * the object browser component where the object attributes are listed. It
 * creates the interface required to do so.
 */
public class SObjectCellEditor extends AbstractCellEditor implements TableCellEditor {

    static final long serialVersionUID = 42L;
    /**
     * The object to be edited.
     */
    private NSObject o;
    /**
     * A pull down for fields with limited options.
     */
    private JComboBox<String> cb;
    /**
     * An editable text region for the fields with free input.
     */
    private JTextField tf;

    /**
     * The constructor takes the object to be edited as an input, creates and
     * initializes the components in the application.
     *
     * @param inO
     */
    public SObjectCellEditor(NSObject inO) {
        this.o = inO;
        this.cb = new JComboBox<String>();
        this.tf = new JTextField();
    }

    /**
     * Obtains the value obtained in the current cell. This correponds to the
     * selected item number if limited options exist, or the text if it is a
     * free entry field.
     *
     * @return
     */
    @Override
    public Object getCellEditorValue() {
        if (this.cb.isEditable()) {
            return this.tf.getText();
        } else {
            return this.cb.getSelectedItem();
        }
    }

    /**
     * Creates a component to edit a specific cell in the attribute table. This
     * component, again, corresponds to a pull down if there are a finite number
     * of options, and a field if it is a free entry.
     *
     * @param table the table where the cell is stored.
     * @param value the value currently stored in the cell.
     * @param isSelected a flag indicating if the current cell is selected.
     * @param row the row position of the cell.
     * @param column the column position of the cell.
     * @return
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
            int row, int column) {
        StringTokenizer st;
        this.cb.removeAllItems();
        this.cb.setPopupVisible(false);
        if (column > 0) {
            if (this.o != null) {
                if (this.o.getSnippet().getAttribute(row).hasOptions()) {
                    st = new StringTokenizer(this.o.getSnippet().getAttribute(row).getOptions());
                    this.cb.setEditable(false);
                    while (st.hasMoreTokens()) {
                        this.cb.addItem(st.nextToken());
                    }
                    this.cb.setSelectedItem(this.o.getAttribute(row));
                    return this.cb;
                } else {
                    this.cb.setEditable(true);
                    this.tf.setText(this.o.getAttribute(row));
                    return this.tf;
                }
            } else {
                return this.cb;
            }
        }
        return this.cb;
    }
    private static final Logger LOG = Logger.getLogger(SObjectCellEditor.class.getName());
}

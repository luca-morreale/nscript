/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */

package edu.virginia.patek.nscript;

import edu.virginia.patek.nscript.NSObject;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

/** Implements the abstract cell behavior by providing a way to edit the cells
 *  in the object browser component where the object attributes are listed.
 *  It creates the interface required to do so.
 */
public class SObjectCellEditor extends AbstractCellEditor implements TableCellEditor {
  static final long serialVersionUID = 42L;

  /** The object to be edited. */
  NSObject o;
  /** A pull down for fields with limited options. */
  JComboBox cb;
  /** An editable text region for the fields with free input. */
  JTextField tf;

  /** The constructor takes the object to be edited as an input, creates
   *  and initializes the components in the application.
   */
  public SObjectCellEditor(NSObject inO)
  {
    o = inO;
    cb = new JComboBox();
    tf = new JTextField();
  }

  /** Obtains the value obtained in the current cell. This correponds to
   *  the selected item number if limited options exist, or the text if
   *  it is a free entry field.
   */
  public Object getCellEditorValue()
  {
    if (cb.isEditable())
      return tf.getText();
    else
      return cb.getSelectedItem();
  }

  /** Creates a component to edit a specific cell in the attribute table.
   *  This component, again, corresponds to a pull down if there are a
   *  finite number of options, and a field if it is a free entry.
   *
   *  @param table the table where the cell is stored.
   *  @param value the value currently stored in the cell.
   *  @param isSelected a flag indicating if the current cell is selected.
   *   @param row the row position of the cell.
   *  @param column the column position of the cell.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column)
  {
    StringTokenizer st;
    String options;

    cb.removeAllItems();
    cb.setPopupVisible(false);
    if (column > 0) {
      if (o != null) {
        if (o.getSnippet().getAttribute(row).hasOptions) {
          st = new StringTokenizer(o.getSnippet().getAttribute(row).getOptions());
          cb.setEditable(false);
          while (st.hasMoreTokens())
            cb.addItem(st.nextToken());
          cb.setSelectedItem(o.getAttribute(row));
          return ((Component) cb);
        } else {
          cb.setEditable(true);
          tf.setText(o.getAttribute(row));
          return ((Component) tf);
        }
      } else {
        return (Component) cb;
      }
    }
    return (Component) cb;
  }
}

/* ----------------------------------------------------------------------------
 * NSCRIPT
 * version 1.0.2
 * Author:              Enrique Campos-Nanez
 * Contact information: Dr. Stephen D. Patek
 *                      Department of Systems and Information Engineering
 *                      101-C Olsson Hall
 *                      Charlottesville, VA 22904
 *                      University of Virginia
 *                      e-mail: patek@virginia.edu
 * ------------------------------------------------------------------------- */

package edu.virginia.patek.nscript;

import javax.swing.*;
import java.awt.*;

/** A view that holds a list of the objects involved in a simulation.
 *  Not yet implemented. */
public class NSWorldPane extends JLabel implements ListCellRenderer {
  /** Default constructor. */
  public NSWorldPane()
  {
    super("Empty");
    setOpaque(true);
  }

  /** Class that implements the rendering of the cells (rows) in the
   *  world view.
   */
  public Component getListCellRendererComponent(
    JList list,
    Object value,
    int index,
    boolean isSelected,
    boolean cellHasFocus
  )
  {
    String s = (String) value;
    setText( s );

    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }

    return this;
  }

}

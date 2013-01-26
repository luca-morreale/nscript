/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */

package edu.virginia.patek.nscript;

import javax.swing.*;
import java.awt.*;

/** A view that holds a list of the objects involved in a simulation.
 *  Not yet implemented. */
public class NSWorldPane extends JLabel implements ListCellRenderer<String> {
    static final long serialVersionUID = 42L;

    /** Default constructor. */
    public NSWorldPane() {
        super(Messages.tr("empty"));
        setOpaque(true);
    }

    /** Class that implements the rendering of the cells (rows) in the
     *  world view.
     */
    @Override
    public Component getListCellRendererComponent(
        JList <? extends String > list,
        String value,
        int index,
        boolean isSelected,
        boolean cellHasFocus
    ) {
        setText(value);

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

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
import java.awt.event.*;

/** Extends the JDialog object to implement the manipulation of index Arrays,
 *  that are part of the simulation interface. Arrays are used to create
 *  several objects that share the same behavior.
 */
public class SArrayDialog extends JDialog implements ActionListener {
    static final long serialVersionUID = 42L;

    /** A reference to the simulation model. */
    NSModel M;
    /** A table where the information about the current arrays will be shown. */
    JTable AT;
    /** The 'Close' button. Finishes the edition process. */
    JButton closeBtn;
    /** The 'Add' button. Adds a new index Array. */
    JButton addBtn;
    /** The 'Remove' button. Removes the currently selected array. */
    JButton removeBtn;
    /** The 'Remove All' button. Removes all the index Arrays in the model. */
    JButton removeAllBtn;

    /** The only constructor takes a reference to the model, and the
     *  main frame, creates the interface and shows the dialog box on
     *  the screen.
     *
     *  @param inM a reference to the simulation model.
     *  @param pFrame the main frame of the application.
     */
    public SArrayDialog(NSModel inM, JFrame pFrame) {
        super(pFrame, Messages.tr("configure_index"), true);
        M = inM;
        AT = new JTable(new SArrayTableModel(M));
        this.getContentPane().add(new JLabel(Messages.tr("available_indices")), BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(AT), BorderLayout.CENTER);

        // Pane for buttons on the right of the dialog box
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new GridLayout(5, 1));

        addBtn = new JButton(Messages.tr("add"));
        addBtn.addActionListener(this);
        removeBtn = new JButton(Messages.tr("remove"));
        removeBtn.addActionListener(this);
        removeAllBtn = new JButton(Messages.tr("remove_all"));
        removeAllBtn.addActionListener(this);
        closeBtn = new JButton(Messages.tr("close"));
        closeBtn.addActionListener(this);
        buttonPane.add(addBtn);
        buttonPane.add(removeBtn);
        buttonPane.add(removeAllBtn);
        buttonPane.add(new JLabel());
        buttonPane.add(closeBtn);
        this.getContentPane().add(buttonPane, BorderLayout.EAST);
        setSize(350, 200);
        setVisible(true);
    }

    /** Responds to the actions of all buttons.
     *
     *  @param ae stores the information of the current event.
     */
    public void actionPerformed(ActionEvent ae) {
        JButton b = (JButton) ae.getSource();
        if (b == closeBtn) {
            dispose();
        }
        if (b == addBtn) {
            M.addArray(Messages.tr("untitled_index"), 10);
            AT.setModel(new SArrayTableModel(M));
        }
        if (b == removeBtn) {
            M.removeArray(AT.getSelectedRow());
            AT.setModel(new SArrayTableModel(M));
        }
        if (b == removeAllBtn) {
            M.removeAllArrays();
            AT.setModel(new SArrayTableModel(M));
        }
    }
}

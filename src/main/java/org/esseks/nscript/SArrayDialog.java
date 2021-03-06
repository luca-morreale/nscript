/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Extends the JDialog object to implement the manipulation of index Arrays,
 * that are part of the simulation interface. Arrays are used to create several
 * objects that share the same behavior.
 */
public class SArrayDialog extends JDialog implements ActionListener {

    static final long serialVersionUID = 42L;
    /**
     * A reference to the simulation model.
     */
    private NSModel M;
    /**
     * A table where the information about the current arrays will be shown.
     */
    private JTable AT;
    /**
     * The 'Close' button. Finishes the edition process.
     */
    private JButton closeBtn;
    /**
     * The 'Add' button. Adds a new index Array.
     */
    private JButton addBtn;
    /**
     * The 'Remove' button. Removes the currently selected array.
     */
    private JButton removeBtn;
    /**
     * The 'Remove All' button. Removes all the index Arrays in the model.
     */
    private JButton removeAllBtn;

    /**
     * The only constructor takes a reference to the model, and the main frame,
     * creates the interface and shows the dialog box on the screen.
     *
     * @param inM a reference to the simulation model.
     * @param pFrame the main frame of the application.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public SArrayDialog(NSModel inM, JFrame pFrame) {
        super(pFrame, Messages.tr("configure_index"), true);
        this.M = inM;
        this.AT = new JTable(new SArrayTableModel(this.M));
        this.getContentPane().add(new JLabel(Messages.tr("available_indices")), BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(this.AT), BorderLayout.CENTER);

        // Pane for buttons on the right of the dialog box
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new GridLayout(5, 1));

        this.addBtn = new JButton(Messages.tr("add"));
        this.addBtn.addActionListener(this);
        this.removeBtn = new JButton(Messages.tr("remove"));
        this.removeBtn.addActionListener(this);
        this.removeAllBtn = new JButton(Messages.tr("remove_all"));
        this.removeAllBtn.addActionListener(this);
        this.closeBtn = new JButton(Messages.tr("close"));
        this.closeBtn.addActionListener(this);
        buttonPane.add(this.addBtn);
        buttonPane.add(this.removeBtn);
        buttonPane.add(this.removeAllBtn);
        buttonPane.add(new JLabel());
        buttonPane.add(this.closeBtn);
        this.getContentPane().add(buttonPane, BorderLayout.EAST);
        this.setSize(350, 200);
        this.setVisible(true);
    }

    /**
     * Responds to the actions of all buttons.
     *
     * @param ae stores the information of the current event.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        JButton b = (JButton) ae.getSource();
        if (b == this.closeBtn) {
            this.dispose();
        }
        if (b == this.addBtn) {
            this.M.addArray(Messages.tr("untitled_index"), 10);
            this.AT.setModel(new SArrayTableModel(this.M));
        }
        if (b == this.removeBtn) {
            this.M.removeArray(this.AT.getSelectedRow());
            this.AT.setModel(new SArrayTableModel(this.M));
        }
        if (b == this.removeAllBtn) {
            this.M.removeAllArrays();
            this.AT.setModel(new SArrayTableModel(this.M));
        }
    }
    private static final Logger LOG = Logger.getLogger(SArrayDialog.class.getName());
}

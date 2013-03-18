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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Creates a panel that allows the user to modify the properties of an object.
 */
public class SObjectBrowser extends JPanel implements ActionListener {

    static final long serialVersionUID = 42L;
    /**
     * A reference to the simulation model.
     */
    private NSModel M;
    /**
     * The default button from the interface.
     */
    private JButton defaultBtn;
    /**
     * A label that describes the current object. Typically its class.
     */
    private JLabel description;
    /**
     * The name field, where the user sets and edits the name of the selected
     * object.
     */
    private JTextField nameField;
    /**
     * The name label. "Name: "
     */
    private JLabel name;
    /**
     * A drop down index containing the available indices + the 'None' entry.
     * Through this element, the user can specify if a given object is indexed
     * by a given array.
     */
    private JComboBox<NSArray> arrayIndex;
    /**
     * The attribute table, where the object properties are listed, and the user
     * can modify.
     */
    private JTable attrTable;
    /**
     * A reference to the current object being edited.
     */
    private NSObject o = null;

    /**
     * The default constructor receives a reference to the current simulation
     * model.
     *
     * @param inModel
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public SObjectBrowser(NSModel inModel) {
        super();
        this.M = inModel;
        this.name = new JLabel(Messages.tr("name"));
        this.nameField = new JTextField();
        this.nameField.addActionListener(this);
        this.description = new JLabel(Messages.tr("no_selection"));
        JPanel p = new JPanel(new GridLayout(1, 2));
        JPanel aiPanel = new JPanel(new BorderLayout());
        this.arrayIndex = new JComboBox<NSArray>();
        this.arrayIndex.addActionListener(this);
        // arrayIndex.addItemListener(this);
        aiPanel.add(this.arrayIndex, BorderLayout.CENTER);
        aiPanel.add(new JLabel(Messages.tr("indexed_by")), BorderLayout.WEST);
        p.add(this.name);
        p.add(this.nameField);
        JPanel p2 = new JPanel(new GridLayout(3, 1));
        p2.add(this.description);
        p2.add(p);
        p2.add(aiPanel);

        this.setLayout(new BorderLayout());
        this.add(p2, BorderLayout.NORTH);

        this.defaultBtn = new JButton(Messages.tr("use_defaults"));
        this.defaultBtn.addActionListener(this);

        JPanel p3 = new JPanel(new GridLayout(1, 2));
        p3.add(this.defaultBtn);

        this.add(p3, BorderLayout.SOUTH);

        // OK, the table
        this.attrTable = new JTable(new SObjectTableModel(null));
        this.attrTable.getSelectionModel().addListSelectionListener(new PropertiesUpdater());
        JScrollPane sp_at = new JScrollPane(this.attrTable);
        this.add(sp_at, BorderLayout.CENTER);
        this.setBorder(new EmptyBorder(2, 2, 2, 2));
    }

    /**
     * Provides an interface through which the program can advertise changes in
     * the simulation model.
     */
    public void selectionChanged() {
        NSRelation or;
        int i, oc;

        SObjectTableModel tm;
        oc = 0;
        for (i = 0; i < this.M.getSize(); i++) {
            if (this.M.getObjectAt(i).isSelected()) {
                oc++;
                this.o = (NSObject) this.M.getObjectAt(i);
            }
        }
        if (oc == 1) {
            this.nameField.setText(this.o.getName());
            if (this.o.getSnippet().isRelation()) {
                or = (NSRelation) this.o;
                this.description.setText(Messages.tr("relates") + " (" + or.getFrom().getName() + ", " + or.getTo().getName() + ")");
            } else {
                this.description.setText(Messages.tr("entity_class") + ": " + this.o.getSnippet().getName() + " : " + this.o.getSnippet().getBase());
            }
            // Prepare array list
            this.arrayIndex.removeAllItems();
            this.arrayIndex.addItem(new NSArray(Messages.tr("no_index"), 0));
            for (i = 0; i < this.M.getArrayCount(); i++) {
                this.arrayIndex.addItem(this.M.getArray(i));
            }
            if (this.o.getArrayIndex() >= 0) {
                this.arrayIndex.setSelectedIndex(this.o.getArrayIndex() + 1);
            }
        }
        if (oc > 1) {
            this.nameField.setText("");
            this.description.setText(Messages.tr("multiple_selection"));
            this.arrayIndex.setSelectedIndex(-1);
            this.o = null;
        }
        if (oc == 0) {
            this.nameField.setText("");
            this.description.setText(Messages.tr("no_selection"));
            this.arrayIndex.setSelectedIndex(-1);
            this.o = null;
        }
        tm = new SObjectTableModel(this.o);
        this.attrTable.setModel(tm);
        if (oc == 1) {
            this.attrTable.getColumnModel().getColumn(1).setCellEditor(new SObjectCellEditor(this.o));
        }
    }

    /**
     * Implements the ActionListener interface by responding to the different
     * actions the interface elements generate when manipulated by the user.
     *
     * @param ae
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (this.o == null) {
            return;
        }

        if (ae.getSource() == this.defaultBtn) {
            this.o.getSnippet().instantiateNSObject(this.o);
        }
        else if (ae.getSource() == this.nameField) {
            this.o.setName(this.nameField.getText());
        }

        this.M.updateAllViews(false);
        this.M.setDirty(true);
    }

    private class PropertiesUpdater implements ListSelectionListener {
        /** Responds to changes in the selection of the list. */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && (SObjectBrowser.this.o != null)) {
                for (int i = e.getFirstIndex(); i <= e.getLastIndex(); ++i) {
                    SObjectBrowser.this.o.setAttribute(i, (String) SObjectBrowser.this.attrTable.getValueAt(i, 1));
                }
                SObjectBrowser.this.M.updateAllViews(true);
                SObjectBrowser.this.M.setDirty(true);
            }
        }
    }

    private static final Logger LOG = Logger.getLogger(SObjectBrowser.class.getName());
}

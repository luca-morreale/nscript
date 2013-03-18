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
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Implements a view that lists the elements (objects) that are part of the
 * current simulation script.
 */
public class NSWorldView extends JPanel {

    static final long serialVersionUID = 42L;
    /**
     * A reference to the simulation model.
     */
    private NSWorld w;
    /**
     * The list GUI element.
     */
    private JList<String> l;

    /**
     * Constructor that copies a reference to the simulation model.
     *
     * @param inW
     */
    public NSWorldView(NSWorld inW) {
        super();

        this.w = inW;
        this.l = new JList<String>(new SWPListModel(this.w));
        this.l.setCellRenderer(new NSWorldPane());
        JScrollPane sp = new JScrollPane(this.l);
        this.setLayout(new BorderLayout());
        this.add(sp, BorderLayout.CENTER);
    }

    /**
     * Updates the view when the model suffers changes.
     */
    public void updateList() {
        this.l.setModel(new SWPListModel(this.w));
        this.updateUI();
    }

    /**
     * Implements the abstract list model to render the elements in the list.
     */
    private static class SWPListModel extends AbstractListModel<String> {

        static final long serialVersionUID = 42L;
        /**
         * A reference to the model.
         */
        NSWorld W;

        /**
         * The constructor takes a reference to the model.
         *
         * @param inW a reference to the simulation model.
         */
        SWPListModel(NSWorld inW) {
            this.W = inW;
        }

        /**
         * Returns a string with a description of the ith model.
         *
         * @return the description of the ith model as text.
         */
        @Override
        public String getElementAt(int index) {
            return this.W.getObject(index).getName() + '(' + this.W.getObject(index).getSnippet().getName() + ')';
        }

        /**
         * Obtains the size of the array (in this case the size of the model.
         *
         * @return the number of objects in the current simulation model.
         */
        @Override
        public int getSize() {
            return this.W.getObjectsCount();
        }
    }
    private static final Logger LOG = Logger.getLogger(NSWorldView.class.getName());
}

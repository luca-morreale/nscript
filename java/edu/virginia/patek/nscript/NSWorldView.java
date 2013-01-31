/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package edu.virginia.patek.nscript;

import java.awt.*;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Implements a view that lists the elements (objects) that are part of the
 * current simulation script.
 */
public class NSWorldView extends JPanel {

    static final long serialVersionUID = 42L;
    /**
     * A reference to the simulation model.
     */
    NSWorld w;
    /**
     * The list GUI element.
     */
    JList<String> l;

    /**
     * Constructor that copies a reference to the simulation model.
     * @param inW 
     */
    public NSWorldView(NSWorld inW) {
        super();

        w = inW;
        l = new JList<String>(new SWPListModel(w));
        l.setCellRenderer(new NSWorldPane());
        JScrollPane sp = new JScrollPane(l);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
    }

    /**
     * Updates the view when the model suffers changes.
     */
    public void updateList() {
        l.setModel(new SWPListModel(w));
        updateUI();
    }

    /**
     * Implements the abstract list model to render the elements in the list.
     */
    class SWPListModel extends AbstractListModel<String> {

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
            W = inW;
        }

        /**
         * Returns a string with a description of the ith model.
         *
         * @return the description of the ith model as text.
         */
        @Override
        public String getElementAt(int index) {
            return W.getObject(index).getName() + '(' + W.getObject(index).getSnippet().getName() + ')';
        }

        /**
         * Obtains the size of the array (in this case the size of the model.
         *
         * @return the number of objects in the current simulation model.
         */
        @Override
        public int getSize() {
            return W.getObjectCount();
        }
    }
    private static final Logger LOG = Logger.getLogger(NSWorldView.class.getName());
}

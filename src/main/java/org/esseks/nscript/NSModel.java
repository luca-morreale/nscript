/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 * Copyright (C) 2014 Luca Morreale
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 * Holds the current simulation script information. Inherits the object storing
 * capabilities from NSWorld, and implements the DMModel interface, in order to
 * work correctly with the DMControl, and DMView objects. Also contains
 * references to all the views of the model to inform them of model changes.
 */
public class NSModel extends NSWorld implements DMModel {

    /**
     *      */
    public final static long serialVersionUID = 42L;
    /**
     * Reference to the tool box that stores the currently opened libraries.
     */
    private SToolBar toolBox;
    /**
     * Reference to the main modeling and visualization view.
     */
    private DMView editView;
    /**
     * Reference to the view where the Tcl version of the simulation script is
     * represented.
     */
    private JTextArea tclView;
    /**
     * Reference to the panel where individual objects can be edited.
     */
    private SObjectBrowser objectPanel;
    /**
     * Reference to the view that lists all the objects in the simulation (not
     * yet implemented).
     */
    private NSWorldView worldView;
    /**
     * Flag that tells if the model has been modified and needs to be saved to a
     * file.
     */
    private boolean isDirty;

    /**
     * The onlye constructor for this class of objects. Takes an initial
     * environment object (the ns object, that stores the simulation
     * environment), and a reference to the toolbox.
     *
     * @param inEnv
     * @param inTBar
     */
    public NSModel(NSObject inEnv, SToolBar inTBar) {
        super(inEnv);
        this.toolBox = inTBar;
        this.isDirty = false;
    }

    /**
     * Stores the references to all the views of the model.
     *
     * @param inEView
     * @param inTclView
     * @param inObjectPanel
     * @param inWorldView
     */
    public void setViews(DMView inEView, JTextArea inTclView, SObjectBrowser inObjectPanel, NSWorldView inWorldView) {
        this.editView = inEView;
        this.tclView = inTclView;
        this.objectPanel = inObjectPanel;
        this.worldView = inWorldView;
    }

    /**
     * Returns the current edition mode for the model (perhaps this should be
     * moved to the view).
     *
     * @return the current edition mode (integer).
     */
    @Override
    public int getEditionMode() {
        TclSnippet s = this.toolBox.getSelectedSnippet();

        if (s == null) {
            return DMModel.SELECT_MODE;
        }

        if (s.isRelation()) {
            return DMModel.RELATION_MODE;
        } else {
            return DMModel.ICON_MODE;
        }
    }

    /**
     * Adds a simple object (an entity object, not a relation) to the model.
     *
     * @param r the size of the view.
     * @param p the position of the new object.
     * @param unselectOther if true all other objects should be unselected after
     * adding the new object.
     */
    @Override
    public void addSimpleObject(Dimension r, Point p, boolean unselectOther) {
        String newName;
        TclSnippet s = this.toolBox.getSelectedSnippet();
        NSObject o;
        double x, y;

        x = p.getX() / r.width;
        y = p.getY() / r.height;

        if (s == null) {
            return;
        }

        // Check for uniqueness
        if (s.isUnique() && this.objectOfClassExists(s.getName())) {
            return;
        }

        this.isDirty = true;
        // Get a new unique generic name
        int i = -1;
        do {
            i++;
            newName = s.getName() + i;
        } while (!this.isValidName(newName));

        o = new NSEntity(s, newName, x, y);
        s.instantiateNSObject(o);
        this.addObject(o);
        this.tclView.setText(this.toTcl());
        this.worldView.updateList();
    }

    /**
     * Adds a new relation.
     *
     * @param oFrom the object where the relation starts.
     * @param oTo the objects where the relation ends.
     * @param unSelectOthers if true the editor all other objects should be
     * unselected after the addition of the new object.
     */
    @Override
    public void addRelationObject(DMObject oFrom, DMObject oTo, boolean unSelectOthers) {
        String newName;
        int i;
        TclSnippet s = this.toolBox.getSelectedSnippet();
        NSRelation o;

        if ((s == null) || !s.isRelation()) {
            return;
        }

        if (s.isFromBaseUnique() && this.relationOfClassExists(oFrom, s.getName())) {
            return;
        }
        if (s.isToBaseUnique() && this.relationOfClassExists(oTo, s.getName())) {
            return;
        }

        this.isDirty = true;
        i = -1;
        do {
            i++;
            newName = s.getName() + i;
        } while (!this.isValidName(newName));

        if (((NSEntity) oFrom).getSnippet().getBase().equals(s.getFromBase())
                && ((NSEntity) oTo).getSnippet().getBase().equals(s.getToBase())) {
            if (oFrom == oTo) {
                return;
            }
            o = new NSRelation(s, newName, (NSEntity) oFrom, (NSEntity) oTo);
            s.instantiateNSObject(o);
            this.addObject(o);
            this.tclView.setText(this.toTcl());
            this.worldView.updateList();
        }
    }

    /**
     * Tells all its dependent views to update their representation, as a
     * response to a change in the model information.
     */
    @Override
    public void updateAllViews(boolean onTheFly) {
        this.editView.repaint();
        this.tclView.setText(this.toTcl());
        this.worldView.updateList();
        
        if (!onTheFly) {
            this.objectPanel.selectionChanged();
        }
    }

    /**
     * Verifies if an object of a given type exists.
     *
     * @return true if such object exists, false otherwise.
     */
    boolean objectOfClassExists(String theClass) {
        NSObject o;
        int i;

        for (i = 0; i < this.getSize(); i++) {
            o = (NSObject) this.getObjectAt(i);
            if (o.getSnippet().getName().equals(theClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if a relation of a given class exists.
     *
     * @return true if such a relation exists, false otherise.
     */
    boolean relationOfClassExists(DMObject dmo, String theClass) {
        NSObject o, O;
        NSRelation or;
        int i;

        O = (NSObject) dmo;
        for (i = 0; i < this.getSize(); i++) {
            o = (NSObject) this.getObjectAt(i);
            if (o.getSnippet().isRelation()) {
                or = (NSRelation) o;
                if (((or.getFrom() == O) && or.getSnippet().getName().equals(theClass))
                        || ((or.getTo() == O) && or.getSnippet().getName().equals(theClass))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Obtains the number of objects currently in the simulation.
     *
     * @return the number of objects (1 or more).
     */
    @Override
    public int getSize() {
        return this.getObjectsCount();
    }

    /**
     * Obtains the object at a given index.
     *
     * @param inIndex
     */
    @Override
    public DMObject getObjectAt(int inIndex) {
        if ((inIndex >= 0) && (inIndex < this.getObjectsCount())) {
            return (DMObject) (this.getObject(inIndex));
        } else {
            return null;
        }
    }

    /**
     * Removes the currently selected objects.
     */
    @Override
    public void removeSelected() {
        int i;
        NSEditableObject o;
        NSRelation or;

        // First, select also arcs attached to a entity to be deleted
        for (i = 0; i < this.getSize(); i++) {
            o = (NSEditableObject) this.getObjectAt(i);
            if (o.getSnippet().isRelation()) {
                or = (NSRelation) o;
                if (or.getFrom().isSelected() || or.getTo().isSelected()) {
                    or.select();
                }
            }
        }

        this.editView.repaint();

        Iterator<NSObject> iter = this.getObjectsIterator();

        iter.next();
        while (iter.hasNext()) {
            o = (NSEditableObject) iter.next();
            if (o.isSelected()) {
                this.isDirty = true;
                iter.remove();
            }
        }

        this.updateAllViews(false);
    }

    /**
     * Creates a new model by flushing all the stored objects, and initializing
     * the ns environment object to its default values.
     */
    public void newModel() {
        // Clean
        this.clearArrays();

        Iterator<NSObject> iter = this.getObjectsIterator();

        iter.next();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }

        ((NSObject) this.getObjectAt(0)).getSnippet().instantiateNSObject((NSObject) this.getObjectAt(0));
        ((NSObject) this.getObjectAt(0)).setName("ns");
        this.updateAllViews(false);
        this.setDirty(false);
    }

    /**
     * Creates a Tcl representation of the current object. This call creates the
     * TclView used in the interface.
     *
     * @return a String containing the Tcl script of the model.
     */
    public String toTcl() {
        StringBuilder s = new StringBuilder(this.getSize() + 1);
        this.orderObjects();
        
        for (int i = 0; i < this.getSize(); i++) {
            NSObject ob = (NSObject) this.getObjectAt(i);
            s.append(ob.getSnippet().toTcl(this, ob, '#')).append('\n');
        }

        s.append("$").append(((NSObject) this.getObjectAt(0)).getName()).append(" run");
        return s.toString();
    }

    /**
     * Represents the current simulation model as a string. This representation
     * is used to store the model on disk.
     *
     * @return a String with the representation of the object as a string.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.getArrayCount() + this.getObjectsCount() + 1);

        s.append(Integer.toString(this.getArrayCount())).append("\n");
        for (int i = 0; i < this.getArrayCount(); i++) {
            s.append(this.getArray(i).toString());
        }
        s.append(Integer.toString(this.getObjectsCount())).append("\n");
        for (int i = 0; i < this.getObjectsCount(); i++) {
            s.append(((NSObject) this.getObjectAt(i)).toString());
        }
        return s.toString();
    }

    /**
     * True if the model needs to be saved to disk (changes have been made to it
     * since it was opened).
     *
     * @return true if the model needs to be saved, false otherwise.
     */
    public boolean dirty() {
        return this.isDirty;
    }

    /**
     * Sets the state of the model to dirty. Called by all the editing methods.
     *
     * @param dirtyState
     */
    @Override
    public void setDirty(boolean dirtyState) {
        this.isDirty = dirtyState;
    }
    private static final Logger LOG = Logger.getLogger(NSModel.class.getName());
}

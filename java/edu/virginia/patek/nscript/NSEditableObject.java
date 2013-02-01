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

/**
 * A generic extension to NSObject that implements the DMObject interface to
 * make the object editable by the DMControl-DMView components. This extension
 * will be used through inheritance by other objects (NSRelation and NSIconic).
 */
public class NSEditableObject extends NSObject implements DMObject {
    /** */
    public static final long serialVersionUID = 42L;

    /**
     * Flag to keep the 'Selected' state of the object.
     */
    boolean selected;

    /**
     * Uses the constructor from NSObject.
     * @param inSnippet
     * @param inName
     */
    public NSEditableObject(TclSnippet inSnippet, String inName) {
        super(inSnippet, inName);
    }

    /**
     * True if the object is selected for edition, false otherwise.
     *
     * @return true if selected, false otherwise.
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Selects the object for edition. After calling this method, method
     * isSelected() will return true.
     */
    @Override
    public void select() {
        selected = true;
    }

    /**
     * Removes the selected status, so that edition commands do not affect this
     * object anymore.
     */
    @Override
    public void unselect() {
        selected = false;
    }

    /**
     * Changes the 'Selected' status. If the object was selected, it will become
     * unselected, and vice versa.
     */
    @Override
    public void toggleSelect() {
        selected = !selected;
    }

    /**
     * Command to draw itself on a view. Empty implementation, since these class
     * is still abstract.
     */
    @Override
    public void drawSelf(Graphics g, Dimension r) {
    }

    /**
     * False by default, since the class is still abstract.
     *
     * @return false.
     */
    @Override
    public boolean isHit(Dimension r, Point p) {
        return false;
    }

    /**
     * Returns false by default, since the class is still abstract.
     *
     * @param r the size of the drawing canvas.
     * @param p1 one of the corners of the rectangle.
     * @param p2 the other corner of the rectangle.
     * @return false (abstract).
     */
    @Override
    public boolean isContained(Dimension r, Point p1, Point p2) {
        return false;
    }

    /**
     * Translates the position of the object by a given dimension.
     *
     * @param r the size of the drawing canvas.
     * @param byWhat the amount of translation.
     */
    @Override
    public void moveBy(Dimension r, Dimension byWhat) {
    }
    private static final Logger LOG = Logger.getLogger(NSEditableObject.class.getName());
}

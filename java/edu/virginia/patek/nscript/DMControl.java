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
import java.awt.event.*;

/**
 * DMControl.java
 *
 * This object implements the Control part of the MVC framework. Takes care of
 * handling events (keys + mouse + mouseMotion). <b>nscript</b> is structured
 * around a Model-View-Controller Architecture DMControl represents an abstract
 * controller which requires the existance of an abstract model (role played by
 * DMModel) to work. Implements the logic of selection and movement of objects,
 * which are stored in the model, including the use of key modifiers like shift.
 */
public class DMControl extends KeyAdapter
        implements MouseListener, MouseMotionListener {

    /**
     * A reference to the view object in charge of rendering the view.
     */
    DMView V;
    /**
     * A reference to the model which stores the actual information.
     */
    DMModel M;
    /**
     * A reference where a link object starts, for the case when lines are being
     * created.
     */
    DMObject startO;
    /**
     * Action taken by the user.
     */
    int action;
    /**
     * Part of the state of the controller.
     */
    boolean editing;
    /**
     * Coordinates of the start of a dragging action.
     */
    Point start;
    /**
     * Coordinates of the end of a dragging action.
     */
    Point end;
    static final int NONE = 0;
    static final int MOVING = 1;
    static final int SELECTING = 2;
    static final int LINKING = 3;

    /**
     * The only constructor requires references to the view object as well as
     * the model.
     *
     * @param inV the view (DMView) object.
     * @param inM the model (DMModel) object.
     */
    public DMControl(DMView inV, DMModel inM) {
        // Store the references
        V = inV;
        M = inM;
        editing = false;
        action = DMControl.NONE;
    }

    /**
     * A function that returns which of the model objects is hit my a mouse
     * click.
     *
     * @param r the dimension of the view.
     * @param p the location of the mouse when the button was pressed.
     */
    public DMObject modelIsHit(Dimension r, Point p) {
        int i;
        for (i = 0; i < M.getSize(); i++) {
            if (M.getObjectAt(i).isHit(r, p)) {
                return M.getObjectAt(i);
            }
        }
        return null;
    }

    /**
     * Counts the number of selected objects in the model.
     *
     * @return int the number of objects selected.
     */
    public int getSelectedCount() {
        int i, count;

        count = 0;
        for (i = 0; i < M.getSize(); i++) {
            if (M.getObjectAt(i).isSelected()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a reference to the i-th selected object in the model.
     *
     * @param index to the desired object. index must be between 1 and
     * DMControl.getSelectedCount().
     * @return a reference to the object, if the index is correct, <b>Null</b>
     * otherwise.
     */
    public DMObject getIthSelected(int index) {
        int i, count;

        count = 1;

        for (i = 0; i < M.getSize(); i++) {
            if (M.getObjectAt(i).isSelected()) {
                if (count == index) {
                    return M.getObjectAt(i);
                } else {
                    count++;
                }
            }
        }
        // Not found
        return null;
    }

    /**
     * Clears the whole selection.
     */
    public void unselectAll() {
        int i;

        for (i = 0; i < M.getSize(); i++) {
            M.getObjectAt(i).unselect();
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    /**
     * Processes the click event, where the user presses and releases the mouse
     * button in the same location. Implementation of the mouseListener
     * interface.
     *
     * @param me the mouse event object.
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        DMObject e;
        boolean shift = me.isShiftDown();

        switch (M.getEditionMode()) {

            case DMModel.SELECT_MODE:
                e = modelIsHit(V.getBounds().getSize(), me.getPoint());
                if (e != null) {
                    if (shift) {
                        e.toggleSelect();
                    } else {
                        unselectAll();
                        e.select();
                    }
                } else {
                    if (!shift) {
                        unselectAll();
                    }
                }
                M.updateAllViews();
                break;

            case DMModel.ICON_MODE:
                M.addSimpleObject(V.getBounds().getSize(), me.getPoint(), true);
                M.updateAllViews();
                break;
        }
    }

    /**
     * Process the event of the button being pressed, and prepares, if necessary
     * for a dragging action. The actual behavior depends on the mode the Model
     * is working on. Specifically, it responds to SELECT_MODE, by preparing for
     * a movement of the selected items if one of them is hit when no <shift>
     * key is pressed. If <shift> key is pressed, the response is to start a
     * dragging action to create a rectangle. The 'selected' state of all
     * elements of the model encompassed by the resulting rectangle will be
     * toggled.
     *
     * @param me a MouseEvent object containing the mouse state information.
     */
    @Override
    public void mousePressed(MouseEvent me) {
        DMObject e;
        boolean shift = me.isShiftDown();

        V.requestFocus();
        // Check if someone is hit
        e = modelIsHit(V.getBounds().getSize(), me.getPoint());

        switch (M.getEditionMode()) {

            case DMModel.SELECT_MODE:
                if (e != null && e.isSelected()) { // We have a hit
                    action = DMControl.MOVING;
                    start = me.getPoint();
                } else {
                    if (!shift) {
                        unselectAll();
                    }
                    action = DMControl.SELECTING;
                    start = me.getPoint();
                    end = start;
                }
                break;

            case DMModel.RELATION_MODE:
                if (e != null) {  // we have a hit
                    action = DMControl.LINKING;
                    start = me.getPoint();
                    end = start;
                    startO = e;
                } else {
                    action = DMControl.NONE;
                }
                break;
        }

    }

    /**
     * Draws an auxiliary figure to aid the user in the editing process. It
     * draws the line partially being drawn when creating a new link between
     * objects, or a rectangle, when a selection rectangle is created.
     *
     * @param g a Graphics object with the information about how to draw on the
     * screen provided by the calling routine (in this case DMView).
     */
    public void drawControls(Graphics g) {
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;

        switch (action) {
            case (DMControl.SELECTING):
                if (start.getX() < end.getX()) {
                    x1 = (int) start.getX();
                    x2 = (int) end.getX();
                } else {
                    x2 = (int) start.getX();
                    x1 = (int) end.getX();
                }
                if (start.getY() < end.getY()) {
                    y1 = (int) start.getY();
                    y2 = (int) end.getY();
                } else {
                    y2 = (int) start.getY();
                    y1 = (int) end.getY();
                }
                break;
            case (DMControl.LINKING):
                x1 = (int) start.getX();
                y1 = (int) start.getY();
                x2 = (int) end.getX();
                y2 = (int) end.getY();
        }

        switch (action) {
            case (DMControl.SELECTING):
                g.drawRect(x1, y1, x2 - x1, y2 - y1);
                break;
            case (DMControl.LINKING):
                g.drawLine(x1, y1, x2, y2);
                break;
        }
    }

    /**
     * Process the mouse released event that occurs when the user has finished
     * its dragging action. This can happen when the editing mode is SELECTING,
     * MOVING, or LINKING (creation of a link between to iconic objects, such as
     * a network link).
     *
     * @param me a MouseEvent object with the information about the mouse
     * position.
     */
    @Override
    public void mouseReleased(MouseEvent me) {
        DMObject e = modelIsHit(V.getBounds().getSize(), me.getPoint());
        int i;
        boolean shift = me.isShiftDown();

        switch (action) {

            case (DMControl.SELECTING):
                for (i = 0; i < M.getSize(); i++) {
                    if (M.getObjectAt(i).isContained(V.getBounds().getSize(), start, me.getPoint())) {
                        if (shift) {
                            M.getObjectAt(i).toggleSelect();
                        } else {
                            M.getObjectAt(i).select();
                        }
                    }
                }
                M.updateAllViews();
                break;

            case (DMControl.MOVING):
                Dimension r = new Dimension((int) (me.getPoint().getX() - start.getX()),
                        (int) (me.getPoint().getY() - start.getY()));
                for (i = 0; i < M.getSize(); i++) {
                    if (M.getObjectAt(i).isSelected()) {
                        M.getObjectAt(i).moveBy(V.getBounds().getSize(), r);
                    }
                    M.setDirty(true);
                }
                M.updateAllViews();

                break;

            case (DMControl.LINKING):
                if (action == DMControl.LINKING) {
                    if (e != null) {
                        M.addRelationObject(startO, e, true);
                        M.updateAllViews();
                    } else {
                        V.repaint();
                    }
                }
        }
        action = DMControl.NONE;
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    /**
     * Reponse to the movement of the mouse. Only relevant if a link is being
     * created (LINKING), a set of selected objects is being moved (MOVING), or
     * a selection rectangle is being drawn (SELECTING).
     *
     * @param me a MouseEvent with the information about the mouse position and
     * state.
     */
    @Override
    public void mouseDragged(MouseEvent me) {
        Dimension r;
        int i;

        if (action == DMControl.MOVING) {
            r = new Dimension((int) (me.getPoint().getX() - start.getX()),
                    (int) (me.getPoint().getY() - start.getY()));
            for (i = 0; i < M.getSize(); i++) {
                if (M.getObjectAt(i).isSelected()) {
                    M.getObjectAt(i).moveBy(V.getBounds().getSize(), r);
                }
            }
            M.updateAllViews();
            start = me.getPoint();
        } else {
            end = me.getPoint();
            V.repaint();
        }

    }

    /**
     * Implementation of selected key events. Specifically the 'backspace' key
     * that marks object removal.
     *
     * @param e a KeyEvent with the information about the key being pressed.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            M.removeSelected();
        }
    }
}
